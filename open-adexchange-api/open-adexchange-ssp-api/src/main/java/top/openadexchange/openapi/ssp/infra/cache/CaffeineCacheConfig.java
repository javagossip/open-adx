package top.openadexchange.openapi.ssp.infra.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import top.openadexchange.domain.entity.AdGroupAggregate;
import top.openadexchange.domain.entity.AdPlacementAggregate;
import top.openadexchange.domain.entity.CreativeAggregate;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.Site;
import top.openadexchange.model.SiteAdPlacement;

@Configuration
public class CaffeineCacheConfig {

    //Caffeine本地缓存，用来缓存dsp以及广告相关信息
    @Bean
    public Cache<Integer, DspAggregate> dspCache() {
        return Caffeine.newBuilder().initialCapacity(20).maximumSize(500).build();
    }

    @Bean
    public Cache<String, Dsp> dspCacheByDspId() {
        return Caffeine.newBuilder().initialCapacity(20).maximumSize(500).build();
    }

    @Bean
    public Cache<Integer, AdGroupAggregate> adGroupCache() {
        return Caffeine.newBuilder().initialCapacity(100).maximumSize(500).build();
    }

    @Bean
    public Cache<Integer, Site> siteCache() {
        return Caffeine.newBuilder().initialCapacity(100).maximumSize(500).build();
    }

    @Bean
    public Cache<Integer, SiteAdPlacement> siteAdPlacementCache() {
        return Caffeine.newBuilder().initialCapacity(100).maximumSize(500).build();
    }

    @Bean
    public Cache<Integer, AdPlacement> adPlacmentCache() {
        return Caffeine.newBuilder().initialCapacity(50).maximumSize(500).build();
    }

    @Bean
    public Cache<Integer, CreativeAggregate> creativeCache() {
        return Caffeine.newBuilder().initialCapacity(100).maximumSize(500).build();
    }

    @Bean
    public Cache<Integer, AdPlacementAggregate> adPlacementAggregateCache() {
        return Caffeine.newBuilder().initialCapacity(50).maximumSize(200).build();
    }
}