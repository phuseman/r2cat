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

import java.util.HashMap;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;

/**
 * Several methods of the TreebasedContigSorter are used here. This enhances the
 * heuristic of that class to an exact branch and bound traveling salesman
 * algorithm which computes one optimal tour. This tour can be applied to an
 * allignmentPostitionsList to visualize the result.
 * 
 * First the weight matrix has to be filled and then findPath() can be called.
 * 
 * This approach can be very slow for a dozen of nodes!!
 * 
 * @author phuseman
 */
public class TreebasedContigSorterExact extends TreebasedContigSorter {
	//sort the entries of each row/column to access the next best edge
	// starting from a contig end
	private Vector<Vector<Integer>> sortedAdjacencyRank;

	//global: the best tsp tour found so far.
	// contains the best solution so far
	// the format of a solution is as follows:
	// each entry gives the rank of the contig in the solution.
	// for example:
	// 0 1 2 3 4
	// 2,5,1,3,4
	// means that the first contig is the one with index 2, then 0 and so on
	private int[] bestTravelingSalesmanTour;
	//the cost of the best tour so far.
	private double bestTravelingSalesmanTourCost;
	
	//weight of a contig to the origin, based on the projected contigs
	private double [] nearToOriginWeight;
	
	//contig index where the tsp should start
	private int startContig=0;

	/**
	 * Creates an instance of a treebased contig sorter
	 * which computes a optimal tsp tour 
	 * with an exact branch and bound tsp algorithm.
	 * 
	 * @param matchesToReferenceGenomes
	 *            matches of the same set of contigs to several reference
	 *            genomes. Use the {@link TreebasedContigSorterProject} to
	 *            generate this.
	 */
	public TreebasedContigSorterExact(
			Vector<AlignmentPositionsList> matchesToReferenceGenomes) {
		super(matchesToReferenceGenomes);

	}

	/* (non-Javadoc)
	 * @see de.bielefeld.uni.cebitec.cav.treebased.TreebasedContigSorter#findPath()
	 */
	public void findPath() {
		
		travelingSalesmanBranchAndBound();

		
		//display the result
		for (int j = 0; j < bestTravelingSalesmanTour.length; j++) {
			if (j == numberOfContigs) {
				System.out.print("| ");
			}
			System.out.print(bestTravelingSalesmanTour[j] + " ");
		}
		System.out.println();
		System.out.println("Optimal cost:"+bestTravelingSalesmanTourCost);

		//direct the result to a file, or order an alignmentPositionsList
		System.out.println(showTourAsNeatoGraph(bestTravelingSalesmanTour));
	}


	/* (non-Javadoc)
	 * @see de.bielefeld.uni.cebitec.cav.treebased.TreebasedContigSorter#fillWeightMatrix()
	 * 
	 * Override. We want to find a node which is suited to be a startpoint for a tsp.
	 */
	public void fillWeightMatrix() {
		nearToOriginWeight = new double[numberOfContigs * 2];
		
		//do the actual filling
		super.fillWeightMatrix();
		
		
		
		//check which contig has the best distance to the origin
		double maximum = 0;
		int maxIndex = 0;
		for (int i = 0; i < nearToOriginWeight.length; i++) {
			if (maximum < nearToOriginWeight[i]) {
				maximum = nearToOriginWeight[i];
				maxIndex = i;
			}
		}
		this.startContig = maxIndex;
		
		//after all weights have been computed, convert these to distances
		convertScoresToDistances();
		// and create a helping table which contains the nearest neighbors
		createNearestNeighborTable();
	}
	
	/**
	 * The tsp algorithm works with distances and not with scores. We modify the scores to distances here.
	 * Each weight is substracted from the maximum weight in the graph.
	 */
	private void convertScoresToDistances() {
		// get the minimum and maximum weight
		double min = Double.MAX_VALUE;
		for (int i = 0; i < adjacencyWeightMatrix.length; i++) {
			for (int j = 0; j < adjacencyWeightMatrix[i].length; j++) {
				// adjacencyWeightMatrix[i][j]++;
				// adjacencyWeightMatrix[i][j]=Math.log(adjacencyWeightMatrix[i][j]);
	
				if (maximumWeight < adjacencyWeightMatrix[i][j]) {
					maximumWeight = adjacencyWeightMatrix[i][j];
				}
				if (min > adjacencyWeightMatrix[i][j]) {
					min = adjacencyWeightMatrix[i][j];
				}
			}
		}
		if (min < 0) {
			System.err
					.println("error: smallest value in adjacency weight matrix was negative");
		}
	
		// change the weights to a distance, in order to use a branch and bound
		// algorithm to find the best path
		for (int i = 0; i < adjacencyWeightMatrix.length; i++) { // column
			// number
			for (int j = i; j < adjacencyWeightMatrix[i].length; j++) { // row
				// number
	
				adjacencyWeightMatrix[i][j] = maximumWeight
						- adjacencyWeightMatrix[i][j];
	
				// make it symmetric. its easier to use with the tsp algorithm.
				adjacencyWeightMatrix[j][i] = adjacencyWeightMatrix[i][j];
			}
		}
	
		// set the tail to head connections to distance 0
		for (int i = 0; i < numberOfContigs; i++) {
			adjacencyWeightMatrix[i][i + numberOfContigs] = 0;
			adjacencyWeightMatrix[i + numberOfContigs][i] = 0;
		}
	
	}

