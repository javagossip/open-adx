package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.openapi.ssp.domain.model.IpLocation;

@ExtensionPoint
public interface IP2RegionService {

    IpLocation getRegion(String ip);

    IpLocation getRegionByIpV6(String ipv6);
}
