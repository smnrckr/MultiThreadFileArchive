package worker;

import model.FileStatistics;
import service.FileAnalyzerService;

import java.io.File;

public class Worker {

    // Dosya analiz işlemlerini gerçekleştiren servis sınıfı
    private final FileAnalyzerService analyzerService = new FileAnalyzerService();

    // Belirtilen dosyanın analizini yapan metod
    public FileStatistics process(File file) {
        System.out.println("Reading file: " + file.getName());

        // Dosya analizini gerçekleştir ve sonuçları al
        FileStatistics stats = analyzerService.analyze(file);
        if (stats == null) {
            System.err.println("HATA: " + file.getName() + " analiz edilemedi.");
            return null;
        }

        // Konsola analiz sonuçlarını yazdır
        System.out.println(file.getName() + " - " + stats);
        return stats;
    }
}
