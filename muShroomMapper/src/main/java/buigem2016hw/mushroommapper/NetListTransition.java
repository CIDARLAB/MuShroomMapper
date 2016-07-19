/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import java.util.ArrayList;
import java.util.List;
import org.cellocad.BU.dom.DGate;
import org.cellocad.BU.dom.DGateType;
import org.cellocad.BU.dom.DWire;
import org.cellocad.BU.dom.DWireType;
import org.cellocad.BU.dom.LayerType;
import org.cellocad.BU.fluigi.VerilogFluigiGrammar;
import org.cellocad.BU.fluigi.VerilogFluigiWalker;
import org.cellocad.BU.netsynth.Utilities;
import org.json.JSONException;

/**
 * @author Shane
 * creates our graph object from the output of NetSynth (parsed Verilog)
 */

public class NetListTransition 
{
    public String filepath;
    public String line;
    VerilogFluigiWalker walker;
    public List<String> inPorts = new ArrayList<>();
    public List<String> outPorts = new ArrayList<>();
    public List<MuWire> wires = new ArrayList<>();
    public List<MuGate> gates = new ArrayList<>();
       
    public NetListTransition(ParsedUCF ucf, String vFilePath) throws JSONException
    {
        line = "";
        line = Utilities.getFileContentAsString(vFilePath);
        walker = VerilogFluigiGrammar.getuFWalker(line);
        inPorts = walker.details.inputs;                //create list of in ports from inputList command
        outPorts = walker.details.outputs;              //create list of out ports from outputList command
        int gateCount=0;           //counts the number of gates throughout for use with IDing gates for visualization
//        for(DGate dg:walker.netlist)                    //translating all operation gates as MuGates
//        {
//            MuGate opGate = new MuGate(dg, "gate");
//            gates.add(opGate);
//        }
        for(DGate dg:walker.netlist)
        {
            switch(dg.gtype)
            {
                case uF:
                    dg.opInfo = ucf.opMap.get(dg.symbol);   //adding UCF-operator info to each operation gate
                    
                    if (dg.opInfo.getString("layer").equals("flow")) dg.layer = LayerType.flow;                 //filling out layer of uF operation gates
                    else if (dg.opInfo.getString("layer").equals("control")) dg.layer = LayerType.control;
                    else System.out.println("Operator " + dg.symbol + " without layer attribute! Check UCF!");
                    
                    dg.inTermFlag = true;                   //these operation gates will have their in/outTerm JSONArrays defined in their opInfo
                    dg.outTermFlag = true;
                    
                    dg.output.fromGate = dg;
                    for (DWire in:dg.input)
                    {
                        in.toGate = dg;    
                    }
                    
                    dg.gindex = gateCount;
                    gateCount++;
                    break;
                    
                case uF_IN:
                    dg.output.fromGate = dg;
                    
                    dg.outTermVal = 2;
                    dg.inTermVal = -1;                          //an input gate doesn't have an input from any other gate
                    
                    dg.gindex = gateCount;
                    gateCount++;
                    break;
                    
                case uF_OUT:
                    dg.input.get(0).toGate = dg;
                    
                    dg.inTermVal = 4;
                    dg.outTermVal = -1;                        //an output gate doesn't output to any other gate
                    
                    dg.gindex = gateCount;
                    gateCount++;
                    break;       
            }
        }
        
//        for(String wireName:walker.details.inputs)      //translating all inputs as both input MuGates and connecting MuWires   TODO: Clean this up
//        {
//            MuGate in = new MuGate("input", wireName);  //the MuGate for the inport
//            MuWire w = new MuWire(wireName, 0, in);     //the MuWire coming out of the inport
//
//            wires.add(w);
//            gates.add(in);
//        }
//        for(String wireName:walker.details.outputs)     //translating all outputs as both output MuGates and connecting MuWires TODO: Clean this up
//        {
//            MuGate out = new MuGate("output", wireName);//the MuGate for the outport
//            wires.add(new MuWire(wireName, 1 , out));   //the MuWire off the outport
//            gates.add(out);
//        }
        combineValveChannels();
    }
    
    public void combineValveChannels()      //identifying duplicate channels made by a valve splitting up a flow channel into input and output
    {
        for(MuGate gate:gates)
        {
            int fInCount = 0;
            int cInCount = 0;
            for(DWire input:gate.input)
            {
                if(input.wtype.equals("fchan") || input.wtype.equals("finput")) fInCount++;
                else if (input.wtype.equals("cchan") || input.wtype.equals("cinput")) cInCount++;
            }
            if (fInCount==1 && cInCount==1)
            {
                for (MuWire input:gate.muInput)
                {
                    if(input.wtype.equals("fchan") || input.wtype.equals("finput")) //TODO: needs work
                    {
                        input.dupChannel = gate.muOutput;   //for identifying duplicate channel from valve
                        gate.muOutput.isWritten = true;
                    }
                }
            }
        }
    }
}
