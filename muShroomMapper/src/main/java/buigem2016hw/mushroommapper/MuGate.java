/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import org.cellocad.BU.dom.DGate;
import org.json.JSONObject;

/**
 *
 * @author Shane
 */
public class MuGate extends DGate{
    public JSONObject opInfo;
    public String type;
    public String ioWire;
    public int io;
    public String mintName;
    
    public MuGate(String type, String wireName){
        this.type = type;
        this.ioWire = wireName;
        this.symbol = "0";
    }
    public MuGate(DGate dg, String type){
        symbol = dg.symbol;
        output = dg.output;
        input = dg.input;
        gname = dg.gname;
        gindex = dg.gindex;
        gatestage = dg.gatestage;
        picpath = dg.picpath;
        this.type=type;
    }
    
    public void addOpInfo(JSONObject JSONInput){
        opInfo = JSONInput;
    }
}
