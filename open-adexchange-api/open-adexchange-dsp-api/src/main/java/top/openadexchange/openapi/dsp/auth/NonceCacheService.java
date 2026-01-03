package top.openadexchange.openapi.dsp.auth;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Nonce缓存服务，用于防重放攻击
 * 注意：在生产环境中建议使用Redis等分布式缓存
 */
@Service
public class NonceCacheService {

    private final Map<String, Long> nonceCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public NonceCacheService() {
        // 启动定时清理任务，清理5分钟前的nonce
        scheduler.scheduleAtFixedRate(this::cleanupExpiredNonces, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 检查nonce是否已存在
     */
    public boolean isNonceExists(String nonce) {
        Long timestamp = nonceCache.get(nonce);
        if (timestamp == null) {
            return false;
        }
        // 检查是否过期（5分钟）
        if (System.currentTimeMillis() - timestamp > 5 * 60 * 1000) {
            nonceCache.remove(nonce);
            return false;
        }
        return true;
    }

    /**
     * 记录nonce
     */
    public void recordNonce(String nonce) {
        nonceCache.put(nonce, System.currentTimeMillis());
    }

    /**
     * 清理过期的nonce
     */
    private void cleanupExpiredNonces() {
        long currentTime = System.currentTimeMillis();
        nonceCache.entrySet().removeIf(entry -> currentTime - entry.getValue() > 5 * 60 * 1000);
    }

    /**
     * 销毁时清理资源
     */
    public void destroy() {
        scheduler.shutdown();
    }
}