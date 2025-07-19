package thread;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileArchiverZip extends Thread {
    private final Path inputDirectory;
    private final Path outputZip;
    private final Semaphore semaphore;
    private final boolean deleteAfterZip;
    public static final int BUFFER_SIZE = 4096;

    public FileArchiverZip(Path inputDirectory, Path outputZip, Semaphore semaphore, boolean deleteAfterZip) {
        this.inputDirectory = inputDirectory;
        this.outputZip = outputZip;
        this.semaphore = semaphore;
        this.deleteAfterZip = deleteAfterZip;
    }
    @Override
    public void run() {
        long startTime = System.nanoTime(); // Zip işlemi başlangıcı
        try {
            semaphore.acquire();
            int currentCount = ThreadMonitor.activeThreads.incrementAndGet();
            System.out.println("[" + getName() + "] Zip thread started | Active threads: " + currentCount + "/10");
            // Dosyaları zip'le ve listelerini al
            List<Path> fileList = zipFiles();
            // Zip sonrası dosyalar silinecek mi? Burayı silmesini istediğimiz için mainde true olarak parametre verdik
            if (deleteAfterZip) {
                deleteFileFromZip(fileList);
            }
            System.out.println("[" + getName() + "] Zip completed -> " + outputZip);
        } catch (IOException | InterruptedException e) {
            System.err.println("[" + getName() + "] Error while zipping: " + e.getMessage());
            e.printStackTrace();
        } finally {
            long endTime = System.nanoTime(); // Zip işlemi bitişi
            long duration = endTime - startTime;
            System.out.println("[" + getName() + "] Zip işlemi süresi: " + duration + " ns (" + (duration / 1_000_000.0) + " ms)");
            semaphore.release();
            int currentCount = ThreadMonitor.activeThreads.decrementAndGet();
            System.out.println("[" + getName() + "] Zip thread finished | Active threads: " + currentCount + "/10");
        }
    }
    private List<Path> zipFiles() throws IOException {
        if (Files.notExists(outputZip.getParent())) {
            Files.createDirectories(outputZip.getParent());
        }
        // inputDirectory içindeki tüm .txt dosyalarını listele
        List<Path> files = Files.list(inputDirectory)
                .filter(path -> path.toString().endsWith(".txt"))
                .toList();
        // ZipOutputStream ile zip dosyasını yazmaya başla
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputZip.toFile()))) {
            for (Path file : files) {
                addFileToZip(file, zipOut);
            }
        }
        return files; // listeyi geri döndür (silmek için kullanılacak)
    }
    private void addFileToZip(Path file, ZipOutputStream zipOut) throws IOException {
        // Her dosya için okuma akışı oluştur
        try (FileInputStream fileIn = new FileInputStream(file.toFile())) {
            // Zip dosyasına yeni bir entry (dosya) ekle
            ZipEntry zipEntry = new ZipEntry(file.getFileName().toString());
            zipOut.putNextEntry(zipEntry);
            // Dosya içeriğini buffer ile parça parça zip'e yaz
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = fileIn.read(buffer)) != -1) {
                zipOut.write(buffer, 0, length);
            }
            zipOut.closeEntry();
            System.out.println("[" + getName() + "] Zipped: " + file.getFileName());
        }
    }
    private void deleteFileFromZip(List<Path> files) throws IOException {
        // Zip işlemi tamamlandıktan sonra orijinal dosyaları sil
        for (Path file : files) {
            try {
                Files.deleteIfExists(file);
                System.out.println("[" + getName() + "] Deleted: " + file);
            } catch (IOException e) {
                System.err.println("[" + getName() + "] Could not delete: " + file.getFileName() + " (" + e.getMessage() + ")");
            }
        }
    }
}