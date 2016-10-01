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
    public String deviceName;
    public List<DGate> gateGraph = new ArrayList<>();
    public List<DWire> wireGraph = new ArrayList<>();
    public List<DGate> fluidInputs = new ArrayList<>();
    public ArrayList<ArrayList<DGate>> fluidLines = new ArrayList<ArrayList<DGate>>();
       
    public NetListTransition(ParsedUCF ucf, String vFilePath) throws JSONException
    {
        line = "";
        line = Utilities.getFileContentAsString(vFilePath);
        walker = VerilogFluigiGrammar.getuFWalker(line);
        int gateCount=0;           //counts the number of gates throughout for use with IDing gates for visualization
        deviceName = walker.details.modulename;
        System.out.println("Creating the microfluidic graph...");
        for(DGate dg:walker.netlist)
        {
            switch(dg.gtype)
            {
                case uF:
                    System.out.println("Graphing operation microfluidic gate...");  
                    dg.opInfo = ucf.opMap.get(dg.symbol);   //adding UCF-operator info to each operation gate
                    
                    if (dg.opInfo.getString("layer").equals("flow"))
                    {
                        System.out.println("... On the flow layer...");
                        dg.layer = LayerType.flow;
                    }                 //filling out layer of uF operation gates
                    else if (dg.opInfo.getString("layer").equals("control"))
                    {
                        System.out.println("... On the control layer...");
                        dg.layer = LayerType.control;
                    }
                    else System.out.println("Operator " + dg.symbol + " without layer attribute! Check UCF!");
//                    System.out.println("layer: " + dg.layer);
                    dg.inTermInd = 0;
                    dg.outTermInd = 0;
        
                    dg.inTermFlag = true;                   //these operation gates will have their in/outTerm JSONArrays defined in their opInfo
                    dg.outTermFlag = true;
                    
                    dg.output.fromGate = dg;
                    wireGraph.add(dg.output);  
                    for (DWire in:dg.input)
                    {                        
                        in.toGate = dg; 
                        wireGraph.add(in);
                    }
                    
                    dg.gindex = gateCount;
                    gateCount++;
                    System.out.println("Done with gate.");
                    break;
                    
                case uF_IN:  
                    System.out.println("Graphing input microfluidic gate...");
                    dg.output.fromGate = dg;
                    wireGraph.add(dg.output);
                    if (dg.layer == LayerType.flow)
                    {
                        System.out.println("... On the flow layer...");
                        dg.outTermVal = 2;
                        dg.inTermVal = -1;                          //an input gate doesn't have an input from any other gate
                        fluidInputs.add(dg);
                    }
                    else if (dg.layer == LayerType.control)
                    {
                        System.out.println("... On the control layer...");
                        dg.outTermVal = 1;
                        dg.inTermVal = -1;       
                    }
                                   
                    dg.gindex = gateCount;
                    gateCount++;
                    System.out.println("Done with gate.");
                    break;
                    
                case uF_OUT:
                    System.out.println("Graphing output microfluidic gate ");
                    dg.input.get(0).toGate = dg;
                    wireGraph.add(dg.input.get(0));
                    
                    dg.inTermVal = 4;
                    dg.outTermVal = -1;                        //an output gate doesn't output to any other gate
                    
                    dg.gindex = gateCount;
                    gateCount++;
                    System.out.println("Done with gate.");
                    break;       
            }
        }
        gateGraph = walker.netlist;
    } 
    public void fluidLiner()        //tracing fluidLines in microfluidic device
    {
        DGate currentGate;
        int lineCount = 0;
        for (DGate startPoint:fluidInputs)
        {
            currentGate = startPoint;
            while (currentGate.gtype != DGateType.uF_OUT)
            {
                (fluidLines.get(lineCount)).add(currentGate);
                currentGate = currentGate.output.toGate;
            }
            lineCount++;
        }
    }    
}
