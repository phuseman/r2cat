/**
 * This Class is a ArrayList of Clusters which is always sorted by the 
 * queryStart - positions. 
 * 
 * It also has a ArrayList targetStarts of references 
 * which can be sorted by the targetStarts - positions. 
 * (that means if the fifth element of the ClusterOrganizer has the highest 
 * start - position in the target, the last position in the 
 * targetStarts - ArrayList will be a 5)
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model;

import de.bielefeld.uni.cebitec.qgram.Match;
import de.bielefeld.uni.cebitec.r2cat.UnimogExport.ExportConstants;
import java.util.ArrayList;

/**
 *
 * @author Mark Ugarov
 */
public class ClusterOrganizer extends ArrayList<Cluster> {
    private ArrayList<Integer> targetOrder;
    private long repeatID;
    
    public ClusterOrganizer(){
        targetOrder = new ArrayList();
        repeatID = 0;
    }

    @Override
    /**
     * The ClusterOrganizer must always be sorted!!!
     */
    public boolean add(Cluster c) {
         int position = this.querySortByInsert(0, this.size()-1, c);
         return true;
    }
    

    @Override
    /**
     * The ClusterOrganizer must always be sorted!!!
     */
    public void add(int index, Cluster element) {
        if( (index > 0 && this.get(index-1).getQueryStart()> element.getQueryStart())
           || ((index < this.size() -1) && this.get(index).getQueryStart() < element.getQueryStart()) )
        {
           this.add(element);
        }
        else{
            super.add(index, element);
        }
    }
    
    /**
     * 
     * @param lowerBreakpoint
     * @param upperBreakpoint
     * @param insert
     * @return the position where the Cluster was inserted
     */
    private int querySortByInsert(int lowerBreakpoint, int upperBreakpoint, Cluster insert){
        Cluster rel;
        int newUpper = Math.min(upperBreakpoint, this.size()-1);
        if(this.isEmpty()){
            super.add(insert);
            return 0;
        }
        else if(newUpper<0){
            super.add(0,insert);
            return 0;
        }
        else if(lowerBreakpoint >= newUpper ){
            if(newUpper<0 || newUpper>=this.size()){
                System.err.println("Index out of range whyle querySortByInsert");
                return newUpper;
            }
            rel = this.get(newUpper);
            if(rel.getQueryStart() <= insert.getQueryStart()){
                super.add(newUpper+1,insert);
                return newUpper+1;
            }
            else {
                super.add(newUpper, insert);
                return newUpper;
            }
        }
        else{
            int middlePoint = (int) lowerBreakpoint+((newUpper-lowerBreakpoint+1)/2);
            rel = this.get(middlePoint);
            //System.out.println(lowerBreakpoint + " " + middlePoint +" "+upperBreakpoint+"----"+this.filteredQ.size());
            if(rel.getQueryStart() <= insert.getQueryStart()){
                return this.querySortByInsert(middlePoint, newUpper, insert);
            }
            else{
                return this.querySortByInsert(lowerBreakpoint, middlePoint-1, insert);
            }
        }
    }
    
    /**
     * This method returns the next position in the ClusterOrganizer which
     * consists of matches.
     * @param index is the index after which will be searched
     * @return is the position of the next Cluster which consists of Matches. 
     */
   public int findNextMatchInQuery(int index){
        int position = index+1;
        while(position < this.size()  && !this.get(position).consistsOfMatches() ){
            
            // increase the return - value if the position is not a match
            // and no matches had been found yet
            if(!this.get(position).consistsOfMatches()){position ++;} 
            else {return position;}
        }
        return position;
    }
    
   /**
     * Joining of two Cluster. Joins only if both Clusters are Matches with the
     * same direction or both are no Matches.
     * @param index1 is the index of the a Cluster
     * @param index2 is the index of the another Cluster
     */
        
    public void join(int index1, int index2){
            this.get(index1).joinMatchesFrom(this.get(index2));
            this.remove(index2);
    } 
    
    /**
     * A Method which needs a Cluster and his position in the ClusterOrganizer.
     * Since the position is a reference, this method is inserting the position
     * of the Cluster in the ArrayList targetStarts so that all positions
     * in targetStarts will be sorted by the startpoints of their Cluster .
     * @param lowerBreakpoint 
     * @param upperBreakpoint
     * @param insert
     * @param qPosition 
     */
    private void targetSortByInsert(int lowerBreakpoint, int upperBreakpoint, Cluster insert, int qPosition){
        int newUpper = Math.min(this.targetOrder.size()-1,upperBreakpoint); 
        if (this.targetOrder.isEmpty()){
            this.targetOrder.add(qPosition);
            return;
        }
        if(newUpper<0){
            this.targetOrder.add(qPosition);
        }
        else if(lowerBreakpoint >= newUpper ){
            if(this.get(this.targetOrder.get(newUpper)).getTargetStart() <= insert.getTargetStart()){
                this.targetOrder.add(newUpper+1,qPosition);
            }
            else {
                this.targetOrder.add(newUpper, qPosition);
            }
        }
        else{
            int middlePoint = (int) lowerBreakpoint+((newUpper-lowerBreakpoint+1)/2);
            middlePoint = Math.min(this.targetOrder.size()-1, middlePoint);
            //System.out.println(lowerBreakpoint + " " + middlePoint +" "+upperBreakpoint+"----"+this.filteredQ.size());
            if(this.get(this.targetOrder.get(middlePoint)).getTargetStart() <= insert.getTargetStart()){
                 this.targetSortByInsert(middlePoint+1, newUpper, insert, qPosition);
            }
            else{
                 this.targetSortByInsert(lowerBreakpoint, middlePoint-1, insert, qPosition);
            }
        }
    }
    

    public void createSortedTargetStartList(){
        this.recreateAllTargets();
    }
     
