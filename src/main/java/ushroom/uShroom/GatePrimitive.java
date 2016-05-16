/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ushroom.uShroom;

/**
 *
 * @author Everett
 */

//To do: Update for control layer

public class GatePrimitive {
    public int inputs, outputs;
    public String picturePath, operator, mintSyntax;
    
    public GatePrimitive(int inputs, int outputs, String operator, String picturePath, String mintSyntax){
        this.inputs = inputs;
        this.outputs = outputs;
        this.picturePath = picturePath;
        this.operator = operator;
        this.mintSyntax = mintSyntax;        
    }   
}