	/**
	 * Create a rank array with the indices of the sorted distances this will be
	 * used to take the nearest neighbor in the tsp branch and bound algorithm
	 */
	private void createNearestNeighborTable() {
		sortedAdjacencyRank = new Vector<Vector<Integer>>();
		for (int i = 0; i < adjacencyWeightMatrix.length; i++) { // column
			// number
			sortedAdjacencyRank.add(new Vector<Integer>());
			for (int j = 0; j < adjacencyWeightMatrix[i].length; j++) { // row
				// number
				// compute the index where the position of the actual element
				// has to be placed
				int r = 0;
				while (r < sortedAdjacencyRank.get(i).size()
						&& adjacencyWeightMatrix[i][sortedAdjacencyRank.get(i)
								.get(r)] < adjacencyWeightMatrix[i][j]) {
					r++;
				}
				sortedAdjacencyRank.get(i).insertElementAt(j, r);

			} // for each entry in a row (from left to right)
		} // for each column (from up to down)

	}

	/* (non-Javadoc)
	 * @see de.bielefeld.uni.cebitec.cav.treebased.TreebasedContigSorter#fillWeightMatrixForOneReference(java.util.Vector, double)
	 * Before filling the weights we calculate how far each contig is from the origin.
	 */
	protected void fillWeightMatrixForOneReference(
			Vector<ProjectedContig> matchesList, double treeDistance) {
		determineBestStartContig(matchesList);
		super.fillWeightMatrixForOneReference(matchesList,treeDistance);

	}

	/**
	 * Calculates for a set of projected contigs which contig end is nearest to the origin (i.e. has the highest score).
	 * the result is stored in nearToOriginWeight. There in the end the maximum has to be determined.
	 * @param matchesList
	 */
	private void determineBestStartContig(Vector<ProjectedContig> matchesList) {
		ProjectedContig origin = new ProjectedContig();

		// get information which should be the fist node
		for (int a = 0; a < matchesList.size(); a++) {
			ProjectedContig match = matchesList.get(a);
			int distanceToOrigin = origin.distance(match);
			double weight = Math.exp(-(Math.pow(
					((double) distanceToOrigin / 5000.), 2.)));
			if (match.forwardMatch) {
				nearToOriginWeight[match.contigIndex] += weight;
			} else {
				nearToOriginWeight[match.contigIndex + numberOfContigs] += weight;
			}
		}
	}
	
	/**
	 * 	Find path through the graph (=matrix) with maximum weight (min distance) such that each contig
	 *	is incorporated once
	 * 	Calculate solution for the traveling salesman problem with a branch
	 *	and bound recurrence.
	 *
	 * This method is called to start the recursion. The recursion branches and bounds with respect to the global variabls
	 * bestTravelingSalesmanTour and bestTravelingSalesmanTourCost
	 */
	private void travelingSalesmanBranchAndBound() {
		//will contain the best solution so far.
		bestTravelingSalesmanTour = new int[numberOfContigs * 2];
		// will contain the best distance so far. start with the highest possible value
		bestTravelingSalesmanTourCost = Double.MAX_VALUE;

		// start with the empty tour, first node to use has index 0, zero
		// incorporated nodes and the actual distance is 0;
		int[] initialTour = new int[numberOfContigs * 2];
		
		// we start at the contig which was determined to be closest to the origin
		initialTour[startContig] = 1;
		
		//start the recursion
		travelingSalesmanBranchAndBound(initialTour, startContig, 1, 0.);

	}

