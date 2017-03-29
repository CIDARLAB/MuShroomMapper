package buigem2016hw.mushroommapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cellocad.BU.dom.DGate;
import org.cellocad.BU.dom.DGateType;
import org.cellocad.BU.dom.DWire;
import org.cellocad.BU.dom.DWireType;
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
        if (ucf.autoSplitMerge == true) //when UCF option of automerging and splitting is enabled
        {
            System.out.println("With automerging/splitting of channels enabled...");
            inputCloneFinder(graph.gateGraph);
//        } 
//        else //normal non automerge/split
//        {
//            System.out.println("With no extra settings enabled...");
            for (DGate dg : graph.gateGraph) 
            {
                if (dg.isWritten == false)
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

                                    if(dg.inputClones != null && !(dg.inputClones.isEmpty()))   //if gate has input clones   
                                    {//writing up MINT for splitting channels for inputClones
                                        flowDevices += "V TREE fDevice" + flowDeviceCount + "_" + "SplitTree 1 to ";
                                        flowDevices += (dg.inputClones.size()+1) + " spacing=" + 10*ucf.channelWidth; 
                                        //the spacing is dependent on channel size of input for now (10*channel size), can make more parametric later
                                        flowDevices += " flowChannelWidth=" + ucf.channelWidth;  
                                        flowDevices += ";\n";
                                        
                                        //creating tree for splitting output of inputClones
                                        DGate treeGate = new DGate();   //treegate = tree made by splitting output
                                        treeGate.mintName = "fDevice" + flowDeviceCount + "_" + "SplitTree";
                                        treeGate.isInputClone = true;   //need to link the treeGates together
                                        treeGate.inTermVal = 1;
                                        treeGate.outTermVal = 2;    //need to increment this each time we reference it
                                        
                                        dg.output.fromGate = treeGate;  //the outputs of this gate now come from the tree splitter
                                        for (DGate clone:dg.inputClones)    //for all the clones you have
                                        {
                                            clone.output.fromGate = treeGate;   //the fromGate is the same as mine
                                            clone.isWritten = true; //marked as written to stop multiple gate clones + treeGates
                                        }
                                        flowDeviceCount++;
                                        dg.input.get(0).toGate = dg;    //i shouldn't need this...
                                        DWire treeWire = new DWire();   //wire connecting gate to treeGate (split tree)
                                        treeWire.toGate = treeGate;
                                        treeWire.fromGate = dg;
                                        treeWire.wtype = DWireType.fchannel;
                                        graph.wireGraph.add(treeWire);  //adding uF gate -> tree wire to wireGraph so it will be written later
                                    }
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

                            currentInTerm = dw.toGate.opInfo.getInt("controlTerms");

                            //these if/else for new up valves (to make sure that orientation is correct)
                            if (currentInTerm == 1) 
                            {
                                currentOutTerm = 3;
                            } 
                            else 
                            {
                                currentOutTerm = 1;
                            }

                            controlChannels += "CHANNEL controlChannel" + controlChannelCount + " from ";
                            //controlChannels += dw.fromGate.mintName + " " + dw.fromGate.outTermVal + " to ";            //port way - commented out for new up valves
                            controlChannels += dw.fromGate.mintName + " " + currentOutTerm + " to ";            //port way - new up valve way
//                        controlChannels += "CBank" + " " + dw.fromGate.bankCount + " to ";                        //bank way
                            controlChannels += dw.toGate.mintName + " " + currentInTerm + " w=" + channelWidth + ";\n";
                            dw.isWritten = true;
                            controlChannelCount++;

                            break;

                        case finput:
                            if (dw.toGate.isInputClone)
                            {
                                flowChannels += "CHANNEL flowchannel" + flowChannelCount + " from ";
                                flowChannels += dw.fromGate.mintName + " " + "2" + " to ";
                                flowChannels += dw.toGate.mintName + " " + dw.toGate.inTermVal + " w=" + channelWidth + ";\n";
                                dw.toGate.inTermVal++;
                                dw.isWritten = true;
                                flowChannelCount++;
                                break;
                            }
                            if (dw.toGate.layer.equals(LayerType.control)) {
                                currentInTerm = dw.toGate.opInfo.getInt("inputTerms");
                            }
                            else 
                            {
                                inTermsArray = dw.toGate.opInfo.getJSONArray("inputTerms");
                                currentInTerm = inTermsArray.getInt(dw.toGate.inTermInd);
                                dw.toGate.inTermInd++;
                            }

                            flowChannels += "CHANNEL flowChannel" + flowChannelCount + " from ";
                            flowChannels += dw.fromGate.mintName + " " + dw.fromGate.outTermVal + " to ";             //port way
//                            flowChannels += "FBank" + " " + dw.fromGate.bankCount + " to ";                             //bank way
                            flowChannels += dw.toGate.mintName + " " + currentInTerm + " w=" + channelWidth + ";\n";
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

                        case foutput:
                            currentInTerm = dw.toGate.inTermVal;
                            if (dw.fromGate.isInputClone)
                            {
                                //if (dw.fromGate.outTermVal == 1) dw.fromGate.outTermVal = 2;    //output to tree is 2 or up
                                flowChannels += "CHANNEL flowchannel" + flowChannelCount + " from ";
                                flowChannels += dw.fromGate.mintName + " " + dw.fromGate.outTermVal + " to ";
                                flowChannels += dw.toGate.mintName + " " + "4" + " w=" + channelWidth + ";\n";
                                dw.fromGate.outTermVal++;
                                dw.isWritten = true;
                                flowChannelCount++;
                                //currentOutTerm = dw.fromGate.outTermVal; //i dont think this is needed
                                break;
                            }
                            else currentOutTerm = dw.fromGate.opInfo.getInt("outputTerms");     //need to be catch all with outTermFlag, be done with JSON parsing by translation, store terms int object attribute?
                            flowChannels += "CHANNEL flowChannel" + flowChannelCount + " from ";
                            flowChannels += dw.fromGate.mintName + " " + currentOutTerm + " to ";
                            flowChannels += dw.toGate.mintName + " " + currentInTerm + " w=" + channelWidth + ";\n";
                            dw.isWritten = true;
                            flowChannelCount++;

                            break;

                        case cchannel:
                            //to be implemented (do cchannels a really exist? current primitives/modules don't have any afaik)
                            break;

                        case fchannel:      
                            if (dw.toGate.isInputClone)
                            {
                                flowChannels += "CHANNEL flowchannel" + flowChannelCount + " from ";
                                flowChannels += dw.fromGate.mintName + " " + "2" + " to ";
                                flowChannels += dw.toGate.mintName + " " + dw.toGate.inTermVal + " w=" + channelWidth + ";\n";
                                dw.toGate.inTermVal++;
                                dw.isWritten = true;
                                flowChannelCount++;
                                break;
                            }
                            if (dw.fromGate.isInputClone)
                            {
                                //if(dw.fromGate.inTermVal == 1) dw.fromGate.inTermVal = 2;
                                flowChannels += "CHANNEL flowchannel" + flowChannelCount + " from ";
                                flowChannels += dw.fromGate.mintName + " " + dw.fromGate.outTermVal + " to ";
                                flowChannels += dw.toGate.mintName + " " + dw.toGate.inTermVal + " w=" + channelWidth + ";\n";  //toGate.inTermVal _should_ be 4, but leaving it as its actualy Val just in case
                                dw.toGate.outTermVal++;
                                dw.isWritten = true;
                                flowChannelCount++;
                                break;
                            }
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

            if (controlInPortCount + controlOutPortCount > 0) //if no ports, don't print port
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
    
    public void inputCloneFinder(List<DGate> gates) //checking if gates have input clones
    {
        for (int outerGateCount = 0; outerGateCount < gates.size(); outerGateCount++) 
        {    
            for (int innerGateCount = outerGateCount+1; innerGateCount < gates.size(); innerGateCount++)
            {
                System.out.println("outerGate Count: " + outerGateCount + " type: " + gates.get(outerGateCount).gtype);
                System.out.println("innerGate Count: " + innerGateCount + " type: " + gates.get(innerGateCount).gtype);
                if (inputMatchChecker(gates.get(innerGateCount), gates.get(outerGateCount)))
                {   
                    System.out.println("found clone!");
                    //add clones to eachother's list of input clones
//                    if(gates.get(innerGateCount).inputClones.isEmpty())
//                    {
//                        gates.get(innerGateCount).fromGate = gates.get(outerGateCount).fromGate;
//                    }
                    gates.get(innerGateCount).inputClones.add(gates.get(outerGateCount));
                    gates.get(outerGateCount).inputClones.add(gates.get(innerGateCount));
                }                
            }
        }
    }

    public boolean inputMatchChecker(DGate iG, DGate oG)  //checking if input lists are the same
    {
        if(iG.gtype == DGateType.uF && oG.gtype == DGateType.uF)    //weeding out
        {
            System.out.println("Both have uF gtype!");
            System.out.println("innerGate Symbol: " + iG.symbol);
            System.out.println("outerGate Symbol: " + oG.symbol);
            if(iG.symbol.equals(oG.symbol))      //further weeding              
            {
                System.out.println("same symbol!");
               if(iG.input.size() == oG.input.size())   //final weeding before "heavy" lifting (checking if inputs match up)
               {
                   List<String> iGInputList = new ArrayList<>();
                   List<String> oGInputList = new ArrayList<>();
                   
                   for(int index = 0; index < iG.input.size(); index++) //adding input names to strings
                   {
                       iGInputList.add(iG.input.get(index).name);
                       System.out.println("innerGate input name at index " + index + " is " + iG.input.get(index).name);
                       oGInputList.add(oG.input.get(index).name); 
                       System.out.println("outerGate input name at index " + index + " is " + oG.input.get(index).name);
                   }
                   Collections.sort(iGInputList);   //alphabetizing input names in lists
                   Collections.sort(oGInputList);
                   System.out.println("Checked input names!");
                   return (iGInputList.equals(oGInputList));    //checking if the sorted names are equal
               }
            }
        }
        return false;
    }
}