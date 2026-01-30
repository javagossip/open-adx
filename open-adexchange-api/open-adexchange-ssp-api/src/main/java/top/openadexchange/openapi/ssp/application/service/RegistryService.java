package top.openadexchange.openapi.ssp.application.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.commons.EnvUtils;
import top.openadexchange.openapi.ssp.constants.Constants;
import top.openadexchange.openapi.ssp.utils.InstanceIdUtils;

@Service
@Slf4j
public class RegistryService {

    @Resource(name = "oaxStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    private final AtomicBoolean registered = new AtomicBoolean(false);
    private String cachedInstanceId;
    private String cachedRegistryKey;

    /**
     * 执行注册
     */
    public void register() {
        cachedRegistryKey = Constants.RegistryKeys.serviceNodeKey(EnvUtils.getAppName());
        cachedInstanceId = InstanceIdUtils.getInstanceId();
        // 使用 Hash 存储：Field 为实例ID，Value 为最后心跳时间
        redisTemplate.opsForHash().put(cachedRegistryKey, cachedInstanceId, String.valueOf(System.currentTimeMillis()));
        // 设置整体过期时间（作为兜底）
        redisTemplate.expire(cachedRegistryKey, 60, TimeUnit.SECONDS);
        registered.set(true);
        log.info(">>> 服务实例已注册: {}", cachedInstanceId);
    }

    @EventListener(ContextClosedEvent.class)
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("容器准备关闭，开始执行收尾工作...");
        deregister();
    }

    /**
     * 执行注销（优雅下线）
     */
    public void deregister() {
        if (registered.compareAndSet(true, false)) {
            try {
                // 使用缓存的键和实例ID，避免在关闭时重新获取可能不可用的信息
                if (cachedRegistryKey != null && cachedInstanceId != null && redisTemplate != null) {
                    redisTemplate.opsForHash().delete(cachedRegistryKey, cachedInstanceId);
                    log.info("<<< 服务实例已注销: {}", cachedInstanceId);
                }
            } catch (Exception ex) {
                log.error("服务实例注销失败: {}", cachedInstanceId, ex);
            }
        }
    }

    /**
     * 心跳续期
     */
    @Scheduled(fixedRate = 10000) // 每10秒报一次平安
    public void heartbeat() {
        String registryKey = Constants.RegistryKeys.serviceNodeKey(EnvUtils.getAppName());
        String instanceId = InstanceIdUtils.getInstanceId();
        redisTemplate.opsForHash().put(registryKey, instanceId, String.valueOf(System.currentTimeMillis()));
    }

    /**
     * 获取当前在线节点数
     */
    public int getNodeCount() {
        String registryKey = Constants.RegistryKeys.serviceNodeKey(EnvUtils.getAppName());
        Map<Object, Object> nodes = redisTemplate.opsForHash().entries(registryKey);
        long now = System.currentTimeMillis();
        // 过滤掉超过 30 秒没有心跳的节点（防止异常宕机的节点被计入）
        long count =
                nodes.values().stream().map(v -> Long.parseLong((String) v)).filter(time -> now - time < 30000).count();
        return (int) (count <= 0 ? 1 : count);
    }
}
