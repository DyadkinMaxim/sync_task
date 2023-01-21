package client;

import datasource.base.Datasource;
import datasource.base.Param;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class ConnectionUI {

    private JFrame frame = new JFrame("Connecting datasources...");

    public void connectDatasources(
            Datasource sourceDatasource, List<Param> sourceParams,
            Datasource targetDatasource, List<Param> targetParams, SyncConfig syncConfig) {
        SwingWorker<Boolean, Void> sw1 = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                boolean isConnected = false;
                try {
                    frame.getContentPane().removeAll();
                    frame.repaint();
                    frame.pack();
                    var panel = new JPanel();
                    var border = panel.getBorder();
                    Border margin = new EmptyBorder(5, 5, 5, 5);
                    panel.setBorder(new CompoundBorder(border, margin));
                    JProgressBar progressBar = new JProgressBar();
                    progressBar.setIndeterminate(true);

                    panel.add(progressBar);
                    frame.add(panel);
                    frame.setSize(320, 70);
                    frame.setResizable(false);
                    frame.setAlwaysOnTop(true);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                    //Thread.sleep(5000);
                    sourceDatasource.connect(sourceParams);
                    targetDatasource.connect(targetParams);
                    isConnected = true;
                    frame.setVisible(false);
                    JOptionPane.showMessageDialog(null, "Ready to sync!");
                } catch (Exception ex) {
                    frame.setVisible(false);
                    log.error(ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Can't connect to datasources");
                    frame.setVisible(false);
                }
                return isConnected;
            }

            @Override
            protected void done() {
                try {
                    syncConfig.finishConfig(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        sw1.execute();
    }
}
