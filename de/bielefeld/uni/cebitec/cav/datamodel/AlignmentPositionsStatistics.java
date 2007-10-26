/***************************************************************************
 *   Copyright (C) 2007 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.cav.datamodel;

import java.util.Observable;
import java.util.Observer;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList.NotifyEvent;

/**
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

	protected void generateStatistics() {

		this.resetStatistics();
		
		// compute the center of mass for each contig/query
		for (AlignmentPosition element : apl) {
			element.getQuery().centerOfMass +=  element.getTargetCenter() * element.size();
			element.getQuery().totalAlignmentLength += element.size();
		}
		


		for (DNASequence query : apl.getQueries().values()) {
			
			//center of mass
			query.centerOfMass = query.centerOfMass / query.totalAlignmentLength; 


			if (minQueryLen > query.getSize()) {
				minQueryLen = query.getSize();
			}
			if (maxQueryLen < query.getSize()) {
				maxQueryLen = query.getSize();
			}

			sumQueryLen += query.getSize();
			numberQueries++;
		}

		

		
		
		for (DNASequence target : apl.getTargets().values()) {
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
