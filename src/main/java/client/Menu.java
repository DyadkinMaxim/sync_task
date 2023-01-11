package client;

import datasource.SimpleDS;
import datasource.base.DatasourceManager;
import datasource.local.LocalDatasource;
import datasource.ssh.SSHDatasource;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class Menu {
    private static JFrame frame = new JFrame("Start menu");

    public static void main(String[] args) {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var panel = new JPanel();
        frame.add(panel);
        var label = new JLabel("Select target type: ");
        panel.add(label, BorderLayout.LINE_START);

        var dsManager = new DatasourceManager();
        dsManager.add(new LocalDatasource());
        dsManager.add(new SSHDatasource());
        dsManager.add(new SimpleDS());
        var targetTypes = dsManager.getNames().toArray(new String[0]);

        var targetDropDown = new JComboBox<>(targetTypes);
        targetDropDown.setMaximumSize(targetDropDown.getPreferredSize());
        panel.add(targetDropDown, BorderLayout.CENTER);

        var selectBtn = new JButton("Select");
        panel.add(selectBtn, BorderLayout.LINE_END);

        frame.setSize(300, 70 + (20 * targetTypes.length));
        frame.setLocation(430, 100);
        frame.setVisible(true);

        selectBtn.addActionListener(new ActionListener() {
            @SuppressWarnings("deprecation")
            public void actionPerformed(ActionEvent e) {
                var targetDatasource =
                        dsManager.getByName(targetDropDown.getSelectedItem().toString());
                frame.setVisible(false);
                GUIForm.syncConfig.init(targetDatasource);

            }
        });
    }

    public void setVisible(boolean isVisible) {
        frame.setVisible(isVisible);
    }
}