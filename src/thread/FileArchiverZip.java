package thread;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class FileArchiverZip extends Thread {
    private final Path inputDirectory;
    private final Path outputZip;
    private final Semaphore semaphore;
    private final boolean deleteAfterZip;
    public static final int BUFFER_SIZE = 4096;

    // Burada bu sınıf içindeki thread-safe map ile analiz sonuçlarını tutuyoruz
    private static final ConcurrentHashMap<String, FileStatistics> results = new ConcurrentHashMap<>();

    //line + char sayısı tutar
    private static class FileStatistics {
        private final int lineCount;
        private final int charCount;

        public FileStatistics(int lineCount, int charCount) {
            this.lineCount = lineCount;
            this.charCount = charCount;
        }

        public int getLineCount() {
            return lineCount;
        }

        public int getCharCount() {
            return charCount;
        }
    }
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
            //  Ziplenen dosyalar analiz ediliyor
            analyzeFiles(fileList);
            // Sonuçlar ekrana yazdırılıyor
            printResults();

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
            System.out.println("[" + getName() + "] Zip duration: " + duration + " ns (" + (duration / 1_000_000.0) + " ms)");
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
    // Her dosyanın satır ve karakter sayısını hesaplar
    private void analyzeFiles(List<Path> files) throws IOException {
        for (Path file : files) {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            int lineCount = lines.size();
            int charCount = lines.stream().mapToInt(String::length).sum();
            results.put(file.getFileName().toString(), new FileStatistics(lineCount, charCount));  // ✔ doğru olan bu
        }
    }
    //  Analiz sonuçlarını ekrana yazdırır
    public static void printResults() {
        int totalLines = 0;
        int totalChars = 0;

        System.out.println("\n📄 Dosya Analiz Sonuçları:");

        for (Map.Entry<String, FileStatistics> entry : results.entrySet()) {
            String fileName = entry.getKey();
            FileStatistics stats = entry.getValue();

            System.out.println(fileName + " - " + stats.getLineCount() + " satır / " + stats.getCharCount() + " karakter");

            totalLines += stats.getLineCount();
            totalChars += stats.getCharCount();
        }

        System.out.println("\n🧾 Toplam: " + totalLines + " satır / " + totalChars + " karakter");
    }

}