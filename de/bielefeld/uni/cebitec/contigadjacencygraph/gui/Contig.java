package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class Contig implements Comparable{
	
	private DNASequence contig;
	private String name;
	private long lenght;
	private boolean isRepetitiv;
	private boolean isReverse;
	private double support;

	private Contig[] fiveMostLikleyRightNeigbours;
	private Contig[] fiveMostLikleyLeftNeigbours;
		
	public Contig(){
		
	}
	
	public Contig(String name, long length, boolean isRepetitiv, boolean isReverse, double support){
		this.name = name;
		this.lenght= length;
		this.isRepetitiv = isRepetitiv;
		this.isReverse = isReverse;
		this.support = support;
	}


	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}


	/*
	 * TODO Methode die Feststellt, ob das geg. Contig reverse ist oder nicht
	 * und die dies auch bei den Nachbar feststellt.
	 */
	public void detectIfContigReverse(){
		
	}
	
	
	
	/*
	 * Getter for all Variables is this class
	 */
	
	
	public String getName() {
		return name;
	}


	public long getLenght() {
		return lenght;
	}



	public boolean isRepetitiv() {
		return isRepetitiv;
	}



	public boolean isReverse() {
		return isReverse;
	}

	public double getSupport() {
		return support;
	}

}
