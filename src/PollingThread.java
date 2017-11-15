
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
//import UnicornGUI;

/**
 * Created by qiao on 11/13/17.
 */
public class PollingThread extends Thread {
    private final static int POLL_PERIOD = 1000;

    //private final static int COMPUTED = 1;
    //private final static int UPDATED = 2;

    private int taskID;
    private String pollURL;
    //private String type;
    private String pollFile;
    private String getURL;
    private String getDir;
    private String display;
    private boolean complete = false;
    //private boolean latest = false;

    //private int status = 0; // 0: incomplete;
                            // 1: completed and sent to doc;
                            // 2: updated and need to send to doc

    private long timeStamp = 0;
    //private long currentTimeStamp = 0;

    public PollingThread(int taskID, String pollURL, String pollFile,
                         String getURL, String getDir,
                         String display) {
        this.taskID = taskID;
         this.pollURL = pollURL;
        this.pollFile = pollFile;
        //this.type = type;
        this.getURL = getURL;
        this.getDir = getDir;
        this.display = display;
    }

    @Override
    public void run() {
        String command = "./getStatus.sh "+this.pollURL+" "+Integer.toString(this.taskID);
        //String[] command = new String[]{"bash", "url_parser.py", "--url", this.getURL + Integer.toString(this.taskID), "--type", this.type, "--output", this.pollFile};
        while (true) { // repeat until gets killed
            try {
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();

                FileReader f = new FileReader(pollFile);
                BufferedReader br = new BufferedReader(f);
                String currentLine;
                while ((currentLine = br.readLine()) != null) {
                    //System.out.println(currentLine);
                    if (currentLine.equals("1")) {
                        if (this.complete == false) {
                            this.complete = true;
                            this.timeStamp = Long.parseLong(br.readLine());
                            UnicornGUI.display(this.display+" finishes, please view.\n", "largeBold");
                        }
                        else {//already complete, only need to test timestamp
                            Long newStamp = Long.parseLong(br.readLine());
                            if (newStamp > this.timeStamp) {
                                this.timeStamp = newStamp;
                                UnicornGUI.display(this.display+" results are updated, please view.\n", "largeBold");
                                //TODO: actually display the updated result proactively. Or wait a click?
                            }
                        }
                    }
                }

                br.close();
                f.close();

                Thread.sleep(POLL_PERIOD);

                if (this.isInterrupted()) {
                    throw new InterruptedException();
                }
            }
            catch (FileNotFoundException f) {
                f.printStackTrace();
                continue;
            }
            catch (InterruptedException g) {
                 return;
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        //System.out.println("polling for "+this.targetFile+" for value "
                //+Integer.toString(this.targetValue)+" exits");
        //return;
    }
}
