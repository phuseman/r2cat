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

package de.bielefeld.uni.cebitec.cav.PrimerDesign;

/**
 * @author phuseman
 * 
 */
public class ContigPair {
	protected String contig1 = null;
	protected boolean onRightEnd1 = false;
	protected String contig2 = null;
	protected boolean onRightEnd2 = true;

	public ContigPair(String contigId1, String contigId2) {
		this.contig1 = contigId1;
		this.contig2 = contigId2;
	}

	public void setContig1ReverseComplemented(boolean b) {
		onRightEnd1 = b;
	}

	public void setContig2ReverseComplemented(boolean b) {
		onRightEnd2 = !b;
	}

}
