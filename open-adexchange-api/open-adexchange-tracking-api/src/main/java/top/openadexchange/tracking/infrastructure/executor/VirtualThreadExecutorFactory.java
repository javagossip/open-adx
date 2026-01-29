package top.openadexchange.tracking.infrastructure.executor;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.commons.concurrent.NamedThreadFactory;
import top.openadexchange.tracking.domain.gateway.ExecutorFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Extension(keys = {"virtualThread", "default"})
public class VirtualThreadExecutorFactory implements ExecutorFactory {

    private static ThreadFactory threadFactory = NamedThreadFactory.virtual("tracking-executor");

    @Override
    public ExecutorService getExecutor() {
        return Executors.newThreadPerTaskExecutor(threadFactory);
    }
}
