package buigem2016hw.mushroommapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.cellocad.BU.netsynth.Utilities;
import org.json.JSONException;
import org.apache.commons.cli.Options;
import org.cidarlab.fluigi.fluigi.Fluigi;

/**
 *
 * @author Shane
 */

/**
 * To do: Implement Apache Commons CLI` to feed muShroomMapper arguments via Node Command Line
 */
public class Main {

    /**
    //these three from fluigi CLI
    private static String verilogFilePath = null;
    private static String outputFormat = "DEFAULT";
    private static String paramPathName = null;    
    
    private static Options createCommandLineOptions() {
        final Options options = new Options();
        
        // * Fluigi Options
        options.addOption("i", "init", true, "Give the initialization (*.ini) file, 3D initalization (*_3d.ini) file.");
        options.addOption("o", "out", true, "Specify output format (json,eps,svg)");
        options.addOption("h", "help", false, "Show help information.");
        options.addOption("v", "hdl", false, "This enables the verilog file input mode flag");
        options.addOption("d", "debug", false, "This enables all the debug printing");
        
        return options;
    }
    
    private static void outputCommandLineHelp(final Options options) 
    {
        final HelpFormatter formater = new HelpFormatter();
       formater.printHelp("Usage: fluigi <filename> [-i <initialization_file>] [-o output format]", options);
    }
    
    private static void processCommandline(final CommandLine cl, Options options) throws IllegalArgumentException 
    {
        /**
         * Fluigi processCommandline
        String inputPathName = cl.getArgs()[0];

        System.out.println(inputPathName);

        if ((null != cl) && cl.hasOption("init")) {
            paramPathName = cl.getOptionValue("init");
            if (null == paramPathName) {
                System.exit(ErrorCodes.MISSING_ARG_VALUES);
            }
        }

        if ((null != cl) && cl.hasOption("out")) {
            outputFormat = cl.getOptionValue("out").toLowerCase();
            if (null == outputFormat) {
                System.exit(ErrorCodes.MISSING_ARG_VALUES);
            }
        }

        verilogInputFlag = cl.hasOption("hdl");

        isDebugPrintEnabled = cl.hasOption("debug");

        if ((null != cl) && cl.hasOption("help")) {
            outputCommandLineHelp(options);
        }
        * 
        */
    //}
    
        
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
        //final Options options = createCommandLineOptions();
        //CommandLineParser parser = new DefaultParser();
        
        //Read in ucf
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        //System.out.println("Enter path/to/UCFFile: ");
        //String ucfPath = reader.nextLine(); // reads in filepath
        String ucfPath = "SampleInput/sample.json";     //temp path for debugging
        System.out.println("Reading UCF...");
        ParsedUCF ucf = new ParsedUCF(ucfPath);
        
        //Read Verilog + Make Graph
        //System.out.println("Enter path/to/VerilogFile: ");
        //String vPath = reader.nextLine(); // reads in filepath
        String vPath = "SampleInput/sample.v";          //temp path for debugging
        System.out.println("Reading Verilog...");
        NetListTransition nlt = new NetListTransition(ucf, vPath);
        
        //Create JGraphX from netlist Graph while error checking
        System.out.println("Creating JGraphX, error checking...");
        GraphTranslation gt = new GraphTranslation(nlt, ucf);
        
        // create a visualization using JGraphX
        Visualization v = new Visualization();
        v.display(gt.jgraphx);
        
        //Create Mint file from netlist graph and parsed ucf
        System.out.println("Creating Mint file output...");
        CreateMint cm = new CreateMint(nlt, ucf);
        System.out.println("All done!");
    }    
}
