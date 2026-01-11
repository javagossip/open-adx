package top.openadexchange.openapi.ssp.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oax.engine")
public class OaxEngineProperties {

    private String indexService;
    private String ip2RegionService;
    private String metadataRepository;
    private String executorFactory;
    private String httpClient;

    private int dspCallTimeout = 200;
}
