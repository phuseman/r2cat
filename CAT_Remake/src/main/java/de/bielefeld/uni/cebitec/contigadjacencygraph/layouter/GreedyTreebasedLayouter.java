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

package de.bielefeld.uni.cebitec.contigadjacencygraph.layouter;

import java.util.PriorityQueue;

import de.bielefeld.uni.cebitec.contigadjacencygraph.ContigAdjacencyGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.treecat.TreebasedContigSorterProject;

/**
 * @author phuseman
 * 
 */
public class GreedyTreebasedLayouter  implements LayouterInterface{
	
	private ContigAdjacencyGraph contigAdjacencyGraph;
	private LayoutGraph layoutGraph;
	
	
	/**
	 * Creates an instance of a treebased contig sorter which uses a heuristic to
	 * find several good adjacencies of contigs.
	 * 
	 * @param matchesToReferenceGenomes
	 *            matches of the same set of contigs to several reference
	 *            genomes. Use the {@link TreebasedContigSorterProject} to
	 *            generate this.
	 */

	public GreedyTreebasedLayouter(){
		;
	}
	


	@Override
	public void findLayout(ContigAdjacencyGraph g, LayoutGraph l) {
		this.contigAdjacencyGraph = g;
		this.layoutGraph = l;
		greedyHeuristic();
	}
		
	/**
	 * This is a greedy heuristic based on the multi fragment heuristic for the TSP.
	 * It pics all good connections as long as one of the two involved contigs has still a free end left.
	 */
	private void greedyHeuristic() {

		// tells if the node with number i is incorporated yet
		AdjacencyEdge[] usedContigs = new AdjacencyEdge[2 * contigAdjacencyGraph.getNumberOfContigs()];

		// matrix entries in sorted order. biggest first.
		PriorityQueue<AdjacencyEdge> freeConnections = new PriorityQueue<AdjacencyEdge>();

		// add all matrix entries
		for (int i = 0; i < contigAdjacencyGraph.adjacencyWeightMatrix.length; i++) { // column
			// number
			for (int j = i + 1; j < contigAdjacencyGraph.adjacencyWeightMatrix[i].length; j++) { // row
				// leave out tail to head connections of the same contig
				if (j == i + contigAdjacencyGraph.getNumberOfContigs()) {
					continue;
				}
				// if the score is 0, do not include this connection
				if (contigAdjacencyGraph.adjacencyWeightMatrix[i][j] == 0) {
					continue;
				}

				freeConnections.add(contigAdjacencyGraph.getAdjacencyEdgeFromMatrixEntries(layoutGraph, i, j));
			}
		}

		AdjacencyEdge e;
		while (freeConnections.peek() != null) {
			e = freeConnections.poll();

			if (
// dont include edges with a too small weight.
//					e.getSupport()>0.1 && 
					// include edge if at least one of the connectors is not occupied yet.
					(usedContigs[e.getMatrixIdi()] == null || usedContigs[e.getMatrixIdj()] == null)) {
				
				layoutGraph.addEdge(e);
				// if the contigs are not used yet, incorporate them
				if (usedContigs[e.getMatrixIdi()] == null) {
					usedContigs[e.getMatrixIdi()] = e;
				}
				if (usedContigs[e.getMatrixIdj()] == null) {
					usedContigs[e.getMatrixIdj()] = e;
				}

			} // one of the contig ends is not connected yet
		} // while there are good connections
	}
} // class
