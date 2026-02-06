package top.openadexchange.openapi.ssp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "oax.engine")
public class OaxEngineProperties {

    private String indexService;
    private String ip2RegionService;
    private String metadataRepository;
    private String executorFactory;
    private String httpClient;

    private int dspCallTimeout = 200;
    private String rateLimiterFactory;

    /**
     * 跟踪服务器URL，用于生成ADX平台自有的曝光和点击监测地址
     */
    private String trackingUrl;
}
