package top.openadexchange.tracking.application.service;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.RedisKeys;
import top.openadexchange.constants.enums.PriceMode;
import top.openadexchange.dto.TrackToken;
import top.openadexchange.dto.event.ClickEvent;
import top.openadexchange.dto.event.DspBidEvent;
import top.openadexchange.dto.event.DspReqEvent;
import top.openadexchange.dto.event.DspWinEvent;
import top.openadexchange.dto.event.ImpressionEvent;
import top.openadexchange.tracking.application.factory.ClickEventFactory;
import top.openadexchange.tracking.application.factory.ImpressionEventFactory;
import top.openadexchange.tracking.domain.gateway.AdDedupService;
import top.openadexchange.tracking.domain.gateway.OaxTrackingServices;
import top.openadexchange.tracking.domain.model.TrackTokenParseResult;
import top.openadexchange.tracking.infrastructure.constants.KafkaConstants;
import top.openadexchange.tracking.utils.RevenueUtils;

@Service
@Slf4j
public class TrackingService {

    private static final Logger IMP_LOG = LoggerFactory.getLogger("imp");
    private static final Logger CLK_LOG = LoggerFactory.getLogger("clk");

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource(name = "oaxStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private OaxTrackingServices oaxTrackingServices;

    /**
     * 曝光跟踪
     *
     * @param tk
     * @param request
     */
    public void impTrack(String tk, HttpServletRequest request) {
        // ① ② 解析tk，校验签名和过期时间
        TrackTokenParseResult trackTokenResult = TrackingTokenParser.parse(tk);
        TrackToken trackToken = trackTokenResult.getData();
        String impId = trackToken.getImpId();

        // ④ 构造曝光事件
        ImpressionEvent impEvent = ImpressionEventFactory.of(trackToken, request);
        // ⑤ 同步写 Kafka（Source of Truth）
        String eventJson = JSON.toJSONString(impEvent);
        //kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC_IMPRESSION, impId, eventJson);
        IMP_LOG.info("{}", eventJson);

        if (!trackTokenResult.isValid()) {
            log.warn("Invalid imp track token: {}", trackTokenResult.getErrorMsg());
            return;
        }
        // ③ 去重检查：使用布隆过滤器确保同一impId 的曝光只处理一次
        AdDedupService adDedupService = oaxTrackingServices.getAdDedupService();
        if (!adDedupService.tryAddImpression(impId)) {
            log.warn("Dup impression ignored: {}", impId);
            return;
        }
        // ⑥ 实时计数（Redis，弱一致）
        String statAdSlotKey = RedisKeys.keyStatAdSlot(trackToken.getAdSlotId());
        String statDspKey = RedisKeys.keyStatDsp(trackToken.getDspId());
        String statCridKey = RedisKeys.keyStatCrid(trackToken.getCrid());
        String statAdslotsKey = RedisKeys.keyStatAdslots();

        List<String> hashKeys = Arrays.asList(statAdSlotKey, statDspKey, statCridKey);

        //这里计算一下一次曝光的收益，收益计入媒体广告位的收益中，按照 micro cent（1分=1,000,000）计算
        long price = trackToken.getPrice();
        int revShare = trackToken.getRevShare();
        String adSlotId = trackToken.getAdSlotId();
        String dspId = trackToken.getDspId();

        long mediaRevenue = RevenueUtils.calcMediaRevenue(price, revShare);
        long dspCost = RevenueUtils.calcDspCost(price, revShare);
        long adxRevenue = RevenueUtils.calcAdxRevenue(price, revShare);

        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> ops) throws DataAccessException {
                K _statAdSlotKey = (K) statAdSlotKey;
                batchIncrementHashKeys(ops, hashKeys, RedisKeys.HASH_FIELD_IMP);
                ops.opsForHash().increment((K) statDspKey, RedisKeys.HASH_FIELD_DSP_COST, dspCost);
                ops.opsForHash().increment(_statAdSlotKey, RedisKeys.HASH_FIELD_REVENUE, mediaRevenue);
                ops.opsForHash().increment(_statAdSlotKey, RedisKeys.HASH_FIELD_ADX_REVENUE, adxRevenue);

                ops.opsForSet().add((K) statAdslotsKey, (V) adSlotId);
                ops.opsForSet().add((K) RedisKeys.keyStatDsps(), (V) dspId);
                return null;
            }
        });

        // ⑦ 异步生成 BillingEvent（CPM 模式下曝光产生计费）
        if (PriceMode.CPM.name().equalsIgnoreCase(trackToken.getPriceMode())) {
            //sendBillingEventAsync(impEvent);
        }
    }

    public void clkTrack(String tk, HttpServletRequest request) {
        // ① ② 解析tk，校验签名和过期时间
        TrackTokenParseResult parseResult = TrackingTokenParser.parse(tk);
        TrackToken trackToken = parseResult.getData();
        String impId = trackToken.getImpId();

        // ④ 构造点击事件
        ClickEvent clickEvent = ClickEventFactory.of(trackToken, request);
        // ⑤ 同步写 Kafka（Source of Truth）
        String eventPayload = JSON.toJSONString(clickEvent);
        //kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC_CLICK, impId, eventPayload);
        CLK_LOG.info("{}", eventPayload);

        if (!parseResult.isValid()) {
            log.warn("Invalid click track token: {}", parseResult.getErrorMsg());
            return;
        }
        // ③ 去重检查：使用布隆过滤器确保同一 impId 的点击只处理一次
        // tryAddClick 返回 true 表示是新元素，false 表示可能已存在
        AdDedupService adDedupService = oaxTrackingServices.getAdDedupService();
        if (!adDedupService.tryAddClick(impId)) {
            log.warn("Duplicate click ignored: {}", impId);
            return;
        }

        String dspId = trackToken.getDspId();
        String adSlotId = trackToken.getAdSlotId();

        String statAdSlotKey = RedisKeys.keyStatAdSlot(trackToken.getAdSlotId());
        String statCrIdKey = RedisKeys.keyStatCrid(trackToken.getCrid());
        String statDspKey = RedisKeys.keyStatDsp(trackToken.getDspId());

        // ⑥ 实时计数（Redis，弱一致）
        List<String> hashKeys = Arrays.asList(statAdSlotKey, statCrIdKey, statDspKey);

        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> ops) throws DataAccessException {
                batchIncrementHashKeys(ops, hashKeys, RedisKeys.HASH_FIELD_CLK);
                ops.opsForSet().add((K) RedisKeys.keyStatAdslots(), (V) adSlotId);
                ops.opsForSet().add((K) RedisKeys.keyStatDsps(), (V) dspId);
                return null;
            }
        });

        // ⑦ 异步生成 BillingEvent（CPC 模式下点击产生计费）
        if (PriceMode.CPC.name().equalsIgnoreCase(trackToken.getPriceMode())) {
            //sendBillingEventAsync(clickEvent);
        }
    }

    public <K, V> Object batchIncrementHashKeys(RedisOperations<K, V> ops, List<String> hashKeys, String hashField) {
        for (String incrHashKey : hashKeys) {
            K key = (K) incrHashKey;
            ops.opsForHash().increment(key, hashField, 1L);
            if (ops.getExpire(key) == null || ops.getExpire(key) < 0) {
                ops.expire(key, Duration.ofDays(2));
            }
        }
        return null;
    }

    /**
     * 异步发送计费事件
     */
    @Async
    public void sendBillingEventAsync(ImpressionEvent impEvent) {
        try {
            String billingJson = JSON.toJSONString(impEvent);
            kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC_BILLING, impEvent.getImpId(), billingJson);
            log.debug("Billing event sent for impression: {}", impEvent.getImpId());
        } catch (Exception e) {
            log.error("Failed to send billing event for impression: {}", impEvent.getImpId(), e);
        }
    }

    /**
     * 异步发送计费事件
     */
    @Async
    public void sendBillingEventAsync(ClickEvent clickEvent) {
        try {
            String billingJson = JSON.toJSONString(clickEvent);
            kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC_BILLING, clickEvent.getImpId(), billingJson);
            log.debug("Billing event sent for click: {}", clickEvent.getClickId());
        } catch (Exception e) {
            log.error("Failed to send billing event for click: {}", clickEvent.getClickId(), e);
        }
    }

    public void onDspReqEvent(DspReqEvent dspReqEvent) {
        log.info("DSP request event: {}", dspReqEvent);
        String adslotKey = RedisKeys.keyStatAdSlot(dspReqEvent.getAdSlotId());
        String dspKey = RedisKeys.keyStatDsp(dspReqEvent.getDspId());

        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> ops) throws DataAccessException {
                batchIncrementHashKeys(ops, Arrays.asList(adslotKey, dspKey), RedisKeys.HASH_FIELD_REQ);
                ops.opsForSet().add((K) RedisKeys.keyStatAdslots(), (V) dspReqEvent.getAdSlotId());
                ops.opsForSet().add((K) RedisKeys.keyStatDsps(), (V) dspReqEvent.getDspId());
                return null;
            }
        });
    }

    public void onDspBidEvent(DspBidEvent dspBidEvent) {
        log.info("DSP bid event: {}", dspBidEvent);
        String adSlotKey = RedisKeys.keyStatAdSlot(dspBidEvent.getAdSlotId());
        String dspKey = RedisKeys.keyStatDsp(dspBidEvent.getDspId());
        String cridKey = RedisKeys.keyStatCrid(dspBidEvent.getCrid());

        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> ops) throws DataAccessException {
                batchIncrementHashKeys(ops, Arrays.asList(adSlotKey, dspKey, cridKey), RedisKeys.HASH_FIELD_BID);
                ops.opsForSet().add((K) RedisKeys.keyStatAdslots(), (V) dspBidEvent.getAdSlotId());
                ops.opsForSet().add((K) RedisKeys.keyStatDsps(), (V) dspBidEvent.getDspId());
                return null;
            }
        });
    }

    public void onDspWinEvent(DspWinEvent dspWinEvent) {
        log.info("DSP win event: {}", dspWinEvent);
        String adSlotKey = RedisKeys.keyStatAdSlot(dspWinEvent.getAdSlotId());
        String dspKey = RedisKeys.keyStatDsp(dspWinEvent.getDspId());
        String cridKey = RedisKeys.keyStatCrid(dspWinEvent.getCrid());

        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> ops) throws DataAccessException {
                batchIncrementHashKeys(ops, Arrays.asList(adSlotKey, dspKey, cridKey), RedisKeys.HASH_FIELD_WIN);
                ops.opsForSet().add((K) RedisKeys.keyStatAdslots(), (V) dspWinEvent.getAdSlotId());
                ops.opsForSet().add((K) RedisKeys.keyStatDsps(), (V) dspWinEvent.getDspId());
                return null;
            }
        });
    }
}
