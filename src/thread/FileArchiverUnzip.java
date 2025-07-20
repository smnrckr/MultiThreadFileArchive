package thread;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileArchiverUnzip extends Thread {
    private final Path zipFile;
    private final Path outputDirectory;
    private final Semaphore semaphore;

    public FileArchiverUnzip(Path zipFile, Path outputDirectory, Semaphore semaphore){
        this.zipFile = zipFile;
        this.outputDirectory = outputDirectory;
        this.semaphore = semaphore;
    }
    @Override
    public void run() {
        long startTime = System.nanoTime(); // Unzip işlemi başlangıcı
        try {
            // Thread başlarken semafor al
            semaphore.acquire();
            // Aktif thread sayısını güncelle
            int activeCount = ThreadMonitor.activeThreads.incrementAndGet();
            System.out.println("[" + getName() + "] Unzip thread started | Active threads: " + activeCount + "/10");
            // Unzip işlemini başlat
            unzipFiles();
            System.out.println("[" + getName() + "] Unzip completed -> " + outputDirectory);
        } catch (InterruptedException | IOException e) {
            System.err.println("[" + getName() + "] Error while unzipping: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // İşlem bitince semaforu serbest bırak
            long endTime = System.nanoTime(); // Unzip işlemi bitişi
            long duration = endTime - startTime;
            System.out.println("[" + getName() + "] Unzip duration: " + duration + " ns (" + (duration / 1_000_000.0) + " ms)");
            semaphore.release();
            int activeCount = ThreadMonitor.activeThreads.decrementAndGet();
            System.out.println("[" + getName() + "] Unzip thread finished | Active threads: " + activeCount + "/10");
        }
    }
    private void unzipFiles() throws IOException {
        // Hedef klasör yoksa oluştur
        if (Files.notExists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }
        // Zip dosyasını okuma akışına çevir
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
            ZipEntry zipEntry;
            // Zip içindeki her ögeyi sırayla işle
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                // Hedef dosya/klasör yolunu oluştur
                Path filePath = outputDirectory.resolve(zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    // Eğer klasörse hedefte oluştur
                    Files.createDirectories(filePath);
                    System.out.println("[" + getName() + "] Created directory: " + filePath);
                } else {
                    // Dosya ise önce klasörünü oluştur, sonra kopyala
                    Files.createDirectories(filePath.getParent());
                    copyFiles(zipInputStream, filePath);
                    System.out.println("[" + getName() + "] Extracted file: " + filePath);
                }
                zipInputStream.closeEntry();
            }
        }
    }
    private void copyFiles(InputStream inputStream, Path outpath) throws IOException {
        // Zipten okunan dosya içeriğini hedefe yaz
        try (OutputStream outputStream = Files.newOutputStream(outpath,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            byte[] buffer = new byte[FileArchiverZip.BUFFER_SIZE]; // Okuma/yazma için buffer
            int length;
            // Zip akışından parça parça oku(yukarıda görüldüğü gibi 4kb verdik) ve hedef dosyaya yaz
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }
}