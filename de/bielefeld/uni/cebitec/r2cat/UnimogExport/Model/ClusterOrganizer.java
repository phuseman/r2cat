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
import java.util.ArrayList;

/**
 *
 * @author Mark Ugarov
 */
public class ClusterOrganizer extends ArrayList<Cluster> {
    private ArrayList<Integer> targetStarts;
    
    public ClusterOrganizer(){
        targetStarts = new ArrayList();
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
        
        int newUpper = Math.min(upperBreakpoint, this.size()-1);
        Cluster rel;
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
            long start1 = rel.getQueryStart();
            long start2 = insert.getQueryStart();
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
    
    
    private void targetSortByInsert(int lowerBreakpoint, int upperBreakpoint, Cluster insert, int qPosition){
        int newUpper = Math.min(this.targetStarts.size()-1,upperBreakpoint); 
        if (this.targetStarts.isEmpty()){
            this.targetStarts.add(qPosition);
        }
        else if(lowerBreakpoint >= newUpper ){
            if(this.get(this.targetStarts.get(newUpper)).getTargetStart() <= insert.getTargetStart()){
                this.targetStarts.add(newUpper+1,qPosition);
            }
            else {
                this.targetStarts.add(newUpper, qPosition);
            }
        }
        else{
            int middlePoint = (int) lowerBreakpoint+((newUpper-lowerBreakpoint+1)/2);
            middlePoint = Math.min(this.targetStarts.size()-1, middlePoint);
            //System.out.println(lowerBreakpoint + " " + middlePoint +" "+upperBreakpoint+"----"+this.filteredQ.size());
            if(this.get(this.targetStarts.get(middlePoint)).getTargetStart() <= insert.getTargetStart()){
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
        if(this.size() == this.targetStarts.size()){
            boolean allExist = true;
            for(int i = 0; i< this.size() && allExist; i++){
                // is i in the targetStarts?
                if(!this.targetStarts.contains(i)){
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
        this.targetStarts = new ArrayList();
        for(int i = 0; i< this.size() ; i++){
            this.targetSortByInsert(0, this.targetStarts.size()-1, this.get(i), i);
        }
    } 
    
    // only resorting of the given targets - this method doesn't proove that all
    // targets are in the list
    public void resortTargets(int index){
        int i = index;
        if(i< this.targetStarts.size()-2 
                && this.get(this.targetStarts.get(i)).getTargetStart()<=this.get(this.targetStarts.get(i+1)).getTargetStart()){
            resortTargets(i+1);
        }
        else if(i< this.targetStarts.size()-2 
                && this.get(this.targetStarts.get(i)).getTargetStart()>this.get(this.targetStarts.get(i+1)).getTargetStart()){
            Cluster smaller = this.get(this.targetStarts.get(i+1));
            this.targetStarts.remove(i+1);
            this.targetSortByInsert(0, i, smaller, i+1);
            this.increaseTargetsAfter(i);       
        } 
    }
    
    private void increaseTargetsAfter(int index){
        for(int targetStart : this.targetStarts){
            if (targetStart>index){
                targetStart++;
            }
        }
    }
    
    public ArrayList<Integer> getTargetOrder(){
        return this.targetStarts;
    }
    
    /**
     * Identify the repeats in 2 Clusters c1 and c2. 
     * @param c1 is the position of a Cluster which start (query or target) 
     * is smaller than the start of c2
     * @param pos2 is the position of a Cluster which start (query or target) 
     * is bigger than the start of c1
     */
    private void cutRepeats(int pos1, int pos2){
        Cluster c1 = this.get(pos1);
        Cluster c2 = this.get(pos2);
        
        long qOverlap;
        long tOverlap;
        long qRepeatStart;
        long qRepeatEnd;
        long tRepeatStart;
        long tRepeatEnd;
        
        if(c1.getQueryLargerIndex()<= c2.getQuerySmallerIndex() 
                && c1.getTargetLargerIndex() <= c2.getTargetSmallerIndex()){
            // no repeat can be found
        }
        
        //there are basically 4 different types of repeats (presented by overlaps) 
        //for every direction (forward or backward)
        
        //identify and cut the forward - repeats
        else if(!c1.isInverted() && !c2.isInverted()){
            // overlap in query and target
            if(c1.getQueryEnd() > c2.getQueryStart() && c1.getTargetEnd() > c2.getTargetStart()){

            }
            // overlap only in the query
            else if(c1.getQueryEnd() > c2.getQueryStart()){

            }
            // overlap only in the target
            else if(c1.getTargetEnd() > c2.getTargetStart()){

            }
            // otherwise there is no repeat 
            else{

            }
//            System.out.println(
//                        "Overlap: \n "+ c1.getQueryEnd()+"-"+c2.getQueryStart()+"="+qOverlap + 
//                        "\n and "+c1.getTargetLargerIndex()+"-"+c2.getTargetSmallerIndex()+"="+tOverlap+
//                        "\n on Cluster "+c1.getQueryName() +" and "+c2.getQueryName());
        }
        // identify and cut the backward - repeats
        else if (c1.isInverted() && c2.isInverted()){
            
            // overlap in query and target
            if(c1.getQueryEnd() > c2.getQueryStart() && c2.getTargetEnd() > c1.getTargetStart()){

            }
            // overlap only in the query
            else if(c1.getQueryEnd() > c2.getQueryStart()){

            }
            // overlap only in the target
            else if(c2.getTargetEnd() > c1.getTargetStart()){
                
            }
            // otherwise there is no repeat
            else{

            }
        }
        else{
            // inverted repeat (Query: A, Target: -A or similar)

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
        int index1, index2;
        if(this.get(i1).getQueryStart() <= this.get(i2).getQueryStart()){
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
    public double getRepeatlessScore(Cluster c1, Cluster c2){
        /** 
         * Pay attention to the fact, that the score of the following (= upper
         * right) Cluster c2 the bestScore is not influenced by leading 
         * repeats (but by closing Repeats). That allows us to simply generate
         * the repeatless score by cutting the leading repeat in c2 respective 
         * the closing repeat in c1. 
         */
        return (c1.size()+c2.getBestScore() - this.getMaximalOverlap(c1, c2));
    }
    
    /**
     * Calculates the overlap between a Cluster c1 and a following Cluster c2.
     * @param c1 is the first Cluster
     * @param c2 is a following Cluster
     * @return 0 if no overlap was found, otherwise it returns the overlap
     * Pay attention to the fact that this method doesn't prove that c1 and
     * c2 are in the right order. For that, you have to use follows(c1, cut, c2).
     */
    public long getMaximalOverlap(Cluster c1, Cluster c2){
        long temp1 = c1.getQueryLargerIndex() - c2.getQuerySmallerIndex();
        long temp2 = c1.getTargetLargerIndex() - c2.getTargetSmallerIndex();
        long temp = Math.max(temp1, temp2);
        return (temp>0)?temp:0;
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
        if(c1.getQuerySmallerIndex()+cut < c2.getQuerySmallerIndex()
           && c1.getTargetSmallerIndex()+cut < c2.getTargetSmallerIndex()){
            return true;
        }
        else{
            return false;
        }
    }
    
    public void setClusters(ArrayList<Cluster> clusters){
        this.clear();
        for(Cluster c:clusters){
            this.add(c);
        }
    }
}
