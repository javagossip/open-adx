package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

@ExtensionPoint
public interface RateLimiterFactory {

    RateLimiter createRateLimiter(Integer limit);
}
