package client;

import datasource.SimpleDS;
import datasource.base.Datasource;
import datasource.base.DatasourceManager;
import datasource.base.Param;
import datasource.local.LocalDatasource;
import datasource.ssh.SSHDatasource;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Configuration {

    public static void main(String[] args) {
        var manager = new DatasourceManager();
        manager.add(new LocalDatasource());
        manager.add(new SSHDatasource());
        manager.add(new SimpleDS());

        JFrame frame = new JFrame("Configuration");
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        JLabel label = new JLabel("Set configuration properties: ");
        panel.add(label);
        JLabel sourceLabel = new JLabel("Source: ");
        var sourceFont = new Font("Courier", Font.BOLD,14);
        sourceLabel.setFont(sourceFont);
        panel.add(sourceLabel);

        var sourcePathLabel = new JLabel("Path to source directory: ");
        sourcePathLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        //sourcePathLabel.setSize(150,40);
        panel.add(sourcePathLabel);
        var sourcePath = new JTextField();
        sourcePath.setMaximumSize(new Dimension(350,20));
        panel.add(sourcePath);

        JLabel targetLabel = new JLabel("Target: ");
        var targetFont = new Font("Courier", Font.BOLD,14);
        targetLabel.setFont(targetFont);
        panel.add(targetLabel);

        var target = manager.getByName("SSH");
        var settings = target.getConnectionSettings();
        for (Param param : settings) {
            generateComponent(param, panel);
        }

        JButton syncButton = new JButton("Submit");
        syncButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(syncButton);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320, 200 + (30 * settings.size()));
        frame.setLocation(430, 100);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private static void generateComponent(Param param, JPanel panel) {
        var label = new JLabel(param.getLabelText());
        panel.add(label);
        var component = param.getUiComponent();
        if(component.getClass() == JTextField.class) {
            component.setSize(new Dimension(300,20));
            component.setMaximumSize(new Dimension(350,20));
        }
        panel.add(component);
    }
}