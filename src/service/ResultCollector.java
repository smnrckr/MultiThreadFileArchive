package service;

import model.FileStatistics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ResultCollector {

    private static final ConcurrentHashMap<String, FileStatistics> results = new ConcurrentHashMap<>();

    public static void addResult(String fileName, FileStatistics stats) {
        results.put(fileName, stats);
    }

    public static void printAllResults() {
        int totalLines = 0;
        int totalChars = 0;

        System.out.println("\n📄 Dosya Analiz Sonuçları:");

        for (Map.Entry<String, FileStatistics> entry : results.entrySet()) {
            String fileName = entry.getKey();
            FileStatistics stats = entry.getValue();

            System.out.println(fileName + " - " + stats.getLineCount() + " satır / " + stats.getCharacterCount() + " karakter");

            totalLines += stats.getLineCount();
            totalChars += stats.getCharacterCount();
        }

        System.out.println("\n🧾 Toplam: " + totalLines + " satır / " + totalChars + " karakter");
    }
}
