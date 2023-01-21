package client;

import datasource.base.DatasourceManager;
import datasource.local.LocalDatasource;
import datasource.ssh.SSHDatasource;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class Menu {
    private static JFrame frame = new JFrame("Start menu");
    private static DatasourceManager dsManager = new DatasourceManager();

    public static void main(String[] args) {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        var panel = new JPanel();
        frame.add(panel);
        var label = new JLabel("Select target type: ");
        panel.add(label, BorderLayout.LINE_START);

        dsManager.add(new LocalDatasource());
        dsManager.add(new SSHDatasource());
        var targetTypes = dsManager.getNames().toArray(new String[0]);

        var targetDropDown = new JComboBox<>(targetTypes);
        targetDropDown.setMaximumSize(targetDropDown.getPreferredSize());
        panel.add(targetDropDown, BorderLayout.CENTER);

        var selectBtn = new JButton("Select");
        panel.add(selectBtn, BorderLayout.LINE_END);

        frame.setSize(300, 70 + (20 * targetTypes.length));
        var dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);

        selectBtn.addActionListener(e -> {
            var targetDatasource =
                    dsManager.getByName(targetDropDown.getSelectedItem().toString());
            frame.setVisible(false);
            GUIForm.syncConfig.init(targetDatasource);

        });
    }

    public void setVisible(boolean isVisible) {
        frame.setVisible(isVisible);
    }
}