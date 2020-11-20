package insilico.vega.gui.utilities;

import insilico.core.exception.GenericFailureException; 
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.swing.JFileChooser;

/**
 *
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class PDFDocumentLauncher {

    private final String DocumentURL;
    private final String DocumentFilename;
    
    
    public PDFDocumentLauncher(String DocumentURL, String DocumentFilename) {
        this.DocumentFilename = DocumentFilename;
        this.DocumentURL = DocumentURL;
    }
    
    public void Open() throws GenericFailureException {
        
        try {

            boolean DesktopSupported = true;
            if (!Desktop.isDesktopSupported()) {
                DesktopSupported = false;
            } else {
                Desktop desktop = Desktop.getDesktop();
                if (!desktop.isSupported(Desktop.Action.OPEN)) {
                    DesktopSupported = false;
                }
            }

            URL uPDF = getClass().getResource(DocumentURL);

            if (DesktopSupported) {

                File doc = File.createTempFile("VEGA_doc_", ".pdf");
                doc.deleteOnExit();
                InputStream in = (InputStream) uPDF.openStream();
                OutputStream out = new FileOutputStream(doc);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0)
                    out.write(buf, 0, len);

                out.close();
                Desktop.getDesktop().open(doc);

            } else {

                // Gets output directory
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setDialogTitle("Save PDF document");
                fc.setSelectedFile(new File(DocumentFilename + ".pdf"));
                int res = fc.showSaveDialog(null);

                // Saves doc
                if (!((res == JFileChooser.CANCEL_OPTION) || (res == 
                       JFileChooser.ERROR_OPTION))) {

                    InputStream in = (InputStream) uPDF.openStream();
                    OutputStream out = new FileOutputStream(fc.getSelectedFile());

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0)
                        out.write(buf, 0, len);
                }

            }

        } catch (IOException ex) {
            throw new GenericFailureException("Unable to open file.\n(Error: " + ex.getMessage() + ")");            
        }            
    }
}
