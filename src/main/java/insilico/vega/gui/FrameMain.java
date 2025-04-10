package insilico.vega.gui;


import insilico.core.python.CdddDescriptors;
import insilico.vega.gui.models.VegaModelsWrapper;
import insilico.core.exception.GenericFailureException;
import insilico.vega.gui.utilities.PythonSetup;
import insilico.vega.gui.utilities.UpdatesReader;
import insilico.core.exception.InitFailureException;
import insilico.core.exception.MoleculeConversionException;
import insilico.core.model.InsilicoModel;
import insilico.core.model.iInsilicoModel;
import insilico.core.model.iInsilicoModelConsensus;
import insilico.core.model.report.pdf.ReportPDFMultiple;
import insilico.core.model.report.pdf.ReportPDFSingle;
import insilico.core.model.report.txt.ReportTXTConsensusSingle;
import insilico.core.model.report.txt.ReportTXTMultiple;
import insilico.core.model.report.txt.ReportTXTSingle;
import insilico.core.model.runner.InsilicoModelConsensusWrapper;
import insilico.core.model.runner.InsilicoModelRunnerByMolecule;
import insilico.core.model.runner.InsilicoModelWrapper;
import insilico.core.model.runner.iInsilicoModelRunnerMessenger;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.MDLTagReader;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.core.molecule.conversion.file.MoleculeFileSDF;
import insilico.core.molecule.conversion.file.MoleculeFileSmiles;
import insilico.core.tools.utils.GeneralUtilities;
//import insilico.core.tools.GeneralUtilities;
//import insilico.core.tools.logger.InsilicoLogger;
import insilico.vega.gui.resources.VegaVersion;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

        

/**
 * Standalone application for VEGA
 * 
 * @author Alexandros Spiliopoulos and Alberto Manganaro
 */

public class FrameMain extends JFrame {

    private static final Logger LOGGER = LogManager.getLogger();

    // Molecules list
    private ArrayList<InsilicoMolecule> DataSet;
    
    // Available models 
    private VegaModelsWrapper Models;
    
    // Selected models
    protected ArrayList<InsilicoModel> CurModels = new ArrayList<>();
    protected ArrayList<iInsilicoModelConsensus> CurConsModels = new ArrayList<>();
    
    // Models runner and container
    protected InsilicoModelRunnerByMolecule Runner;
    
    // Tab for models
    private PanelModelList ModelsTab;
    
    // Dialogs cache
    private File LastMolFile;
    private File LastDir;
    
    // Time for calculation
    private long startTime;
    private String ElapsedTime;
    
    private final FrameAbout AboutFrame;
    private final PanelGlass GlassPanel;
    
    private final PanelMolViewer PanelMoleculeViewer = new PanelMolViewer();
    private final Object[][] arg1 = new Object[8][7];
    private Tab_Changer_Step1 tc1;
    private DefaultTableModel TableModel;
    private JTableHeader TableHeader;

    public JPopupMenu popup;
    
    private UpdatesReader Updates;
    
    private static PythonSetup pySup;

    /**
     *  Constructor of the class
     */ 
    public FrameMain(JFrame FrameLoader) {

        initComponents();

        // Set glass panel
        GlassPanel = new PanelGlass();
        this.setGlassPane(GlassPanel);
        
        // Create about frame
        AboutFrame = new FrameAbout();
        AboutFrame.setVisible(false);
        
        LastMolFile = null;
        LastDir = null;

        // Start logger
//        try {
//            InsilicoLogger.InitLogger();
//        } catch (InitFailureException ex) {
//            JOptionPane.showMessageDialog(null, "Warning: unable to start logger");
//        }
        
        // Builds molecules list
        DataSet = new ArrayList<>();


        class InitializeModels extends SwingWorker<Object, Object> {

            final JFrame frameReference;

            InitializeModels(JFrame frameReference) {
                this.frameReference = frameReference;
            }

            // Builds models list
            @Override
            protected Object doInBackground() throws Exception {
                try{
                    iInsilicoModelRunnerMessenger loadingMessenger = new iInsilicoModelRunnerMessenger() {
                        @Override
                        public void SendMessage(String msg) {
                            ((FrameLoading) FrameLoader).setLabelText(msg);
                        }

                        @Override
                        public void UpdateProgress() {
                            return;
                        }
                    };
                    Models = new VegaModelsWrapper(loadingMessenger);

                    //FOR FUTURE OTHER DESCRIPTORS MAKE AN AUTOMATED IMPLEMENTATION AS MODELS
                    if(!VegaVersion.UNINSTALL_VEGA)
                        loadingMessenger.SendMessage("Checking CDDD descriptors environment");
                    CdddDescriptors cdddDescriptors = new CdddDescriptors(List.of("CCCCC"), VegaVersion.UNINSTALL_VEGA, loadingMessenger);
                    cdddDescriptors.dispose();

                    //clean conda installation
                    pySup.cleanConda();

                    if(VegaVersion.UNINSTALL_VEGA){
                        cdddDescriptors.removeCondaEnv();
                        boolean uninstallResult = pySup.removeCondaInstallation();
                        LogManager.shutdown();
                        pySup.removeLogFolder();
                        if(uninstallResult) {
                            JOptionPane.showMessageDialog(FrameLoader,
                                    "All additional files have been removed.\r\n" +
                                            "It is now possible to delete the folder containing the VEGA binaries.");

                        }else{
                            JOptionPane.showMessageDialog(FrameLoader,
                                    "Something went wrong during the uninstallation.\r\n" +
                                            "Try again, if the problem persists, cancel manually the conda installation");
                        }

                        FrameLoader.dispatchEvent(new WindowEvent(FrameLoader, WindowEvent.WINDOW_CLOSING));
                        return false;
                    }


                }catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Fatal error: unable to initialize models.\nReported error: " + ex.getMessage());
                    frameReference.dispose();
                }

                return null;
            }

            //once it done run this
            @Override
            protected void done() {
                // Build the models tab
                ModelsTab = new PanelModelList(frameReference, Models);
                Step2.add(ModelsTab, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 135-48, 595, 340));
                //ModelsTab.gotoTox();

                // Sets help tooltip
                String AboutTxt = "<html> " + VegaVersion.AppName + " <br>";
                AboutTxt += " Version " + VegaVersion.Version + "<br><br>";
                AboutTxt += " Click to show more info </html>";
                Help_Label.setToolTipText(AboutTxt);

                // Sets frame title
                frameReference.setTitle(VegaVersion.AppName + " - version " +
                        VegaVersion.VersionMajor + "." + VegaVersion.VersionMinor +
                        "." + VegaVersion.VersionRevision);

                // Init the form
                getContentPane().setBackground(Color.white);
                setSize(800,600);

                Step1.setVisible(true);
                Step2.setVisible(false);
                Step3.setVisible(false);

                Step1_Over.setVisible(true);
                Step1_Label.setVisible(false);

                Progress_Bar.setVisible(false);

                PDF_Panel1.setVisible(false);
                PDF_Panel2.setVisible(false);
                PDF_Panel3.setVisible(false);
                CSV_Panel1.setVisible(false);
                CSV_Panel2.setVisible(false);

                // Center the form
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                int w = frameReference.getSize().width;
                int h = frameReference.getSize().height;
                int x = (dim.width-w)/2;
                int y = (dim.height-h)/2;
                frameReference.setLocation(x, y);

                //// TABLE ARGUMENTS TABCHANGER STEP1

                arg1[0][0]= Step1;
                arg1[0][1]= Step2;
                arg1[0][2]= Step3;
                arg1[0][3]= SideBar;
                arg1[0][4]= Progress_Bar;
                arg1[1][0]= Step1_Over;
                arg1[1][1]= Step2_Over;
                arg1[1][2]= Step3_Over;
                arg1[1][3]= Predict_Icon;
                arg1[2][0]= Step1_Label;
                arg1[2][1]= Step2_Label;
                arg1[2][2]= Step3_Label;
                arg1[3][0]= Header_Img1;
                arg1[3][1]= Header_Img2;
                arg1[3][2]= Header_Img3;
                arg1[4][0]= Cancel_Btn1;
                arg1[4][1]= ProgressBar;
                arg1[4][2]= Cancel_Lbl;

                tc1 = new Tab_Changer_Step1(arg1);

                //
                Updates = new UpdatesReader(frameReference);
                Updates.start();

                Marvin_Panel.add(PanelMoleculeViewer);

                FrameLoader.setVisible(false);
                frameReference.setVisible(true);
            }
        }

        try {
            InitializeModels im = new InitializeModels(this);
            im.execute();
        }catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Fatal error: unable to start Vega.\nReported error: " + ex.getMessage());
            dispose();
        }
    }

    
    
