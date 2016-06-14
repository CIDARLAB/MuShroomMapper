/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Scanner;
import org.json.JSONException;
import static org.cidarlab.fluigi.fluigi.Fluigi.processMintDevice;
import org.json.JSONArray;

/**
 *  Takes the modified netlist graph and the parsed UCF and creates a Mint file for use in Fluigi
 * @author Shane
 */

//To do: set up Mint control layer writing

public class CreateMint {
    String line = "";
    String flowPorts = "";
    //String controlPorts = ""; //control layer to be implemented
    List<String> channelList;
    
    
    public CreateMint(NetListTransition graph, ParsedUCF ucf, String fileName) throws UnsupportedEncodingException, FileNotFoundException, JSONException, IOException{
        PrintWriter mintWriter = new PrintWriter(fileName, "UTF-8");
        mintWriter.println("# .uf output by muShroomMapper");
        mintWriter.println("DEVICE testDevice");
        mintWriter.println("");
        mintWriter.println("LAYER FLOW");
        mintWriter.println("");
        int inPortCount = 0;
        int outPortCount = 0;
        //adding inports
        for(MuGate port:graph.gates){
            if(port.type.equals("input")){
                flowPorts+="inPort"+inPortCount+",";
                port.mintName = "inPort"+inPortCount;
                inPortCount++;
            }
        }
        //adding outports
        for (MuGate port:graph.gates){
            if(port.type.equals("output")){
                flowPorts+="outPort"+outPortCount;
                port.mintName = "outPort"+outPortCount;
                if (outPortCount == (graph.outPorts.size()-1)) flowPorts+=" r=100;";
                else flowPorts+=",";
                outPortCount++;
            }
        }
        mintWriter.println("PORT " + flowPorts);
        //adding devices
        int deviceCount = 0;
        for (MuGate mg:graph.gates){
            if (mg.type.equals("gate")){
                String mint = mg.opInfo.get("mint") + ";";
                mint = mint.replaceAll("NAME", "Device"+deviceCount);
                mg.mintName = "Device"+deviceCount;
                //System.out.println(mg.primitive.mintSyntax);
                mintWriter.println(mint);
                deviceCount++;
            }
        }
        int channelCount = 0;
        //adding channels
        int wireCount = 0;
        for(MuWire w : graph.wires)                 //printing channels eg: "CHANNEL channel0 from Device0 2 to Device1 4 w=100;"
        {                       //TODO: make sure we're not asking MuGates who dont have in/outTerm JSONArrays
            System.out.println("Wire count = "+wireCount);  //debugging
            System.out.println("Wire name = "+w.name);
            System.out.println("Wire type = "+w.type);
            int currInTerm;
            int currOutTerm;
            
            
            if (w.fromGate.outTermFlag == true)
            {
                currOutTerm = w.fromGate.opInfo.getInt("outputTerms");
            }
            else currOutTerm = w.fromGate.outTermVal;
            
            if (w.toGate.inTermFlag == true)
            {                
                JSONArray inTermsArray = w.toGate.opInfo.getJSONArray("inputTerms");
                System.out.println("ToGate symbol = "+w.toGate.symbol);
                System.out.println("ToGate type = "+w.toGate.type);
                System.out.println("InTermIndex = "+w.toGate.inTermInd);
                currInTerm = inTermsArray.getInt(w.toGate.inTermInd);
                w.toGate.inTermInd++;       //incrementing the gate's in terminal index
            }
            else currInTerm = w.toGate.inTermVal;
                        
            
            String channelMintLine = "CHANNEL channel"+channelCount+" from ";   //adding "CHANNEL channel0 from " to line
            channelMintLine += w.fromGate.mintName+" ";                         //adding "Device0 " to line
            channelMintLine += currOutTerm+" to ";                               //adding "2 to " to line
            channelMintLine += w.toGate.mintName+" ";                           //adding  "Device1 " to line
            channelMintLine += currInTerm+" w=100;";                             //adding "4 w=100;" to line
            mintWriter.println(channelMintLine);                                //printing whole line
            channelCount++;
            wireCount++;
        }                       //TODO: NEED TO MAKE CHANNEL SIZE PARAMETRIC ^^^
        
        mintWriter.println("");
        mintWriter.println("END LAYER");
        /*  //Got rid of empty control layer due to Fluigi bug which breaks on finding empty control layer
        mintWriter.println("");
        mintWriter.println("LAYER CONTROL");
        //Where control stuff would go
        mintWriter.println("");
        mintWriter.println("#To be implemented");
        mintWriter.println("");
        mintWriter.println("END LAYER");
        */
        mintWriter.close();
    }
}
