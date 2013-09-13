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
import java.util.ArrayList;

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

    /**
     * existsInQuery and existsInTarget especially necessary fot unique regions
     * because sometimes gaps exists in the target and not in the query and the
     * other way round
     */
    private boolean existsInQuery;
    private long queryStart;
    private long queryEnd;

    private boolean existsInTarget;
    private long targetStart;
    private long targetEnd;
    
    private ArrayList<Match> includedMatches;
    private ArrayList<Repeat> leadingQueryRepeats;
    private ArrayList<Repeat> closingQueryRepeats;
    private ArrayList<Repeat> leadingTargetRepeats;
    private ArrayList<Repeat> closingTargetRepeats;
    
    /**
     * The following fields are for the calculation of the best path through all
     * Clusters. The calculation is made in the ExportMainModel. 
     */
    private double bestScore;
    private Cluster bestPredecessor;
    
    
    /**
     * hasMatches is true, if the Cluster consists of Matches or is a Match
     */
    private boolean hasMatches;
    
    /**
     * Creating a Cluster out of a Match. 
     * @param m is a Match which shall be converted
     */
    public Cluster(Match m){
        this.hasMatches = true;

        this.bestPredecessor = null;
        this.includedMatches = new ArrayList();
        this.includedMatches.add(m);
        
        this.leadingQueryRepeats = new ArrayList();
        this.closingQueryRepeats = new ArrayList();
        this.leadingTargetRepeats = new ArrayList();
        this.closingTargetRepeats = new ArrayList();

        this.queryStart = m.getQueryStart();
        this.queryEnd = m.getQueryEnd();
        this.targetStart = m.getTargetStart();
        this.targetEnd = m.getTargetEnd();
        
        if(this.queryStart>this.queryEnd){
            this.switchStartEnd();
        }
        this.bestScore = this.size();
       
    }
    
    /**
     * Creating a new Cluster "manually".
     * @param qStart
     * @param qEnd
     * @param tStart
     * @param tEnd
     * @param isM 
     */
    public Cluster(long qStart, long qEnd, long tStart, long tEnd, boolean isM){
        this.hasMatches = isM;
        
        if(hasMatches){
            this.includedMatches = new ArrayList();
            this.leadingQueryRepeats = new ArrayList();
            this.closingQueryRepeats = new ArrayList();
            this.leadingTargetRepeats = new ArrayList();
            this.closingTargetRepeats = new ArrayList();
        }
        else{
            this.includedMatches = null;
            this.closingQueryRepeats = null;
            this.leadingQueryRepeats = null;
            this.leadingTargetRepeats = null;
            this.closingTargetRepeats = null;
        }
       this.bestPredecessor = null;

        this.queryStart = qStart;
        this.queryEnd = qEnd;
        this.targetStart = tStart;
        this.targetEnd = tEnd;
        
        if(this.queryStart>this.queryEnd){
            this.switchStartEnd();
        }
        this.bestScore = this.size();

    }
    
    
