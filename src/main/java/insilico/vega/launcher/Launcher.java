package insilico.vega.launcher;

import insilico.vega.gui.FrameMain;
import insilico.vega.gui.PanelEndpointGroup;
import insilico.vega.gui.PanelModelList;
import insilico.vega.gui.models.VegaModelsWrapper;
import javax.swing.JFrame;

/**
 *
 * @author Alberto Manganaro <a.manganaro@kode-solutions.net>
 */
public class Launcher {
    
    public static void main(String args[]) throws Exception {

        // to avoid blurring of images in labels
        System.setProperty("sun.java2d.uiScale", "1.0");

        // Run GUI
        FrameMain.launch();
            
    }   
    
}
