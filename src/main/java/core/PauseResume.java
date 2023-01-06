package core;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PauseResume {
    private JFrame frame = new JFrame("PauseResume");
    private JButton button = new JButton("Start");
    private JTextArea textArea = new JTextArea(5, 20);
    JLabel inProgresslabel = new JLabel("Sync in progress...");

    private Object lock = new Object();
    private volatile boolean paused = true;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PauseResume();
            }
        });
    }

    public PauseResume() {
        job.start();
        button.addActionListener(pauseResume);

        textArea.setLineWrap(true);
        frame.add(button, java.awt.BorderLayout.NORTH);
        frame.add(textArea, java.awt.BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private Thread job = new Thread(new Runnable() {
        @Override
        public void run() {
            allowPause();
            frame.add(button, BorderLayout.SOUTH);
            frame.add(inProgresslabel);
            Work.work();
            done();
        }
    });

    private void allowPause() {
        synchronized (lock) {
            while (paused) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    log.info(ex.getMessage());
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
        } catch (InterruptedException ex) {
            log.info(ex.getMessage());
        }
    }

    private void done() {
        button.setText("Sync again");
        inProgresslabel.setVisible(false);
        paused = true;
    }
}
