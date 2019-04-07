import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;

import static java.lang.Thread.sleep;


/**
 * Created by qiao on 11/12/17.
 */
public class UnicornGUI extends JPanel {
    private final static int CANVAS_WIDTH = 1300;
    private final static int CANVAS_HEIGHT = 900;
    private final static int TEXT_PANE_WIDTH = 600;
    private final static int TEXT_PANE_HEIGHT = 600;
    private final static int BUTTON_WIDTH = 150;
    private final static int BUTTON_HEIGHT = 50;
    private final static Font TITLE_FONT = new Font("Calibri", Font.BOLD,32);
    private final static Font BUTTON_FONT = new Font("Calibri", Font.BOLD,20);


    private final static int NUM_OF_MONITOR = 4;
    public final static int COMPUTED = 1;
    public final static int UPDATED = 2;

    //TODO: SMPC does not need a route right now, only use as a place holder
    //TODO: schedule is not defined yet
    private final static String[] pollingRoute = {"/path_complete_lookup/",
                                                "/resource_complete_lookup/",
                                                "/resource_complete_lookup/",
                                                "/scheduling_complete_lookup/"};
    //TODO: SMPC does not need a route right now, only use as a place holder
    //TODO: schedule is not defined yet
    private final static String[] getRoute = {"/task_lookup/",
                                            "/resource_lookup/",
                                            "/resource_lookup/",
                                            "/scheduling_result_lookup/"};
    private final static String[] getRouteType = {"task", "resource", "smpc", "scheduling"};

    //private final static String[] type = {"task", "resource", "task", "scheduling"};
    private final static String[] displayHead = {"Path query",
                                                "Resource query",
                                                "SMPC",
                                                "Scheduling"};



    private static boolean SUBMITTED = false;


    public static StyledDocument doc;
    private static JFrame frame;
    private static JToolBar toolBar = new JToolBar("Unicorn GUI ToolBar");
    private static JButton clearButton;
    private static JButton submitButton;
    private static JButton stopAllButton;
    private static JButton showPathQueryResultButton;
    private static JButton showResourceQueryResultButton;
    private static JButton showSMPCResultButton;
    private static JButton showSchedulingButton;
    private static JButton startLinkBWButton;


    //polling files
    private static String[] monitorFiles = new String[NUM_OF_MONITOR];
    private static String[] resultDirs = new String[NUM_OF_MONITOR];
    private static String linkBWProgCommand ;


    private static String taskInput;
    private static String orchestratorURL;
    private static int taskID = 1;

    private static ArrayList<PollingThread> pollingPool = new ArrayList<>();

    //polling exit displays
    private static String[] computeDisp = {"Path query is finished!\n",
            "Resource query is finished!\n",
            "Secure multi-party computation is finished!\n",
            "Scheduling is finished!\n"};

    private static String[] updateDisp = {"Path query is updated!\n",
            "Resource query is updated!\n",
            "Secure multi-party computation is updated!\n",
            "Scheduling is updated!\n"};


    private static JButton initButton(String buttonTitle) {
        JButton button = new JButton(buttonTitle);
        button.setFont(BUTTON_FONT);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setLayout(null);
        button.setBounds(600, 400, BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        return button;
    }


    private static void initToolBar() {
        //toolBar.addSeparator(new Dimension(50, 0));
        toolBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        submitButton = initButton("Start");
        submitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("Start.");
                submitTask();
            }
        });
        toolBar.add(submitButton);

        stopAllButton = initButton("Stop");
        stopAllButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("Stop.");
                stopAll();
            }
        });
        toolBar.add(stopAllButton);

        //clearButton = new JButton("Clear");
        //clearButton.setLayout(null);
        //clearButton.setBounds(600, 400, BUTTON_WIDTH, BUTTON_HEIGHT);
        //clearButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        clearButton = initButton("Clear");
        clearButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("Clear everything.");
                clearAllOutput();
            }
        });
        toolBar.add(clearButton);

        //"<html><center>Show<br />Throughput</html>"); for future reference
        startLinkBWButton = initButton("Throughput");
        startLinkBWButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("Show throughput.");
                startLinkBWProgam();
            }
        });
        toolBar.add(startLinkBWButton);


        //clearButton.setLocation(600, 400);
