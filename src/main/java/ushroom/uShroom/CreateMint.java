/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 *
 * @author Riastradh
 */
public class CreateMint {
    String line = "";
    String fileName;
    public void CreateMint(netListTransition modifiedGraph, ) throws FileNotFoundException, UnsupportedEncodingException{    //are these throws necessary?
        Scanner ufNameInput = new Scanner(System.in);  // Reading from System.in
        System.out.println("What would you like to name your .uf file? ");
        fileName = ufNameInput.nextLine(); // Scans the next token of the input as an int.
        PrintWriter mintWriter = new PrintWriter(fileName, "UTF-8");
        
        
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
