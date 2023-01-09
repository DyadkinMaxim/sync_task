package client;

import datasource.SimpleDS;
import datasource.base.DatasourceManager;
import datasource.base.Param;
import datasource.local.LocalDatasource;
import datasource.ssh.SSHDatasource;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SyncConfig {

    public void init(String targetType) {
        var manager = new DatasourceManager();
        manager.add(new LocalDatasource());
        manager.add(new SSHDatasource());
        manager.add(new SimpleDS());

        JFrame frame = new JFrame("Set configuration properties:");
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        JLabel sourceLabel = new JLabel("Source: ");
        var sourceFont = new Font("Courier", Font.BOLD, 14);
        sourceLabel.setFont(sourceFont);
        sourceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sourceLabel);

        var sourcePathLabel = new JLabel("Path to source directory: ");
        sourcePathLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        //sourcePathLabel.setSize(150,40);
        panel.add(sourcePathLabel);
        var sourcePath = new JTextField();
        //sourcePath.setMaximumSize(new Dimension(350, 20));
        sourcePath.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sourcePath);

        JLabel targetLabel = new JLabel("Target: ");
        var targetFont = new Font("Courier", Font.BOLD, 14);
        targetLabel.setFont(targetFont);
        targetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(targetLabel);

        var target = manager.getByName(targetType);
        var settings = target.getConnectionSettings();
        for (Param param : settings) {
            generateComponent(param, panel);
        }

        JButton syncBtn = new JButton("Submit");
        syncBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(syncBtn);

        syncBtn.addActionListener(new ActionListener() {
            @SuppressWarnings("deprecation")
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                GUIForm.pauseResume.init();

            }
        });

        JButton backBtn = new JButton("Menu");
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(backBtn);

        backBtn.addActionListener(new ActionListener() {
            @SuppressWarnings("deprecation")
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                GUIForm.menu.setVisible(true);

            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 200 + (30 * settings.size()));
        frame.setLocation(430, 100);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private static void generateComponent(Param param, JPanel panel) {
        var label = new JLabel(param.getLabelText());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        var component = param.getUiComponent();
        if (component.getClass() == JTextField.class) {
            component.setSize(new Dimension(300, 20));
            component.setAlignmentX(Component.LEFT_ALIGNMENT);
            //component.setMaximumSize(new Dimension(350, 20));
        }
        panel.add(component);
    }
}