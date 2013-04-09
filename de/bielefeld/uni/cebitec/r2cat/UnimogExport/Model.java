/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport;

import de.bielefeld.uni.cebitec.qgram.Match;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import java.util.ArrayList;

/**
 *
 * @author Mark Ugarov
 */
public class Model {

    private MatchList matches;
    private long minlenght;
    
    private boolean linear;
    //ArrayList which is filtered by length and sorted by the starting postion in the query
    private ArrayList<Match> filteredQ;
    //another ArrayList qhich is getting the order of the matches in filteredQ
    // in the Target
    private ArrayList<Integer> orderT;
    private StringBuilder output;
    
    public Model(MatchList matchList, long minLen, boolean linear){
        this.matches = matchList;
        this.minlenght = minLen;
        this.linear = linear;
        this.filteredQ = new ArrayList();
        this.orderT = new ArrayList();
        this.output = new StringBuilder();
        this.calculate();
        this.write();
    }
    public String getOuput(){
        return output.toString();
    }
    
    private void calculate(){
        // calculate the filteredQ ArrayList from all matches while filtering by length

        for(int indexM = 0; indexM<this.matches.size(); indexM++){
            Match m = this.matches.getMatchAt(indexM);
            if(m.size()>=this.minlenght){
               if(this.filteredQ.isEmpty()){
                   this.filteredQ.add(m);
               }
               else{
                   // sorting match in filteredQ while inserting 
                   int  indexQ = (this.filteredQ.size()-1);
                   while(indexQ>0 && m.getQueryStart()<this.filteredQ.get(indexQ).getQueryStart()){
                       indexQ--;
                   }
                   this.filteredQ.add(indexQ, m);
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
                Match m = this.filteredQ.get(indexQ);
                int indexT = (this.orderT.size()-1);
                while(indexT>0 && m.getTargetStart()<this.filteredQ.get(indexT).getTargetStart()){
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
        for(Match m: filteredQ){
            this.output.append("cluster"+i+" ");
            i++;
        }
        if(linear){
            output.append("|");
        }
        else{
            output.append(")");
        }
        // writing target data
        this.output.append("\n>"+this.matches.getTargets().get(0)+"\n");
        for(int j: this.orderT){
            if(this.filteredQ.get(j).getTargetStart()>this.filteredQ.get(j).getTargetEnd()){
                this.output.append("-");
            }
            this.output.append("cluster"+ j +" ");
        }
        if(linear){
            output.append("|");
        }
        else{
            output.append(")");
        }
    }
}
