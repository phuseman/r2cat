package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.HashMap;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;

public class PrimerResult {
	private DNASequence contigLeft;
	private DNASequence contigRight;
	private Vector<Primer> forwardPrimer;
	private Vector<Primer> reversePrimer;
	
	public PrimerResult(){
		forwardPrimer = new Vector<Primer>();
		reversePrimer = new Vector<Primer>();
	}
	
	public void addPair(Primer leftPrimer, Primer rightPrimer){
		forwardPrimer.add(leftPrimer);
		reversePrimer.add(rightPrimer);
		
	}
	
	public void addContigs(DNASequence left, DNASequence right){
		contigLeft = left;
		contigRight = right;
	}
	
	@Override 
	public String toString(){
		String NEW_LINE = System.getProperty("line.separator");
		String TAB = "\t";
		String infos ="Primer picking results for contigs";
		StringBuilder results = new StringBuilder();
		String contigIDLeft = contigLeft.getId();
		String contigIDRight =contigRight.getId();
		String descriptionLeft = contigLeft.getDescription();
		String descriptionRight = contigRight.getDescription();
		String infoDes = "Description of ";
		String legend = ("primer direction "+TAB+TAB+"start "+TAB+"length "+TAB+"offset "+TAB+"Tm"+TAB+"score"+TAB+"sequence"+NEW_LINE);
		results.append(infos+" "+contigIDLeft+" and "+contigIDRight+NEW_LINE+NEW_LINE);
		results.append(infoDes+contigIDLeft+" "+descriptionLeft+NEW_LINE+infoDes+contigIDRight+" "+descriptionRight+NEW_LINE+NEW_LINE);
		results.append(legend);
		for(int i = 0;i<this.forwardPrimer.size();i++){
			if(i<50){
			results.append("forward primer: "+TAB+forwardPrimer.elementAt(i).toString()+NEW_LINE);
			results.append("reverse primer: "+TAB+reversePrimer.elementAt(i).toString()+NEW_LINE);
				}
			}
		
		
		//System.out.println(results.toString());
		return results.toString();
	}
	//header erstellen

}
