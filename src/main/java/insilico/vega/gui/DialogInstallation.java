package insilico.vega.gui;

import javax.swing.*;

public class DialogInstallation extends javax.swing.JDialog {

    private javax.swing.JLabel jLabel2;


    public DialogInstallation(){
        this.setTitle("INFO");
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        this.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        this.setResizable(false);
        this.setType(Type.POPUP);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        initComponents();

        this.setSize(400, 150);
        this.setLocationRelativeTo(null);
    }

    private void initComponents() {
        jLabel2 = new javax.swing.JLabel();
        jLabel2.setText("Please wait while installing Conda...");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(60, Short.MAX_VALUE))
        );

        pack();

    }
}
