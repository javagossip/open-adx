package top.openadexchange.openapi.ssp.domain.gateway;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.chaincoretech.epc.ExtensionRegistry;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import top.openadexchange.openapi.ssp.config.OpenApiSspProperties;

//服务工厂
@Component
@EnableConfigurationProperties(OpenApiSspProperties.class)
public class OpenApiSspServices {

    private IndexService indexService;
    private IP2RegionService ip2RegionService;
    private MetadataRepository dbMetadataRepository;
    private MetadataRepository cachedMetadataRepository;

    @Resource
    private OpenApiSspProperties openApiSspProperties;

    @PostConstruct
    public void init() {
        indexService = ExtensionRegistry.getExtensionByKey(IndexService.class, openApiSspProperties.getIndexService());
        ip2RegionService =
                ExtensionRegistry.getExtensionByKey(IP2RegionService.class, openApiSspProperties.getIp2RegionService());
        dbMetadataRepository = ExtensionRegistry.getExtensionByKey(MetadataRepository.class, "db");
        cachedMetadataRepository = ExtensionRegistry.getExtensionByKey(MetadataRepository.class, "cache");
    }

    public IndexService getIndexService() {
        return indexService;
    }

    public IP2RegionService getIp2RegionService() {
        return ip2RegionService;
    }

    public MetadataRepository getDbMetadataRepository() {
        return dbMetadataRepository;
    }

    public MetadataRepository getCachedMetadataRepository() {
        return cachedMetadataRepository;
    }
}