	/**
	 * This function will call itself recursively to compute a traveling
	 * salesman tour in a branch and bound fashion. For each partial tour all not
	 * used neigbors are incorporated, in order of increasing distance. If the
	 * distance of a partial tour is worse than the best complete solution, then
	 * the branching is stopped for this partial solution.
	 * 
	 * @param visitedNodes
	 *            array of the nodes visited so far. an entry of zero means that
	 *            this node is not incorporated. a value greater zero gives the
	 *            rank of this node.
	 * @param lastNode
	 *            the last node which was processed. We need this, to realize
	 *            the intra contig edges (heat to tail or vice versa)
	 * @param nodesUsed
	 *            gives the number of nodes which are incorporated in the
	 *            current part of the tour
	 * @param distance
	 *            the distance of the current path of the tour.
	 */
	private void travelingSalesmanBranchAndBound(int[] visitedNodes,
			int lastNode, int nodesUsed, double distance) {
	
		// incorporate the contig.head -> contig.tail (or vice versa) connection
		if (nodesUsed < 2 * numberOfContigs) {
			if (lastNode < numberOfContigs) {
				// if the node was head, go to tail
				lastNode += numberOfContigs;
			} else {
				// if the last node was tail, go to head
				lastNode -= numberOfContigs;
			}
			nodesUsed++;
	
			assert (visitedNodes[lastNode] != 0);
			visitedNodes[lastNode] = nodesUsed;
		}
	
		// if the tour is complete (all nodes are used once)
		// close the circle (edgecost from last to first)
		// and check if this tour is better than the best so far.
		if (nodesUsed == 2 * numberOfContigs) {
			// find the first node
			int firstNode = 0;
			for (int j = 0; j < visitedNodes.length; j++) {
				if (visitedNodes[j] == 1) {
					firstNode = j;
					break;
				}
			}
			double circleClosingWeight = adjacencyWeightMatrix[lastNode][firstNode];
	
			// check if the total distance is the smallest so far
			// if so, replace the best solution by this
			if (distance + circleClosingWeight < bestTravelingSalesmanTourCost) {
				// debug
				checkConsistencyOfTour(visitedNodes);
	
				bestTravelingSalesmanTourCost = distance + circleClosingWeight;
				bestTravelingSalesmanTour = visitedNodes.clone();
			}
	
			// since this tour is complete we do not need to find the next
			// neighbour
			return;
		}
	
		// find the best edge of all neigbors of the current node.
		int nextNeighbor = -1;
		double distanceToNextNeighbor = 0;
		for (int i = 0; i < sortedAdjacencyRank.get(lastNode).size(); i++) {
	
			// take the neigbors in order of their increasing edge weights
			nextNeighbor = sortedAdjacencyRank.get(lastNode).get(i);
	
			// if this node is already used, take the next one.
			if (visitedNodes[nextNeighbor] != 0) {
				nextNeighbor = -1;
				continue;
			}
	
			if (nextNeighbor == -1) {
				// there are no candidates left.
				return;
			}
			// ... else incorporate this node in the tour
			distanceToNextNeighbor = adjacencyWeightMatrix[lastNode][nextNeighbor];
	
			// if (distanceToNextNeighbor == Double.MAX_VALUE) {
			// return;
			// }
	
			// bound -- do not branch if the solution will be worse than the
			// best seen so far
			if (distance + distanceToNextNeighbor < bestTravelingSalesmanTourCost) {
	
				assert (visitedNodes[nextNeighbor] != 0);
	
				int[] visitedNodesRecursionInstance = (visitedNodes.clone());
	
				visitedNodesRecursionInstance[nextNeighbor] = nodesUsed + 1;
	
				// ... and branch
				travelingSalesmanBranchAndBound(visitedNodesRecursionInstance,
						nextNeighbor, nodesUsed + 1, distance
								+ distanceToNextNeighbor);
	
			}
		} // find next best edge (nearest not used neighbour)
	}

