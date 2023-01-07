package client;

import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Finish {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Finish");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 130);
        frame.setLocation(430, 100);

        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        JLabel label = new JLabel("Synchronization finished successfully :)");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        JButton agianButton = new JButton("Sync again");
        agianButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(agianButton);

        JButton menuButton = new JButton("To menu");
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(menuButton);
        frame.setVisible(true);
    }
}
