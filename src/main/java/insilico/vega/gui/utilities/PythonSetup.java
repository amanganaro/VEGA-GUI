package insilico.vega.gui.utilities;

import insilico.vega.gui.FrameMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class PythonSetup {

    boolean isWindows;

    public PythonSetup(){
        isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    public boolean checkPython() throws IOException, InterruptedException {
        boolean result = false;
        if(isWindows){
            result=executeCommandLine(null, "cmd.exe", "/C", "python --version");
        }else{
            result = executeCommandLine(null, "python", "--version");
        }
        return result;
    }

    public boolean installPython() throws InterruptedException, IOException {
        boolean result = false;
        if(isWindows){
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
            java.util.logging.Logger.getLogger(PythonSetup.class.getName()).log(java.util.logging.Level.INFO,  "Linux user: Install Python by yourself");
        }
        return result;
    }


    public boolean installConda() throws IOException, InterruptedException {
        boolean result = false;

        if(isWindows){
            result = executeCommandLine(null,"cmd.exe", "/c", "curl https://repo.anaconda.com/miniconda/Miniconda3-latest-Windows-x86_64.exe -o miniconda.exe");
            if(result){
                result = executeCommandLine( null,"cmd.exe", "/c", "start /wait \"\" .\\miniconda.exe /InstallationType=JustMe /AddToPath=1 /RegisterPython=0 /S");
                if(result){
                    result = executeCommandLine(null, "cmd.exe", "/c", "del miniconda.exe");
                    /*if(result){
                        result = executeCommandLine(null, "cmd.exe", "/C", "conda init");
                    }*/
                }
            }

        }else {
            result = executeCommandLine(null,"bash", "-c", "mkdir -p ~/miniconda3");
            if(result){
                result = executeCommandLine(null,"bash", "-c", "wget https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh " +
                        "-O ~/miniconda3/miniconda.sh && chmod +x  ~/miniconda3/miniconda.sh");
                if(result){
                    result = executeCommandLine(null, "bash","-c", "~/miniconda3/miniconda.sh -b -u -p ~/miniconda3");
                    if(result) {
                        result = executeCommandLine(null, "bash", "-c", "rm ~/miniconda3/miniconda.sh");
                        /*if(result){
                            result = executeCommandLine(null, "bash", "-c", "~/miniconda3/bin/conda init bash");
                        }*/
                    }
                }
            }
        }

        return result;
    }

    public void condaInit() throws IOException, InterruptedException {

        if(isWindows){
            executeCommandLine(null, "cmd.exe", "/c", "del miniconda.exe");
        }else{
            executeCommandLine(null, "bash", "-c", "conda init bash");
        }

    }

    public boolean checkConda() throws IOException, InterruptedException {
        boolean result = false;

        if(isWindows){
            result=executeCommandLine(null, "cmd.exe", "/c", "conda --version");
        }else {
            result = executeCommandLine(null, "bash", "-c", "conda --version");
        }
        return result;
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

        if(envVariables != null) {
            Map<String, String> env = processBuilder.environment();
            envVariables.forEach((key, variables) ->
                    env.compute(key, (k, currentPath) ->
                            variables + (currentPath != null ? currentPath : "")));
        }

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String s = readProcessOutput(process.getInputStream()).toString();
        java.util.logging.Logger.getLogger(PythonSetup.class.getName()).log(Level.INFO, "Process builder: "+s);
        int exitCode = process.waitFor();
        return exitCode == 0;
    }

}
