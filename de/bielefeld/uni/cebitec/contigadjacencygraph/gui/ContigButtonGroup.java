package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public class ContigButtonGroup extends ButtonGroup{


	public void setAllRadioButtonAsSelected(boolean flag ){
		
		
		for ( Enumeration<AbstractButton> e = this.getElements(); e.hasMoreElements(); )
		   {
			ContigRadioButton button =(ContigRadioButton) e.nextElement();
			button.setOneNeighbourOfThisSideAlreadySelected(flag);
		   }
		
		
	}

}
