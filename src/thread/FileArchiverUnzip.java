package thread;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileArchiverUnzip extends Thread {
    private final Path zipFile;
    private final Path outputDirectory;
    private final Semaphore semaphore;
    private final JTextArea statusTextArea;

    public FileArchiverUnzip(Path zipFile, Path outputDirectory, Semaphore semaphore, JTextArea statusTextArea){
        this.zipFile = zipFile;
        this.outputDirectory = outputDirectory;
        this.semaphore = semaphore;
        this.statusTextArea = statusTextArea;
    }
    @Override
    public void run() {
        try {
            semaphore.acquire();
            int activeCount = ThreadMonitor.activeThreads.incrementAndGet();
            SwingUtilities.invokeLater(() -> {
                statusTextArea.append("[" + getName() + "] Unzip thread started | Active threads: " + activeCount + "/10\n");
                statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
            });
            Thread.sleep(1000);
            unzipFiles();
            System.out.println("[" + getName() + "] Unzip completed -> " + outputDirectory);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
            int activeCount = ThreadMonitor.activeThreads.decrementAndGet();
            SwingUtilities.invokeLater(() -> {
                statusTextArea.append("[" + getName() + "] Unzip thread finished | Active threads: " + activeCount + "/10\n");
                statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
            });
        }
    }
    private void unzipFiles() throws IOException {
        if (Files.notExists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }
        try(ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                Path filePath = outputDirectory.resolve(zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(filePath);
                    System.out.println(" [" + getName() + "] Created directory: " + filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    copyFiles(zipInputStream, filePath);
                    System.out.println(" [" + getName() + "] Created directory: " + filePath);
                }
                zipInputStream.closeEntry();
            }
        }
    }
    private void copyFiles(InputStream inputStream, Path outpath) throws IOException {
        try(OutputStream outputStream = Files.newOutputStream(outpath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)){
            byte[] buffer = new byte[FileArchiverZip.BUFFER_SIZE];
            int length;
            while ((length = inputStream.read(buffer)) >0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }
}
