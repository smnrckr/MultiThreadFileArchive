package service;

import model.FileStatistics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * ResultCollector sınıfı, çoklu thread'ler tarafından elde edilen dosya analiz sonuçlarını
 * thread-safe şekilde saklar ve daha sonra toplam sonuçları yazdırır.
 */

public class ResultCollector {

    // Tüm dosya analiz sonuçlarını thread-safe biçimde saklayan ConcurrentHashMap
    private static final ConcurrentHashMap<String, FileStatistics> results = new ConcurrentHashMap<>();

    /**
     * Her bir dosyanın analiz sonucunu eşsiz ismiyle birlikte ekler.
     *
     * @param fileName Dosya adı (ör: "file1.txt")
     * @param stats    Dosyaya ait analiz sonuçları (satır ve karakter sayısı)
     */

    public static void addResult(String fileName, FileStatistics stats) {
        results.put(fileName, stats);
    }


    /**
     * Tüm dosyaların analiz sonuçlarını ekrana yazdırır.
     * Aynı zamanda toplam satır ve karakter sayılarını hesaplayarak gösterir.
     */

    public static void printAllResults() {
        int totalLines = 0;
        int totalChars = 0;

        System.out.println("\n📄 Dosya Analiz Sonuçları:");

        for (Map.Entry<String, FileStatistics> entry : results.entrySet()) {
            String fileName = entry.getKey();
            FileStatistics stats = entry.getValue();

            // Her dosya için ayrı ayrı analiz sonucunu yazdır
            System.out.println(fileName + " - " + stats.getLineCount() + " satır / " + stats.getCharacterCount() + " karakter");

            // Toplamları hesapla
            totalLines += stats.getLineCount();
            totalChars += stats.getCharacterCount();
        }
        // Toplam analiz sonucunu yazdır
        System.out.println("\n🧾 Toplam: " + totalLines + " satır / " + totalChars + " karakter");
    }
}
