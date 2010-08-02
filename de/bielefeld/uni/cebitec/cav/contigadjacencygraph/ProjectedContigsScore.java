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

/**
 * @author phuseman
 *
 */
public class ProjectedContigsScore {
	//scorefunction parameters: publication (standard) settings
	private double weightingFactorForLostFragments = 0.1; //lost fragment factor
	private double averageInsertionSizeDeviation = 10000.;
	private double averageLostFragmentSize = 2000.;
	private double averageLostFragmentDeviation = 1000.;

	private double treeDistance = 1.;
	
	
	public double score(ProjectedContig a, ProjectedContig b) {
		//calculate the distance of the two projected contigs...
//				distanceWeight = scorefunctionGumbel(distance, treeDistance);
		return  this.scorefunction(a.distance(b));

	}
	
	public void setTreecatScoreSettings() {
		// publication (standard) settings
		weightingFactorForLostFragments = 0.1; // lost fragment factor
		averageInsertionSizeDeviation = 10000.;
		averageLostFragmentSize = 2000.;
		averageLostFragmentDeviation = 1000.;

	}

	public void setRepcatScoreSettings() {
		// repcat settings
		weightingFactorForLostFragments = 0; // lost fragment factor
		averageInsertionSizeDeviation = 2000.;
		averageLostFragmentSize = 2000.;
		averageLostFragmentDeviation = 1000.;
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
			private double scorefunction(double distance) {
		double insertionDeletionExponent = -1
				/ 2.
				* ((distance / (treeDistance * averageInsertionSizeDeviation)) * (distance / (treeDistance * averageInsertionSizeDeviation)));
		double lostFragmentExponent = -1
				/ 2.
				* (((distance - averageLostFragmentSize) / averageLostFragmentDeviation) * ((distance - averageLostFragmentSize) / averageLostFragmentDeviation));
		;

		double sqrt2pi = Math.sqrt(2. * Math.PI);

		double insertionDeletionScore = (1. / (treeDistance * averageInsertionSizeDeviation) * sqrt2pi)
				* Math.exp(insertionDeletionExponent);
		double lostFragmentScore = (1. / averageLostFragmentDeviation * sqrt2pi)
				* Math.exp(lostFragmentExponent);

		double score = ((1 - weightingFactorForLostFragments)
				* insertionDeletionScore + weightingFactorForLostFragments
				* lostFragmentScore);

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
			private double scorefunctionGumbel(double distance) {
		// hard coded :( parameters for Gumble distribution
		double z = 0; // center
		double beta = 1000. * treeDistance;// width

		z = Math.exp(-(distance / beta));

		// we don't divide by beta (normalisation) to have
		// slightly higher values (and to save computation
		// time).
		return z * Math.exp(-z);// /beta;
	}

		/**
		 * @return the treeDistance
		 */
		public double getTreeDistance() {
			return treeDistance;
		}

		/**
		 * @param treeDistance the treeDistance to set
		 */
		public void setTreeDistance(double treeDistance) {
			this.treeDistance = treeDistance;
		}

}
