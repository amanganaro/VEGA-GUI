package insilico.vega.gui.resources;

/**
 *
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class VegaVersion {

    //// internal options

    // all models are selected at start
    public static boolean SET_ALL_CB_SELECTED = false;

    // print list of available models to std out
    public static boolean PRINT_MODEL_LIST_TO_STDOUT = false;

    public static boolean UNINSTALL_VEGA = false;

    public final static String AppName = "VEGA in silico platform";
    public final static String BuildDate = "24/09/2025";
    public final static String Version = "1.2.5b19";
    public final static int VersionMajor = 1;
    public final static int VersionMinor = 2;
    public final static int VersionRevision = 5;
    public final static String[] Libraries = {
        "Chemistry Development Kit (CDK) ver 2.3",
        "OpenPDF ver 1.3",
        "Weka ver 3.5.8",
        "jPMML ver 1.5.6"};
    
    public final static String GUIDE_URL = "/resources/manual-1.2.4.pdf";
}
