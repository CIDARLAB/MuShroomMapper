/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import static org.cidarlab.fluigi.fluigi.PiranhaPlanner.processMintDevice;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Shane
 */

public class ParsedUCF {
    
    public Map<String, JSONObject> opMap = new HashMap();
    public int channelWidth;
    public int portRadius;
    public boolean autoSplitMerge;
    
    ParsedUCF(String ucfPath) throws FileNotFoundException, JSONException, UnsupportedEncodingException, IOException
    {
        String ucfString = new Scanner(new File(ucfPath)).useDelimiter("\\Z").next();   //take in file as string
        JSONArray opArray = new JSONArray(ucfString);                                   //make string a JSONArray
        for(int n = 0; n < opArray.length(); n++)                                       //place each JSONObject in JSONArray into map as <JSONObject.operator, JSONObject
        {
            if (n == 0)                                             //the general info block -- could reformat to bring this out of loop
            {
                JSONObject opObj = opArray.getJSONObject(n);        //grabbing JSONObject
                channelWidth = opObj.getInt("channelWidth");
                portRadius = opObj.getInt("portRadius");
                autoSplitMerge = opObj.getBoolean("automatedSplitMerge");
                continue;   //moving to next block
            }
            if (n == 1)                                             //vulcan hw info block
            {
                JSONObject opObj = opArray.getJSONObject(n);
                if(opObj.getBoolean("Are you using Vulcan?") == true)
                {
                    JSONObject pcrObject = opObj.getJSONObject("Vulcan Hardware").getJSONObject("PCR");
                    int numBends = pcrObject.getInt("Number of bends (1 bend = 1 'period')");
                    int bendSpacing = pcrObject.getInt("Spacing between bends (um)");
                    int bendLength = pcrObject.getInt("Length of each bend (um)");
                    int channelWidth = pcrObject.getInt("Width of channels");
                    
                    String pcrMINTString = "3D DEVICE integratedPCR\n" + "\n" + "LAYER FLOW\n" + "\n" + "PORT flowInPort0,outPort0 r=2020;\n" + "\n" + "H MIXER pcrMixer numBends="+numBends+" bendSpacing="+bendSpacing+" bendLength="+bendLength+" channelWidth="+channelWidth+";\n" + "\n" + "CHANNEL flowChannel0 from flowInPort0 2 to pcrMixer 1 w="+channelWidth+";\n" +
"CHANNEL flowChannel1 from pcrMixer 2 to outPort0 4 w="+channelWidth+";\n" + "\n" + "END LAYER\n" + "\n" + "LAYER CONTROL\n" + "\n" + "END LAYER";
                    PrintWriter mintWriter = new PrintWriter("output/pcr.uf", "UTF-8");
                    mintWriter.println(pcrMINTString);
                    mintWriter.close();
                    processMintDevice("output/pcr.uf", "fluigi.ini", "sej", true);                                       
                }
                continue;
            }
            JSONObject opObj = opArray.getJSONObject(n);        //grabbing JSONObject                               
            String operator = opObj.getString("operator");      //grabbing operator attribute of JSONObject
            opMap.put(operator, opObj);                         //placing operator, JSONObject into map
        }       
    }     
}