package top.openadexchange.tracking.application.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.jspecify.annotations.Nullable;
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
import top.openadexchange.constants.enums.PriceMode;
import top.openadexchange.dto.TrackToken;
import top.openadexchange.tracking.application.factory.ClickEventFactory;
import top.openadexchange.tracking.application.factory.ImpressionEventFactory;
import top.openadexchange.tracking.domain.event.ClickEvent;
import top.openadexchange.tracking.domain.event.ImpressionEvent;
import top.openadexchange.tracking.domain.gateway.AdDedupService;
import top.openadexchange.tracking.domain.gateway.OaxTrackingServices;
import top.openadexchange.tracking.domain.model.IncrHashKey;
import top.openadexchange.tracking.domain.model.TrackTokenParseResult;
import top.openadexchange.tracking.infrastructure.constants.KafkaConstants;
import top.openadexchange.tracking.infrastructure.constants.RedisKeys;

@Service
@Slf4j
public class TrackingService {

    private static final Logger IMP_LOG = LoggerFactory.getLogger("imp");
    private static final Logger CLK_LOG = LoggerFactory.getLogger("clk");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource(name = "oaxStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private OaxTrackingServices oaxTrackingServices;
    @Resource
    private AntiFraudService antiFraudService;

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
        kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC_IMPRESSION, impId, eventJson);
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
        String today = LocalDate.now().format(DATE_FORMATTER);
        String adSlotImpKey = String.format(RedisKeys.HASH_KEY_IMP_ADSLOT, today);
        String cridImpKey = String.format(RedisKeys.HASH_KEY_IMP_CRID, today);
        String dspImpKey = String.format(RedisKeys.HASH_KEY_IMP_DSP, today);

        List<IncrHashKey> incrHashKeys = Arrays.asList(new IncrHashKey(adSlotImpKey, trackToken.getAdSotId()),
                new IncrHashKey(cridImpKey, trackToken.getCrid()),
                new IncrHashKey(dspImpKey, trackToken.getDspId()));
        batchIncrementHashKeys(incrHashKeys);

        // ⑦ 异步生成 BillingEvent（CPM 模式下曝光产生计费）
        if (PriceMode.CPM.name().equalsIgnoreCase(trackToken.getPriceMode())) {
            sendBillingEventAsync(impEvent);
        }
    }

    public void clkTrack(String tk, HttpServletRequest request) {
        // ① ② 解析tk，校验签名和过期时间
        TrackTokenParseResult parseResult = TrackingTokenParser.parse(tk);
        TrackToken payload = parseResult.getData();
        String impId = payload.getImpId();

        // ④ 构造点击事件
        ClickEvent clickEvent = ClickEventFactory.of(payload, request);
        // ⑤ 同步写 Kafka（Source of Truth）
        String eventPayload = JSON.toJSONString(clickEvent);
        kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC_CLICK, impId, eventPayload);
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

        // ⑥ 实时计数（Redis，弱一致）
        String now = LocalDate.now().format(DATE_FORMATTER);
        String adSlotClickKey = String.format(RedisKeys.HASH_KEY_CLK_ADSLOT, now);
        String cridClickKey = String.format(RedisKeys.HASH_KEY_CLK_CRID, now);
        String dspClickKey = String.format(RedisKeys.HASH_KEY_CLK_DSP, now);

        List<IncrHashKey> incrHashKeys = Arrays.asList(new IncrHashKey(adSlotClickKey, payload.getAdSotId()),
                new IncrHashKey(cridClickKey, payload.getCrid()),
                new IncrHashKey(dspClickKey, payload.getDspId()));

        batchIncrementHashKeys(incrHashKeys);
        // ⑦ 异步生成 BillingEvent（CPC 模式下点击产生计费）
        if (PriceMode.CPC.name().equalsIgnoreCase(payload.getPriceMode())) {
            sendBillingEventAsync(clickEvent);
        }
    }

    public void batchIncrementHashKeys(List<IncrHashKey> incrHashKeys) {
        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public @Nullable <K, V> Object execute(RedisOperations<K, V> ops) throws DataAccessException {
                for (IncrHashKey incrHashKey : incrHashKeys) {
                    K key = (K) incrHashKey.getKey();
                    ops.opsForHash().increment(key, incrHashKey.getField(), 1L);
                    if (ops.getExpire(key) == null || ops.getExpire(key) < 0) {
                        ops.expire(key, Duration.ofDays(2));
                    }
                }
                return null;
            }
        });
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
}
