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
    
    
    public void CreateMint(netListTransition modifiedGraph, ParsedUCF ucf) throws FileNotFoundException, UnsupportedEncodingException{    //are these throws necessary?
        Scanner ufNameInput = new Scanner(System.in);  // Reading from System.in
        System.out.println("What would you like to name your .uf file? ");
        fileName = ufNameInput.nextLine(); // Scans the next token of the input as an int.
        PrintWriter mintWriter = new PrintWriter(fileName, "UTF-8");
        mintWriter.println("# .uf output by muShroomMapper");
        mintWriter.println("DEVICE testDevice");
        mintWriter.println("");
        mintWriter.println("LAYER FLOW");
        mintWriter.println("");
        mintWriter.println("PORT " + flowPorts + ";");
        //print gates
        //print channels connecting
        mintWriter.println("END LAYER");
        mintWriter.println("LAYER CONTROL");
        mintWriter.println("END LAYER");
        
        
}
    
    public String getUCFMint(char op, String ucfFileName){
     //open file(ucfFileName)
     //search for op+"mint"+op
     String mintCode = "a"; //where a is the regexed line after op+mint+op
     return mintCode;        
    }

    public void createChannel(String netList, String wireName){
        //for line in netList
            //
    }
}