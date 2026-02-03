package top.openadexchange.openapi.ssp.application.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;
import top.openadexchange.openapi.ssp.domain.gateway.RateLimiter;
import top.openadexchange.openapi.ssp.domain.gateway.RateLimiterFactories;

@Component
@Slf4j
public class RateLimiterManager {

    // 存储服务名与对应的本地限流器
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    @Resource
    private RateLimiterFactories rateLimiterFactories;
    @Resource
    private MetadataCacheService metadataCacheService;

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

    //服务节点变更的时候，重新分配节点的限流限额，这里采用轮训的方式来更新
    @Scheduled(fixedRate = 60000)
    public void dynamicUpdateRateLimiters() {
        if (limiters == null || limiters.isEmpty()) {
            return;
        }
        limiters.forEach((resource, limiter) -> {
            Dsp dsp = metadataCacheService.getDspByDspId(resource);
            if (dsp != null) {
                updateLimiter(resource, dsp.getQpsLimit());
            }
        });
    }
}
