package top.openadexchange.openapi.ssp.application.service.impl;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.application.service.DspCacheService;
import top.openadexchange.openapi.ssp.constants.Constants.CacheNames;

@Service
public class CaffeineDspCacheService implements DspCacheService {

    @Resource
    private CacheManager caffeineCacheManager;
    private Cache dspCache;

    @PostConstruct
    public void init() {
        dspCache = caffeineCacheManager.getCache(CacheNames.DSP);
    }

    @Override
    public Dsp getDspById(Integer dspId) {
        return dspCache.get(dspId, Dsp.class);
    }

    @Override
    public void updateDspCache(Dsp dsp) {
        dspCache.put(dsp.getId(), dsp);
    }

    @Override
    public void clearAllDspCache() {
        dspCache.clear();
    }

    @Override
    public void deleteDspCache(Long dspId) {
        dspCache.evict(dspId);
    }
}
