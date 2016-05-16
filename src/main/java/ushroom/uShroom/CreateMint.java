/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Scanner;

/**
 *  Takes the modified netlist graph and the parsed UCF and creates a Mint file for use in Fluigi
 * @author Shane
 */

//To do: set up Mint control layer writing

public class CreateMint {
    String line = "";
    String fileName;
    //String portRadius = "100";
    //String channelWidth = "100";
    String flowPorts = "";
    //String controlPorts = ""; //control layer to be implemented
    List<String> channelList;
    
    
    public CreateMint(netListTransition graph, ParsedUCF ucf) throws UnsupportedEncodingException, FileNotFoundException{
        Scanner ufNameInput = new Scanner(System.in);  // Reading from System.in
        System.out.println("What would you like to name your .uf file? ");
        fileName = ufNameInput.nextLine(); // Scans the next token of the input as an int.
        PrintWriter mintWriter = new PrintWriter(fileName, "UTF-8");
        mintWriter.println("# .uf output by muShroomMapper");
        mintWriter.println("DEVICE testDevice");
        mintWriter.println("");
        mintWriter.println("LAYER FLOW");
        mintWriter.println("");
        int inPortCount = 0;
        int outPortCount = 0;
        //adding inports
        for(muGate port:graph.gates){
            if(port.type.equals("input")){
                flowPorts+="inPort"+inPortCount+",";
                port.mintName = "inPort"+inPortCount;
                inPortCount++;
            }
        }
        //adding outports
        for (muGate port:graph.gates){
            if(port.type.equals("output")){
                flowPorts+="outPort"+outPortCount;
                port.mintName = "outPort"+outPortCount;
                if (outPortCount == (graph.outPorts.size()-1)) flowPorts+=", r=100;";
                else flowPorts+=",";
                outPortCount++;
            }
        }
        mintWriter.println("PORT " + flowPorts);
        //print gates
        int deviceCount = 0;
        for (muGate mg:graph.gates){
            if (mg.type.equals("gate")){
                String mint = mg.primitive.mintSyntax + ";";
                mint = mint.replaceAll("NAME", "Device"+deviceCount);
                mg.mintName = "Device"+deviceCount;
                //System.out.println(mg.primitive.mintSyntax);
                mintWriter.println(mint);
                deviceCount++;
            }
        }
        int channelCount = 0;
        for(Wire w : graph.wires){
            mintWriter.println("CHANNEL "+"channel"+channelCount+" from "+ w.fromGate.mintName+" 2 to " +w.toGate.mintName+" 4 w=100;");
            channelCount++;
        }
        
        mintWriter.println("");
        mintWriter.println("END LAYER");
        mintWriter.println("");
        mintWriter.println("LAYER CONTROL");
        mintWriter.println("");
        mintWriter.println("#To be implemented");
        mintWriter.println("");
        mintWriter.println("END LAYER");
        mintWriter.close();
    }

    public void createChannel(String netList, String wireName){
        //for line in netList
            //
    }
}
