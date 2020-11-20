package insilico.vega.gui.utilities;

import insilico.vega.gui.FrameMain;
import insilico.vega.gui.resources.VegaVersion;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;

/**
 *
 * @author amanganaro
 */
public class UpdatesReader extends Thread {
    
    private static final String UPDATE_URL = "http://amcc.it/vega_download/updates.dat";

    private JFrame ParentFrame;
    
    private int MajorVersion;
    private int MinorVersion;
    private int Revision;
    private ArrayList<String> WhatsNew;
    
    
    public UpdatesReader(JFrame Parent) {
        
        MajorVersion = -1;
        MinorVersion = -1;
        Revision = -1;
        WhatsNew = new ArrayList<String>();
        
        ParentFrame = Parent;
    }
    
    
    @Override
    public void run() {
//
//        try {
//
//            // Reads updates through HTTP 
//            
//            String HttpResults = "";
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpGet httpget = new HttpGet(UPDATE_URL);
//            HttpResponse response = httpclient.execute(httpget);
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                HttpResults = EntityUtils.toString(entity);
//            } else throw new Exception("no HTTP response");
//            
//            
//            // Parses obtained results
//            
//            BufferedReader br = new BufferedReader(new StringReader(HttpResults));
//            String s;
//            int idx = 0;
//            WhatsNew.clear();
//            
//            while ((s = br.readLine()) != null) {
//                switch (idx) {
//                    case 0:
//                        MajorVersion = Integer.valueOf(s);
//                        break;
//                    case 1:
//                        MinorVersion = Integer.valueOf(s);
//                        break;
//                    case 2:
//                        Revision = Integer.valueOf(s);
//                        break;
//                    default:
//                        WhatsNew.add(s);
//                }
//                idx++;
//            }
//            
//            
//            // Checks if there are available updates
//            
//            boolean UpdateFound = false;
//            if (MajorVersion > VegaVersion.VersionMajor) {
//                UpdateFound = true;
//            } else if ((MajorVersion == VegaVersion.VersionMajor) && (MinorVersion > VegaVersion.VersionMinor)) {
//                UpdateFound = true;
//            } else if ((MajorVersion == VegaVersion.VersionMajor) && (MinorVersion == VegaVersion.VersionMinor) && (Revision > VegaVersion.VersionRevision)) {
//                UpdateFound = true;
//            }
//            
//            if (UpdateFound) {
//                String msg = "A new version of the application (" +
//                        MajorVersion + "." + MinorVersion + "." +
//                        Revision + ") is available.\n\n";
//                if (!WhatsNew.isEmpty())
//                    for (int i=0; i<WhatsNew.size(); i++)
//                        msg += WhatsNew.get(i) + "\n";
//                JOptionPane.showMessageDialog(ParentFrame, msg);            
//            }
//            
//        } catch (Exception e) {
//            // do nothing
//        }
    }
            
    
    
    public int GetMajorVersion() {
        return MajorVersion;
    }

    public int GetMinorVersion() {
        return MinorVersion;
    }

    public int GetRevision() {
        return Revision;
    }

    public ArrayList<String> GetWhatsNew() {
        return WhatsNew;
    }
    
}
