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

package de.bielefeld.uni.cebitec.cav.contigadjacencygraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPosition;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;

/**
 * This class provides methods to create a (fully connected) contig adjacency graph based on matches of contigs to one or several reference genomes.
 * The heart is a matrix containing the scores of all contig ends connecting all other ends.
 * This matrix can be filled using the provided matches with a projection to the reference genome and a subsecuent scoring of the distance of two matches.
 * After the matrix has been filled it can be used with a Layouter to find the interesting edges.
 * 
 * 
 * @author phuseman
 *
 */
public class ContigAdjacencyGraph {

		private Vector<AlignmentPositionsList> contigsToReferencesMatches = null;
		private Vector<Double> contigsToReferencesDistanceList = null;
		private int numberOfContigs = 0;

		private HashMap<String, Integer> contigIndexMap = new HashMap<String, Integer>();
		private Vector<DNASequence> contigs = new Vector<DNASequence>();

		//the adjacencyWeightMatrix consists of 4 parts: see the Documentation of fillWeightMatrix()
		protected double[][] adjacencyWeightMatrix = null;
		

		// the row/collumn sums of the scores. these help to estimate how significant an edge is.
		private double[] totalSupport = null;
		
		
		private boolean removeRepetitiveMatchesForNonrepetitiveContigs=false;
		private ProjectedContigsScore projectedContigsScorer;
		

		

		
		/**
		 * Creates an instance of a contig adjacency graph.
		 * The fully connected graph can be used by a layouter to
		 * find several good adjacencies of contigs.
		 * 
		 * @param matchesToReferenceGenomes
		 *            matches of the same set of contigs to several reference
		 *            genomes. Use the {@link TreebasedContigSorterProject} to
		 *            generate this.
		 */

		public ContigAdjacencyGraph(
				Vector<AlignmentPositionsList> matchesToReferenceGenomes) {
			this.contigsToReferencesMatches = matchesToReferenceGenomes;
			this.numberOfContigs = getNumberOfInvolvedContigs();
			this.projectedContigsScorer = new ProjectedContigsScore();
		}
		




		/**
		 * Sets the distance of the contigs genome to the different reference genomes.
		 * The order of the weights has to be the same as the order in contigsToReferencesMatches.
		 * @param contigsToReferencesDistanceList
		 */
		public void setTreeWeights(Vector<Double> contigsToReferencesDistanceList) {
			this.contigsToReferencesDistanceList = contigsToReferencesDistanceList;
		}





		/**
		 * This calculates the weight matrix from the given matches
		 * (Vector<Alignmentpositionslist>) for all references with the given
		 * scoring function and possibly includes the treeweights, if these are set.
		 * 
		 * the contig adjacency matrix consists of 4 blocks that give the weights for all possible connection.
		 * Each contig has two connectors, a left and a right connector: *left |contig> *right
		 * <pre>
		 *       r                l
		 * ----------------+-------------+ j
		 * r| right->right |  right->left|
		 *  |--------------+-------------+
		 * l| left->right  | left->left  |
		 *  ---------------+-------------+
		 *  i
		 *  </pre>
		 * since it is symmetric only half of the matrix (j > i) will be actually filled with values.
		 * 
		 * 
		 */
			public String fillWeightMatrix() {
				this.adjacencyWeightMatrix = new double[numberOfContigs * 2][numberOfContigs * 2];
			
				StringBuilder out = new StringBuilder();
				
				// for each reference create the projected contigs and fill the weight
				// matrix based on the distances.
				//
				for (int i = 0; i < contigsToReferencesMatches.size(); i++) {
					double treeDistance = 1;
					if (contigsToReferencesDistanceList!=null
							&&!contigsToReferencesDistanceList.isEmpty()
							&& contigsToReferencesDistanceList.get(i)!= -1) {;
						treeDistance = contigsToReferencesDistanceList.get(i);
					} else {
						out.append("Found no tree distance, using neutral treedistance of 1\n");
						treeDistance = 1;
					}
					AlignmentPositionsList apl = contigsToReferencesMatches.get(i);
					
					//add the scores for each reference genome. The method changes the adjacencyWeightMatrix and returns some
					//summary values how many pairs of projected contigs were considered
					out.append( "In reference " + (i+1) + ", " +
							fillWeightMatrixForOneReference(createListOfProjectedContigs(apl),treeDistance)
							+"\n");
				}
				
				//after all matches were considered, calculate the total support
				calculateTotalSupport();
				
				
				return out.toString();
			}





