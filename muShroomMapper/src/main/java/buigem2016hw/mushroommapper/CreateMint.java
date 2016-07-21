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
import org.cellocad.BU.dom.DWire;
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
    
    public CreateMint(NetListTransition graph, ParsedUCF ucf, String fileName) throws UnsupportedEncodingException, FileNotFoundException, JSONException, IOException
    {
        for(DGate dg:graph.gateGraph)
        {
            switch(dg.gtype)
            {
                case uF:
                    switch (dg.layer) 
                    {
                        case flow:
                            String fDevLine = dg.opInfo.getString("mint") + ";";
                            fDevLine = fDevLine.replaceAll("NAME", "Device" + flowDeviceCount + dg.opInfo.getString("name"));
                            dg.mintName = "Device" + flowDeviceCount + dg.opInfo.getString("name");
                            
                            for (DWire in:dg.input)
                            {
                                if (in.isWritten == false)
                                {
                                    
                                    in.isWritten = true;
                                }
                            }
                            
                            if (dg.output.isWritten == false)                            
                            {
                                
                                dg.output.isWritten = true;
                            }
                        
                            flowDevices+=fDevLine;
                            flowDevices+="\n";
                            dg.isWritten = true;
                            flowDeviceCount++;
                            break;
                        
                        case control:       //TODO: test how control devices print
                            String cDevLine = dg.opInfo.getString("mint") + ";";
                            cDevLine = cDevLine.replaceAll("NAME", "Device" + controlDeviceCount + dg.opInfo.getString("name"));
                            dg.mintName = "Device" + flowDeviceCount + dg.opInfo.getString("name");
                            
                            for (DWire in:dg.input)
                            {
                                if (in.isWritten == false)
                                {
                                    
                                    in.isWritten = true;
                                }
                            }
                            
                            if (dg.output.isWritten == false)                            
                            {
                                
                                dg.output.isWritten = true;
                            }
                            
                            controlDevices+=cDevLine;
                            controlDevices+="\n";
                            dg.isWritten = true;
                            controlDeviceCount++;
                            break;
                        
                        default: System.out.println("unlayered gate! UCF/Bug?"); break;
                    }
                    break;
                    
                case uF_IN:
                    switch (dg.layer) 
                    {
                        case flow:
                            flowInPorts += "flowInPort" + flowInPortCount + ",";
                            dg.mintName = "flowInPort" + flowInPortCount;
                            
                            if (dg.output.isWritten == false)
                            {
                                
                                dg.output.isWritten = true;
                            }
                            
                            dg.isWritten = true;
                            flowInPortCount++;
                            break;
                        
                        case control:
                            controlInPorts += "controlInPort"+controlInPortCount+",";
                            dg.mintName = "controlInPort"+controlInPortCount;
                            
                            if (dg.output.isWritten == false)
                            {
                                
                                dg.output.isWritten = true;
                            }
                            
                            dg.isWritten = true;
                            controlInPortCount++;
                            break;
                        
                        default: System.out.println("unlayered gate! UCF/Bug?"); break; 
                    }
                    break;
                    
                case uF_OUT:
                    switch (dg.layer) 
                    {
                        case flow:
                            flowOutPorts+="outPort"+flowOutPortCount+",";
                            dg.mintName = "outPort"+flowOutPortCount;
                            
                            if (dg.input.get(0).isWritten == false)
                            {
                                
                                dg.input.get(0).isWritten = true;
                            }
                            
                            dg.isWritten = true;
                            flowOutPortCount++;
                            break;
                        case control:
                           controlOutPorts+="outPort"+controlOutPortCount+",";
                            dg.mintName = "outPort"+controlOutPortCount;
                            
                            if (dg.input.get(0).isWritten == false)
                            {
                                
                                dg.input.get(0).isWritten = true;
                            }
                                                        
                            dg.isWritten = true;
                            controlOutPortCount++; 
                            break;

                        default: System.out.println("unlayered gate! UCF/Bug?"); break;
                    }
                    break;
                    
                default:
                    System.out.println("Untyped gate! Netsynth bug?"); break;
            }
        }
        flowPorts = flowInPorts+flowOutPorts;
        flowPorts = flowPorts.substring(0, flowPorts.length()-1);   //removing extra comma
        
        controlPorts = controlInPorts+controlOutPorts;
        controlPorts = controlPorts.substring(0, controlPorts.length()-1); //removing extra comma
        
        printMint(fileName);
                 
        //adding channels
//        String controlChannels = "";
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
        mintWriter.println(flowDevices);
        mintWriter.println(flowChannels);
        
        mintWriter.println("");
        mintWriter.println("END LAYER");
        mintWriter.println("");
        
        mintWriter.println("LAYER CONTROL");
        mintWriter.println("");
        
        mintWriter.println("PORT " + controlPorts + " r=" + portRadius);
        mintWriter.println(controlDevices);
        mintWriter.println(controlChannels);
        
        mintWriter.println("");
        mintWriter.println("END LAYER");
        
        mintWriter.close();
    }
    
}
