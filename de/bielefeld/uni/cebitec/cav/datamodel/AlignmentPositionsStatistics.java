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

import java.util.Observable;
import java.util.Observer;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList.NotifyEvent;

/**
 * Collects and computes some statistics for the alignment positions. This is sometimes used as a 
 * convinience function and sometimes to avoid a unnecessary repeated calculation over all alignmentpositions.
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
	 * targets and queries, the sum of the lengths and the center of mass for the queries.
	 * the latter is used to order the queries on the y axis.
	 */
	protected void generateStatistics() {

		this.resetStatistics();
		
		// go through all AlignmentPositions and collect the weighted centers and the total lengths for each query
		double sizeFactor=0;
		for (AlignmentPosition element : apl) {
			//take the cubic size. long matches are weighted heavier this way.
			sizeFactor=element.size() * element.size() * element.size();
			element.getQuery().centerOfMass +=  element.getTargetCenter()* sizeFactor;
			element.getQuery().centerOfMassFactor +=sizeFactor;

			element.getQuery().totalAlignmentLength += element.size();
			if (element.isReverseHit()) {
				element.getQuery().reverseAlignmentLength+=element.size();
			}
		}


		// go through all queries
		for (DNASequence query : apl.getQueries()) {
			
			//compute the center of mass for each contig/query
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

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 * 
	 * if the list of alignments is updated this method is called.
	 * it regenerates the statistics, only if the list has changed
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
