package top.openadexchange.tracking.utils;

import java.time.Duration;

import org.springframework.data.redis.core.RedisOperations;

public final class RedisOpsUtils {

    public static void sadd(RedisOperations redisOps, String key, String value, Duration expire) {
        redisOps.opsForSet().add(key, value);
        Long existsExpire = redisOps.getExpire(key);
        if (existsExpire == null || existsExpire < 0) {
            redisOps.expire(key, expire);
        }
    }
}
