package buigem2016hw.mushroommapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.json.JSONException;
import org.json.JSONArray;

/**
 *  Takes the modified netlist graph and the parsed UCF and creates a Mint file for use in Fluigi
 * @author Shane
 */

//To do: set up Mint control layer writing

public class CreateMint {
    String line = "";
    String flowPorts = "";
    String controlPorts = "";
    List<String> channelList;
    
    
    public CreateMint(NetListTransition graph, ParsedUCF ucf, String fileName) throws UnsupportedEncodingException, FileNotFoundException, JSONException, IOException{
        PrintWriter mintWriter = new PrintWriter(fileName, "UTF-8");
        mintWriter.println("# .uf output by muShroomMapper");
        mintWriter.println("DEVICE testDevice");
        mintWriter.println("");
        mintWriter.println("LAYER FLOW");
        mintWriter.println("");
        
        //initializing counters
        int flowInPortCount = 0;
        int controlInPortCount = 0;
        int outPortCount = 0;
        int deviceCount = 0;  
        int channelCount = 0;        
        
        //concatenating flow and control inports
        for(MuGate port:graph.gates)
        {
            if(port.type.equals("input"))
            {
                if(port.opInfo.get("layer").equals("flow"))
                {   //creating flow inports
                    flowPorts += "flowInPort" + flowInPortCount + ",";
                    port.mintName = "flowInPort" + flowInPortCount;
                    flowInPortCount++;
                }               
                else
                {
                    controlPorts += "controlInPort"+controlInPortCount+",";
                    port.mintName = "controlInPort"+controlInPortCount;
                    controlInPortCount++;
                }
            }
        }
        //concatenating flow outports
        for (MuGate port:graph.gates)
        {
            if(port.type.equals("output"))
            {
                flowPorts+="outPort"+outPortCount;
                port.mintName = "outPort"+outPortCount;
                if (outPortCount == (graph.outPorts.size()-1)) flowPorts+=" r=100;";
                else flowPorts+=",";
                outPortCount++;
            }
        }
        mintWriter.println("PORT " + flowPorts);        //printing out concatenated flow ports to mint file
        
        //adding devices
        for (MuGate mg:graph.gates)
        {
            if (mg.type.equals("gate"))
            {
                String mint = mg.opInfo.get("mint") + ";";
                mint = mint.replaceAll("NAME", "Device"+deviceCount+mg.opInfo.getString("name"));
                mg.mintName = "Device"+deviceCount+mg.opInfo.getString("name");
                mintWriter.println(mint);               //printing out mF gate
                mg.isWritten = true;                    //tagging gate as printed
                deviceCount++;
            }
        }

        //adding channels
        String controlChannels = "";
        for(MuWire w : graph.wires)                 //printing flow channels eg: "CHANNEL flowchannel0 from Device0 2 to Device1 4 w=100;"
        {
            if (w.isWritten == true) continue;      //skip any duplicate channels
            else if (w.dupChannel != null) w.setDestination(w.dupChannel.toGate);   //combining the duplicate channel from any valve-gates
                
            int currInTerm;                         //checking which terminal to use for channel printing
            int currOutTerm;        
            if (w.fromGate.outTermFlag == true)
            {
                currOutTerm = w.fromGate.opInfo.getInt("outputTerms");
            }
            else currOutTerm = w.fromGate.outTermVal;
            
            if (w.toGate.inTermFlag == true)
            {                
                JSONArray inTermsArray = w.toGate.opInfo.getJSONArray("inputTerms");
                currInTerm = inTermsArray.getInt(w.toGate.inTermInd);
                w.toGate.inTermInd++;       //incrementing the gate's in terminal index
            }
            else currInTerm = w.toGate.inTermVal;
                        
            if (w.type.equals("fchan") || w.type.equals("finput"))
            {
                String channelMintLine = "CHANNEL flowchannel"+channelCount+" from ";   //adding "CHANNEL flowchannel0 from " to line
                channelMintLine += w.fromGate.mintName+" ";                         //adding "Device0 " to line
                channelMintLine += currOutTerm+" to ";                               //adding "2 to " to line
                channelMintLine += w.toGate.mintName+" ";                           //adding  "Device1 " to line
                channelMintLine += currInTerm+" w=100;";                             //adding "4 w=100;" to line
                mintWriter.println(channelMintLine);                                //printing whole line
                w.isWritten = true;                                                 //marking wire as printed
                channelCount++;
            }
            else if (w.type.equals("cchan") || w.type.equals("cinput"))
            {
                controlChannels += "\nCHANNEL controlchannel"+channelCount+" from ";   //adding "CHANNEL flowchannel0 from " to line
                controlChannels += w.fromGate.mintName+" ";                         //adding "Device0 " to line
                controlChannels += currOutTerm+" to ";                               //adding "2 to " to line
                controlChannels += w.toGate.mintName+" ";                           //adding  "Device1 " to line
                controlChannels += currInTerm+" w=100;\n";                             //adding "4 w=100;" to line
                w.isWritten = true;                                                 //marking wire as printed
                channelCount++;  
            }
        }                       //TODO: NEED TO MAKE CHANNEL SIZE PARAMETRIC ^^^ <---GUI controlled sizing?
        
        mintWriter.println("");
        mintWriter.println("END LAYER");
        mintWriter.println("");
        mintWriter.println("LAYER CONTROL");
        mintWriter.println("");
        mintWriter.println("PORT " + controlPorts);             //printing control ports to mint file... could for through and mark control ports printed here?
        //print control gates
        mintWriter.println(controlChannels);
        mintWriter.println("");
        mintWriter.println("END LAYER");
        
        mintWriter.close();
    }
}
