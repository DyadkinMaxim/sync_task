package client;

import core.Work;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PauseResume {
    private JFrame frame = new JFrame("PauseResume");
    private JButton button = new JButton("Start");
    private JTextArea textArea = new JTextArea(5, 20);

    private final Object lock = new Object();
    private volatile boolean paused = true;

    public void init() {
        JScrollPane scroll = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        button.addActionListener(pauseResume);
        textArea.setLineWrap(true);
        frame.add(button, java.awt.BorderLayout.NORTH);
        //frame.add(textArea, BorderLayout.CENTER);
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

    private java.awt.event.ActionListener pauseResume =
            new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    paused = !paused;
                    button.setText(paused ? "Resume" : "Pause");
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            };

    private void done() {
        button.setText("Start sync");
        paused = true;
    }

    public void setVisible(boolean isVisible) {
        frame.setVisible(isVisible);
    }
}