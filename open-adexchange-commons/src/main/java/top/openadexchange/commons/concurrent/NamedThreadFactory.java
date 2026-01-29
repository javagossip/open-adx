package top.openadexchange.commons.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    private final boolean virtual;
    private final boolean daemon;

    public NamedThreadFactory(String namePrefix) {
        this(namePrefix, false);
    }

    public NamedThreadFactory(String namePrefix, boolean virtual) {
        this(namePrefix, virtual, true);
    }

    public NamedThreadFactory(String namePrefix, boolean virtual, boolean daemon) {
        this.namePrefix = namePrefix;
        this.virtual = virtual;
        this.daemon = daemon;
    }

    public static NamedThreadFactory virtual(String namePrefix) {
        return new NamedThreadFactory(namePrefix, true);
    }

    public static NamedThreadFactory def(String namePrefix) {
        return new NamedThreadFactory(namePrefix, false, true);
    }

    @Override
    public Thread newThread(Runnable runnable) {
        if (virtual) {
            return Thread.ofVirtual().name(namePrefix + "-" + threadNumber.getAndIncrement()).unstarted(runnable);
        } else {
            Thread thread = new Thread(runnable, namePrefix + "-" + threadNumber.getAndIncrement());
            thread.setDaemon(daemon);
            return thread;
        }
    }
}