		/**
		 * The scores of the adjacencyWeightMatrix are used by a layouter to determine those connections wich occur most likely in
		 * a true ordering of the contigs. There are different algorithms to do this.
		 * @param layouter Instance of a layouter
		 * @return the layout graph with the most promising edges.
		 */
		public LayoutGraph findPath( LayouterInterface layouter) {
			LayoutGraph layoutGraph = new LayoutGraph(contigs);
			
			double[] leftTotalSupport = new double[numberOfContigs];
			double[] rightTotalSupport = new double[numberOfContigs];
			
			System.arraycopy(totalSupport, 0, rightTotalSupport, 0, numberOfContigs);
			System.arraycopy(totalSupport, numberOfContigs, leftTotalSupport, 0, numberOfContigs);
			
			layoutGraph.setTotalSupportLeftConnectors(leftTotalSupport);
			layoutGraph.setTotalSupportRightConnectors(rightTotalSupport);
			
			layouter.findLayout(this, layoutGraph);
			return layoutGraph;
		}





		/**
		 * This is a debugging function which writes the contig adjacency matrix in
		 * a comma separated value style into a file.
		 * 
		 * @param file
		 *            file to write the matrix to
		 * @throws IOException
		 */
		public void writeWeightMatrix(File file) throws IOException {
			BufferedWriter csv = new BufferedWriter(new FileWriter(file));
			char seperator=',';
			
			csv.write("matrixIndex"+seperator+"contigID"+seperator);
			for (int i = 0; i < adjacencyWeightMatrix.length; i++) {
				try {
					csv.write(getContigFromMatrixIndex(i).getId()
							+ (matrixIndexIsLeftConnector(i)? "_left" : "_right") + seperator);
				} catch (ArrayIndexOutOfBoundsException e) {
					csv.write(seperator);
				}
			}
			csv.newLine();

			for (int i = 0; i < adjacencyWeightMatrix.length; i++) {
				try {
					csv.write(((Integer) i).toString() + seperator + getContigFromMatrixIndex(i).getId()
							+ (matrixIndexIsLeftConnector(i)? "_left" : "_right") + seperator);
				} catch (ArrayIndexOutOfBoundsException e) {
					csv.write(seperator);
				}
				for (int j = 0; j < adjacencyWeightMatrix[i].length; j++) {
						csv.write(((Double)getWeightFromWeightMatrix(i, j)).toString() + seperator);
				}
				csv.newLine();
			}
			csv.newLine();
			
			
			
			csv.close();
		}
		
		/**
		 * Gives the number of distinct contigs that have matches on any of the provided reference genomes.
		 * @return number of contigs
		 */
		public int getNumberOfContigs() {
			return numberOfContigs;
		}


		/**
		 * Sets the scoring parameters to those given in
		 * P. Husemann and J. Stoye. Phylogenetic comparative assembly.
		 * Algorithms Mol. Biol., 5(1):3, 2010.
		 */
		public void setTreecatScoreSettings() {
			projectedContigsScorer.setTreecatScoreSettings();
		}

		/**
		 * Sets the scoring parameters to test parameters for the repeat aware contig sorter.
		 * Here the lost fragments are ignored and the width of the gaussian distribution is reduced.
		 */
		public void setRepcatScoreSettings() {
			projectedContigsScorer.setRepcatScoreSettings();
		}


		/**
		 * When this option is set to true, in the calculation of the contig adjacency graph matche pairs are ignored,
		 * if a match is marked as repetitive, but the corresponding contig is not. 
		 * @param removeRepetitiveMatchesForNonrepetitiveContigs
		 */
		public void setRemoveRepetitiveMatchesForNonrepetitiveContigs(
				boolean removeRepetitiveMatchesForNonrepetitiveContigs) {
			this.removeRepetitiveMatchesForNonrepetitiveContigs = removeRepetitiveMatchesForNonrepetitiveContigs;
		}





