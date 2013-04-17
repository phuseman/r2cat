/**
 * The function of Cluster is very similar to the class Match but there are some
 * distinctions.
 *  - a Cluster can be a Match
 *  - by making a new Cluster the constructor makes sure that the startindex of the
 * Cluster in the query is smaller than the endindex
 *  - a Cluster can be a collection of shorter Matches, which combined describe a line
 *  - a Cluster can also be an "Unmatch", so when there is no Match or line of
 * Matches  there must be a Cluster too
 *  - two (or more) clusters can be joined to form a bigger cluster
 *  - a Cluster only need the start- and stop- positions in the query and the target
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model;

import de.bielefeld.uni.cebitec.qgram.Match;

/**
 *
 * @author Mark Ugarov
 */
public class Cluster {
    /**
     * A Cluster has two names: one for the target and one for the query.
     * If the Cluster is a Match or a line of matches, both names are equal or
     * (if it is inverted in the target) equal except a '-' in the nameInTarget
     */
    private String nameInQuery;
    private long queryStart;
    private long queryEnd;
     
    private String nameInTarget;
    private long targetStart;
    private long targetEnd;
    
    private boolean isMatch;
    
    public Cluster(Match m){
        this.isMatch = true;
        
        this.queryStart = m.getQueryStart();
        this.queryEnd = m.getQueryEnd();
        this.targetStart = m.getTargetStart();
        this.targetEnd = m.getTargetEnd();
        
        if(this.queryStart>this.queryEnd){
            this.switchStartEnd();
        }
        
        this.nameInQuery = this.generateQueryName();
        this.nameInTarget = this.generateTargetName();
                
    }
    
    public Cluster(long qStart, long qEnd, long tStart, long tEnd, boolean isM){
        this.isMatch = isM;
        
        this.queryStart = qStart;
        this.queryEnd = qEnd;
        this.targetStart = tStart;
        this.targetEnd = tEnd;
        
        if(this.queryStart>this.queryEnd){
            this.switchStartEnd();
        }
        
        this.nameInQuery = this.generateQueryName();
        this.nameInTarget = this.generateTargetName();
        
    }
    
    private String generateQueryName(){
        StringBuilder ret = new StringBuilder();
        if(this.isMatch){
            ret.append(this.queryStart);
            ret.append("matches");
            ret.append(this.targetStart);
        }
        else{
            ret.append("unmatch");
            ret.append(this.queryStart);
            // in case this.targetStart = this.queryStart there still must be
            // a difference in their names
            ret.append("q");
        }
        return ret.toString();
    }
    
    private String generateTargetName(){
        StringBuilder ret = new StringBuilder();
        if(this.isMatch){
            if(this.isInverted()){
                ret.append("-");
            }
            ret.append(this.queryStart);
            ret.append("matches");
            ret.append(this.targetStart);
        }
        else{
            ret.append("unmatch");
            ret.append(this.targetStart);
            // in case this.targetStart = this.queryStart there still must be
            // a difference in their names
            ret.append("t");
        }
        return ret.toString();
    }
    
    private void switchStartEnd(){
        //System.out.println("Switching end- and startindex.");
        //System.out.println("Was : qStart = "+this.queryStart+ ", qEnd = "+this.queryEnd+", tStart = "+this.targetStart+", tEnd ="+this.targetEnd);
        long newQueryEnd = this.queryStart;
        this.queryStart = this.queryEnd;
        this.queryEnd = newQueryEnd;
        
        long newTargetEnd = this.targetStart;
        this.targetStart = this.targetEnd;
        this.targetEnd = newTargetEnd;
        //System.out.println("Is now : qStart = "+this.queryStart+ ", qEnd = "+this.queryEnd+", tStart = "+this.targetStart+", tEnd ="+this.targetEnd);
    }
    
    public String getQueryName(){
        return this.nameInQuery;
    }
    
    public long getQueryStart(){
        return this.queryStart;
    }
    
    public long getQueryEnd(){
        return this.queryEnd;
    }
    public long getQuerySmallerIndex() {
            return (queryStart < queryEnd) ? queryStart : queryEnd;
    }

    public long getQueryLargerIndex() {
            return (queryStart > queryEnd) ? queryStart : queryEnd;
    }


    public String getTargetName(){
        return this.nameInTarget;
    }
    
    public long getTargetStart(){
        return this.targetStart;
    }
    
    public long getTargetEnd(){
        return this.targetEnd;
    }
    
    public long getTargetSmallerIndex() {
            return (targetStart < targetEnd) ? targetStart : targetEnd;
    }

    public long getTargetLargerIndex() {
            return (targetStart > targetEnd) ? targetStart : targetEnd;
    }
    
    
    public boolean isMatch(){
        return this.isMatch;
    }
    
    // returns true if the Cluster is inverted in the target
    public boolean isInverted(){
        return (this.getTargetStart() > this.getTargetEnd()) ?  true:  false;
    }
    
    public long size() {
	return this.queryEnd - this.queryStart;
    }
    
}
