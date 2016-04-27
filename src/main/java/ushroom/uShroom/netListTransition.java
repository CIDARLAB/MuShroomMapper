/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import java.util.ArrayList;
import java.util.List;
import org.cellocad.BU.dom.DGate;
import org.cellocad.BU.dom.DWire;
import org.cellocad.BU.fluigi.VerilogFluigiGrammar;
import org.cellocad.BU.fluigi.VerilogFluigiWalker;
import org.cellocad.BU.netsynth.NetSynth;
import static org.cellocad.BU.netsynth.NetSynth.printuFGate;
import org.cellocad.BU.netsynth.Utilities;

/**
 *
 * @author Riastradh
 */
public class netListTransition {
    public String filepath;
    public String line;
    VerilogFluigiWalker walker;
    public List<String> inPorts;
    public List<String> outPorts;
    public List<Wire> wires;
    public List<DGate> gates;
    
    
    public netListTransition(){
        filepath = Utilities.getNetSynthResourcesFilepath()+"fluigi.v"; //change filepath to suit our needs
        line = "";
        line = Utilities.getFileContentAsString(filepath);
        walker = VerilogFluigiGrammar.getuFWalker(line);
        //create list of in ports from inputList command
        inPorts = walker.details.inputs;
        //create list of out ports from outputList command
        outPorts = walker.details.outputs;
        //create list of channels from wiresList command
        for(String wireName:walker.details.wires){
            wires.add(new Wire(wireName));
        }
        //list of gates
        gates = walker.netlist;
    }
    public void parseNetList(){

        //filling out Wire objects
        for(Wire wire:wires){
            for(DGate gate:gates){
                if(wire.name == gate.output.name) wire.setOrigin(gate);
                else for(DWire input:gate.input){
                    if(wire.name==input.name) wire.setDestination(gate);
                }
        }
    }
}
