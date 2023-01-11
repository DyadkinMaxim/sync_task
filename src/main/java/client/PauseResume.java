package client;

import core.SyncJob;
import datasource.base.Datasource;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PauseResume {
    private JFrame frame = new JFrame("PauseResume");
    private JButton controlBtn = new JButton("Start sync");
    private JButton menuBtn = new JButton("Menu");
    private JButton exitBtn = new JButton("Exit");
    private JTextArea textArea = new JTextArea(5, 20);
    JScrollPane scroll = new JScrollPane(textArea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    private final Object lock = new Object();
    private Datasource sourceDatasource;
    private Datasource targetDatasource;
    private static volatile boolean isPaused;

    public synchronized void init(Datasource sourceDatasource, Datasource targetDatasource) {
        this.sourceDatasource = sourceDatasource;
        this.targetDatasource = targetDatasource;

        controlBtn.addActionListener(controlListener);
        menuBtn.addActionListener(menuListener);
        exitBtn.addActionListener(exitListener);
        textArea.setLineWrap(true);
        textArea.setText("");
        frame.add(controlBtn, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        //frame.add(menuBtn, BorderLayout.CENTER);
        frame.add(exitBtn, BorderLayout.SOUTH);
        frame.pack();
        frame.setSize(400, 300);
        frame.setLocation(430, 100);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        isPaused = true;
        createThread().start();
    }

    public void allowPause() {
        synchronized (lock) {
            while (isPaused) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    // nothing
                }
            }
        }
    }

    public void printProgress(String message) {
        textArea.append("\n" + LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + " " + message);
        log.info(message);
    }

    private Thread createThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    allowPause();
                    syncJob();
                }
            }
        });
    }


    private Thread monitoring = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                allowPause();
                syncJob();
            }
        }
    });

    private void syncJob() {
        SyncJob.job(sourceDatasource, targetDatasource, this);
        done();
    }

    private ActionListener controlListener =
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    isPaused = !isPaused;
                    controlBtn.setText(isPaused ? "Resume" : "Pause");
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            };

    private ActionListener menuListener =
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    isPaused = true;
                    interruptMonitoring();
                    frame.setVisible(false);
                    GUIForm.menu.setVisible(true);
                }
            };

    private ActionListener exitListener =
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        sourceDatasource.disconnect();
                        targetDatasource.disconnect();
                        System.exit(1);
                    } catch (IOException ex) {
                        log.error(ex.getMessage());
                    }
                }
            };

    private void done() {
        controlBtn.setText("Start sync");
        isPaused = true;
    }

    private void interruptMonitoring() {
        isPaused = true;
        synchronized (lock) {
            lock.notifyAll();
        }
        Thread.currentThread().interrupt();
    }
}