package top.openadexchange.openapi.ssp.infra.kafka;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//监听OAX元数据变更消息, 实时更新元数据缓存以及索引
@Component
@Slf4j
public class AdMetadataListener {

    @KafkaListener(topics = KafkaConstants.TOPIC_OAX_METADATA,
            groupId = KafkaConstants.CONSUMER_GROUP_OAX_METADATA)
    public void listen(String message) {
        log.info("Received message: {}", message);
    }
}
