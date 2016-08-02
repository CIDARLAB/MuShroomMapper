/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.cellocad.BU.dom.DGate;
import org.cellocad.BU.dom.DGateType;
import org.cellocad.BU.dom.DWire;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Everett
 */

/*To do: 
    Consider abandoning JGraphX (Will its visualization fit into the overall Fluigi flow's GUI? 
    Move error checking elsewhere? It doesn't seem logical here...
*/
public class GraphTranslation{
    public mxGraph jgraphx = new mxGraph();
    mxStylesheet styleSheet = jgraphx.getStylesheet();
    Object parent = jgraphx.getDefaultParent();
    List<Object> vertices = new ArrayList<>();   
//    
    GraphTranslation(NetListTransition net, ParsedUCF ucf) throws Exceptions, JSONException{
        //Error checking!!
        checkForErrors(net, ucf);
        
        //Create styles for each operator type 
        jgraphx.getModel().beginUpdate();
        generateJGraphX(net, ucf);
        jgraphx.getModel().endUpdate();
    }
//    
    private void generateJGraphX(NetListTransition net, ParsedUCF ucf) throws JSONException{
        //create sytles
        generateStyles(ucf);
        for( DGate d : net.gateGraph ){
            vertices.add(d.gindex, jgraphx.insertVertex(parent, Integer.toString(d.gindex), d.gindex, 0, 0, 80, 30, "style_"+d.symbol));
        }
        
        for ( DWire w : net.wireGraph){
            Object inVert = vertices.get(w.fromGate.gindex);
            Object outVert = vertices.get(w.toGate.gindex);
            jgraphx.insertEdge(parent, null, "", inVert, outVert);
        }
    }
//    
    private void checkForErrors(NetListTransition net, ParsedUCF ucf) throws Exceptions, JSONException{
        for( DGate d : net.gateGraph ){
            //TODO: Skip input and piutput gates 
            if(d.gtype == DGateType.uF_IN || d.gtype == DGateType.uF_OUT)   continue;
            else{
                //Check if all operators occuring in netlist are in ucf
                if(!(ucf.opMap.keySet().contains(d.symbol))){
                    //if not
                    throw new Exceptions("Dgate operator "+d.symbol+" not found in UCF");
                }
                for(String op: ucf.opMap.keySet()){
                    if(d.symbol.equals(op)){
                        JSONObject opObj = ucf.opMap.get(op);       //
                        //check that number of inputs match and only 1 output
                        if(d.input.size() != opObj.getInt("inputs"))
                        {
                            throw new Exceptions("DGate "+d.gname+" has incorrect number of inputs");
                        } 
                        else if ( opObj.getInt("outputs") != 1){
                            throw new Exceptions("DGate "+d.gname+" has incorrect number of outputs");
                        }                                
                    }
                }
            }
        }        
    }
//    
//    
//    //old E code for testing custom images... Doesn't work?
//    public static DirectedGraph generateDefault(){
//        // create a JGraphT graph
//        DirectedGraph g = new DefaultDirectedGraph( DefaultEdge.class );
//        Image img = null; 
//        Image imga = null,imgb = null ,imgc = null,imgd = null;
//        try{
//            img = ImageIO.read(new File("rhett.jpg"));
//            imga = ImageIO.read(new File("a.jpg"));
//            imgb = ImageIO.read(new File("b.jpg"));
//            imgc = ImageIO.read(new File("c.jpg"));
//            imgd = ImageIO.read(new File("d.jpg"));
//
//        } catch (IOException ex) {
//            Logger.getLogger(GraphTranslation.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        String v1 = "v1";
//        String v2 = "v2";
//        String v3 = "v3";
//        String v4 = "v4";
//        String v5 = "v5";
//
//        // add some sample data (graph manipulated via JGraphT)
//        g.addVertex(img);
//        g.addVertex(imga);
//        g.addVertex(imgb);
//        g.addVertex(imgc);
//        g.addVertex(imgd);
//
//        g.addEdge(img,imgd);
//        g.addEdge(imga, imgb);
//        //g.addEdge(v2, v3);
//        g.addEdge(imgc, imga);
//        g.addEdge(img,imga);
//        g.addEdge(imgd, imgc);        
//        
//        return g;
//    }
//
    private void generateStyles(ParsedUCF ucf) throws JSONException {
         for( String op : ucf.opMap.keySet() ){
            JSONObject opObj = ucf.opMap.get(op);
            String styleName = "style_" + op;
            Map<String, Object> style = new HashMap<>();
            style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
            style.put(mxConstants.STYLE_IMAGE, opObj.getString("picpath"));
            style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);
            styleSheet.putCellStyle(styleName, style);  
        }
    }
}