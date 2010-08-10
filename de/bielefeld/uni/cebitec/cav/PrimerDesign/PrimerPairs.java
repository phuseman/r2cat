package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerGenerator.Bases;

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
	class Bases{
		private final static char A ='A',a='a',G ='G',g='g', C='C',c='c',T='T',t='t',N='N', n='n';
	}
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
	
/*	*//**
	 * This method is used to retrieve the complement of a given primer sequence.
	 * 
	 * @param primerSeq
	 * @return complement of primerSeq
	 *//*
	
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
	}*/
	
	/**
	 * This method retrieves the reverse complement of a given primer sequence.
	 * @param primerSeq
	 * @return
	 */
	public char[] getReverseComplement(char[] primerSeq){

		char[] alphabetMap= new char[256];
		char[] reverseComplement = new char[primerSeq.length];
		
		for (int j = 0; j < alphabetMap.length; j++) {
			alphabetMap[j]= (char) j;
		}
		alphabetMap[Bases.a]=Bases.t;
		alphabetMap[Bases.A]=Bases.T;
		alphabetMap[Bases.c]=Bases.g;
		alphabetMap[Bases.C]=Bases.G;
		alphabetMap[Bases.g]=Bases.c;
		alphabetMap[Bases.G]=Bases.C;
		alphabetMap[Bases.t]=Bases.a;
		alphabetMap[Bases.T]=Bases.A;
		
		int m = 0;
		for (int k = primerSeq.length-1; k>=0; k--,m++) {
			reverseComplement[m]= alphabetMap[primerSeq[k]];
			
		}
	
		return reverseComplement;
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
	 * nucleotides at the 3'end of forward primer and the 5'end of the reverse primer.
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
		String firstSeqFirstBases =null;
		String secondSeqFirstBases =null;
		char[] eightBases = new char[8];
		char[] leftLastBases = new char[4];
		char[] rightLastBases = new char[4];
		char[] leftFirstBases = new char[4];
		char[] rightFirstBases = new char[4];
		secondSeq = this.getReverseComplement(secondSeq);
		for(int k = 0;k<=firstSeq.length-8;k++){
			System.arraycopy(firstSeq, k, eightBases, 0, 7);
			//last four bases of the 3'end of the forward primer
			System.arraycopy(firstSeq, firstSeq.length-4, leftLastBases,0, 3);
			//first four bases of the 5'end of the forward primer
			System.arraycopy(firstSeq, 0, leftFirstBases, 0, 3);
			//last four bases of the 5'end of the reverse primer
			System.arraycopy(secondSeq, secondSeq.length-4, rightLastBases, 0, 3);
			//first four bases of the 3'end of the reverse primer
			System.arraycopy(secondSeq, 0, leftFirstBases, 0, 3);
			firstSeqLastBases = new String(leftLastBases);
			secondSeqLastBases = new String(rightLastBases);
			firstSeqFirstBases=new String(leftFirstBases);
			secondSeqFirstBases = new String(rightFirstBases);
			firstSeqEightBases = new String(eightBases);
			secondSeqAsString = new String(secondSeq);
			if(secondSeqAsString.contains(firstSeqEightBases)||firstSeqLastBases.equals(secondSeqLastBases)||firstSeqFirstBases.equals(secondSeqFirstBases)){
				notComplement = false;
				return notComplement;
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
	
	public PrimerResult pairPrimer(Vector<Primer> primerCandidates, PrimerResult primerResult, String[] contigIDs){
		HashMap<Integer,Integer> primerPairIndexLeftPrimerFirst = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> primerPairIndexRightPrimerFirst = new HashMap<Integer,Integer>();
		Vector<HashMap<Integer,Integer>> primerPairIndexMaps = new Vector<HashMap<Integer,Integer>>();
		Vector<Primer> leftPrimer = new Vector<Primer>();
		Vector<Primer> rightPrimer = new Vector<Primer>();
		char[] forwardPrimerSeq;
		char[] reversePrimerSeq;
		boolean sequenceCheck = false;
		boolean temperatureCheck =false;
		double leftPrimerTemperature=0;
		double rightPrimerTemperature= 0;
		int j = 0;
		for(int k = 0; k<primerCandidates.size();k++){
			if(primerCandidates.elementAt(k).getContigID().equals(contigIDs[0])){
				leftPrimer.add(primerCandidates.elementAt(k));
			} else{
				rightPrimer.add(primerCandidates.elementAt(k));
			}
		}
		for(int i = 0; i<leftPrimer.size();j++,i++){
			forwardPrimerSeq =  leftPrimer.elementAt(i).getPrimerSeq();
			leftPrimerTemperature =  leftPrimer.elementAt(i).getTemperature();
			if(j<rightPrimer.size()){
				reversePrimerSeq = rightPrimer.elementAt(j).getPrimerSeq();
				rightPrimerTemperature =  rightPrimer.elementAt(j).getTemperature();
				temperatureCheck = this.temperatureCheck(leftPrimerTemperature, rightPrimerTemperature);
					if(temperatureCheck){
							sequenceCheck = this.seqCheck(forwardPrimerSeq, reversePrimerSeq);
								if(sequenceCheck&&!primerPairIndexLeftPrimerFirst.containsKey(i)){
									primerResult.addPair(leftPrimer.elementAt(i), rightPrimer.elementAt(j));
									primerPairIndexLeftPrimerFirst.put(i, j);
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
			if(a<leftPrimer.size()){
			forwardPrimerSeq = leftPrimer.elementAt(a).getPrimerSeq();
			leftPrimerTemperature =leftPrimer.elementAt(a).getTemperature();
			for(int m = 0; m<rightPrimer.size();m++){
				reversePrimerSeq=rightPrimer.elementAt(m).getPrimerSeq();
				rightPrimerTemperature=rightPrimer.elementAt(m).getTemperature();
				temperatureCheck = this.temperatureCheck(leftPrimerTemperature, rightPrimerTemperature);
				if(temperatureCheck){
							sequenceCheck = this.seqCheck(forwardPrimerSeq, reversePrimerSeq);
							if(sequenceCheck&&!primerPairIndexLeftPrimerFirst.containsKey(a)){
								primerResult.addPair(leftPrimer.elementAt(a), rightPrimer.elementAt(m));
								primerPairIndexLeftPrimerFirst.put(a, m);
							} else{
								if(!noPartnerLeft.contains(a)&&!primerPairIndexLeftPrimerFirst.containsKey(a)){
									noPartnerLeft.add(a);
								}
							}
				}else{
					if(!noPartnerLeft.contains(a)&&!primerPairIndexLeftPrimerFirst.containsKey(a)){
						noPartnerLeft.add(a);
						}
				}
			}
			
		}
	}
		int countRight = 0;
		for(Integer b :notPairedPrimer){
			if(b<rightPrimer.size()){
				countRight++;
			reversePrimerSeq=rightPrimer.elementAt(b).getPrimerSeq();
			rightPrimerTemperature=rightPrimer.elementAt(b).getTemperature();
			for(int m = 0; m<leftPrimer.size();m++){
				forwardPrimerSeq=leftPrimer.elementAt(m).getPrimerSeq();
				leftPrimerTemperature=leftPrimer.elementAt(m).getTemperature();
				temperatureCheck = this.temperatureCheck(leftPrimerTemperature, rightPrimerTemperature);
				if(temperatureCheck){
					sequenceCheck = this.seqCheck(reversePrimerSeq, forwardPrimerSeq);
					//b position im Rechten Primer Vektor und m position im linken Primer Vektor
							if(sequenceCheck&&!primerPairIndexRightPrimerFirst.containsKey(b)){
								primerResult.addPair(leftPrimer.elementAt(m), rightPrimer.elementAt(b));
								primerPairIndexRightPrimerFirst.put(b, m);
							} else{
								if(!noPartnerRight.contains(b)&&!primerPairIndexRightPrimerFirst.containsKey(b)){
									noPartnerRight.add(b);
								}
							}
				}else{
				if(!noPartnerRight.contains(b)&&!primerPairIndexRightPrimerFirst.containsKey(b)){
					noPartnerRight.add(b);
				}
				}
			}
		}
		}
		
		this.noPartnerCheck(primerPairIndexLeftPrimerFirst,primerPairIndexRightPrimerFirst);
	}
		return primerResult;
		
	}
	
	/**
	 * This method checks if all primer candidates (forward and reverse) got a fitting partner primer. 
	 */
	public void noPartnerCheck(HashMap<Integer,Integer> pairsFirstLeftPrimer,HashMap<Integer,Integer> pairsFirstRightPrimer){
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
}
