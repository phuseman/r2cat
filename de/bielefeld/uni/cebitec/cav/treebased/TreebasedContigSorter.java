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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPosition;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.utils.NeatoWriter;

/**
 * @author phuseman
 * 
 */
public class TreebasedContigSorter {
	public class MatrixEntry implements Comparable<MatrixEntry> {
		int i = 0;
		int j = 0;
		double score = 0;
	
		public MatrixEntry(int i, int j, double score) {
			this.i = i;
			this.j = j;
			this.score = score;
		}
	
		@Override
		public int compareTo(MatrixEntry other) {
			if (this.score == other.score) {
				return 0;
			}
			if (this.score > other.score) {
				return -1;
			} else {
				return 1;
			}
		}
	
		public String toString() {
			return String.format("%s %.2f", showContigConnectionAsciiString(i,
					j), Math.log10(score));
		}
	
	}

	
	protected Vector<AlignmentPositionsList> contigsToReferencesMatches = null;
	protected Vector<Double> contigsToReferencesDistanceList = null;
	protected int numberOfContigs = 0;

	protected HashMap<String, Integer> contigIndexMap = new HashMap<String, Integer>();
	protected Vector<DNASequence> contigs = new Vector<DNASequence>();

	protected double[][] adjacencyWeightMatrix = null;
	protected double maximumWeight = 0;


	// debugging
	protected NeatoWriter neato;

	
	/**
	 * Creates an instance of a treebased contig sorter which uses a heuristic to
	 * find several good adjacencies of contigs.
	 * 
	 * @param matchesToReferenceGenomes
	 *            matches of the same set of contigs to several reference
	 *            genomes. Use the {@link TreebasedContigSorterProject} to
	 *            generate this.
	 */

	public TreebasedContigSorter(
			Vector<AlignmentPositionsList> matchesToReferenceGenomes) {
		this.contigsToReferencesMatches = matchesToReferenceGenomes;
		this.numberOfContigs = getNumberOfInvolvedContigs();
		neato = null;
	}
	

	/**
	 * Creates an instance of a treebased contig sorter which uses a heuristic
	 * to find several good adjacencies of contigs. The resulting graph is
	 * written as a neato file, which can be visualized using the graphviz
	 * package.
	 * 
	 * @param matchesToReferenceGenomes
	 * @param graphOutput
	 */
	public TreebasedContigSorter(
			Vector<AlignmentPositionsList> matchesToReferenceGenomes,
			File graphOutput) {
		this.contigsToReferencesMatches = matchesToReferenceGenomes;
		this.numberOfContigs = getNumberOfInvolvedContigs();

		neato = new NeatoWriter(graphOutput);

	}
	



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

	public void setTreeWeights(Vector<Double> contigsToReferencesDistanceList) {
		this.contigsToReferencesDistanceList = contigsToReferencesDistanceList;
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

	
	public void fillWeightMatrix() {
		// the contig adjacency matrix consists of 4 blocks:
		// h t
		// ---------------------------- j
		// h| head->head head->tail
		// t| tail->head tail->tail
		// i
		// since it is symmetric only half of the matrix will be used.
		// (j > i)
		this.adjacencyWeightMatrix = new double[numberOfContigs * 2][numberOfContigs * 2];
	
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
				System.out.println("Found no tree distance, using neutral treedistance of 1");
				treeDistance = 1;
			}
			AlignmentPositionsList apl = contigsToReferencesMatches.get(i);
			fillWeightMatrixForOneReference(createListOfProjectedContigs(apl),
					treeDistance);
		}
		
		
		//debugging
//		try {
//			writeWeightMatrix(new File("/homes/phuseman/compassemb/treebased/log.csv"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	
	/**
	 * This is a debugging function which writes the contig adjacency matrix in
	 * a comma separated value style into a file.
	 * 
	 * @param file
	 *            file to write the matrix to
	 * @throws IOException
	 */
	private void writeWeightMatrix(File file) throws IOException {
		BufferedWriter csv = new BufferedWriter(new FileWriter(file));
		char seperator=',';
		
		csv.write(seperator);
		csv.write(seperator);
		for (int i = 0; i < adjacencyWeightMatrix.length; i++) {
			try {
				csv.write(contigs.get(i % numberOfContigs).getId()
						+ ((i >= numberOfContigs) ? "_tail" : "_head") + seperator);
			} catch (ArrayIndexOutOfBoundsException e) {
				csv.write(seperator);
			}
		}
		csv.newLine();

		for (int i = 0; i < adjacencyWeightMatrix.length; i++) {
			try {
				csv.write(((Integer) i).toString() + seperator + contigs.get(i % numberOfContigs).getId()
						+ ((i >= numberOfContigs) ? "_tail" : "_head") + seperator);
			} catch (ArrayIndexOutOfBoundsException e) {
				csv.write(seperator);
			}
			for (int j = 0; j < adjacencyWeightMatrix[i].length; j++) {
				// csv.write((int) (adjacencyWeightMatrix[i][j] / maximumWeight
				// *
				// 100.)
				// + seperator);
				// s.append(Math.log(adjacencyWeightMatrix[i][j]+1.));
				csv.write(((Double)adjacencyWeightMatrix[i][j]).toString() + seperator);
			}
			csv.newLine();
		}
		csv.newLine();
		
		csv.close();
	}
	
