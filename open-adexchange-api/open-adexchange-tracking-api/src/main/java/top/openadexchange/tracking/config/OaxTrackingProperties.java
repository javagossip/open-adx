package top.openadexchange.tracking.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oax.tracking")
@Data
public class OaxTrackingProperties {

    private String executorFactory;
    private String adDedupService;
}
