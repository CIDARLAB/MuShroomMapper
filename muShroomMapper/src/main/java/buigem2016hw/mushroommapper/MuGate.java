/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import java.util.List;
import org.cellocad.BU.dom.DGate;
import org.json.JSONObject;

/**
 *
 * @author Shane
 */
public class MuGate extends DGate
{
    public JSONObject opInfo;   //from UCF, contains all info regarding the microfluidic operation the gate performs
    public String type;
    public String layer;
    public String ioWire;
    public int io;
    public String mintName;         //name used for this gate in mint file
    public int inTermInd;       //current index of inTerm JSONArray
    public int outTermInd;      //current index of outTerm JSONArray
    public int inTermVal = 4;       //by default input terminal is 4th orientation (left) EG: *** ----->IN[(Device)]OUT-----> ***
    public int outTermVal = 2;      //by default output terminal is 2nd orientation (right)
    public boolean inTermFlag = false;      //flag that is true if inTerm JSONArray exists (used for gates with nonstandard orientations)
    public boolean outTermFlag = false;     //flag that is true if outTerm JSONArray exists (used for gates with nonstandard orientations)
    public boolean isWritten = false;
    public List<MuWire> muInput;
    public MuWire muOutput;
        
    public MuGate(String type, String wireName) //
    {
        this.type = type;
        this.ioWire = wireName;
        this.symbol = "0";
    }
    public MuGate(DGate dg, String type)    //conversion from DGate
    {
        symbol = dg.symbol;
        output = dg.output;         //the output wire of the gate
        input = dg.input;           //list of input wires to the gate
        gname = dg.gname;
        gindex = dg.gindex;
        gatestage = dg.gatestage;
        picpath = dg.picpath;
        inTermInd = 0;
        outTermInd = 0;
        this.type=type;
    }
    
    public void addOpInfo(JSONObject JSONInput) //linking JSON info
    {
        opInfo = JSONInput;
    }
}
