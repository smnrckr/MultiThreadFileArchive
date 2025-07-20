package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import util.SafeFileUtils;

public class FileReaderService {

    /**
     * Dosyadaki tüm satırları okur. Okuma işlemi için SafeFileUtils kullanılır,
     * böylece dosya okuma sırasında oluşabilecek hatalar güvenli şekilde yakalanır ve yönetilir.
     *
     * @param file Okunacak dosya
     * @return Satırların dizisi veya hata durumunda null
     */
    public String[] readAllLines(File file) {
        List<String> lines = SafeFileUtils.readAllLinesSafe(file);
        if (lines == null) {
            return null;
        }
        return lines.toArray(new String[0]);
    }
}
