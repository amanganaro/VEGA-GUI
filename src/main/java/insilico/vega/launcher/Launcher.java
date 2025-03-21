package insilico.vega.launcher;

import insilico.core.ad.ADCheckIndices;
import insilico.vega.gui.FrameMain;
import insilico.vega.gui.resources.VegaVersion;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Properties;

/**
 *
 * @author Alberto Manganaro <a.manganaro@kode-solutions.net>
 */
public class Launcher {
    
    public static void main(String args[]) throws Exception {

//        Properties props = new Properties();
//        props.load(Launcher.class.getResourceAsStream("log4j2.properties"));
//        PropertyConfigurator.configure(props);
        //System.setProperty("log4j.configurationFile", "log4j2.properties");


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
                if (curArg.equalsIgnoreCase("-uninstall"))
                    VegaVersion.UNINSTALL_VEGA = true;

            }
        }

        String logDir = System.getProperty("user.home")+ File.separator+ "vega"+ File.separator + "logs";
        File directory = new File(logDir);
        FileUtils.forceMkdir(directory);

        // Run GUI
        FrameMain.launch();
            
    }   
    
}
