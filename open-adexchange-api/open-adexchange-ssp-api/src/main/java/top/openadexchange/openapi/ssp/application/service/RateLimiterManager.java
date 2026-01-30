package top.openadexchange.openapi.ssp.application.service;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import top.openadexchange.openapi.ssp.domain.gateway.RateLimiter;
import top.openadexchange.openapi.ssp.domain.gateway.RateLimiterFactories;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimiterManager {

    // 存储服务名与对应的本地限流器
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    @Resource
    private RateLimiterFactories rateLimiterFactories;

    /**
     * 更新或创建限流器
     *
     * @param resource 接口名或资源名
     * @param qps 每秒允许的请求数
     */
    public void updateLimiter(String resource, Integer qps) {
        if (qps == null || qps <= 0) {
            log.info("QPS is null or <= 0, remove rate limiter, resource: {}", resource);
            removeRateLimiter(resource);
            return;
        }
        limiters.compute(resource, (key, oldLimiter) -> {
            if (oldLimiter == null) {
                return rateLimiterFactories.getRateLimiterFactory().createRateLimiter(qps);
            } else {
                // 动态调整速率
                oldLimiter.setLimit(qps);
                return oldLimiter;
            }
        });
    }

    public boolean tryAcquire(String resource) {
        RateLimiter limiter = limiters.get(resource);
        return limiter == null || limiter.tryAcquire();
    }

    public void removeRateLimiter(String resource) {
        limiters.remove(resource);
    }
}
