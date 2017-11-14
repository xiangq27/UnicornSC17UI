import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Created by qiao on 11/11/17.
 */
public class MockBandwidthInput {


    public static void main(String[] args) {

        if (args.length != 6) {
            System.out.println("Usage:");
            System.out.println("java MockBandwidthInput -file <file name> " +
                    "-minimum <minimal bw> " +
                    "-maximum <maximal bw> ");
            return;
        }

        while(true) {
            try {
                FileWriter f = new FileWriter(new File(args[1]));
                FileWriter g = new FileWriter(new File(args[1]+"1"));
                FileWriter h = new FileWriter(new File(args[1]+"2"));

                BufferedWriter b = new BufferedWriter(f);
                BufferedWriter c = new BufferedWriter(g);
                BufferedWriter d = new BufferedWriter(h);

                int randomNum = Integer.parseInt(args[3])
                                + (int)(Math.random() * (Integer.parseInt(args[5])-Integer.parseInt(args[3])));

                System.out.println(Integer.toString(randomNum));

                b.write(Integer.toString(randomNum));
                c.write(Integer.toString(randomNum/2));
                d.write(Integer.toString(randomNum/3));
                b.close();
                c.close();
                d.close();
                f.close();
                g.close();
                h.close();

                //Process p = Runtime.getRuntime().exec(
                        //"cat "+args[1]+" > ./debug.txt");
                //p.waitFor();

                sleep(1000);
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}
