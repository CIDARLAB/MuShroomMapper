/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Everett
 */
public class ParsedUCF {
    public List<GatePrimitive> gates;
    public List<String> operators;
    
    ParsedUCF(String filepath) throws FileNotFoundException, IOException{
        
        File file = new File(filepath);
        Path path = Paths.get(filepath);
        
        Pattern operator = Pattern.compile("^(.)in");
        Matcher opMatch = operator.matcher("");
        
        try(
            BufferedReader reader = Files.newBufferedReader(path);
            LineNumberReader lineReader = new LineNumberReader(reader);
                )
        {
            String line = null;
            while ((line = lineReader.readLine()) != null) {
                opMatch.reset(line); //reset the input
                if (opMatch.find()) {
                    if(!(operators.contains(opMatch.group(1)))){
                        operators.add(opMatch.group(1));
                    }
                }
            }
            System.out.println(operators.toString());
            lineReader.setLineNumber(0);
            for (String operator1 : operators) {
                int inputs = -1;
                int outputs = -1; 
                String picPath = null;
                String op = operator1;
                String mintSyntax = null;
                Pattern in = Pattern.compile("["+op+"]in["+op+"]\\s+([0-9]+)");
                Matcher inMatch = in.matcher("");
                Pattern out = Pattern.compile("["+op+"]out["+op+"]\\s+([0-9]+)");
                Matcher outMatch = out.matcher("");
                Pattern pic = Pattern.compile("["+op+"]pic["+op+"]\\s+(\\S+)");
                Matcher picMatch = pic.matcher("");
                Pattern mint = Pattern.compile("["+op+"]mint["+op+"]\\s+(.+)");
                Matcher mintMatch = mint.matcher("");
                while ((line = lineReader.readLine()) != null) {
                    inMatch.reset(line);
                    outMatch.reset(line);
                    picMatch.reset(line);
                    mintMatch.reset(line);
                    if(inMatch.find()){
                        inputs = Integer.parseInt(inMatch.group(1));
                        
                    }else if(outMatch.find()){
                        outputs = Integer.parseInt(outMatch.group(1));
                        
                    }else if(picMatch.find()){
                        picPath = picMatch.group(1);
                        
                    }else if(mintMatch.find()){
                        mintSyntax = mintMatch.group(1);
                    }               
                }
                gates.add(new GatePrimitive(inputs, outputs, 
                        op, picPath, mintSyntax));
            }                
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}