	/**
		 * Gives out a tour [integer array] as neato string
		 * @param tour
		 * @return
		 */
		private String showTourAsNeatoGraph(int[] tour) {
			StringBuilder tourStr = new StringBuilder();
			
			tourStr.append("# type the following to generate a graph:\n"
					+ "# dot -Tps -o graph.ps thisfile.dot\n#\n"
					+ "digraph OptimalContigOrder {\n"
					+ " graph [splines=true, size=\"7,10\"];\n"
					+ " edge [len=\"1.5\", fontsize=\"10\"];\n"
					+ " node [ fontsize=\"14\", shape = record];\n\n");
			
			// write a "record" for each contig. the ports head and tail can then be used for connections
			for (int k = 0; k < contigs.size(); k++) {
				//head part of a contig
				tourStr.append(k + " [label=\"<tail>tail|" + contigs.get(k).getId() + "\\n"
						+ String.format("%.1f", contigs.get(k).getSize()/1000.) + "kb|<head>head\\>\",");
				
				if((startContig%numberOfContigs)==k) {
					tourStr.append("color=black,fontcolor=white, style=filled");
				}
				
				tourStr.append("];\n");
			}
	
			
			int lastContigNode = startContig;
			for (int i = 2; i <= numberOfContigs * 2; i++) {
				for (int j = 0; j < tour.length; j++) {
					if (tour[j] == i) {
						if((j%numberOfContigs) == (lastContigNode%numberOfContigs)) {
							lastContigNode = j;
							continue;
						}
						
						tourStr.append(showContigConnectionAsNeatoString(lastContigNode, j));
						
						tourStr.append( " [label=\""
								+ String.format("%.2f",
										adjacencyWeightMatrix[lastContigNode][j])
								+ "\"]\n");
						
						lastContigNode = j;
					}
				}
			}
			tourStr.append(showContigConnectionAsNeatoString(lastContigNode, startContig));
			tourStr.append( " [label=\""
					+ String.format("%.2f",
							adjacencyWeightMatrix[lastContigNode][startContig])
					+ "\"]\n");
			
			tourStr.append("}\n");

			return tourStr.toString();
		}

	private String showContigConnectionAsNeatoString(int i, int j) {
		// the adjacency matrix consists of 4 blocks:
		//          h          t
		// ---------------------------- j
		// h| head->head head->tail
		// t| tail->head tail->tail
		// i
		// since it is symmetric only half of the matrix will be used.
	
		StringBuilder connection = new StringBuilder();
		if (i >= numberOfContigs) {
			connection.append((i -numberOfContigs) + ":tail");
		} else {
			connection.append( i + ":head");
		}
	
		connection.append(" -> ");
	
		if (j >= numberOfContigs) {
			connection.append((j - numberOfContigs) + ":tail");
		} else {
			connection.append(j + ":head");
		}
	
		return connection.toString();
	
	}

	/**
	 * Checks if a tour makes sense.
	 * @param tour
	 * @return
	 */
	private boolean checkConsistencyOfTour(int[] tour) {
		for (int i = 0; i < (tour.length / 2); i++) {
			if (tour[i] != 0) {
				if (tour[i] == (tour[i + numberOfContigs] - 1)) {
					return true;
				}
				if (tour[i] == (tour[i + numberOfContigs] + 1)) {
					return true;
				}

				System.err
						.println(String
								.format(
										"Consistency of a tsp-tour failed: index %d and %d shourld be consecutive, but are %d and %d",
										i, i + numberOfContigs, tour[i], tour[i
												+ numberOfContigs]));
				return false;
			}
		}
		return true;
	}


	/**
	 * Applies the best tsp tour to a alignment positions list.
	 * The result can then be visualized.
	 * @param apl
	 * @return
	 */
	public Vector<DNASequence> applyLayout(AlignmentPositionsList apl) {
		Vector<DNASequence> contigLayout = new Vector<DNASequence>();
		DNASequence contig;

		int contigId = 0;
		boolean reverseComplement = false;

		// make the original contigs acessible
		HashMap<String, DNASequence> originalContigs = new HashMap<String, DNASequence>();
		for (DNASequence sequence : apl.getQueries()) {
			originalContigs.put(sequence.getId(), sequence);
		}

		// take global best tour
		int[] tour = bestTravelingSalesmanTour;
		// check if this tour is consistent
		if (checkConsistencyOfTour(tour) == false) {
			return null;
		}

		// order the original contigs in contigLayout

		for (int i = 1; i <= numberOfContigs * 2; i += 2) {
			for (int j = 0; j < tour.length; j++) {
				if (tour[j] == i) {
					if (j < numberOfContigs) {
						contigId = j;
						reverseComplement = true;

					} else {
						contigId = j - numberOfContigs;
						reverseComplement = false;

					}

					System.out.println(contigs.get(contigId).getId()
							+ (reverseComplement ? " reversed" : ""));

					contig = originalContigs.get(contigs.get(contigId).getId());
					if (contig == null) {
						System.err.println("Contig "
								+ contigs.get(contigId).getId()
								+ " was not present in the given list");
					} else {
						contig.setReverseComplemented(reverseComplement);
						contigLayout.add(contig);
					}
				}
			}
		}
		return contigLayout;
	}
}
