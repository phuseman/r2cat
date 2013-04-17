/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model;

import de.bielefeld.uni.cebitec.qgram.Match;
import java.util.ArrayList;

/**
 *
 * @author Mark Ugarov
 */
public class ClusterOrganizer extends ArrayList<Cluster> {
    
       
    /**
     * Joining of two Cluster. Joins only if both Clusters are Matches or both
     * are not Matches.
     * @param index1 is the index of the a Cluster
     * @param index2 is the index of the another Cluster
     */
        
    public void join(int index1, int index2){
        if (this.get(index1).isMatch() == this.get(index2).isMatch())  {
            Cluster old1 = this.get(index1);
            Cluster old2 = this.get(index2);
            
            long qStart = Math.min(old1.getQueryStart(), old2.getQueryStart());
            long qEnd = Math.max(old1.getQueryEnd(), old2.getQueryEnd());
            
            long tStart;
            long tEnd;
            
            if(old1.isInverted() && old2.isInverted()){
                tStart = Math.max(this.get(index1).getTargetStart(), this.get(index2).getTargetEnd());
                tEnd = Math.min(this.get(index1).getTargetEnd(), this.get(index2).getTargetEnd());
            }
            else{
                tStart = Math.min(this.get(index1).getTargetStart(), this.get(index2).getTargetEnd());
                tEnd = Math.max(this.get(index1).getTargetEnd(), this.get(index2).getTargetEnd());
            }
            
            Cluster newCluster = new Cluster(qStart, qEnd, tStart,tEnd, this.get(index1).isMatch());
            
            this.remove(Math.max(index1, index2));
            this.remove(Math.min(index1, index2));
            this.add(Math.min(index1, index2), newCluster);
                                            
        }
    } 
    
    /**
     * Calculates the distance between 2 entries with Pythagoras. a^2 +b^2 = c^2
     * BE CAREFULL: gives back the SQUARE of the distance c^2
     * @param i1 is the index of one cluster
     * @param i2 is the index of another cluster
     * @return the square of the distance between the end of the first and the
     * start of the second cluster (notice: the first cluster is the cluster
     * with the smaller queryStart)
     */
    public double getSquareDistance(int i1, int i2){
        int index1, index2;
        if(this.get(i1).getQueryStart()<=this.get(i2).getQueryStart()){
            index1 = i1;
            index2 = i2;
        }
        else{
            index1 = i2;
            index2 = i1;
        }
        long qDis = (this.get(index2).getQuerySmallerIndex() - this.get(index1).getQueryLargerIndex());
        long tDis = (this.get(index2).getTargetSmallerIndex() - this.get(index1).getTargetLargerIndex());
        return qDis*tDis;
    }
}
