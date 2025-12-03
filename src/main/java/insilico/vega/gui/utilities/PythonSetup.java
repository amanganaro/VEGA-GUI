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
import java.util.Arrays;
import java.util.Map;

public class PythonSetup {

    private static final Logger LOGGER = LogManager.getLogger();
    private Path vegaInstallationPath = Paths.get(System.getProperty("user.home"), "vega");
    private Path condaInstallationPath = Paths.get(System.getProperty("user.home"), "vega", "conda");

    public PythonSetup() throws IOException {
        Files.createDirectories(vegaInstallationPath);
    }

    public boolean checkPython() throws IOException, InterruptedException {
        boolean result = false;
        if(SystemUtils.IS_OS_WINDOWS){
            result=executeCommandLine(null, "cmd.exe", "/C", "python --version");
        }else{
            result = executeCommandLine(null, "python", "--version");
        }
        return result;
    }

    public boolean installPython() throws InterruptedException, IOException {
        boolean result = false;
        if(SystemUtils.IS_OS_WINDOWS){
            result=executeCommandLine(null,"cmd.exe", "/c",
                    "curl https://www.python.org/ftp/python/3.13.0/python-3.13.0-amd64.exe -o python-amd64.exe");
            if(result){
                result=executeCommandLine(null,"cmd.exe", "/c",
                        "\"python-amd64.exe\" /passive InstallAllUsers=1 Include_launcher=0 Include_test=0 PrependPath=1 Include_doc=0 && exit");
                if(result){
                    result=executeCommandLine(null, "cmd.exe", "/c", "del python-amd64.exe");
                }
            }
        }
        // for linux/mac users python must be installed by themselves
        else{
            LOGGER.info("Linux user: Install Python by yourself");
        }
        return result;
    }


    public boolean installConda() throws IOException, InterruptedException {
        boolean result = false;


        if(SystemUtils.IS_OS_WINDOWS){

            String pathToMiniconda = Paths.get(vegaInstallationPath.toString(), "miniconda.exe").toAbsolutePath().toString();
            result = executeCommandLine(null,"cmd.exe", "/c", "curl --version");
            if(result)
                result = executeCommandLine(null,"cmd.exe", "/c",
                        "curl --ssl-revoke-best-effort https://repo.anaconda.com/miniconda/Miniconda3-py39_25.5.1-1-Windows-x86_64.exe -o "+
                                "\"" + pathToMiniconda + "\"");
            else
                result = executeCommandLine(null,"cmd.exe", "/c",
                        "wget https://repo.anaconda.com/miniconda/Miniconda3-py39_25.5.1-1-Windows-x86_64.exe -O "+
                                "\"" + pathToMiniconda + "\"");
            if(result){
                result = executeCommandLine( null,"cmd.exe", "/c", "start /wait \"\" " +
                        "\"" + pathToMiniconda + "\"" + " /InstallationType=JustMe /AddToPath=0 /RegisterPython=0 /S /D=" +
                        //here the quotes has not to be inserted as conda /D parameter works without them. Even there are spaces in path
                        condaInstallationPath.toAbsolutePath().toString());
                if(result){
                    result = executeCommandLine(null, "cmd.exe", "/c", "del " + "\""+pathToMiniconda+"\"");
                    if(result){
                        String pathToActivateBat = Paths.get(condaInstallationPath.toString(), "Scripts", "activate.bat").toAbsolutePath().toString();
                        result=executeCommandLine(null, "cmd.exe", "/c",
                                "\""+pathToActivateBat+"\" && conda tos accept");
                    }
                }
            }
            else{
                // problem with download, delete the vega folder
                result = executeCommandLine(null, "cmd.exe", "/c", "del " +
                        vegaInstallationPath.toAbsolutePath().toString());
            }
        }else if(SystemUtils.IS_OS_LINUX){
            result = executeCommandLine(null,"bash", "-c", "mkdir -p ~/vega");
            if(result){
                result = executeCommandLine(null, "bash", "-c", "curl --version");
                if(result)
                    result = executeCommandLine(null,"bash", "-c",
                            "curl --ssl-revoke-best-effort https://repo.anaconda.com/miniconda/Miniconda3-py39_25.5.1-1-Linux-x86_64.sh " +
                                    "-o ~/vega/miniconda.sh && chmod +x  ~/vega/miniconda.sh");
                else
                    result = executeCommandLine(null,"bash", "-c",
                            "wget https://repo.anaconda.com/miniconda/Miniconda3-py39_25.5.1-1-Linux-x86_64.sh " +
                                    "-O ~/vega/miniconda.sh && chmod +x ~/vega/miniconda.sh");
                if(result){
                    result = executeCommandLine(null, "bash","-c",
                            "~/vega/miniconda.sh -b -f -p "+condaInstallationPath.toAbsolutePath().toString());
                    if(result) {
                        result = executeCommandLine(null, "bash", "-c", "rm ~/vega/miniconda.sh");
                        if(result){
                            result = executeCommandLine(null, "bash", "-c",
                                    "source "+"\""+condaInstallationPath.toAbsolutePath().toString()+"/bin/activate" + "\"" +
                                            " && conda tos accept");
                        }
                    }
                }
                else{
                    // problem with download, delete the vega folder
                    result = executeCommandLine(null, "bash", "-c", "rm -r ~/vega");
                }
            }
        }else if(SystemUtils.IS_OS_MAC){
            //controllare se viene usato zsh o bash come default shell
            result = executeCommandLine(null,"bash", "-c", "mkdir -p ~/vega");
            if(result){
                result = executeCommandLine(null, "bash", "-c", "curl --version");
                if(result)
                    result = executeCommandLine(null,"bash", "-c",
                            "curl --ssl-revoke-best-effort https://repo.anaconda.com/miniconda/Miniconda3-py39_25.5.1-1-MacOSX-x86_64.sh " +
                                    "-o ~/vega/miniconda.sh && chmod +x ~/vega/miniconda.sh");
                else
                    result = executeCommandLine(null,"bash", "-c",
                            "wget https://repo.anaconda.com/miniconda/Miniconda3-py39_25.5.1-1-MacOSX-x86_64.sh " +
                                    "-O ~/vega/miniconda.sh && chmod +x ~/vega/miniconda.sh");
                if(result){
                    result = executeCommandLine(null, "bash","-c",
                            "~/vega/miniconda.sh -b -p " + condaInstallationPath.toAbsolutePath().toString());
                    if(result) {
                        result = executeCommandLine(null, "bash", "-c", "rm ~/vega/miniconda.sh");
                        if(result){
                            result = executeCommandLine(null, "bash", "-c",
                                    "source " + "\"" + condaInstallationPath.toAbsolutePath().toString() + "/bin/activate" + "\"" +
                                            " && conda tos accept");
                        }
                    }
                }
                else{
                    // problem with download, delete the vega folder
                    result = executeCommandLine(null, "bash", "-c", "rm -r ~/vega");
                }
            }
        }

        return result;
    }

