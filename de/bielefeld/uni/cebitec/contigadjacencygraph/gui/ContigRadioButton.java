package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import javax.swing.JRadioButton;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;

public class ContigRadioButton extends JRadioButton {
	
	private AdjacencyEdge edge;
	private int i;
	
	public ContigRadioButton (AdjacencyEdge includingEdge, int index){
		this.edge = includingEdge;
		this.i = index;
	}

	public AdjacencyEdge getEdge() {
		return edge;
	}

	public int getI() {
		return i;
	}

}
