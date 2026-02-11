package top.openadexchange.openapi.ssp.infra.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.enums.DomainEventType;
import top.openadexchange.model.DomainEvent;
import top.openadexchange.openapi.ssp.application.service.ApplicationWarmupService;
import top.openadexchange.openapi.ssp.domain.model.DomainEventPayload;

@Component
@Slf4j
public class OaxDomainEventListener {

    @Resource
    private ApplicationWarmupService applicationWarmupService;

    @KafkaListener(topics = KafkaConstants.TOPIC_OAX_DOMAIN_EVNETS,
            id = "oax.domain.events.listener-${HOSTNAME:localhost}-${server.port}")
    public void onDomainEvent(String event) {
        log.info("onDomainEvent: {}", event);
        DomainEvent domainEvent = JSON.parseObject(event, DomainEvent.class);
        DomainEventType eventType = DomainEventType.valueOf(domainEvent.getType());
        DomainEventPayload payload = JSON.parseObject(domainEvent.getPayload(), DomainEventPayload.class);
        Long entityId = payload.getEntityId();

        switch (eventType) {
            case AD_PLACEMENT_CREATED:
            case AD_PLACEMENT_UPDATED:
            case AD_PLACEMENT_DELETED:
                applicationWarmupService.updateAdPlacementById(entityId);
                break;
            case DSP_CREATED:
            case DSP_UPDATED:
            case DSP_DELETED:
                applicationWarmupService.updateDspById(entityId);
                break;
            case SITE_CREATED:
            case SITE_UPDATED:
            case SITE_DELETED:
                applicationWarmupService.updateSiteById(entityId);
                break;
            case SITE_AD_PLACEMENT_CREATED:
            case SITE_AD_PLACEMENT_UPDATED:
            case SITE_AD_PLACEMENT_DELETED:
                applicationWarmupService.updateSiteAdPlacementById(entityId);
                break;
            case PUBLISHER_CREATED:
            case PUBLISHER_UPDATED:
            case PUBLISHER_DELETED:
                applicationWarmupService.updatePublisherById(entityId);
                break;
            default:
                log.warn("Unsupported event type: {}", eventType);
                break;
        }
    }
}
