package top.openadexchange.tracking.domain.gateway;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.chaincoretech.epc.ExtensionRegistry;

import jakarta.annotation.Resource;
import top.openadexchange.tracking.config.OaxTrackingProperties;

@Component
@EnableConfigurationProperties(OaxTrackingProperties.class)
public class ExecutorFactories {

    @Resource
    private OaxTrackingProperties oaxTrackingProperties;

    public ExecutorFactory getExecutorFactory() {
        return ExtensionRegistry.getExtensionByKey(ExecutorFactory.class, oaxTrackingProperties.getExecutorFactory());
    }
}
