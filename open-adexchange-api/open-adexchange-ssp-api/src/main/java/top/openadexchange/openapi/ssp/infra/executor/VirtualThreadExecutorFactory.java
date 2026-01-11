package top.openadexchange.openapi.ssp.infra.executor;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.domain.gateway.ExecutorFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Extension(keys = {"vthread"})
public class VirtualThreadExecutorFactory implements ExecutorFactory {

    @Override
    public ExecutorService getExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
