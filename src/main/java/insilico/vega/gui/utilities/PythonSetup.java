package insilico.vega.gui.utilities;

import insilico.core.tools.utils.FileUtilities;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class PythonSetup {

    private static final Logger LOGGER = LogManager.getLogger();
    private Path condaInstallationPath = Paths.get(System.getProperty("user.home"), "vega", "conda");
    private String envVariables = condaInstallationPath.toAbsolutePath().toString() + ";" +
            condaInstallationPath.toAbsolutePath().toString()+ File.separator+"Scripts";
    private Map<String, String> env = Map.of("Path", envVariables);

    public PythonSetup(){

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
            result=executeCommandLine(null,"cmd.exe", "/c", "curl https://www.python.org/ftp/python/3.13.0/python-3.13.0-amd64.exe -o python-amd64.exe");
            if(result){
                result=executeCommandLine(null,"cmd.exe", "/c","\"python-amd64.exe\" /passive InstallAllUsers=1 Include_launcher=0 Include_test=0 PrependPath=1 Include_doc=0 && exit");
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
            result = executeCommandLine(null,"cmd.exe", "/c", "curl https://repo.anaconda.com/miniconda/Miniconda3-latest-Windows-x86_64.exe -o miniconda.exe");
            if(result){
                result = executeCommandLine( null,"cmd.exe", "/c", "start /wait \"\" .\\miniconda.exe /InstallationType=JustMe /AddToPath=0 /RegisterPython=0 /S /D="+condaInstallationPath.toAbsolutePath().toString());
                if(result){
                    result = executeCommandLine(null, "cmd.exe", "/c", "del miniconda.exe");
                }
            }
        }else if(SystemUtils.IS_OS_LINUX){
            result = executeCommandLine(null,"bash", "-c", "mkdir -p ~/miniconda3");
            if(result){
                result = executeCommandLine(null,"bash", "-c", "wget https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh " +
                        "-O ~/miniconda3/miniconda.sh && chmod +x  ~/miniconda3/miniconda.sh");
                if(result){
                    result = executeCommandLine(null, "bash","-c", "~/miniconda3/miniconda.sh -b -u -p"+condaInstallationPath.toAbsolutePath().toString());
                    if(result) {
                        result = executeCommandLine(null, "bash", "-c", "rm ~/miniconda3/miniconda.sh");
                    }
                }
            }
        }

        return result;
    }

    public void condaInit() throws IOException, InterruptedException {

        if(SystemUtils.IS_OS_WINDOWS){
            executeCommandLine(env, "cmd.exe", "/c", "conda init");
        }else{
            executeCommandLine(env, "bash", "-c", "conda init bash");
        }

    }

    public boolean checkConda() throws IOException, InterruptedException {
        boolean result = false;

        if(SystemUtils.IS_OS_WINDOWS){
            result=executeCommandLine(env, "cmd.exe", "/c", "conda --version");
        }else {
            result = executeCommandLine(env, "bash", "-c", "conda --version");
        }
        return result;
    }

    public void removeALlPythonFolders() throws IOException {
        String folderToRemove;
        if(SystemUtils.IS_OS_WINDOWS)
             folderToRemove = Paths.get(System.getProperty("user.home"),
                "\\AppData\\Local\\vega-models\\").toString();
        else
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "/.local/share/vega-models/").toString();

        FileUtilities.deleteFolder(folderToRemove);
        if(SystemUtils.IS_OS_WINDOWS)
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "\\AppData\\Local\\cddd\\").toString();
        else
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "/.local/share/cddd/").toString();

        FileUtilities.deleteFolder(folderToRemove);
    }

    public void removeCondaInstallation() throws IOException {
        String folderToRemove;
        if(SystemUtils.IS_OS_WINDOWS)
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "\\vega\\conda\\").toString();
        else
            folderToRemove = Paths.get(System.getProperty("user.home"),
                    "/vega/conda/").toString();

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

        System.out.println(processBuilder.environment());
        if(envVariables != null) {
            envVariables.forEach((key, value) ->
                    processBuilder.environment().put(key, String.valueOf(value)));
        }
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String s = readProcessOutput(process.getInputStream()).toString();
        LOGGER.info("Process builder: {}",s);
        int exitCode = process.waitFor();
        return exitCode == 0;
    }

}