    public void condaInit() throws IOException, InterruptedException {

        if(SystemUtils.IS_OS_WINDOWS){
            executeCommandLine(null,"cmd.exe", "/c", "conda init");
        }else{
            executeCommandLine(null, "bash", "-c", "conda init bash");
        }

    }

    /*
    * Clean cache of conda, that remove all packages in cache. Also remove conda pkgs folder containing (only for windows)
    * because it contains the packages that are duplicate in each env. This reduces the space by half of original one.
    * Also remove other (apparently) not needed files
    * */
    public void cleanConda() throws IOException, InterruptedException {

        if(SystemUtils.IS_OS_WINDOWS){
            Path p = Paths.get(condaInstallationPath.toAbsolutePath().toString(), "pkgs");
            if(!FileUtils.isEmptyDirectory(new File(p.toString()))){
                FileUtils.cleanDirectory(new File(p.toString()));
            }

            executeCommandLine(null,"cmd.exe", "/c", "\""+condaInstallationPath.toAbsolutePath().toString()
                    +"\\Scripts\\activate.bat" + "\"" + " && conda clean --all --yes");

            executeCommandLine(null,"cmd.exe", "/c", "del /s /q "+
                    "\"" + condaInstallationPath.toAbsolutePath().toString() +"\\*.a"+"\"");
            executeCommandLine(null,"cmd.exe", "/c", "del /s /q " +
                    "\""+condaInstallationPath.toAbsolutePath().toString() +"\\*.js.map"+"\"");

        }else{
            executeCommandLine(null, "bash", "-c",
                    "source " + "\"" + condaInstallationPath.toAbsolutePath().toString()
                            + "/bin/activate" + "\"" +
                            " && conda clean --all --yes");
        }
    }

    public boolean checkConda() throws IOException, InterruptedException {
        boolean result = false;

        if(SystemUtils.IS_OS_WINDOWS){
            result=executeCommandLine(null, "cmd.exe", "/c",
                    "\"" + condaInstallationPath.toAbsolutePath().toString()+"\\Scripts\\activate.bat" + "\"" +
                            " && conda --version");
        }else {
            result = executeCommandLine(null, "bash", "-c",
                    "source " + "\"" + condaInstallationPath.toAbsolutePath().toString()+"/bin/activate" + "\""+
                            " && conda --version");
        }
        return result;
    }

    public void removeALlPythonFolders() throws IOException {
        String folderToRemove = "";
        if(SystemUtils.IS_OS_WINDOWS)
             folderToRemove = Paths.get(System.getProperty("user.home"),
                "\\AppData\\Local\\vega-models\\").toString();
        else if(SystemUtils.IS_OS_LINUX)
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "/.local/share/vega-models/").toString();
        else if(SystemUtils.IS_OS_MAC)
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "/Library/Application Support/vega-models/").toString();

        FileUtilities.deleteFolder(folderToRemove);
        if(SystemUtils.IS_OS_WINDOWS)
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "\\AppData\\Local\\cddd\\").toString();
        else if(SystemUtils.IS_OS_LINUX)
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "/.local/share/cddd/").toString();
        else if(SystemUtils.IS_OS_MAC)
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "/Library/Application Support/cddd/").toString();

        FileUtilities.deleteFolder(folderToRemove);
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

    public void removeLogFolder() throws IOException {
        String folderToRemove = Paths.get(System.getProperty("user.home"), "vega", "logs").toString();
        FileUtilities.deleteFolder(folderToRemove);
    }

    private StringBuilder readProcessOutput(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output;
    }

    private boolean executeCommandLine(Map<String, String> envVariables, String... commands) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        String commandString = String.join(" ", commands);
        LOGGER.info("Process builder command: {}", commandString);

        if(envVariables != null) {
            envVariables.forEach((key, value) ->
                    processBuilder.environment().put(key, String.valueOf(value)));
        }
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String s = readProcessOutput(process.getInputStream()).toString();
        LOGGER.info("Process builder result: {}",s);
        int exitCode = process.waitFor();
        return exitCode == 0;
    }

}
