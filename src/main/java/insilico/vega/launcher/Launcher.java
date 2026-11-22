package insilico.vega.launcher;

import insilico.core.ad.ADCheckIndices;
import insilico.core.tools.utils.VegaFolders;
import insilico.core.tools.utils.VegaFolders.VegaFolder;
import insilico.vega.gui.FrameMain;
import insilico.vega.gui.resources.VegaVersion;

/**
 *
 * @author Alberto Manganaro <a.manganaro@kode-solutions.net>
 */
public class Launcher {

    /** System property read by log4j2.properties for the log folder. */
    private static final String LOG_FOLDER_PROPERTY = "vega.logs";

    /** System property read by PadelAdapter (LocalJar) for the folder of the PaDEL service. */
    private static final String PADEL_FOLDER_PROPERTY = "vega.padel.folder";

    public static void main(String args[]) throws Exception {

        // Local folders first, before anything can initialize log4j or start the PaDEL service: both read their
        // folder from a system property, set here from the single VEGA root
        System.setProperty(LOG_FOLDER_PROPERTY, VegaFolders.ensure(VegaFolder.LOGS).toString());
        if (System.getProperty(PADEL_FOLDER_PROPERTY) == null) {
            System.setProperty(PADEL_FOLDER_PROPERTY, VegaFolders.get(VegaFolder.PADEL).toString());
        }

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

        // Run GUI
        FrameMain.launch();
            
    }   
    
}
