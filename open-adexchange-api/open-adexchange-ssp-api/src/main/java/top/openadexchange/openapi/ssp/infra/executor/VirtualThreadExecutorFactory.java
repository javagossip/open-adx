package top.openadexchange.openapi.ssp.infra.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.chaincoretech.epc.annotation.Extension;

import lombok.extern.slf4j.Slf4j;
import top.openadexchange.openapi.ssp.domain.gateway.ExecutorFactory;

@Extension(keys = {"virtualThread", "default"})
@Slf4j
public class VirtualThreadExecutorFactory implements ExecutorFactory {

    private static final ExecutorService EXECUTOR_INSTANCE = Executors.newThreadPerTaskExecutor(Thread.ofVirtual()
            .name("oax-vt-", 1)
            .inheritInheritableThreadLocals(false)
            .uncaughtExceptionHandler((t, e) -> e.printStackTrace())
            .factory());

    @Override
    public ExecutorService getExecutor() {
        return EXECUTOR_INSTANCE;
    }
}
