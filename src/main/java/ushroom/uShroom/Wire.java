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
    public Wire(String wireName){
        name=wireName;
    }
    public void setOrigin(muGate originGate){
        fromGate = originGate;
    }
    public void setDestination(muGate destGate){
        toGate = destGate;
    }
}
