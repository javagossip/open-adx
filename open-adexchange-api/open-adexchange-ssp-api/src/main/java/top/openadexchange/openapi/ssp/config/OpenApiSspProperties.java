package top.openadexchange.openapi.ssp.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "openapi.ssp")
public class OpenApiSspProperties {

    private String indexService;
    private String ip2RegionService;
    private String metadataRepository;
    private String executorFactory;
    private String httpClient;
}
