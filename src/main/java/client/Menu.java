package client;

import datasource.SimpleDS;
import datasource.base.DatasourceManager;
import datasource.local.LocalDatasource;
import datasource.ssh.SSHDatasource;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class Menu {
    public static void main(String[] args) {

        var frame = new JFrame("Start menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        var panel = new JPanel();
        frame.add(panel);

        var label = new JLabel("Select target type: ");
        panel.add(label, BorderLayout.LINE_START);

        var manager = new DatasourceManager();
        manager.add(new LocalDatasource());
        manager.add(new SSHDatasource());
        manager.add(new SimpleDS());
        var choices = manager.getNames().toArray(new String[0]);

        var comboBox = new JComboBox<>(choices);
        comboBox.setMaximumSize(comboBox.getPreferredSize());
        panel.add(comboBox, BorderLayout.CENTER);

        var selectButton = new JButton("Select");
        panel.add(selectButton, BorderLayout.LINE_END);

        frame.setSize(300, 70 + (20 * choices.length));
        frame.setLocation(430, 100);
        frame.setVisible(true);
    }
}