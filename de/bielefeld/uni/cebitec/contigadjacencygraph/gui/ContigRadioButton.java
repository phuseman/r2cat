package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import javax.swing.JRadioButton;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;

public class ContigRadioButton extends JRadioButton {
	
	private AdjacencyEdge edge;
//	private int i;
	private boolean isLeft;
	
	public ContigRadioButton (AdjacencyEdge includingEdge){
		this.edge = includingEdge;
//		this.i = index;
	}
	
	public boolean isLeft() {
		return isLeft;
	}

	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
	}

	public AdjacencyEdge getEdge() {
		return edge;
	}

//	public int getI() {
//		return i;
//	}

}
