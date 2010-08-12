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
		
		public PrimerPair(Primer left, Primer right, double totalscore) {
			this.left = left;
			this.right=right;
			this.score=totalscore;
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
     * @param temp2 temperature of the secont primer
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
	 * @return boolean if these sequence are suided as a primer pair or not.
	 */
	
	public boolean sequenceSimilarityQuickCheck(char[] firstSeq,char[] secondSeqReversed){
		if( firstSeq[0] == secondSeqReversed[0] 
		 && firstSeq[1] == secondSeqReversed[1] 
		 && firstSeq[2] == secondSeqReversed[2]) {
			//if the first three bases match, discard this pair.
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
    
    

	/**
	 * This method goes through the given vector of the possible forward and reverse primers in order
	 * to check if the primers are a fitting pair. (Has to check temperature difference and the property 
	 * of complementary.
	 * If one of the properties is violated those primers are saved (notPairedPrimer)
	 * and then paired up with a fitting primer-candidate.
	 * The pair information is saved in HashMaps with position in the forward primer vector as the key and
	 * the position in the reverse primer vector as the value.
	 * 
	 * @param forwardPrimer
	 * @param reversePrimer
	 */
	
	public PrimerResult pairPrimer(Vector<Primer> leftPrimer,Vector<Primer> rightPrimer, PrimerResult primerResult){
		Collections.sort(leftPrimer);
		Collections.sort(rightPrimer);
		
		boolean[] leftUsed = new boolean[leftPrimer.size()];
		boolean[] rightUsed = new boolean[rightPrimer.size()];
		PriorityQueue<PrimerPair> bestPairs = new PriorityQueue<PrimerPair>();
		
		int topCandidatesToConsider = 20;
		if(topCandidatesToConsider>rightPrimer.size()) {
			topCandidatesToConsider=rightPrimer.size();
		}
		if(topCandidatesToConsider>leftPrimer.size()) {
			topCandidatesToConsider=leftPrimer.size();
		}
		
		Primer left = null;
		Primer right = null;
		double temperatureScore = 0;
		double sequenceSimilarityScore = 0;
		double totalscore = 0;
		char[] reverseComplementOfSecondPrimer=null;

		
		nextleft : for (int i = 0; i < topCandidatesToConsider; i++) {
			for (int j = 0; j < topCandidatesToConsider; j++) {
				temperatureScore = 0;
				sequenceSimilarityScore = 0;
				left=leftPrimer.get(i);
				right = rightPrimer.get(j);
				if(temperatureDifferenceQuickCheck(right.getPrimerTemperature(), left.getPrimerTemperature())) {
					temperatureScore = temperatureDifferenceScore(right.getPrimerTemperature(), left.getPrimerTemperature());
					reverseComplementOfSecondPrimer = base.getReverseComplement(right.getPrimerSeq());
					if(sequenceSimilarityQuickCheck(left.getPrimerSeq(), reverseComplementOfSecondPrimer)) {
						sequenceSimilarityScore = sequenceSimilarityScore(left.getPrimerSeq(), reverseComplementOfSecondPrimer);
						
						totalscore = temperatureScore * sequenceSimilarityScore * (left.getPrimerScore() + right.getPrimerScore());
						bestPairs.add(new PrimerPair(left,right,totalscore));
						leftUsed[i]=true;
						rightUsed[j]=true;
						
//						System.out.println("===========");
//						System.out.println("Tr:"+ right.getPrimerTemperature()+"\nTl:"+ left.getPrimerTemperature()+"\nTscore="+temperatureScore);
//						System.out.println("l  : "+new String(left.getPrimerSeq()));
//						System.out.println("rcr: "+new String(reverseComplementOfSecondPrimer));
//						System.out.println("similarity score"+sequenceSimilarityScore);
//						System.out.println("Gesamt: "+ (temperatureScore * sequenceSimilarityScore * (left.getPrimerScore() + right.getPrimerScore())));
//						System.out.println("===========");
						continue nextleft;
					}
				}
			}
		}

		
		

		// the same with the other side
		
		nextright : for (int i = 0; i < topCandidatesToConsider; i++) {
			if(rightUsed[i]) {
				continue;
			}
			for (int j = 0; j < topCandidatesToConsider; j++) {
				if (leftUsed[j]){
					continue;
				}
				temperatureScore = 0;
				sequenceSimilarityScore = 0;
				right = rightPrimer.get(i);
				left=leftPrimer.get(j);
				if(temperatureDifferenceQuickCheck(right.getPrimerTemperature(), left.getPrimerTemperature())) {
					temperatureScore = temperatureDifferenceScore(right.getPrimerTemperature(), left.getPrimerTemperature());
					reverseComplementOfSecondPrimer = base.getReverseComplement(right.getPrimerSeq());
					if(sequenceSimilarityQuickCheck(left.getPrimerSeq(), reverseComplementOfSecondPrimer)) {
						sequenceSimilarityScore = sequenceSimilarityScore(left.getPrimerSeq(), reverseComplementOfSecondPrimer);
						
						totalscore = temperatureScore * sequenceSimilarityScore * (left.getPrimerScore() + right.getPrimerScore());
						bestPairs.add(new PrimerPair(left,right,totalscore));
						rightUsed[i]=true;
						leftUsed[j]=true;
						continue nextright;
					}
				}
			}
		}

		
		PrimerPair pair = null;
		while (!bestPairs.isEmpty()) {
			pair = bestPairs.poll();
			primerResult.addPair(pair.left, pair.right);
		}
		
		return primerResult;
	}	

	}
	
