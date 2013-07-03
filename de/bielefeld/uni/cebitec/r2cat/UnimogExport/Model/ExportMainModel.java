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
    //ArrayList which is filtered by length and sorted by the starting postion in the query
    private ClusterOrganizer filteredQ;
    //another ArrayList qhich is getting the order of the matches in filteredQ
    // in the Target
    private ArrayList<Integer> orderT;
    private StringBuilder output;
    
    public boolean isWritten;
    
    public ExportMainModel(MatchList matchList, long maxDis,  boolean useU, boolean useR, long minLen, boolean qCirc, boolean tCirc){
        this.matches = matchList;
        this.maxDistanceSquare = maxDis*maxDis;
        this.useUnique = useU;
        this.useRepeats = useR;
        this.minlenght = minLen;
        this.queryIsCircular = qCirc;
        this.targetIsCircular = tCirc;
        this.filteredQ = new ClusterOrganizer();
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
        // sorting the filteredQ ArrayList while constructing
        for(Match m:matches){
               Cluster c = new Cluster(m);
               this.filteredQ.add(c);
        }    
        //testOrder();
        mergeShortCluster();
        rejectShortClusters();
        this.filteredQ = detectPath();
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
        for(int indexQ =0; indexQ<this.filteredQ.size(); indexQ++){
            if(this.orderT.isEmpty()){
                this.orderT.add(indexQ);
            }
            else{
                Cluster c = this.filteredQ.get(indexQ);
                int indexT = (this.orderT.size()-1);
                while(indexT > 0 && c.getTargetStart() < this.filteredQ.get(indexT).getTargetStart()){
                    indexT--;
                }
                this.orderT.add(indexT, indexQ);
            }
            
        }        
    }
    
    
    
    private void write(){
        // writing query data
        this.output.append(">"+this.matches.getQueries().get(0)+"\n");
        long i = 0;
        for(Cluster c: filteredQ){
            this.output.append(c.getQueryName()+" ");
            i++;
        }
        this.output.append(this.queryIsCircular ? ")":"|");
        
        // writing target data
        this.output.append("\n>"+this.matches.getTargets().get(0)+"\n");
        for(int j: this.orderT){
            this.output.append(this.filteredQ.get(j).getTargetName() +" ");
        }
        this.output.append(this.targetIsCircular ? ")":"|");
        this.isWritten = true;
    }

    // only for testing!
    private void testOrder() {
        //only for testing
        for(int i= 0; i<this.filteredQ.size()-2; i++){
            if(this.filteredQ.get(i).getQueryStart()>this.filteredQ.get(i+1).getQueryStart()){
                System.err.println("---------------not sorted!!!---------");
            }
        }
    }

    private void mergeShortCluster() {
        int inQ=0;
        // merging Clusters whose distance is shorter than the maximal distance the user configured
       
        // first step: going through the List in order of the queryStarts
        while(inQ<this.filteredQ.size()-2){
          
            
            if(this.filteredQ.getSquareDistance(inQ, inQ+1)<=this.maxDistanceSquare 
                    && this.filteredQ.get(inQ).isInverted() == this.filteredQ.get(inQ+1).isInverted()){
                this.filteredQ.join(inQ, inQ+1);
            }
            else{
                inQ++;
            }
        }

        // second step: going through the List in order of the targetStarts
        inQ=0; 

        this.filteredQ.createSortedTargetStartList();
        ArrayList<Integer> targetOrder = this.filteredQ.getTargetOrder();
        int po1;
        int po2;
        int maxPos = this.filteredQ.size()-2;
        while(inQ<maxPos){
            po1 = targetOrder.get(inQ);
            po2 = targetOrder.get(inQ+1);
            if(this.filteredQ.getSquareDistance(po1, po2)<=this.maxDistanceSquare 
                    && this.filteredQ.get(po1).isInverted() == this.filteredQ.get(po2).isInverted()){
                this.filteredQ.join(po1, po2);
                this.filteredQ.createSortedTargetStartList();
                targetOrder = this.filteredQ.getTargetOrder();
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
        while(inQ<this.filteredQ.size()-2){
            if(this.filteredQ.getSquareDistance(inQ, inQ+1)>this.maxDistanceSquare){
                Cluster c1 = this.filteredQ.get(inQ);
                Cluster c2 = this.filteredQ.get(inQ+1);
                this.filteredQ.add(inQ+1, new Cluster(c1.getQueryEnd(), c2.getQueryStart(), c1.getTargetEnd(), c2.getTargetStart(), false, false));
                inQ++;
            }
            inQ++;  
        }
    }

    private void rejectShortClusters() {
        // reject Clusters which are shorter than the minlength (given by user)

        int inQ = 0;
        long squareMinLength = ((long)this.minlenght * (long)this.minlenght);
        while(inQ<this.filteredQ.size()){
            if(this.filteredQ.get(inQ).getSquareSize()<squareMinLength){
                this.filteredQ.remove(inQ);
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
        for(int i = this.filteredQ.size()-1; i >=0; i--){
            c1 = this.filteredQ.get(i);
            for(int j =i+1; j<this.filteredQ.size(); j++){
                c2 = this.filteredQ.get(j);
                if(this.filteredQ.follows(c1, 0, c2)
                   && c1.getBestScore() <= this.filteredQ.getRepeatlessScore(c1, c2))
                {
                        c1.setBestPredecessor(c2);
                        c1.setBestScore(this.filteredQ.getRepeatlessScore(c1, c2));
                }
            }
            if(bestEnd == null || c1.getBestScore()>bestEnd.getBestScore()){
                System.out.println("new bestEnd has been found "+c1.getBestScore());
                bestEnd = c1;
            }
        }
        if(bestEnd == null){
            System.err.println("No optimal path has been detected.");
            return this.filteredQ;
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
        for(Cluster c:this.filteredQ){
            for(Match m:c.getIncludedMatches()){
                pathMatches.addMatch(m);
            }
        }
        this.matches.copyDataFromOtherMatchList(pathMatches);
        this.matches.notifyMatchObserver();
    }
}
