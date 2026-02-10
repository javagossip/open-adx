package top.openadexchange.openapi.ssp.application.service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.commons.cache.RedisOpsUtils;
import top.openadexchange.constants.Constants;
import top.openadexchange.constants.RedisKeys;

@Service
@Slf4j
public class MetricsCollector {

    private final ConcurrentMap<String, AdSlotMetrics> adSlotMetrics = new ConcurrentHashMap<>(20);
    private final ConcurrentMap<String, AdSlotMetrics> slaveAdSlotMetrics = new ConcurrentHashMap<>(20);

    private final ConcurrentMap<String, DspMetrics> dspMetrics = new ConcurrentHashMap<>(10);
    private final ConcurrentMap<String, DspMetrics> slaveDspMetrics = new ConcurrentHashMap<>(10);

    private final AtomicReference<ConcurrentMap<String, AdSlotMetrics>> currentAdSlotMetricsRef =
            new AtomicReference<>(adSlotMetrics);
    private final AtomicReference<ConcurrentMap<String, DspMetrics>> currentDspMetricsRef =
            new AtomicReference<>(dspMetrics);

    private final ConcurrentMap<String, Set<String>> hourlyDspIdSetMap = new ConcurrentHashMap<>(2);
    private final ConcurrentMap<String, Set<String>> slaveHourlyDspIdSetMap =
            new ConcurrentHashMap<String, Set<String>>(2);

    private final ConcurrentMap<String, Set<String>> hourlyAdSlotIdSetMap =
            new ConcurrentHashMap<String, Set<String>>(2);
    private final ConcurrentMap<String, Set<String>> slaveHourlyAdSlotIdSetMap =
            new ConcurrentHashMap<String, Set<String>>(2);

    private final AtomicReference<ConcurrentMap<String, Set<String>>> currentHourlyDspIdSetMapRef =
            new AtomicReference<>(hourlyDspIdSetMap);
    private final AtomicReference<ConcurrentMap<String, Set<String>>> currentHourlyAdSlotIdSetMapRef =
            new AtomicReference<>(hourlyAdSlotIdSetMap);

    @Resource(name = "oaxStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    public record DspMetrics(String dspId, LongAdder reqs, LongAdder bids, LongAdder wins) {

        public DspMetrics(String dspId) {
            this(dspId, new LongAdder(), new LongAdder(), new LongAdder());
        }

        public static DspMetrics of(String dspId) {
            return new DspMetrics(dspId);
        }
    }

    public record AdSlotMetrics(String adSlotId, LongAdder reqs, LongAdder bids, LongAdder wins) {

        public AdSlotMetrics(String adSlotId) {
            this(adSlotId, new LongAdder(), new LongAdder(), new LongAdder());
        }

        public static AdSlotMetrics of(String adSlotId) {
            return new AdSlotMetrics(adSlotId);
        }
    }

    public void incrDspReqs(String dspId) {
        DspMetrics metrics = currentDspMetricsRef.get().computeIfAbsent(dspMetricKey(dspId), DspMetrics::of);
        metrics.reqs.increment();
    }

    public void incrDspBids(String dspId) {
        DspMetrics metrics = currentDspMetricsRef.get().computeIfAbsent(dspMetricKey(dspId), DspMetrics::of);
        metrics.bids.increment();
    }

    public void incrDspWins(String dspId) {
        DspMetrics metrics = currentDspMetricsRef.get().computeIfAbsent(dspMetricKey(dspId), DspMetrics::of);
        metrics.wins.increment();
    }

    public void incrAdSlotReqs(String adSlotId) {
        AdSlotMetrics metrics =
                currentAdSlotMetricsRef.get().computeIfAbsent(adSlotMetricKey(adSlotId), AdSlotMetrics::of);
        metrics.reqs.increment();
    }

    public void incrAdSlotBids(String adSlotId) {
        AdSlotMetrics metrics =
                currentAdSlotMetricsRef.get().computeIfAbsent(adSlotMetricKey(adSlotId), AdSlotMetrics::of);
        metrics.bids.increment();
    }

    public void incrAdSlotWins(String adSlotId) {
        AdSlotMetrics metrics =
                currentAdSlotMetricsRef.get().computeIfAbsent(adSlotMetricKey(adSlotId), AdSlotMetrics::of);
        metrics.wins.increment();
    }

    private String adSlotMetricKey(String adSlotId) {
        String now = Constants.formatNow();
        currentHourlyAdSlotIdSetMapRef.get().computeIfAbsent(now, k -> new HashSet<>()).add(adSlotId);
        return RedisKeys.keyStatAdSlot(adSlotId, now);
    }

    private String dspMetricKey(String dspId) {
        String now = Constants.formatNow();
        currentHourlyDspIdSetMapRef.get().computeIfAbsent(now, k -> new HashSet<>()).add(dspId);
        return RedisKeys.keyStatDsp(dspId, now);
    }

    private ConcurrentMap<String, Set<String>> swapAndGetHourlyDspIdSetMap() {
        ConcurrentMap<String, Set<String>> activeMap = currentHourlyDspIdSetMapRef.get();
        ConcurrentMap<String, Set<String>> nextActiveMap =
                (activeMap == hourlyDspIdSetMap) ? slaveHourlyDspIdSetMap : hourlyDspIdSetMap;

        currentHourlyDspIdSetMapRef.set(nextActiveMap);
        return activeMap;
    }

