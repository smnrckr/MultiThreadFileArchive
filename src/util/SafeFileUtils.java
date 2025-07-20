package util;

import java.io.*;
import java.util.*;

/**
 * SafeFileUtils, dosya okuma işlemlerini güvenli bir şekilde gerçekleştirmek için yardımcı metotlar sağlar.
 * Özellikle dosya okuma sırasında oluşabilecek hataları yakalar ve kullanıcıya bildirir.
 */
public class SafeFileUtils {
    /**
     * Verilen dosyadaki tüm satırları güvenli bir şekilde okur ve bir liste olarak döndürür.
     * Eğer dosya okunamazsa, null döner ve hata mesajı basar.
     *
     * @param file Okunacak dosya
     * @return Satırların listesi veya hata durumunda null
     */
    public static List<String> readAllLinesSafe(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("HATA: " + file.getName() + " okunamadı! Sebep: " + e.getMessage());
            return null;
        }
        return lines;
    }
} 