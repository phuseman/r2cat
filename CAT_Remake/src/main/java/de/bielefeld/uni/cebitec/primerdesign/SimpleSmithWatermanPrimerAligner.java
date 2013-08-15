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

package de.bielefeld.uni.cebitec.primerdesign;

/**
 * This class can be used to apply the smith waterman algorithm to two char arrays.
 * It is designed to align only small primer sequences which are not longer than 30 characters!
 * 
 * The default score settings (match=1,mismatch=0,indel=-0.5) are equvalent to the unit cost edit distance (0 for match, 1 for mismatch, 1 for indel). 
 * 
 * @author phuseman
 * 
 */
public class SimpleSmithWatermanPrimerAligner {
	private final int maxTableSize = 30;
	private double[][] smithWatermanScoreMatrix = null;
	// score considering diagonal cell (i-1/j-1) when characters are the same
	private double matchScore = 1;
	// score considering diagonal cell (i-1/j-1) when characters are distinct
	private double misMatchScore = -1;//0;
	// score considering cell to the left (i-1/j) or from above (i/j-1)
	private double indelScore = -2; // -0.5;

	public SimpleSmithWatermanPrimerAligner() {
		smithWatermanScoreMatrix = new double[maxTableSize][maxTableSize];
	}


	/**
	 * This method sets the parameters for the getAlignmentScore-method
	 * The sequences are set and the length of the sequences are set as the border values
	 * for the alignment.
	 * 
	 * @param firstSeq
	 * @param secondSeq
	 * @return
	 */
	public double getAlignmentScore(char[] firstSeq, char[] secondSeq) {
		return getAlignmentScore(firstSeq, secondSeq, firstSeq.length, secondSeq.length);
	}

	/**
	 * This method calculates the alignment for two given sequence to the given positions. (maxI and maxJ)
	 * For the local alignment it uses a simplified version of the smith-waterman algorithm.
	 * It returns the maximum score of the alignment.
	 * 
	 * @param firstSeq
	 * @param secondSeq
	 * @param maxI
	 * @param maxJ
	 * @return alignemtnScore
	 */
	public double getAlignmentScore(char[] firstSeq, char[] secondSeq, int maxI, int maxJ) {
		if (firstSeq.length > maxTableSize || secondSeq.length > maxTableSize) {
			throw new IllegalArgumentException("Sequences that schould be aligned are too big.");
		}

		double maxScore = 0;

		double fromDiagonal = 0;
		double fromLeft = 0;
		double fromAbove = 0;

		// the first row and column is always zero, so we start with the
		// calculation of the scores for the remaining cells in the score matrix
		// (1,1)
		for (int i = 1; i < maxI; i++) {
			for (int j = 1; j < maxJ; j++) {

				fromDiagonal = smithWatermanScoreMatrix[i - 1][j - 1]
						+ ((firstSeq[i - 1] == secondSeq[j - 1]) ? matchScore
								: misMatchScore);
				fromLeft = smithWatermanScoreMatrix[i - 1][j] + indelScore;
				fromAbove = smithWatermanScoreMatrix[i][j - 1] + indelScore;

				// maximum of the above scores is put into the considering cell

				smithWatermanScoreMatrix[i][j] = 
					Math.max(
						Math.max(fromDiagonal, fromLeft),
						Math.max(fromAbove,0)
						);

				// save highest score
				if (maxScore < smithWatermanScoreMatrix[i][j]) {
					maxScore = smithWatermanScoreMatrix[i][j];
				}

			}
		}
	
		return maxScore;
	}


	/**
	 * @return the matchScore
	 */
	public double getMatchScore() {
		return matchScore;
	}


	/**
	 * @param matchScore the matchScore to set
	 */
	public void setMatchScore(double matchScore) {
		this.matchScore = matchScore;
	}


	/**
	 * @return the misMatchScore
	 */
	public double getMisMatchScore() {
		return misMatchScore;
	}


	/**
	 * @param misMatchScore the misMatchScore to set
	 */
	public void setMisMatchScore(double misMatchScore) {
		this.misMatchScore = misMatchScore;
	}


	/**
	 * @return the indelScore
	 */
	public double getIndelScore() {
		return indelScore;
	}


	/**
	 * @param indelScore the indelScore to set
	 */
	public void setIndelScore(double indelScore) {
		this.indelScore = indelScore;
	}

}
