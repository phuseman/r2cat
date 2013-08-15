/**
 * A Repeat is a subsequence of the sequence of the Clusters. 
 * A Repeat can occure more than once in a Cluster, but the program only needs
 * one (for every Cluster it occurs). 
 * Two Repeats in two different Clusters have different start- and stop-positions
 * but the same ID. 
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model;

import java.util.ArrayList;

/**
 *
 * @author Mark Ugarov
 */
public class Repeat {
    private long queryStart;
    private long queryEnd;
    private long targetStart;
    private long targetEnd;
    private double ID;
    
    private boolean isPalindrom;
    
    public Repeat(long qS, long qE, long tS, long tE, double ID){
        this.queryStart = qS;
        this.queryEnd = qE;
        this.targetStart = tS;
        this.targetEnd = tE;
        this.ID = ID;
        
        this.isPalindrom = false;
    }
    
    public Repeat(long qS, long qE, long tS, long tE, double ID, boolean isPal){
        this.queryStart = qS;
        this.queryEnd = qE;
        this.targetStart = tS;
        this.targetEnd = tE;
        this.ID = ID;
        
        this.isPalindrom = isPal;
    }
    
    public double getID(){
        return this.ID;
    }
    
    public long getQuerySize(){
        return (this.queryStart <this.queryEnd)
                ?(this.queryEnd - this.queryStart)
                :(this.queryStart - this.queryEnd);
    }
    
    public long getTargetSize(){
        return (this.targetStart<this.targetEnd)
                ?(this.targetEnd-this.targetStart)
                :(this.targetStart-this.targetEnd);
    }

    public long getSquareSize(){
        return ((this.getTargetSize() * this.getTargetSize()) + (this.getQuerySize()*this.getQuerySize()));
    }
    
    public long getQueryStart(){
        return this.queryStart;
    }
    public long getQueryEnd(){
        return this.queryEnd;
    }
    public long getTargetStart(){
        return this.targetStart;
    }
    public long getTargetEnd(){
        return this.targetEnd;
    }
    
    
}
