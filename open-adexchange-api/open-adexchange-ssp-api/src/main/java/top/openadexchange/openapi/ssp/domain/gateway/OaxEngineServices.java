package top.openadexchange.openapi.ssp.domain.gateway;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.chaincoretech.epc.ExtensionRegistry;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import top.openadexchange.openapi.ssp.config.OaxEngineProperties;

//服务工厂
@Component
@EnableConfigurationProperties(OaxEngineProperties.class)
public class OaxEngineServices {

    private IndexService indexService;
    private IP2RegionService ip2RegionService;
    private MetadataRepository dbMetadataRepository;
    private MetadataRepository cachedMetadataRepository;

    @Resource
    private OaxEngineProperties oaxEngineProperties;

    @PostConstruct
    public void init() {
        indexService = ExtensionRegistry.getExtensionByKey(IndexService.class, oaxEngineProperties.getIndexService());
        ip2RegionService =
                ExtensionRegistry.getExtensionByKey(IP2RegionService.class, oaxEngineProperties.getIp2RegionService());
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
