package top.openadexchange.job.handler;

import org.springframework.stereotype.Component;

import com.xxl.job.core.handler.annotation.XxlJob;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.job.service.DomainEventService;

@Component
@Slf4j
public class DomainEventPublisher {

    @Resource
    private DomainEventService domainEventService;

    @XxlJob(value = "oax.domain.event.publisher")
    public void domainEventPollAndPublish() {
        try {
            log.info("domain event poll and publisher job start");
            domainEventService.pollAndPublishEvents();
            log.info("domain event poll and publisher job end");
        } catch (Exception ex) {
            log.error("domain event poll and publisher job error", ex);
            throw ex;
        }
    }
}
