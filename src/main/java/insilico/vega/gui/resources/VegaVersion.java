package insilico.vega.gui.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

    public static boolean USE_PYTHON_MODELS = false;

    public final static String AppName = "VEGA in silico platform";
    /** Build properties written by Maven, see src/main/resources/vega-build.properties. */
    private final static Properties BUILD = loadBuildProperties();

    /** Build date, set by Maven at packaging time. */
    public final static String BuildDate = BUILD.getProperty("build.date", "unknown");

    /** Full version as in the pom, including any beta qualifier (e.g. 1.2.9-b1). */
    public final static String Version = BUILD.getProperty("version", "unknown");

    public final static String[] Libraries = {
        "Chemistry Development Kit (CDK) ver 2.3",
        "OpenPDF ver 1.3",
        "Weka ver 3.5.8",
        "jPMML ver 1.5.6"};
    
    public final static String GUIDE_URL = "/resources/manual-1.2.6.pdf";

    private static Properties loadBuildProperties() {
        Properties properties = new Properties();
        try (InputStream in = VegaVersion.class.getResourceAsStream("/vega-build.properties")) {
            if (in != null)
                properties.load(in);
        } catch (IOException ignored) {
            // the defaults of the getters are used
        }
        return properties;
    }
}
