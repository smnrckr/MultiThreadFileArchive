import thread.FileProcessorThread;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File folder = new File("input");

        File[] txtFiles = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (txtFiles != null) {
            for (File file : txtFiles) {
                FileProcessorThread fileProcessorThread = new FileProcessorThread(file);
                fileProcessorThread.start();
            }
        } else {
            System.out.println("No files found in input folder.");
        }
    }
}
