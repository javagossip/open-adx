package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.ExtensionRegistry;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

import top.openadexchange.openapi.ssp.config.OaxEngineProperties;

@Component
public class ExecutorFactories {

    @Resource
    private OaxEngineProperties oaxEngineProperties;
    private ExecutorFactory executorFactory;

    public ExecutorFactory getExecutorFactory() {
        if (executorFactory == null) {
            executorFactory = ExtensionRegistry.getExtensionByKey(ExecutorFactory.class, "default");
        }
        return executorFactory;
    }
}
