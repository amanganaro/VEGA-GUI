package insilico.vega.gui;
import javax.swing.*;
import java.awt.*;

public class FrameLoading extends JFrame {

    private JLabel jLabel1;
    private JLabel jLabel2;
    private String mainMessage;

    public FrameLoading(String mainMessage) {
        this.mainMessage = mainMessage;
        initComponents();
    }

    private void initComponents() {

        jLabel1 = new JLabel();
        jLabel2 = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(191, 225, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        setUndecorated(true);


        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setIcon(new ImageIcon(getClass().getResource("/icons/vega_logo_small.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel2.setText(mainMessage);
        jLabel2.setVerticalAlignment(SwingConstants.TOP);
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 454, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                                                .addContainerGap())))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getSize().width;
        int h = getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        setLocation(x, y);
    }

    public void setLabelText(String text) {
        jLabel2.setText(text);
    }

    public JLabel getLabel() {
        return jLabel2;
    }

}
