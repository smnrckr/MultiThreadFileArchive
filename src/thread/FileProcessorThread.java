package thread;

import worker.Worker;

import java.io.File;
import java.util.concurrent.Semaphore;

public class FileProcessorThread extends Thread {
    private final File file;
    private final Semaphore semaphore;

    public FileProcessorThread(File file, Semaphore semaphore) {
        this.file = file;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();

            int currentCount = ThreadMonitor.activeThreads.incrementAndGet();
            System.out.println("[" + getName() + "] Started " + file.getName() + " | Active threads: " + currentCount + "/10");

            Worker worker = new Worker();
            worker.process(file);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
            int currentCount = ThreadMonitor.activeThreads.decrementAndGet();
            System.out.println("[" + getName() + "] Finished " + file.getName() + " | Active threads: " + currentCount + "/10");
        }
    }
}
