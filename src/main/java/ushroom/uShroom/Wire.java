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
public class Wire {
    public String name;
    public muGate fromGate;
    public muGate toGate;
    public String type;

    public Wire(String wireName){
        name=wireName;
        type = "connector";
    }
    public Wire(String wireName, int io, muGate ioGate){
        if (io == 0) {
            toGate = ioGate;
            type = "input";
        }
        else{
            fromGate = ioGate;
            type = "output";
        }
        name=wireName;
    }
    public void setOrigin(muGate originGate){
        fromGate = originGate;
    }
    public void setDestination(muGate destGate){
        toGate = destGate;
    }
}
