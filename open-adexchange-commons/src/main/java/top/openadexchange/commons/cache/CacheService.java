package top.openadexchange.commons.cache;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import java.util.concurrent.TimeUnit;

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
