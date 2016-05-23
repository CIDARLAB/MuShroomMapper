/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.cellocad.BU.dom.DGate;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 * @author Everett
 */

//To do: Consider abandoning JGraphX (Will its visualization fit into the overall Fluigi flow's GUI? 

public class GraphTranslation{
    public mxGraph jgraphx = new mxGraph();
    mxStylesheet styleSheet = jgraphx.getStylesheet();
    Object parent = jgraphx.getDefaultParent();
    List<Object> vertices = new ArrayList<Object>();   
    
    GraphTranslation(NetListTransition net, ParsedUCF ucf) throws ShroomException{
        //Error checking!!
        checkForErrors(net, ucf);
        
        //Create styles for each operator type 
        jgraphx.getModel().beginUpdate();
        generateJGraphX(net, ucf);
        jgraphx.getModel().endUpdate();
    }
    
    private void generateJGraphX(NetListTransition net, ParsedUCF ucf){
        //create sytles
        generateStyles(ucf);
        for( MuGate d : net.gates ){
            vertices.add(d.gindex, jgraphx.insertVertex(parent, Integer.toString(d.gindex), d.gindex, 0, 0, 80, 30, "style_"+d.symbol));
        }
        
        for ( MuWire w : net.wires){
            Object inVert = vertices.get(w.fromGate.gindex);
            Object outVert = vertices.get(w.toGate.gindex);
            jgraphx.insertEdge(parent, null, "", inVert, outVert);
        }
    }
    
    private void checkForErrors(NetListTransition net, ParsedUCF ucf) throws ShroomException{
        for( MuGate d : net.gates ){
            //TODO: Skip input and piutput gates 
            if(d.type.equals("input") || d.type.equals("output")){
                continue;
            }else{
                //Check if all operators occuring in netlist are in ucf
                if(!(ucf.operators.contains(d.symbol))){
                    //if not
                    throw new ShroomException("Dgate operator "+d.symbol+" not found in UCF");
                }
                for(GatePrimitive prim : ucf.primitives){
                    if(d.symbol.equals(prim.operator)){
                        
                        //check that number of inputs match and only 1 output
                        if(d.input.size() != prim.inputs){
                            throw new ShroomException("DGate "+d.gname+" has incorrect number of inputs");
                        } else if (prim.outputs != 1){
                            throw new ShroomException("DGate "+d.gname+" has incorrect number of outputs");
                        }
                                
                        //add the primitave as the MuGate's primitive
                        d.addPrimitive(prim);
                    }
                }
            }
        }        
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

    private void generateStyles(ParsedUCF ucf) {
         for( GatePrimitive p : ucf.primitives ){
            String styleName = "style_"+p.operator;
            Hashtable<String, Object> style = new Hashtable<String, Object>();
            style.put( mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
            style.put( mxConstants.STYLE_IMAGE, p.picturePath);
            style.put( mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);
            styleSheet.putCellStyle(styleName, style);  
         }
    }
}