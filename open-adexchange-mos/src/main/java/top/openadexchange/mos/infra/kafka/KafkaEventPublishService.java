package top.openadexchange.mos.infra.kafka;

import com.alibaba.fastjson2.JSON;
import com.chaincoretech.epc.annotation.Extension;

import jakarta.annotation.Resource;

import org.springframework.kafka.core.KafkaTemplate;

import top.openadexchange.model.DomainEvent;
import top.openadexchange.mos.domain.gateway.EventPublishService;

import java.util.Arrays;
import java.util.Map;

@Extension(keys = {"kafka", "default"})
public class KafkaEventPublishService implements EventPublishService {

    private static final String DEFAULT_TOPIC = "oax.metadata";
    private static final Map<String, String> eventTypeTopicMap = Map.of("DspCreated", "oax.dsp");

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publishEvent(DomainEvent event) {
        String eventType = event.getType();
        //根据eventType获取对应的topic, 默认使用oax.metadata
        String topic = resolveTopic(eventType);
        kafkaTemplate.send(topic, JSON.toJSONString(event));
    }

    private String resolveTopic(String eventType) {
        return eventTypeTopicMap.getOrDefault(eventType, DEFAULT_TOPIC);
    }

    @Override
    public void publishEvents(DomainEvent... events) {
        if (events == null) {
            return;
        }
        Arrays.stream(events).forEach(this::publishEvent);
    }
}
