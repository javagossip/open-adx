package top.openadexchange.mos.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

@ExtensionPoint
public interface EventPublishService {

    void publishEvent(Object event);
}
