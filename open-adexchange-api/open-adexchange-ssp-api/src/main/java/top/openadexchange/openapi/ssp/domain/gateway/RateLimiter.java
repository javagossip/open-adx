package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

@ExtensionPoint
public interface RateLimiter {

    void setLimit(int qps);

    boolean tryAcquire();
}