/*
        showPathQueryResultButton = new JButton("Get path query results");
        showPathQueryResultButton.setBounds(200, 100, BUTTON_WIDTH, BUTTON_HEIGHT);
        showPathQueryResultButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));*/
        showPathQueryResultButton = initButton("Domain Path");
        showPathQueryResultButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("Print path query results.");
                String[] test = {"test.txt"};
                getPathQueryResult(test);
            }
        });
        toolBar.add(showPathQueryResultButton);

        showResourceQueryResultButton = initButton("ReSA");
        showResourceQueryResultButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("Print resource query results.");
                String[] test = {"test.txt"};
                getResourceQueryResult(test);
            }
        });
        toolBar.add(showResourceQueryResultButton);

        showSMPCResultButton = initButton("SMPC ReSA");
        showSMPCResultButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("Print SMPC results.");
                String[] test = {"test.txt"};
                getSMPCResult(test);
            }
        });
        toolBar.add(showSMPCResultButton);

        showSchedulingButton = initButton("Schedule");
        showSchedulingButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("Print scheduling results.");
                String[] test = {"test.txt"};
                getSchedulingResult(test);
            }
        });
        toolBar.add(showSchedulingButton);

        //showPathQueryResultButton.setLocation(600, 300);
        //frame.setLayout(new BorderLayout());

        frame.add(toolBar, BorderLayout.PAGE_START);

    }

    public UnicornGUI() {
        initToolBar();

        setLayout(new BorderLayout());

        //Create a text pane.
        JTextPane textPane = createTextPane();
        //textPane.setSize(new Dimension(TEXT_PANE_WIDTH, TEXT_PANE_HEIGHT));
        JScrollPane paneScrollPane = new JScrollPane(textPane);
        paneScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        paneScrollPane.setPreferredSize(new Dimension(TEXT_PANE_WIDTH, TEXT_PANE_HEIGHT));
        paneScrollPane.setMinimumSize(new Dimension(10, 10));

        JPanel textPanePanel = new JPanel(new BorderLayout());
        textPanePanel.add(paneScrollPane, BorderLayout.PAGE_START);

        //set the border and the title of output panel
        Border outputBodyBorder = BorderFactory.createEmptyBorder(10,10,20,20);
        TitledBorder outputTitleBorder = BorderFactory
                .createTitledBorder(outputBodyBorder,
                    "Unicorn Demo Output",
                    TitledBorder.CENTER,
                    TitledBorder.TOP,
                        TITLE_FONT);
        textPanePanel.setBorder(BorderFactory
                .createCompoundBorder(outputTitleBorder, outputBodyBorder));
        //textPanePanel.setPreferredSize(new Dimension(TEXT_PANE_WIDTH, TEXT_PANE_HEIGHT));

        //setBounds(0, 0, TEXT_PANE_WIDTH, TEXT_PANE_HEIGHT);
        add(textPanePanel, BorderLayout.PAGE_START);//, BorderLayout.CENTER);

    }

    private static void flushMonitorFiles() {

        try {
            for (int i = 0; i < monitorFiles.length; i++) {
                FileWriter f = new FileWriter(monitorFiles[i]);
                BufferedWriter bw = new BufferedWriter(f);

                bw.write("0");
                bw.close();
                f.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void submitTask() {
        if (!SUBMITTED) {

            flushMonitorFiles();

            String s = "The following task is submitted to Unicorn:\n";
            display(s, "largeBold");
            try {
        //        File f = new File(taskInput);
          //      Scanner sc = new Scanner(f);
            //    String task = sc.useDelimiter("\\Z").next();
              //  display(task + "\n", "regular");
                SUBMITTED = true;
                //sc.close();
				//String command = "./submit.sh";

                String jsonContent = new Scanner(new File(taskInput)).useDelimiter("\\Z").next();

                String[] command = new String[]{"curl", "-v", orchestratorURL + "/task",
                        "-H", "Content-type: application/json", "-d", "[" + jsonContent + "]"};

                //System.out.println(command);
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();


                //System.out.println(taskInput);
                String parseTaskCom = "python parseTaskJson.py -input "+taskInput+" -output ./task";
                Process parseTaskpro = Runtime.getRuntime().exec(parseTaskCom);
                parseTaskpro.waitFor();

                String content = new Scanner(new File("./task"))
                                    .useDelimiter("\\Z").next();
                doc.insertString(doc.getLength(), content, doc.getStyle("regular"));



                //start polling threads
                for (int i = 0; i < NUM_OF_MONITOR; i++) {
                    //String pollURL = orchestratorURL+pollingRoute[i]+Integer.toString(taskID);
                    if (i ==2)
                        continue;

                    PollingThread pollingThread = new PollingThread(taskID,
                            orchestratorURL+pollingRoute[i], monitorFiles[i],
                            //type[i], monitorFiles[i],
                            orchestratorURL+getRoute[i], resultDirs[i],
                            displayHead[i]);
                            //computeDisp[i]);
                    pollingPool.add(pollingThread);
                    pollingThread.start();
                }



                //p.waitFor();



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            String s = "The task is already submitted to Unicorn, please stop first.\n";
            display(s, "largeBold");
        }

    }

    private static void stopAll() {
        if (SUBMITTED) {
            //TODO: stop orchestrator, fdt, server(maybe?)

            for (PollingThread p : pollingPool) {
                p.interrupt();
            }

            display("The task is stopped.\n", "largeBold");
            SUBMITTED = false;
        }
        else {
            display("No task is running.\n", "largeBold");
        }
    }


    private static void clearAllOutput() {
        try {
            doc.remove(0, doc.getLength());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getPathQueryResult(String[] fileNames) {
        String[] command = {"python2", "url_parser.py", "--url="+orchestratorURL+getRoute[0]
                +Integer.toString(taskID), "--type", getRouteType[0],
                "--output", "./"+getRouteType[0]+"-result"};
        //System.out.println(Arrays.toString(command));
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            String content = new Scanner(new File("./"+getRouteType[0]+"-result"))
                                .useDelimiter("\\Z").next();
            doc.insertString(doc.getLength(), content, doc.getStyle("regular"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getResourceQueryResult(String[] fileNames) {
        String[] command = {"python2", "url_parser.py", "--url="+orchestratorURL+getRoute[1]
                +Integer.toString(taskID), "--type", getRouteType[1],
                "--output", "./"+getRouteType[1]+"-result"};
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            String content = new Scanner(new File("./"+getRouteType[1]+"-result"))
                    .useDelimiter("\\Z").next();
            doc.insertString(doc.getLength(), content, doc.getStyle("regular"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getSMPCResult(String[] fileNames) {

    }

    private static void getSchedulingResult(String[] fileNames) {
        String[] command = {"python2", "url_parser.py", "--url="+orchestratorURL+getRoute[3]
                +Integer.toString(taskID), "--type", getRouteType[3],
                "--output", "./"+getRouteType[3]+"-result"};

        System.out.println(Arrays.toString(command));
        
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            String content = new Scanner(new File("./"+getRouteType[3]+"-result"))
                    .useDelimiter("\\Z").next();
            doc.insertString(doc.getLength(), content, doc.getStyle("regular"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startLinkBWProgam() {
        //Thread t = new Thread(() -> {
            System.out.println(linkBWProgCommand);
            try {
                Process p = Runtime.getRuntime().exec(linkBWProgCommand);
                //p.waitFor();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        //});
//        t.start();
    }


    private static JTextPane createTextPane() {
        JTextPane textPane = new JTextPane();
        doc = textPane.getStyledDocument();
        addStylesToDocument(doc);
        return textPane;
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Unicorn Graphic User Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        //Add content to the window.

        frame.add(new UnicornGUI());
        //frame.setLayout(new FlowLayout());
        //frame.add(clearButton, BorderLayout.PAGE_END);
        //frame.add(getPathQueryResultButton);//, BorderLayout.LINE_START);

        //Display the window.
        frame.pack();
        frame.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    protected static void addStylesToDocument(StyledDocument doc) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontSize(regular, 16);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        Style large = doc.addStyle("large", regular);
        StyleConstants.setFontSize(large, 20);

        s = doc.addStyle("largeBold", large);
        StyleConstants.setBold(s, true);

        /*s = doc.addStyle("icon", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon pigIcon = createImageIcon("images/Pig.gif",
                "a cute pig");
        if (pigIcon != null) {
            StyleConstants.setIcon(s, pigIcon);
        }

        s = doc.addStyle("button", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon soundIcon = createImageIcon("images/sound.gif",
                "sound icon");
        JButton button = new JButton();
        if (soundIcon != null) {
            button.setIcon(soundIcon);
        } else {
            button.setText("BEEP");
        }
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(0,0,0,0));
        button.setActionCommand(buttonString);
        button.addActionListener(this);
        StyleConstants.setComponent(s, button);*/
    }

    public static void display(String s, String font) {
        try {
            doc.insertString(doc.getLength(), s, doc.getStyle(font));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void parseConfig(String configFile) {
        try {
            FileReader f = new FileReader(configFile);
            BufferedReader br = new BufferedReader(f);

            String currentLine;

            Process p = Runtime.getRuntime().exec("./getBW.sh");

            while ((currentLine = br.readLine()) != null) {
                System.out.println(currentLine);
                if (currentLine.equals("task config")) {
                    taskInput = br.readLine();
                } else if (currentLine.equals("orchestrator url")) {
                    orchestratorURL = br.readLine();
                } else if (currentLine.equals("path query")) {
                    monitorFiles[0] = br.readLine();
                    resultDirs[0] = br.readLine();
                } else if (currentLine.equals("resource query")) {
                    monitorFiles[1] = br.readLine();
                    resultDirs[1] = br.readLine();
                } else if (currentLine.equals("smpc")) {
                    monitorFiles[2] = br.readLine();
                    resultDirs[2] = br.readLine();
                } else if (currentLine.equals("schedule")) {
                    monitorFiles[3] = br.readLine();
                    resultDirs[3] = br.readLine();
                } else if (currentLine.equals("link bandwidth app")) {
                    linkBWProgCommand = br.readLine();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java UnicornGUI -config <config file name>");
            return;
        }

        parseConfig(args[1]);

        //String[] test = {"test.txt"};
        //Schedule a job for the event dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
                //getPathQueryResult(test);
            }
        });

        //UnicornGUI dialog = new UnicornGUI(new JFrame(), "hello JCGs", "This is a JDialog example");
        // set the size of the window
        //dialog.setSize(300, 150);
    }




}
