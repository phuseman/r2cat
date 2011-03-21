package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import javax.swing.JRadioButton;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class ContigRadioButton extends JRadioButton {
	
	private AdjacencyEdge edge;
	private ContigAppearance contigObject;
//	private int i;
	private boolean isLeft;
	
	public ContigRadioButton (AdjacencyEdge includingEdge, ContigAppearance contig){
		this.edge = includingEdge;
		this.contigObject = contig;
//		this.i = index;
	}
	
	public ContigAppearance getContigObject() {
		return contigObject;
	}

	public void setContigObject(ContigAppearance contigObject) {
		this.contigObject = contigObject;
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
