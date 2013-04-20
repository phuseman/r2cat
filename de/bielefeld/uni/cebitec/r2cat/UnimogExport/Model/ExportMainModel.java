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
    
    public ExportMainModel(MatchList matchList, long maxDis, long minLen, boolean qCirc, boolean tCirc){
        this.matches = matchList;
        this.maxDistanceSquare = maxDis*maxDis;
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
               if(this.filteredQ.isEmpty()){
                   this.filteredQ.add(c);
               }
               else{
                   // sorting match in filteredQ while inserting 
                   // TODO use Quicksort
                   int  indexQ = (this.filteredQ.size()-1);
                   while(indexQ>0 && c.getQueryStart()< this.filteredQ.get(indexQ).getQueryStart()){
                       indexQ--;
                   }
                   this.filteredQ.add(indexQ, c);
               }
             
        }    
        int inQ=0;
        // merging Clusters whose distance is shorter than the maximal distance the user configured
        while(inQ<this.filteredQ.size()-2){
            if(this.filteredQ.getSquareDistance(inQ, inQ+1)<=this.maxDistanceSquare){
                this.filteredQ.join(inQ, inQ+1);
            }
            else{
                inQ++;
            }
        }
        // reject Clusters which are shorter than the minlength (given by user)
        inQ = 0;
        while(inQ<this.filteredQ.size()){
            if(this.filteredQ.get(inQ).size()<this.minlenght){
                this.filteredQ.remove(inQ);
            }
            else{
                //System.out.println(this.filteredQ.get(inQ).size());
                inQ++;
            }
        }
        
        
        // reject Clusters which are in the "shadow" of a bigger cluster
        inQ = 0;
        while(inQ < this.filteredQ.size()-1){
            if(
                this.filteredQ.get(inQ).getQueryStart() <= this.filteredQ.get(inQ+1).getQueryStart()
                && this.filteredQ.get(inQ).getQueryEnd() >= this.filteredQ.get(inQ+1).getQueryEnd()
                )   {
                    this.filteredQ.remove(inQ+1);
            }
            else if(
                    this.filteredQ.get(inQ+1).getQueryStart() <= this.filteredQ.get(inQ).getQueryStart()
                    && this.filteredQ.get(inQ+1).getQueryEnd() >= this.filteredQ.get(inQ).getQueryEnd()
                )   {
                    this.filteredQ.remove(inQ);
            }
            else{
                inQ++;
            }
        }
        
        // detecting repeats 
        //(wherever there is a overlap greater than the maxDistance, there are two repeats
        // and both together can be presented by ONE Cluster)
        
        inQ = 0;
        while(inQ<this.filteredQ.size()-1){
            Cluster c1 =  this.filteredQ.get(inQ);
            Cluster c2 = this.filteredQ.get(inQ+1);
            if(//this.filteredQ.getSquareDistance(inQ, inQ+1)>this.maxDistanceSquare && <-- allready proven while merging
                c1.getQueryEnd()>c2.getTargetStart()){
                // recognize that the overlap in the query and the overlap in the target are the same
                long overlap = c1.getQueryEnd() - c2.getQueryStart();
                //cutting the second Cluster
                this.filteredQ.remove(inQ+1);
                this.filteredQ.add(inQ+1,new Cluster (c2.getQueryStart()+(2*overlap), c2.getQueryEnd(), c2.getTargetStart()+(2*overlap), c2.getTargetEnd(),true));
                
                this.filteredQ.remove(inQ);
                //adding two repeats as one Cluster (not unique / problems with unimog)
                //TODO Checkbox for the user (the following should be optional)
                if(true){
                    this.filteredQ.add(inQ,new Cluster(c1,c2,overlap));
                }
                //cutting the first cluster
                this.filteredQ.add(inQ, new Cluster (c1.getQueryStart(), c1.getQueryEnd()-(2*overlap), c1.getTargetStart()+(2*overlap), c2.getTargetEnd(),true));
            }
            inQ++;
        }
        
        // checking for unique regions -> whereever there is no Match, there must be a unique region
        inQ = 0;
        // TODO Checkbox for the user (the following should be optional)
        if(true){
            while(inQ<this.filteredQ.size()-1){
                if(this.filteredQ.getSquareDistance(inQ, inQ+1)>this.maxDistanceSquare){
                    Cluster c1 = this.filteredQ.get(inQ);
                    Cluster c2 = this.filteredQ.get(inQ+1);
                    this.filteredQ.add(inQ, new Cluster(c1.getQueryEnd(), c2.getQueryStart(), c1.getTargetEnd(), c2.getTargetStart(), false));
                }
                else{
                    inQ++;  
                }
            }
        }
        
        // sorting reference for orderT which points to a match in filteredQ
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
}
