/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import java.util.ArrayList;
import java.util.List;
import org.cellocad.BU.dom.DGate;
import org.cellocad.BU.dom.DWire;
import org.cellocad.BU.fluigi.VerilogFluigiGrammar;
import org.cellocad.BU.fluigi.VerilogFluigiWalker;
import org.cellocad.BU.netsynth.Utilities;

/**
 * @author Shane
 * creates our graph object from the output of NetSynth (parsed Verilog)
 */

/**To do:
 * Note the wires that are split apart by a valve
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
       
    public NetListTransition(ParsedUCF ucf, String vFilePath)
    {
        line = "";
        line = Utilities.getFileContentAsString(vFilePath);
        walker = VerilogFluigiGrammar.getuFWalker(line);
        inPorts = walker.details.inputs;                //create list of in ports from inputList command
        outPorts = walker.details.outputs;              //create list of out ports from outputList command
        //create list of channels from wiresList command
        int gateCount=0;            //counts the number of gates throughout for use with IDing gates for visualization
        for(DGate dg:walker.netlist)                    //translating all operation gates as MuGates
        {
            MuGate opGate = new MuGate(dg, "gate");
            gates.add(opGate);
            opGate.gindex = gateCount;
            gateCount++;
        }
        for (MuGate mg:gates)                           //adding opInfo to each operation gate
        {
            mg.addOpInfo(ucf.opMap.get(mg.symbol));
            mg.inTermFlag = true;       //these operation gates will have their in/outTerm JSONArrays defined in their opInfo
            mg.outTermFlag = true;
        }
        for(String wireName:walker.details.wires)       //translating all traditional verilog wires to MuWires
        {
            wires.add(new MuWire(wireName));
            System.out.println(wireName);
        }
        for(String wireName:walker.details.inputs)      //translating all inputs as both input MuGates and connecting MuWires
        {
            MuGate in = new MuGate("input", wireName);  //the MuGate for the inport
            MuWire w = new MuWire(wireName, 0, in);     //the MuWire coming out of the inport
            in.outTermVal = 2;
            in.inTermVal = -1;                          //an input gate doesn't have an input from any other gate
            wires.add(w);
            gates.add(in);
            in.gindex = gateCount;
            gateCount++;
            //System.out.println(wireName);
        }
        for(String wireName:walker.details.outputs)     //translating all outputs as both output MuGates and connecting MuWires
        {
            MuGate out = new MuGate("output", wireName);//the MuGate for the outport
            wires.add(new MuWire(wireName, 1 , out));   //the MuWire off the outport
            out.inTermVal = 4;
            out.outTermVal = -1;                        //an output gate doesn't output to any other gate
            gates.add(out);
            out.gindex = gateCount;
            gateCount++;
        }

        parseNetList();
        combineValveChannels();
      


    }
    public void parseNetList()
    {

        //filling out MuWire objects
        for(MuWire wire:wires)                                                      //goes through all wires
        {
            for(MuGate gate:gates)                                                  //goes through all gates
            {
                if ("input".equals(wire.type))                                      //if wire is an input wire
                {
                    for(DWire input:gate.input)                                     //for through all inputs of the gate
                    {
                        if(wire.name.equals(input.name)) wire.setDestination(gate); //if input wire is input to the gate, set gate as wire's toGate
                    }
                }
                else if("output".equals(wire.type))                                 //if wire is an output wire
                {
                    if(wire.name.equals(gate.output.name)) wire.setOrigin(gate);    //if output wire is output of the gate, set gate as wire's fromGate
                }
                else                                                                //if wire isn't an input/output wire
                {  
                    if(wire.name.equals(gate.output.name)) wire.setOrigin(gate);    //if wire is output of gate, set gate as wire's fromGate
                    else for(DWire input:gate.input)                                //if not output of gate, go through all inputs of gate 
                    {
                        if(wire.name.equals(input.name)) wire.setDestination(gate); //if wire is an input of the gate, set gate as wire's toGate
                    }
                }
            }
        }      
    }
    
    public void combineValveChannels()      //getting rid of duplicate channels made by a valve splitting up a flow channel into input and output
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
                    if(input.wtype.equals("fchan") || input.wtype.equals("finput")) 
                    {
                        
                        gate.muOutput.isWritten = true;
                    }
                }
            }
        }
    }
}