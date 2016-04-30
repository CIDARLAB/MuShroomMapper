/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

import org.cellocad.BU.dom.DGate;

/**
 *
 * @author Riastradh
 */
public class muGate extends DGate{
    public GatePrimitive primitive;
    
    public muGate(DGate dg){
        symbol = dg.symbol;
        output = dg.output;
        input = dg.input;
        gname = dg.gname;
        gindex = dg.gindex;
        gatestage = dg.gatestage;
        picpath = dg.picpath;
    }
    
    public void addPrimitive(GatePrimitive prim){
        primitive = prim;
    }
}
