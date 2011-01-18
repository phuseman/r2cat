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

package de.bielefeld.uni.cebitec.contigadjacencygraph.layouter;

import de.bielefeld.uni.cebitec.contigadjacencygraph.ContigAdjacencyGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;

/**
 * This interface should bundle the common properties for different contig layouters.
 * Each layouter should use a contig adjacency graph and reduce it to a layout graph
 * which can then be plotted, visualized or outputted in another way.
 * @author phuseman
 *
 */
public interface LayouterInterface {
	
	/**
	 * The method takes the fully connected graph as input and modifies it to a layout graph.
	 * A reference to the layout graph is already provided.
	 * 
	 * The actual implementation can be an exact branch and bound procedure that finds the optimal 
	 * layout for a set of contigs given the contig adjacency graph, or it can bee a greedy heuristic
	 * possibly alowing several edges per contig connector.
	 * 
	 * @param g
	 * @param l
	 */
	public void findLayout(ContigAdjacencyGraph g, LayoutGraph l);
}
