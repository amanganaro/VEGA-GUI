package insilico.vega.launcher;

import insilico.vega.gui.FrameMain;

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
