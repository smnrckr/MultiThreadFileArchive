package service;

import model.FileStatistics;

import java.io.File;

public class FileAnalyzerService {
    private final FileReaderService readerService = new FileReaderService();

    public FileStatistics analyze(File file) {
        String[] lines = readerService.readAllLines(file);
        if (lines == null) {
            System.err.println("Analiz atlandı: " + file.getName() + " okunamadığı için analiz yapılmadı.");
            return null;
        }
        int lineCount = lines.length; //satır sayısı
        int charCount = 0;

        for (String line : lines) {
            charCount += line.length(); // boşluk dahil karakter sayısı
        }

        return new FileStatistics(lineCount, charCount);
    }
}
