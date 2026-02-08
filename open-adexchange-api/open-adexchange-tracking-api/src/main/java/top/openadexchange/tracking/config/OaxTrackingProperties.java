package top.openadexchange.tracking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "oax.tracking")
@Data
public class OaxTrackingProperties {

    private String executorFactory;
    private String adDedupService;
}
