/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import java.io.File;
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
        
        //TODO: Create JGraphT from Prashant Graph
        String filepath = "fluigi.v";   
        String line = "";        
        line = Utilities.getFileContentAsString(filepath);
        //System.out.println("FILE LINE :: \n" + fileLine);
        VerilogFluigiWalker walker = VerilogFluigiGrammar.getuFWalker(line);
                
        // create a JGraphT graph
        DirectedGraph g = GraphTranslation.generateDefault();
        // create a visualization using JGraph, via an adapter
        Visualization v = new Visualization();
        
        v.display(g);
    }
    
}
