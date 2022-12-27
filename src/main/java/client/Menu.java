package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class Menu {

    public JFrame frame;

    public Menu() {initialize();}

    private void initialize() {
        frame = new JFrame("Start window");
        JButton local = new JButton("Local");
        local.setBounds(100, 100, 95, 30);

        JButton remote = new JButton("Remote");
        remote.setBounds(100, 200, 95, 30);

        frame.add(local);
        frame.add(remote);
        frame.setSize(400, 400);
        frame.setLayout(null);
        frame.setVisible(true);
        local.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

//                if(!GUIForm.local.frame.isVisible())
//                {
//                    frame.setVisible(false);
//                    GUIForm.local.frame.setVisible(true);
//                }
//                else
//                {
//                    JOptionPane.showMessageDialog(frame.getComponent(0), "Already Opened", "Warning", 0);
//                }
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}