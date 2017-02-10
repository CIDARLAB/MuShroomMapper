package buigem2016hw.mushroommapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cellocad.BU.dom.DGate;
import org.cellocad.BU.dom.DGateType;
import org.cellocad.BU.dom.DWire;
import org.cellocad.BU.dom.LayerType;
import org.json.JSONException;
import org.json.JSONArray;

/**
 * Takes the modified netlist graph and the parsed UCF and creates a Mint file
 * for use in Fluigi
 *
 * @author Shane
 */
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
    public String controlInPorts = "";        //need to fix 
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

     //to be connected with UCF

//    List<String> channelList;   
    public CreateMint(NetListTransition graph, ParsedUCF ucf, String fileName) throws UnsupportedEncodingException, FileNotFoundException, JSONException, IOException 
    {
    int portRadius = ucf.portRadius;
    int channelWidth = ucf.channelWidth;
        for (DGate dg : graph.gateGraph) 
        {
            switch (dg.gtype) 
            {
                case uF:
                    switch (dg.layer) 
                    {
                        case flow:
                            String fDevLine = dg.opInfo.getString("mint") + ";";
                            fDevLine = fDevLine.replaceAll("NAME", "fDevice" + flowDeviceCount + "_" + dg.opInfo.getString("name"));
                            dg.mintName = "fDevice" + flowDeviceCount + "_" + dg.opInfo.getString("name");

                            flowDevices += fDevLine;
                            flowDevices += "\n";
                            dg.isWritten = true;
                            flowDeviceCount++;
                            break;

                        case control:       
                            String cDevLine = dg.opInfo.getString("mint") + ";";
                            cDevLine = cDevLine.replaceAll("NAME", "cDevice" + controlDeviceCount + "_" + dg.opInfo.getString("name"));
                            dg.mintName = "cDevice" + controlDeviceCount + "_" + dg.opInfo.getString("name");

                            controlDevices += cDevLine;
                            controlDevices += "\n";
                            dg.isWritten = true;
                            controlDeviceCount++;
                            break;

                        default:
                            System.out.println("unlayered gate! UCF/Bug?");
                            break;
                    }
                    break;

                case uF_IN:                                         //inport
                    switch (dg.layer) 
                    {
                        case flow:                                  //flow inport
                            flowInPorts += "flowInPort" + flowInPortCount + ",";
                            dg.mintName = "flowInPort" + flowInPortCount;

                            dg.isWritten = true;
                            flowInPortCount++;
                            dg.bankCount = flowInPortCount;
                            break;

                        case control:                               //control inport
                            controlInPorts += "controlInPort" + controlInPortCount + ",";
                            dg.mintName = "controlInPort" + controlInPortCount;

                            dg.isWritten = true;
                            controlInPortCount++;
                            dg.bankCount = controlInPortCount;
                            break;

                        default:
                            System.out.println("unlayered gate! UCF/Bug?");
                            break;
                                }
                            break;
                        
                case uF_OUT:
                    switch (dg.layer) 
                    {
                        case flow:
                            flowOutPorts += "outPort" + flowOutPortCount + ",";
                            dg.mintName = "outPort" + flowOutPortCount;

                            dg.isWritten = true;
                            flowOutPortCount++;
                            break;
                        case control:
                            controlOutPorts += "outPort" + controlOutPortCount + ",";
                            dg.mintName = "outPort" + controlOutPortCount;

                            dg.isWritten = true;
                            controlOutPortCount++;
                            break;

                        default:
                            System.out.println("unlayered gate! UCF/Bug?");
                            break;
                    }
                    break;

                default:
                    System.out.println("Untyped gate! Netsynth bug?");
                    break;
            }
        }

        for (DWire dw : graph.wireGraph) 
        {
            int currentInTerm;
            int currentOutTerm;
            JSONArray inTermsArray;
            if (dw.isWritten == false) 
            {
                switch (dw.wtype) 
                {
                    case cinput:
                        //inTermsArray = dw.toGate.opInfo.getJSONArray("inputTerms");     //need to be catch all with if inTermsFlag

                        currentInTerm = dw.toGate.get(0).opInfo.getInt("controlTerms");
                        
                        //these if/else for new up valves (to make sure that orientation is correct)
                        if(currentInTerm == 1) currentOutTerm = 3;
                        else currentOutTerm = 1;
                        
                        controlChannels += "CHANNEL controlChannel" + controlChannelCount + " from ";
                        //controlChannels += dw.fromGate.mintName + " " + dw.fromGate.outTermVal + " to ";            //port way - commented out for new up valves
                        controlChannels += dw.fromGate.mintName + " " + currentOutTerm + " to ";            //port way - new up valve way
//                        controlChannels += "CBank" + " " + dw.fromGate.bankCount + " to ";                        //bank way
                        controlChannels += dw.toGate.mintName + " " + currentInTerm + " w=" + channelWidth + ";\n";
                        dw.isWritten = true;
                        controlChannelCount++;

                        break;

                    case finput:
                        if (dw.toGate.get(0).layer.equals(LayerType.control)) 
                        {
                            currentInTerm = dw.toGate.get(0).opInfo.getInt("inputTerms");
                        } 
                        else 
                        {
                            inTermsArray = dw.toGate.get(0).opInfo.getJSONArray("inputTerms");
                            currentInTerm = inTermsArray.getInt(dw.toGate.get(0).inTermInd);
                            dw.toGate.get(0).inTermInd++;
                        }

                        flowChannels += "CHANNEL flowChannel" + flowChannelCount + " from ";
                        flowChannels += dw.fromGate.mintName + " " + dw.fromGate.outTermVal + " to ";             //port way
//                        flowChannels += "FBank" + " " + dw.fromGate.bankCount + " to ";                             //bank way
                        flowChannels += dw.toGate.get(0).mintName + " " + currentInTerm + " w=" + channelWidth + ";\n";
                        dw.isWritten = true;
                        flowChannelCount++;

                        break;

                    case coutput:
                        currentInTerm = dw.toGate.inTermVal;
                        currentOutTerm = dw.fromGate.opInfo.getInt("outPutTerms");     //need to be catch all with outTermFlag, be done with JSON parsing by translation, store terms int object attribute?

                        controlChannels += "CHANNEL controlChannel" + controlChannelCount + " from ";
                        controlChannels += dw.fromGate.mintName + " " + currentOutTerm + " to ";
                        controlChannels += dw.toGate.mintName + " " + currentInTerm + " w=" + channelWidth + ";\n";
                        dw.isWritten = true;
                        controlChannelCount++;

                        break;

                    case foutput:       //need to for through toGate/merge/split wires
                        currentInTerm = dw.toGate.inTermVal;
                        currentOutTerm = dw.fromGate.opInfo.getInt("outputTerms");     //need to be catch all with outTermFlag, be done with JSON parsing by translation, store terms int object attribute?

                        flowChannels += "CHANNEL flowChannel" + flowChannelCount + " from ";
                        flowChannels += dw.fromGate.mintName + " " + currentOutTerm + " to ";
                        flowChannels += dw.toGate.mintName + " " + currentInTerm + " w=" + channelWidth + ";\n";
                        dw.isWritten = true;
                        flowChannelCount++;

                        break;

                    case cchannel:
                        //to be inplemented
                        break;

                    case fchannel:      //need to for through toGate
                        if (dw.toGate.layer.equals(LayerType.control)) 
                        {
                            currentInTerm = dw.toGate.opInfo.getInt("inputTerms");      //not permanent
                        } 
                        else 
                        {
                            inTermsArray = dw.toGate.opInfo.getJSONArray("inputTerms");
                            currentInTerm = inTermsArray.getInt(dw.toGate.inTermInd);
                            dw.toGate.inTermInd++;
                        }
                        
                        currentOutTerm = dw.fromGate.opInfo.getInt("outputTerms");

                        flowChannels += "CHANNEL flowChannel" + flowChannelCount + " from ";
                        flowChannels += dw.fromGate.mintName + " " + currentOutTerm + " to ";
                        flowChannels += dw.toGate.mintName + " " + currentInTerm + " w=" + channelWidth + ";\n";
                        dw.isWritten = true;
                        flowChannelCount++;

                            break;

                default:
                        System.out.println("untyped wire! check netsynth!");
                        break;
                }
                    }
                    }
        if (flowInPortCount + flowOutPortCount > 0)
        {
            flowPorts = flowInPorts + flowOutPorts;
            flowPorts = flowPorts.substring(0, flowPorts.length() - 1);   //removing extra comma
        }
        
        if (controlInPortCount + controlOutPortCount > 0)
        {
            controlPorts = controlInPorts + controlOutPorts;
            controlPorts = controlPorts.substring(0, controlPorts.length() - 1); //removing extra comma
        }
        String deviceName = graph.deviceName;
        
        PrintWriter mintWriter = new PrintWriter(fileName, "UTF-8");
        mintWriter.println("# .uf output by muShroomMapper");
        mintWriter.println("3D DEVICE " + deviceName);                     //TODO: make 3D device a flag
        mintWriter.println("");
        
        mintWriter.println("LAYER FLOW\n\n");

        mintWriter.println("PORT " + flowPorts + " r=" + portRadius + ";\n");     //because multiple ports are bugged in fluigi
//        mintWriter.println("V BANK FBank of " + flowInPortCount + " PORT r=" + portRadius + " dir=RIGHT spacing=3000 channelWidth=" + channelWidth + ";");
        mintWriter.println(flowDevices);
        mintWriter.println(controlDevices); // this is in flow because 3dvalve is bugged in fluigi
        mintWriter.println(flowChannels);

        mintWriter.println("");
        mintWriter.println("END LAYER");
        mintWriter.println("");

        mintWriter.println("LAYER CONTROL\n\n");
        
        if (controlInPortCount + controlOutPortCount > 0)   //if no ports, don't print port
        {
          mintWriter.println("PORT " + controlPorts + " r=" + portRadius + ";\n");  //because multiple ports are bugged in fluigi
//        if (controlInPortCount > 0) mintWriter.println("H BANK CBank of " + controlInPortCount + " PORT r=" + portRadius + " dir=UP spacing=2500 channelWidth=" + channelWidth + ";");
//        mintWriter.println(controlDevices); //because 3dvalve is bugged in fluigi   
        }

        mintWriter.println(controlChannels);

        mintWriter.println("");
        mintWriter.println("END LAYER");

        mintWriter.close();
    }
}