////// IMPORTING of molecules //////////////////////////////////////////////////    
     
    
    /**
     *  Import a single SMILES string
     *  the string is read from Load_Textfield component
     */
    private void LoadSingleSMILES() {                                               

        // Inner class for running import inside a SwingWorker thread
        class SWLoadSMILES extends SwingWorker<Object, Object> {

            @Override
            protected Object doInBackground() throws Exception {

                Step1.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                int rows=TableModel.getRowCount();
            
                try {
                    String MolStr = Load_Textfield.getText();
                    InsilicoMolecule mol = SmilesMolecule.Convert(MolStr);
                    mol.SetId("Molecule " + rows);
                   
                    if (mol.IsValid()) { 
                        DataSet.add(mol);
                        TableModel.insertRow(rows, new Object[]{""+mol.GetId(),mol.GetSMILES()});
                        Load_Textfield.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, 
                                "Error while reading SMILES. Please check the given structure.\n(Reported error: " +
                                mol.GetErrors().GetMessages() + ")");
                    }
                } catch (Throwable e) {
                    JOptionPane.showMessageDialog(null, 
                            "Error while importing SMILES.\n(Reported error: " + 
                            e.getMessage());
                }

                Step1.setCursor(Cursor.getDefaultCursor());
                
                return null;
            }

        }
        
        (new SWLoadSMILES()).execute();
     }         

    
    
    /**
     * Load a molecule file (SMILES or SDF format)
     */    
    private void LoadFile() {  
         
        final int FileTypeUNKNOWN = 0;
        final int FileTypeSMILES = 1;
        final int FileTypeSDF = 2;
         
        // Filters for file dialog

        class SMILESFilter extends FileFilter {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                String ext = null;
                String s = f.getName();
                int i = s.lastIndexOf('.');
                if (i > 0 &&  i < s.length() - 1) 
                    ext = s.substring(i+1).toLowerCase();
                
                if (ext != null) {
                    if (ext.equals("smi") || ext.equals("txt")) 
                        return true;
                    else 
                        return false;
                }

                return false;
            }

            @Override
            public String getDescription() {
                return "SMILES files (.smi, .txt)";
            }
        }        
        
        class SDFFilter extends FileFilter {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                String ext = null;
                String s = f.getName();
                int i = s.lastIndexOf('.');
                if (i > 0 &&  i < s.length() - 1) 
                    ext = s.substring(i+1).toLowerCase();
                
                if (ext != null) {
                    if (ext.equals("sdf") || ext.equals("sd")) 
                        return true;
                    else 
                        return false;
                }

                return false;
            }

            @Override
            public String getDescription() {
                return "SDF Multiple Mol file (.sdf, .sd)";
            }
        }     

        
        // Shows file dialogs and let user choose file
        
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new SMILESFilter());
        fc.addChoosableFileFilter(new SDFFilter());
        fc.setDialogTitle("Select molecule file");
        if (LastMolFile != null)
            try {
                fc.setSelectedFile(LastMolFile);
            } catch (Throwable e) { /* do nothing */ }
        int res = fc.showOpenDialog(this);
        if ((res == JFileChooser.CANCEL_OPTION) || (res == JFileChooser.ERROR_OPTION))
            return;
        final File selFile = fc.getSelectedFile();
        LastMolFile = selFile;    
        
        
        // Checks type of the chosen file
        
        int CurFileType = FileTypeUNKNOWN;
        String ext = null;
        String s = selFile.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) 
            ext = s.substring(i+1).toLowerCase();
        if (ext != null) {
            if (ext.equals("smi") || ext.equals("txt")) 
                CurFileType = FileTypeSMILES;
            if (ext.equals("sdf") || ext.equals("sd")) 
                CurFileType = FileTypeSDF;
        }
        final int selFileType = CurFileType;

        
        // Loads file
      
        class SWLoadMolFile extends SwingWorker<Object, Object> {
            
            private boolean Error = false;
            private int CurLoadedMolecules = 0;
            public Frame parent;
                
            
            @Override
            protected Object doInBackground() throws Exception {
                
                // INSERT DISABLE OF FORM HERE
                
                Error = false;
                
                try {
                    
                    CurLoadedMolecules = 0;
                    
                    if (selFileType == FileTypeSMILES) {
                        
                        // For SMILES

                        int fSMILES = -1, fCAS = -1, fId = -1;
                        
                        // Check if files contains more than one column
                        int nFields = -1;

                        BufferedReader br = new BufferedReader(new FileReader(selFile));
                        String CurLine;
                        int idx = 1;
                        while ( ((CurLine = br.readLine())!=null) && (idx<50) ) {
                            idx++;
                            if ( (CurLine.equalsIgnoreCase("")) || (CurLine.equalsIgnoreCase("\n")))
                                continue;
                            String s = GeneralUtilities.TrimString(CurLine);
                            String[] ss = s.split("\t");

                            if (nFields == -1)
                                nFields = ss.length;
                            else {
                                if (nFields != ss.length) {
                                    throw new MoleculeConversionException("File contains unsequal number of fields in some rows");                                        
                                }
                            }
                        }
                        br.close();
                        
                        if (nFields > 1) {
                            DialogSMILES dlgSMILES = new DialogSMILES(parent, true, selFile, nFields);
                            dlgSMILES.setLocationRelativeTo(parent);
                            dlgSMILES.setVisible(true);
                            if (!dlgSMILES.Error) {
                                fSMILES = dlgSMILES.fieldSMILES;
                                fCAS = dlgSMILES.fieldCAS;
                                fId = dlgSMILES.fieldId;
                            }
                        }
                        
                        MoleculeFileSmiles SMIReader = new MoleculeFileSmiles();
                        SMIReader.setCASField(fCAS);
                        SMIReader.setIdField(fId);
                        SMIReader.setSmilesField(fSMILES);
                        SMIReader.OpenFile(selFile.getAbsolutePath());
                        DataSet = SMIReader.ReadAll();
                        SMIReader.CloseFile();
                        
                    } else if (selFileType == FileTypeSDF) {
                        
                        // for SDF
                        
                        String tagId = null, tagCAS = null;
                        
                        MDLTagReader tags = new MDLTagReader();
                        ArrayList<String> foundTags = tags.SearchTags(selFile);
                        if (foundTags.size()>0) {
                            DialogSDF dlgSDF = new DialogSDF(parent, true, foundTags);
                            dlgSDF.setLocationRelativeTo(parent);
                            dlgSDF.setVisible(true);
                            tagCAS = dlgSDF.tagCAS;
                            tagId = dlgSDF.tagId;
                        }
                        
                        MoleculeFileSDF SDFReader = new MoleculeFileSDF();
                        SDFReader.setCASTag(tagCAS);
                        SDFReader.setIdTag(tagId);
                        SDFReader.OpenFile(selFile.getAbsolutePath());
                        DataSet = SDFReader.ReadAll();
                        SDFReader.CloseFile();
                        
                    }
                    
                    
                    if (!DataSet.isEmpty()) 
                        for (int i=0; i<DataSet.size(); i++){
                            int rows = TableModel.getRowCount();
                            TableModel.insertRow(rows, new Object[]{DataSet.get(i).GetId(),DataSet.get(i).GetSMILES()});
                            CurLoadedMolecules++;
                        }
                    
                } catch (Throwable e) {
                    JOptionPane.showMessageDialog(null, 
                            "Error while importing File.\n(Reported error: " + 
                            e.getMessage());
                    Error = true;
                }

                return null;
                
            }
            
 
            @Override
            protected void done() { 
                if (isCancelled()) {
                    JOptionPane.showMessageDialog(null,
                            "File import stopped.");
                } else if (!Error) {
                    JOptionPane.showMessageDialog(null, 
                            "File successfully loaded.\n" + 
                            CurLoadedMolecules + " molecule imported");
                }
//                Step1.setCursor(Cursor.getDefaultCursor());
                setEnableGUI(true);
            }

        }
            
//        Step1.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setEnableGUI(false);
        SWLoadMolFile worker = new SWLoadMolFile();
        worker.parent = this;
        worker.execute();

    }      


    
