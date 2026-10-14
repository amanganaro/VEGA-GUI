package insilico.vega.gui.utilities;

import insilico.core.tools.utils.FileUtilities;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static insilico.core.tools.utils.GeneralUtilities.executeCommandLine;

public class PythonSetup {

    private static final Logger LOGGER = LogManager.getLogger();
    private Path condaInstallationPath = Paths.get(System.getProperty("user.home"), "vega", "conda");

    public PythonSetup() {}

    public boolean checkConda() throws IOException, InterruptedException {
        boolean result = false;

        if(SystemUtils.IS_OS_WINDOWS){
            result=executeCommandLine(null, "cmd.exe", "/c",
                    "\"" + condaInstallationPath.toAbsolutePath().toString()+"\\python.exe\" " +
                            "\"" + condaInstallationPath.toAbsolutePath().toString()+"\\Scripts\\conda-script.py\" --version");
        }else {
            result = executeCommandLine(null, "bash", "-c",
                    "source " + "\"" + condaInstallationPath.toAbsolutePath().toString()+"/bin/activate" + "\""+
                            " && conda --version");
        }
        return result;
    }

    public void removeLastFolders() throws IOException {
        String folderToRemove = Paths.get(System.getProperty("user.home"), "vega", "python").toString();
        FileUtilities.deleteFolder(folderToRemove);

        if (SystemUtils.IS_OS_WINDOWS) {
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "AppData", "Local", "cddd").toString();
            FileUtilities.deleteFolder(folderToRemove);
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "AppData", "Local", "vega-models").toString();
            FileUtilities.deleteFolder(folderToRemove);
        }
        else if(SystemUtils.IS_OS_LINUX) {
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    ".local", "share", "cddd").toString();
            FileUtilities.deleteFolder(folderToRemove);
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    ".local", "share", "vega-models").toString();
            FileUtilities.deleteFolder(folderToRemove);
        }
        else if(SystemUtils.IS_OS_MAC){
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "Library", "Application Support", "cddd").toString();
            FileUtilities.deleteFolder(folderToRemove);
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "Library", "Application Support", "vega-models").toString();
            FileUtilities.deleteFolder(folderToRemove);
        }
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
        boolean result;
        if(SystemUtils.IS_OS_WINDOWS)
            result = executeCommandLine(null, "cmd.exe", "/c",
                    "start /wait \"\" " + "\"" + condaInstallationPath.toAbsolutePath().toString()
                            + "\\Uninstall-Miniconda3.exe" + "\"" + " /S");
        else
            result = executeCommandLine(null, "bash", "-c",
                    "rm -rf " + "\"" + condaInstallationPath.toAbsolutePath().toString() + "\"");

        return result;
    }
}
