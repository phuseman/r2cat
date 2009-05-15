/***************************************************************************
 *   Copyright (C) 2009 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.cav.treebased;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPosition;

/**
 * This represents a contig which was projected onto a reference genome, based on one match between the two.
 * 
 * @author phuseman
 * 
 */
public class ProjectedContig {
	public int contigIndex = 0;
	public int referenceIndex = 0;
	public int start = 0;
	public int stop = 0;
	public int hitLength = 0;
	public boolean forwardMatch = true;
	public int qhits = 0;

	// debugging
	public AlignmentPosition ap;
	
	/**
	 * Creates an empty projected contig, which can be seen as a empty hit on the origin of a reference.
	 */
	public ProjectedContig() {
		this.ap = null;
		this.contigIndex = -1;
		this.referenceIndex = -1;
		this.start = 0;
			this.stop =0;
			this.hitLength = 0;
			this.forwardMatch = true;
			this.qhits = 0;
	}

	/**
	 * Creates a projected contig object from an alignmentposition. The
	 * contigindex is used for the contig adjacency matrix and the reference
	 * index is used to check if the references of two projected contigs are
	 * distinct.
	 * 
	 * @param contigIndex index inside the contig adjacency matrix
	 * @param referenceIndex to distinguish between projections onto different references
	 * @param ap match which is the basis for this projected contig
	 */
	public ProjectedContig(int contigIndex, int referenceIndex,
			AlignmentPosition ap) {
		this.ap = ap;
		this.contigIndex = contigIndex;
		this.referenceIndex = referenceIndex;

		// compute the projected position onto the target
		if (!ap.isReverseHit()) {
			this.start = (int) (ap.getTargetStart() - ap.getQueryStart());
			this.stop = (int) (ap.getTargetEnd() + (ap.getQuery().getSize() - ap
					.getQueryEnd()));
		} else {
			this.start = (int) (ap.getTargetStart() - ((ap.getQuery().getSize() - ap
					.getQueryStart())));
			this.stop = (int) (ap.getTargetEnd() + ap.getQueryEnd());
		}

		this.hitLength = (int) ap.size();
		this.forwardMatch = !ap.isReverseHit();
		this.qhits = ap.getNumberOfQHits();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String
				.format(
						"Idx:%d, start:%d, stop:%d, hitlen:%d (%.2f%%), forward:%b, qhits:%d",
						contigIndex, start, stop, hitLength,
						(100. * (double) hitLength / ap.getQuery().getSize()),
						forwardMatch, qhits);
	}

	/**
	 * Calculates the distance between this projected contig and another. If the
	 * projections overlap this distance can be negaitve.
	 * 
	 * @param other
	 *            other projected contig
	 * @return ditance between this and the other
	 */
	public int distance(ProjectedContig other) {
		// unlikely case 1: both start at the same.
		// |------|
		// |----|
		// d: -xxxxxx
		// then I would say that this is an overlap by the shorter one.
		// Imagine that the shorter one would start one base earlier
		if (this.start == other.start) {
			return (this.hitLength < other.hitLength) ? -this.hitLength
					: -other.hitLength;
		}
		// case 2a: one starts before the other
		// |------|
		// |----|
		// d: xxx

		// case 2b:
		// |------|
		// |----|
		// d: -xxx

		// // case 2c:
		// |---|
		// |------------|
		// d: -xxxxxxxxx

		// // case 2d:
		// |---|
		// |------------|
		// d: -xxxxx

		// I would take the end of the first one up to the start of the second
		// one
		if (this.start < other.start) {
			return other.start - this.stop;
		} else {
			return this.start - other.stop;
		}
	}

	// this is most probable nonsense... compare with the sequence analysis
	// script chapter 7.2
	// public int estimateBitScore(int queryLength, int targetLength) {
	// double pValue = 1-(Math.exp(-targetLength*queryLength*Math.pow(0.25,
	// qhits+10)));
	// return (int) (Math.log(pValue)/Math.log(2));
	//			
	// }

}