////// MODELS CALCULATION and REPORTS //////////////////////////////////////////    

        
    /**
     * Main procedure for calculation of models
     */
    private void ExecuteModels() {
       
        // Clean log table
        Bar_Txt.setText("");
        
        
        // Builds worker thread
        class SWModelRunner extends SwingWorker<Object, Object> {
            
            private boolean RunnerError = false;
            
            @Override
            protected Object doInBackground() throws Exception {
               
                //// Models calculation
                startTime = System.currentTimeMillis(); 

                // Messenger for progress bar
                iInsilicoModelRunnerMessenger Messenger = new iInsilicoModelRunnerMessenger() {
                    @Override
                    public void SendMessage(String msg) {
                        Bar_Txt.append("\n " + msg);
                    }

                    @Override
                    public void UpdateProgress() {
                        ProgressBar.setValue(ProgressBar.getValue() + 1);
                        ProgressBar.setString("Molecule " + ProgressBar.getValue() 
                                + "/" + DataSet.size() );
                    } 
                };

                Bar_Txt.append(" Starting models execution.");                
                ProgressBar.setIndeterminate(false);
                ProgressBar.setStringPainted(true);
                ProgressBar.setMinimum(0);
                ProgressBar.setMaximum(DataSet.size());
                ProgressBar.setValue(ProgressBar.getMinimum());
                
                try {
                    Runner = new InsilicoModelRunnerByMolecule();
                    Runner.setMessenger(Messenger);
                    
                    // Add single models
                    for (InsilicoModel curModel : CurModels)
                        Runner.AddModel(curModel);
                    
                    // Add consensus models, and required single models
                    for (iInsilicoModelConsensus curModel : CurConsModels) {
                        for (iInsilicoModel requiredModel : curModel.GetRequiredModels())
                            Runner.AddModel(requiredModel, false);
                        Runner.AddModel(curModel);
                    }

                    Runner.Run(DataSet);
                    
                } catch (Throwable e) {
                    Bar_Txt.append("\n An error has occurred: " + e.getMessage()); 
                    RunnerError = true;
                    if (e.getClass() == OutOfMemoryError.class) {
                        try {
                            Runner = null;
                            System.gc();
                        } catch (Throwable ee) {};
                        JOptionPane.showMessageDialog(null, 
                            "A memory error occurred. Please check the user's guide for troubleshooting about memory usage.");                            
                    }
                }

                return null;
            }

  
            @Override 
            protected void done() { 
                if (isCancelled()) {
                    Thread.interrupted();
                    Bar_Txt.append("\n Execution cancelled by user.");
                    tc1.cancelProcess();
                } else if (RunnerError) {
                    Bar_Txt.append("\n Execution stopped due to some errors.");
                    tc1.gotoNoStep();
                    Cancel_Btn1.setVisible(false);
                    Cancel_Lbl.setVisible(false);
                    ProgressBar.setVisible(false);
                } else {
                    Bar_Txt.append("\n Models execution completed.");
                    saveReports();
                }
            }
    
        }
              
    
        final SWModelRunner SW = new SWModelRunner();
        Cancel_Btn1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                Cancel_Btn1MouseReleased(evt);
                SW.cancel(true);
            }
    
            private void Cancel_Btn1MouseReleased(MouseEvent evt) {
                SW.cancel(true);
            }
        }); 

        SW.execute(); 
   
    }          
    

    /**
     * Generate the reports 
     */
    private void saveReports() {
       
         
        // SwingWorker for creating reports
        class SRreports extends SwingWorker<Object, Object> {

            private boolean RunnerError = false;
            
            @Override
            protected Object doInBackground() throws Exception {
              
                boolean Resolution = High_Res.isSelected();
                ProgressBar.setIndeterminate(true);
                ProgressBar.setString("Generating reports");

                try {
                
                    //// Multiple PDF reports

                    if(PDF_Choice1.isSelected()){

                        Bar_Txt.append("\n Generating Multiple PDF Documents...");
                        ReportPDFSingle PDF = new ReportPDFSingle(true);
                        for (InsilicoModelWrapper curModel : Runner.GetModelWrappers()) {
                            if (curModel.isFlagForOutput()) {
                                try {
                                    byte[] document = PDF.CreateReport(DataSet, curModel);
                                    String ReportFile = CreateAndChekReportFilename(PDF_Panel1_Txt.getText() + "/report_" + curModel.getModel().getInfo().getKey(), "pdf");
                                    File Script = new File(ReportFile);
                                    Script.createNewFile();
                                    FileOutputStream outFileStream = new FileOutputStream(Script);
                                    outFileStream.write(document);
                                    outFileStream.flush();
                                    outFileStream.close();
                                } catch (InitFailureException | GenericFailureException | IOException e) {
                                    Bar_Txt.append("\n ERROR while generating report for model " +curModel.getModel().getInfo().getName() + " (cause: " + e.getMessage() + ")");
                                    RunnerError = true;
                                }
                            }
                        }
                        for (InsilicoModelConsensusWrapper curModel : Runner.GetModelConsensusWrappers()) {
                            try {
                                byte[] document = PDF.CreateReport(DataSet, curModel);
                                String ReportFile = CreateAndChekReportFilename(PDF_Panel1_Txt.getText() + "/report_" + curModel.getModel().getInfo().getKey(), "pdf");
                                File Script = new File(ReportFile);
                                Script.createNewFile();
                                FileOutputStream outFileStream = new FileOutputStream(Script);
                                outFileStream.write(document);
                                outFileStream.flush();
                                outFileStream.close();
                            } catch (InitFailureException | GenericFailureException | IOException e) {
                                Bar_Txt.append("\n ERROR while generating report for model " +curModel.getModel().getInfo().getName() + " (cause: " + e.getMessage() + ")");
                                RunnerError = true;
                            }
                        }
                    }


                    //// Single PDF report by model

                    if(PDF_Choice2.isSelected()){

                        Bar_Txt.append("\n Generating PDF single document (ordered by model)...");
                        ReportPDFMultiple PDF = new ReportPDFMultiple(Resolution);
                        try {
                            byte[] document = PDF.CreateReportByModel(DataSet, Runner.GetModelWrappers(), Runner.GetModelConsensusWrappers());
                            String ReportFile = CreateAndChekReportFilename(PDF_Panel2_Txt.getText() + "/report_by_models", "pdf");
                            File Script = new File(ReportFile);
                            Script.createNewFile();
                            FileOutputStream outFileStream = new FileOutputStream(Script);
                            outFileStream.write(document);
                            outFileStream.flush();
                            outFileStream.close();
                            Bar_Txt.append("  Done.");
                        } catch (InitFailureException | GenericFailureException | IOException e) {
                            Bar_Txt.append("\n ERROR while generating report (cause: " + e.getMessage() + ")");
                            RunnerError = true;
                        }
                    }


                    //// Single PDF report by molecule

                    if(PDF_Choice3.isSelected()){

                        Bar_Txt.append("\n Generating PDF single document (ordered by molecule)...");
                        ReportPDFMultiple PDF = new ReportPDFMultiple(Resolution);
                        try {
                            byte[] document = PDF.CreateReportByMolecule(DataSet, Runner.GetModelWrappers(), Runner.GetModelConsensusWrappers());
                            String ReportFile = CreateAndChekReportFilename(PDF_Panel3_Txt.getText() + "/report_by_molecules", "pdf");
                            File Script = new File(ReportFile);
                            Script.createNewFile();
                            FileOutputStream outFileStream = new FileOutputStream(Script);
                            outFileStream.write(document);
                            outFileStream.flush();
                            outFileStream.close();
                            Bar_Txt.append("  Done.");
                        } catch (InitFailureException | GenericFailureException | IOException e) {
                            Bar_Txt.append("\n ERROR while generating report (cause: " + e.getMessage() + ")");
                            RunnerError = true;
                        }
                    }


                    //// Text summary report

                    if(CSV_Choice1.isSelected()){

                        Bar_Txt.append("\n Generating Summary as tab-separated textfile...");
                        try {
                            String ReportFile = CreateAndChekReportFilename(CSV_Panel1_Txt.getText() +
                                "/report_summary", "txt");
                            ReportTXTMultiple.PrintReport(Runner.GetModelWrappers(), Runner.GetModelConsensusWrappers(), ReportFile);
                        } catch (IOException | GenericFailureException e) {
                            Bar_Txt.append("\n ERROR while generating report (cause: " + e.getMessage() + ")");
                            RunnerError = true;
                        }     
                    }


                    //// Text single reports

                    if(CSV_Choice2.isSelected()){

                        Bar_Txt.append("\n Generating Multiple tab-separated textfiles...");
                        for (InsilicoModelWrapper curModel : Runner.GetModelWrappers()) {
                            if (curModel.isFlagForOutput()) {
                                try {
                                    String ReportFile = CreateAndChekReportFilename(CSV_Panel2_Txt.getText() +
                                                    "/report_" + curModel.getModel().getInfo().getKey(), "txt");
                                    ReportTXTSingle.PrintReport(DataSet, curModel, new PrintWriter(ReportFile));
                                } catch (Exception e) {
                                    Bar_Txt.append("\n ERROR while generating report for model " + curModel.getModel().getInfo().getName() + " (cause: " + e.getMessage() + ")");
                                    RunnerError = true;
                                }
                            }
                        }
                        for (InsilicoModelConsensusWrapper curModel : Runner.GetModelConsensusWrappers()) {
                            try {
                                String ReportFile = CreateAndChekReportFilename(CSV_Panel2_Txt.getText() +
                                                "/report_" + curModel.getModel().getInfo().getKey(), "txt");
                                ReportTXTConsensusSingle.PrintReport(DataSet, curModel, ReportFile);
                            } catch (IOException e) {
                                Bar_Txt.append("\n ERROR while generating report for model " + curModel.getModel().getInfo().getName() + " (cause: " + e.getMessage() + ")");
                                RunnerError = true;
                            }
                        }
                    }
                
                } catch (Throwable e) {
                    Bar_Txt.append("\n ERROR while generating report - " + e.getMessage());
                    RunnerError = true;
                    if (e.getClass() == OutOfMemoryError.class) {
                        try {
                            Runner = null;
                            System.gc();
                        } catch (Throwable ee) {};
                        JOptionPane.showMessageDialog(null, 
                            "A memory error occurred. Please check the user's guide for troubleshooting about memory usage.");                            
                    }
                }
                
                ProgressBar.setIndeterminate(false);
                ProgressBar.setStringPainted(false);
                
                return null;
            }
            

            @Override
            protected void done() { 
                if (isCancelled()) {
                    Thread.interrupted();
                    tc1.cancelProcess();
                } else if (RunnerError) {
                    Bar_Txt.append("\n Execution stopped due to some errors.");
                    tc1.gotoNoStep();
                    Cancel_Btn1.setVisible(false);
                    Cancel_Lbl.setVisible(false);
                    ProgressBar.setVisible(false);
                } else {
                    long elapsedTimeMillis = System.currentTimeMillis() - startTime;
                    float elapsedTimeSec = ((int)elapsedTimeMillis/1000F) % 60;
                    float elapsedTimeMin = elapsedTimeMillis/(60*1000F);
                    ElapsedTime = "\n Total time elapsed: " + (int) elapsedTimeMin +
                        " min, " + (int) elapsedTimeSec + " sec";
                    Bar_Txt.append("\n Report generation completed.");
                    Bar_Txt.append(ElapsedTime);

                    // Final popup message and reset gui
                    JOptionPane.showMessageDialog(null, 
                            "Calculation done." + ElapsedTime);
                    tc1.gotoNoStep();
                    Cancel_Btn1.setVisible(false);
                    Cancel_Lbl.setVisible(false);
                    ProgressBar.setVisible(false);
                    
                }
            }
            
        }
        
    
        final SRreports SR1 = new SRreports();
   
        Cancel_Btn1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                Cancel_Btn1MouseReleased(evt);
                SR1.cancel(true);
            }
    
            private void Cancel_Btn1MouseReleased(MouseEvent evt) {
                SR1.cancel(true);
            }
        }); 

        
        SR1.execute();
        
        // Force garbage collector
        System.gc();
    }

    
    /**
     * Procedure to create file names for reports, avoiding overwriting
     * If file already exists, insert progressive numbers in the filename
     */
    public static String CreateAndChekReportFilename(String PathAndName, String Extension) {
        String curName = PathAndName + "." + Extension;
        File f = new File(curName);

        if (!(f.exists()))
            return curName;
        else {
            for (int i=1; i < 1000; i++) {
                curName = PathAndName + "_" + i + "." + Extension;
                f = new File(curName);
                if (!(f.exists()))
                    break;
            }
        }
        
        return curName;
    }
    
    
    /**
     * Checks if it is possible to start calculation
     */
    private boolean CheckBeforeCalculation() {

        // check for molecules
        if (!(Zebra_Table.getRowCount() > 0)) {
            JOptionPane.showMessageDialog(null, " There are no molecules to be processed! "); 
            tc1.gotoStep1();  
            return false;
        }
        
        // Builds list of selected models
        CurModels = Models.GetSelectedModels();
        CurConsModels = Models.GetSelectedModelsCons();
        
        // Checks for models
        if (!( (CurModels.size()+CurConsModels.size()) > 0)) {
            JOptionPane.showMessageDialog(null, " There are no models selected! "); 
            tc1.gotoStep2();            
            return false;
        }

        // Checks for output settings
        if (!(PDF_Choice1.isSelected()||PDF_Choice2.isSelected()||PDF_Choice3.isSelected()||
                CSV_Choice1.isSelected()||CSV_Choice2.isSelected())) {
            JOptionPane.showMessageDialog(null, " There are no reports selected for output! "); 
            tc1.gotoStep3();            
            return false;
        }
        if ( (PDF_Choice1.isSelected() && (PDF_Panel1_Txt.getText().isEmpty())) ||
                (PDF_Choice2.isSelected() && (PDF_Panel2_Txt.getText().isEmpty())) ||
                (PDF_Choice3.isSelected() && (PDF_Panel3_Txt.getText().isEmpty())) ||
                (CSV_Choice1.isSelected() && (CSV_Panel1_Txt.getText().isEmpty())) ||
                (CSV_Choice2.isSelected() && (CSV_Panel2_Txt.getText().isEmpty())) ) {
            JOptionPane.showMessageDialog(null, " Output directory for report is missing! "); 
            tc1.gotoStep3();            
            return false;
        }
        if  (PDF_Choice1.isSelected()) 
            if (!CheckDirectory(PDF_Panel1_Txt.getText()))
                return false;
        if  (PDF_Choice2.isSelected()) 
            if (!CheckDirectory(PDF_Panel2_Txt.getText()))
                return false;
        if  (PDF_Choice3.isSelected()) 
            if (!CheckDirectory(PDF_Panel3_Txt.getText()))
                return false;
        if  (CSV_Choice1.isSelected()) 
            if (!CheckDirectory(CSV_Panel1_Txt.getText()))
                return false;
        if  (CSV_Choice2.isSelected()) 
            if (!CheckDirectory(CSV_Panel2_Txt.getText()))
                return false;

        
        return true;
    }
    
    
    private boolean CheckDirectory(String Folder) {
        
        File f = new File(Folder);
        
        if (!f.exists()) {
            JOptionPane.showMessageDialog(null, " Output directory does not exist (" + Folder + ") "); 
            tc1.gotoStep3();            
            return false;            
        }
        
        return true;
    }
    
     
    private void setEnableGUI(boolean value){
        if(!value){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            GlassPanel.setVisible(true); 
        }
        else{
            GlassPanel.setVisible(false);  
            this.setCursor(Cursor.getDefaultCursor());
        }
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SideBar = new JPanel();
        Step1_Label = new JLabel();
        Step1_Over = new JLabel();
        Step2_Label = new JLabel();
        Step2_Over = new JLabel();
        Step3_Label = new JLabel();
        Step3_Over = new JLabel();
        Predict_Icon = new JLabel();
        Help_Label = new JLabel();
        Help_Effect_Lbl = new JLabel();
        Step1 = new JPanel();
        Step1_Title = new JLabel();
        Veganic_Logo1 = new JLabel();
        Header_Img1 = new JLabel()
        {
            protected void paintComponent( Graphics g )
            {
                int w = getWidth( );
                int h = getHeight( );
                Graphics2D g2d = (Graphics2D)g;
                // Paint a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(102, 153, 204),
                    0, 85, new Color(255,255,255));

                g2d.setPaint( gp );
                g2d.fillRect( 0, 0, w, h );
            }
        }
        ;
        Delete_Btn = new JLabel();
        DeleteAll_Btn = new JLabel();
        Delete_Lbl = new JLabel();
        DeleteAll_Lbl = new JLabel();
        Zebra_ScrollPanel = new JScrollPane();
        TableModel = new DefaultTableModel(new Object[][]{},new Object[]{"ID","SMILES"});
        Zebra_Table = new ZebraTable();
        Marvin_Panel = new JPanel();
        MarvinPanel_Label = new JLabel();
        Veganic_Bottomhills1 = new JLabel();
        jPanelInsert = new JPanel();
        Load_Lbl = new JLabel();
        Load_Textfield = new JTextField();
        Load_Btn = new JLabel();
        Import_Lbl = new JLabel();
        Import_Btn = new JLabel();
        Step2 = new JPanel();
        Veganic_Logo2 = new JLabel();
        Step2_Title = new JLabel();
        Header_Img2 = new JLabel()
        {
            protected void paintComponent( Graphics g )
            {
                int w = getWidth( );
                int h = getHeight( );
                Graphics2D g2d = (Graphics2D)g;
                // Paint a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(102, 153, 204),
                    0, 85, new Color(255,255,255));

                g2d.setPaint( gp );
                g2d.fillRect( 0, 0, w, h );
            }
        }
        ;
        Veganic_Bottomhills2 = new JLabel();
        Step3 = new JPanel();
        Veganic_Logo3 = new JLabel();
        Step3_Title = new JLabel();
        Header_Img3 = new JLabel()
        {
            protected void paintComponent( Graphics g )
            {
                int w = getWidth( );
                int h = getHeight( );
                Graphics2D g2d = (Graphics2D)g;
                // Paint a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(102, 153, 204),
                    0, 85, new Color(255,255,255));

                g2d.setPaint( gp );
                g2d.fillRect( 0, 0, w, h );
            }
        }
        ;
        Veganic_Bottomhills3 = new JLabel();
        Separator = new JLabel();
        PDF_Icon = new JLabel();
        CSV_Icon = new JLabel();
        PDF_Choice1 = new JCheckBox();
        PDF_Choice2 = new JCheckBox();
        PDF_Choice3 = new JCheckBox();
        CSV_Choice1 = new JCheckBox();
        CSV_Choice2 = new JCheckBox();
        PDF_Panel1 = new JPanel();
        PDF_Panel1_Btn = new JLabel();
        PDF_Panel1_Txt = new JTextField()
        {

        }
        ;
        PDF_Panel1_Lbl = new JLabel()
        {
            protected void paintComponent( Graphics g )
            {
                int w = getWidth( );
                int h = getHeight( );
                Graphics2D g2d = (Graphics2D)g;
                // Paint a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                    0, -85, new Color(190,190,190),
                    0, 30, new Color(255,255,255));

                g2d.setPaint( gp );
                g2d.fillRect( 0, 0, w, h );
            }
        }
        ;
        PDF_Panel2 = new JPanel();
        PDF_Panel2_Btn = new JLabel();
        PDF_Panel2_Txt = new JTextField();
        PDF_Panel2_Lbl = new JLabel()
        {
            protected void paintComponent( Graphics g )
            {
                int w = getWidth( );
                int h = getHeight( );
                Graphics2D g2d = (Graphics2D)g;
                // Paint a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                    0, -85, new Color(190,190,190),
                    0, 30, new Color(255,255,255));

                g2d.setPaint( gp );
                g2d.fillRect( 0, 0, w, h );
            }
        }
        ;
        PDF_Panel3 = new JPanel();
        PDF_Panel3_Btn = new JLabel();
        PDF_Panel3_Txt = new JTextField();
        PDF_Panel3_Lbl = new JLabel()
        {
            protected void paintComponent( Graphics g )
            {
                int w = getWidth( );
                int h = getHeight( );
                Graphics2D g2d = (Graphics2D)g;
                // Paint a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                    0, -85, new Color(190,190,190),
                    0, 30, new Color(255,255,255));

                g2d.setPaint( gp );
                g2d.fillRect( 0, 0, w, h );
            }
        }
        ;
        CSV_Panel1 = new JPanel();
        CSV_Panel1_Btn = new JLabel();
        CSV_Panel1_Txt = new JTextField();
        CSV_Panel1_Lbl = new JLabel()
        {
            protected void paintComponent( Graphics g )
            {
                int w = getWidth( );
                int h = getHeight( );
                Graphics2D g2d = (Graphics2D)g;
                // Paint a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                    0, -85, new Color(190,190,190),
                    0, 30, new Color(255,255,255));

                g2d.setPaint( gp );
                g2d.fillRect( 0, 0, w, h );
            }
        }
        ;
        CSV_Panel2 = new JPanel();
        CSV_Panel2_Btn = new JLabel();
        CSV_Panel2_Txt = new JTextField();
        CSV_Panel2_Lbl1 = new JLabel()
        {
            protected void paintComponent( Graphics g )
            {
                int w = getWidth( );
                int h = getHeight( );
                Graphics2D g2d = (Graphics2D)g;
                // Paint a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                    0, -85, new Color(190,190,190),
                    0, 30, new Color(255,255,255));

                g2d.setPaint( gp );
                g2d.fillRect( 0, 0, w, h );
            }
        }
        ;
        High_Res = new JRadioButton();
        Low_Res = new JRadioButton();
        Progress_Bar = new JPanel()
        /*{
            protected void paintComponent( Graphics g )
            {
                int w = getWidth( );
                int h = getHeight( );
                Graphics2D g2d = (Graphics2D)g;
                // Paint a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(102, 153, 204),
                    0, 600, new Color(255,255,255));

                g2d.setPaint( gp );
                g2d.fillRect( 0, 0, w, h );
            }
        }*/
        ;
        Veganic_Logo4 = new JLabel();
        Header_Img4 = new JLabel()
        {
            protected void paintComponent( Graphics g )
            {
                int w = getWidth( );
                int h = getHeight( );
                Graphics2D g2d = (Graphics2D)g;
                // Paint a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(102, 153, 204),
                    0, 85, new Color(255,255,255));

                g2d.setPaint( gp );
                g2d.fillRect( 0, 0, w, h );
            }
        }
        ;
        TextArea = new JScrollPane();
        Bar_Txt = new JTextArea();
        Bottomhills_Progress = new JLabel();
        Cancel_Btn1 = new JLabel();
        Cancel_Lbl = new JLabel();
        ProgressBar = new JProgressBar();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("VegaNIC - VEGA Non-Interactive Client");
        setBackground(new Color(255, 255, 255));
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setForeground(Color.white);
        setMinimumSize(new Dimension(800, 600));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        SideBar.setBackground(new Color(102, 153, 204));
        SideBar.setForeground(new Color(0, 0, 204));
        SideBar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Step1_Label.setIcon(new ImageIcon(getClass().getResource("/icons/Step1_Icon.png"))); // NOI18N
        Step1_Label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        Step1_Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                Step1_LabelMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                Step1_LabelMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                Step1_LabelMouseExited(evt);
            }
        });
        SideBar.add(Step1_Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 98, 80));

        Step1_Over.setBackground(new Color(153, 153, 153));
        Step1_Over.setIcon(new ImageIcon(getClass().getResource("/icons/Step1_Over.png"))); // NOI18N
        Step1_Over.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        SideBar.add(Step1_Over, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 98, 80));

        Step2_Label.setIcon(new ImageIcon(getClass().getResource("/icons/Step2_Icon.png"))); // NOI18N
        Step2_Label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        Step2_Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                Step2_LabelMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                Step2_LabelMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                Step2_LabelMouseExited(evt);
            }
        });
        SideBar.add(Step2_Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 98, 80));

        Step2_Over.setIcon(new ImageIcon(getClass().getResource("/icons/Step2_Over.png"))); // NOI18N
        Step2_Over.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        SideBar.add(Step2_Over, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 98, 80));

        Step3_Label.setIcon(new ImageIcon(getClass().getResource("/icons/Step3_Icon.png"))); // NOI18N
        Step3_Label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        Step3_Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                Step3_LabelMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                Step3_LabelMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                Step3_LabelMouseExited(evt);
            }
        });
        SideBar.add(Step3_Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 98, 80));

        Step3_Over.setIcon(new ImageIcon(getClass().getResource("/icons/Step3_Over.png"))); // NOI18N
        Step3_Over.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        SideBar.add(Step3_Over, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 98, 80));

        Predict_Icon.setIcon(new ImageIcon(getClass().getResource("/icons/Predict_Icon.png"))); // NOI18N
        Predict_Icon.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        Predict_Icon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                Predict_IconMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                Predict_IconMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                Predict_IconMouseExited(evt);
            }
        });
        SideBar.add(Predict_Icon, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 370, 98, 80));

        Help_Label.setIcon(new ImageIcon(getClass().getResource("/icons/Help_Lbl.png"))); // NOI18N
        Help_Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                Help_LabelMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                Help_LabelMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                Help_LabelMouseExited(evt);
            }
        });
        SideBar.add(Help_Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 510, 33, 34));

        Help_Effect_Lbl.setIcon(new ImageIcon(getClass().getResource("/icons/help_eff.png"))); // NOI18N
        SideBar.add(Help_Effect_Lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 540, 32, 7));

        getContentPane().add(SideBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 108, 600));

        Step1.setBackground(new Color(255, 255, 255));
        Step1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Step1_Title.setIcon(new ImageIcon(getClass().getResource("/icons/Step1_Title.png"))); // NOI18N
        Step1.add(Step1_Title, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 411, 75));

        Veganic_Logo1.setHorizontalAlignment(SwingConstants.RIGHT);
        Veganic_Logo1.setIcon(new ImageIcon(getClass().getResource("/icons/vega_logo_small.png"))); // NOI18N
        Step1.add(Veganic_Logo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 160, 38));
        Step1.add(Header_Img1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 690, 85));

        Delete_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Delete_Btn.png"))); // NOI18N
        Delete_Btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                Delete_BtnMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                Delete_BtnMouseExited(evt);
            }
            public void mousePressed(MouseEvent evt) {
                Delete_BtnMousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                Delete_BtnMouseReleased(evt);
            }
        });
        Step1.add(Delete_Btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(364, 436, 66, 38));

        DeleteAll_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/DeleteAll_Btn.png"))); // NOI18N
        DeleteAll_Btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                DeleteAll_BtnMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                DeleteAll_BtnMouseExited(evt);
            }
            public void mousePressed(MouseEvent evt) {
                DeleteAll_BtnMousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                DeleteAll_BtnMouseReleased(evt);
            }
        });
        Step1.add(DeleteAll_Btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(291, 436, 66, 38));

        Delete_Lbl.setFont(new Font("Verdana", 0, 12)); // NOI18N
        Delete_Lbl.setForeground(new Color(61, 69, 76));
        Delete_Lbl.setText("Delete All");
        Step1.add(Delete_Lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(295, 475, -1, -1));

        DeleteAll_Lbl.setFont(new Font("Verdana", 0, 12)); // NOI18N
        DeleteAll_Lbl.setForeground(new Color(61, 69, 76));
        DeleteAll_Lbl.setText("Delete");
        Step1.add(DeleteAll_Lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(378, 475, -1, -1));

        Zebra_ScrollPanel.setBorder(null);
        Zebra_ScrollPanel.setAutoscrolls(true);

        Zebra_Table.setModel(TableModel);
        Zebra_Table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        Zebra_Table.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        Zebra_ScrollPanel.setViewportView(Zebra_Table);

        Zebra_Table.getTableHeader().setPreferredSize(
            new Dimension(Zebra_Table.getColumnModel().getTotalColumnWidth(), 18));

        TableHeader=Zebra_Table.getTableHeader();
        TableHeader.setOpaque(false);
        TableHeader.setFont(new Font("Verdana",1,12));
        TableHeader.setBackground(new Color(204,204,204));
        TableHeader.setForeground(new Color(255,255,255));
        TableHeader.setBorder(BorderFactory.createLineBorder(new Color(192, 192, 192)));

        Zebra_Table.getColumn("ID").setPreferredWidth(75);
        Zebra_Table.getColumn("SMILES").setPreferredWidth(340);
        Zebra_Table.setBackground(new Color(255, 255, 255));

        Zebra_Table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                if(Zebra_Table.getSelectedRow()>=0){
                    PanelMoleculeViewer.SetMolecule(DataSet.get(Zebra_Table.getSelectedRow()));
                }
            }
        });

        Zebra_Table.setFont(new Font("Verdana", 0, 11)); // NOI18N
        Zebra_Table.setForeground(new Color(61, 69, 76));
        Zebra_Table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        Zebra_Table.setGridColor(new Color(144, 188, 231));
        Zebra_Table.setInheritsPopupMenu(true);
        Zebra_Table.setSelectionBackground(new Color(144, 188, 231));
        Zebra_Table.setShowHorizontalLines(false);
        Zebra_ScrollPanel.setViewportView(Zebra_Table);

        Step1.add(Zebra_ScrollPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 195, 406, 235));

        Marvin_Panel.setBackground(new Color(255, 255, 255));
        Marvin_Panel.setLayout(new BorderLayout());
        Step1.add(Marvin_Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(456, 200, 200, 200));

        MarvinPanel_Label.setIcon(new ImageIcon(getClass().getResource("/icons/molecule_Border.png"))); // NOI18N
        Step1.add(MarvinPanel_Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(452, 195, 210, 210));

        Veganic_Bottomhills1.setIcon(new ImageIcon(getClass().getResource("/icons/Veganic_Bottomhills.png"))); // NOI18N
        Veganic_Bottomhills1.setText("jLabel2");
        Veganic_Bottomhills1.setIconTextGap(444);
        Veganic_Bottomhills1.setMaximumSize(new Dimension(692, 106));
        Veganic_Bottomhills1.setMinimumSize(new Dimension(692, 106));
        Veganic_Bottomhills1.setPreferredSize(new Dimension(692, 106));
        Step1.add(Veganic_Bottomhills1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 466, 692, 106));

        jPanelInsert.setBorder(new javax.swing.border.LineBorder(Color.lightGray, 1, true));
        jPanelInsert.setOpaque(false);
        jPanelInsert.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Load_Lbl.setFont(new Font("Verdana", 0, 12)); // NOI18N
        Load_Lbl.setForeground(new Color(61, 69, 76));
        Load_Lbl.setText("Insert SMILES:");
        jPanelInsert.add(Load_Lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 130, 30));

        Load_Textfield.setOpaque(false);
        Load_Textfield.setSelectionColor(new Color(144, 188, 231));
        Load_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Load_TextfieldActionPerformed(evt);
            }
        });
        jPanelInsert.add(Load_Textfield, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 400, 35));

        Load_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Load_Btn.png"))); // NOI18N
        Load_Btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                Load_BtnMousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                Load_BtnMouseReleased(evt);
            }
            public void mouseExited(MouseEvent evt) {
                Load_BtnMouseExited(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                Load_BtnMouseEntered(evt);
            }
        });
        jPanelInsert.add(Load_Btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 28, 66, 39));

        Import_Lbl.setFont(new Font("Verdana", 0, 12)); // NOI18N
        Import_Lbl.setForeground(new Color(61, 69, 76));
        Import_Lbl.setText("Import File");
        Import_Lbl.setVerifyInputWhenFocusTarget(false);
        jPanelInsert.add(Import_Lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 0, 80, 30));

        Import_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Import_Btn.png"))); // NOI18N
        Import_Btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                Import_BtnMousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                Import_BtnMouseReleased(evt);
            }
            public void mouseExited(MouseEvent evt) {
                Import_BtnMouseExited(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                Import_BtnMouseEntered(evt);
            }
        });
        jPanelInsert.add(Import_Btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 28, 66, 39));

        Step1.add(jPanelInsert, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 640, 80));

        getContentPane().add(Step1, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 0, 692, -1));

        Step2.setBackground(new Color(255, 255, 255));
        Step2.setMinimumSize(new Dimension(692, 600));
        Step2.setPreferredSize(new Dimension(692, 600));
        Step2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Veganic_Logo2.setHorizontalAlignment(SwingConstants.RIGHT);
        Veganic_Logo2.setIcon(new ImageIcon(getClass().getResource("/icons/vega_logo_small.png"))); // NOI18N
        Step2.add(Veganic_Logo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 160, 38));

        Step2_Title.setIcon(new ImageIcon(getClass().getResource("/icons/Step2_Title.png"))); // NOI18N
        Step2.add(Step2_Title, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 5, 411, 75));
        Step2.add(Header_Img2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 690, 85));

        Veganic_Bottomhills2.setIcon(new ImageIcon(getClass().getResource("/icons/Veganic_Bottomhills.png"))); // NOI18N
        Veganic_Bottomhills2.setText("jLabel2");
        Veganic_Bottomhills2.setIconTextGap(444);
        Veganic_Bottomhills2.setMaximumSize(new Dimension(692, 106));
        Veganic_Bottomhills2.setMinimumSize(new Dimension(692, 106));
        Veganic_Bottomhills2.setPreferredSize(new Dimension(692, 106));
        Step2.add(Veganic_Bottomhills2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 466, 692, 106));

        getContentPane().add(Step2, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 0, 692, 600));

        Step3.setBackground(new Color(255, 255, 255));
        Step3.setMinimumSize(new Dimension(692, 600));
        Step3.setPreferredSize(new Dimension(692, 600));
        Step3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Veganic_Logo3.setHorizontalAlignment(SwingConstants.RIGHT);
        Veganic_Logo3.setIcon(new ImageIcon(getClass().getResource("/icons/vega_logo_small.png"))); // NOI18N
        Step3.add(Veganic_Logo3, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 160, 38));

        Step3_Title.setIcon(new ImageIcon(getClass().getResource("/icons/Step3_Title.png"))); // NOI18N
        Step3.add(Step3_Title, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 5, -1, -1));

        Header_Img3.setIcon(new ImageIcon(getClass().getResource("/icons/Header_Img.PNG"))); // NOI18N
        Step3.add(Header_Img3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 690, 85));

        Veganic_Bottomhills3.setIcon(new ImageIcon(getClass().getResource("/icons/Veganic_Bottomhills.png"))); // NOI18N
        Veganic_Bottomhills3.setText("jLabel2");
        Veganic_Bottomhills3.setIconTextGap(444);
        Veganic_Bottomhills3.setMaximumSize(new Dimension(692, 106));
        Veganic_Bottomhills3.setMinimumSize(new Dimension(692, 106));
        Veganic_Bottomhills3.setPreferredSize(new Dimension(692, 106));
        Step3.add(Veganic_Bottomhills3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 466, 692, 106));

        //Separator.setOpaque(true);
        Separator.setBackground(new Color(51, 51, 51));
        Separator.setForeground(new Color(51, 51, 51));
        Separator.setIcon(new ImageIcon(getClass().getResource("/icons/Separator1.png"))); // NOI18N
        Separator.setRequestFocusEnabled(false);
        Step3.add(Separator, new org.netbeans.lib.awtextra.AbsoluteConstraints(335, 100, 11, 350));

        PDF_Icon.setIcon(new ImageIcon(getClass().getResource("/icons/PDF_Icon.png"))); // NOI18N
        Step3.add(PDF_Icon, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 38, 48));

        CSV_Icon.setIcon(new ImageIcon(getClass().getResource("/icons/CSV_Icon.png"))); // NOI18N
        Step3.add(CSV_Icon, new org.netbeans.lib.awtextra.AbsoluteConstraints(486, 94, 38, 48));

        PDF_Choice1.setOpaque(false);
        PDF_Choice1.setFont(new Font("Verdana", 0, 12)); // NOI18N
        PDF_Choice1.setForeground(new Color(61, 69, 76));
        PDF_Choice1.setText("PDF reports (one for each model)");
        PDF_Choice1.setRequestFocusEnabled(false);
        PDF_Choice1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                PDF_Choice1ItemStateChanged(evt);
            }
        });
        Step3.add(PDF_Choice1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 310, 25));

        PDF_Choice2.setOpaque(false);
        PDF_Choice2.setFont(new Font("Verdana", 0, 12)); // NOI18N
        PDF_Choice2.setForeground(new Color(61, 69, 76));
        PDF_Choice2.setText("Single PDF report (ordered by model)");
        PDF_Choice2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                PDF_Choice2ItemStateChanged(evt);
            }
        });
        Step3.add(PDF_Choice2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, 310, 25));

        PDF_Choice3.setOpaque(false);
        PDF_Choice3.setFont(new Font("Verdana", 0, 12)); // NOI18N
        PDF_Choice3.setForeground(new Color(61, 69, 76));
        PDF_Choice3.setText("Single PDF report (ordered by molecule)");
        PDF_Choice3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                PDF_Choice3ItemStateChanged(evt);
            }
        });
        Step3.add(PDF_Choice3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 310, 25));

        CSV_Choice1.setOpaque(false);
        CSV_Choice1.setFont(new Font("Verdana", 0, 12)); // NOI18N
        CSV_Choice1.setForeground(new Color(61, 69, 76));
        CSV_Choice1.setText("Summary (single plain text file)");
        CSV_Choice1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                CSV_Choice1ItemStateChanged(evt);
            }
        });
        Step3.add(CSV_Choice1, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 250, 310, 25));

        CSV_Choice2.setOpaque(false);
        CSV_Choice2.setFont(new Font("Verdana", 0, 12)); // NOI18N
        CSV_Choice2.setForeground(new Color(61, 69, 76));
        CSV_Choice2.setText("Plain text files (one for each model)");
        CSV_Choice2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                CSV_Choice2ItemStateChanged(evt);
            }
        });
        Step3.add(CSV_Choice2, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 160, 310, 25));

        PDF_Panel1.setForeground(new Color(204, 204, 204));
        PDF_Panel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        PDF_Panel1.setOpaque(false);

        PDF_Panel1_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png"))); // NOI18N
        PDF_Panel1_Btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                PDF_Panel1_BtnMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                PDF_Panel1_BtnMouseExited(evt);
            }
            public void mousePressed(MouseEvent evt) {
                PDF_Panel1_BtnMousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                PDF_Panel1_BtnMouseReleased(evt);
            }
        });
        PDF_Panel1.add(PDF_Panel1_Btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 51, 30));

        PDF_Panel1_Txt.setOpaque(false);
        PDF_Panel1.add(PDF_Panel1_Txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 26));
        PDF_Panel1.add(PDF_Panel1_Lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 26));

        Step3.add(PDF_Panel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 199, 310, 30));

        PDF_Panel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        PDF_Panel2.setOpaque(false);

        PDF_Panel2_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png"))); // NOI18N
        PDF_Panel2_Btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                PDF_Panel2_BtnMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                PDF_Panel2_BtnMouseExited(evt);
            }
            public void mousePressed(MouseEvent evt) {
                PDF_Panel2_BtnMousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                PDF_Panel2_BtnMouseReleased(evt);
            }
        });
        PDF_Panel2.add(PDF_Panel2_Btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 51, 30));

        PDF_Panel2_Txt.setOpaque(false);
        PDF_Panel2.add(PDF_Panel2_Txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 26));
        PDF_Panel2.add(PDF_Panel2_Lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 26));

        Step3.add(PDF_Panel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 310, 30));

        PDF_Panel3.setOpaque(false);
        PDF_Panel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        PDF_Panel3.setOpaque(false);

        PDF_Panel3_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png"))); // NOI18N
        PDF_Panel3_Btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                PDF_Panel3_BtnMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                PDF_Panel3_BtnMouseExited(evt);
            }
            public void mousePressed(MouseEvent evt) {
                PDF_Panel3_BtnMousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                PDF_Panel3_BtnMouseReleased(evt);
            }
        });
        PDF_Panel3.add(PDF_Panel3_Btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 51, 30));

        PDF_Panel3_Txt.setOpaque(false);
        PDF_Panel3.add(PDF_Panel3_Txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 26));
        PDF_Panel3.add(PDF_Panel3_Lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 26));

        Step3.add(PDF_Panel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, 310, 30));

        CSV_Panel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        CSV_Panel1.setOpaque(false);

        CSV_Panel1_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png"))); // NOI18N
        CSV_Panel1_Btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                CSV_Panel1_BtnMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                CSV_Panel1_BtnMouseExited(evt);
            }
            public void mousePressed(MouseEvent evt) {
                CSV_Panel1_BtnMousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                CSV_Panel1_BtnMouseReleased(evt);
            }
        });
        CSV_Panel1.add(CSV_Panel1_Btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 51, 30));

        CSV_Panel1_Txt.setOpaque(false);
        CSV_Panel1.add(CSV_Panel1_Txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 26));
        CSV_Panel1.add(CSV_Panel1_Lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 26));

        Step3.add(CSV_Panel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 290, 310, 30));

        CSV_Panel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        CSV_Panel2.setOpaque(false);

        CSV_Panel2_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png"))); // NOI18N
        CSV_Panel2_Btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                CSV_Panel2_BtnMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                CSV_Panel2_BtnMouseExited(evt);
            }
            public void mousePressed(MouseEvent evt) {
                CSV_Panel2_BtnMousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                CSV_Panel2_BtnMouseReleased(evt);
            }
        });
        CSV_Panel2.add(CSV_Panel2_Btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 51, 30));

        CSV_Panel2_Txt.setOpaque(false);
        CSV_Panel2.add(CSV_Panel2_Txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 26));
        CSV_Panel2.add(CSV_Panel2_Lbl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 26));

        Step3.add(CSV_Panel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 199, 310, 30));

        High_Res.setFont(new Font("Verdana", 0, 11)); // NOI18N
        High_Res.setOpaque(false);
        High_Res.setText("High Resolution");
        High_Res.setRequestFocusEnabled(false);
        Step3.add(High_Res, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 430, 120, -1));

        Low_Res.setFont(new Font("Verdana", 0, 11)); // NOI18N
        Low_Res.setOpaque(false);
        Low_Res.setSelected(true);
        Low_Res.setText("Low Resolution");
        Low_Res.setRequestFocusEnabled(false);
        Step3.add(Low_Res, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 430, 120, -1));

        ButtonGroup group = new ButtonGroup();
        group.add(High_Res);
        group.add(Low_Res);

        getContentPane().add(Step3, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 0, 692, 600));

        Progress_Bar.setBackground(new Color(255, 255, 255));
        Progress_Bar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Veganic_Logo4.setHorizontalAlignment(SwingConstants.RIGHT);
        Veganic_Logo4.setIcon(new ImageIcon(getClass().getResource("/icons/vega_logo_small.png"))); // NOI18N
        Progress_Bar.add(Veganic_Logo4, new org.netbeans.lib.awtextra.AbsoluteConstraints(618, 20, 160, 38));
        Progress_Bar.add(Header_Img4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 85));

        TextArea.setBackground(new Color(246, 245, 245));
        TextArea.setBorder(BorderFactory.createTitledBorder(null, "Progress", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new Font("Verdana", 0, 14), new Color(92, 122, 151))); // NOI18N
        TextArea.setAutoscrolls(true);
        TextArea.setOpaque(false);

        Bar_Txt.setEditable(false);
        Bar_Txt.setColumns(20);
        Bar_Txt.setFont(new Font("Verdana", 0, 12)); // NOI18N
        Bar_Txt.setForeground(new Color(54, 117, 180));
        Bar_Txt.setRows(5);
        Bar_Txt.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(255, 255, 255)));
        Bar_Txt.setFocusable(false);
        Bar_Txt.setSelectionColor(new Color(61, 69, 76));
        TextArea.setViewportView(Bar_Txt);

        Progress_Bar.add(TextArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, 510, 190));

        Bottomhills_Progress.setIcon(new ImageIcon(getClass().getResource("/icons/Bottomhills_Progress.png"))); // NOI18N
        Progress_Bar.add(Bottomhills_Progress, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 466, 800, 106));

        Cancel_Btn1.setIcon(new ImageIcon(getClass().getResource("/icons/Delete_Btn.png"))); // NOI18N
        Cancel_Btn1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                Cancel_Btn1MouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                Cancel_Btn1MouseExited(evt);
            }
            public void mousePressed(MouseEvent evt) {
                Cancel_Btn1MousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                Cancel_Btn1MouseReleased(evt);
            }
        });
        Progress_Bar.add(Cancel_Btn1, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 340, 66, 38));

        Cancel_Lbl.setFont(new Font("Verdana", 0, 12)); // NOI18N
        Cancel_Lbl.setForeground(new Color(61, 69, 76));
        Cancel_Lbl.setText("Cancel");
        Progress_Bar.add(Cancel_Lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 370, 50, 30));
        Progress_Bar.add(ProgressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 310, 510, 20));

        getContentPane().add(Progress_Bar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    
private void Step2_LabelMouseClicked(MouseEvent evt) {//GEN-FIRST:event_Step2_LabelMouseClicked

    tc1.gotoStep2();
    
}//GEN-LAST:event_Step2_LabelMouseClicked

private void Step3_LabelMouseClicked(MouseEvent evt) {//GEN-FIRST:event_Step3_LabelMouseClicked

    tc1.gotoStep3();    
    
}//GEN-LAST:event_Step3_LabelMouseClicked


private void Predict_IconMouseClicked(MouseEvent evt) {//GEN-FIRST:event_Predict_IconMouseClicked
    
    if (CheckBeforeCalculation()) {
        Bar_Txt.setText("");
        ExecuteModels();
        tc1.predict();
    }
    
}//GEN-LAST:event_Predict_IconMouseClicked

private void Load_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Load_TextfieldActionPerformed

      LoadSingleSMILES();
      
}//GEN-LAST:event_Load_TextfieldActionPerformed

