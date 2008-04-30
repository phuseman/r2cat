/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
 *   phuseman ät cebitec.uni-bielefeld.de                                     *
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

package de.bielefeld.uni.cebitec.cav.qgram;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * @author phuseman
 * 
 */
public class QGramIndex {
private int[] bucketPointers;
private int[] qgramIndices;
private byte[] input;

	public QGramIndex(int maxQCode) {
		bucketPointers = new int[maxQCode];
}

	public void generateIndex(byte[] input){
		this.input = input;
		qgramIndices = new int[input.length];
		computeBucketBoundaries();
		collectQGramIndices();
	}

	private void computeBucketBoundaries() {
		for (int i = 0; i < input.length; i++) {
			
		} 
		
	}

	private void collectQGramIndices() {
		// TODO Auto-generated method stub
		
	}
	
}
