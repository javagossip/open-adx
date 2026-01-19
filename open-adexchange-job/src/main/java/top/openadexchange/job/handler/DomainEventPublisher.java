package top.openadexchange.job.handler;

import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.query.QueryWrapper;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import top.openadexchange.constants.enums.EventStatus;
import top.openadexchange.dao.DomainEventDao;
import top.openadexchange.model.DomainEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class DomainEventPublisher {

    private static final String DOMAIN_EVENTS_TOPIC = "oax.domain.events";
    private static final List<String> EVENT_STATUS_LIST =
            Arrays.asList(EventStatus.PENDING.name(), EventStatus.FAILED.name());
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private DomainEventDao domainEventDao;

    @XxlJob(value = "oax.domain.event.publisher")
    public void domainEventPollAndPublish() {
        log.info("domain event poll and publisher job start");
        int limit = 100;
        int offset = 0;
        while (true) {
            List<DomainEvent> domainEvents = domainEventDao.list(QueryWrapper.create()
                    .in(DomainEvent::getStatus, Arrays.asList(EventStatus.PENDING.name(), EventStatus.FAILED.name()))
                    .limit(offset, limit));
            if (domainEvents.isEmpty()) {
                break;
            }
            publishDomainEvents(domainEvents);
            offset += limit;
        }
    }

    private void publishDomainEvents(List<DomainEvent> domainEvents) {
        domainEvents.forEach(domainEvent -> domainEvent.setStatus(EventStatus.PROCESSING.name()));
        domainEventDao.updateBatch(domainEvents);

        CompletableFuture<?>[] futures = new CompletableFuture<?>[domainEvents.size()];
        for (int i = 0; i < domainEvents.size(); i++) {
            DomainEvent domainEvent = domainEvents.get(i);
            log.info("Publish domain event: {}", domainEvent);
            CompletableFuture<SendResult<String, String>> sendResultCF =
                    kafkaTemplate.send(DOMAIN_EVENTS_TOPIC, JSON.toJSONString(domainEvent));
            futures[i] = sendResultCF.whenComplete((r, ex) -> {
                if (ex == null) {
                    log.info("Publish domain event success: {}", domainEvent);
                    domainEvent.setStatus(EventStatus.SUCCESS.name());
                } else {
                    domainEvent.setStatus(EventStatus.FAILED.name());
                    log.error("Publish domain event failed: {}", domainEvent, ex);
                }
            });
        }
        CompletableFuture.allOf(futures).thenRun(() -> domainEventDao.updateBatch(domainEvents));
    }

    @XxlJob(value = "oax.kafka.tester")
    public void testPublishEventToKafka() {
        String param = XxlJobHelper.getJobParam();
        KafkaTesterParam kafkaTesterParam = new KafkaTesterParam();
        if (!StringUtils.hasText(param)) {
            kafkaTesterParam = JSON.parseObject(param, KafkaTesterParam.class);
        }

        log.info("==Test publish event to kafka ==");
        CompletableFuture<SendResult<String, String>> sendResultCf = kafkaTemplate.send(
                kafkaTesterParam.getTopic() == null ? KafkaTesterParam.TEST_TOPIC : kafkaTesterParam.getTopic(),
                kafkaTesterParam.getMessage() == null ? "Hello oax~" : kafkaTesterParam.getMessage());
        sendResultCf.whenComplete((r, ex) -> {
            if (ex == null) {
                log.info("发送成功: {}", r.getRecordMetadata().offset());
            } else {
                log.error("发送失败: {}", ex.getMessage(), ex);
            }
        });
    }

    @Data
    public static class KafkaTesterParam {

        public static final String TEST_TOPIC = "oax.test";

        private String topic = TEST_TOPIC;
        private String message;
    }
}
