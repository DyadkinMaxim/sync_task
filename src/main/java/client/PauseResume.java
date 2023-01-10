package client;

import core.Work;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private JTextArea textArea = new JTextArea(5, 20);

    private final Object lock = new Object();
    private volatile boolean paused = true;

    public void init() {
        JScrollPane scroll = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        controlBtn.addActionListener(controlListener);
        menuBtn.addActionListener(menuListener);
        textArea.setLineWrap(true);
        frame.add(controlBtn, BorderLayout.NORTH);
        frame.add(menuBtn, BorderLayout.SOUTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(400, 300);
        frame.setLocation(430, 100);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        monitoring.start();
    }

    public void allowPause() {
        synchronized (lock) {
            while (paused) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    // nothing
                }
            }
        }
    }

    public void printProgress(String message) {
        textArea.append("\n" + message);
        log.info(message);
    }

    private Thread monitoring = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                allowPause();
                work();
            }
        }
    });

    private void work() {
        Work.work(this);
        done();
    }

    private ActionListener controlListener =
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    paused = !paused;
                    controlBtn.setText(paused ? "Resume" : "Pause");
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            };

    private ActionListener menuListener =
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    interruptMonitoring();
                    frame.setVisible(false);
                    GUIForm.menu.setVisible(true);
                }
            };

    private void done() {
        controlBtn.setText("Start sync");
        paused = true;
    }

    private void interruptMonitoring() {
        paused = true;
        synchronized (lock) {
            lock.notifyAll();
        }
        monitoring.interrupt();
    }
}