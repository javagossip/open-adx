package top.openadexchange.commons.cache;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;

import org.springframework.data.redis.core.RedisOperations;

public final class RedisOpsUtils {

    public static void sadd(RedisOperations redisOps, String key, Set<String> values, Duration expire) {
        redisOps.opsForSet().add(key, values.toArray(new String[0]));
        Long existsExpire = redisOps.getExpire(key);
        if (existsExpire == null || existsExpire < 0) {
            redisOps.expire(key, expire);
        }
    }

    public static void sadd(RedisOperations redisOps, String key, String value, Duration expire) {
        redisOps.opsForSet().add(key, value);
        Long existsExpire = redisOps.getExpire(key);
        if (existsExpire == null || existsExpire < 0) {
            redisOps.expire(key, expire);
        }
    }
}
