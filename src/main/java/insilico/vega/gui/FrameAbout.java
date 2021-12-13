package insilico.vega.gui;

import insilico.core.exception.GenericFailureException;
import insilico.core.exception.InitFailureException;
import insilico.core.version.InsilicoInfo;
import insilico.vega.gui.resources.VegaVersion;
import insilico.vega.gui.utilities.PDFDocumentLauncher;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class FrameAbout extends JFrame {

    /**
     * Creates new form FrameAbout
     */
    public FrameAbout() {
        initComponents();
        
        jLabelVegaVersion.setText(VegaVersion.Version);
        jLabelBuildDate.setText("(build date: " + VegaVersion.BuildDate + ")");
        try {
            InsilicoInfo info = new InsilicoInfo();
            jLabelCoreVersion.setText(info.getVersion());
        } catch (InitFailureException ex) {
            jLabelCoreVersion.setText("N/A");
        }
        String lib = "";
        for (String s : VegaVersion.Libraries) {
            if (!lib.isEmpty()) lib += System.getProperty("line.separator");
            lib += " " + s;
        }
        jTextAreaLibraries.setText(lib);
        
        final JFrame Parent = this;
        jLabelDownloadGuide.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                OnClickableLabelMouseOver(evt);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                (new OpenPDFGuideWorker(Parent)).execute();   
            }
        });
        
        jLabelVegaLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                OnClickableLabelMouseOver(evt);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                OpenExternalWebBrowser("http://www.vega-qsar.eu");
            }
        });
        
        jLabelIRFMN.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                OnClickableLabelMouseOver(evt);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                OpenExternalWebBrowser("http://www.marionegri.it");
            }
        });
        
        jLabelKODE.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                OnClickableLabelMouseOver(evt);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                OpenExternalWebBrowser("https://chm.kode-solutions.net");
            }
        });
        
    }

    
    private void OnClickableLabelMouseOver(MouseEvent evt) {
        JLabel src = (JLabel) evt.getSource();
        src.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    
    private class OpenPDFGuideWorker extends SwingWorker<Object, Object> {
        private final JFrame Parent;
        private final String UrlPDF;
        private final String ModelName;
        
        public OpenPDFGuideWorker(JFrame Parent) {
            this.Parent = Parent;
            this.UrlPDF = VegaVersion.GUIDE_URL;
            this.ModelName = "VEGA_user_guide";
        }
        
        @Override
        protected Object doInBackground() throws Exception {
            Parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                PDFDocumentLauncher.Open(UrlPDF, ModelName);
            } catch (GenericFailureException ex) {
                JOptionPane.showMessageDialog(Parent,
                    "Unable to open file.\n(Error: " + ex.getMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);            
            } finally {
                Parent.setCursor(Cursor.getDefaultCursor());
            }               
            return null;
        }
    }    
    
    
     
    private void OpenExternalWebBrowser(String URL) {
        
         if( !java.awt.Desktop.isDesktopSupported() ) {
            JOptionPane.showMessageDialog(this,
                    "Desktop is not supported - can not open browser", "Error", JOptionPane.ERROR_MESSAGE);                       
            return;
         }

         java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

         if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
            JOptionPane.showMessageDialog(this,
                    "Desktop doesn't support the browse action - can not open browser", "Error", JOptionPane.ERROR_MESSAGE);                       
            return;
         }

        try {
            java.net.URI uri = new java.net.URI(URL);
            desktop.browse( uri );
        } catch ( IOException | URISyntaxException e ) {
            JOptionPane.showMessageDialog(this,
                    "Unable to browse webiste - " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);                       
        }
     }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabelVegaVersion = new JLabel();
        jLabelCoreVersion = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        jLabelDownloadGuide = new JLabel();
        jLabelBuildDate = new JLabel();
        jLabel9 = new JLabel();
        jLabelVegaLink = new JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new JLabel();
        jLabel8 = new JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaLibraries = new javax.swing.JTextArea();
        jLabelIRFMN = new JLabel();
        jLabelKODE = new JLabel();

        setTitle("About VEGA");
        setMinimumSize(new java.awt.Dimension(520, 460));
        setPreferredSize(new java.awt.Dimension(520, 460));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 129, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 76, 125)));

        jLabel4.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel4.setText("Version:");

        jLabel5.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel5.setText("Calculation core version:");

        jLabelVegaVersion.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabelVegaVersion.setText("-");

        jLabelCoreVersion.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabelCoreVersion.setText("-");

        jLabel6.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel6.setText("The user's guide is available (PDF document)");

        jLabel7.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel7.setText("The application is released under the GNU GPL-3 license");

        jLabelDownloadGuide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save-file-16.png"))); // NOI18N

        jLabelBuildDate.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabelBuildDate.setText("-");

        jLabel9.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel9.setText("Visit the project website:");

        jLabelVegaLink.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabelVegaLink.setForeground(new java.awt.Color(51, 102, 255));
        jLabelVegaLink.setText("www.vega-qsar.eu");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelVegaVersion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelBuildDate))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelDownloadGuide))
                    .addComponent(jLabel7)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelCoreVersion))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelVegaLink)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabelVegaVersion)
                    .addComponent(jLabelBuildDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabelCoreVersion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabelDownloadGuide))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabelVegaLink))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/vega_logo_small.png"))); // NOI18N
        jPanel4.add(jLabel3, new java.awt.GridBagConstraints());

        jLabel8.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel8.setText("VEGA uses the following open source libraries:");

        jTextAreaLibraries.setEditable(false);
        jTextAreaLibraries.setColumns(20);
        jTextAreaLibraries.setRows(1);
        jScrollPane1.setViewportView(jTextAreaLibraries);

        jLabelIRFMN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/logoirfmn.jpg"))); // NOI18N

        jLabelKODE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/logo_chm.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelIRFMN)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelKODE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelKODE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelIRFMN, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JLabel jLabelBuildDate;
    private JLabel jLabelCoreVersion;
    private JLabel jLabelDownloadGuide;
    private JLabel jLabelIRFMN;
    private JLabel jLabelKODE;
    private JLabel jLabelVegaLink;
    private JLabel jLabelVegaVersion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaLibraries;
    // End of variables declaration//GEN-END:variables
}
