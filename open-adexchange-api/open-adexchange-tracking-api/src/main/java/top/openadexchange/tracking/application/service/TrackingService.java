package top.openadexchange.tracking.application.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
import top.openadexchange.constants.PriceMode;
import top.openadexchange.dto.TrackToken;
import top.openadexchange.tracking.application.factory.ClickEventFactory;
import top.openadexchange.tracking.application.factory.ImpressionEventFactory;
import top.openadexchange.tracking.domain.event.ClickEvent;
import top.openadexchange.tracking.domain.event.ImpressionEvent;
import top.openadexchange.tracking.domain.gateway.AdDedupService;
import top.openadexchange.tracking.domain.gateway.OaxTrackingServices;
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
    @Resource
    private RedisTemplate<String, String> oaxStringRedisTemplate;
    @Resource
    private OaxTrackingServices oaxTrackingServices;
    @Resource
    private AntiFraudService antiFraudService;

    /**
     * <pre>
     * HTTP Request
     *    │
     *    ▼
     * ① 参数解析（tk）
     *    │
     *    ▼
     * ② 合法性校验（签名 / 过期）
     *    │
     *    ▼
     * ③ 生成业务唯一 ID（impId / clickId）
     *    │
     *    ▼
     * ④ 构造事实事件（Fact Event）
     *    │
     *    ▼
     * ⑤ 同步写 Kafka（Source of Truth）
     *    │
     *    ▼
     * ⑥ 实时计数（Redis，弱一致）
     *    │
     *    ▼
     * ⑦ 生成 BillingEvent（异步）
     *    │
     *    ▼
     * HTTP Response（pixel / redirect）
     * </pre>
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
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        String adSlotImpKey = String.format(RedisKeys.HASH_KEY_IMP_ADSLOT, currentDate);
        String cridImpKey = String.format(RedisKeys.HASH_KEY_IMP_CRID, currentDate);

        oaxStringRedisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public @Nullable <K, V> Object execute(RedisOperations<K, V> ops) throws DataAccessException {
                ops.opsForHash().increment((K) adSlotImpKey, trackToken.getAdSotId(), 1L);
                ops.opsForHash().increment((K) cridImpKey, trackToken.getCrid(), 1L);

                Long expire = ops.getExpire((K) adSlotImpKey);
                if (expire == null || expire < 0) {
                    ops.expire((K) adSlotImpKey, Duration.ofDays(2));
                }
                Long expireForCridKey = ops.getExpire((K) cridImpKey);
                if (expireForCridKey == null || expireForCridKey < 0) {
                    ops.expire((K) cridImpKey, Duration.ofDays(2));
                }
                return null;
            }
        });
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
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        String adSlotClickKey = String.format(RedisKeys.HASH_KEY_CLK_ADSLOT, currentDate);
        String cridClickKey = String.format(RedisKeys.HASH_KEY_CLK_CRID, currentDate);

        oaxStringRedisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public @Nullable <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.opsForHash().increment((K) adSlotClickKey, payload.getAdSotId(), 1L);
                operations.opsForHash().increment((K) cridClickKey, payload.getCrid(), 1L);

                Long expireForAdSlotKey = operations.getExpire((K) adSlotClickKey);
                if (expireForAdSlotKey == null || expireForAdSlotKey < 0) {
                    operations.expire((K) adSlotClickKey, Duration.ofDays(2));
                }
                Long expireForCridKey = operations.getExpire((K) cridClickKey);
                if (expireForCridKey == null || expireForCridKey < 0) {
                    operations.expire((K) cridClickKey, Duration.ofDays(2));
                }
                return null;
            }
        });
        // ⑦ 异步生成 BillingEvent（CPC 模式下点击产生计费）
        if (PriceMode.CPC.name().equalsIgnoreCase(payload.getPriceMode())) {
            sendBillingEventAsync(clickEvent);
        }
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
