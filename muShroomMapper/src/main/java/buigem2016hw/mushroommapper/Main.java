/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import org.cellocad.BU.netsynth.Utilities;
import org.jgrapht.DirectedGraph;
import org.json.JSONException;



/**
 *
 * @author Shane
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws org.json.JSONException
     * @throws ushroom.uShroom.ShroomException
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, JSONException {
         
       
        //Read in ucf
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter path/to/UCFFile: ");
        String ucfPath = reader.nextLine(); // reads in filepath
        System.out.println("Reading UCF...");
        ParsedUCF ucf = new ParsedUCF(ucfPath);
        ucf.opMap.get("+");
                
        /*
        System.out.println(ucf.opMap.values());
        //Read Verilog
        System.out.println("Enter path/to/VerilogFile: ");
        String vPath = reader.nextLine(); // reads in filepath
        System.out.println("Reading Verilog...");
        NetListTransition nlt = new NetListTransition(ucf, vPath);
        //Create JGraphX from netlist Graph while error checking
        System.out.println("Creating JGraphX, error checking...");
        GraphTranslation gt = new GraphTranslation(nlt, ucf);
        
        // create a visualization using JGraph, via an adapter
        Visualization v = new Visualization();
        v.display(gt.jgraphx);
        //Create Mint file from netlist graph and parsed ucf
        System.out.println("Creating Mint file output...");
        CreateMint cm = new CreateMint(nlt, ucf);
        
        System.out.println("All done!");
       */
    }
    
}
