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
	Vector<LinkedList<Integer>> positions;

	public QGramIndex(int maxQCode) {
		positions = new Vector<LinkedList<Integer>>(maxQCode);
		positions.setSize(maxQCode);
	}

	public void addPosition(int qgram, int position) {
		if (qgram == -1) {
			return;
		} else {
			if (positions.elementAt(qgram) == null) {
				positions.set(qgram, new LinkedList());
			}
			// System.out.println("size:"+positions.size());
			positions.elementAt(qgram).add(position);
		}
	}

	public void print() {
		for (int i = 0; i < positions.size(); i++) {
			if (positions.get(i) != null) {
				System.out.println("Code: " + i);

				for (Iterator iter = positions.get(i).iterator(); iter
						.hasNext();) {
					int element = (Integer) iter.next();
					System.out.print( element + ", ");

				}
				System.out.println();
			}
		}
	}
}
