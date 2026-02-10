package top.openadexchange.openapi.ssp.infra.ratelimiter;

import top.openadexchange.openapi.ssp.application.service.RegistryService;
import top.openadexchange.openapi.ssp.domain.gateway.RateLimiter;

public class GuavaRateLimiter implements RateLimiter {

    private final com.google.common.util.concurrent.RateLimiter internalRateLimiter;
    private final RegistryService registryService;

    public GuavaRateLimiter(int limit, RegistryService registryService) {
        this.registryService = registryService;
        internalRateLimiter = com.google.common.util.concurrent.RateLimiter.create(limit);
    }

    public static GuavaRateLimiter create(int limit, RegistryService registryService) {
        int realLimit = calcRealLimit(limit, registryService);
        return new GuavaRateLimiter(realLimit, registryService);
    }

    private static int calcRealLimit(int limit, RegistryService registryService) {
        int nodeCount = registryService.getNodeCount();
        //实际限额增加10%
        int realLimit = (int) ((limit / nodeCount) * 1.1D);
        //要确保单机限速至少要大于 0
        return Math.max(realLimit, 1);
    }

    @Override
    public void setLimit(int limit) {
        internalRateLimiter.setRate(calcRealLimit(limit, registryService));
    }

    @Override
    public boolean tryAcquire() {
        return internalRateLimiter.tryAcquire();
    }
}
