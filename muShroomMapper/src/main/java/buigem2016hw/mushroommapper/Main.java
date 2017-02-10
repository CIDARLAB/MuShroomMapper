//Fluigi flow layer integration Master Revision
package buigem2016hw.mushroommapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.json.JSONException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
//import static org.cidarlab.fluigi.fluigi.Fluigi.processMintDevice;

/**
 *
 * @author Shane
 */

/**
 * To do: Implement Apache Commons CLI` to feed muShroomMapper arguments via
 * Node Command Line
 */
public class Main 
{
    private static String lfrFilePath = null;
    private static String ucfFilePath = null;

    private static Options createCommandLineOptions() 
    {
        final Options options = new Options();
        options.addOption("l", "lfr", true, "give lfr filepath (.v)");
        options.addOption("u", "ucf", true, "give ucf filepath (.json)");

        return options;
    }

    private static void processCommandLine(final CommandLine cl, Options options) {

        if ((null != cl) && cl.hasOption("lfr")) 
        {
            lfrFilePath = cl.getOptionValue("lfr");
            if (null == lfrFilePath) System.exit(ErrorCodes.MISSING_ARG_VALUES);
        }

        if ((null != cl) && cl.hasOption("ucf")) 
        {
            ucfFilePath = cl.getOptionValue("ucf");
            if (null == ucfFilePath) System.exit(ErrorCodes.MISSING_ARG_VALUES);
        }
    }
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws org.json.JSONException
     * @throws buigem2016hw.mushroommapper.Exceptions
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, JSONException, Exceptions 
    {

        //Fluigi Options
        final Options options = createCommandLineOptions();
        CommandLineParser parser = new DefaultParser();

        switch (args.length) {
            case 0:
                //outputCommandLineHelp(options);
                System.exit(ErrorCodes.NO_ARGS);
                break;
            default:
                try {
                    CommandLine cl = parser.parse(options, args);
                    processCommandLine(cl, options);

                } catch (ParseException ex) {
                    System.err.println("Parsing failed. " + ex.getMessage());
                    System.exit(ErrorCodes.BAD_ARGS);
                }
        }

        if (null != ucfFilePath && null != lfrFilePath) 
        {
        //Read in ucf
            //Scanner reader = new Scanner(System.in);  // Reading from System.in
            //System.out.println("Enter path/to/UCFFile: ");
            //String ucfPath = reader.nextLine(); // reads in filepath
            //String ucfPath = "SampleInput/sample.json";     //temp path for debugging
            //System.out.println("Reading UCF...");
            ParsedUCF ucf = new ParsedUCF(ucfFilePath);

        //Read Verilog + Make Graph
            //System.out.println("Enter path/to/VerilogFile: ");
            //String vPath = reader.nextLine(); // reads in filepath
            //String vPath = "SampleInput/sample.v";          //temp path for debugging
            //System.out.println("Reading Verilog...");
            NetListTransition nlt = new NetListTransition(ucf, lfrFilePath);

        //Create JGraphX from netlist Graph while error checking
            /*
            System.out.println("Creating JGraphX, error checking...");
            GraphTranslation gt = new GraphTranslation(nlt, ucf);

        //create a visualization using JGraphX
            Visualization v = new Visualization();
            v.display(gt.jgraphx);
            */
            
        //Create Mint file from netlist graph and parsed ucf
//            System.out.println("Creating Mint file output...");
//            Scanner ufNameInput = new Scanner(System.in);  //Reading from System.in
            //System.out.println("What would you like to name your .uf file? ");
            //String fileName = ufNameInput.nextLine();
            String fileName = "testDevice.uf";
            CreateMint cm = new CreateMint(nlt, ucf, fileName);
            //processMintDevice(fileName, "SampleInput/fluigi.ini", "sej");     //old code from when MM called Fluigi method
            System.out.println("MM is all done!");
            System.exit(0);
        }
    }
}
