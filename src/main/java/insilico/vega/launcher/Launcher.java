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
        
//        VegaModelsWrapper list = new VegaModelsWrapper();
        
//        JFrame f = new JFrame();
//        f.add(new PanelModelList(f));
//        f.setVisible(true);
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Run GUI
        FrameMain.launch();
            
    }   
    
}
