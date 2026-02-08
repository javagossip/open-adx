package top.openadexchange.tracking.domain.gateway;

import java.util.concurrent.ExecutorService;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.chaincoretech.epc.ExtensionRegistry;

import jakarta.annotation.Resource;
import top.openadexchange.tracking.config.OaxTrackingProperties;

@Component
@EnableConfigurationProperties(OaxTrackingProperties.class)
public class OaxTrackingServices {

    @Resource
    private OaxTrackingProperties oaxTrackingProperties;

    private AdDedupService adDedupService;
    private ExecutorFactory executorFactory;

    public AdDedupService getAdDedupService() {
        if (adDedupService == null) {
            adDedupService = ExtensionRegistry.getExtensionByKey(AdDedupService.class,
                    oaxTrackingProperties.getAdDedupService());
        }
        return adDedupService;
    }

    public ExecutorFactory getExecutorFactory() {
        if (executorFactory == null) {
            executorFactory = ExtensionRegistry.getExtensionByKey(ExecutorFactory.class,
                    oaxTrackingProperties.getExecutorFactory());
        }
        return executorFactory;
    }

    public ExecutorService getExecutor() {
        return getExecutorFactory().getExecutor();
    }
}
