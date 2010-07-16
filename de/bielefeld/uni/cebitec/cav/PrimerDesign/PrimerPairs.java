package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
	
	private HashMap<Integer,Integer> pairsFirstLeftPrimer = new HashMap<Integer,Integer>();
	private HashMap<Integer,Integer> pairsFirstRightPrimer = new HashMap<Integer,Integer>();
	private ArrayList<Integer> notPairedPrimer = new ArrayList<Integer>();
	private ArrayList<Integer> noPartnerLeft = new ArrayList<Integer>();
	private ArrayList<Integer> noPartnerRight = new ArrayList<Integer>();

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
	 * This method is used to retrieve the complement of a given primer sequence.
	 * 
	 * @param primerSeq
	 * @return complement of primerSeq
	 */
	
	public char[] getComplement(char[] primerSeq){

		char[] alphabetMap= new char[256];
		char[] complement = new char[primerSeq.length];
		
		for (int j = 0; j < alphabetMap.length; j++) {
			alphabetMap[j]= (char) j;
		}
		alphabetMap['a']='t';
		alphabetMap['A']='T';
		alphabetMap['c']='g';
		alphabetMap['C']='G';
		alphabetMap['g']='c';
		alphabetMap['G']='C';
		alphabetMap['t']='a';
		alphabetMap['T']='A';
		
		for (int j = 0; j<primerSeq.length; j++) {
			complement[j]= alphabetMap[primerSeq[j]];
		}
	
		return complement;
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
	 * This method checks if the sequences of the forward and reverse primer (possible pair)
	 * include a complementary sequence of eight nucleotides somewhere in the primer sequences or four
	 * nucleotides at the beginning of the primer sequences.
	 * 
	 * It returns true if the sequence are not complementary.
	 *
	 * @param firstSeq
	 * @param secondSeq
	 * @return notComplement
	 */
	
	public boolean seqCheck(char[] firstSeq,char[] secondSeq){
		boolean notComplement = false;
		String firstSeqLastBases = null;
		String secondSeqLastBases = null;
		String firstSeqEightBases = null;
		String secondSeqAsString  = null;
		char[] eightBases = new char[8];
		char[] leftLastBases = new char[4];
		char[] rightLastBases = new char[4];
		secondSeq = this.getComplement(secondSeq);
		for(int k = 0;k<firstSeq.length-8;k++){
			System.arraycopy(firstSeq, k, eightBases, 0, 8);
			System.arraycopy(firstSeq, 0, leftLastBases, 0, 3);
			System.arraycopy(secondSeq, 0, rightLastBases, 0, 3);
			firstSeqLastBases = new String(leftLastBases);
			secondSeqLastBases = new String(rightLastBases);
			firstSeqEightBases = new String(eightBases);
			secondSeqAsString = new String(secondSeq);
			if(secondSeqAsString.contains(firstSeqEightBases)||firstSeqLastBases.contains(secondSeqLastBases)){
				notComplement = false;
			} else{
				notComplement = true;
			}
		}
		return notComplement;
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
	
	public void pairPrimer(Vector<Primer> forwardPrimer, Vector<Primer> reversePrimer){
		char[] forwardPrimerSeq;
		char[] reversePrimerSeq;
		boolean sequenceCheck = false;
		boolean temperatureCheck =false;
		double leftPrimerTemperature=0;
		double rightPrimerTemperature= 0;
		int j = 0;
		for(int i = 0; i<forwardPrimer.size();j++,i++){
			forwardPrimerSeq =  forwardPrimer.elementAt(i).getPrimerSeq();
			leftPrimerTemperature =  forwardPrimer.elementAt(i).getTemperature();
			if(j<reversePrimer.size()){
				reversePrimerSeq = reversePrimer.elementAt(j).getPrimerSeq();
				rightPrimerTemperature =  reversePrimer.elementAt(j).getTemperature();
				temperatureCheck = this.temperatureCheck(leftPrimerTemperature, rightPrimerTemperature);
					if(temperatureCheck){
							sequenceCheck = this.seqCheck(forwardPrimerSeq, reversePrimerSeq);
								if(sequenceCheck&&!pairsFirstLeftPrimer.containsKey(i)){
											pairsFirstLeftPrimer.put(i, j);
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
		int countLeft= 0;
		for(Integer a : notPairedPrimer){
			countLeft++;
			if(a<forwardPrimer.size()){
			forwardPrimerSeq = forwardPrimer.elementAt(a).getPrimerSeq();
			leftPrimerTemperature =forwardPrimer.elementAt(a).getTemperature();
			for(int m = 0; m<reversePrimer.size();m++){
				reversePrimerSeq=reversePrimer.elementAt(m).getPrimerSeq();
				rightPrimerTemperature=reversePrimer.elementAt(m).getTemperature();
				temperatureCheck = this.temperatureCheck(leftPrimerTemperature, rightPrimerTemperature);
				if(temperatureCheck){
							sequenceCheck = this.seqCheck(forwardPrimerSeq, reversePrimerSeq);
							if(sequenceCheck&&!pairsFirstLeftPrimer.containsKey(a)){
								pairsFirstLeftPrimer.put(a, m);
							} else{
								if(!noPartnerLeft.contains(a)&&!pairsFirstLeftPrimer.containsKey(a)){
									noPartnerLeft.add(a);
								}
							}
				}else{
					if(!noPartnerLeft.contains(a)&&!pairsFirstLeftPrimer.containsKey(a)){
						noPartnerLeft.add(a);
						}
				}
			}
			
		}
	}
		int countRight = 0;
		for(Integer b :notPairedPrimer){
			if(b<reversePrimer.size()){
				countRight++;
			reversePrimerSeq=reversePrimer.elementAt(b).getPrimerSeq();
			rightPrimerTemperature=reversePrimer.elementAt(b).getTemperature();
			for(int m = 0; m<forwardPrimer.size();m++){
				forwardPrimerSeq=forwardPrimer.elementAt(m).getPrimerSeq();
				leftPrimerTemperature=forwardPrimer.elementAt(m).getTemperature();
				temperatureCheck = this.temperatureCheck(leftPrimerTemperature, rightPrimerTemperature);
				if(temperatureCheck){
					sequenceCheck = this.seqCheck(reversePrimerSeq, forwardPrimerSeq);
					//b position im Rechten Primer Vektor und m position im linken Primer Vektor
							if(sequenceCheck&&!pairsFirstRightPrimer.containsKey(b)){
								pairsFirstRightPrimer.put(b, m);
							} else{
								if(!noPartnerRight.contains(b)&&!pairsFirstRightPrimer.containsKey(b)){
									noPartnerRight.add(b);
								}
							}
				}else{
				if(!noPartnerRight.contains(b)&&!pairsFirstRightPrimer.containsKey(b)){
					noPartnerRight.add(b);
				}
				}
			}
		}
		}
		
		this.noPartnerCheck();
	}
		
	}
	
	/**
	 * This method checks if all primer candidates (forward and reverse) got a fitting partner primer. 
	 */
	public void noPartnerCheck(){
		int countR = 0;
		int countL = 0; 
		for(int n = 0; n<noPartnerRight.size();n++){
			if(pairsFirstRightPrimer.containsKey(noPartnerRight.get(n))){
				countR++;
			}
		}
		if(countR==noPartnerRight.size()){
			noPartnerRight.clear();
		}
		for(int n = 0; n<noPartnerLeft.size();n++){	
			if(pairsFirstLeftPrimer.containsKey(noPartnerLeft.get(n))){
				countL++;
			}
		}
		if(countL==noPartnerLeft.size()){
			noPartnerLeft.clear();
		}
	}
	
	public HashMap<Integer, Integer> getPairsFirstLeftPrimer() {
		return pairsFirstLeftPrimer;
	}

	public void setPairsFirstLeftPrimer(
			HashMap<Integer, Integer> pairsFirstLeftPrimer) {
		this.pairsFirstLeftPrimer = pairsFirstLeftPrimer;
	}

	public HashMap<Integer, Integer> getPairsFirstRightPrimer() {
		return pairsFirstRightPrimer;
	}

	public void setPairsFirstRightPrimer(
			HashMap<Integer, Integer> pairsFirstRightPrimer) {
		this.pairsFirstRightPrimer = pairsFirstRightPrimer;
	}

	public ArrayList<Integer> getNoPartnerLeft() {
		return noPartnerLeft;
	}

	public void setNoPartnerLeft(ArrayList<Integer> noPartnerLeft) {
		this.noPartnerLeft = noPartnerLeft;
	}

	public ArrayList<Integer> getNoPartnerRight() {
		return noPartnerRight;
	}

	public void setNoPartnerRight(ArrayList<Integer> noPartnerRight) {
		this.noPartnerRight = noPartnerRight;
	}
}
