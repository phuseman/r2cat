/**
 * A Repeat is a subsequence of the sequence of the Clusters. 
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model;

/**
 *
 * @author Mark Ugarov
 */
public class Repeat {
    private long queryStart;
    private long queryEnd;
    private long targetStart;
    private long targetEnd;
    
    private boolean isPalindrom;
    
    public Repeat(long qS, long qE, long tS, long tE){
        this.queryStart = qS;
        this.queryEnd = qE;
        this.targetStart = tS;
        this.targetEnd = tE;
        
        this.isPalindrom = false;
    }
    
    public Repeat(long qS, long qE, long tS, long tE, boolean isPal){
        this.queryStart = qS;
        this.queryEnd = qE;
        this.targetStart = tS;
        this.targetEnd = tE;
        
        this.isPalindrom = isPal;
    }
    
}
