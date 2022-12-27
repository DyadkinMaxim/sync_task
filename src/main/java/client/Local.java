package client;

import java.awt.SystemColor;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

public class Local extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public Local() {
        setTitle("Local window: ");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 649, 474);
        contentPane = new JPanel();
        contentPane.setBackground(SystemColor.activeCaption);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);


        JFileChooser source = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        source.setDialogTitle("Select source directory: ");
        source.setAcceptAllFileFilterUsed(false);
        int sourceValue = source.showOpenDialog(null);
        if (sourceValue == JFileChooser.APPROVE_OPTION) {
            System.out.println(source.getSelectedFile().getPath());
        }
        contentPane.add(source);

        JFileChooser target = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        target.setDialogTitle("Select target directory: ");
        target.setAcceptAllFileFilterUsed(false);

        int targetValue = target.showOpenDialog(null);
        if (targetValue == JFileChooser.APPROVE_OPTION) {
            System.out.println(target.getSelectedFile().getPath());
        }
        contentPane.add(target);
    }
}