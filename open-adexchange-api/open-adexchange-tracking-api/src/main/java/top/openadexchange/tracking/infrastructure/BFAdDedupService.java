package top.openadexchange.tracking.infrastructure;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;

import com.chaincoretech.epc.annotation.Extension;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;

import top.openadexchange.constants.RedisKeys;
import top.openadexchange.tracking.domain.gateway.AdDedupService;

/**
 * 布隆过滤器实现
 */
@Slf4j
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
     * 布隆过滤器过期时间（天） 设置为2天以确保覆盖当天和前一天的数据，避免跨天切换时的数据丢失
     */
    private static final long EXPIRE_DAYS = 2L;

    /**
     * 本地缓存布隆过滤器引用，避免重复获取
     */
    private final ConcurrentMap<String, RBloomFilter<String>> bfCache = new ConcurrentHashMap<>();

    @Resource
    private RedissonClient redissonClient;

    @Override
    public boolean tryAddImpression(String impId) {
        RBloomFilter<String> bloomFilter = createOrGetBf(getImpBfName());
        boolean added = bloomFilter.add(impId);
        if (added) {
            log.debug("Successfully added impression id to bloom filter: {}", impId);
        }
        return added;
    }

    private RBloomFilter<String> createOrGetBf(String bfName) {
        return bfCache.computeIfAbsent(bfName, name -> {
            log.info("Creating or getting bloom filter: {}", name);
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(name);
            if (bloomFilter.tryInit(EXPECTED_INSERTIONS, FALSE_PROBABILITY)) {
                log.info("Bloom filter initialized: {}, expectedInsertions: {}, falseProbability: {}",
                        name,
                        EXPECTED_INSERTIONS,
                        FALSE_PROBABILITY);
            }
            // 无论是否初始化，都设置过期时间，确保从Redis恢复的布隆过滤器也有过期时间
            bloomFilter.expire(Duration.ofDays(EXPIRE_DAYS));
            log.debug("Bloom filter expire time set: {} days for {}", EXPIRE_DAYS, name);
            return bloomFilter;
        });
    }

    private String getImpBfName() {
        return String.format(RedisKeys.BLOOM_FILTER_IMP, getFormattedDate());
    }

    @Override
    public boolean tryAddClick(String clkId) {
        RBloomFilter<String> bloomFilter = createOrGetBf(getClkBfName());
        boolean added = bloomFilter.add(clkId);
        if (added) {
            log.debug("Successfully added click id to bloom filter: {}", clkId);
        }
        return added;
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
        return String.format(RedisKeys.BLOOM_FILTER_CLK, getFormattedDate());
    }

    /**
     * 获取格式化的日期字符串 使用UTC时间以确保跨时区的一致性
     */
    private String getFormattedDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 定时清理本地缓存的布隆过滤器 每天凌晨1点执行，清理超过保留期限的布隆过滤器引用
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanExpiredCache() {
        try {
            LocalDate expireDate = LocalDate.now().minusDays(EXPIRE_DAYS);

            int cleanedCount = 0;
            for (String bfName : bfCache.keySet()) {
                LocalDate bfDate = extractDateFromBfName(bfName);
                if (bfDate != null && bfDate.isBefore(expireDate)) {
                    RBloomFilter<String> removed = bfCache.remove(bfName);
                    if (removed != null) {
                        cleanedCount++;
                        log.info("Cleaned expired bloom filter from local cache: {}", bfName);
                    }
                }
            }

            log.info("Bloom filter cache cleanup completed. Expire date: {}, Cleaned: {} filters",
                    expireDate.format(DATE_FORMATTER),
                    cleanedCount);
        } catch (Exception e) {
            log.error("Error while cleaning expired bloom filter cache", e);
        }
    }

    /**
     * 从布隆过滤器名称中提取日期
     *
     * @return LocalDate 对象，如果解析失败返回 null
     */
    private LocalDate extractDateFromBfName(String bfName) {
        if (bfName == null || bfName.length() < 8) {
            return null;
        }
        try {
            String dateStr = bfName.substring(bfName.length() - 8);
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("Failed to parse date from bloom filter name: {}", bfName);
            return null;
        }
    }

}
