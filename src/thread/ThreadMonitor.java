package thread;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadMonitor {
    public static final AtomicInteger activeThreads = new AtomicInteger(0);
}

