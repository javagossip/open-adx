package top.openadexchange.mos.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.model.DomainEvent;

@ExtensionPoint
public interface EventPublishService {

    void publishEvent(DomainEvent event);

    void publishEvents(DomainEvent... events);
}
