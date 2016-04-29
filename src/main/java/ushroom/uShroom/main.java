/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import java.io.IOException;
import org.cellocad.BU.netsynth.Utilities;
import org.jgrapht.DirectedGraph;



/**
 *
 * @author Riastradh
 */
public class main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        //TODO: Take in user input on verilog and ucf 
        
        //TODO: Read in verilog file, run with Prashant's code   
        String filepath = "fluigi.v";   
        String line = "";
        line = Utilities.getFileContentAsString(filepath);
        
        //Read in verilog file, run with Prashant's code  
        netListTransition net = new netListTransition();
        
        //Read in ucf
        ParsedUCF ucf = new ParsedUCF("simpleucf.ucf");
        System.out.println(ucf.operators);

        
        //TODO: Create JGraphX from Prashant Graph while error checking
        GraphTranslation gt = new GraphTranslation(net, ucf);

        
        // create a JGraphT graph
        DirectedGraph g = GraphTranslation.generateDefault();
        
        
        // create a visualization using JGraph, via an adapter
        Visualization v = new Visualization();
        v.display(g);
        
        //TODO: Submit to Mint 
    }
    
}
