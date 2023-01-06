package client;

import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InProgress {
    public static void main(String[] args) {
        JFrame frame = new JFrame("In progress");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 130);
        frame.setLocation(450, 150);

        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        JLabel label = new JLabel("Synchronization is in progress...");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        JLabel progressPercent = new JLabel("56%");
        progressPercent.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(progressPercent);

        JButton stopButton = new JButton("Stop");
        stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(stopButton);
        frame.setVisible(true);

        JButton resume = new JButton("Resume");
        resume.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(resume);
        frame.setVisible(true);
    }
}
