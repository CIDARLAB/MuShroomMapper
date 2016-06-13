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
 *
 * @author Shane
 * creates our graph object from the output of NetSynth (parsed Verilog)
 */

/*To do:

*/
public class NetListTransition {
    public String filepath;
    public String line;
    VerilogFluigiWalker walker;
    public List<String> inPorts = new ArrayList<>();
    public List<String> outPorts = new ArrayList<>();
    public List<MuWire> wires = new ArrayList<>();
    public List<MuGate> gates = new ArrayList<>();
       
    public NetListTransition(ParsedUCF ucf, String vFilePath){
        line = "";
        line = Utilities.getFileContentAsString(vFilePath);
        walker = VerilogFluigiGrammar.getuFWalker(line);
        //create list of in ports from inputList command
        inPorts = walker.details.inputs;
        //create list of out ports from outputList command
        outPorts = walker.details.outputs;
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
            in.inTermVal = -1;
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
            out.outTermVal = -1;
            gates.add(out);
            out.gindex = gateCount;
            gateCount++;
        }

        parseNetList();
      


    }
    public void parseNetList(){

        //filling out MuWire objects
        for(MuWire wire:wires){
            for(MuGate gate:gates){
                if ("input".equals(wire.type)){
                    for(DWire input:gate.input){
                        if(wire.name.equals(input.name)) wire.setDestination(gate);
                    }
                }
                else if("output".equals(wire.type)){
                    if(wire.name.equals(gate.output.name)) wire.setOrigin(gate);
                }
                else
                {  
                    if(wire.name.equals(gate.output.name)) wire.setOrigin(gate);
                    else for(DWire input:gate.input)
                    {
                        if(wire.name.equals(input.name)) wire.setDestination(gate);
                    }
                }
            }
        }      
    }
}