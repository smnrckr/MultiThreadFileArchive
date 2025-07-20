package service;

import model.FileStatistics;

import java.io.File;

public class FileAnalyzerService {

    // Dosyadan güvenli bir şekilde satır okuma işlemlerini gerçekleştiren servis
    private final FileReaderService readerService = new FileReaderService();

    /**
     * Verilen dosyayı analiz eder: satır ve karakter sayılarını hesaplar.
     * @param file analiz edilecek dosya
     * @return FileStatistics nesnesi veya dosya okunamıyorsa null
     */
    public FileStatistics analyze(File file) {
        // Dosyanın tüm satırlarını oku
        String[] lines = readerService.readAllLines(file);
        if (lines == null) {
            System.err.println("Analiz atlandı: " + file.getName() + " okunamadığı için analiz yapılmadı.");
            return null;
        }
        int lineCount = lines.length; //satır sayısı
        int charCount = 0;

        for (String line : lines) {
            charCount += line.length(); //karakter sayısı
        }

        return new FileStatistics(lineCount, charCount);
    }
}
