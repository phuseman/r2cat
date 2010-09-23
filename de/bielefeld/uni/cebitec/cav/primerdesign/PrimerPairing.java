/***************************************************************************
 *   Copyright (C) 2010 by Yvonne Hermann, Peter Husemann                  *
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


package de.bielefeld.uni.cebitec.cav.primerdesign;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * This class includes methods to make primer pairs from primer candidates of the possible forward and reverse
 * primer located on the according contig end.
 * 
 * It includes getter and setter methods for the HashMaps which include the information of
 * which pairs are possible.
 * 
 * @author yherrman
 *
 */
public class PrimerPairing {
	private Bases base;
	private SimpleSmithWatermanPrimerAligner swa;
	
	private final double maxTemperatureDifference = 5.;
	//value for std of a gaussian courve such that x=8 is quite small and x=0 is y=1.0
	private final double maxAlignmentScore = 32.;
	

	public class PrimerPair implements Comparable<PrimerPair>{
		Primer left;
		Primer right;
		double score;
		
		public PrimerPair(Primer left, Primer right, double pairScore) {
			this.left = left;
			this.right=right;
			this.score=pairScore;
		}

		@Override
		public int compareTo(PrimerPair o) {
			if (this.score < o.score) {
				return 1;
			}
			if (this.score > o.score) {
				return -1;
			}
			return 0;
		}
	}
	
	public PrimerPairing() {
		base = Bases.getInstance();
		swa = new SimpleSmithWatermanPrimerAligner();
	}



    /**
	 * This method tries to find suitable pairs of primers.
	 * If primers are generated for two contigs, then the best scoring primers for each contig do not necessarily fit together.
	 * If the sequences are reverse complementary, the primer pair could bind together. Another issue is the temperature of the
	 * given candidates. This method compares temperature and sequence and the previously calculated score for the individual primers and
	 * generates a list of those which are best scoring and fit together by means of temperature and sequence (dis)similarity.
	 * 
	 * @param leftPrimer
	 * @param rightPrimer
	 */
	
	public PrimerResult pairPrimer(Vector<Primer> leftPrimer,
			Vector<Primer> rightPrimer, PrimerResult primerResult) {
		Collections.sort(leftPrimer);
		Collections.sort(rightPrimer);
	
		PriorityQueue<PrimerPair> bestPairs = new PriorityQueue<PrimerPair>();
	
		int maxCandidatesToConsider = 20;
		int maxRight = rightPrimer.size();
		int maxLeft = leftPrimer.size();
	
		double totalscore = 0;
	
		for (int i = 0; i <= maxCandidatesToConsider; i++) {
			for (int j = i; j >= 0; j--) {
				//for all right primers with j<=i calculate score and add them to the pairs list
				if (i < maxLeft && j < maxRight) {
					totalscore = scorePrimerPair(leftPrimer.get(i), rightPrimer
							.get(j));
					//add pair only if none of the quickchecks have failed (score==0.)
					if (totalscore > 0.) {
						bestPairs.add(new PrimerPair(leftPrimer.get(i),
								rightPrimer.get(j), totalscore));
					}
				}
				//for all left primers with j<i calculate score and add them to the pairs list
				if (i != j && j < maxLeft && i < maxRight) {
					totalscore = scorePrimerPair(leftPrimer.get(j), rightPrimer
							.get(i));
					//add pair only if none of the quickchecks have failed (score==0.)
					if (totalscore > 0.) {
						bestPairs.add(new PrimerPair(leftPrimer.get(j),
								rightPrimer.get(i), totalscore));
					}
				}
			}
		}
	
		
		//select the top 20 scores from all compared primers
		PrimerPair pair = null;
		int numberOfResults = 1;
		while (!bestPairs.isEmpty() && numberOfResults <= 20) {
			pair = bestPairs.poll();
			primerResult.addPair(pair.left, pair.right);
			numberOfResults++;
		}
	
		return primerResult;
	}



