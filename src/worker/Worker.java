package worker;

import model.FileStatistics;
import service.FileAnalyzerService;
import service.ResultCollector;

import java.io.File;

public class Worker {

    private final FileAnalyzerService analyzerService = new FileAnalyzerService();

    public void process(File file) {
        System.out.println("Reading file: " + file.getName());
        FileStatistics stats = analyzerService.analyze(file);
        if (stats == null) {
            System.err.println("HATA: " + file.getName() + " analiz edilemedi.");
            return;
        }
        ResultCollector.addResult(file.getName(), stats);
        //stats.getCharacterCount();  karakter sayısına ulaşılabilir
        //stats.getLineCount(); satır sayısına ulaşılabilir
        System.out.println(file.getName() + " - " + stats);
    }
}
