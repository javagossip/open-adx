package top.openadexchange.openapi.ssp.domain.gateway;

import org.springframework.stereotype.Component;

import com.chaincoretech.epc.ExtensionRegistry;

import jakarta.annotation.Resource;
import top.openadexchange.openapi.ssp.config.OaxEngineProperties;

@Component
public class RateLimiterFactories {

    @Resource
    private OaxEngineProperties oaxEngineProperties;

    private RateLimiterFactory rateLimiterFactory;

    public RateLimiterFactory getRateLimiterFactory() {
        if (rateLimiterFactory != null) {
            return rateLimiterFactory;
        }
        rateLimiterFactory = ExtensionRegistry.getExtensionByKey(RateLimiterFactory.class,
                oaxEngineProperties.getRateLimiterFactory());
        return rateLimiterFactory;
    }
}
