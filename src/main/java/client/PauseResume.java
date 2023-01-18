package client;

import core.SyncJob;
import datasource.base.Datasource;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import lombok.extern.slf4j.Slf4j;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

@Slf4j
public class PauseResume {
    private JFrame frame = new JFrame("PauseResume");
    private JButton controlBtn = new JButton("Start sync");
    private JTextArea textArea = new JTextArea(5, 20);
    JScrollPane scroll = new JScrollPane(textArea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    private final Object lock = new Object();
    private Datasource sourceDatasource;
    private Datasource targetDatasource;
    private static AtomicBoolean isPaused = new AtomicBoolean(true);

    public synchronized void init(Datasource sourceDatasource, Datasource targetDatasource) {
        this.sourceDatasource = sourceDatasource;
        this.targetDatasource = targetDatasource;

        controlBtn.addActionListener(controlListener);
        textArea.setLineWrap(true);
        textArea.setText("");
        frame.add(controlBtn, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.setSize(400, 300);
        var dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createThread().start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    sourceDatasource.disconnect();
                    targetDatasource.disconnect();
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                    JOptionPane.showMessageDialog(
                            null, "Can't disconnect from datasources");
                }
                e.getWindow().dispose();
            }
        });
    }

    public void allowPause() {
        synchronized (lock) {
            while (isPaused.get()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void printProgress(String message) {
        textArea.append("\n" + LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + " " + message);
        textArea.setCaretPosition(textArea.getDocument().getLength());
        log.info(message);
    }

    private Thread createThread() {
        return new Thread(() -> {
            while (true) {
                allowPause();
                syncJob();
            }
        });
    }

    private void syncJob() {
        SyncJob.job(sourceDatasource, targetDatasource, this);
    }

    private ActionListener controlListener =
            e -> {
                changePause();
                synchronized (lock) {
                    lock.notifyAll();
                }
            };

    public void changePause() {
        isPaused.compareAndSet(isPaused.get(), !isPaused.get());
        controlBtn.setText(isPaused.get() ? "Resume" : "Pause");
    }
}