    private ConcurrentMap<String, Set<String>> swapAndGetHourlyAdSlotIdSetMap() {
        ConcurrentMap<String, Set<String>> activeMap = currentHourlyAdSlotIdSetMapRef.get();
        ConcurrentMap<String, Set<String>> nextActiveMap =
                (activeMap == hourlyAdSlotIdSetMap) ? slaveHourlyAdSlotIdSetMap : hourlyAdSlotIdSetMap;

        currentHourlyAdSlotIdSetMapRef.set(nextActiveMap);
        return activeMap;
    }

    private ConcurrentMap<String, DspMetrics> swapAndGetDspMetrics() {
        ConcurrentMap<String, DspMetrics> activeMap = currentDspMetricsRef.get();
        ConcurrentMap<String, DspMetrics> nextActiveMap = (activeMap == dspMetrics) ? slaveDspMetrics : dspMetrics;
        // 切换
        currentDspMetricsRef.set(nextActiveMap);
        // 返回刚才写满的 Map 供同步逻辑读取
        return activeMap;
    }

    private ConcurrentMap<String, AdSlotMetrics> swapAndGetAdSlotMetrics() {
        ConcurrentMap<String, AdSlotMetrics> activeMap = currentAdSlotMetricsRef.get();
        ConcurrentMap<String, AdSlotMetrics> nextActiveMap =
                (activeMap == adSlotMetrics) ? slaveAdSlotMetrics : adSlotMetrics;
        // 切换
        currentAdSlotMetricsRef.set(nextActiveMap);
        // 返回刚才写满的 Map 供同步逻辑读取
        return activeMap;
    }

    @Scheduled(fixedDelay = 5,
            timeUnit = TimeUnit.SECONDS)
    public void syncMetricsToRedis() {
        try {
            // 1、同步dsp统计数据到redis
            log.info("Synchronized dsp metrics to redis");
            ConcurrentMap<String, DspMetrics> dspMetrics = swapAndGetDspMetrics();
            dspMetrics.forEach((dspMetricKey, metrics) -> {
                redisTemplate.executePipelined(new SessionCallback<>() {
                    @Override
                    public @Nullable <K, V> Object execute(RedisOperations<K, V> ops) throws DataAccessException {
                        ops.opsForHash().increment((K) dspMetricKey, RedisKeys.HASH_FIELD_REQ, metrics.reqs.sum());
                        ops.opsForHash().increment((K) dspMetricKey, RedisKeys.HASH_FIELD_BID, metrics.bids.sum());
                        ops.opsForHash().increment((K) dspMetricKey, RedisKeys.HASH_FIELD_WIN, metrics.wins.sum());

                        //RedisOpsUtils.sadd(ops, RedisKeys.keyStatDsps(), dspMetricKey);
                        Long expire = ops.getExpire((K) dspMetricKey);
                        if (expire == null || expire < 0) {
                            ops.expire((K) dspMetricKey, Duration.ofDays(2));
                        }
                        return null;
                    }
                });
            });
            dspMetrics.clear();
            // 2、同步 adslot统计数据到redis
            log.info("Synchronized adslot metrics to redis");
            ConcurrentMap<String, AdSlotMetrics> adSlotMetrics = swapAndGetAdSlotMetrics();
            adSlotMetrics.forEach((adSlotMetricKey, metrics) -> {
                redisTemplate.executePipelined(new SessionCallback<>() {
                    @Override
                    public @Nullable <K, V> Object execute(RedisOperations<K, V> ops) throws DataAccessException {
                        ops.opsForHash().increment((K) adSlotMetricKey, RedisKeys.HASH_FIELD_REQ, metrics.reqs.sum());
                        ops.opsForHash().increment((K) adSlotMetricKey, RedisKeys.HASH_FIELD_BID, metrics.bids.sum());
                        ops.opsForHash().increment((K) adSlotMetricKey, RedisKeys.HASH_FIELD_WIN, metrics.wins.sum());

                        Long expire = ops.getExpire((K) adSlotMetricKey);
                        if (expire == null || expire < 0) {
                            ops.expire((K) adSlotMetricKey, Duration.ofDays(2));
                        }
                        return null;
                    }
                });
            });
            adSlotMetrics.clear();

            log.info("Synchronized stat dspIds to redis");
            ConcurrentMap<String, Set<String>> dspIds = swapAndGetHourlyDspIdSetMap();
            redisTemplate.executePipelined(new SessionCallback<>() {
                @Override
                public @Nullable <K, V> Object execute(RedisOperations<K, V> ops) throws DataAccessException {
                    dspIds.forEach((hour, dspIdSet) -> {
                        RedisOpsUtils.sadd(ops, RedisKeys.keyStatDsps(hour), dspIdSet, Duration.ofDays(2));
                    });
                    return null;
                }
            });
            dspIds.clear();

            log.info("Synchronized stat adSlotIds to redis");
            ConcurrentMap<String, Set<String>> adSlotIds = swapAndGetHourlyAdSlotIdSetMap();
            redisTemplate.executePipelined(new SessionCallback<>() {
                @Override
                public @Nullable <K, V> Object execute(RedisOperations<K, V> ops) throws DataAccessException {
                    adSlotIds.forEach((hour, adSlotIdSet) -> {
                        RedisOpsUtils.sadd(ops, RedisKeys.keyStatAdslots(hour), adSlotIdSet, Duration.ofDays(2));
                    });
                    return null;
                }
            });
            adSlotIds.clear();
        } catch (Exception ex) {
            log.error("sync metrics to redis error", ex);
        }
    }
}
