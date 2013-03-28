/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport;

/**
 *
 * @author Mark Ugarov
 */
public class Model {

    private boolean linear;
    private StringBuilder output;
    
    public Model(boolean linear){
        this.linear = linear;
        this.output = new StringBuilder();
        
        // Magic happens here!
        this.output.append("TEST");
        
        if(linear){
            output.append("|");
        }
        else{
            output.append(")");
        }
        
        
    }
    public String getOuput(){
        return output.toString();
    }
    
}
