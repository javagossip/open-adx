package top.openadexchange.tracking.infrastructure.executor;

import com.chaincoretech.epc.annotation.Extension;

import lombok.extern.slf4j.Slf4j;
import top.openadexchange.commons.concurrent.NamedThreadFactory;
import top.openadexchange.tracking.domain.gateway.ExecutorFactory;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Extension(keys = {"virtualThread", "default"})
@Slf4j
public class VirtualThreadExecutorFactory implements ExecutorFactory {

    private static final ExecutorService EXECUTOR_INSTANCE = Executors.newThreadPerTaskExecutor((Thread.ofVirtual()
            .name("tracking-executor-", 1)
            .inheritInheritableThreadLocals(false)
            .uncaughtExceptionHandler((t, e) -> {
                log.error("Uncaught exception in tracking thread: {}", t.getName(), e);
            })
            .factory()));

    @Override
    public ExecutorService getExecutor() {
        return EXECUTOR_INSTANCE;
    }
}
