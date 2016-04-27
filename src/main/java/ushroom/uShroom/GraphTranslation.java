/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 * @author Riastradh
 */
public class GraphTranslation{
    
    private GraphTranslation(){
    }   
    
    public static DirectedGraph generateGraph(){
        // create a JGraphT graph
        DirectedGraph g = new DefaultDirectedGraph( DefaultEdge.class );

        
        return g;
    }
    
    public static DirectedGraph generateDefault(){
        // create a JGraphT graph
        DirectedGraph g = new DefaultDirectedGraph( DefaultEdge.class );


        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add some sample data (graph manipulated via JGraphT)
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v3, v1);
        g.addEdge(v4, v3);        
        
        return g;
    }
}