private void Load_BtnMouseEntered(MouseEvent evt) {//GEN-FIRST:event_Load_BtnMouseEntered

   Load_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Load_Btn_Over.png")));
    
}//GEN-LAST:event_Load_BtnMouseEntered

private void Load_BtnMouseExited(MouseEvent evt) {//GEN-FIRST:event_Load_BtnMouseExited

    Load_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Load_Btn.png")));
    
}//GEN-LAST:event_Load_BtnMouseExited

private void Load_BtnMousePressed(MouseEvent evt) {//GEN-FIRST:event_Load_BtnMousePressed
 
    Load_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Load_Btn.png")));
    
   
    
}//GEN-LAST:event_Load_BtnMousePressed

private void Load_BtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_Load_BtnMouseReleased

    Load_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Load_Btn_Over.png")));
    LoadSingleSMILES();  
}//GEN-LAST:event_Load_BtnMouseReleased

private void Import_BtnMouseEntered(MouseEvent evt) {//GEN-FIRST:event_Import_BtnMouseEntered

    Import_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Import_Btn_Over.png")));
    
}//GEN-LAST:event_Import_BtnMouseEntered

private void Import_BtnMouseExited(MouseEvent evt) {//GEN-FIRST:event_Import_BtnMouseExited
    
    Import_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Import_Btn.png")));
    
}//GEN-LAST:event_Import_BtnMouseExited

