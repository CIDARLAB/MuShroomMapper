/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import org.cellocad.BU.dom.DGate;

/**
 *
 * @author Shane
 */
public class muGate extends DGate{
    public GatePrimitive primitive;
    public String type;
    public String ioWire;
    public int io;
    public String mintName;
    
    public muGate(String type, String wireName){
        this.type = type;
        this.ioWire = wireName;
        this.symbol = "0";
    }
    public muGate(DGate dg, String type){
        symbol = dg.symbol;
        output = dg.output;
        input = dg.input;
        gname = dg.gname;
        gindex = dg.gindex;
        gatestage = dg.gatestage;
        picpath = dg.picpath;
        this.type=type;
    }
    
    public void addPrimitive(GatePrimitive prim){
        primitive = prim;
    }
}
