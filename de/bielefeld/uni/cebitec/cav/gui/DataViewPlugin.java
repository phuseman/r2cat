package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.Graphics;
import java.awt.event.ComponentListener;
import java.util.Observable;
import java.util.Observer;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;

public interface DataViewPlugin extends Observer,
ComponentListener {

	/**
	 * This method sets the List of Alignment positions.<br>
	 * @param ap
	 *            the List to be set
	 */
	public abstract void setAlignmentsPositionsList(AlignmentPositionsList ap);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 * 
	 * the observed object is the list of alignments
	 */
	public abstract void update(Observable o, Object arg);

	/**
	 * Sets a zoom value which is applied before painting the alignments. 
	 * @param zoom
	 *            value to zoom in.
	 */
	public abstract void setZoom(double zoom);
	
	/** Implement a method which visualizes alignments somehow */
	public abstract void paintComponent(Graphics g);

}