// TODO include Repeats
    private String generateQueryName(){
        StringBuilder ret = new StringBuilder();
        if(this.queryStart >= this.queryEnd
              || this.queryStart < 0
                ){
            System.err.println("QUERY NAME ERROR:"+this.queryStart +" to "+this.queryEnd);
        }
        if(this.hasMatches){
            for(Repeat r : this.leadingQueryRepeats){
                ret.append("repeat"+r.getID()+" ");
            }
            ret.append(this.queryStart);
            ret.append("matches");
            ret.append(this.targetStart);
            for(Repeat r: this.closingQueryRepeats){
                ret.append(" repeat"+r.getID());
            }
            
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
        
        if(this.hasMatches){
            if(this.isInverted()){
                if(!this.closingTargetRepeats.isEmpty()){
                    for(int cR = this.closingTargetRepeats.size()-1; cR>=0; cR--){
                        ret.append("-repeat"+this.closingTargetRepeats.get(cR).getID()+" ");
                    }
                }
                
                ret.append("-");
                ret.append(this.queryStart);
                ret.append("matches");
                ret.append(this.targetStart);
                if(!this.leadingTargetRepeats.isEmpty()){
                   for(int lR = this.leadingTargetRepeats.size()-1; lR>=0; lR--){
                        ret.append(" -repeat"+this.leadingTargetRepeats.get(lR).getID());
                   } 
                }
                
            }
            else{
                if(!this.leadingTargetRepeats.isEmpty()){
                    for(Repeat r: this.leadingTargetRepeats){
                        ret.append("repeat"+r.getID()+ " ");
                    }
                }
                
                ret.append(this.queryStart);
                ret.append("matches");
                ret.append(this.targetStart);
                if(!this.closingTargetRepeats.isEmpty()){
                    for(Repeat r: this.closingTargetRepeats){
                        ret.append(" repeat"+r.getID());
                    }
                }
                
            }    
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
    
    public void invertCluster(){
        long newEnd = this.targetStart;
        this.targetStart = this.targetEnd;
        this.targetEnd = newEnd;
          
        if(this.leadingTargetRepeats== null && this.closingTargetRepeats == null){
            return;
        }
        //inverting the rank of the repeats
        ArrayList newLeader = this.closingTargetRepeats;
        this.closingTargetRepeats = this.leadingTargetRepeats;
        this.leadingQueryRepeats = newLeader;
        
    }
    
    /**
     * Make sure c.getQueryStart()> this.getQueryStart().
     * @param c 
     */
    public void joinMatchesFrom(Cluster c){
        if(!this.hasMatches || !c.consistsOfMatches()){
            return;
        }
        for(Match m: c.getIncludedMatches()){
            this.includedMatches.add(m);
        }
        this.queryStart = Math.min(this.queryStart, c.getQueryStart());
        this.queryEnd = Math.max(this.queryEnd, c.getQueryEnd());
        if(!this.isInverted() && !c.isInverted()){
            this.targetStart = Math.min(this.targetStart, c.getTargetSmallerIndex());
            this.targetEnd = Math.max(this.targetEnd, c.getTargetLargerIndex());
        }
        else if(this.isInverted() && c.isInverted()){
            this.targetStart = Math.max(this.targetStart, c.getTargetLargerIndex());
            this.targetEnd = Math.min(this.targetEnd, c.getTargetSmallerIndex());
        }
        else if(!this.isInverted() && c.isInverted()){
            if(this.targetStart < c.getTargetEnd()){
                // this.targetStart = this.targetStart()
                this.targetEnd = Math.max(this.targetEnd, c.getTargetLargerIndex());
            }
            else{
                this.targetStart = this.getTargetEnd();
                this.targetEnd = c.getTargetEnd();
            }
        }
        else if(this.isInverted() && !c.isInverted()){
            if(this.targetEnd < c.getTargetStart()){
                this.targetStart = this.targetEnd;
                this.targetEnd = c.getTargetEnd();
            }
            else{
                //this.targetStart = this.getTargetStart();
                this.targetEnd = c.getTargetStart();
            }
        }
//        System.out.println("New Query: "+this.queryStart + " to "+this.queryEnd);
//        System.out.println("New Target: "+this.targetStart + " to "+this.targetEnd);
    }
    
    public ArrayList<Match> getIncludedMatches(){
        return this.includedMatches;
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
        return this.generateQueryName();
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
        return this.generateTargetName();
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
    
    
    public boolean consistsOfMatches(){
        return this.hasMatches;
    }
     
    // returns true if the Cluster is inverted in the target
    public boolean isInverted(){
        return (this.getTargetStart() > this.getTargetEnd()) ?  true:  false;
    }
    
    
    public long getQuerySize(){
        return this.queryEnd - this.queryStart;
    }
    
    public long getTargetSize(){
        return (this.targetStart<this.targetEnd)
                ? this.targetEnd-this.targetStart
                : this.targetStart-this.targetEnd;
    }
    
    public double size() {
	return Math.sqrt(((long) this.getTargetSize() * (long) this.getTargetSize()) + ((long) this.getQuerySize()*(long) this.getQuerySize()));
    }
    
    public long getSquareSize(){
        return (((long) this.getTargetSize() * (long) this.getTargetSize()) + ((long) this.getQuerySize()*(long) this.getQuerySize()));
    }
            
    
    public boolean isInQueryShadowOf(Cluster c){
        return (this.queryStart>=c.getQueryStart() && this.queryEnd<= c.getQueryEnd())
                ? true :false;
    }
    
    public boolean isInTargetShadowOf(Cluster c){
        return (this.getTargetSmallerIndex()>=c.getTargetSmallerIndex() && this.getTargetLargerIndex() <= c.getTargetLargerIndex())
                ?true:false;
    }
    
    
    public double getBestScore(){
        return this.bestScore;
    }
    public Cluster getBestPredecessor(){
        return this.bestPredecessor;
    }
    public void setBestScore(double bS){
        this.bestScore = bS;
    }
    public void setBestPredecessor(Cluster c){
        this.bestPredecessor = c;
    }
    
    
    public void addLeadingQueryRepeat(Repeat e){
        this.leadingQueryRepeats.add(e);
    }
    public ArrayList<Repeat> getLeadingQueryRepeats(){
        return this.leadingQueryRepeats;
    }
    public void addLeadingTargetRepeat(Repeat e){
        this.leadingTargetRepeats.add(e);
    }
    public ArrayList<Repeat> getLeadingTargetRepeats(){
        return this.leadingTargetRepeats;
    }
    public void addClosingQueryRepeat(Repeat e){
        this.closingQueryRepeats.add(0,e);
    }
    public ArrayList<Repeat> getClosingQueryRepeats(){
        return this.closingQueryRepeats;
    }
    public void addClosingTargetRepeat(Repeat e){
        this.closingTargetRepeats.add(0,e);
    }
    public ArrayList<Repeat> getClosingTargetRepeats(){
        return this.closingTargetRepeats;
    }
    
    public long getGradient(){
        return (this.getQuerySize()/this.getTargetSize());
    }
    public long getReziprocalGradient(){
        return (this.getTargetSize()/this.getQuerySize());
    }
    
    public long getDiagonal(){
        return this.queryStart -this.targetStart;
    }
}
