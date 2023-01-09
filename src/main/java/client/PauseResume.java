package client;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PauseResume {
    private JFrame frame = new JFrame("PauseResume");
    private JButton button = new JButton("Start");
    private JTextArea textArea = new JTextArea(5, 20);

    private final Object lock = new Object();
    private volatile boolean paused = true;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PauseResume();
            }
        });
    }

    public void init() {
        counter.start();
        button.addActionListener(pauseResume);

        textArea.setLineWrap(true);
        frame.add(button, java.awt.BorderLayout.NORTH);
        frame.add(textArea, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(300, 200);
        frame.setLocation(430, 100);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Thread counter = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                work();
            }
        }
    });

    private void work() {
        for (int i = 0; i < 10; i++) {
            allowPause();
            write(Integer.toString(i));
            sleep();
        }
        done();
    }

    private void allowPause() {
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

    private void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // nothing
        }
    }

    private void done() {
        button.setText("Start");
        paused = true;
    }

    public void write(String str) {
        textArea.append(str);
    }

    public void setVisible(boolean isVisible) {
        frame.setVisible(isVisible);
    }
}
