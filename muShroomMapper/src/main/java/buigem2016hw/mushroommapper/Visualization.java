/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
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

/**
 *
 * @author Everett
 */

//To do: Will this work with the Fluigi flow GUI?

public class Visualization extends JApplet{
    // 
    private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
    private static final Dimension DEFAULT_SIZE = new Dimension(1000, 750);
    
    
    
    public void display(mxGraph g){        
        
        getContentPane().add(new mxGraphComponent(g));
        resize(DEFAULT_SIZE);
        
        // positioning via jgraphx layouts
        mxHierarchicalLayout layout = new mxHierarchicalLayout(g, WEST);
        layout.execute(g.getDefaultParent());

        JFrame frame = new JFrame();
        frame.getContentPane().add(this);
        frame.setTitle("JGraphT Adapter to JGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    }
}