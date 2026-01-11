package top.openadexchange.openapi.ssp.domain.gateway;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.chaincoretech.epc.ExtensionRegistry;

import jakarta.annotation.Resource;
import top.openadexchange.openapi.ssp.config.OaxEngineProperties;

//服务工厂
@Component
@EnableConfigurationProperties(OaxEngineProperties.class)
public class OaxEngineServices {

    private IndexService indexService;
    private IP2RegionService ip2RegionService;
    private MetadataRepository metadataRepository;

    @Resource
    private OaxEngineProperties oaxEngineProperties;

    public IndexService getIndexService() {
        if (indexService != null) {
            return indexService;
        }
        indexService = ExtensionRegistry.getExtensionByKey(IndexService.class, oaxEngineProperties.getIndexService());
        return indexService;
    }

    public IP2RegionService getIp2RegionService() {
        if (ip2RegionService != null) {
            return ip2RegionService;
        }
        ip2RegionService =
                ExtensionRegistry.getExtensionByKey(IP2RegionService.class, oaxEngineProperties.getIp2RegionService());
        return ip2RegionService;
    }

    public MetadataRepository getCachedMetadataRepository() {
        if (metadataRepository != null) {
            return metadataRepository;
        }
        metadataRepository = ExtensionRegistry.getExtensionByKey(MetadataRepository.class,
                oaxEngineProperties.getMetadataRepository());
        return metadataRepository;
    }
}
