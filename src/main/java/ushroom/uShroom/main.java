/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

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
        // create a JGraphT graph
        DirectedGraph g = GraphTranslation.generateDefault();
        // create a visualization using JGraph, via an adapter
        Visualization v = new Visualization();
        
        v.display(g);
    }
    
}
