/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.Graphics;
import java.awt.event.ComponentListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;

public abstract class DataViewPlugin extends JPanel implements Observer,
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

	/**
	 * Gets the actual zoom factor
	 * @return zoom value
	 */
	public abstract double getZoom();

	/** Implement a method which visualizes alignments somehow */
	public abstract void paintComponent(Graphics g);

}