package buigem2016hw.mushroommapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.String;
import org.cellocad.BU.dom.DGate;
import org.cellocad.BU.dom.DGateType;
import org.cellocad.BU.dom.LayerType;
import org.json.JSONException;
import org.json.JSONArray;

/**
 *  Takes the modified netlist graph and the parsed UCF and creates a Mint file for use in Fluigi
 * @author Shane
 */

//To do: set up Mint control layer writing

public class CreateMint 
{
//    String line = "";
    //initializing MINT sections to print
    //flow sections
    String flowInPorts = "";
    String flowOutPorts = "";
    String flowPorts = "";
    String flowDevices = "";
    String flowChannels = "";
    //control sections
    String controlInPorts = "";
    String controlOutPorts = "";
    String controlPorts = "";
    String controlDevices = "";
    String controlChannels = "";
    
    //initializing MINT section counters
    //flow counters
    int flowInPortCount = 0;
    int flowOutPortCount = 0;
    int flowDeviceCount = 0;
    int flowChannelCount = 0;
    //control counters
    int controlInPortCount = 0;
    int controlOutPortCount = 0;
    int controlDeviceCount = 0;
    int controlChannelCount = 0;
    
    String portRadius = "100"; //to be connected with GUI
 
//    List<String> channelList;
//    List<enum> switchList;    //how to do this? :(
    
    
    
    
    public CreateMint(NetListTransition graph, ParsedUCF ucf, String fileName) throws UnsupportedEncodingException, FileNotFoundException, JSONException, IOException
    {
        for(DGate dg:graph.gateGraph)
        {
            switch(dg.gtype)
            {
                case uF:
                    if (dg.layer == LayerType.flow)
                    {
                        flowDeviceCount++;
                    }
                    else if (dg.layer == LayerType.control)
                    {
                        controlDeviceCount++;
                    }
                    else System.out.println("unlayered gate! UCF/Bug?");
                    break;
                    
                case uF_IN:
                    if (dg.layer == LayerType.flow)
                    {
                        flowInPorts += "flowInPort" + flowInPortCount + ",";
                        dg.mintName = "flowInPort" + flowInPortCount;
                        flowInPortCount++;
                    }
                    else if (dg.layer == LayerType.control)
                    {
                        controlInPorts += "controlInPort"+controlInPortCount+",";
                        dg.mintName = "controlInPort"+controlInPortCount;
                        controlInPortCount++;
                    }
                    else System.out.println("unlayered gate! UCF/Bug?");
                    break;
                    
                case uF_OUT:
                    if (dg.layer == LayerType.flow)
                    {
                        flowOutPorts+="outPort"+flowOutPortCount+",";
                        dg.mintName = "outPort"+flowOutPortCount;
                        flowOutPortCount++;
                    }
                    else if (dg.layer == LayerType.control)
                    {
                        controlOutPorts+="outPort"+controlOutPortCount+",";
                        dg.mintName = "outPort"+controlOutPortCount;
                        controlOutPortCount++;
                    }
                    else System.out.println("unlayered gate! UCF/Bug?");
                    break;
                default:
                    System.out.println("Untyped gate! Netsynth bug?");
            }
        }
        flowPorts = flowInPorts+flowOutPorts;
        flowPorts = flowPorts.substring(0, flowPorts.length()-1);   //removing extra comma
        
        controlPorts = controlInPorts+controlOutPorts;
        controlPorts = controlPorts.substring(0, controlPorts.length()-1); //removing extra comma
        
//        PrintWriter mintWriter = new PrintWriter(fileName, "UTF-8");
//        mintWriter.println("# .uf output by muShroomMapper");
//        mintWriter.println("DEVICE testDevice");
//        mintWriter.println("");
//        mintWriter.println("LAYER FLOW");
//        mintWriter.println("");
                
        //concatenating flow and control inports
//        for(MuGate port:graph.gates)
//        {
//            if(port.type.equals("input"))
//            {
//                
//                if(port.layer.equals("flow"))
//                {   //creating flow inports
//                    flowPorts += "flowInPort" + flowInPortCount + ",";
//                    port.mintName = "flowInPort" + flowInPortCount;
//                    flowInPortCount++;
//                }               
//                else if (port.layer.equals("control"))
//                {
//                    controlInPorts += "controlInPort"+controlInPortCount+",";
//                    port.mintName = "controlInPort"+controlInPortCount;
//                    controlInPortCount++;
//                }
//                else System.out.println("Unidentified port layer!");
//            }
//        }
        //concatenating flow outports
//        for (MuGate port:graph.gates)
//        {
//            if(port.type.equals("output"))
//            {
//                flowOutPorts+="outPort"+flowOutPortCount;
//                port.mintName = "outPort"+flowOutPortCount;
//                if (outPortCount == (graph.outPorts.size()-1)) flowPorts+=" r=100;";
//                else flowPorts+=",";
//                outPortCount++;
//            }
//        }
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
        

    }
    public void printMint(String fileName) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintWriter mintWriter = new PrintWriter(fileName, "UTF-8");
        mintWriter.println("# .uf output by muShroomMapper");
        mintWriter.println("DEVICE testDevice");
        mintWriter.println("");
        
        mintWriter.println("LAYER FLOW");
        mintWriter.println("");
        mintWriter.println("PORT " + flowPorts + " r=" + portRadius);
        //print flow ports, devices, channels
        mintWriter.println("");
        mintWriter.println("END LAYER");
        mintWriter.println("");
        
        mintWriter.println("LAYER CONTROL");
        mintWriter.println("");
        mintWriter.println("PORT " + controlPorts + " r=" + portRadius);
        //print control devices
        mintWriter.println(controlChannels);
        mintWriter.println("");
        mintWriter.println("END LAYER");
        
        mintWriter.close();
    }
    
}
