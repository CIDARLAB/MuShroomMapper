/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buigem2016hw.mushroommapper;

import org.cellocad.BU.dom.DWire;

/**
 *
 * @author Shane
 */

//To do: Convert to extending DWire? For Sure

public class MuWire extends DWire 
{
//    public MuGate fromGate;
//    public MuGate toGate;
    public String type;
//    public boolean isWritten = false;
//    public MuWire dupChannel;
//    public String layer;

    public MuWire(DWire dw)
    {
        this.wtype = dw.wtype;
        this.name = dw.name;
    }
    public MuWire(String wireName)
    {
        name=wireName;
        type = "connector";
    }
    public MuWire(String wireName, int io, MuGate ioGate)
    {
        if (io == 0) {
            fromGate = ioGate;
            type = "input";
        }
        else{
            toGate = ioGate;
            type = "output";
        }
        name=wireName;
    }
//    public void setOrigin(MuGate originGate)
//    {
//        fromGate = originGate;
//    }
//    public void setDestination(MuGate destGate)
//    {
//        toGate = destGate;
    }
}
