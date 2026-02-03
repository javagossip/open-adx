package top.openadexchange.tracking.infrastructure;

import com.chaincoretech.epc.annotation.Extension;

import jakarta.annotation.Resource;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;

import top.openadexchange.constants.RedisKeys;
import top.openadexchange.tracking.domain.gateway.AdDedupService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 布隆过滤器实现
 */
@Extension(keys = {"bloomFilter", "default"})
public class BFAdDedupService implements AdDedupService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    /**
     * 预期每天的曝光/点击数量（可根据实际业务调整）
     */
    private static final long EXPECTED_INSERTIONS = 10_000_000L;

    /**
     * 误判率（0.01 = 1%）
     */
    private static final double FALSE_PROBABILITY = 0.01;

    /**
     * 布隆过滤器过期时间（天）
     */
    private static final long EXPIRE_DAYS = 2L;

    /**
     * 本地缓存布隆过滤器引用，避免重复获取
     */
    private final ConcurrentHashMap<String, RBloomFilter<String>> bfCache = new ConcurrentHashMap<>();

    @Resource
    private RedissonClient redissonClient;

    @Override
    public boolean tryAddImpression(String impId) {
        RBloomFilter<String> bloomFilter = createOrGetBf(getImpBfName());
        return bloomFilter.add(impId);
    }

    private RBloomFilter<String> createOrGetBf(String bfName) {
        return bfCache.computeIfAbsent(bfName, name -> {
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(name);
            if (bloomFilter.tryInit(EXPECTED_INSERTIONS, FALSE_PROBABILITY)) {
                bloomFilter.expire(Duration.ofDays(EXPIRE_DAYS));
            }
            return bloomFilter;
        });
    }

    private String getImpBfName() {
        return String.format(RedisKeys.BLOOM_FILTER_IMP, LocalDate.now().format(DATE_FORMATTER));
    }

    @Override
    public boolean tryAddClick(String clkId) {
        RBloomFilter<String> bloomFilter = createOrGetBf(getClkBfName());
        return bloomFilter.add(clkId);
    }

    @Override
    public boolean containsImpression(String impId) {
        RBloomFilter<String> bloomFilter = createOrGetBf(getImpBfName());
        return bloomFilter.contains(impId);
    }

    @Override
    public boolean containsClick(String clkId) {
        RBloomFilter<String> bloomFilter = createOrGetBf(getClkBfName());
        return bloomFilter.contains(clkId);
    }

    private String getClkBfName() {
        return String.format(RedisKeys.BLOOM_FILTER_CLK, LocalDate.now().format(DATE_FORMATTER));
    }
}
