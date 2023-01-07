package client;

import datasource.base.DatasourceManager;
import datasource.local.LocalDatasource;
import datasource.ssh.SSHDatasource;
import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class Menu {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Start menu");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 100);
        frame.setLocation(430, 100);

        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        frame.add(panel);

        JLabel label = new JLabel("Select target type: ");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        DatasourceManager manager = new DatasourceManager();
        manager.add(new LocalDatasource());
        manager.add(new SSHDatasource());
        String[] choices = manager.getNames().toArray(new String[0]);
        final JComboBox<String> comboBox = new JComboBox<>(choices);
        comboBox.setMaximumSize(comboBox.getPreferredSize());
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(comboBox);

        JButton selectButton = new JButton("Select");
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(selectButton);

        frame.setVisible(true);
    }
}