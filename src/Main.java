import thread.FileProcessorThread;
import service.ResultCollector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        File folder = new File("input");

        File[] txtFiles = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (txtFiles != null) {
            Semaphore semaphore = new Semaphore(10);
            List<Thread> threads = new ArrayList<>();

            for (File file : txtFiles) {
                FileProcessorThread fileProcessorThread = new FileProcessorThread(file, semaphore);
                threads.add(fileProcessorThread);
                fileProcessorThread.start();
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ResultCollector.printAllResults();

        } else {
            System.out.println("No files found in input folder.");
        }
    }
}