    /**
     * This Method updates the ranking of all targets.
     * It first prooves that all Targets are in the ArrayList targetStarts.
     */
    private void updateTargetOrder(){
        // make sure, that all targets are in the list
        if(this.size() == this.targetOrder.size()){
            boolean allExist = true;
            for(int i = 0; i< this.size() && allExist; i++){
                // is i in the targetStarts?
                if(!this.targetOrder.contains(i)){
                    allExist = false;
                }
            }
            if(allExist){
                resortTargets(0);
            }
            else{
                // if one Cluster is not presented in the sorted ArrayList of
                // targets but the two ArrayList have the same size, one or
                // more Elements have to be double - so it is usefull to recreate
                // the whole targetStarts - ArrayList
                this.recreateAllTargets();
            }
        }
        // else recreate the whole list
        else{
            this.recreateAllTargets();
        }
    
    }
    

    // reseting the whole targetStarts - ArrayList 
    private void recreateAllTargets(){
        this.targetOrder = new ArrayList();
        for(int i = 0; i< this.size() ; i++){
            this.targetSortByInsert(0, this.targetOrder.size()-1, this.get(i), i);
        }
    } 
    
    // only resorting of the given targets - this method doesn't proove that all
    // targets are in the list
    public void resortTargets(int index){
        int i = index;
        if(i< this.targetOrder.size()-2 
                && this.get(this.targetOrder.get(i)).getTargetStart()<=this.get(this.targetOrder.get(i+1)).getTargetStart()){
            resortTargets(i+1);
        }
        else if(i< this.targetOrder.size()-2 
                && this.get(this.targetOrder.get(i)).getTargetStart()>this.get(this.targetOrder.get(i+1)).getTargetStart()){
            Cluster smaller = this.get(this.targetOrder.get(i+1));
            this.targetOrder.remove(i+1);
            this.targetSortByInsert(0, i, smaller, i+1);
            this.increaseTargetsAfter(i);       
        } 
    }
    
    private void increaseTargetsAfter(int index){
        for(int targetStart : this.targetOrder){
            if (targetStart>index){
                targetStart++;
            }
        }
    }
    public void deleteFromTargetOrder(int index){
        this.targetOrder.remove(index);
    }
    public void decreaseTargetsAfter(int index){
        for(int targetStart:this.targetOrder){
            if(targetStart<index){
                targetStart--;
            }
        }
    }
    
    public ArrayList<Integer> getTargetOrder(){
        return this.targetOrder;
    }
    
