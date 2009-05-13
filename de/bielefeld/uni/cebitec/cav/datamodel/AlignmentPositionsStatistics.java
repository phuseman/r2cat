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

package de.bielefeld.uni.cebitec.cav.datamodel;

import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList.NotifyEvent;

/**
 * Collects and computes some statistics for the alignment positions. This is
 * sometimes used as a convinience function and sometimes to avoid a unnecessary
 * repeated calculation over all alignmentpositions.
 * 
 * @author Peter Husemann
 * 
 */
public class AlignmentPositionsStatistics implements Observer {
	private AlignmentPositionsList apl;

	private long minQueryLen;

	private long maxQueryLen;

	private long sumQueryLen;

	private int numberQueries;

	private long minTargetLen;

	private long maxTargetLen;

	private long sumTargetLen;

	private int numberTargets;

	/**
	 * @param apl
	 */
	public AlignmentPositionsStatistics(AlignmentPositionsList apl) {
		this.apl = apl;
		this.generateStatistics();
	}

	/**
	 * Resets all stored statistics. Necessary to recompute all values.
	 */
	private void resetStatistics() {
		minQueryLen = Long.MAX_VALUE;
		maxQueryLen = 0;
		sumQueryLen = 0;
		numberQueries = 0;

		minTargetLen = Long.MAX_VALUE;
		maxTargetLen = 0;
		sumTargetLen = 0;
		numberTargets = 0;
	}

	/**
	 * Generates some statistics. For example the minimum and maximum lengths of
	 * targets and queries, the sum of the lengths and the center of mass for
	 * the queries. the latter is used to order the queries on the y axis.
	 */
	protected void generateStatistics() {

		this.resetStatistics();

		// go through all AlignmentPositions and collect the weighted centers
		// and the total lengths for each query
		double sizeFactor = 0;

		
		///// detect repeats
		Vector<AlignmentPosition> sortedPositions = apl.getAlignmentPositions();
		// sort by contig id's and then by contig start position of a match.
		// this way it is easier to detect repeating matches (next for loop)
		Collections.sort(sortedPositions, new Comparator<AlignmentPosition>() {
			public int compare(AlignmentPosition a, AlignmentPosition b) {
				if (a.getQuery() == b.getQuery()) {
					if (a.getQuerySmallerIndex() == b.getQuerySmallerIndex()) {
						return 0;
					}
					if (a.getQuerySmallerIndex() < b.getQuerySmallerIndex()) {
						return -1;
					} else {
						return 1;
					}
				} else {
					return a.getQuery().getId().compareTo(b.getQuery().getId());
				}
			}
		});
		
		// go through the sorted list and find repeating matches
		for (int i = 0; i < sortedPositions.size(); i++) {
			
			for (int j = i+1; j < sortedPositions.size(); j++) {
				if (sortedPositions.get(i).getQuery() == sortedPositions.get(j).getQuery()) {
					// two matches are a repeat, if the positions on the contig are very close to each other...
					if((Math.abs(sortedPositions.get(i).getQuerySmallerIndex()-sortedPositions.get(j).getQuerySmallerIndex())+
							Math.abs(sortedPositions.get(i).getQueryLargerIndex()-sortedPositions.get(j).getQueryLargerIndex()))<100) {
						//... and the positions on the target are sufficiently far apart.
						if((Math.abs(sortedPositions.get(i).getTargetSmallerIndex()-sortedPositions.get(j).getTargetSmallerIndex())+
								Math.abs(sortedPositions.get(i).getTargetLargerIndex()-sortedPositions.get(j).getTargetLargerIndex()))>sortedPositions.get(i).size()) {

							sortedPositions.get(i).addRepeat();
							sortedPositions.get(j).addRepeat();
						}
					}
					
				}else {
					break;
				}
			}
		}
		

		
		//calculate the center of mass (or in this case cubic center of mass)
		for (AlignmentPosition element : apl) {
			// take the cubic size. long matches are weighted heavier this way.
			sizeFactor = element.size() * element.size() * element.size();
			element.getQuery().centerOfMass += element.getTargetCenter()
					* sizeFactor;
			element.getQuery().centerOfMassFactor += sizeFactor;

			element.getQuery().totalAlignmentLength += element.size();
			if (element.isReverseHit()) {
				element.getQuery().reverseAlignmentLength += element.size();
			}
			
			// if this hit occures several times on the reference and it covers more than 95 percent of the contig,
			// mark this contig as a repeat
			if (element.getRepeatCount()>0 && ((double)element.size()/element.getQuery().getSize())>.95) {
				element.getQuery().setRepetitive(true);
				
			}
		}

		// go through all queries
		for (DNASequence query : apl.getQueries()) {

			// compute the center of mass for each contig/query
			query.centerOfMass = query.centerOfMass / query.centerOfMassFactor;

			// compute min and max query size
			if (minQueryLen > query.getSize()) {
				minQueryLen = query.getSize();
			}
			if (maxQueryLen < query.getSize()) {
				maxQueryLen = query.getSize();
			}

			// the sum of all queries and the sum of their sizes
			sumQueryLen += query.getSize();
			numberQueries++;
		}

		// same for all targets
		for (DNASequence target : apl.getTargets()) {
			if (minTargetLen > target.getSize()) {
				minTargetLen = target.getSize();
			}
			if (maxTargetLen < target.getSize()) {
				maxTargetLen = target.getSize();
			}

			sumTargetLen += target.getSize();
			numberTargets++;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 * 
	 * if the list of alignments is updated this method is called. it
	 * regenerates the statistics, only if the list has changed
	 */
	public void update(Observable o, Object arg) {
		if (arg == null) {
			;
		} else {
			AlignmentPositionsList.NotifyEvent action = (AlignmentPositionsList.NotifyEvent) arg;
			if (action == NotifyEvent.MARK) {
				;
			} else if (action == NotifyEvent.HIDE) {
				;
			} else if (action == NotifyEvent.CHANGE) {
				this.generateStatistics();
			}
		}
	}

	public long getTargetsSize() {
		return sumTargetLen;
	}

	public long getMaximumTargetSize() {
		return maxTargetLen;
	}

	public long getQueriesSize() {
		return sumQueryLen;
	}

	public long getMaximumQuerySize() {
		return maxQueryLen;
	}

	public int getNumberOfQueries() {
		return apl.getQueries().size();
	}

	public int getNumberOfTargets() {
		return apl.getTargets().size();
	}

}