private void Import_BtnMousePressed(MouseEvent evt) {//GEN-FIRST:event_Import_BtnMousePressed
    
    Import_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Import_Btn.png")));
    
}//GEN-LAST:event_Import_BtnMousePressed

private void Import_BtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_Import_BtnMouseReleased
   
    Import_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Import_Btn_Over.png")));
    LoadFile();
    
}//GEN-LAST:event_Import_BtnMouseReleased

private void Delete_BtnMouseEntered(MouseEvent evt) {//GEN-FIRST:event_Delete_BtnMouseEntered

    Delete_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Delete_Btn_Over.png")));
    
}//GEN-LAST:event_Delete_BtnMouseEntered

private void Delete_BtnMouseExited(MouseEvent evt) {//GEN-FIRST:event_Delete_BtnMouseExited

    Delete_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Delete_Btn.png")));
    
}//GEN-LAST:event_Delete_BtnMouseExited

private void Delete_BtnMousePressed(MouseEvent evt) {//GEN-FIRST:event_Delete_BtnMousePressed

    Delete_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Delete_Btn.png")));
    
}//GEN-LAST:event_Delete_BtnMousePressed

private void Delete_BtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_Delete_BtnMouseReleased

    Delete_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Delete_Btn_Over.png")));
    
    
       int numRows = Zebra_Table.getSelectedRows().length;
       int numRows1 = DataSet.size(); 

        for(int i=0; i<numRows ; i++ ){
           
        DataSet.remove(Zebra_Table.getSelectedRow());   
        TableModel.removeRow(Zebra_Table.getSelectedRow());
        PanelMoleculeViewer.SetMolecule(null);
        
        }
        
        int rowCount = Zebra_Table.getRowCount();
       
       /* for(int i=0; i<rowCount ; i++ ) {
        
            Zebra_Table.setValueAt(i+1,i, 0);
      
        
        }
      */ 
}//GEN-LAST:event_Delete_BtnMouseReleased

