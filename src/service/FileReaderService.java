package service;

import java.io.File;
import java.util.List;
import util.SafeFileUtils;

public class FileReaderService {

    /**
     * Verilen dosyayı okuyup tüm satırlarını dizi olarak döndürür. Okuma işlemi için SafeFileUtils kullanılır,
     * böylece dosya okuma sırasında oluşabilecek hatalar güvenli şekilde yakalanır ve yönetilir.
     *
     * @param file Okunacak dosya
     * @return String dizisi olarak satırlar, hata olursa null
     */
    public String[] readAllLines(File file) {
        List<String> lines = SafeFileUtils.readAllLinesSafe(file);
        if (lines == null) {
            return null;
        }
        return lines.toArray(new String[0]);
    }
}
