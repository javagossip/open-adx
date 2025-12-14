package top.openadexchange.mos.domain.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import top.openadexchange.mos.domain.event.PublisherCreatedEvent;

@Component
public class PublisherEventListener {
    @EventListener
    public void handlePublisherCreatedEvent(PublisherCreatedEvent event) {
        //TODO:
    }
}
