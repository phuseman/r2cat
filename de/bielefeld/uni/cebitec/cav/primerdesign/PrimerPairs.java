package de.bielefeld.uni.cebitec.cav.primerdesign;

import java.util.ArrayList;
import java.util.Comparator;
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
public class PrimerPairs {
	private Bases base;
	private SimpleSmithWatermanPrimerAligner swa;

	public PrimerPairs() {
		base = Bases.getInstance();
		swa = new SimpleSmithWatermanPrimerAligner();
	}


	/**
	 * This method is used to sort all primer candidates in a vector according to their scores and returns
	 * a vector of sorted primer objects from highest to lowest.
	 *  
	 * @param primer
	 * @return primer
	 */
	
	public Vector<Primer> sortPrimer(Vector<Primer> primer){
		Comparator<Primer> comparator = new PrimerScoreComparator();
		PriorityQueue<Primer> queue = new PriorityQueue<Primer>(100,comparator);
		for(int j = 0;j<primer.size();j++){
			queue.add(primer.elementAt(j));
		}
		primer.clear();
		while(queue.size()!=0){
			primer.add(queue.remove());
		
		}
			return primer;
	}
	
	/**
	 * This method checks if the temperature difference of the forward and reverse primer (possible pair).
	 * 
	 * It returns true when the difference between the two temperatures is beneath 5 degrees.
	 * 
	 * @param firstTemperature
	 * @param secondTemperature
	 * @return temperatureCheck
	 */
	public boolean temperatureCheck(double firstTemperature, double secondTemperature){
		double temperatureDifference =0;
		double temperatureDifferenceBorder = 5;
		boolean temperatureCheck=false;
		temperatureDifference = Math.abs(firstTemperature-secondTemperature);
		if(temperatureDifference<temperatureDifferenceBorder){
			temperatureCheck =true;
		} else{
			temperatureCheck=false;
		}
		return temperatureCheck;
	}	
	/**
	 * This method aligns the first sequence against the reverse complement to see if the sequences would ligate or not.
	 * 
	 *
	 * @param firstSeq
	 * @param secondSeq
	 * @return notComplementary
	 */
	
	public boolean seqencesDifferSufficiently(char[] firstSeq,char[] secondSeq){
		//investigate if both sequences ligate toghether (are partially complementary)
		char[] secondSeqReversed = base.getReverseComplement(secondSeq);
		
		if( firstSeq[0] == secondSeqReversed[0] 
		 && firstSeq[1] == secondSeqReversed[1] 
		 && firstSeq[2] == secondSeqReversed[2]) {
			//if the first three bases match, discard this pair.
			return false;
		}
		double normalizedScore = swa.getAlignmentScore(firstSeq, secondSeqReversed) / Math.min(firstSeq.length,secondSeqReversed.length);
		
		//if approximately less than 8 of 24 bases are matching this pair differs sufficiently
		if(normalizedScore<0.33) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Method checks if the bases of the sequences matches if so it returns 1 and if the don't match it returns -1/3.
	 * 
	 * @param baseOfFirstSeq
	 * @param baseOfSecondSeq
	 * @return score for match or a substitution
	 */
	public double checkBases(char baseOfFirstSeq, char baseOfSecondSeq){
		if(baseOfFirstSeq == baseOfSecondSeq){
			//for a match
			return 1;
		} else{
			//for a substitution
			return (-1/3);
		}
	}
	/**
	 * This method calculates the score considering the gap scoring scheme of the smith-waterman algorithm.
	 * @param gapLength
	 * @return gapScore
	 */
	public double gapScoring(int gapLength){
		double gapScore = 0;
		gapScore = 1+(-1/3)*gapLength;
		return gapScore;
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
		
		//andere Loesung fuers pairing...
		
		ArrayList<Integer> notPairedPrimer = new ArrayList<Integer>();
		boolean temperatureCheck =false;
		

		leftPrimer = this.sortPrimer(leftPrimer);
		rightPrimer = this.sortPrimer(rightPrimer);
		int j = 0;
		for(int i = 0; i<leftPrimer.size();j++,i++){
			char[] leftPrimerSeq =  leftPrimer.elementAt(i).getPrimerSeq();
			double leftPrimerTemperature =  leftPrimer.elementAt(i).getPrimerTemperature();
			if(j<rightPrimer.size()){
				char[] rightPrimerSeq = rightPrimer.elementAt(j).getPrimerSeq();
				double rightPrimerTemperature = rightPrimer.elementAt(j).getPrimerTemperature();
				temperatureCheck = this.temperatureCheck(leftPrimerTemperature, rightPrimerTemperature);
					if(temperatureCheck){
							
								if(seqencesDifferSufficiently(leftPrimerSeq, rightPrimerSeq)){
									primerResult.addPair(leftPrimer.elementAt(i), rightPrimer.elementAt(j));
								} else{
											notPairedPrimer.add(i);
									}
					} else{
							notPairedPrimer.add(i);
						}
			}else{
					notPairedPrimer.add(i);
				}
		}
		//andere loesung... wenn nicht kommentiert... haengt das Programm in dieser Schleife...
/*
		if(!notPairedPrimer.isEmpty()){
		for(Integer a : notPairedPrimer){
			if(a<leftPrimer.size()){
			char[] leftPrimerSeq = leftPrimer.elementAt(a).getPrimerSeq();
			double leftPrimerTemperature =leftPrimer.elementAt(a).getPrimerTemperature();
			for(int m = 0; m<rightPrimer.size();m++){
				char[] rightPrimerSeq=rightPrimer.elementAt(m).getPrimerSeq();
				double rightPrimerTemperature=rightPrimer.elementAt(m).getPrimerTemperature();
				temperatureCheck = this.temperatureCheck(leftPrimerTemperature, rightPrimerTemperature);
				if(temperatureCheck){
							sequenceCheck = this.seqCheck(leftPrimerSeq, rightPrimerSeq);
							if(sequenceCheck){
								primerResult.addPair(leftPrimer.elementAt(a), rightPrimer.elementAt(m));
							}
			}
			
		}
	}
		for(Integer b :notPairedPrimer){
			if(b<rightPrimer.size()){
			char[] rightPrimerSeq=rightPrimer.elementAt(b).getPrimerSeq();
			double rightPrimerTemperature=rightPrimer.elementAt(b).getPrimerTemperature();
			for(int m = 0; m<leftPrimer.size();m++){
				char[] leftPrimerSeq=leftPrimer.elementAt(m).getPrimerSeq();
				double leftPrimerTemperature=leftPrimer.elementAt(m).getPrimerTemperature();
				temperatureCheck = this.temperatureCheck(leftPrimerTemperature, rightPrimerTemperature);
				if(temperatureCheck){
					sequenceCheck = this.seqCheck(rightPrimerSeq, leftPrimerSeq);
					//b position im Rechten Primer Vektor und m position im linken Primer Vektor
							if(sequenceCheck){
								primerResult.addPair(leftPrimer.elementAt(m), rightPrimer.elementAt(b));
							}
				}
				}
			}
		}
		}
		
	}*/
		return primerResult;
	}	

	}
	