		/**
		 * Since the matrix is symmetric, only half of it is stored. this function makes sure to get
		 * the appropriate value, such that w[i][j] == w[j][i].
		 * @param i
		 * @param j
		 * @return
		 */
		protected double getWeightFromWeightMatrix(int i, int j) {
			if (i<j) {
				return adjacencyWeightMatrix[i][j];
			} else {
				return adjacencyWeightMatrix[j][i];
			}
		}





		/**
		 * Gets the index of a contig from a matrix row or column. (Each contig is represented twice in the matrix,
		 * with a left/tail and a right/head connector)
		 * @param index
		 * @return
		 */
		protected DNASequence getContigFromMatrixIndex(int index) {
			if (index >= numberOfContigs) {
				index -= numberOfContigs;
			}
			return contigs.get(index);
		}





		/**
		 * Given a matrix index, this method returns the number of the contig. (The matrix index encodes the number and the connector end (left,right) of a contig)
		 * @param index in matrix
		 * @return number of the contig.
		 */
		protected int getContigNumberFromMatrixIndex(int index) {
			if (index >= numberOfContigs) {
				index -= numberOfContigs;
			}
			return index;
		}





		/**
		 * Tells if a matrix entry refers the right connector of a contig.
		 * 
		 * @param index in the matrix
		 * @return boolean is right connector
		 */
		protected boolean matrixIndexIsRightConnector(int index) {
			if (index < numberOfContigs) {
				return true;
			} else {
				return false;
			}
		}





		/**
		 * Tells if a matrix entry refers the left connector of a contig.
		 * 
		 * @param index in the matrix
		 * @return boolean is right connector
		 */
		protected boolean matrixIndexIsLeftConnector(int index) {
			return !matrixIndexIsRightConnector(index);
		}





		/**
		 * Convenience function to create an adjacency edge for two matrix entries, given a layout graph.
		 * @param l instance of a layout graph
		 * @param i matrix index of the first contig
		 * @param j matrix index of the second contig
		 * @return an AdjacencyEdge to be used in a LayoutGraph
		 */
		protected AdjacencyEdge getAdjacencyEdgeFromMatrixEntries(LayoutGraph l, int i, int j) {
			return l.new AdjacencyEdge(
					getContigNumberFromMatrixIndex(i),
					matrixIndexIsLeftConnector(i),
					getContigNumberFromMatrixIndex(j),
					matrixIndexIsLeftConnector(j),
					getWeightFromWeightMatrix(i, j));
		}





		/**
		 * Creates a list of projected contigs. That means that the position of the
		 * match determines where the contig would be located on the reference
		 * genome.
		 * 
		 * @param apl
		 *            list of matches
		 * @return a list of projected contigs
		 */
		private Vector<ProjectedContig> createListOfProjectedContigs(
				AlignmentPositionsList apl) {
			int contigIndex = 0;
			int targetIndex = 0;
		
			ProjectedContig projectedContig;
		
			HashMap<String, Integer> targetIndexMap = new HashMap<String, Integer>();
		
			Vector<ProjectedContig> projectedContigsList = new Vector<ProjectedContig>();
		
			for (AlignmentPosition ap : apl) {
				// debugging: filter by differnt criteria
				// if (ap.getQuery().getSize() < 5000) {
				// System.out.println("Skipping:" + ap + ", contig is is too small "
				// + ap.getQuery().getSize());
				// continue;
				// }
		
				// if (ap.size() < 200) {
				// System.out.println("Skipping:" + ap + ", hit is is too small "
				// + ap.size());
				// continue;
				// }
				//
				//			
				// if (ap.getRepeatCount() > 1) {
				// System.out.println("Skipping:" + ap + ", it is repeating "
				// + ap.getRepeatCount());
				// continue;
				// }
		
				// if (ap.getRepeatCount() > 1 && !ap.getQuery().isRepetitive()) {
				// System.out.println("Skipping:" + ap
				// + ", it is repeating but its not the whole contig"
				// + ap.getRepeatCount());
				// continue;
				// }
		
				// check if this target already exists
				if (targetIndexMap.containsKey(ap.getTarget().getId())) {
					targetIndex = targetIndexMap.get(ap.getTarget().getId());
				} else {
					targetIndex = targetIndexMap.size();
					targetIndexMap.put(ap.getTarget().getId(), targetIndex);
				}
		
				// check if query already exists
				if (contigIndexMap.containsKey(ap.getQuery().getId())) {
					contigIndex = contigIndexMap.get(ap.getQuery().getId());
		
					// if the contig is repetitive on this target, set it in the
					// global list.
					if (ap.getQuery().isRepetitive()) {
						contigs.get(contigIndex).setRepetitive(true);
					}
		
				} else {
					// if not, add it
					contigIndex = contigs.size();
					contigIndexMap.put(ap.getQuery().getId(), contigs.size());
					contigs.add(ap.getQuery());
				}
		
				projectedContig = new ProjectedContig(contigIndex, targetIndex, ap);
		
				projectedContigsList.add(projectedContig);
			}// go through all AlignmentPositions
		
			return projectedContigsList;
		}





