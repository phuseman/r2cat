/***************************************************************************
 *   Copyright (C) 2007 by Peter Husemann                                  *
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

import java.awt.geom.Line2D;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPosition;

/**
 * Object for displaying an alignment between a query and a target sequence.
 * Alignment means in this case that a diagonal is drawn if there is a high
 * similarity between a certain query region and a certain target region.<br>
 * The object extends Line2D.Double which allows to be drawn with the draw
 * function of a graphics object. Additionally some geometrical functions are
 * available.
 * 
 * @author Peter Husemann
 * 
 */
public class AlignmentPositionDisplayer extends Line2D.Double {

	// the alignment position (data) which should be displayed
	private AlignmentPosition alignmentPosition;

	// normalisation factor for the drawing
	private static double normalisationFactorX = 1.0;

	private static double normalisationFactorY = 1.0;

	// do not display the alignment -- not used at the moment
	private boolean invisible = false;

	private boolean reversed = false;

	public AlignmentPositionDisplayer() {
		;
	}

	/**
	 * Sets the Data (AlignmentPosition) and computes the drawcoordinates
	 * 
	 * @param alignmentPosition
	 */
	public AlignmentPositionDisplayer(AlignmentPosition alignmentPosition) {
		super();
		this.alignmentPosition = alignmentPosition;
		this.setPosition(false, false);
	}

	/**
	 * Sets the drawing coordinates for an alignment. These are scaled by
	 * normalisation factors (x and y direction).
	 */
	private void setPosition(boolean withOffsets, boolean reversed) {
		long xOffset = alignmentPosition.getTarget().getOffset();
		long yOffset = 0;


		if (withOffsets) {
			yOffset = alignmentPosition.getQuery().getOffset();
		}

		if (reversed && alignmentPosition.getQuery().isReverseComplemented()) {
			long alSize = alignmentPosition.getQuery().getSize();

			this.reversed = reversed;
			
			this.x1 = normalisationFactorX
					* (xOffset + alignmentPosition.getTargetStart());
			this.y1 = -(normalisationFactorY * (yOffset + alSize - alignmentPosition
					.getQueryStart()));
			this.x2 = (normalisationFactorX * (xOffset + alignmentPosition
					.getTargetEnd()));
			this.y2 = -(normalisationFactorY * (yOffset + alSize - alignmentPosition
					.getQueryEnd()));
		} else {
			this.x1 = normalisationFactorX
					* (xOffset + alignmentPosition.getTargetStart());
			this.y1 = -(normalisationFactorY * (yOffset + alignmentPosition
					.getQueryStart()));
			this.x2 = (normalisationFactorX * (xOffset + alignmentPosition
					.getTargetEnd()));
			this.y2 = -(normalisationFactorY * (yOffset + alignmentPosition
					.getQueryEnd()));
		}
	}

	public void rescale(boolean withOffsets, boolean reversed) {
		this.setPosition(withOffsets, reversed);
	}

	/**
	 * @param normalisationFactor
	 *            the normalisationFactor to set
	 */
	public static void setNormalisationFactor(double normalisationFactor) {
		AlignmentPositionDisplayer.normalisationFactorX = normalisationFactor;
		AlignmentPositionDisplayer.normalisationFactorY = normalisationFactor;
	}

	/**
	 * @return the alignmentPosition
	 */
	public AlignmentPosition getAlignmentPosition() {
		return alignmentPosition;
	}

	/**
	 * @return the normalisationFactorX
	 */
	public static double getNormalisationFactorX() {
		return normalisationFactorX;
	}

	/**
	 * @param normalisationFactorX
	 *            the normalisationFactorX to set
	 */
	public static void setNormalisationFactorX(double normalisationFactorX) {
		AlignmentPositionDisplayer.normalisationFactorX = normalisationFactorX;
	}

	/**
	 * @return the normalisationFactorY
	 */
	public static double getNormalisationFactorY() {
		return normalisationFactorY;
	}

	/**
	 * @param normalisationFactorY
	 *            the normalisationFactorY to set
	 */
	public static void setNormalisationFactorY(double normalisationFactorY) {
		AlignmentPositionDisplayer.normalisationFactorY = normalisationFactorY;
	}

	/**
	 * @return the invisible
	 */
	public boolean isInvisible() {
		return invisible;
	}

	/**
	 * @param invisible
	 *            the invisible to set
	 */
	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	/**
	 * @return if element is selected
	 */
	public boolean isSelected() {
		return this.alignmentPosition.isSelected();
	}

	/**
	 * @param select
	 *            element
	 */
	public void setSelected(boolean select) {
		this.alignmentPosition.setSelected(select);
	}

	public void switchSelected() {
		if (this.isSelected()) {
			this.setSelected(false);
		} else {
			this.setSelected(true);
		}
	}

	public boolean isReversed() {
		return reversed;
	}

}
