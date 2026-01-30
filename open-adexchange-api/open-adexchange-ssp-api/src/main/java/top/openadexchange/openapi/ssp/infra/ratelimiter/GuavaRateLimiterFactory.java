package top.openadexchange.openapi.ssp.infra.ratelimiter;

import com.chaincoretech.epc.annotation.Extension;

import jakarta.annotation.Resource;
import top.openadexchange.openapi.ssp.application.service.RegistryService;
import top.openadexchange.openapi.ssp.domain.gateway.RateLimiter;
import top.openadexchange.openapi.ssp.domain.gateway.RateLimiterFactory;

@Extension(keys = {"default", "guava"})
public class GuavaRateLimiterFactory implements RateLimiterFactory {

    @Resource
    private RegistryService registryService;

    @Override
    public RateLimiter createRateLimiter(Integer limit) {
        return GuavaRateLimiter.create(limit, registryService);
    }
}