private void DeleteAll_BtnMouseEntered(MouseEvent evt) {//GEN-FIRST:event_DeleteAll_BtnMouseEntered

    DeleteAll_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/DeleteAll_Btn_Over.png")));
        
}//GEN-LAST:event_DeleteAll_BtnMouseEntered

private void DeleteAll_BtnMouseExited(MouseEvent evt) {//GEN-FIRST:event_DeleteAll_BtnMouseExited

    DeleteAll_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/DeleteAll_Btn.png")));
    
}//GEN-LAST:event_DeleteAll_BtnMouseExited

private void DeleteAll_BtnMousePressed(MouseEvent evt) {//GEN-FIRST:event_DeleteAll_BtnMousePressed

    DeleteAll_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/DeleteAll_Btn.png")));

}//GEN-LAST:event_DeleteAll_BtnMousePressed

private void DeleteAll_BtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_DeleteAll_BtnMouseReleased

    DeleteAll_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/DeleteAll_Btn_Over.png")));
        
    while (TableModel.getRowCount()>0)
        TableModel.removeRow(0);

    Zebra_Table.repaint();
    Load_Textfield.setText("");
    PanelMoleculeViewer.SetMolecule(null);

    // Removes list and force garbage collector
    DataSet = new ArrayList<>();
    System.gc();
}//GEN-LAST:event_DeleteAll_BtnMouseReleased