		/**
		 * Helper method to append the weights of all pairwise matches to the adjacencyWeightMatrix.
		 * @param matchesList the projected matches of one reference genome to consider (can be several sequences/chromosomes)
		 * @param treeDistance the phylogenetic distance to the contigs
		 * @return summary about the incorporated pairs of matches.
		 */
		private String fillWeightMatrixForOneReference(
					Vector<ProjectedContig> matchesList, double treeDistance) {
			projectedContigsScorer.setTreeDistance(treeDistance);
				
				//collect some "statistics"
				int possiblePairs = 0;
				int employedPairs = 0;
				int differentReferencesPairs = 0;
				int sameContigPairs = 0;
				int repeatingMatchOnNonRepeatingContigPairs = 0;
				StringBuilder out = new StringBuilder();
				
				
				//avoid reinstanciation inside the for loops:
				ProjectedContig first;
				ProjectedContig second;
				// indices for the contig adjacency matrix
				int i = 0;
				int j = 0;
			
				// go through all pairs of projected contigs
				for (int matchIndexA = 0; matchIndexA < matchesList.size(); matchIndexA++) {
					for (int matchIndexB = matchIndexA + 1; matchIndexB < matchesList
							.size(); matchIndexB++) {
					//do not move this into the outer for loop!!
						first = matchesList.get(matchIndexA);
						second = matchesList.get(matchIndexB);
						
						possiblePairs++;
			
			
						// skip this pair if the references are distinct
						if (first.referenceIndex != second.referenceIndex) {
							differentReferencesPairs++;
							continue;
						}
						// skip this pair if they belong to the same contig
						if (first.contigIndex == second.contigIndex) {
							sameContigPairs++;
							continue;
						}
						
		
						// the first contig should be the leftmost projected contig. if not -> swap
						if (second.start < first.start) {
							ProjectedContig tmp = first;
							first = second;
							second = tmp;
						}
			
						
						
						if(removeRepetitiveMatchesForNonrepetitiveContigs) {
						//skip repetitive matches for nonrepetitive contigs
						if (!first.ap.getQuery().isRepetitive() 
							&& first.ap.getRepeatCount() >= 1 ) {
							repeatingMatchOnNonRepeatingContigPairs++;
							continue;
						}
		
						//skip repetitive matches for nonrepetitive contigs
						if (!second.ap.getQuery().isRepetitive() 
								&& second.ap.getRepeatCount() >= 1 ) {
								repeatingMatchOnNonRepeatingContigPairs++;
								continue;
							}
						}
						
						
						// compute the index of this projected contigs pair in the
						// adjacency matrix:
						i = first.contigIndex;
						j = second.contigIndex;
			
			
						// the contig adjacency matrix consists of 4 blocks:
						//          h          t
						// ---------------------------- j (second)
						// h| head->head head->tail
						// t| tail->head tail->tail
						// i (first)
						// since it is symmetric only half of the matrix will be used.
						// (j > i)
						//
						// if both are forward matches,
						// the normal direction is first.head -> second.tail
						// |first>=|second>
						// 
						// if the first one is reversed... <first|=...
						if (!first.forwardMatch) {
							// then this is a
							// connection starting from first.tail and we have to
							// adjust the index to use lower half of the matrix
							i += numberOfContigs;
						}
			
						// if the second match is forward (...=|second>), the connection is 
						// to the tail of it. adjust the index to use the right half of the matrix.
						if (second.forwardMatch) {
							j += numberOfContigs;
						}
			
						// the matrix is symmetric. swap indices such that j>=i.
						// use the upper left half
						if (i > j) {
							int tmp = j;
							j = i;
							i = tmp;
						}
			
						//calculate a score for a connection that is based on
						// the distance of the projected contigs and the quantity of the matches
						// and add it to the weight matrix.
						adjacencyWeightMatrix[i][j] += (projectedContigsScorer.score(first, second)
								* (double) first.qhits * (double) second.qhits);
			
						employedPairs++;
						assert (adjacencyWeightMatrix[i][j] < 0);
						
			
					} // all pairs of projected contigs
				} // all pairs of projected contigs
		
				out.append(String.format(" %.1f%% of all %d possible pairs of projected contigs were used.\n",100.*employedPairs/possiblePairs,possiblePairs));
				if (differentReferencesPairs>0) {
				out.append(String.format(" * Skipped %d (%.1f%%) pairs that are on different reference genomes\n", differentReferencesPairs, 100.*differentReferencesPairs/possiblePairs));
				}
				if (sameContigPairs>0) {
				out.append(String.format(" * Skipped %d (%.1f%%) pairs belonging to the same contig\n", sameContigPairs, 100.*sameContigPairs/possiblePairs));
				}
				if (repeatingMatchOnNonRepeatingContigPairs>0) {
				out.append(String.format(" * Skipped %d (%.1f%%) pairs where one match was repetitive while the contig was not\n", repeatingMatchOnNonRepeatingContigPairs, 100.*repeatingMatchOnNonRepeatingContigPairs/possiblePairs));
				}
		
				return out.toString();
			}





