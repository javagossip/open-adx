package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.ExtensionRegistry;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

import top.openadexchange.openapi.ssp.config.OpenApiSspProperties;

@Component
public class ExecutorFactories {

    @Resource
    private OpenApiSspProperties openApiSspProperties;
    private ExecutorFactory executorFactory;

    @PostConstruct
    public void init() {
        executorFactory =
                ExtensionRegistry.getExtensionByKey(ExecutorFactory.class, openApiSspProperties.getExecutorFactory());
    }

    public ExecutorFactory getExecutorFactory() {
        return executorFactory;
    }
}
