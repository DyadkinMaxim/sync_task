package client;

import datasource.base.Datasource;
import datasource.base.Param;
import datasource.local.LocalDatasource;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SyncConfig {

    JFrame frame = new JFrame("Set configuration properties:");

    public void init(Datasource targetDatasource) {
        var panel = initUI(targetDatasource);
        var sourcePathField = addSourceUI(panel);
        var targetComponents = addTargetUI(targetDatasource.getConnectionSettings(), panel);
        addSubmitBtn(panel, sourcePathField, targetDatasource, targetComponents);
        addMenuBtn(panel);
    }

    private JPanel initUI(Datasource targetDatasource) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350,
                200 +(30 * targetDatasource.getConnectionSettings().size()));
        frame.setLocation(430, 100);
        frame.setVisible(true);
        frame.setResizable(false);

        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        JLabel targetLabel = new JLabel("Target: ");
        var targetFont = new Font("Courier", Font.BOLD, 14);
        targetLabel.setFont(targetFont);
        targetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(targetLabel);
        return panel;
    }

    private JTextField addSourceUI(JPanel panel) {
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
        sourcePath.setName("sourcePath");
        panel.add(sourcePath);
        return sourcePath;
    }

    private List<JComponent> addTargetUI(List<Param> settings, JPanel panel) {
        List<JComponent> components = new ArrayList<>();
        for (var param : settings) {
            var label = new JLabel(param.getLabelText());
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label);
            var component = param.getUiComponent();
            component.setName(param.getName());
            if (component.getClass() == JTextField.class) {
                component.setSize(new Dimension(300, 20));
                component.setAlignmentX(Component.LEFT_ALIGNMENT);
                //component.setMaximumSize(new Dimension(350, 20));
            }
            panel.add(component);
            components.add(component);
        }
        return components;
    }

    private void addSubmitBtn(JPanel panel, JTextField sourcePath,Datasource targetDatasource,
                              List<JComponent> components) {
        JButton submitBtn = new JButton("Submit");
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(submitBtn);

        submitBtn.addActionListener(new ActionListener() {
            @SuppressWarnings("deprecation")
            public void actionPerformed(ActionEvent e) {
                var sourceDatasource = new LocalDatasource();
                var sourceParams = collectSourcePath(sourcePath);
                var targetParams =
                        collectTargetParams(targetDatasource.getConnectionSettings(), components);
                connectDatasources(sourceDatasource, sourceParams, targetDatasource, targetParams);
                frame.setVisible(false);
                GUIForm.pauseResume.init(sourceDatasource, targetDatasource);

            }
        });
    }

    private void addMenuBtn(JPanel panel) {
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
    }

    private List<Param> collectSourcePath(JTextField sourcePath) {
        var sourceParams = new LocalDatasource().getConnectionSettings();
        Param.getParam(sourceParams, "filePath").setValue(sourcePath.getText());
        return sourceParams;
    }

    private List<Param> collectTargetParams(List<Param> settings, List<JComponent> components) {
        // todo fill settings
        List<Param> configParams = new ArrayList<>();
        for (var component : components) {
            var param = Param.getParam(settings, component.getName());
            String paramValue;
            if (component instanceof JFileChooser) {
                JFileChooser fileChooser = (JFileChooser) component;
                paramValue = fileChooser.getSelectedFile().getAbsolutePath();
            } else {
                JTextField textField = (JTextField) component;
                paramValue = textField.getText();
            }
            param.setValue(paramValue);
            configParams.add(param);
        }
        return configParams;
    }

    private void connectDatasources(
            Datasource sourceDatasource, List<Param> sourceParams,
            Datasource targetDatasource, List<Param> targetParams) {
        try {
            sourceDatasource.connect(sourceParams);
            targetDatasource.connect(targetParams);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}