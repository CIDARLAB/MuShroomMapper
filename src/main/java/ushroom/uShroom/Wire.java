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
    public DGate fromGate;
    public DGate toGate;
    public Wire(String wireName){
        this.name=wireName;
    }
    public void setOrigin(DGate originGate){
        this.fromGate = originGate;
    }
    public void setDestination(DGate destGate){
        this.toGate = destGate;
    }
}
