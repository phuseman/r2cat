/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model;

import de.bielefeld.uni.cebitec.qgram.Match;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import java.util.ArrayList;

/**
 *
 * @author Mark Ugarov
 */
public class ExportMainModel extends Thread{

    private MatchList matches;
    private long maxDistanceSquare;
    private boolean useUnique;
    private boolean useRepeats;
    
    private long minlenght;
    private boolean queryIsCircular;
    private boolean targetIsCircular;
    private long querySize;
    private long targetSize;
    //ArrayList which is filtered by length and sorted by the starting postion in the query
    private ClusterOrganizer sortedByQuery;
    //another ArrayList qhich is getting the order of the matches in filteredQ
    // in the Target
    private ArrayList<Integer> orderT;
    private StringBuilder output;
    
    public boolean isWritten;
    
    /**
     * Initializing the Class with all parameters.
     * @param matchList a List of Matches which will be transformed to Cluster
     * @param maxDis every two Cluster with a distance lower than this will be merged
     * @param useU if true, unique regions will be mentioned
     * @param useR if true, repeats will be mentioned (not compatible with UniMoG)
     * @param minLen every Cluster shorter than this (after mergeNeighbors()) will be rejected
     * @param qCirc is true if the query is circular (Vectors, Plasmids...)
     * @param tCirc is true if the target is circular
     */
    public ExportMainModel(MatchList matchList, long maxDis,  boolean useU, boolean useR, long minLen, boolean qCirc, boolean tCirc){
        this.matches = matchList;
        this.maxDistanceSquare = maxDis*maxDis;
        this.useUnique = useU;
        this.useRepeats = useR;
        this.minlenght = minLen;
        this.querySize = matches.getStatistics().getQueriesSize();
        this.targetSize = matches.getStatistics().getTargetsSize();
                
        this.queryIsCircular = qCirc;
        this.targetIsCircular = tCirc;
        this.sortedByQuery = new ClusterOrganizer();
        this.orderT = new ArrayList();
        this.output = new StringBuilder();
        this.isWritten = false;
    }
    
    @Override
    public void run(){
        this.calculate();
        this.write();
    }
    
    public String getOuput(){
        return output.toString();
    }
    
    private void calculate(){
        this.isWritten = false;
        this.construct();
        testOrder();
        if(this.maxDistanceSquare > 0){
            mergeNeighbors();
        }
        if(this.minlenght > 1){
           rejectShortClusters(); 
        }
        
        this.sortedByQuery = detectPath();
        resynthesizeGraphics();
        
        if(this.useUnique){
            checkForUnique();
        }
        
        //testOrder();
        //TODO
        /** detecting repeats 
         * wherever there is a overlap greater than the maxDistance, there are two repeats
         * and both together can be presented by ONE Cluster
        */
        

        this.testOrder();
        
        /** sorting reference for orderT which points to a match in filteredQ*/
        // TODO possible whith quicksort?
        for(int indexQ =0; indexQ<this.sortedByQuery.size(); indexQ++){
            if(this.orderT.isEmpty()){
                this.orderT.add(indexQ);
            }
            else{
                Cluster c = this.sortedByQuery.get(indexQ);
                int indexT = (this.orderT.size()-1);
                while(indexT > 0 && c.getTargetStart() < this.sortedByQuery.get(indexT).getTargetStart()){
                    indexT--;
                }
                this.orderT.add(indexT, indexQ);
            }
            
        }        
    }
    
    private void construct(){
        // sorting the filteredQ ArrayList while constructing
        for(Match m:matches){
               Cluster c = new Cluster(m);
               this.sortedByQuery.add(c);
        }    
    }
    
    private void write(){
        // writing query data
        this.output.append(">"+this.matches.getQueries().get(0)+"\n");
        long i = 0;
        for(Cluster c: sortedByQuery){
            this.output.append(c.getQueryName()+" ");
            i++;
        }
        this.output.append(this.queryIsCircular ? ")":"|");
        
        // writing target data
        this.output.append("\n>"+this.matches.getTargets().get(0)+"\n");
        // please notice: while rejectShortClusters() and detectPath() 
        // the order of targets could have been adulterated so it has to be recreated
        this.sortedByQuery.createSortedTargetStartList();
        this.orderT = this.sortedByQuery.getTargetOrder();
        for(int j: this.orderT){
            this.output.append(this.sortedByQuery.get(j).getTargetName() +" ");
        }
        this.output.append(this.targetIsCircular ? ")":"|");
        this.isWritten = true;
    }

    // only for testing!
    private void testOrder() {
        //only for testing
        for(int i= 0; i<this.sortedByQuery.size()-2; i++){
            if(this.sortedByQuery.get(i).getQueryStart()>this.sortedByQuery.get(i+1).getQueryStart()){
                System.err.println("---------------not sorted!!!---------");
            }
        }
    }

