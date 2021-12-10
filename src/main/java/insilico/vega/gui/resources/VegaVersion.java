package insilico.vega.gui.resources;

/**
 *
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class VegaVersion {

    //// internal options

    // all models are selected at start
    public final static boolean SET_ALL_CB_SELECTED = true;

    // print list of available models to std out
    public final static boolean PRINT_MODEL_LIST_TO_STDOUT = false;

    public final static String AppName = "VEGA in silico platform";
    public final static String BuildDate = "05/11/2020";
    public final static String Version = "1.1.5-b44"; 
    public final static int VersionMajor = 1;
    public final static int VersionMinor = 1;
    public final static int VersionRevision = 5;
    public final static String[] Libraries = {
        "Chemistry Development Kit (CDK) ver 1.4.9",
        "iText ver 2.1.4",
        "Weka ver 3.5.8",
        "HttpClient (Apache HttpComponents) ver 4.1.3",
        "jPMML ver 1.3.6"};
    
    public final static String GUIDE_URL = "/insilico/vega/gui/resources/manual-1.1.5b.pdf";    
    
}
