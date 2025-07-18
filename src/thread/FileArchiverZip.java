package thread;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileArchiverZip  extends Thread {
    private final Path inputDirectory;
    private final Path outputZip;
    private final Semaphore semaphore;
    private final boolean deleteAfterZip;
    private final JTextArea statusTextArea;
    public static final int BUFFER_SIZE = 4096;


    public FileArchiverZip(Path inputDirectory, Path outputZip, Semaphore semaphore, boolean deleteAfterZip, JTextArea statusTextArea) {
        this.inputDirectory = inputDirectory;
        this.outputZip = outputZip;
        this.semaphore = semaphore;
        this.deleteAfterZip=deleteAfterZip;
        this.statusTextArea = statusTextArea;
    }
    @Override
    public void run() {
        try {
            semaphore.acquire();
            int currentCount = ThreadMonitor.activeThreads.incrementAndGet();
            SwingUtilities.invokeLater(() -> {
                statusTextArea.append("[" + getName() + "] Zip thread started | Active threads: " + currentCount + "/10\n");
                statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
            });
            Thread.sleep(1000);
            List<Path> file_list = zipFiles();
            if (deleteAfterZip) {
                deleteFileFromZip(file_list);
            }
            System.out.println("[" + getName() + "] Zip completed -> " + outputZip);
        } catch (IOException  | InterruptedException e) {
            System.err.println("[" + getName() + "] Error while zipping: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private List<Path> zipFiles() throws IOException {
        if (Files.notExists(outputZip.getParent())) {
            Files.createDirectories(outputZip.getParent());
        }
        List<Path> files = Files.list(inputDirectory)
                .filter(path -> path.toString().endsWith(".txt"))
                .toList();
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputZip.toFile()))) {
            for (Path file : files) {
                addFileToZip(file, zipOut);
            }
        }
        return files;
    }
    private void addFileToZip(Path file, ZipOutputStream zipOut) throws IOException {
        try(FileInputStream fileIn = new FileInputStream(file.toFile())) {
            ZipEntry zipEntry = new ZipEntry(file.getFileName().toString());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[BUFFER_SIZE];
            int length;
            while ((length = fileIn.read(bytes)) != -1) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.closeEntry();
            System.out.println("[" + getName() + "] Zipped: " + file.getFileName());
        }
    }
    private void deleteFileFromZip(List<Path> files) throws IOException {
        for (Path file : files) {
            try {
                Files.deleteIfExists(file);
                System.out.println("[" + getName() + "] Deleted: " + file);
            } catch (IOException e) {
                System.err.println("[" + getName() + "] Could not delete: " + file.getFileName() + " (" + e.getMessage() + ")");
            }
        }
    }
}
