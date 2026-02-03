package top.openadexchange.tracking.infrastructure.kafka;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import jakarta.annotation.Resource;
import top.openadexchange.dto.event.DspBidEvent;
import top.openadexchange.dto.event.DspReqEvent;
import top.openadexchange.dto.event.DspWinEvent;
import top.openadexchange.tracking.application.service.TrackingService;
import top.openadexchange.tracking.infrastructure.constants.KafkaConstants;

@Component
@Slf4j
public class DspTrackEventListener {

    @Resource
    private TrackingService trackingService;

    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC_DSP_REQ,
            groupId = KafkaConstants.KAFKA_CONSUMER_GROUP_DSP_REQ)
    public void onDspReqEvent(String event) {
        log.info("onDspReqEvent: {}", event);
        DspReqEvent dspReqEvent = JSON.parseObject(event, DspReqEvent.class);
        trackingService.onDspReqEvent(dspReqEvent);
    }

    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC_DSP_BID,
            groupId = KafkaConstants.KAFKA_CONSUMER_GROUP_DSP_BID)
    public void onDspBidEvent(String event) {
        log.info("onDspBidEvent: {}", event);
        DspBidEvent dspBidEvent = JSON.parseObject(event, DspBidEvent.class);
        trackingService.onDspBidEvent(dspBidEvent);
    }

    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC_DSP_WIN,
            groupId = KafkaConstants.KAFKA_CONSUMER_GROUP_DSP_WIN)
    public void onDspWinEvent(String event) {
        log.info("onDspWinEvent: {}", event);
        DspWinEvent dspWinEvent = JSON.parseObject(event, DspWinEvent.class);
        trackingService.onDspWinEvent(dspWinEvent);
    }
}

