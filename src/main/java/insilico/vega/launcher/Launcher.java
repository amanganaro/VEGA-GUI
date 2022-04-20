package insilico.vega.launcher;

import insilico.core.ad.ADCheckIndices;
import insilico.vega.gui.FrameMain;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Alberto Manganaro <a.manganaro@kode-solutions.net>
 */
public class Launcher {
    
    public static void main(String args[]) throws Exception {

        // to avoid blurring of images in labels
        System.setProperty("sun.java2d.uiScale", "1.0");

        //
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-adtrain")) {
                ADCheckIndices.setOnlyFromTraining(true);
            }
        }

        // Run GUI
        FrameMain.launch();
            
    }   
    
}
