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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Everett
 */

/*To Do: 
Convert to JSON list of objects (where each operator in an object) format!
*/

public class ParsedUCF {
    public List<GatePrimitive> primitives = new ArrayList<GatePrimitive>();
    public List<String> operators = new ArrayList<String>();
    
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
                
                String str = "["+op+"]in["+op+"]\\s+([0-9]+)";
                System.out.println(str);
                Pattern in = Pattern.compile(str);
                Matcher inMatch = in.matcher("");
                
                str = "["+op+"]out["+op+"]\\s+([0-9]+)";
                System.out.println(str);
                Pattern out = Pattern.compile(str);
                Matcher outMatch = out.matcher("");
                
                str = "["+op+"]pic["+op+"]\\s+(\\S+)";
                System.out.println(str);
                Pattern pic = Pattern.compile(str);
                Matcher picMatch = pic.matcher("");
                
                str = "["+op+"]mint["+op+"]\\s+(.+)";
                System.out.println(str);
                Pattern mint = Pattern.compile(str);
                Matcher mintMatch = mint.matcher("");
                
                BufferedReader reader1 = Files.newBufferedReader(path);
                LineNumberReader lineReader1 = new LineNumberReader(reader1);
                
                while ((line = lineReader1.readLine()) != null) {
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
                System.out.println("op is equal to:"+op);
                primitives.add(new GatePrimitive(inputs, outputs, 
                        op, picPath, mintSyntax));
            }                
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}