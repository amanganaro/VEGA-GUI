package insilico.vega.gui;

import insilico.core.exception.GenericFailureException;
import insilico.core.main;
import insilico.core.model.InsilicoModelInfo;
import insilico.core.model.guide.GuidePDFGenerator;
import insilico.core.model.iInsilicoModel;
import insilico.core.model.iInsilicoModelConsensus;
import insilico.core.model.qmrf.QMRFDocument;
import insilico.core.model.trainingset.TrainingSet;
import insilico.vega.gui.utilities.PDFDocumentLauncher;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class FrameModelInfo extends JFrame {

    /**
     * Creates new form FrameModelInfo from a model
     * @param Model
     */
    public FrameModelInfo(iInsilicoModel Model) {
        initComponents();
    
        final iInsilicoModel CurModel = Model;
        
        jLabelName.setText(CurModel.getInfo().getName());
        jLabelVersion.setText(CurModel.getInfo().getVersion());

        final JFrame Parent = this;

        // Always show PDF guide and TS
        jPanelGuide.setVisible(false);
        jPanelTraining.setVisible(true);

        boolean HasQMRF = !CurModel.getInfo().getQMRF().isEmpty();
        jPanelQMRF.setVisible(false);
        jPanelQMRFDownload.setVisible(HasQMRF);


//        jLabelDownloadGuide.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent evt) {
//                OnClickableLabelMouseOver(evt);
//            }
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                (new OpenGuideWorker(Parent, CurModel.getInfo())).execute();               }
//        });
        
        jLabelDownloadTraining.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                OnClickableLabelMouseOver(evt);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                (new OpenTSWorker(Parent, CurModel, CurModel.getInfo().getKey())).execute();   
            }
        });

        jLabelDownloadLocalQMRF.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                OnClickableLabelMouseOver(evt);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                (new OpenQMRFWorker(Parent, CurModel.getInfo())).execute();
            }
        });
                
    }


    /**
     * Creates new form FrameModelInfo from a consensus model
     * @param Model
     */
    public FrameModelInfo(iInsilicoModelConsensus Model) {
        initComponents();
    
//        final iInsilicoModelConsensus CurModel = Model;
//
//        jLabelName.setText(CurModel.getInfo().getName());
//        jLabelVersion.setText(CurModel.getInfo().getVersion());
//
//        final JFrame Parent = this;
//
//        jLabelDownloadGuide.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent evt) {
//                OnClickableLabelMouseOver(evt);
//            }
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
////                (new OpenPDFWorker(Parent, CurModel.getInfo())).execute();
//            }
//        });
//
//        jLabelDownloadQMRF.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent evt) {
//                OnClickableLabelMouseOver(evt);
//            }
//        });
//
//        boolean HasGuide = CurModel.getInfo().hasGuideURL();
//        boolean HasTS = false; // TS not available for consensus
//        boolean HasQMRF = CurModel.getInfo().hasQMRFLink();
//        boolean HasLocalQMRF = false;
//
//        jPanelGuide.setVisible(HasGuide);
//        jPanelTraining.setVisible(HasTS);
//        jPanelQMRF.setVisible(HasQMRF);
//        jPanelQMRFDownload.setVisible(HasLocalQMRF);
        
    }
    
    
    private void OnClickableLabelMouseOver(MouseEvent evt) {
        JLabel src = (JLabel) evt.getSource();
        src.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
    
    
//    private class OpenGuideWorker extends SwingWorker<Object, Object> {
//        private final JFrame Parent;
//        private final InsilicoModelInfo CurModelInfo;
//
//        public OpenGuideWorker(JFrame Parent, InsilicoModelInfo ModelInfo) {
//            this.Parent = Parent;
//            this.CurModelInfo = ModelInfo;
//        }
//
//        @Override
//        protected Object doInBackground() throws Exception {
//            Parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//            try {
//                String ModelName = CurModelInfo.getKey();
//                GuidePDFGenerator pdfGen = new GuidePDFGenerator();
//                byte[] GuideByte = pdfGen.CreateGuide(CurModelInfo);
//                PDFDocumentLauncher.Open(GuideByte, ModelName);
//            } catch (GenericFailureException ex) {
//                JOptionPane.showMessageDialog(Parent,
//                    "Unable to open file.\n(Error: " + ex.getMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);
//            } finally {
//                Parent.setCursor(Cursor.getDefaultCursor());
//            }
//            return null;
//        }
//    }


    private class OpenQMRFWorker extends SwingWorker<Object, Object> {
        private final JFrame Parent;
        private final InsilicoModelInfo CurModelInfo;

        public OpenQMRFWorker(JFrame Parent, InsilicoModelInfo ModelInfo) {
            this.Parent = Parent;
            this.CurModelInfo = ModelInfo;
        }

        @Override
        protected Object doInBackground() throws Exception {
            Parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                String ModelName = CurModelInfo.getKey();
                URL u = main.class.getResource(CurModelInfo.getQMRF());
                QMRFDocument doc = new QMRFDocument(u);
                byte[] bos = doc.CreatePDF();
                PDFDocumentLauncher.Open(bos, "QMRF_" + ModelName);
            } catch (GenericFailureException ex) {
                JOptionPane.showMessageDialog(Parent,
                        "Unable to open file.\n(Error: " + ex.getMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                Parent.setCursor(Cursor.getDefaultCursor());
            }
            return null;
        }
    }


    private class OpenTSWorker extends SwingWorker<Object, Object> {
        private final JFrame Parent;
        private final iInsilicoModel Model;
        private final String ModelName;
        
        public OpenTSWorker(JFrame Parent, iInsilicoModel Model, String ModelName) {
            this.Parent = Parent;
            this.Model = Model;
            this.ModelName = ModelName;
        }
        
        @Override
        protected Object doInBackground() throws Exception {
            Parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                // Gets output directory
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setDialogTitle("Save text file");
                fc.setSelectedFile(new File("dataset_" + ModelName + ".txt"));
                int res = fc.showSaveDialog(Parent);

                // Saves doc
                if (!((res == JFileChooser.CANCEL_OPTION) || (res == 
                       JFileChooser.ERROR_OPTION))) {
                    PrintWriter out = new PrintWriter(fc.getSelectedFile());
                    ((TrainingSet)(Model.GetTrainingSet())).Print(out, false);
                    out.close();
                    Model.Purge();
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Parent,
                    "Unable to save file.\n(Error: " + ex.getMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);            
            } finally {
                Parent.setCursor(Cursor.getDefaultCursor());
            }               
            return null;
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
        jLabel1 = new JLabel();
        jLabelName = new JLabel();
        jLabel2 = new JLabel();
        jLabelVersion = new JLabel();
        jLabel3 = new JLabel();
        jPanelTraining = new javax.swing.JPanel();
        jLabelTraining = new JLabel();
        jLabelDownloadTraining = new JLabel();
        jPanelGuide = new javax.swing.JPanel();
        jLabelGuide = new JLabel();
        jLabelDownloadGuide = new JLabel();
        jPanelQMRF = new javax.swing.JPanel();
        jLabelQMRF = new JLabel();
        jLabelDownloadQMRF = new JLabel();
        jPanelQMRFDownload = new javax.swing.JPanel();
        jLabelLocalQMRF = new JLabel();
        jLabelDownloadLocalQMRF = new JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Information on the model");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel1.setText("Model:");

        jLabelName.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabelName.setText("jLabel1");

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel2.setText("Version:");

        jLabelVersion.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabelVersion.setText("jLabel1");

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel3.setText("Available documentation:");

        jPanelTraining.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabelTraining.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabelTraining.setText("Training set (plain text with SMILES)");

        jLabelDownloadTraining.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save-file-16.png"))); // NOI18N

        javax.swing.GroupLayout jPanelTrainingLayout = new javax.swing.GroupLayout(jPanelTraining);
        jPanelTraining.setLayout(jPanelTrainingLayout);
        jPanelTrainingLayout.setHorizontalGroup(
            jPanelTrainingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTrainingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTraining)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelDownloadTraining)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelTrainingLayout.setVerticalGroup(
            jPanelTrainingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTrainingLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelTrainingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabelDownloadTraining)
                    .addComponent(jLabelTraining))
                .addContainerGap())
        );

        jPanelGuide.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabelGuide.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabelGuide.setText("Model's guide (PDF)");

        jLabelDownloadGuide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save-file-16.png"))); // NOI18N

        javax.swing.GroupLayout jPanelGuideLayout = new javax.swing.GroupLayout(jPanelGuide);
        jPanelGuide.setLayout(jPanelGuideLayout);
        jPanelGuideLayout.setHorizontalGroup(
            jPanelGuideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGuideLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelGuide)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelDownloadGuide)
                .addContainerGap(344, Short.MAX_VALUE))
        );
        jPanelGuideLayout.setVerticalGroup(
            jPanelGuideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGuideLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelGuideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabelDownloadGuide)
                    .addComponent(jLabelGuide))
                .addContainerGap())
        );

        jPanelQMRF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabelQMRF.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabelQMRF.setText("QMRF document (external link to JRC database)");

        jLabelDownloadQMRF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/external-link-16.png"))); // NOI18N

        javax.swing.GroupLayout jPanelQMRFLayout = new javax.swing.GroupLayout(jPanelQMRF);
        jPanelQMRF.setLayout(jPanelQMRFLayout);
        jPanelQMRFLayout.setHorizontalGroup(
            jPanelQMRFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelQMRFLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelQMRF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelDownloadQMRF)
                .addContainerGap(183, Short.MAX_VALUE))
        );
        jPanelQMRFLayout.setVerticalGroup(
            jPanelQMRFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelQMRFLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelQMRFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabelDownloadQMRF)
                    .addComponent(jLabelQMRF))
                .addContainerGap())
        );

        jPanelQMRFDownload.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabelLocalQMRF.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabelLocalQMRF.setText("QMRF document");

        jLabelDownloadLocalQMRF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save-file-16.png"))); // NOI18N

        javax.swing.GroupLayout jPanelQMRFDownloadLayout = new javax.swing.GroupLayout(jPanelQMRFDownload);
        jPanelQMRFDownload.setLayout(jPanelQMRFDownloadLayout);
        jPanelQMRFDownloadLayout.setHorizontalGroup(
            jPanelQMRFDownloadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelQMRFDownloadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelLocalQMRF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelDownloadLocalQMRF)
                .addContainerGap(365, Short.MAX_VALUE))
        );
        jPanelQMRFDownloadLayout.setVerticalGroup(
            jPanelQMRFDownloadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelQMRFDownloadLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelQMRFDownloadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabelDownloadLocalQMRF)
                    .addComponent(jLabelLocalQMRF))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelTraining, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelName))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelVersion))
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanelGuide, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelQMRF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelQMRFDownload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabelName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabelVersion))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTraining, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelGuide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelQMRF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelQMRFDownload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
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
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabelDownloadGuide;
    private JLabel jLabelDownloadLocalQMRF;
    private JLabel jLabelDownloadQMRF;
    private JLabel jLabelDownloadTraining;
    private JLabel jLabelGuide;
    private JLabel jLabelLocalQMRF;
    private JLabel jLabelName;
    private JLabel jLabelQMRF;
    private JLabel jLabelTraining;
    private JLabel jLabelVersion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelGuide;
    private javax.swing.JPanel jPanelQMRF;
    private javax.swing.JPanel jPanelQMRFDownload;
    private javax.swing.JPanel jPanelTraining;
    // End of variables declaration//GEN-END:variables
}
