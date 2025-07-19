import thread.FileArchiverUnzip;
import thread.FileArchiverZip;
import thread.FileProcessorThread;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        File folder = new File("input");
        File[] txtFiles = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (txtFiles != null) {
            Semaphore semaphore = new Semaphore(10);

            // Tüm dosyalar için thread listesi oluştur
            List<FileProcessorThread> threadList = new ArrayList<>();

            for (File file : txtFiles) {
                FileProcessorThread fileProcessorThread = new FileProcessorThread(file, semaphore);
                fileProcessorThread.start();
                threadList.add(fileProcessorThread);
            }

            // Hepsi bitene kadar bekle
            for (FileProcessorThread t : threadList) {
                t.join();
            }

            // Zip thread class'ının nesnesi oluşturulur.
            FileArchiverZip zipThread = new FileArchiverZip(
                    Paths.get("input"),
                    Paths.get("output/files.zip"),
                    semaphore,
                    true // zipten sonra silmesi için gerekli olan parametre
            );
            zipThread.start();
            zipThread.join();

            // Unzip thread class'ının nesnesi oluşturulur.
            FileArchiverUnzip unzipThread = new FileArchiverUnzip(
                    Paths.get("output/files.zip"),
                    Paths.get("unzipped_output"),
                    semaphore
            );
            unzipThread.start();
            unzipThread.join();

            System.out.println("Tüm işlemler başarıyla tamamlandı.");
        } else {
            System.out.println("No files found in input folder.");
        }
    }
}