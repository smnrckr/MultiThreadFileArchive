import thread.FileArchiverUnzip;
import thread.FileArchiverZip;
import thread.FileProcessorThread;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        JTextArea textArea_thread = new JTextArea();
        textArea_thread.setEditable(false);
        textArea_thread.setBorder(BorderFactory.createTitledBorder("Thread Durumları"));
        JTextArea textArea_log = new JTextArea();
        textArea_log.setEditable(false);
        textArea_log.setBorder(BorderFactory.createTitledBorder("Log Durumları"));

        JScrollPane scrollPane_thread = new JScrollPane(textArea_thread);
        JScrollPane scrollPane_log = new JScrollPane(textArea_log);

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(scrollPane_thread);
        panel.add(scrollPane_log);

        JFrame frame = new JFrame("Dosya Analiz Uygulaması");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(panel);
        frame.setVisible(true);

        PrintStream printStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                SwingUtilities.invokeLater(()-> {
                    textArea_log.append(String.valueOf((char) b));
                    textArea_log.setCaretPosition(textArea_log.getDocument().getLength());
                });
            }
        }, true);
        System.setOut(printStream);
        System.setErr(printStream);

        File folder = new File("input");

        File[] txtFiles = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (txtFiles != null) {
            Semaphore semaphore = new Semaphore(10);

            for (File file : txtFiles) {
                FileProcessorThread fileProcessorThread = new FileProcessorThread(
                        file,
                        semaphore,
                        textArea_thread
                );
                fileProcessorThread.start();
                fileProcessorThread.join();
            }
            FileArchiverZip zipThread = new FileArchiverZip(
                    Paths.get("input"),
                    Paths.get("output/files.zip"),
                    semaphore,
                    true,
                    textArea_thread
            );
            zipThread.start();
            zipThread.join();
            FileArchiverUnzip unzipThread = new FileArchiverUnzip(
                    Paths.get("output/files.zip"),
                    Paths.get("unzipped_output"),
                    semaphore,
                    textArea_thread
            );
            unzipThread.start();
            unzipThread.join();
        } else {
            System.out.println("No files found in input folder.");
        }
    }
}
