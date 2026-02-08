package top.openadexchange.mos.application.factory;

import java.util.Map;

import com.alibaba.fastjson2.JSON;

import top.openadexchange.constants.enums.EventStatus;
import top.openadexchange.model.DomainEvent;

public class DomainEventFactory {

    public static DomainEvent create(String eventType, Number entityId) {
        DomainEvent domainEvent = new DomainEvent();
        domainEvent.setEntityId(entityId.toString());
        domainEvent.setType(eventType);
        domainEvent.setPayload(JSON.toJSONString(Map.of("entityId", entityId)));
        domainEvent.setStatus(EventStatus.PENDING.name());
        return domainEvent;
    }
}
