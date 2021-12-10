package insilico.vega.launcher;

import insilico.core.model.InsilicoModel;
import insilico.core.model.InsilicoModelOutput;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.nrf2_up.ismNRF2Up;
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