private void Step1_LabelMouseClicked(MouseEvent evt) {//GEN-FIRST:event_Step1_LabelMouseClicked

    tc1.gotoStep1();
    
}//GEN-LAST:event_Step1_LabelMouseClicked

private void Predict_IconMouseEntered(MouseEvent evt) {//GEN-FIRST:event_Predict_IconMouseEntered

    Predict_Icon.setIcon(new ImageIcon(getClass().getResource("/icons/Predict_Over.png")));
    
}//GEN-LAST:event_Predict_IconMouseEntered

private void Predict_IconMouseExited(MouseEvent evt) {//GEN-FIRST:event_Predict_IconMouseExited

    Predict_Icon.setIcon(new ImageIcon(getClass().getResource("/icons/Predict_Icon.png")));
   
}//GEN-LAST:event_Predict_IconMouseExited

private void Step1_LabelMouseEntered(MouseEvent evt) {//GEN-FIRST:event_Step1_LabelMouseEntered

    Step1_Label.setIcon(new ImageIcon(getClass().getResource("/icons/Step1_Over.png")));
    
}//GEN-LAST:event_Step1_LabelMouseEntered

private void Step1_LabelMouseExited(MouseEvent evt) {//GEN-FIRST:event_Step1_LabelMouseExited

    Step1_Label.setIcon(new ImageIcon(getClass().getResource("/icons/Step1_Icon.png")));
    
}//GEN-LAST:event_Step1_LabelMouseExited

private void Step2_LabelMouseEntered(MouseEvent evt) {//GEN-FIRST:event_Step2_LabelMouseEntered

    Step2_Label.setIcon(new ImageIcon(getClass().getResource("/icons/Step2_Over.png")));
    
}//GEN-LAST:event_Step2_LabelMouseEntered

private void Step2_LabelMouseExited(MouseEvent evt) {//GEN-FIRST:event_Step2_LabelMouseExited

    Step2_Label.setIcon(new ImageIcon(getClass().getResource("/icons/Step2_Icon.png")));
  
}//GEN-LAST:event_Step2_LabelMouseExited

private void Step3_LabelMouseEntered(MouseEvent evt) {//GEN-FIRST:event_Step3_LabelMouseEntered

   Step3_Label.setIcon(new ImageIcon(getClass().getResource("/icons/Step3_Over.png")));
   
}//GEN-LAST:event_Step3_LabelMouseEntered

