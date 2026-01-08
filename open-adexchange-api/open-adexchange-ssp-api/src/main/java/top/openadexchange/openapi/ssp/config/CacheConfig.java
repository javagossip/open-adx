package top.openadexchange.openapi.ssp.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching // 启用缓存功能
public class CacheConfig {

    //Caffeine本地缓存，用来缓存dsp以及广告相关信息
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(Caffeine.newBuilder().initialCapacity(100) // 初始容量
                .maximumSize(500) // 最大缓存数量
                .recordStats()); // 启用统计，无过期时间，除非手动清理
        return caffeineCacheManager;
    }

    // 配置类，用于配置缓存相关Bean
    // 目前RoaringBitmap不需要特别的配置，但保留此配置类以备将来扩展
}