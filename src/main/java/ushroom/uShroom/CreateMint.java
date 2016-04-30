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
 * @author Riastradh
 */
public class CreateMint {
    String line = "";
    String fileName;
    //String portRadius = "100";
    //String channelWidth = "100";
    String flowPorts = "";
    //String controlPorts = ""; //control layer to be implemented
    List<String> channelList;
    
    
    public CreateMint(netListTransition graph, ParsedUCF ucf) throws UnsupportedEncodingException, FileNotFoundException{    //are these throws necessary?
        Scanner ufNameInput = new Scanner(System.in);  // Reading from System.in
        System.out.println("What would you like to name your .uf file? ");
        fileName = ufNameInput.nextLine(); // Scans the next token of the input as an int.
        PrintWriter mintWriter = new PrintWriter(fileName, "UTF-8");
        mintWriter.println("# .uf output by muShroomMapper");
        mintWriter.println("DEVICE testDevice");
        mintWriter.println("");
        mintWriter.println("LAYER FLOW");
        mintWriter.println("");
        int inPortCount = 1;
        int outPortCount = 1;
        //adding inports
        for(String port:graph.inPorts){
            flowPorts+="inPort"+inPortCount+",";
            inPortCount++;
        }
        //adding outports
        for (String port:graph.outPorts){
            flowPorts+="outPort"+outPortCount;
            if (outPortCount == graph.outPorts.size()) flowPorts+=";";
            else flowPorts+=",";
            outPortCount++;
        }
        mintWriter.println("PORT " + flowPorts);
        //print gates
        int deviceCount = 0;
        for (muGate mg:graph.gates){
            String mint = mg.primitive.mintSyntax;
            mint = mint.replaceAll("NAME", "Device"+deviceCount);
            //System.out.println(mg.primitive.mintSyntax);
            mintWriter.println(mint);
            deviceCount++;
        }
        //print channels connecting
        //for ()
        mintWriter.println("END LAYER");
        mintWriter.println("LAYER CONTROL");
        mintWriter.println("END LAYER");
        mintWriter.close();
    }

    public void createChannel(String netList, String wireName){
        //for line in netList
            //
    }
}
