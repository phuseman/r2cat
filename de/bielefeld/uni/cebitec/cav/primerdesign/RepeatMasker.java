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

import java.io.IOException;

import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;

/**
 * Interface for different repeat masking procedures. Each one should accept a FastaFileReader,
 * set all sequences to capitals and do a repeat masking step where all repetitive sequences are set to lowercase letters.
 * 
 * @author phuseman
 *
 */
public interface RepeatMasker {
	
	
	/**
	 * Performs the actual repeat masking. After this step, all repetitive regions are set to lowercase letters.
	 * Nonrepetitive parts are uppercase.
	 * @return FastaFileReader containing the repeat masked sequences.
	 */
	public FastaFileReader doRepeatMasking() throws IOException, InterruptedException;

}