		/**
		 * After the matrix has been filled, this can be used to calculate the row resp. column summs.
		 * These are called total support for each connector and can give a clue how important a single edge is with respect to all edges to that contig.
		 */
		private void calculateTotalSupport() {
			totalSupport = new double[2 * numberOfContigs];
		
			// collect the total support for all edges 
		
			for (int i = 0; i < adjacencyWeightMatrix.length; i++) { // column
				for (int j = i + 1; j < adjacencyWeightMatrix[i].length; j++) { // row
					// leave out tail to head connections of the same contig
					if (j == i + numberOfContigs) {
						assert (adjacencyWeightMatrix[i][j] == 0);
						continue;
					}
					
					//skip zero weight edges
					if (adjacencyWeightMatrix[i][j] == 0) {
						continue;
					}
		
					// calculate the sum of all scores for edges of a contig
					// connector.
					// with this number it is possible to estimate how significant a
					// score
					// is.
					totalSupport[i] += adjacencyWeightMatrix[i][j];
					totalSupport[j] += adjacencyWeightMatrix[i][j];
				}
			}
		
		}





		/**
		 * It can happen that not all contigs are matched to all reference genomes.
		 * This function checks how many distinct contigs exist. It is called in the constructor
		 * and the result is saved in numberOfContigs, to avoid several recomputations.
		 * @return
		 */
		private int getNumberOfInvolvedContigs() {
			HashMap<String, DNASequence> existingContigs = new HashMap<String, DNASequence>();
			for (AlignmentPositionsList apl : contigsToReferencesMatches) {
				for (DNASequence contig : apl.getQueries()) {
					if (!existingContigs.containsKey(contig.getId())) {
						existingContigs.put(contig.getId(), contig);
					}
				}
			}
			return existingContigs.size();
		}
		
}
