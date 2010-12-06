package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import javax.swing.JPanel;

public class ContigPanel extends JPanel implements Cloneable{

	private String name;
	private long size;
	private String isReverseToString;
	private ContigBorder border;
	private String text;
	
	public ContigPanel(){
		
	}
	
	@Override
	public ContigPanel clone(){
		try{
			return (ContigPanel) super.clone();
		} catch ( CloneNotSupportedException e ) { 
		    // this shouldn't happen, since we are Cloneable 
			throw new InternalError(); 
		} 
		
	}
	
	
}
