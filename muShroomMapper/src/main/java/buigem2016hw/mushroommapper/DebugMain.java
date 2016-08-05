/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import static org.cidarlab.fluigi.fluigi.PiranhaPlanner.processMintDevice;
import org.json.JSONException;

/**
 *
 * @author Shane
 */
public class DebugMain 
{

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws FileNotFoundException, JSONException, IOException 
    {
               //Read in ucf
            Scanner reader = new Scanner(System.in);  // Reading from System.in
//           System.out.println("Enter path/to/UCFFile: ");
//            String ucfPath = reader.nextLine(); // reads in filepath
            String ucfPath = "ucf.json";     //temp path for debugging
            System.out.println("Reading UCF...");
            ParsedUCF ucf = new ParsedUCF(ucfPath);

        //Read Verilog + Make Graph
            String vPath = "lfr.v";          //path for debugging
            System.out.println("Reading Verilog...");
            NetListTransition nlt = new NetListTransition(ucf, vPath);

        //Create JGraphX from netlist Graph while error checking
            /*
            System.out.println("Creating JGraphX, error checking...");
            GraphTranslation gt = new GraphTranslation(nlt, ucf);

        //create a visualization using JGraphX
            Visualization v = new Visualization();
            v.display(gt.jgraphx);
            */
        
        //Create Mint file from netlist graph and parsed ucf
            System.out.println("Creating Mint file output...");
            //Scanner ufNameInput = new Scanner(System.in);  //Reading from System.in
            //System.out.println("What would you like to name your .uf file? ");
            //String fileName = ufNameInput.nextLine();
//            String fileName = "debugDevice.uf";
            String ufFileName = nlt.deviceName + ".uf";
            CreateMint cm = new CreateMint(nlt, ucf, "output/"+ufFileName);
            System.out.println("Entering Fluigi");
            processMintDevice("output/"+ufFileName, "fluigi.ini", "sej", true);
            System.out.println("MM is all done!");
            System.exit(0);
    }
    
}
