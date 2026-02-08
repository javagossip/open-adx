package top.openadexchange.job.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.enums.EventStatus;
import top.openadexchange.dao.DomainEventDao;
import top.openadexchange.model.DomainEvent;

@Service
@Slf4j
public class DomainEventService {

    private static final String DOMAIN_EVENTS_TOPIC = "oax.domain.events";

    @Resource
    private DomainEventDao domainEventDao;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void pollAndPublishEvents() {
        int limit = 100;
        int offset = 0;
        while (true) {
            List<DomainEvent> domainEvents = domainEventDao.listAndUpdateUnHandleEvents(offset, limit);
            if (domainEvents.isEmpty()) {
                break;
            }
            publishDomainEvents(domainEvents);
            offset += limit;
        }
    }

    private void publishDomainEvents(List<DomainEvent> domainEvents) {
        Collection<DomainEvent> distinctEvents = domainEvents.stream()
                .collect(Collectors.toMap(DomainEvent::getEntityId, Function.identity(), (a, b) -> a))
                .values();

        List<CompletableFuture<?>> futureList = new ArrayList<>(distinctEvents.size());
        distinctEvents.forEach(domainEvent -> {
            log.info("Publish domain event: {}", domainEvent);
            CompletableFuture<SendResult<String, String>> sendFuture =
                    kafkaTemplate.send(DOMAIN_EVENTS_TOPIC, JSON.toJSONString(domainEvent));

            futureList.add(sendFuture.whenComplete((r, ex) -> {
                if (ex == null) {
                    log.info("Publish domain event success: {}", domainEvent);
                    domainEvent.setStatus(EventStatus.SUCCESS.name());
                } else {
                    domainEvent.setStatus(EventStatus.FAILED.name());
                    log.error("Publish domain event failed: {}", domainEvent, ex);
                }
            }));
        });
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenRun(() -> domainEventDao.updateBatch(domainEvents));
    }
}
