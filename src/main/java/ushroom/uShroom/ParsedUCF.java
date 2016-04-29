/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static javax.xml.transform.OutputKeys.ENCODING;
import org.cellocad.BU.netsynth.Utilities;

/**
 *
 * @author Everett
 */
public class ParsedUCF {
    public List<GatePrimitive> gates;
    
    
    public void ParsedUCF(){
        
    }
    public void ParsedUCF(String filepath) throws FileNotFoundException, IOException{
        File file = new File(filepath);
        
        Pattern operator = Pattern.compile("^(.)in");
        Matcher opMatch = operator.matcher("");
        
            Path path = Paths.get(filepath);
    try (
      BufferedReader reader = Files.newBufferedReader(path, ENCODING);
      LineNumberReader lineReader = new LineNumberReader(reader);
    ){
      String line = null;
      while ((line = lineReader.readLine()) != null) {
        opMatch.reset(line); //reset the input
        if (opMatch.find()) {
          for(int i = 0; i < gates.size();i++){
              
        }
      }      
    }    
    catch (IOException ex){
      ex.printStackTrace();
    }
        
        Pattern in = Pattern.compile(".in.\\s+([0-9]+)");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
             while ((line = br.readLine()) != null) {
                 // process the line.
                 
             }
    }
}
        
        
    }
    
    
}