    private void mergeNeighbors() {
        int inQ=0;
        // merging Clusters whose distance is shorter than the maximal distance the user configured
       
        // first step: going through the List in order of the queryStarts
        while(inQ<this.sortedByQuery.size()-2){
            if(this.sortedByQuery.getSquareDistance(inQ, inQ+1)<=this.maxDistanceSquare 
                    && this.sortedByQuery.get(inQ).isInverted() == this.sortedByQuery.get(inQ+1).isInverted()){
                //System.out.println("Merged while square of distance " +this.filteredQ.getSquareDistance(inQ, inQ+1));
                this.sortedByQuery.join(inQ, inQ+1);
            }
            else{
                inQ++;
            }
        }

        // second step: going through the List in order of the targetStarts
        inQ=0; 

        this.sortedByQuery.createSortedTargetStartList();
        ArrayList<Integer> targetOrder = this.sortedByQuery.getTargetOrder();
        int po1;
        int po2;
        int maxPos = this.sortedByQuery.size()-2;
        while(inQ<maxPos){
            po1 = targetOrder.get(inQ);
            po2 = targetOrder.get(inQ+1);
            if(this.sortedByQuery.getSquareDistance(po1, po2)<=this.maxDistanceSquare 
                    && this.sortedByQuery.follows(this.sortedByQuery.get(po1), minlenght, this.sortedByQuery.get(po1))
                ){
                this.sortedByQuery.join(po1, po2);
                this.sortedByQuery.deleteFromTargetOrder(inQ+1);
                this.sortedByQuery.decreaseTargetsAfter(inQ);
                targetOrder = this.sortedByQuery.getTargetOrder();
                maxPos--;
            }
            else{
                inQ++;
            }
        }
    }

    // checking for unique regions -> whereever there is no Match, there must be a unique region
    private void checkForUnique() {
        int inQ = 0;
        while(inQ<this.sortedByQuery.size()-2){
            if(this.sortedByQuery.getSquareDistance(inQ, inQ+1)>this.maxDistanceSquare){
                Cluster c1 = this.sortedByQuery.get(inQ);
                Cluster c2 = this.sortedByQuery.get(inQ+1);
                this.sortedByQuery.add(inQ+1, new Cluster(c1.getQueryEnd(), c2.getQueryStart(), c1.getTargetEnd(), c2.getTargetStart(), false));
                inQ++;
            }
            inQ++;  
        }
        this.sortedByQuery.createSortedTargetStartList();
        int inT = 0;
        int t1,t2;
        while(inQ<this.sortedByQuery.size()-2){
            t1 =this.sortedByQuery.getTargetOrder().get(inT);
            t2 =this.sortedByQuery.getTargetOrder().get(inT+1);
            if(this.sortedByQuery.getSquareDistance(t1, t2)>this.maxDistanceSquare){
                Cluster c1 = this.sortedByQuery.get(t1);
                Cluster c2 = this.sortedByQuery.get(t2);
                this.sortedByQuery.add(new Cluster(c1.getQueryEnd(), c2.getQueryStart(), c1.getTargetEnd(), c2.getTargetStart(), false));
                this.sortedByQuery.createSortedTargetStartList();
            }
            else{
                inQ++; 
            }  
        }
    }

    private void rejectShortClusters() {
        // reject Clusters which are shorter than the minlength (given by user)

        int inQ = 0;
        long squareMinLength = ((long)this.minlenght * (long)this.minlenght);
        while(inQ<this.sortedByQuery.size()){
            if(this.sortedByQuery.get(inQ).getSquareSize()<squareMinLength){
                //System.out.println("Remove: size "+this.filteredQ.get(inQ).size());
                this.sortedByQuery.remove(inQ);
                }
            else{
                //System.out.println(this.filteredQ.get(inQ).size());
                inQ++;
            }
        }
    }
    
    private ClusterOrganizer detectPath(){
        //detecting the optimal path through the remaining Clusters
        Cluster bestEnd = null;
        Cluster c1;
        Cluster c2;
        for(int i = this.sortedByQuery.size()-1; i >=0; i--){
            c1 = this.sortedByQuery.get(i);
            for(int j =i+1; j<this.sortedByQuery.size(); j++){
                c2 = this.sortedByQuery.get(j);
                if((this.sortedByQuery.follows(c1, 0, c2))
                   && c1.getBestScore() <= this.sortedByQuery.getRepeatlessScore(c1, c2, this.querySize, this.targetSize, this.queryIsCircular, this.targetIsCircular))
                {
                        c1.setBestPredecessor(c2);
                        c1.setBestScore(this.sortedByQuery.getRepeatlessScore(c1, c2, this.querySize, this.targetSize, this.queryIsCircular, this.targetIsCircular));
                }
            }
            if(bestEnd == null || c1.getBestScore()>bestEnd.getBestScore()){
                //System.out.println("new bestEnd has been found "+c1.getBestScore());
                bestEnd = c1;
            }
        }
        if(bestEnd == null){
            System.err.println("No optimal path has been detected.");
            return this.sortedByQuery;
        }
        ClusterOrganizer retOrg = new ClusterOrganizer();
        Cluster aktC = bestEnd;
        while(aktC != null){
            retOrg.add(aktC);
            aktC = aktC.getBestPredecessor();
        }
        return retOrg;
    }

    private void resynthesizeGraphics() {
        MatchList pathMatches = new MatchList();
        for(Cluster c:this.sortedByQuery){
            for(Match m:c.getIncludedMatches()){
                pathMatches.addMatch(m);
            }
        }
        this.matches.copyDataFromOtherMatchList(pathMatches);
        this.matches.notifyObservers(MatchList.NotifyEvent.CHANGE);
    }
    
}
