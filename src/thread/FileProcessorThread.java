package thread;

import worker.Worker;

import java.io.File;

public class FileProcessorThread extends Thread {
    private final File file;

    public FileProcessorThread(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        System.out.println("[" + Thread.currentThread().getName() + "] Started processing: " + file.getName());

        Worker worker = new Worker();
        worker.process(file);

        System.out.println("[" + Thread.currentThread().getName() + "] Finished processing: " + file.getName());
    }
}