    /**
     * Identify the repeats in 2 Clusters c1 and c2. 
     * @param c1 is the position of a Cluster which start (query or target) 
     * is smaller than the start of c2
     * @param pos2 is the position of a Cluster which start (query or target) 
     * is bigger than the start of c1
     */
    public void cutRepeats(int pos1, int pos2){
        Cluster c1 = this.get(pos1);
        Cluster c2 = this.get(pos2);
        long minRepLengthSquare = ExportConstants.MIN_REPEAT_LENGTH*ExportConstants.MIN_REPEAT_LENGTH;
       
        long qOverlap;
        long tOverlap;
        Repeat A1;
        Repeat A2;
        Repeat B1;
        Repeat B2;
       
        if( c1.isInverted() == c2.isInverted() 
            && c1.getQueryLargerIndex()<= c2.getQuerySmallerIndex()
                //the following line could be an logical Error
            && Math.min(c1.getTargetLargerIndex(), c2.getTargetLargerIndex()) <= Math.max(c1.getTargetSmallerIndex(), c2.getTargetSmallerIndex())
           ){
                    // no repeat can be found
            }

        //there are basically 4 different types of repeats (presented by overlaps)
        //for every direction (forward or backward)

        //identify and cut the forward - repeats
        else if(!c1.isInverted() && !c2.isInverted()){
            // overlap in query and target, see scenario forward 1 and forward 2
            if(c1.getQueryEnd() > c2.getQueryStart() && c1.getTargetEnd() > c2.getTargetStart()){
                qOverlap = c1.getQueryEnd() - c2.getQueryStart();
                tOverlap = c1.getTargetEnd() - c2.getTargetStart();
                //scenario forward 1
                if(qOverlap >= tOverlap){
                    A1 = new Repeat(    c1.getQueryEnd() -(c1.getGradient()*tOverlap), // qstart
                                        c1.getQueryEnd(), // qend
                                        c2.getTargetStart(), // tstart
                                        c1.getTargetEnd(), // tend
                                        repeatID// repeatID
                    );                    
                    A2  = new Repeat(   c2.getQueryStart(),// qstart
                                        c2.getQueryStart() +(c2.getGradient()*tOverlap),// qend
                                        c2.getTargetStart(),// tstart
                                        c1.getTargetEnd(),// tend
                                        repeatID// repeatID
                    );
                    B1  = new Repeat(   c2.getQueryStart()+A2.getQuerySize(),// qstart
                                        c1.getQueryEnd()-A1.getQuerySize(),// qend
                                        c2.getTargetStart()-(c1.getReziprocalGradient()*(c1.getQueryEnd()-A1.getQuerySize() - c2.getQueryStart()- A2.getQuerySize())),// tstart
                                        c2.getTargetStart(),// tend
                                        repeatID+0.5// repeatID
                    );
                    B2  = new Repeat(   c2.getQueryStart()+A2.getQuerySize(),// qstart
                                        c1.getQueryEnd()-A2.getQuerySize(),// qend
                                        c1.getTargetStart(),// tstart
                                        c1.getTargetStart() + (c2.getReziprocalGradient()*(c1.getQueryEnd()-A2.getQuerySize() - c2.getQueryStart()-A2.getQuerySize())),// tend
                                        repeatID+0.5// repeatID
                    );
                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(A1);
                        c1.addClosingTargetRepeat(A1);
                    }

                    if(B1.getSquareSize() >= minRepLengthSquare && B2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(B1);
                        c1.addClosingTargetRepeat(B1);
                        c2.addLeadingTargetRepeat(B2);
                    }
                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(A1);
                        c1.addClosingTargetRepeat(A1);
                        c2.addLeadingTargetRepeat(A2); 
                    }
                    repeatID++;
                }
                // scenario forward 2
                else{
                    A1 = new Repeat(    c2.getQueryStart(),// qstart
                                        c1.getQueryEnd(),// qend
                                        c1.getTargetEnd() - (c1.getGradient()*qOverlap),// tstart
                                        c1.getTargetEnd(),// tend
                                        repeatID// repeatID
                    );                    
                    A2  = new Repeat(   c2.getQueryStart(),// qstart
                                        c1.getQueryEnd(),// qend
                                        c2.getTargetStart(),// tstart
                                        c2.getTargetStart() - (c2.getGradient()*qOverlap),// tend
                                        repeatID// repeatID
                    );
                    B1  = new Repeat( c2.getQueryStart() - (c1.getGradient() * (c1.getTargetEnd() - A1.getTargetSize()-c2.getTargetStart()-A2.getTargetSize())),// qstart
                                    c2.getQueryStart(),// qend
                                    c2.getTargetStart()+A2.getTargetSize(),// tstart
                                    c1.getTargetEnd()-A1.getTargetSize(),// tend
                                    repeatID+0.5// repeatID
                    );
                    B2  = new Repeat(   c1.getQueryEnd(),// qstart
                                        c1.getQueryEnd() + (c2.getGradient()*( c1.getTargetEnd() - A1.getTargetSize() - c2.getTargetStart() - A2.getTargetSize())),// qend
                                        c2.getTargetStart()+A2.getTargetSize(),// tstart
                                        c1.getTargetEnd()-A1.getTargetSize(),// tend
                                        repeatID+0.5// repeatID)
                    );
                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(A1);
                        c1.addClosingTargetRepeat(A1);
                    }

                    if(B1.getSquareSize() >= minRepLengthSquare && B2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(B1);
                        c1.addClosingTargetRepeat(B1);
                        c2.addLeadingQueryRepeat(B2);
                    }

                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(A1);
                        c1.addClosingTargetRepeat(A1);
                        c2.addLeadingQueryRepeat(A2);
                    }
                    repeatID++;
                }
            }
            
            // overlap only in the query, see scenario forward 3
            else if(c1.getQueryEnd() > c2.getQueryStart()){
                qOverlap = c1.getQueryEnd() - c2.getQueryStart();
                tOverlap = 0;
                A1 = new Repeat(    c2.getQueryStart(),// qstart
                                    c1.getQueryEnd(),// qend
                                    c1.getTargetEnd() - (c1.getReziprocalGradient()*qOverlap),// tstart
                                    c1.getTargetEnd(),// tend
                                    repeatID// repeatID
                );                    
                A2  = new Repeat(   c2.getQueryStart(),// qstart
                                    c1.getQueryEnd(),// qend
                                    c2.getTargetStart(),// tstart
                                    c2.getTargetStart() + (c2.getReziprocalGradient()*qOverlap),// tend
                                    repeatID// repeatID
                );
                if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                    c1.addClosingQueryRepeat(A1);
                    c1.addClosingTargetRepeat(A1);
                    c2.addLeadingTargetRepeat(A2);
                }
                repeatID++;
            }
            // overlap only in the target, see scenario forward 4
            else if(c1.getTargetEnd() > c2.getTargetStart()){
                qOverlap = 0;
                tOverlap = c1.getTargetEnd() - c2.getTargetStart();
                A1 = new Repeat(    c1.getQueryEnd() - (c1.getGradient()*tOverlap),// qstart
                                    c1.getQueryEnd(),// qend
                                    c2.getTargetStart(),// tstart
                                    c1.getTargetEnd(),// tend
                                    repeatID// repeatID
                );                    
                A2  = new Repeat(   c2.getQueryStart(),// qstart
                                    c2.getQueryStart() + (c2.getGradient() *(c1.getTargetEnd() - c2.getTargetStart())),// qend
                                    c2.getTargetStart(),// tstart
                                    c1.getTargetEnd(),// tend
                                    repeatID// repeatID
                );
                if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                    c1.addClosingQueryRepeat(A1);
                    c1.addClosingTargetRepeat(A1);
                    c2.addLeadingQueryRepeat(A2);
                }
                repeatID++;
            }
            // otherwise no repeat has been found 
            else{
                spitError(c1, c2);
            }
        
        }
        // identify and cut the backward - repeats
        else if (c1.isInverted() && c2.isInverted()){
            // overlap in query and target, see scenario backward 1 and backward 2
            if(c1.getQueryEnd() > c2.getQueryStart() && c2.getTargetEnd() > c1.getTargetStart()){
                qOverlap = c1.getQueryEnd() - c2.getQueryStart();
                tOverlap = c2.getTargetStart() - c1.getTargetEnd();
                //scenario backward 1
                if(tOverlap >= qOverlap){
                    A1 = new Repeat(    c2.getQueryStart(),// qstart
                                        c1.getQueryEnd(),// qend
                                        c1.getTargetEnd()+ (c1.getReziprocalGradient()*qOverlap),// tstart
                                        c1.getTargetEnd(),// tend
                                        repeatID// repeatID
                    );                    
                    A2  = new Repeat(   c2.getQueryStart(),// qstart
                                        c1.getQueryEnd(),// qend
                                        c2.getTargetStart(),// tstart
                                        c2.getTargetStart() -(c2.getReziprocalGradient()*qOverlap),// tend
                                        repeatID// repeatID
                    );
                    B1  = new Repeat(   c2.getQueryStart() -(c1.getGradient() *(c1.getTargetEnd() + A1.getTargetSize() - c2.getTargetStart() - A2.getTargetSize())),// qstart
                                        c2.getQueryStart(),// qend
                                        c2.getTargetStart() - A2.getTargetSize(),// tstart
                                        c1.getTargetEnd() + A1.getTargetSize(),// tend
                                        repeatID+0.5// repeatID
                    );
                    B2  = new Repeat(   c1.getQueryEnd(),// qstart
                                        c1.getQueryEnd() + (c2.getGradient() * (c1.getTargetEnd() + A1.getTargetSize() - c2.getTargetStart() - A2.getTargetSize())),// qend
                                        c2.getTargetStart() - A2.getTargetSize(),// tstart
                                        c1.getTargetEnd() + A1.getTargetSize(),// tend
                                        repeatID+0.5// repeatID)
                    );
                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(A1);
                        c1.addClosingTargetRepeat(A1);
                    }

                    if(B1.getSquareSize() >= minRepLengthSquare && B2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(B1);
                        c1.addClosingTargetRepeat(B1);
                        c2.addLeadingQueryRepeat(B2);
                    }

                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(A1);
                        c1.addClosingTargetRepeat(A1);
                        c2.addLeadingQueryRepeat(A2);
                    }
                    repeatID++;
                }
                //scenario backward 2
                else{
                    A1 = new Repeat(    c1.getQueryEnd()-(c1.getGradient()*qOverlap),// qstart
                                        c1.getQueryEnd(),// qend
                                        c2.getTargetStart(),// tstart
                                        c1.getTargetEnd(),// tend
                                        repeatID// repeatID
                    );                    
                    A2  = new Repeat(   c2.getQueryStart(),// qstart
                                        c2.getQueryStart()+(c2.getGradient()*qOverlap),// qend
                                        c2.getTargetStart(),// tstart
                                        c1.getTargetEnd(),// tend
                                        repeatID// repeatID
                    );
                    B1  = new Repeat(   A2.getQueryEnd(),// qstart
                                        A1.getQueryStart(),// qend
                                        c2.getTargetStart() + (c1.getReziprocalGradient()*(A1.getQueryStart()-A2.getQueryEnd())),// tstart
                                        c2.getTargetStart(),// tend
                                        repeatID+0.5// repeatID
                    );
                    B2  = new Repeat(   A2.getQueryEnd(),// qstart
                                        A1.getQueryStart(),// qend
                                        c1.getTargetEnd(),// tstart
                                        c1.getTargetEnd() - (c2.getReziprocalGradient()*(A1.getQueryStart()-A2.getQueryEnd())),// tend
                                        repeatID+0.5// repeatID)
                    );
                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(A1);
                        c1.addClosingTargetRepeat(A1);
                    }

                    if(B1.getSquareSize() >= minRepLengthSquare && B2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(B1);
                        c1.addClosingTargetRepeat(B1);
                        c2.addLeadingTargetRepeat(B2);
                    }

                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(A1);
                        c1.addClosingTargetRepeat(A1);
                        c2.addLeadingTargetRepeat(A2);
                    }
                    repeatID++;
                }
            }
            // overlap only in the target, see scenario backward 3
            else if(c2.getTargetEnd() > c1.getTargetStart()){
                qOverlap = 0;
                tOverlap = c2.getTargetStart() - c1.getTargetEnd();
                A1 = new Repeat(    c1.getQueryEnd()-(c1.getGradient()*tOverlap),// qstart
                                    c1.getQueryEnd(),// qend
                                    c2.getTargetStart(),// tstart
                                    c1.getTargetEnd(),// tend
                                    repeatID// repeatID
                );                    
                A2  = new Repeat(   c2.getQueryStart(),// qstart
                                    c2.getQueryStart()+(c2.getGradient()*tOverlap),// qend
                                    c2.getTargetStart(),// tstart
                                    c1.getTargetEnd(),// tend
                                    repeatID// repeatID
                );

                if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                    c1.addClosingQueryRepeat(A1);
                    c1.addClosingTargetRepeat(A1);
                    c2.addLeadingQueryRepeat(A2);
                }
                repeatID++;
            }
            // overlap only in the query, see scenario backward 4
            else if(c1.getQueryEnd() > c2.getQueryStart()){
                qOverlap = c1.getQueryEnd() - c2.getQueryStart();
                tOverlap = 0;
                A1 = new Repeat(    c2.getQueryStart(),// qstart
                                    c1.getQueryEnd(),// qend
                                    c1.getTargetEnd() - (c1.getReziprocalGradient()*qOverlap),// tstart
                                    c1.getTargetEnd(),// tend
                                    repeatID// repeatID
                );                    
                A2  = new Repeat(   c2.getQueryStart(),// qstart
                                    c1.getQueryEnd(),// qend
                                    c2.getTargetStart(),// tstart
                                    c2.getTargetStart() +(c2.getReziprocalGradient()*qOverlap),// tend
                                    repeatID// repeatID
                );

                if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                    c1.addClosingQueryRepeat(A1);
                    c1.addClosingTargetRepeat(A1);
                    c2.addLeadingTargetRepeat(A2);
                }
                repeatID++;
            }

            // otherwise no repeat has been found -> ERROR
            else{
                spitError(c1, c2);
            }
        }
        // inverted repeat (Query: A, Target: -A or similar) -> palindroms
        else if (!c1.isInverted() && c2.isInverted()){
        // overlap in the query and the target, see scenario forwardbackward1
            if(c1.getQueryEnd() > c2.getQueryStart() && c1.getTargetEnd() > c2.getTargetEnd()){
                qOverlap = c1.getQueryEnd() - c2.getQueryStart();
                tOverlap = c1.getTargetEnd() - c2.getTargetEnd();
                B1  = new Repeat(   c1.getQueryEnd() - (c1.getGradient() * tOverlap),// qstart
                                    c1.getQueryEnd(),// qend
                                    c2.getTargetEnd(),// tstart
                                    c1.getTargetEnd(),// tend
                                    repeatID+0.5,// repeatID
                                    true //isPalindrom
                );
                A1 = new Repeat(    c2.getQueryStart(),// qstart
                                    B1.getQueryStart(),// qend
                                    c2.getTargetEnd() - (c1.getReziprocalGradient() *(B1.getQueryStart()-c2.getQueryStart())),// tstart
                                    c2.getTargetEnd(),// tend
                                    repeatID,// repeatID
                                    true //isPalindrom
                );        
                A2 = new Repeat(    c2.getQueryStart(),// qstart
                                    B1.getQueryStart(),// qend
                                    c2.getTargetStart(),// tstart
                                    c2.getTargetStart() - (c2.getReziprocalGradient()*(B1.getQueryStart()-c2.getQueryStart())),// tend
                                    repeatID,// repeatID
                                    true //isPalindrom
                ); 
                B2  = new Repeat(   c1.getQueryEnd() - (c2.getGradient() * tOverlap),// qstart
                                    c1.getQueryEnd(),// qend
                                    A2.getTargetEnd(),// tstart
                                    A2.getTargetEnd() - (c2.getReziprocalGradient()*B1.getQuerySize()),// tend
                                    repeatID+0.5,// repeatID
                                    true //isPalindrom
                );
                if(B1.getSquareSize() >= minRepLengthSquare && B2.getSquareSize() >= minRepLengthSquare){
                    c1.addClosingQueryRepeat(B1);
                    c1.addClosingTargetRepeat(B1);
                    c2.addClosingQueryRepeat(B2);
                }
                if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                    c1.addClosingQueryRepeat(A1);
                    c1.addClosingTargetRepeat(A1);
                    c2.addLeadingTargetRepeat(A2);

                }
                if(B1.getSquareSize() >= minRepLengthSquare && B2.getSquareSize() >= minRepLengthSquare){
                    c2.addLeadingTargetRepeat(B2);
                }
                repeatID++;
            }
            // scenario forwardbackward2
            else if(c1.getQueryEnd() > c2.getQueryStart()){
                qOverlap = c1.getQueryEnd() - c2.getQueryStart();
                tOverlap = 0;
                A1 = new Repeat(    c2.getQueryStart(),// qstart
                                    c1.getQueryEnd(),// qend
                                    c1.getTargetEnd() - (c1.getReziprocalGradient()*qOverlap),// tstart
                                    c1.getTargetEnd(),// tend
                                    repeatID,// repeatID
                                    true //isPalindrom
                );                    
                A2  = new Repeat(   c2.getQueryStart(),// qstart
                                    c1.getQueryEnd(),// qend
                                    c2.getTargetStart(),// tstart
                                    c2.getTargetStart()- (c2.getReziprocalGradient() * qOverlap),// tend
                                    repeatID,// repeatID
                                    true //isPalindrom
                );
                if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                    c1.addClosingTargetRepeat(A1);
                    c2.addLeadingTargetRepeat(A2);
                    c1.addClosingQueryRepeat(A1);
                }
                repeatID++;
            }
            //scenario forwardbackward3 and forwardbackward4
            else if(c1.getTargetEnd() > c2.getTargetEnd()){
                //forwardbackward3
                if((c1.getTargetEnd() - c2.getTargetEnd()) < (c2.getTargetStart() - c1.getTargetStart())){
                    qOverlap = 0;
                    tOverlap = c1.getTargetEnd() - c2.getTargetEnd();
                    A1 = new Repeat(    c1.getQueryEnd()-(c1.getGradient() * tOverlap),// qstart
                                        c1.getQueryEnd(),// qend
                                        c2.getTargetEnd(),// tstart
                                        c1.getTargetStart(),// tend
                                        repeatID,// repeatID
                                        true //isPalindrom
                    );                    
                    A2  = new Repeat(   c2.getQueryEnd()-(c2.getGradient() * tOverlap),// qstart
                                        c2.getQueryEnd(),// qend
                                        c2.getTargetEnd(),// tstart
                                        c1.getTargetStart(),// tend
                                        repeatID,// repeatID
                                        true //isPalindrom
                    );
                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingTargetRepeat(A1);
                        c1.addClosingQueryRepeat(A1);
                        c2.addClosingQueryRepeat(A2);
                    }
                    repeatID++;
                }
                //forwardbackward4
                else{
                    qOverlap = 0;
                    tOverlap = c2.getTargetStart() - c1.getTargetStart();
                    A1 = new Repeat(    c1.getQueryStart(),// qstart
                                        c1.getQueryStart() + (c1.getGradient() *tOverlap),// qend
                                        c1.getTargetStart(),// tstart
                                        c2.getTargetStart(),// tend
                                        repeatID,// repeatID
                                        true //isPalindrom
                    );                    
                    A2  = new Repeat(   c2.getTargetStart(),// qstart
                                        c2.getQueryStart() + (c2.getGradient() * tOverlap),// qend
                                        c2.getTargetStart(),// tstart
                                        c1.getTargetStart(),// tend
                                        repeatID,// repeatID
                                        true //isPalindrom
                    );  
                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addLeadingTargetRepeat(A1);
                        c1.addLeadingQueryRepeat(A1);
                        c2.addLeadingQueryRepeat(A2);
                    }
                    repeatID++;
                }
            }
            // otherwise no repeat has been found -> ERROR
            else{
                spitError(c1, c2);
            }
        }
        else if (c1.isInverted() && !c2.isInverted()){
            //scenario backwardforward1
            if(c1.getQueryEnd() > c2.getQueryStart() && c2.getTargetEnd() > c1.getTargetEnd()){
                qOverlap = c1.getQueryEnd() - c2.getQueryStart();
                tOverlap = c2.getTargetEnd() - c1.getTargetEnd();

                B1  = new Repeat(   c2.getQueryEnd() - (c1.getGradient() * tOverlap),// qstart
                                    c1.getQueryEnd(),// qend
                                    c2.getTargetEnd(),// tstart
                                    c1.getTargetEnd(),// tend
                                    repeatID+0.5,// repeatID
                                    true //isPalindrom
                );
                A1 = new Repeat(    c2.getQueryStart(),// qstart
                                    B1.getQueryStart(),// qend
                                    c2.getTargetEnd()+ (c1.getReziprocalGradient()*(c2.getQueryStart() - B1.getQueryStart())),// tstart
                                    c2.getTargetEnd(),// tend
                                    repeatID,// repeatID
                                    true //isPalindrom
                );             
                A2  = new Repeat(   c2.getQueryStart(),// qstart
                                    B1.getQueryStart(),// qend
                                    c2.getTargetStart(),// tstart
                                    c2.getTargetStart() + (c2.getReziprocalGradient()* (c2.getQueryStart() - B1.getQueryStart())),// tend
                                    repeatID,// repeatID
                                    true //isPalindrom
                );
                B2  = new Repeat(   c2.getQueryEnd() - (c1.getGradient() * tOverlap),// qstart
                                    c1.getQueryEnd(),// qend
                                    A2.getTargetEnd(),// tstart
                                    A2.getTargetEnd() + (c2.getReziprocalGradient() * tOverlap),// tend
                                    repeatID+0.5,// repeatID
                                    true //isPalindrom
                );
                if(B1.getSquareSize() >= minRepLengthSquare && B2.getSquareSize() >= minRepLengthSquare){
                    c2.addClosingQueryRepeat(B2);
                    c1.addClosingQueryRepeat(B1);
                }

                if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                    c1.addClosingQueryRepeat(A1);
                    c1.addClosingTargetRepeat(A1);
                    c2.addLeadingTargetRepeat(A2);
                }
                if(B1.getSquareSize() >= minRepLengthSquare && B2.getSquareSize() >= minRepLengthSquare){
                    c1.addClosingTargetRepeat(B1);
                    c2.addLeadingTargetRepeat(B2);
                }
                repeatID++;
            }
            //backwardforward2
            else if(c1.getQueryEnd() > c2.getQueryStart()){
                qOverlap = c1.getQueryEnd() - c2.getQueryStart();
                tOverlap = 0;
                A1 = new Repeat(    c2.getQueryStart(),// qstart
                                    c1.getQueryEnd(),// qend
                                    c1.getTargetEnd() * (c1.getReziprocalGradient() * qOverlap),// tstart
                                    c1.getTargetEnd(),// tend
                                    repeatID,// repeatID
                                    true //isPalindrom
                );             
                A2  = new Repeat(   c2.getQueryStart(),// qstart
                                    c1.getQueryEnd(),// qend
                                    c2.getTargetStart(),// tstart
                                    c2.getTargetStart() + (c2.getReziprocalGradient() *qOverlap),// tend
                                    repeatID,// repeatID
                                    true //isPalindrom
                );
                if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                    c1.addClosingQueryRepeat(A1);
                    c1.addClosingTargetRepeat(A1);
                    c2.addLeadingTargetRepeat(A2);
                }
            }
            //backwardforward3 and backwardforward4
            else if(c2.getTargetEnd() > c1.getTargetEnd()){
                // backwardforward3
                if((c2.getTargetEnd() - c1.getTargetEnd())<(c1.getTargetStart() - c2.getTargetStart())){
                    qOverlap = 0;
                    tOverlap = c2.getTargetEnd() - c1.getTargetEnd();
                    A1 = new Repeat(    c1.getQueryEnd()- (c1.getGradient() * tOverlap),// qstart
                                        c1.getQueryEnd(),// qend
                                        c2.getTargetEnd(),// tstart
                                        c1.getTargetEnd(),// tend
                                        repeatID,// repeatID
                                        true //isPalindrom
                    );             
                    A2  = new Repeat(   c2.getQueryEnd() - (c2.getGradient() * tOverlap),// qstart
                                        c2.getQueryEnd(),// qend
                                        c1.getTargetEnd(),// tstart
                                        c2.getTargetEnd(),// tend
                                        repeatID,// repeatID
                                        true //isPalindrom
                    );
                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addClosingQueryRepeat(A1);
                        c1.addClosingTargetRepeat(A1);
                        c2.addClosingQueryRepeat(A2);
                    }
                }
                //backwardforward4
                else{
                    qOverlap = 0;
                    tOverlap = c1.getTargetStart() - c2.getTargetStart();
                    A1 = new Repeat(    c1.getQueryStart(),// qstart
                                        c1.getQueryStart() + (c1.getGradient() *tOverlap),// qend
                                        c1.getTargetStart(),// tstart
                                        c2.getTargetStart(),// tend
                                        repeatID,// repeatID
                                        true //isPalindrom
                    );             
                    A2  = new Repeat(   c2.getQueryStart(),// qstart
                                        c2.getQueryStart() + (c2.getGradient() * tOverlap),// qend
                                        c2.getTargetStart(),// tstart
                                        c1.getTargetStart(),// tend
                                        repeatID,// repeatID
                                        true //isPalindrom
                    );
                    if(A1.getSquareSize() >= minRepLengthSquare && A2.getSquareSize() >= minRepLengthSquare){
                        c1.addLeadingQueryRepeat(A1);
                        c1.addLeadingTargetRepeat(A1);
                        c2.addLeadingQueryRepeat(A2);
                    }
                }
            }
            // otherwise no repeat has been found -> ERROR
            else{
                spitError(c1, c2);
            }
        }
    }

    
    /**
     * Calculates the distance between 2 entries with Pythagoras. a^2 +b^2 = c^2
     * BE CAREFUL: gives back the SQUARE of the distance c^2
     * @param i1 is the index of one cluster
     * @param i2 is the index of another cluster
     * @return the square of the distance between the end of the first and the
     * start of the second cluster (notice: the first cluster is the cluster
     * with the smaller queryStart)
     */
    public double getSquareDistance(int i1, int i2){
        return this.getSquareDistance(this.get(i1), this.get(i2));
    }
    
    public double getSquareDistance(Cluster ce1, Cluster ce2){
        Cluster c1, c2;
        if(ce1.getQueryStart() <= ce2.getQueryStart()){
            c1= ce1;
            c2 =ce2;
        }
        else{
            c1 = ce2;
            c2 = ce1;
        }
        long qDis = (c2.getQuerySmallerIndex() - c1.getQueryLargerIndex());
        qDis *= qDis;
        
        long tDis = (c2.getTargetStart() - c1.getTargetEnd());

        tDis *= tDis;
        
        return qDis+tDis;
    }
    
    /**
     * This method is essential for calculating a path.
     * In a path a Cluster c0 can have an overlap with the following Cluster c1 in the path. 
     * To get the right score, we have to cut the overlap, mention that this is
     * only right for this path. The next Cluster c2 has to be searched after the
     * overlap.
     * We also have to recognize that we must not care if a Cluster is inverted,
     * so we have to imagine the Clusters as squares.
     * @param c1 is the last Cluster in the path
     * @param c2 is potentially the next Cluster in this path
     * @return the score that can be added if c2 gets a part of the path
     * Pay attention to the fact that this method doesn't prove that c1 and
     * c2 are in the right order. For that, you have to use follows(c1, cut, c2).
     */
    public double getRepeatlessScore(Cluster c1, Cluster c2, long qSize, long tSize, boolean qCirc, boolean tCirc){
        /** 
         * Pay attention to the fact, that the score of the following (= upper
         * right) Cluster c2 the bestScore is not influenced by leading 
         * repeats (but by closing Repeats). That allows us to simply generate
         * the repeatless score by cutting the leading repeat in c2 respective 
         * the closing repeat in c1. 
         */
        double renormJ =1;
        double jumpcost =0; 
                //= Math.abs(c1.getDiagonal() - c2.getDiagonal()) * renormJ;
        double renormE = 0.5;
        double rectangle = Math.sqrt(this.getRectangleDistanceSquare(c1, c2, qSize, tSize, qCirc, tCirc))*renormE;
        //double euklid = (Math.sqrt(this.getSquareDistance(c1, c2))) * renormE;
        //double euklid = this.getMaximalOverlap(c1, c2);
        
        return ( c1.size()
                +c2.getBestScore() 
                - jumpcost
                - rectangle
                //- this.getMaximalOverlap(c1, c2)
                );
    }
    
    /**
     * Calculates the overlap between a Cluster c1 and a following Cluster c2.
     * @param c1 is the first Cluster
     * @param c2 is a following Cluster
     * @return 0 if no overlap was found, otherwise it returns the overlap
     */
    public long getMaximalOverlap(Cluster c1, Cluster c2){
        return Math.max(this.getTargetOverlap(c1, c2),this.getQueryOverlap(c1, c2) );
    }
    
    public long getMinimalOverlap(Cluster c1, Cluster c2){
        return Math.min(this.getTargetOverlap(c1, c2),this.getQueryOverlap(c1, c2) );
    }
    
    public long getQueryOverlap(Cluster c1, Cluster c2){
        if(c1.getQueryStart()>c2.getQueryStart()){
            return this.getQueryOverlap(c2, c1);
        }
        else if(c1.getQueryEnd()<= c2.getQueryStart()){
            return 0;
        }
        else{
            return Math.min(c1.getQueryEnd()- c2.getQueryStart(), c2.getQuerySize());
        }
    }
    
    public long getTargetOverlap(Cluster c1, Cluster c2){
        if(c1.getTargetSmallerIndex()>c2.getTargetSmallerIndex()){
            return this.getTargetOverlap(c2, c1);
        }
        else if(c1.getTargetLargerIndex()<=c2.getTargetSmallerIndex()){
            return 0;
        }
        else{
            return Math.min(c1.getTargetLargerIndex()- c2.getTargetSmallerIndex(), c2.getTargetSize());
        }
    }
    
    public long getQueryDistance(Cluster c1, Cluster c2){
        if(this.getQueryOverlap(c1, c2)!=0){
            return 0;
        }
        else if (c1.getQueryStart()>c2.getQueryStart()){
            return this.getQueryDistance(c2, c1);
        }
        else{
            return c2.getQuerySmallerIndex()- c1.getQueryLargerIndex();
        }
    }
    
    public long getQuerySquareDistance(Cluster c1, Cluster c2){
        return (this.getQueryDistance(c1, c2)*this.getQueryDistance(c1, c2));
    }
    
    public long getTargetDistance(Cluster c1, Cluster c2){
        if(this.getTargetOverlap(c1, c2)!=0){
            return 0;
        }
        else if (c1.getTargetStart()>c2.getTargetStart()){
            return this.getQueryDistance(c2, c1);
        }
        else{
//            System.out.println("unique in Target, size :"+ (c2.getTargetSmallerIndex()- c1.getTargetLargerIndex()));
            return c2.getTargetSmallerIndex()- c1.getTargetLargerIndex();
        }
    }
    
    public long getTargetSquareDistance(Cluster c1, Cluster c2){
        return (this.getTargetDistance(c1, c2)* this.getTargetDistance(c1, c2));
    }
    
   
    
    /**
     * This method proves that a Cluster follows another Cluster.
     * Graphical: c2 follows c1 if it is in the upper right to c1.
     * @param c1 is the first Cluster
     * @param cut cut c1 could be received in this path (== leading repeat in c1)
     * @param c2 is the second Cluster
     * @return true, if c2 is "upper right" in comparison to c1
     */
    public boolean follows(Cluster c1, long cut, Cluster c2){
        boolean targetFollow = false;
        if (c1.isInverted() != c2.isInverted()){
            targetFollow = true; // when the Clusters are not directed in the same way,
            // we have to admit that it could be a follower 
        }
        else if(c1.getTargetSmallerIndex()+cut <= c2.getTargetSmallerIndex()
                && !c1.isInverted() 
                && !c2.isInverted()){
                    targetFollow = true;
        }
        else if(c1.getTargetLargerIndex() >= c2.getTargetLargerIndex()+cut
                && c1.isInverted() 
                && c2.isInverted()){
                    targetFollow = true;
        }
        return(c1.getQuerySmallerIndex()+cut < c2.getQuerySmallerIndex()
                && targetFollow)
                ?true:false;
           
    }
    
    /**
     * If any ArrayList of Cluster allready exist, it can simply be converted 
     * to a ClusterOrganizer.
     * @param clusters has to be a ArrayList of Cluster.
     */
    public void setClusters(ArrayList<Cluster> clusters){
        this.clear();
        for(Cluster c:clusters){
            this.add(c);
        }
    }

    private void spitError(Cluster c1, Cluster c2) {
        long qOverlap;
        long tOverlap;
        qOverlap = c1.getQueryLargerIndex() - c2.getQuerySmallerIndex();
        tOverlap = Math.min(c1.getTargetLargerIndex() - c2.getTargetSmallerIndex(), 
                            c2.getTargetLargerIndex() - c1.getTargetSmallerIndex());
        qOverlap = Math.max(qOverlap, 0);
        tOverlap = Math.max(tOverlap, 0);
        System.err.println(
         "Overlap: "+
          "\n \t"+ c1.getQueryEnd()+"-"+c2.getQueryStart()+"="+qOverlap +
          "\n \t and "+c1.getTargetLargerIndex()+"-"+c2.getTargetSmallerIndex()+"="+tOverlap+
          "\n \t on Cluster "+c1.getQueryName() +" and "+c2.getQueryName()+
          "could not be identify");
    }
    
    private long getRectangleDistanceSquare(Cluster c1, Cluster c2, long qSize, long tSize, boolean qCirc, boolean tCirc){
        long distanceSquare = -1;
        long smallerQueryPoint;
        long smallerTargetPoint;
        long largerQueryPoint;
        long largerTargetPoint;
        long queryDistance1;
        long targetDistance1;
        long jumpQueryDistance;
        long targetDistance2;
        long jumpTargetDistance1;
        long jumpTargetDistance2;

        // scenario rectangle 1 and 2
        smallerQueryPoint = Math.min(c1.getQueryLargerIndex(), c2.getQuerySmallerIndex());
        largerQueryPoint = Math.max(c1.getQueryLargerIndex(), c2.getQuerySmallerIndex());
        queryDistance1 = Math.abs(largerQueryPoint - smallerQueryPoint);
        queryDistance1 *= queryDistance1;
        // scenario rectangle 1
        smallerTargetPoint = c1.getTargetLargerIndex();
        largerTargetPoint = c2.getTargetSmallerIndex();
        targetDistance1 = Math.abs(largerTargetPoint - smallerTargetPoint);
        targetDistance1 *=targetDistance1;
        distanceSquare = queryDistance1 + targetDistance1;
        // scenario rectangle 2
        smallerTargetPoint = c2.getTargetLargerIndex();
        largerTargetPoint = c1.getTargetSmallerIndex();
        targetDistance2 = Math.abs(largerTargetPoint - smallerTargetPoint);
        targetDistance2 *=targetDistance2;
        distanceSquare = Math.min(distanceSquare, queryDistance1 + targetDistance2);
        //scenario rectangle 3 and 4
        if(qCirc){
            smallerQueryPoint = c2.getQueryLargerIndex();
            largerQueryPoint = c1.getQueryLargerIndex() + qSize;
            jumpQueryDistance = Math.abs(smallerQueryPoint - largerQueryPoint);
            jumpQueryDistance *=jumpQueryDistance;
            distanceSquare = Math.min(distanceSquare,
                              jumpQueryDistance + Math.min(targetDistance1, targetDistance2));
        }
        else{
            // not logical but necessary for implementing scenario rectangle 7 and 8
            jumpQueryDistance = 0;
        }
        //scenario rectangle 5 and 6
        if(tCirc){
            //scenario 5
            smallerTargetPoint = c1.getTargetLargerIndex();
            largerTargetPoint = c2.getTargetSmallerIndex()+tSize;
            jumpTargetDistance1 = Math.abs(largerTargetPoint - smallerTargetPoint);
            jumpTargetDistance1 *=jumpTargetDistance1;
            distanceSquare = Math.min(distanceSquare, queryDistance1 + jumpTargetDistance1);
            //scenario 6
            smallerTargetPoint = c2.getTargetLargerIndex();
            largerTargetPoint = c1.getTargetSmallerIndex() +tSize;
            jumpTargetDistance2 = Math.abs(largerTargetPoint - smallerTargetPoint);
            jumpTargetDistance2 *=jumpTargetDistance2;
            distanceSquare = Math.min(distanceSquare, queryDistance1 + jumpTargetDistance2);
            //scenario rectangle 7 and 8
            if(qCirc && tCirc){
                distanceSquare = Math.min(distanceSquare, 
                        jumpQueryDistance + Math.min(jumpTargetDistance1, jumpTargetDistance2));
            }
        }
        
        if(distanceSquare <= 0){
            System.err.println("ERROR while calculating the distance of rectangles "
                    + ""+c1.getQueryName()+" and " +c2.getQueryName());
        }
        return distanceSquare;
        
    }
}
