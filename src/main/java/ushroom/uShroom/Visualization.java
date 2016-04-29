/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.util.Hashtable;
import javax.swing.JApplet;
import javax.swing.JFrame;
import static javax.swing.SwingConstants.*;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;





public class Visualization extends JApplet{
    // 
    private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
    private static final Dimension DEFAULT_SIZE = new Dimension(1000, 750);
    
    private JGraphXAdapter<Image, DefaultEdge> jgxAdapter;
    
    public void display(DirectedGraph g){        
        
        
        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(g);
        
        // get graph stylesheet
        mxStylesheet stylesheet = jgxAdapter.getStylesheet();
        
        // define image style name
        String styleName = "ymixerImageStyle";
        
        // define image style           
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put( mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        style.put( mxConstants.STYLE_IMAGE, "ymixer.png");
        style.put( mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        stylesheet.putCellStyle( styleName, style);

        Object v1 = jgxAdapter.insertVertex(jgxAdapter.getDefaultParent(), null, "Vertex 1", 0, 0, 80, 30, styleName);
        jgxAdapter.setCellStyle(styleName, jgxAdapter.);
        
        getContentPane().add(new mxGraphComponent(jgxAdapter));
        resize(DEFAULT_SIZE);
        
        // positioning via jgraphx layouts
        mxHierarchicalLayout layout = new mxHierarchicalLayout(jgxAdapter, WEST);
        layout.execute(jgxAdapter.getDefaultParent());

        JFrame frame = new JFrame();
        frame.getContentPane().add(this);
        frame.setTitle("JGraphT Adapter to JGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    }
}