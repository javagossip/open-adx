package top.openadexchange.openapi.ssp.infra.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.domain.gateway.ExecutorFactory;

@Extension(keys = {"virtualThread","default"})
public class VirtualThreadExecutorFactory implements ExecutorFactory {

    @Override
    public ExecutorService getExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
