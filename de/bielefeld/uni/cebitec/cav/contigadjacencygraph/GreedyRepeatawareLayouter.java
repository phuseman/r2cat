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

package de.bielefeld.uni.cebitec.cav.contigadjacencygraph;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.contigadjacencygraph.LayoutGraph.AdjacencyEdge;

/**
 * @author phuseman
 * 
 */
public class GreedyRepeatawareLayouter implements LayouterInterface {

	protected ContigAdjacencyGraph contigAdjacencyGraph;
	private LayoutGraph layoutGraph;

	@Override
	public void findLayout(ContigAdjacencyGraph g, LayoutGraph l) {
		this.contigAdjacencyGraph = g;
		this.layoutGraph = l;
		greedyHeuristic();
	}

	/**
	 * This is a greedy heuristic based on the multi fragment heuristic for the
	 * TSP. It pics all good connections as long as one of the two involved
	 * contigs has still a free end left (phase 1).
	 * After that, connections which are affected by the shadow effect are tried to be integrated (phase 2).
	 * In the end repetitive contigs get a special care (phase 3).
	 */
	private void greedyHeuristic() {
		// this parameters determines how much relative support a discarded
		// connection must have to be incorporated in the second round.
		double additionalEdgesThreshold = 0.9;
		// parameter how weak the relative support for a repetitive connector
		// can be.
		double repetitiveConnectionThreshold = 0.001;

		// **** fill a priority queue, in order to access the biggest entries
		// first
		PriorityQueue<AdjacencyEdge> freeConnections = new PriorityQueue<AdjacencyEdge>();

		// gather not used connections
		PriorityQueue<AdjacencyEdge> unusedConnections = new PriorityQueue<AdjacencyEdge>();

		// special queue for repetitive contigs
		PriorityQueue<AdjacencyEdge> repeatConnections = new PriorityQueue<AdjacencyEdge>();

		for (int i = 0; i < contigAdjacencyGraph.adjacencyWeightMatrix.length; i++) { // column
			for (int j = i + 1; j < contigAdjacencyGraph.adjacencyWeightMatrix[i].length; j++) { // row
				// leave out tail to head connections of the same contig
				if (j == i + contigAdjacencyGraph.getNumberOfContigs()) {
					assert (contigAdjacencyGraph.adjacencyWeightMatrix[i][j] == 0);
					continue;
				}
				// if the score is 0, do not include this connection
				if (contigAdjacencyGraph.adjacencyWeightMatrix[i][j] == 0) {
					continue;
				}

				AdjacencyEdge edge = contigAdjacencyGraph
						.getAdjacencyEdgeFromMatrixEntries(layoutGraph, i, j);

				// add all connections to a priority queue
				// distinguish between repetitive and normal contigs
				if (edge.isRepeatConnection()) {
					repeatConnections.add(contigAdjacencyGraph
							.getAdjacencyEdgeFromMatrixEntries(layoutGraph, i,
									j));
				} else {
					freeConnections.add(contigAdjacencyGraph
							.getAdjacencyEdgeFromMatrixEntries(layoutGraph, i,
									j));
				}
			}
		}

		// debugging:contig size to total scor ratio
		// for (int i = 0; i < totalSupport.length; i++) {
		// System.out.println(String.format("%s%s, %d, %f",
		// getContigFromMatrixIndex(i).getId(),
		// getContigConnectorLeftRightFromMatrixIndex(i),
		// getContigFromMatrixIndex(i).getSize(), totalSupport[i] ));
		// }

		// *** phase 1: enter edges in descending weight order like the
		// multifragment heuristic

		// tells if the node with number i is incorporated yet
		AdjacencyEdge[] usedContigs = new AdjacencyEdge[2 * contigAdjacencyGraph.getNumberOfContigs()];

		// first incorporate all regular connections
		// condition: both connectors have to be unused so far
		AdjacencyEdge e;
		while (freeConnections.peek() != null) {
			e = freeConnections.poll();

			if ((usedContigs[e.getMatrixIdi()] == null && usedContigs[e
					.getMatrixIdj()] == null)) {

				layoutGraph.addEdge(e);

				// if the contigs are not used yet, incorporate them
				if (usedContigs[e.getMatrixIdi()] == null) {
					usedContigs[e.getMatrixIdi()] = e;
				}
				if (usedContigs[e.getMatrixIdj()] == null) {
					usedContigs[e.getMatrixIdj()] = e;
				}

			} else { // both contig ends are not connected yet
				unusedConnections.add(e);
			}
		} // while there are good connections in the matrix

		
//		debugging		
//		layoutGraph.writeLayoutAsNeato(new File("1.neato"), LayoutGraph.NeatoOutputType.ONENODE);
		
		
		// ******phase 2: find additional edges, that could be affected by the
		// shadowing effect.
		// go through all unused connections and incorporate them if one
		// connector is not used and the relative support is high enough
		while (unusedConnections.peek() != null) {
			e = unusedConnections.poll();
			// if one connector is free and the relative support is higher than
			// a threshold, incorporate the edge
			if (usedContigs[e.getMatrixIdi()] == null
					&& (e.getRelativeSupporti() > additionalEdgesThreshold)) {
				usedContigs[e.getMatrixIdi()] = e;
				layoutGraph.addEdge(e);
			}
			if (usedContigs[e.getMatrixIdj()] == null
					&& (e.getRelativeSupportj() > additionalEdgesThreshold)) {
				usedContigs[e.getMatrixIdj()] = e;
				layoutGraph.addEdge(e);
			}
		}
		e = null;

		
//		debugging
//		layoutGraph.writeLayoutAsNeato(new File("2.neato"), LayoutGraph.NeatoOutputType.ONENODE);

		// **************** phase 3: the repeat handling

		// remember how often each repeat was incorporated
		HashMap<Integer, Integer> repeatCopyNumber = new HashMap<Integer, Integer>();

		// for each repeat connector create a list of possible candidates
		HashMap<Integer, Vector<AdjacencyEdge>> repeatLeftRightCandidates = new HashMap<Integer, Vector<AdjacencyEdge>>();
		// initialize vectors for repetitive contigs
		for (int i = 0; i < contigAdjacencyGraph.getNumberOfContigs(); i++) {
			if (contigAdjacencyGraph.getContigFromMatrixIndex(i).isRepetitive()) {
				repeatLeftRightCandidates.put(i, new Vector<AdjacencyEdge>());
				repeatLeftRightCandidates.put(i
						+ contigAdjacencyGraph.getNumberOfContigs(),
						new Vector<AdjacencyEdge>());
				repeatCopyNumber.put(i, 1);
			}
		}

		// gather all connections
		Vector<AdjacencyEdge> repeatAllCandidates = new Vector<AdjacencyEdge>();

		// go through all repeat connections and add the candidates to their
		// list
		while (repeatConnections.peek() != null) {
			e = repeatConnections.poll();

			// if both are repetitive, skip this connection
			if (e.getContigi().isRepetitive() && e.getContigj().isRepetitive()) {
				continue;
			}

			// all connections where e.i is repetitive and the relative support
			// is high enough
			if (e.getContigi().isRepetitive()
					&& e.getRelativeSupporti() > repetitiveConnectionThreshold) {
				repeatLeftRightCandidates.get(e.getMatrixIdi()).add(e);
				repeatAllCandidates.add(e);
			}

			// all connections where e.j is repetitive and the relative support
			// is high enough
			if (e.getContigj().isRepetitive()
					&& e.getRelativeSupportj() > repetitiveConnectionThreshold) {
				// as convention e.i is always the repetitive contig! so switch
				// here
				e.swapij();
				repeatLeftRightCandidates.get(e.getMatrixIdi()).add(e);
				repeatAllCandidates.add(e);
			}
		} // add all reptitive edges to the appropriate datastructures

		// remember which conntections are already used
		boolean[] repeatAllCandidatesUsed = new boolean[repeatAllCandidates
				.size()];
		// initialize: all connections are unused, so far
		for (int i = 0; i < repeatAllCandidatesUsed.length; i++) {
			repeatAllCandidatesUsed[i] = false;
		}

		// go through all repetitive contigs
		for (int i = 0; i < repeatAllCandidates.size(); i++) {
			if (repeatAllCandidatesUsed[i]) {
				continue;
			}

			AdjacencyEdge oneSide = repeatAllCandidates.get(i);

			System.out.println("Processing edge " + oneSide
					+ " with relative evidence "
					+ oneSide.getRelativeSupporti() * 100.);

			if (oneSide.getContigj().isRepetitive()) {
				System.err
						.println("Upsi... j is repetitive. This should not be.");
			}


			AdjacencyEdge otherSide = null;
			// get the other side of the repetitive contig
			int otherSideRepeatConnector = oneSide.getMatrixIdi();
			if (otherSideRepeatConnector < contigAdjacencyGraph.getNumberOfContigs()) {
				otherSideRepeatConnector += contigAdjacencyGraph.getNumberOfContigs();
			} else {
				otherSideRepeatConnector -= contigAdjacencyGraph.getNumberOfContigs();
			}

			double otherSideMaxWeight = 0.;

			// go through all possible partners on the other side and check
			// their
			// adjacency weight
			for (int l = 0; l < (repeatLeftRightCandidates
					.get(otherSideRepeatConnector).size()); l++) {
				AdjacencyEdge candidate = repeatLeftRightCandidates.get(
						otherSideRepeatConnector).get(l);
				double weight = contigAdjacencyGraph.getWeightFromWeightMatrix(
						oneSide.getMatrixIdj(), candidate.getMatrixIdj());
				if (weight > otherSideMaxWeight) {
					otherSide = candidate;
					otherSideMaxWeight = weight;
				}
			}

			if (otherSide != null) {
				System.out.println(" best candidate edge: "
						+ otherSide
						+ " with weight "
						+ contigAdjacencyGraph.getWeightFromWeightMatrix(
								oneSide.getMatrixIdj(), otherSide
										.getMatrixIdj()));

				for (int j = 0; j < repeatAllCandidates.size(); j++) {
					if (repeatAllCandidates.get(j).equals(otherSide)) {
						repeatAllCandidatesUsed[j] = true;
					}
				}

				int repeatId = oneSide.geti();

				int copyNumber = repeatCopyNumber.get(repeatId);
				repeatCopyNumber.put(repeatId, copyNumber + 1);
				
				if(oneSide.getRepeatCounti()>0) {
					System.err.println("Meep: Node seems to be already used");
				}
				if(otherSide.getRepeatCounti()>0) {
					System.err.println("Meep: Node seems to be already used");
				}

				
				AdjacencyEdge e1 = oneSide.clone();
				AdjacencyEdge e2 = otherSide.clone();
				

				e1.setRepeatCounti(copyNumber);
				e2.setRepeatCounti(copyNumber);

				
				layoutGraph.addEdge(e1);
				layoutGraph.addEdge(e2);

			} else {
				System.out.println("!!!No candidate found!!!");

			}
			
			

		}

	}

} // class
