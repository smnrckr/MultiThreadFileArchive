package thread;

import worker.Worker;

import javax.swing.*;
import java.io.File;
import java.util.concurrent.Semaphore;

public class FileProcessorThread extends Thread {
    private final File file;
    private final Semaphore semaphore;
    private final JTextArea statusTextArea;

    public FileProcessorThread(File file, Semaphore semaphore, JTextArea statusTextArea) {
        this.file = file;
        this.semaphore = semaphore;
        this.statusTextArea = statusTextArea;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();

            int currentCount = ThreadMonitor.activeThreads.incrementAndGet();
            SwingUtilities.invokeLater(() -> {
                statusTextArea.append("[" + getName() + "] Started " + file.getName() + " | Active threads: " + currentCount + "/10\n");
                statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
            });
            Worker worker = new Worker();
            worker.process(file);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
            int currentCount = ThreadMonitor.activeThreads.decrementAndGet();
            SwingUtilities.invokeLater(() -> {
                statusTextArea.append("[" + getName() + "] Finished " + file.getName() + " | Active threads: " + currentCount + "/10\n");
                statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
            });
        }
    }
}
