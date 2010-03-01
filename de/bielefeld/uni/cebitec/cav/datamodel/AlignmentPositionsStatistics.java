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
	 * Generates some "statistics". For example the minimum and maximum lengths of
	 * targets and queries, the sum of positive strand and reverse complement matches,
	 * Look if matches are repetitive and mark the contigs as repetitive, too.
	 */
	protected void generateStatistics() {

		this.resetStatistics();

		// Calculate the alignmentPosition.getRepeatCount() for each match in order to 
		// decide if a whole contig is repetitive.
		this.calculateRepeatCountForMatches();
		

		//get statistics about the size of the forward and backward alignments
		for (AlignmentPosition element : apl) {

			element.getQuery().totalAlignmentLength += element.size();
			if (element.isReverseHit()) {
				element.getQuery().reverseAlignmentLength += element.size();
			}
			
			// if this hit occurs several times on the reference and it covers more than 95 percent of the contig,
			// mark this contig as a repeating
			if (element.getRepeatCount()>0 ) {
				element.getQuery().setRepetitivePercent((double)element.size()/element.getQuery().getSize());
				//debug output:
//				System.out.println(element.getRepeatCount() + " "  + element.size() + element);
			}
		}

		// go through all queries, determining minimal and maximal lengths, as well as the total size
		for (DNASequence query : apl.getQueries()) {

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

	/**
	 * This method look through the matches and checks for each match and each contig if the match is repetitive.
	 * If so the match and its repeat are flagged as repetitive (through a counter)
	 * This can then be used to decide if a whole contig is repetitive.
	 */
	private void calculateRepeatCountForMatches() {
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

							// if both are true, add one to the repetitiveness counter of these matches
							sortedPositions.get(i).addRepeat();
							sortedPositions.get(j).addRepeat();
						}
					}
					
				}else {
					// if the contigs are different, jump to the next match (i++)
					break;
				}
			}
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
