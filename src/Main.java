import thread.FileArchiverUnzip;
import thread.FileArchiverZip;
import thread.FileProcessorThread;
import service.ResultCollector;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        long programStart = System.nanoTime(); // Programın toplam çalışma süresi başlangıcı
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
            // Zip ve Unzip işlemleri
            long zipStart = System.nanoTime();
            try {
                // Zip thread class'ının nesnesi oluşturulur.
                FileArchiverZip zipThread0 = new FileArchiverZip(
                        Paths.get("input"),
                        Paths.get("output/files.zip"),
                        semaphore,
                        true // zipten sonra silmesi için gerekli olan parametre
                );
                zipThread0.start();
                zipThread0.join();
            } catch (InterruptedException e) {
                System.err.println("HATA: Zip thread beklenirken hata oluştu: " + e.getMessage());
                e.printStackTrace();
            }
            long zipEnd = System.nanoTime();
            long zipDuration = zipEnd - zipStart;
            System.out.println("Toplam zip işlemi süresi: " + zipDuration + " ns (" + (zipDuration / 1_000_000.0) + " ms)");

            long unzipStart = System.nanoTime();
            try {
                // Unzip thread class'ının nesnesi oluşturulur.
                FileArchiverUnzip unzipThread = new FileArchiverUnzip(
                        Paths.get("output/files.zip"),
                        Paths.get("unzipped_output"),
                        semaphore
                );
                unzipThread.start();
                unzipThread.join();
            } catch (InterruptedException e) {
                System.err.println("HATA: Unzip thread beklenirken hata oluştu: " + e.getMessage());
                e.printStackTrace();
            }
            long unzipEnd = System.nanoTime();
            long unzipDuration = unzipEnd - unzipStart;
            System.out.println("Toplam unzip işlemi süresi: " + unzipDuration + " ns (" + (unzipDuration / 1_000_000.0) + " ms)");
            long programEnd = System.nanoTime();
            long programDuration = programEnd - programStart;
            System.out.println("Programın toplam çalışma süresi: " + programDuration + " ns (" + (programDuration / 1_000_000.0) + " ms)");

        } else {
            System.out.println("No files found in input folder.");
        }
    }
}