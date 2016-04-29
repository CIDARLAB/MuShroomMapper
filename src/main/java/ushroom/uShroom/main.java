/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import com.mxgraph.view.mxGraph;
import org.cellocad.BU.fluigi.VerilogFluigiGrammar;
import org.cellocad.BU.fluigi.VerilogFluigiWalker;
import org.cellocad.BU.netsynth.Utilities;
import org.jgrapht.DirectedGraph;



/**
 *
 * @author Riastradh
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //TODO: Take in user input on verilog and ucf 
        
        //TODO: Read in verilog file, run with Prashant's code   
        String filepath = "fluigi.v";   
        String line = "";
        line = Utilities.getFileContentAsString(filepath);
        
        //Read in verilog file, run with Prashant's code, create informative graph obj 
        System.out.println("Reading Verilog...");
        netListTransition nlt = new netListTransition();
        
        //Read in ucf
        System.out.println("Reading UCF...");
        ParsedUCF ucf = new ParsedUCF("simpleucf.ucf");
        
        
        //TODO: Create JGraphX from netlist Graph while error checking
        System.out.println("Creating JGraphX, error checking...");
        GraphTranslation gt = new GraphTranslation(net);

        
        // create a JGraphT graph
        // ?
        DirectedGraph g = GraphTranslation.generateDefault();
        
        
        // create a visualization using JGraph, via an adapter
        Visualization v = new Visualization();
        
        v.display(g);
        
        //Create Mint file from netlist graph and parsed ucf
        System.out.println("Creating Mint file output...");
        CreateMint cm = new CreateMint(nlt, ucf);
        
        System.out.println("All done!");
        
    }
    
}
