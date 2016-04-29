/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import com.mxgraph.view.mxGraph;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 * @author Riastradh
 */
public class GraphTranslation{
    public mxGraph jgraphx = new mxGraph();
    
    GraphTranslation(netListTransition net, ParsedUCF ucf) {
        //TODO Create mxGraph 
        
    }
    
    public static DirectedGraph generateDefault(){
        // create a JGraphT graph
        DirectedGraph g = new DefaultDirectedGraph( DefaultEdge.class );

        Image img = null; 
        Image imga = null,imgb = null ,imgc = null,imgd = null;
        try{
            img = ImageIO.read(new File("rhett.jpg"));
            imga = ImageIO.read(new File("a.jpg"));
            imgb = ImageIO.read(new File("b.jpg"));
            imgc = ImageIO.read(new File("c.jpg"));
            imgd = ImageIO.read(new File("d.jpg"));
        } catch (IOException ex) {
            Logger.getLogger(GraphTranslation.class.getName()).log(Level.SEVERE, null, ex);
        }
        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";
        String v5 = "v5";

        // add some sample data (graph manipulated via JGraphT)
        g.addVertex(img);
        g.addVertex(imga);
        g.addVertex(imgb);
        g.addVertex(imgc);
        g.addVertex(imgd);

        g.addEdge(img,imgd);
        g.addEdge(imga, imgb);
        //g.addEdge(v2, v3);
        g.addEdge(imgc, imga);
        g.addEdge(img,imga);
        g.addEdge(imgd, imgc);        
        
        return g;
    }
}