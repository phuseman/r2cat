/***************************************************************************
 *   Copyright (C) 2010 by Yvonne Herrmann, Peter Husemann                  *
 *   phuseman  a t  cebitec.uni-bielefeld.de                               *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/


package de.bielefeld.uni.cebitec.primerdesign;

import java.util.Vector;

import de.bielefeld.uni.cebitec.qgram.DNASequence;
/**
 * This class holds the results of primers for the given contig pair.
 * 
 */
public class PrimerResult {
	private DNASequence contigLeft;
	private DNASequence contigRight;
	//primer at the right end of the first contig
	private Vector<Primer> forwardPrimer;
	//primer at the left end of the second contig
	private Vector<Primer> reversePrimer;
	
	private Vector<String> comments;
	
	/**
	 * Constructor for this class. 
	 * Needs to get the object of the each contig of the selected pair and sets up
	 * vector of primers, which are used to hold the primers in order of their pairing-set up.
	 * 
	 * @param left contig
	 * @param right contig
	 * 
	 */
	public PrimerResult(DNASequence left, DNASequence right){
		forwardPrimer = new Vector<Primer>();
		reversePrimer = new Vector<Primer>();
		comments = new Vector<String>();
		contigLeft = left;
		contigRight = right;
	}
	/**
	 * This method adds a left primer and a right primer in the vectors with maintaining the 
	 * the same position in each vector. This is how the pairing of primers is saved.
	 * 
	 * @param leftPrimer
	 * @param rightPrimer
	 */
	public void addPair(Primer leftPrimer, Primer rightPrimer){
		forwardPrimer.add(leftPrimer);
		reversePrimer.add(rightPrimer);
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
		String legend = ("primer pair"+TAB+"start"+TAB+"direct."+TAB+"len."+TAB+"distContigEnd"+TAB+"Tm"+TAB+"score"+TAB+"sequence");
		results.append(infos+" "+contigIDLeft+" and "+contigIDRight+NEW_LINE+NEW_LINE);
		results.append(
				"Description of the left contig ("+contigIDLeft+"): "+descriptionLeft+NEW_LINE+
				"Description of the right contig ("+contigIDRight+"): "+descriptionRight+NEW_LINE+NEW_LINE);

		if(!forwardPrimer.isEmpty()&&!reversePrimer.isEmpty()&&forwardPrimer.size()==reversePrimer.size()){
		results.append(legend+NEW_LINE+NEW_LINE);
			for(int i = 0;i<this.forwardPrimer.size();i++){
			results.append("left:"+TAB+forwardPrimer.elementAt(i).toString()+NEW_LINE);
			results.append("right:"+TAB+reversePrimer.elementAt(i).toString()+NEW_LINE+NEW_LINE);
		}
			}else{
				results.append("Sorry, no Primers were found"+NEW_LINE);
			}
		for (String comment : comments) {
			results.append(NEW_LINE+comment);
		}
		return results.toString();
	}
	
	/**
	 * This method returns the contig Ids of the current object.
	 * @return
	 */
	public String getContigIDs(){
		String id1 = contigLeft.getId();
		String id2 = contigRight.getId();
		
		String ids = id1+"-"+id2;
		
		return ids;
	}
	
	public void addAdditionalComment(String comment) {
		comments.add(comment);
	}

}
