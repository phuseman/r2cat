package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import javax.swing.JRadioButton;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;

public class ContigRadioButton extends JRadioButton {
	
	private AdjacencyEdge edge;
	private ContigAppearance contigObject;
	private boolean isLeft;
	private int nachbarIndex;
	private int centralIndex; 
	
	private AdjacencyEdge selectedNeighbourOfButtonGroup;
	private AdjacencyEdge neighboursForTheThisNeighbour;


	/*
	 * This exits only to get easy the informations about the 
	 * selected contig
	 */
	public ContigRadioButton (AdjacencyEdge includingEdge, ContigAppearance contig){
		this.edge = includingEdge;
		this.contigObject = contig;
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

	public int getNachbarIndex() {
		return nachbarIndex;
	}

	public void setNeighbourIndex(int nachbarIndex) {
		this.nachbarIndex = nachbarIndex;
	}

	public int getCentralIndex() {
		return centralIndex;
	}

	public void setCentralIndex(int centralIndex) {
		this.centralIndex = centralIndex;
	}
	public AdjacencyEdge getSelectedNeighbourOfButtonGroup() {
		return selectedNeighbourOfButtonGroup;
	}

	public void setSelectedNeighbourOfButtonGroup(
			AdjacencyEdge selectedNeighbourOfButtonGroup) {
		this.selectedNeighbourOfButtonGroup = selectedNeighbourOfButtonGroup;
	}

	public AdjacencyEdge getNeighboursForTheThisNeighbour() {
		return neighboursForTheThisNeighbour;
	}

	public void setNeighboursForTheThisNeighbour(
			AdjacencyEdge neighboursForTheThisNeighbour) {
		this.neighboursForTheThisNeighbour = neighboursForTheThisNeighbour;
	}

}
