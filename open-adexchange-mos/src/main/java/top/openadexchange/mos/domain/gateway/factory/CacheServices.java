package top.openadexchange.mos.domain.gateway.factory;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.chaincoretech.epc.ExtensionRegistry;

import top.openadexchange.mos.domain.gateway.CacheService;

@Component
public class CacheServices extends ExtensionFactory {

    public CacheService getDefaultCacheService() {
        return getCacheServiceByType(sysConfigService.selectConfigByKey("sys.cache.type"));
    }

    public CacheService getCacheServiceByType(String type) {
        Assert.hasText(type, "type is empty");
        CacheService cacheService = ExtensionRegistry.getExtensionByKey(CacheService.class, type);
        Assert.notNull(cacheService, "cacheService is null");
        return cacheService;
    }
}
