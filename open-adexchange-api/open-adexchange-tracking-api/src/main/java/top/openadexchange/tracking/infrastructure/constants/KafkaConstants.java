package top.openadexchange.tracking.infrastructure.constants;

public final class KafkaConstants {

    // Kafka Topics
    public static final String KAFKA_TOPIC_IMPRESSION = "oax.imp.events";
    public static final String KAFKA_TOPIC_CLICK = "oax.click.events";
    public static final String KAFKA_TOPIC_BILLING = "oax.billing.events";
    public static final String KAFKA_TOPIC_DSP_BID = "oax.bid.events";
    public static final String KAFKA_TOPIC_DSP_WIN = "oax.win.events";
    public static final String KAFKA_TOPIC_DSP_REQ = "oax.req.events";

    public static final String KAFKA_CONSUMER_GROUP_DSP_BID = "oax.bid.events.listener";
    public static final String KAFKA_CONSUMER_GROUP_DSP_WIN = "oax.win.events.listener";
    public static final String KAFKA_CONSUMER_GROUP_DSP_REQ = "oax.req.events.listener";
}
