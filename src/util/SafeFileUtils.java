package util;

import java.io.*;
import java.util.*;

public class SafeFileUtils {
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