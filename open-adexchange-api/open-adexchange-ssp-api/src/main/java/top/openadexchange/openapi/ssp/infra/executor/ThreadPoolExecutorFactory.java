package top.openadexchange.openapi.ssp.infra.executor;

import java.util.concurrent.ExecutorService;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.domain.gateway.ExecutorFactory;

@Extension(keys = {"threadPool"})
public class ThreadPoolExecutorFactory implements ExecutorFactory {

    @Override
    public ExecutorService getExecutor() {
        return null;
    }
}
