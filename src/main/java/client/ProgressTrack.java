package client;


import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

public class ProgressTrack {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;

    public ProgressTrack(){
        prepareGUI();
    }
    public static void main(String[] args){
        ProgressTrack  swingControlDemo = new ProgressTrack();
        swingControlDemo.showProgressBarDemo();
    }
    private void prepareGUI(){
        mainFrame = new JFrame("Java Swing Examples");
        mainFrame.setSize(400,400);
        mainFrame.setLayout(new GridLayout(3, 1));

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });
        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("",JLabel.CENTER);
        statusLabel.setSize(350,100);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }
    private ProgressMonitor progressMonitor;
    private Task task;
    private JButton startButton;
    private JTextArea outputTextArea;

    private void showProgressBarDemo(){
        headerLabel.setText("Control in action: ProgressMonitor");
        startButton = new JButton("Start");
        outputTextArea = new JTextArea("",5,20);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressMonitor = new ProgressMonitor(mainFrame,
                        "Running Task",
                        "", 0, 100);
                progressMonitor.setProgress(0);
                task = new Task();
                task.start();
            }
        });
        controlPanel.add(startButton);
        controlPanel.add(scrollPane);
        mainFrame.setVisible(true);
    }
    private class Task extends Thread {
        public Task(){
        }
        public void run(){
            for(int i =0; i<= 100; i+=10){
                final int progress = i;

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        progressMonitor.setProgress(progress);
                        outputTextArea.setText(outputTextArea.getText()
                                + String.format("Completed %d%% of task.\n", progress));
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }
    }
}