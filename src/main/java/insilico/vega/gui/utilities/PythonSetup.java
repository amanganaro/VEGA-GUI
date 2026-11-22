package insilico.vega.gui.utilities;

import insilico.core.python.Communication;
import insilico.core.tools.utils.VegaFolders;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;

/**
 * Python-related checks done by the GUI at start-up and at uninstallation. The paths of the local folders come from
 * {@link VegaFolders}; the conda operations are those of the Core {@link Communication}.
 */
public class PythonSetup {

    public PythonSetup() {}

    public boolean checkConda() throws IOException, InterruptedException {
        return new Communication().checkConda();
    }

    /**
     * Last step of the uninstallation: deletes everything VEGA installed under its root folder, logs included (log4j
     * has already been shut down when this is called). Files that are not part of the VEGA layout are left alone.
     */
    public void removeLastFolders() throws IOException {
        VegaFolders.removeAll(true);
    }

    public boolean checkIfThereAreCondaPackEnvs(){
        if(SystemUtils.IS_OS_WINDOWS) {
            InputStream isGlobal = getClass().getResourceAsStream("/condapack-env/VEGA_global_V3.zip");
            InputStream isCddd = getClass().getResourceAsStream("/condapack-env/VEGA_cddd_V2.zip");
            return isCddd != null && isGlobal != null;
        }
        return false;
    }

    public boolean removeCondaInstallation() throws IOException, InterruptedException {
        return new Communication().removeCondaInstallation();
    }
}
