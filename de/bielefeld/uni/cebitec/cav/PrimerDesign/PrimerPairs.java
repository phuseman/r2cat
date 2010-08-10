package de.bielefeld.uni.cebitec.cav.PrimerDesign;

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
	private double[][] smithWatermanScoreMatrix = new double[30][30];
	private double currentMaxScore = 0;


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
	 * This method aligns the two sequences of the possible primer pair by using the smith-waterman algorithm.
	 *
	 * @param firstSeq
	 * @param secondSeq
	 * @return notComplement
	 */
	
	public boolean seqCheck(char[] firstSeq,char[] secondSeq){
		Bases base = new Bases();
		//auf true gesetzt, damit es noch results gibt...
		boolean notComplement = true;
		//score considering diagonal cell (H_i-1/j-1)
		double substiutionScore = 0;
		//score considering cell to the left (H_i/j-1)
		double deletionScore = 0;
		//score considering cell above (H_i-1/j)
		double insertionScore = 0;
		//absolut score for current cell
		double currentCellScore = 0;
		currentMaxScore = 0;
		
		secondSeq = base.getReverseComplement(secondSeq);

		//Frage: In den Konstruktor der Klasse?!
		//first row filled with zeros
		for(int k = 0; k<smithWatermanScoreMatrix.length;k++){
				smithWatermanScoreMatrix[0][k] = 0;
		}
		//first column filled with zeros
			for(int m = 0; m<smithWatermanScoreMatrix.length; m++){
				smithWatermanScoreMatrix[m][0] = 0;
		}
			//calculation of the scores for the remaining cells in the score matrix
			for(int i = 1; i<=firstSeq.length;i++){
				for(int j = 1; j<=secondSeq.length; j++){
					substiutionScore = smithWatermanScoreMatrix[i-1][j-1] + checkBases(firstSeq[i-1], secondSeq[j-1]);
					deletionScore = smithWatermanScoreMatrix[i][j-1]-gapScoring(1);
					insertionScore = smithWatermanScoreMatrix[i-1][j]-gapScoring(1);
					
					//maximum of the above scores is put into the considering cell
					 currentCellScore = Math.max(Math.max(substiutionScore, insertionScore), Math.max(deletionScore, 0));
					 smithWatermanScoreMatrix[i][j] = currentCellScore;
					 
					 //save highest score
					 if(currentMaxScore  < currentCellScore){
						 currentMaxScore  = currentCellScore;
					 }
					 //alignment of the first three bases should not get a high score
					if(smithWatermanScoreMatrix[3][3]>=3){
						notComplement = false;
						return notComplement;
					}
					
				}
			}
			//score übergeben?! <--> verhältnis zur länge berechnen
		return notComplement;
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
	
	public PrimerResult pairPrimer(Vector<Primer> primerCandidates, PrimerResult primerResult, String[] contigIDs){
		
		//andere Lösung fürs pairing...
		
		ArrayList<Integer> notPairedPrimer = new ArrayList<Integer>();
		Vector<Primer> leftPrimer = new Vector<Primer>();
		Vector<Primer> rightPrimer = new Vector<Primer>();
		boolean sequenceCheck = false;
		boolean temperatureCheck =false;

		for(int k = 0; k<primerCandidates.size();k++){
			if(primerCandidates.elementAt(k).getContigID().equals(contigIDs[0])){
				leftPrimer.add(primerCandidates.elementAt(k));
			} else{
				rightPrimer.add(primerCandidates.elementAt(k));
			}
		}
		int j = 0;
		for(int i = 0; i<leftPrimer.size();j++,i++){
			char[] leftPrimerSeq =  leftPrimer.elementAt(i).getPrimerSeq();
			double leftPrimerTemperature =  leftPrimer.elementAt(i).getTemperature();
			if(j<rightPrimer.size()){
				char[] rightPrimerSeq = rightPrimer.elementAt(j).getPrimerSeq();
				double rightPrimerTemperature = rightPrimer.elementAt(j).getTemperature();
				temperatureCheck = this.temperatureCheck(leftPrimerTemperature, rightPrimerTemperature);
					if(temperatureCheck){
							sequenceCheck = this.seqCheck(leftPrimerSeq, rightPrimerSeq);
								if(sequenceCheck){
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

		if(!notPairedPrimer.isEmpty()){
		for(Integer a : notPairedPrimer){
			if(a<leftPrimer.size()){
			char[] leftPrimerSeq = leftPrimer.elementAt(a).getPrimerSeq();
			double leftPrimerTemperature =leftPrimer.elementAt(a).getTemperature();
			for(int m = 0; m<rightPrimer.size();m++){
				char[] rightPrimerSeq=rightPrimer.elementAt(m).getPrimerSeq();
				double rightPrimerTemperature=rightPrimer.elementAt(m).getTemperature();
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
			double rightPrimerTemperature=rightPrimer.elementAt(b).getTemperature();
			for(int m = 0; m<leftPrimer.size();m++){
				char[] leftPrimerSeq=leftPrimer.elementAt(m).getPrimerSeq();
				double leftPrimerTemperature=leftPrimer.elementAt(m).getTemperature();
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
		
	}
		return primerResult;
	}	

	}
	
