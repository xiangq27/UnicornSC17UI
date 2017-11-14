import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.awt.Color;


import static java.lang.Thread.sleep;

/**
 * Created by qiao on 11/11/17.
 */
public class BandwidthUI {

    final static int CANVAS_WIDTH  = 800;
    final static int CANVAS_HEIGHT = 450;


    final static int WIDTH  = 550;
    final static int HEIGHT = 600;
    final static double MAX_LINE_HEIGHT = HEIGHT * 0.9;
    final static double MAX_LINE_WIDTH = HEIGHT * 0.9;
    final static double MAX_THROUGHPUT = 100;
    final static double NUM_OF_Y_INTERVALS = 10;
    final static double THROUGHPUT_UNIT_WIDTH = MAX_LINE_HEIGHT / MAX_THROUGHPUT;
    final static double TIME_INTERVAL = 2;
    final static double TIME_INTERVAL_WIDTH = 30;
    final static int NUM_OF_X_INTERVALS = (int) (MAX_LINE_WIDTH / TIME_INTERVAL_WIDTH) - 2;
    final static int ORIGIN_OFFSET = 20;
    //final static double NUM_OF_X_INTERVALS = 5;

    private static LinkedList<String> inputList = new LinkedList<>();
    private static LinkedList<int[]> colorList = new LinkedList<>();


    public static void initStdDraw() {
        //Change from the default of 512x512.
        StdDraw.setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

        //Change scale from the default of [0 - 1.0].
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);

        // Draw the coordinate system
        StdDraw.line(0 + ORIGIN_OFFSET, 0, 0 + ORIGIN_OFFSET, HEIGHT);
        StdDraw.line(0 + ORIGIN_OFFSET, 0, WIDTH - ORIGIN_OFFSET, 0);

        //x and y labels
        StdDraw.text(WIDTH-30, 20, "Time (s)");
        StdDraw.text(60 + ORIGIN_OFFSET, HEIGHT-20, "Throughput (Gbps)");

        //y ticks
        StdDraw.text(-5, -5,"0");
        for (int i = 1; i <= MAX_THROUGHPUT/NUM_OF_Y_INTERVALS; i++) {
            StdDraw.textRight(15, i * (MAX_LINE_HEIGHT/NUM_OF_Y_INTERVALS),
                    Double.toString(i*MAX_THROUGHPUT/NUM_OF_Y_INTERVALS));
        }
    }

    public static void paintWhiteCo() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledRectangle((WIDTH-35)/2, MAX_LINE_HEIGHT/2,
                (WIDTH-74)/2-1.3, MAX_LINE_HEIGHT/2-1);
    }

    public static void paintWhiteX() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledRectangle((WIDTH-35)/2, -12,
                (WIDTH-74)/2+8, 10);
    }

    public static void drawColorline(LinkedList<Double> ySeries, int[] rgb) {
        //System.out.println(points.length);
        //StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenColor(rgb[0], rgb[1], rgb[2]);

        for (int i=0; i < ySeries.size()-1; i++) {
            //StdDraw.text(xSeries.get(i) * TIME_INTERVAL_WIDTH, -5, xSeries.get(i).toString());
            StdDraw.line(i * TIME_INTERVAL_WIDTH + ORIGIN_OFFSET,
                    ySeries.get(i) * THROUGHPUT_UNIT_WIDTH,
                    (i+1) * TIME_INTERVAL_WIDTH + ORIGIN_OFFSET,
                    ySeries.get(i+1) * THROUGHPUT_UNIT_WIDTH);
        }
    }

    public static void parseConfig(String configFile) {
        double XLegendPos = ORIGIN_OFFSET+200;


        try {
            FileReader f = new FileReader(new File(configFile));
            BufferedReader b = new BufferedReader(f);

            String newLink = b.readLine();
            while (newLink != null) {
                String[] para = newLink.split("\\s");

                StdDraw.setPenColor(Integer.parseInt(para[0]),
                        Integer.parseInt(para[1]),
                        Integer.parseInt(para[2]));
                StdDraw.line(XLegendPos, HEIGHT-20,
                        XLegendPos+30, HEIGHT-20);
                StdDraw.text(XLegendPos+50, HEIGHT-20, para[3]);

                inputList.add(para[4]);
                colorList.add(new int[] {Integer.parseInt(para[0]),
                        Integer.parseInt(para[1]),
                        Integer.parseInt(para[2])});

                XLegendPos+=80;
                newLink = b.readLine();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printXTick(LinkedList<Long> xSeries) {
        StdDraw.setPenColor(Color.BLACK);

        for (int i=0; i < xSeries.size(); i++) {
            //StdDraw.text(xSeries.get(i) * TIME_INTERVAL_WIDTH, -5, xSeries.get(i).toString());
            StdDraw.text(i * TIME_INTERVAL_WIDTH + ORIGIN_OFFSET,
                    -15,
                    Long.toString(xSeries.get(i)));
        }

    }

    public static void printTime(String time) {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledRectangle(WIDTH-30,
                HEIGHT-100,
                100, 30);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.text(WIDTH-30,
                HEIGHT-100,
                time);
    }


    public static void main(String[] args) {
        //double[][] test={{1, 2}, {30, 20}, {90, 40}};
        //long minX = 0;
        Calendar cal;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");


        if (args.length != 2) {
            System.out.println("Usage:");
            System.out.println("java BandwidthUI -config <config file>");
            return;
        }

        initStdDraw();
        parseConfig(args[1]);

        LinkedList<Long> xSeries = new LinkedList<>();
        @SuppressWarnings("unchecked")
        LinkedList<Double>[] ySeries = new LinkedList[inputList.size()];

        for (int i = 0; i < inputList.size(); i++) {
            ySeries[i] = new LinkedList<>();
        }

        FileReader[] f = new FileReader[inputList.size()];
        BufferedReader[] b = new BufferedReader[inputList.size()];
        String newVal;

        while(true) {
            try {

                if (xSeries.size() == NUM_OF_X_INTERVALS) {
                    paintWhiteCo();
                    paintWhiteX();
                    xSeries.remove();

                    for (int i=0; i < inputList.size(); i++) {
                        ySeries[i].remove();
                    }
                }
                cal = Calendar.getInstance();
                xSeries.add((long)cal.get(Calendar.SECOND));
                //minX = minX + 1;

                for (int i=0; i < inputList.size(); i++) {
                    f[i] = new FileReader(new File(inputList.get(i)));
                    b[i] = new BufferedReader(f[i]);
                    double newY;

                    //ySeries.add(Math.random()*500);
                    newVal = b[i].readLine();
                    if (newVal != null) {
                        newY = Double.parseDouble(newVal);
                    }
                    else {
                        newY = ySeries[i].getLast();
                    }

                    if (newY >= 100) {
                        newY = 99;
                    }
                    ySeries[i].add(newY);
                    drawColorline(ySeries[i], colorList.get(i));
                }

                printXTick(xSeries);
                //System.out.println(sdf.format(cal.getTime()));
                printTime(sdf.format(cal.getTime()));

//                StdDraw.show(TIME_INTERVAL*1000);
                sleep((long) TIME_INTERVAL * 1000);
                //StdDraw.clear();
            }
            catch (Exception e) {
                e.printStackTrace();
                //System.out.println(newVal);
            }
        }

    }
}
