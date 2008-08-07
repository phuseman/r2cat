/***************************************************************************
 *   Copyright (C) 14.08.2007 by Peter Husemann                                  *
 *   phuseman@cebitec.uni-bielefeld.de                                     *
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

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.ComparativeAssemblyViewer;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPosition;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;

/**
 * @author Peter Husemann
 * 
 */
public class AlignmentPositionDisplayerList implements
		Iterable<AlignmentPositionDisplayer> {
	private AlignmentPositionsList alignmentsPositions;

	private Vector<AlignmentPositionDisplayer> alPosDispList;

	/**
	 * Defines the selection type.
	 * <ul>
	 * <li> ONLY means that the current selection is cleared and the new
	 * selection becomes the actual one
	 * <li> ADD add to the current selection
	 * <li> REMOVE remove from the current selection
	 * <li> TOGGLE switch; if selected unselect and vice versa
	 * </ul>
	 * 
	 * @author Peter Husemann
	 * 
	 */
	public static enum SelectionType {
		ONLY, ADD, REMOVE, TOGGLE
	};

	public AlignmentPositionDisplayerList(
			AlignmentPositionsList alignmentPositionsList) {
		this.alignmentsPositions = alignmentPositionsList;

		alPosDispList = new Vector<AlignmentPositionDisplayer>();
	}

	/**
	 * Generates a new list of {@link AlignmentPositionDisplayer}s which are
	 * scaled to a given width and heigth.
	 * 
	 * @param width
	 *            maximum width in pixel for the target(s)
	 * @param heigth
	 *            maximum heigth for the stacked queries
	 */
	public void generateAlignmentPositionDisplayerList(int width, int heigth) {
		double normalisationX = 1;
		double normalisationY = 1;

		if (!alignmentsPositions.isEmpty()) {

			normalisationX = (double) width
					/ alignmentsPositions.getStatistics().getTargetsSize();
			AlignmentPositionDisplayer.setNormalisationFactorX(normalisationX);

			normalisationY = (double) heigth
					/ alignmentsPositions.getStatistics().getQueriesSize();
			AlignmentPositionDisplayer.setNormalisationFactorY(normalisationY);

			alPosDispList.clear();
			for (AlignmentPosition ap : alignmentsPositions) {
				alPosDispList.add(new AlignmentPositionDisplayer(ap));
			}

			if (ComparativeAssemblyViewer.preferences
					.getDisplayUnidirectional()) {
				this.switchReversed();
				// switchReversed would change the preferences.
				// set it again to true
				ComparativeAssemblyViewer.preferences
						.setDisplayUnidirectional(true);
			}
		}
	}

	/**
	 * Rescales the {@link AlignmentPositionDisplayer}s to a given width and
	 * height. <br>
	 * If the list was not generated: fallback and do it. <br>
	 * 
	 * @param width
	 * @param heigth
	 */
	public void rescaleAlignmentPositionDisplayerList(int width, int heigth) {
		double normalisationX = 1;
		double normalisationY = 1;

		normalisationX = (double) width
				/ alignmentsPositions.getStatistics().getTargetsSize();
		AlignmentPositionDisplayer.setNormalisationFactorX(normalisationX);

		normalisationY = (double) heigth
				/ alignmentsPositions.getStatistics().getQueriesSize();
		AlignmentPositionDisplayer.setNormalisationFactorY(normalisationY);

		if (this.isGenerated()) {
			for (AlignmentPositionDisplayer apd : alPosDispList) {
				apd.rescale();
			}
		} else {
			this.generateAlignmentPositionDisplayerList(width, heigth);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<AlignmentPositionDisplayer> iterator() {
		return alPosDispList.iterator();
	}

	/**
	 * Switch between:<br>
	 * <ul>
	 * <li> displays all alignments in the direction as stored
	 * <li> displays all alignments in one direction
	 * </ul>
	 * 
	 */
	public void switchReversed() {
		for (AlignmentPositionDisplayer elem : alPosDispList) {
			elem.switchReversed();
		}
		// switch preferences
		ComparativeAssemblyViewer.preferences
				.setDisplayUnidirectional(!ComparativeAssemblyViewer.preferences
						.getDisplayUnidirectional());
	}

	/**
	 * Returns the closest alignment to a given point.
	 * 
	 * @param point
	 *            given point
	 * @return AlignmentPositionDisplayer which is the closest
	 */
	public AlignmentPositionDisplayer getClosestHit(Point2D.Double point) {
		double smallestDist = Double.MAX_VALUE;
		AlignmentPositionDisplayer smallest = new AlignmentPositionDisplayer();

		for (AlignmentPositionDisplayer elem : alPosDispList) {
			if (elem.ptSegDist(point) < smallestDist) {
				smallestDist = elem.ptSegDist(point);
				smallest = elem;
			}

		}

		return smallest;
	}

	/**
	 * Generates a vecor of all alignmentpositions which are near the given
	 * point regarding a given distance
	 * 
	 * @param point
	 * @param distance
	 * @return Vector of AlignmentPositions
	 */
	public Vector<AlignmentPositionDisplayer> getNearHits(Point2D.Double point,
			Double distance) {

		Vector<AlignmentPositionDisplayer> hits = null;

		for (AlignmentPositionDisplayer elem : alPosDispList) {
			if (elem.ptSegDist(point) < distance) {
				if (hits == null) {
					hits = new Vector<AlignmentPositionDisplayer>();
				}
				hits.add(elem);
			}

		}

		return hits;
	}

	/**
	 * Unmarks all alignments and notifies all registered observers.
	 */
	public void unmakAll() {
		alignmentsPositions.unmarkAllAlignments();
	}

	public void setAllVisible() {
		for (AlignmentPositionDisplayer elem : alPosDispList) {
			elem.setInvisible(false);
		}
	}

	/**
	 * Creates a histogram of the coverage of the target sequence with alignable
	 * positions.
	 * 
	 * @param numberOfBuckets
	 *            this parameter gives the number of buckets that the histogram
	 *            should have.
	 * @return
	 */
	public double[] getTargetHistogram(int numberOfBuckets) {
		double[] histogram;

		if (numberOfBuckets <= 1) {
			histogram = new double[1];
			histogram[0] = 1;
			return histogram;
		}

		histogram = new double[numberOfBuckets];

		long maximum = alignmentsPositions.getStatistics().getTargetsSize();

		long bucketSize = (long) ((double) (maximum + 1) / (numberOfBuckets - 1));

		long firstBucket = 0;
		long lastBucket = 0;
		long maximumCountOfABucket = 0;

		// for each match...
		for (AlignmentPosition ap : alignmentsPositions) {
			// ... get the first and the last bucket
			firstBucket = (ap.getTargetStart() + ap.getTarget().getOffset())
					/ bucketSize;
			lastBucket = (ap.getTargetEnd() + ap.getTarget().getOffset())
					/ bucketSize;

			// switch first and last if reverted
			if (firstBucket > lastBucket) {
				long tmp = lastBucket;
				lastBucket = firstBucket;
				firstBucket = tmp;
			}

			// add one to each bucket in range (first to last)
			for (int i = (int) firstBucket; i <= lastBucket; i++) {
				try {
					histogram[i] += 1.;
					// and compute the highest bucket so far for normalisation
					if (histogram[i] > maximumCountOfABucket) {
						maximumCountOfABucket = (long) histogram[i];
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.println("\nHistogram bin should be smaller than "+ histogram.length + " but is " + i);
				}
			}
		}

		// after each bucketcount is computed: normalize with the highest count
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] /= maximumCountOfABucket;
		}

		return histogram;
	}

	/**
	 * Returns whether or not the List of displayable alignments has been
	 * generated or not.
	 * 
	 * @return
	 */
	public boolean isGenerated() {
		if (alPosDispList.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Marks all alignments in a rectangle between a given top left and bottom
	 * right point. the type of selection can be specified by the markOperation.
	 * 
	 * @param topLeft
	 * @param bottomRight
	 * @param markOperation
	 */
	public void markArea(Point2D.Double topLeft, Point2D.Double bottomRight,
			SelectionType markOperation) {
		for (AlignmentPositionDisplayer elem : alPosDispList) {

			// reset all if selection type is only
			if (markOperation == SelectionType.ONLY) {
				elem.setSelected(false);
			}

			// if both point lie inside the rectangle...
			if (elem.x1 > topLeft.x && elem.x1 < bottomRight.x
					&& elem.x2 > topLeft.x && elem.x2 < bottomRight.x
					&& elem.y1 > topLeft.y && elem.y1 < bottomRight.y
					&& elem.y2 > topLeft.y && elem.y2 < bottomRight.y) {

				if (markOperation == SelectionType.ADD
						|| markOperation == SelectionType.ONLY) {
					elem.setSelected(true);
				} else if (markOperation == SelectionType.REMOVE) {
					elem.setSelected(false);
				} else if (markOperation == SelectionType.TOGGLE) {
					elem.switchSelected();
				}
			}
		}
		if (alignmentsPositions.hasChanged()) {
			alignmentsPositions
					.notifyObservers(AlignmentPositionsList.NotifyEvent.MARK);
		}
	}

	public boolean isEmpty() {

		return alPosDispList.isEmpty();
	}

	public int size() {
		return alignmentsPositions.size();
	}

	public void toggleOffsets() {
		// call the appropriate method on the datamodel
		alignmentsPositions.toggleOffsets();

		// rescale sets the new positions including offsets or not
		for (AlignmentPositionDisplayer apd : alPosDispList) {
			apd.rescale();
		}
	}

}