private void Step3_LabelMouseExited(MouseEvent evt) {//GEN-FIRST:event_Step3_LabelMouseExited

   Step3_Label.setIcon(new ImageIcon(getClass().getResource("/icons/Step3_Icon.png")));
    
}//GEN-LAST:event_Step3_LabelMouseExited

    private void PDF_Choice1ItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_PDF_Choice1ItemStateChanged
            
        if (evt.getStateChange() == ItemEvent.SELECTED) {
     
                PDF_Panel1.setVisible(true);
            
        }
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
     
                PDF_Panel1.setVisible(false);
            
        }
 
    }//GEN-LAST:event_PDF_Choice1ItemStateChanged

    private void PDF_Choice2ItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_PDF_Choice2ItemStateChanged
        
        if (evt.getStateChange() == ItemEvent.SELECTED) {
     
                PDF_Panel2.setVisible(true);
            
        }
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
     
                PDF_Panel2.setVisible(false);
            
        }
        
    }//GEN-LAST:event_PDF_Choice2ItemStateChanged

    private void PDF_Choice3ItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_PDF_Choice3ItemStateChanged
        
        if (evt.getStateChange() == ItemEvent.SELECTED) {
     
                PDF_Panel3.setVisible(true);
            
        }
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
     
                PDF_Panel3.setVisible(false);
            
        }
        
    }//GEN-LAST:event_PDF_Choice3ItemStateChanged

    private void CSV_Choice1ItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_CSV_Choice1ItemStateChanged
    
        if (evt.getStateChange() == ItemEvent.SELECTED) {
     
                CSV_Panel1.setVisible(true);
            
        }
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
     
                CSV_Panel1.setVisible(false);
            
        }
    }//GEN-LAST:event_CSV_Choice1ItemStateChanged

    private void CSV_Choice2ItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_CSV_Choice2ItemStateChanged

        if (evt.getStateChange() == ItemEvent.SELECTED) {
     
                CSV_Panel2.setVisible(true);
            
        }
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
     
                CSV_Panel2.setVisible(false);
            
        }
    }//GEN-LAST:event_CSV_Choice2ItemStateChanged

    
        
    private void PDF_Panel1_BtnMouseEntered(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel1_BtnMouseEntered
        
        PDF_Panel1_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label_Over.png")));
        
    }//GEN-LAST:event_PDF_Panel1_BtnMouseEntered

    private void PDF_Panel1_BtnMouseExited(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel1_BtnMouseExited
        
       PDF_Panel1_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png")));
        
    }//GEN-LAST:event_PDF_Panel1_BtnMouseExited

    private void PDF_Panel2_BtnMouseEntered(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel2_BtnMouseEntered
        
        PDF_Panel2_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label_Over.png")));
        
    }//GEN-LAST:event_PDF_Panel2_BtnMouseEntered

    private void PDF_Panel2_BtnMouseExited(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel2_BtnMouseExited
        
        PDF_Panel2_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png")));
        
    }//GEN-LAST:event_PDF_Panel2_BtnMouseExited

    private void PDF_Panel3_BtnMouseEntered(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel3_BtnMouseEntered
       
        PDF_Panel3_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label_Over.png")));
        
    }//GEN-LAST:event_PDF_Panel3_BtnMouseEntered

    private void PDF_Panel3_BtnMouseExited(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel3_BtnMouseExited
        
        PDF_Panel3_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png")));
    }//GEN-LAST:event_PDF_Panel3_BtnMouseExited

    private void CSV_Panel1_BtnMouseEntered(MouseEvent evt) {//GEN-FIRST:event_CSV_Panel1_BtnMouseEntered
        
        CSV_Panel1_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label_Over.png")));
        
    }//GEN-LAST:event_CSV_Panel1_BtnMouseEntered

    private void CSV_Panel1_BtnMouseExited(MouseEvent evt) {//GEN-FIRST:event_CSV_Panel1_BtnMouseExited
        
        CSV_Panel1_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png")));
        
    }//GEN-LAST:event_CSV_Panel1_BtnMouseExited

    private void CSV_Panel2_BtnMouseEntered(MouseEvent evt) {//GEN-FIRST:event_CSV_Panel2_BtnMouseEntered
        
        CSV_Panel2_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label_Over.png")));
        
    }//GEN-LAST:event_CSV_Panel2_BtnMouseEntered

    private void CSV_Panel2_BtnMouseExited(MouseEvent evt) {//GEN-FIRST:event_CSV_Panel2_BtnMouseExited
        
        CSV_Panel2_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png")));
        
    }//GEN-LAST:event_CSV_Panel2_BtnMouseExited

    private void PDF_Panel1_BtnMousePressed(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel1_BtnMousePressed
      
        PDF_Panel1_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png")));
        
    }//GEN-LAST:event_PDF_Panel1_BtnMousePressed

    private void PDF_Panel1_BtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel1_BtnMouseReleased
        
        try{
            
        File ReportFile ;
        JFileChooser fc = new JFileChooser();
        if (LastDir != null)
            try {
                fc.setSelectedFile(LastDir);
            } catch (Throwable e) { /* do nothing */ }
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(null);
        ReportFile = fc.getSelectedFile();
        LastDir = ReportFile;
        PDF_Panel1_Txt.setText(ReportFile.toString());
      //  System.out.println(ReportFile.toString() + "/report_");
        }catch(NullPointerException e)
        {
             //Cancel    
        }
        
    }//GEN-LAST:event_PDF_Panel1_BtnMouseReleased

    private void PDF_Panel2_BtnMousePressed(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel2_BtnMousePressed
       
        PDF_Panel2_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png")));
       
    }//GEN-LAST:event_PDF_Panel2_BtnMousePressed

    private void PDF_Panel2_BtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel2_BtnMouseReleased
        
        try{
            
        File ReportFile ;
        JFileChooser fc = new JFileChooser();
        if (LastDir != null)
            try {
                fc.setSelectedFile(LastDir);
            } catch (Throwable e) { /* do nothing */ }
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(null);
        ReportFile = fc.getSelectedFile();
        LastDir = ReportFile;
        PDF_Panel2_Txt.setText(ReportFile.toString());
      //  System.out.println(ReportFile.toString() + "/report_");
        }catch(NullPointerException e)
        {
             //Cancel    
        }
        
    }//GEN-LAST:event_PDF_Panel2_BtnMouseReleased

    private void PDF_Panel3_BtnMousePressed(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel3_BtnMousePressed
     
        PDF_Panel3_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png")));
        
    }//GEN-LAST:event_PDF_Panel3_BtnMousePressed

    private void PDF_Panel3_BtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_PDF_Panel3_BtnMouseReleased
       
        try{
            
        File ReportFile ;
        JFileChooser fc = new JFileChooser();
        if (LastDir != null)
            try {
                fc.setSelectedFile(LastDir);
            } catch (Throwable e) { /* do nothing */ }
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(null);
        ReportFile = fc.getSelectedFile();
        LastDir = ReportFile;
        PDF_Panel3_Txt.setText(ReportFile.toString());
      //  System.out.println(ReportFile.toString() + "/report_");
        }catch(NullPointerException e)
        {
             //Cancel    
        }
        
    }//GEN-LAST:event_PDF_Panel3_BtnMouseReleased

    private void CSV_Panel1_BtnMousePressed(MouseEvent evt) {//GEN-FIRST:event_CSV_Panel1_BtnMousePressed
        
      CSV_Panel1_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png")));
        
    }//GEN-LAST:event_CSV_Panel1_BtnMousePressed

    private void CSV_Panel1_BtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_CSV_Panel1_BtnMouseReleased
       
               try{
            
        File ReportFile ;
        JFileChooser fc = new JFileChooser();
        if (LastDir != null)
            try {
                fc.setSelectedFile(LastDir);
            } catch (Throwable e) { /* do nothing */ }
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(null);
        ReportFile = fc.getSelectedFile();
        LastDir = ReportFile;
        CSV_Panel1_Txt.setText(ReportFile.toString());
      //  System.out.println(ReportFile.toString() + "/report_");
        }catch(NullPointerException e)
        {
             //Cancel    
        }   
     
    }//GEN-LAST:event_CSV_Panel1_BtnMouseReleased

    private void CSV_Panel2_BtnMousePressed(MouseEvent evt) {//GEN-FIRST:event_CSV_Panel2_BtnMousePressed
       
      CSV_Panel2_Btn.setIcon(new ImageIcon(getClass().getResource("/icons/Save_Label.png")));
        
    }//GEN-LAST:event_CSV_Panel2_BtnMousePressed

    private void CSV_Panel2_BtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_CSV_Panel2_BtnMouseReleased
    
        try{
            
        File ReportFile ;
        JFileChooser fc = new JFileChooser();
        if (LastDir != null)
            try {
                fc.setSelectedFile(LastDir);
            } catch (Throwable e) { /* do nothing */ }
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(null);
        ReportFile = fc.getSelectedFile();
        LastDir = ReportFile;
        
        CSV_Panel2_Txt.setText(ReportFile.toString());
      //  System.out.println(ReportFile.toString() + "/report_");
        }catch(NullPointerException e)
        {
             //Cancel    
        }
        
    }//GEN-LAST:event_CSV_Panel2_BtnMouseReleased

    private void Help_LabelMouseEntered(MouseEvent evt) {//GEN-FIRST:event_Help_LabelMouseEntered
        
       Help_Effect_Lbl.setIcon(new ImageIcon(getClass().getResource("/icons/help_eff_over.png")));
       Help_Label.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_Help_LabelMouseEntered

    private void Help_LabelMouseExited(MouseEvent evt) {//GEN-FIRST:event_Help_LabelMouseExited
        
      Help_Effect_Lbl.setIcon(new ImageIcon(getClass().getResource("/icons/help_eff.png")));
        
    }//GEN-LAST:event_Help_LabelMouseExited

    private void Cancel_Btn1MouseEntered(MouseEvent evt) {//GEN-FIRST:event_Cancel_Btn1MouseEntered
        
      Cancel_Btn1.setIcon(new ImageIcon(getClass().getResource("/icons/Delete_Btn_Over.png")));
        
    }//GEN-LAST:event_Cancel_Btn1MouseEntered

    private void Cancel_Btn1MouseExited(MouseEvent evt) {//GEN-FIRST:event_Cancel_Btn1MouseExited
      
        Cancel_Btn1.setIcon(new ImageIcon(getClass().getResource("/icons/Delete_Btn.png")));
        
    }//GEN-LAST:event_Cancel_Btn1MouseExited

    private void Cancel_Btn1MousePressed(MouseEvent evt) {//GEN-FIRST:event_Cancel_Btn1MousePressed
        
        Cancel_Btn1.setIcon(new ImageIcon(getClass().getResource("/icons/Delete_Btn_Over.png")));
    }//GEN-LAST:event_Cancel_Btn1MousePressed

    private void Cancel_Btn1MouseReleased(MouseEvent evt) {//GEN-FIRST:event_Cancel_Btn1MouseReleased
        
        Cancel_Btn1.setIcon(new ImageIcon(getClass().getResource("/icons/Delete_Btn.png")));
        
    }//GEN-LAST:event_Cancel_Btn1MouseReleased

    private void Help_LabelMouseClicked(MouseEvent evt) {//GEN-FIRST:event_Help_LabelMouseClicked
      
        this.AboutFrame.setLocationRelativeTo(null);
        this.AboutFrame.setVisible(true);
              
    }//GEN-LAST:event_Help_LabelMouseClicked

        
    

    /**
     * 
     */
    public static void launch() throws IOException {
        pySup=new PythonSetup();
        FrameLoading fLoader=new FrameLoading(VegaVersion.UNINSTALL_VEGA ? "Uninstalling VEGA..." : "Starting VEGA...");
        fLoader.setVisible(true);
        if(VegaVersion.UNINSTALL_VEGA){
            var selection = JOptionPane.showOptionDialog(fLoader,
                    "VEGA is going to be uninstalled.",
                    "Uninstalling VEGA",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,null, null);

            if(selection == 0) {
                pySup.removeALlPythonFolders();
            }
            else{
                fLoader.dispatchEvent(new WindowEvent(fLoader, WindowEvent.WINDOW_CLOSING));
                return;
            }
        }

        if(!VegaVersion.UNINSTALL_VEGA) {
            /// CHECK PYTHON AND CONDA
            boolean result = checkPythonAndConda(fLoader);
            if (!result) {
                return;
            }
        }
        /* Create and display the form */
        
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new FrameMain(fLoader);
            }
        });
    }

    private static boolean checkPythonAndConda(FrameLoading frameLoader) {
        boolean isOk =false;
        try{
            isOk = pySup.checkConda();
            if(!isOk){
                JOptionPane.showMessageDialog(frameLoader,
                        "A personalized copy of Conda will be installed on the machine. It is needed to run some models. \n\r"
                                +"Click OK to download and install it. \r\n" +
                                "Note that it requires an internet connection.\n\r");
                frameLoader.setLabelText("Installing Conda...");
                isOk = pySup.installConda();
                if(!isOk){
                    JOptionPane.showMessageDialog(frameLoader,
                            "An error occurred during Conda installation. Please restart VEGA.",
                            "Conda installation error",
                            JOptionPane.ERROR_MESSAGE);
                    frameLoader.dispatchEvent(new WindowEvent(frameLoader, WindowEvent.WINDOW_CLOSING));
                    return false;
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return isOk;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTextArea Bar_Txt;
    private JLabel Bottomhills_Progress;
    private JCheckBox CSV_Choice1;
    private JCheckBox CSV_Choice2;
    private JLabel CSV_Icon;
    private JPanel CSV_Panel1;
    private JLabel CSV_Panel1_Btn;
    private JLabel CSV_Panel1_Lbl;
    private JTextField CSV_Panel1_Txt;
    private JPanel CSV_Panel2;
    private JLabel CSV_Panel2_Btn;
    private JLabel CSV_Panel2_Lbl1;
    private JTextField CSV_Panel2_Txt;
    private JLabel Cancel_Btn1;
    private JLabel Cancel_Lbl;
    private JLabel DeleteAll_Btn;
    private JLabel DeleteAll_Lbl;
    private JLabel Delete_Btn;
    private JLabel Delete_Lbl;
    private JLabel Header_Img1;
    private JLabel Header_Img2;
    private JLabel Header_Img3;
    private JLabel Header_Img4;
    private JLabel Help_Effect_Lbl;
    private JLabel Help_Label;
    private JRadioButton High_Res;
    private JLabel Import_Btn;
    private JLabel Import_Lbl;
    private JLabel Load_Btn;
    private JLabel Load_Lbl;
    private JTextField Load_Textfield;
    private JRadioButton Low_Res;
    private JLabel MarvinPanel_Label;
    private JPanel Marvin_Panel;
    private JCheckBox PDF_Choice1;
    private JCheckBox PDF_Choice2;
    private JCheckBox PDF_Choice3;
    private JLabel PDF_Icon;
    private JPanel PDF_Panel1;
    private JLabel PDF_Panel1_Btn;
    private JLabel PDF_Panel1_Lbl;
    private JTextField PDF_Panel1_Txt;
    private JPanel PDF_Panel2;
    private JLabel PDF_Panel2_Btn;
    private JLabel PDF_Panel2_Lbl;
    private JTextField PDF_Panel2_Txt;
    private JPanel PDF_Panel3;
    private JLabel PDF_Panel3_Btn;
    private JLabel PDF_Panel3_Lbl;
    private JTextField PDF_Panel3_Txt;
    private JLabel Predict_Icon;
    private JProgressBar ProgressBar;
    private JPanel Progress_Bar;
    private JLabel Separator;
    private JPanel SideBar;
    private JPanel Step1;
    private JLabel Step1_Label;
    private JLabel Step1_Over;
    private JLabel Step1_Title;
    private JPanel Step2;
    private JLabel Step2_Label;
    private JLabel Step2_Over;
    private JLabel Step2_Title;
    private JPanel Step3;
    private JLabel Step3_Label;
    private JLabel Step3_Over;
    private JLabel Step3_Title;
    private JScrollPane TextArea;
    private JLabel Veganic_Bottomhills1;
    private JLabel Veganic_Bottomhills2;
    private JLabel Veganic_Bottomhills3;
    private JLabel Veganic_Logo1;
    private JLabel Veganic_Logo2;
    private JLabel Veganic_Logo3;
    private JLabel Veganic_Logo4;
    private JScrollPane Zebra_ScrollPanel;
    private JTable Zebra_Table;
    private JPanel jPanelInsert;
    // End of variables declaration//GEN-END:variables
}
