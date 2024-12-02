package insilico.vega.launcher;

import insilico.core.ad.ADCheckIndices;
import insilico.vega.gui.FrameMain;
import insilico.vega.gui.resources.VegaVersion;

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

        // don't recall why we made this option...
        if (args.length > 0) {
            for (String curArg : args) {
                if (curArg.equalsIgnoreCase("-adtrain"))
                    ADCheckIndices.setOnlyFromTraining(true);
                if (curArg.equalsIgnoreCase("-all"))
                    VegaVersion.SET_ALL_CB_SELECTED = true;
                if (curArg.equalsIgnoreCase("-print"))
                    VegaVersion.PRINT_MODEL_LIST_TO_STDOUT = true;

            }
        }

        // Run GUI
        FrameMain.launch();
            
    }   
    
}
