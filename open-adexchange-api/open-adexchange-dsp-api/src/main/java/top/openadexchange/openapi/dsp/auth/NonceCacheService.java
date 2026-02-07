package top.openadexchange.openapi.dsp.auth;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.time.Duration;

/**
 * Nonce缓存服务，用于防重放攻击 注意：在生产环境中建议使用Redis等分布式缓存
 */
@Service
public class NonceCacheService {

    @Resource(name = "oaxStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 检查nonce是否已存在
     */
    public boolean isNonceExists(String nonce) {
        String value = redisTemplate.opsForValue().get(nonceKey(nonce));
        return value != null;
    }

    /**
     * 记录nonce
     */
    public void recordNonce(String nonce) {
        redisTemplate.opsForValue()
                .set(nonceKey(nonce), String.valueOf(System.currentTimeMillis()), Duration.ofMinutes(5));
    }

    public String nonceKey(String nonce) {
        return "nonce:" + nonce;
    }
}