	/**
	 * Checks the melting temperature difference and the sequence
	 * (dis)similarity to the reverse complement of the second sequence and
	 * combines this with the scores of the primers to a pair score.
	 * 
	 * @param left
	 *            primer sequence
	 * @param right
	 *            right
	 * @return pair fitting score
	 */
	private double scorePrimerPair(Primer left, Primer right) {
		double temperatureDifferenceScore;
		double sequenceSimilarityScore;
		char[] reverseComplementOfSecondPrimer;
		if (temperatureDifferenceQuickCheck(right.getPrimerTemperature(), left
				.getPrimerTemperature())) {
			temperatureDifferenceScore = temperatureDifferenceScore(right
					.getPrimerTemperature(), left.getPrimerTemperature());
			reverseComplementOfSecondPrimer = base.getReverseComplement(right
					.getPrimerSeq());
			if (sequenceSimilarityQuickCheck(left.getPrimerSeq(),
					reverseComplementOfSecondPrimer)) {
				sequenceSimilarityScore = sequenceSimilarityScore(left
						.getPrimerSeq(), reverseComplementOfSecondPrimer);

				return temperatureDifferenceScore * sequenceSimilarityScore
						* (left.getPrimerScore() + right.getPrimerScore());
			}
		}
		return 0;
	}



	/**
	 * This method checks if the temperature difference of the forward and reverse primer (possible pair).
	 * It returns true when the difference between the two temperatures less than 5 degrees.
	 * 
	 * @param firstTemperature melting temperature of the first primer sequence.
	 * @param secondTemperature melting temperature of the second primer sequence.
	 * @return temperatureCheck true if these temperatures indicate that this primer pair can be used together.
	 */
	public boolean temperatureDifferenceQuickCheck(double firstTemperature, double secondTemperature){
		if(Math.abs(firstTemperature-secondTemperature)<=maxTemperatureDifference){
			return true;
		} else{
			return false;
		}
	}



	/**
     * Score for the melting temperature difference of two primers. 
     * It is best (returns 1.), if the temperature is the same. If the temperature is more than 5 degree apart,
     * this is the worst and the method returns zero. (values in between fall exponentially in a gaussian distribution)
     * @param temp1 temperature of the first primer
     * @param temp2 temperature of the second primer
     * @return value between 1.0 (best) and 0.0 (worst)
     */
    public double temperatureDifferenceScore(double temp1, double temp2) {
    	Double diff = temp1 - temp2;
    	return Math.exp(-(diff * diff / maxTemperatureDifference));
    }
    
    /**
	 * (Quick)checks if the first three bases match. If so, these primers are not suited as a pair and false is returned
	 * @param firstSeq Sequence of the first primer.
	 * @param secondSeqReversed Please provide the reverse complement sequence of the second primer.
	 * @return boolean if these sequence are suited as a primer pair or not.
	 */
	
	public boolean sequenceSimilarityQuickCheck(char[] firstSeq,char[] secondSeqReversed){
		if( firstSeq[0] == secondSeqReversed[0] 
		 && firstSeq[1] == secondSeqReversed[1] 
		 && firstSeq[2] == secondSeqReversed[2]
		 ||firstSeq[firstSeq.length-1]==secondSeqReversed[secondSeqReversed.length-1]
		 &&firstSeq[firstSeq.length-2]==secondSeqReversed[secondSeqReversed.length-2]
		 &&firstSeq[firstSeq.length-3]==secondSeqReversed[secondSeqReversed.length-3]) {
			//if the first three bases match or the last three bases, discard this pair.
			//needs to be checked so the first three bases of the 3'-end won't interact with the three bases at the 5'end
			return false;
		} else {
			return true;
		}
	}



	/**
     * How identical are the sequences. Here it is best, if the sequences share no similarity (then here a value close to 1 is returned).
     * If the sequences are completely identical, then the value 0.0 is returned. 
     * @param firstSeq sequence of the first primer
     * @param secondSeq typically the reverse complemented sequence of the second primer, such that the ability to ligate is calculated.
     * @return a value between 1.0 (best, no sequence similarity) and 0.0 (worst, identical sequences)
     */
    public double sequenceSimilarityScore(char[] firstSeq,char[] secondSeq) {
    	//remove 1 from the score since a one base hit is very likely.
    	double similarityScore = swa.getAlignmentScore(firstSeq, secondSeq)-1;
    	
    	// if more than 8 bases match, then we cannot use this pair.
    	if(similarityScore >= 7) {
    		return 0.0;
    	} else {
    		return Math.exp(-(similarityScore * similarityScore / maxAlignmentScore));
    	}
    }	

	}
	
