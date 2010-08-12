package de.bielefeld.uni.cebitec.cav.primerdesign;

import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;

public class PrimerResult {
	private DNASequence contigLeft;
	private DNASequence contigRight;
	private Vector<Primer> forwardPrimer;
	private Vector<Primer> reversePrimer;
	
	public PrimerResult(DNASequence left, DNASequence right){
		forwardPrimer = new Vector<Primer>();
		reversePrimer = new Vector<Primer>();
		contigLeft = left;
		contigRight = right;
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
		String infoDes = "Description of: ";
		String legend = ("primer pair"+TAB+TAB+"start"+TAB+"direct."+TAB+"len."+TAB+"distContigEnd"+TAB+"Tm"+TAB+"score"+TAB+"sequence");
		results.append(infos+" "+contigIDLeft+" and "+contigIDRight+NEW_LINE+NEW_LINE);
		results.append(infoDes+contigIDLeft+" "+descriptionLeft+NEW_LINE+infoDes+contigIDRight+" "+descriptionRight+NEW_LINE+NEW_LINE);

		if(!forwardPrimer.isEmpty()&&!reversePrimer.isEmpty()&&forwardPrimer.size()==reversePrimer.size()){
		results.append(legend+NEW_LINE+NEW_LINE);
			for(int i = 0;i<this.forwardPrimer.size();i++){
			results.append("forward primer:"+TAB+TAB+forwardPrimer.elementAt(i).toString()+NEW_LINE);
			results.append("reverse primer:"+TAB+TAB+reversePrimer.elementAt(i).toString()+NEW_LINE+NEW_LINE);
		}
			}else{
				results.append("No Primers found");
			}
		return results.toString();
	}
	
	public String getContigIDs(){
		String id1 = contigLeft.getId();
		String id2 = contigRight.getId();
		
		String ids = id1+"-"+id2;
		
		return ids;
	}

}