	protected void fillWeightMatrixForOneReference(
			Vector<ProjectedContig> matchesList, double treeDistance) {
		
		//avoid reinstanciation inside the for loops:
		ProjectedContig first;
		ProjectedContig second;
		int distance = 0;
		double distanceWeight = 0;
		// indices for the contig adjacency matrix
		int i = 0;
		int j = 0;
	
		// go through all pairs of projected contigs
		for (int matchIndexA = 0; matchIndexA < matchesList.size(); matchIndexA++) {
			for (int matchIndexB = matchIndexA + 1; matchIndexB < matchesList
					.size(); matchIndexB++) {
	
				first = matchesList.get(matchIndexA);
				second = matchesList.get(matchIndexB);
	
				// skip this pair if the references are distinct
				if (first.referenceIndex != second.referenceIndex) {
					continue;
				}
				// skip this pair if they belong to the same contig
				if (first.contigIndex == second.contigIndex) {
					continue;
				}
	
				// the first contig should be the leftmost projected contig. if not -> swap
				if (second.start < first.start) {
					ProjectedContig tmp = first;
					first = second;
					second = tmp;
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
	
				//calculate the distance of the two projected contigs...
				distance = first.distance(second);
				// ...to get a weight factor...
				distanceWeight = scorefunction(distance, treeDistance);
				//... that is multiplied with the "quality" of the matches
				// and added to the weight matrix.
				adjacencyWeightMatrix[i][j] += (distanceWeight
						* (double) first.qhits * (double) second.qhits);
	
				assert (adjacencyWeightMatrix[i][j] < 0);
	
				if (adjacencyWeightMatrix[i][j] > maximumWeight) {
					maximumWeight = adjacencyWeightMatrix[i][j];
				}
	
			} // all pairs of projected contigs
		} // all pairs of projected contigs
	
	}

	/**
		 * This scorefunction models insertions and deletions with one Gaussian distribution 
		 * (expected value 0, std. deviation 10000)
		 * and lost fragments during assembly with a second Gaussian distribution
		 * (expected value 2000, std. deviation 1000).
		 * The phylogenetic distance of the involved species influences the std. deviation of the
		 * first gaussian distribution. A higher evolutionary distance allows larger insertions but
		 * scores adjacent contigs less. 
		 * 
	  0.00025 ++------+--------+-------+-------+-------+--------+-------+------++
	          +       +        +       +       +       +        +   dT=1+****** +
	          |                                                                 |
	          |                                   **                            |
	   0.0002 ++                                  **                           ++
	          |                                  * *                            |
	          |                                  * *                            |
	          |                                  *  *                           |
	  0.00015 ++                                 *  *                          ++
	          |                                  *  *                           |
	          |                                  *  *                           |
	          |                                 *    *                          |
	   0.0001 ++                                *    *                         ++
	          |                                 *    *                          |
	          |                                *     *                          |
	          |                                *     *                          |
	    5e-05 ++                               *     *                         ++
	          |                                *      *                         |
	          |                          ******        *                        |
	          +       ********************     +       +*****************       +
	        0 ********+--------+-------+-------+-------+--------+-------+********
	       -20000  -15000   -10000   -5000     0      5000    10000   15000   20000
	
	
	
		 * 
		 * exerpt from the paper:
		 * 
		 * In order to define $s$ we will give some further details that
		 * biologically motivate this scoring function. The distance of two
		 * projected contigs, which are putatively adjacent, can be positive due to
		 * insertions in the reference genome or negative if the projections are
		 * overlapping. The latter case happens if there are insertions into the
		 * reference genome. Both cases can be seen in the example in
		 * Fig.~\ref{fig:insertionDistance}. Note that insertions in one genome look
		 * the same as deletions in the other since we do not know what happened
		 * during evolution. To model this behavior one can use a Gaussian
		 * distribution with a expected value of zero: \[ s(d,
		 * d_{\mathcal{T}}):=\frac{1}{ d_{\mathcal{T}} \cdot \sigma \sqrt{2\pi}}
		 * e^{-\frac{1}{2}(\frac{d}{ d_{\mathcal{T}} \cdot \sigma})^2} \] We assume
		 * that insertions are larger between evolutionary more distant species,
		 * thus the standard deviation $\sigma$ is scaled by the evolutionary
		 * distance $d_{\mathcal{T}}$ of the involved genomes. The standard
		 * deviation $\sigma$ can be estimated from the sequences of the reference
		 * genomes. But this modeling neglects the fact that in the fragmentation
		 * phase, for example in parallel pyrosequencing, often fragments disappear
		 * such that there are no reads for this fragment. If a fragment is not
		 * sequenced it is the same situation as if there is an insertion into the
		 * reference genome, which causes positive distances. Including this detail
		 * we use two superimposed Gaussian distributions to score that two contigs
		 * are adjacent. The first distribution models insertions (or deletions)
		 * into the contigs and into the reference genome, the second models lost
		 * fragments during sequence assembly.
		 * 
		 * @param distance distance of the projected contigs
		 * @param treeDistance phylogenetic distance
		 * @return scorefactor for this connection
		 */
		private double scorefunction(double distance, double treeDistance) {
			
			double averageInsertionSizeDeviation = 10000.;
			double averageLostFragmentSize = 2000.;
			double averageLostFragmentDeviation = 1000.;
	
			double insertionDeletionExponent=-1/2. * ( 
					  (distance/(treeDistance*averageInsertionSizeDeviation)) 
					* (distance/(treeDistance*averageInsertionSizeDeviation))
					);
			double lostFragmentExponent=-1/2. * ( 
					  ((distance-averageLostFragmentSize)/averageLostFragmentDeviation) 
					* ((distance-averageLostFragmentSize)/averageLostFragmentDeviation)
						);;
			
			double sqrt2pi = Math.sqrt(2.*Math.PI);
			
			
			double insertionDeletionScore=(1./(treeDistance*averageInsertionSizeDeviation)*sqrt2pi)
			*Math.exp( insertionDeletionExponent );
			double lostFragmentScore = (1./averageLostFragmentDeviation*sqrt2pi)
			*Math.exp( lostFragmentExponent );
			
			double score=(insertionDeletionScore+lostFragmentScore)/2.;
			
	//		System.out.println(String.format("%f %f %f", distance, treeDistance, score));
			
			return score;
		}

	/**
		 * Alternative scoring function, the Gumble ditribution.
		 * It looks like a normal distribution which is skewed to the right (see below).
		 * A biological motivation would be needed to use this, so it is not incorporated at the moment.
		 * The tree distance is used to make the curve below wider.
		 * Normally the gumble distribution is normalized. We skip this step, since it is a constant scaling factor..
		 * 
		 * 
	
	   0.4 ++---+--------+--------+--------+--------+---------+--------+-------++
	       |    +        +        +        +       Gumble with beta=2000 ****** +
	  0.35 ++                   ******                                         ++
	       |                   *     **                                         |
	       |                 **        **                                       |
	   0.3 ++               *           **                                     ++
	       |                *             *                                     |
	  0.25 ++              *              **                                   ++
	       |              *                 *                                   |
	       |              *                  *                                  |
	   0.2 ++             *                   **                               ++
	       |             *                     **                               |
	  0.15 ++           *                        *                             ++
	       |            *                         **                            |
	       |           *                           ***                          |
	   0.1 ++         *                               **                       ++
	       |         *                                  ***                     |
	  0.05 ++        *                                    ***                  ++
	       |       **                                        *****              |
	       |    +***     +        +        +        +         +   ***********   +
	     0 *******-------+--------+--------+--------+---------+--------+-----****
	          -4000    -2000      0       2000     4000      6000     8000    10000
		 * gnuplot: a=2000.; plot exp(-exp(-x/a))*(exp(-(x/a)))
		 * latex: \frac{z \cdot e^{-z}}{\beta} with z =
			 e^{-\frac{x-\mu}{\beta}}
	
	
		 * 
		 * @param distance the distance of the projected contigs
		 * @param treeDistance the treedistance of the involved species
		 * @return scoring factor for this distance.
		 */
		private double scorefunctionGumbel(double distance, double treeDistance) {
			// hard coded :(  parameters for Gumble distribution
			double z = 0; // center
			double beta = 2000. * treeDistance;// width
	
			z = Math.exp(-(distance / beta));
	
			// we don't divide by beta (normalisation) to have
			// slightly higher values (and to save computation
			// time).
			return  z * Math.exp(-z);// /beta;
		}

	public void findPath() {
	// write all contigs as node to the neato output
			StringBuffer params;
			String node;
			for (int k = 0; k < contigs.size(); k++) {
				params = new StringBuffer();
				node= contigs.get(k).getId();
				if (node.indexOf("_")!=-1) {
					node = node.substring(0, node.indexOf("_"));
				}
				params.append("label=\"" + node + "\\n"
						+ String.format("%.1f", contigs.get(k).getSize()/1000.) + "kb\",");
				if (contigs.get(k).isRepetitive()) {
					params.append("shape=box,");
				}
				if (contigs.get(k).getSize() < 3500) {
					params.append("color=gray,fontcolor=gray20,");
				}
	
				
				neato.nodeDescription(node, params.toString());
				
			}
	
				greedyHeuristic();
				neato.finish();
		}

	/**
	 * This is a greedy heuristic based on the multi fragment heuristic for the TSP.
	 * It pics all good connections as long as one of the two involved contigs has still a free end left.
	 */
	private void greedyHeuristic() {

		// tells if the node with number i is incorporated yet
		MatrixEntry[] usedContigs = new MatrixEntry[2 * numberOfContigs];

		// matrix entries in sorted order. biggest first.
		PriorityQueue<MatrixEntry> freeConnections = new PriorityQueue<MatrixEntry>();

		// add all matrix entries
		for (int i = 0; i < adjacencyWeightMatrix.length; i++) { // column
			// number
			for (int j = i + 1; j < adjacencyWeightMatrix[i].length; j++) { // row
				// leave out tail to head connections of the same contig
				if (j == i + numberOfContigs) {
					continue;
				}
				// if the score is 0, do not include this connection
				if (adjacencyWeightMatrix[i][j] == 0) {
					continue;
				}

				freeConnections.add(new MatrixEntry(i, j,
						adjacencyWeightMatrix[i][j]));
			}
		}

		Vector<MatrixEntry> incorporatedEdges= new Vector<MatrixEntry>();
		
		MatrixEntry e;
		while (freeConnections.peek() != null) {
			e = freeConnections.poll();

			if ((usedContigs[e.i] == null || usedContigs[e.j] == null)
					&& e.score > 1.) {
				
				incorporatedEdges.add(e);
				// if the contigs are not used yet, incorporate them
				if (usedContigs[e.i] == null) {
					usedContigs[e.i] = e;
				}
				if (usedContigs[e.j] == null) {
					usedContigs[e.j] = e;
				}

			} // one of the contig ends is not connected yet
		} // while there are good connections in the matrix


		boolean connectionToSmallContig=false;
		
		for (MatrixEntry element : incorporatedEdges) {			
			int first = element.i;
			int second = element.j;

			if (first >= numberOfContigs) {
				first -= numberOfContigs;
			}
			if (second >= numberOfContigs) {
				second -= numberOfContigs;
			}

			String a = contigs.get(first).getId();
			if(a.indexOf("_")!=-1) {
			a = a.substring(0, a.indexOf("_"));
			}
			String b = contigs.get(second).getId();
			if(b.indexOf("_")!=-1) {
			b = b.substring(0, b.indexOf("_"));
			}

			if(contigs.get(first).getSize()<3500 || contigs.get(second).getSize()<3500) {
				connectionToSmallContig=true;
			} else {
				connectionToSmallContig=false;
			}
			
			neato.addConnection(a, b, "label="
					+ String.format("%.2f", Math.log10(element.score)) + 
					(connectionToSmallContig?",color=gray,fontcolor=gray20,":""));
		}

	}




	protected String showContigConnectionAsciiString(int i, int j) {
		// the adjacency matrix consists of 4 blocks:
		// h t
		// ---------------------------- j
		// h| head->head head->tail
		// t| tail->head tail->tail
		// i
		// since it is symmetric only half of the matrix will be used.

		String connection = "";
		if (i >= numberOfContigs) {
			connection = "<" + contigs.get(i - numberOfContigs).getId() + "|";
		} else {
			connection = "|" + contigs.get(i).getId() + ">";
		}

		if (j >= numberOfContigs) {
			connection += "|" + contigs.get(j - numberOfContigs).getId() + ">";
		} else {
			connection += "<" + contigs.get(j).getId() + "|";
		}

		return connection;

	}


} // class