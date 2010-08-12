/***************************************************************************
 *   Copyright (C) 2010 by Yvonne Hermann, Peter Husemann                  *
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
			results.append("forward primer:"+TAB+forwardPrimer.elementAt(i).toString()+NEW_LINE);
			results.append("reverse primer:"+TAB+reversePrimer.elementAt(i).toString()+NEW_LINE+NEW_LINE);
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
