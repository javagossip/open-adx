package top.openadexchange.mos.domain.gateway.factory;

import com.chaincoretech.epc.ExtensionRegistry;

import org.springframework.stereotype.Component;

import top.openadexchange.mos.domain.gateway.EventPublishService;

//事件发布器工厂
@Component
public class EventPublishServices {

    public EventPublishService getEventPublishService() {
        return ExtensionRegistry.getExtensionByKey(EventPublishService.class, "default");
    }

    //public EventPublisherService getDbEventPublishService(){}
}
