package top.openadexchange.tracking.domain.gateway;

import com.chaincoretech.epc.ExtensionRegistry;

import jakarta.annotation.Resource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import top.openadexchange.tracking.config.OaxTrackingProperties;

import java.util.concurrent.ExecutorService;

@Component
@EnableConfigurationProperties(OaxTrackingProperties.class)
public class OaxTrackingServices {

    @Resource
    private OaxTrackingProperties oaxTrackingProperties;

    private AdDedupService adDedupService;
    private ExecutorFactory executorFactory;
    private ExecutorService executor;

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
        if (executor == null) {
            synchronized (this) {
                if (executor == null) {
                    executor = getExecutorFactory().getExecutor();
                }
            }
        }
        return executor;
    }
}
