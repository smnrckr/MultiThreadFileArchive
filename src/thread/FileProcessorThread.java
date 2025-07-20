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
        long startTime = System.nanoTime(); // Dosya analizi başlangıcı
        try {
            // izin al
            semaphore.acquire();

            // aktif thread sayısını getir
            int currentCount = ThreadMonitor.activeThreads.incrementAndGet();
            System.out.println("[" + getName() + "] Started " + file.getName() + " | Active threads: " + currentCount + "/10");

            // dosyayı işle
            Worker worker = new Worker();
            worker.process(file);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // izin serbest bırak
            semaphore.release();

            // aktif thread sayısını azalt
            int currentCount = ThreadMonitor.activeThreads.decrementAndGet();
            long endTime = System.nanoTime(); // Dosya analizi bitişi
            long duration = endTime - startTime;
            System.out.println("[" + getName() + "] " + file.getName() + " analysis duration: " + duration + " ns (" + (duration / 1_000_000.0) + " ms)");
            System.out.println("[" + getName() + "] Finished " + file.getName() + " | Active threads: " + currentCount + "/10");
        }
    }
}