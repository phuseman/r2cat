/***************************************************************************
 *   Copyright (C) 2010 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.cav.primerdesign;

/**
 * Convenience class to bundle two contigs for which primer should be generated.
 * |contig1>*|contig2> 
 * *:generate primer for this gap
 * For contig one possible primer will be generated on the right end of the contig in forward direction.
 * For the second contig the primers will be generated on the left end in backward direction.
 * 
 * @author phuseman
 */
public class ContigPair {
	protected String contig1 = null;
	protected boolean onRightEnd1 = true;
	protected String contig2 = null;
	protected boolean onRightEnd2 = false;

	/**
	 * Provide the contig ID's for which the primer shall be generated with a {@link PrimerGenerator}
	 * @param contigId1 Fasta id of the first contig.
	 * @param contigId2 Fasta Id of the second contig.
	 */
	public ContigPair(String contigId1, String contigId2) {
		this.contig1 = contigId1;
		this.contig2 = contigId2;
	}

	/**
	 * If the first contig is reverse complemented, then the primer have to be generated for the left end in reverse complement direction.
	 * @param b contig one is reverse complemented
	 */
	public void setContig1ReverseComplemented(boolean b) {
		onRightEnd1 = !b;
	}

	/**
	 * If the second contig is reverse complemented, then the primer have to be generated for the right end in forward direction.
	 * @param b contig two is reverse complemented
	 */
	public void setContig2ReverseComplemented(boolean b) {
		onRightEnd2 = b;
	}

}
