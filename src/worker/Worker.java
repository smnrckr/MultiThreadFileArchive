package worker;

import model.FileStatistics;
import service.FileAnalyzerService;

import java.io.File;

public class Worker {

    private final FileAnalyzerService analyzerService = new FileAnalyzerService();

    public void process(File file) {
        System.out.println("Reading file: " + file.getName());
        FileStatistics stats = analyzerService.analyze(file);
        //stats.getCharacterCount();  karakter sayısına ulaşılabilir
        //stats.getLineCount(); satır sayısına ulaşılabilir
        System.out.println(file.getName() + " - " + stats);
    }
}
