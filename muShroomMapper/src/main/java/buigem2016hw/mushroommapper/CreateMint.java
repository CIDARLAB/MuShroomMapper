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
 * Takes the modified netlist graph and the parsed UCF and creates a Mint file
 * for use in Fluigi
 *
 * @author Shane
 */
public class CreateMint {
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
    public CreateMint(NetListTransition graph, ParsedUCF ucf, String fileName, int channelWidth) throws UnsupportedEncodingException, FileNotFoundException, JSONException, IOException {
        for (DGate dg : graph.gateGraph) {
            switch (dg.gtype) {
                case uF:
                    switch (dg.layer) {
                        case flow:
                            String fDevLine = dg.opInfo.getString("mint") + ";";
                            fDevLine = fDevLine.replaceAll("NAME", "fDevice" + flowDeviceCount + "_" + dg.opInfo.getString("name"));
                            dg.mintName = "fDevice" + flowDeviceCount + "_" + dg.opInfo.getString("name");

                            flowDevices += fDevLine;
                            flowDevices += "\n";
                            dg.isWritten = true;
                            flowDeviceCount++;
                            break;

                        case control:       //TODO: test how control devices print
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
                    switch (dg.layer) {
                        case flow:                                  //flow inport
                            flowInPorts += "flowInPort" + flowInPortCount + ",";
                            dg.mintName = "flowInPort" + flowInPortCount;

                            dg.isWritten = true;
                            flowInPortCount++;
                            break;

                        case control:                               //control inport
                            controlInPorts += "controlInPort" + controlInPortCount + ",";
                            dg.mintName = "controlInPort" + controlInPortCount;

                            dg.isWritten = true;
                            controlInPortCount++;
                            break;

                        default:
                            System.out.println("unlayered gate! UCF/Bug?");
                            break;
                                }
                            break;
                        
                case uF_OUT:
                    switch (dg.layer) {
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

        for (DWire dw : graph.wireGraph) {
            int currentInTerm;
            int currentOutTerm;
            JSONArray inTermsArray;
            if (dw.isWritten == false) {
                switch (dw.wtype) {
                    case cinput:
                        //inTermsArray = dw.toGate.opInfo.getJSONArray("inputTerms");     //need to be catch all with if inTermsFlag

                        currentInTerm = dw.toGate.opInfo.getInt("controlTerms");

                        controlChannels += "CHANNEL controlChannel" + controlChannelCount + " from ";
                        controlChannels += dw.fromGate.mintName + " " + dw.fromGate.outTermVal + " to ";
                        controlChannels += dw.toGate.mintName + " " + currentInTerm + " w=" + channelWidth + ";\n";
                        dw.isWritten = true;
                        controlChannelCount++;

                        break;

                    case finput:
                        if (dw.toGate.layer.equals(LayerType.control)) {
                            currentInTerm = dw.toGate.opInfo.getInt("inputTerms");
                        } else {
                            inTermsArray = dw.toGate.opInfo.getJSONArray("inputTerms");
                            currentInTerm = inTermsArray.getInt(dw.toGate.inTermInd);
                            dw.toGate.inTermInd++;
                        }

                        flowChannels += "CHANNEL flowChannel" + flowChannelCount + " from ";
                        flowChannels += dw.fromGate.mintName + " " + dw.fromGate.outTermVal + " to ";
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

                    case fchannel:
                        inTermsArray = dw.toGate.opInfo.getJSONArray("inputTerms");     //need to be catch all with if inTermsFlag
                        currentInTerm = inTermsArray.getInt(dw.toGate.inTermInd);
                        dw.toGate.inTermInd++;

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
        flowPorts = flowInPorts + flowOutPorts;
        flowPorts = flowPorts.substring(0, flowPorts.length() - 1);   //removing extra comma

        controlPorts = controlInPorts + controlOutPorts;
        controlPorts = controlPorts.substring(0, controlPorts.length() - 1); //removing extra comma

        printMint(fileName);
    }

    public void printMint(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter mintWriter = new PrintWriter(fileName, "UTF-8");
        mintWriter.println("# .uf output by muShroomMapper");
        mintWriter.println("3D DEVICE testDevice");
        mintWriter.println("");
        
        mintWriter.println("LAYER FLOW\n\n");

        mintWriter.println("PORT " + flowPorts + " r=" + portRadius + ";\n");
        mintWriter.println(flowDevices);
        mintWriter.println(controlDevices); //because 3dvalve is bugged in fluigi
        mintWriter.println(flowChannels);

        mintWriter.println("");
        mintWriter.println("END LAYER");
        mintWriter.println("");

        mintWriter.println("LAYER CONTROL\n\n");

        mintWriter.println("PORT " + controlPorts + " r=" + portRadius + ";\n");
        // mintWriter.println(controlDevices); //because 3dvalve is bugged in fluigi
        mintWriter.println(controlChannels);

        mintWriter.println("");
        mintWriter.println("END LAYER");

        mintWriter.close();
    }
}
