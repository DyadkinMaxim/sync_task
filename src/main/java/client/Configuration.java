package client;

import datasource.Datasource;
import datasource.DatasourceManager;
import datasource.Param;
import java.awt.Component;
import java.awt.SystemColor;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

public class Configuration {
//
//    public void method(Datasource datasource) {
//        JFrame frame = new JFrame("Configuration");
//
//        frame.setVisible(true);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(300, 200);
//        frame.setLocation(430, 100);
//
//        var panel = new JPanel();
//        panel.setLayout(new
//
//                BoxLayout(panel, BoxLayout.Y_AXIS));
//        frame.add(panel);
//
//        JLabel label = new JLabel("Set configuration properties: ");
//        label.setAlignmentX(Component.CENTER_ALIGNMENT);
//        panel.add(label);
//
//        for (Param param : datasource.getConnectionSettings()) {
//            generateComponent(Param param);
//        }
//
//
//        JButton syncButton = new JButton("Syncronize!");
//        syncButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//        panel.add(syncButton);
//
//        frame.setVisible(true);
//    }
//
//    private Component generateComponent(Param param) {
//        String name = param.getName();
//        Component component = param.getUiComponent();
//
//
//    }
}