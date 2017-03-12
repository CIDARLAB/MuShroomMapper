/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
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
    
    ParsedUCF(String ucfPath) throws FileNotFoundException, JSONException
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
            JSONObject opObj = opArray.getJSONObject(n);        //grabbing JSONObject                               
            String operator = opObj.getString("operator");      //grabbing operator attribute of JSONObject
            opMap.put(operator, opObj);                         //placing operator, JSONObject into map
        }       
    }     
}