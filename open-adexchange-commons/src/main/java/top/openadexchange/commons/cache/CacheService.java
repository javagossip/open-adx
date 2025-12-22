package top.openadexchange.commons.cache;

import java.util.concurrent.TimeUnit;

import com.chaincoretech.epc.annotation.ExtensionPoint;

@ExtensionPoint
public interface CacheService {

    <T> T get(String key);

    void put(String key, Object value);

    void put(String key, Object value, long timeout, TimeUnit unit);

    boolean exists(String key);

    void delete(String key);

    Long incr(String key);

    Long incr(String key, long delta);
}
