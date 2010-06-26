package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

public class PrimerPairs {
	private HashMap<Integer,Integer> pairsFirstLeftPrimer = new HashMap<Integer,Integer>();
	private HashMap<Integer,Integer> pairsFirstRightPrimer = new HashMap<Integer,Integer>();
	private ArrayList<Integer> notPairedPrimer = new ArrayList<Integer>();
	private ArrayList<Integer> noPartnerLeft = new ArrayList<Integer>();
	private ArrayList<Integer> noPartnerRight = new ArrayList<Integer>();

	
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
	
	public boolean tempCheck(double firstTemperature, double secondTemperature){
		double tempDifference =0;
		boolean tempCheck=false;
		tempDifference = Math.abs(firstTemperature-secondTemperature);
		if(tempDifference<5){
			tempCheck =true;
		} else{
			tempCheck=false;
		}
		return tempCheck;
	}
	
	public boolean seqCheck(char[] firstSeq,char[] secondSeq){
		boolean notComplement = false;
		String firstSeqLastBases = null;
		String secondSeqLastBases = null;
		String firstSeqEightBases = null;
		String secondSeqAsString  = null;
		char[] eightBases = new char[8];
		char[] leftLastBases = new char[4];
		char[] rightLastBases = new char[4];
		for(int k = 0;k<firstSeq.length-8;k++){
			System.arraycopy(firstSeq, k, eightBases, 0, 8);
			System.arraycopy(firstSeq, firstSeq.length-4, leftLastBases, 0, 3);
			System.arraycopy(secondSeq, secondSeq.length-4, rightLastBases, 0, 3);
			firstSeqLastBases = new String(leftLastBases);
			secondSeqLastBases = new String(rightLastBases);
			firstSeqEightBases = new String(eightBases);
			secondSeqAsString = new String(secondSeq);
			if(secondSeqAsString.contains(firstSeqEightBases)||firstSeqLastBases.equals(secondSeqLastBases)){
				notComplement = false;
			} else{
				notComplement = true;
			}
		}
		return notComplement;
	}
	
	public void pairPrimer(Vector<Primer> leftPrimer, Vector<Primer> rightPrimer){
		char[] leftPrimerSeq;
		char[] rightPrimerSeq;
		boolean seqCheck = false;
		boolean tempCheck =false;
		double leftPrimerTemperature=0;
		double rightPrimerTemperature= 0;
		int j = 0;
		
		for(int i = 0; i<leftPrimer.size();j++,i++){
			leftPrimerSeq =  leftPrimer.elementAt(i).getPrimerSeq();
			leftPrimerTemperature =  leftPrimer.elementAt(i).getTemperature();
			if(j<rightPrimer.size()){
				rightPrimerSeq = rightPrimer.elementAt(j).getPrimerSeq();
				rightPrimerTemperature =  rightPrimer.elementAt(j).getTemperature();
				tempCheck = this.tempCheck(leftPrimerTemperature, rightPrimerTemperature);
					if(tempCheck){
							seqCheck = this.seqCheck(leftPrimerSeq, rightPrimerSeq);
								if(seqCheck&&!pairsFirstLeftPrimer.containsKey(i)){
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
			if(a<leftPrimer.size()){
			leftPrimerSeq = leftPrimer.elementAt(a).getPrimerSeq();
			leftPrimerTemperature =leftPrimer.elementAt(a).getTemperature();
			for(int m = 0; m<rightPrimer.size();m++){
				rightPrimerSeq=rightPrimer.elementAt(m).getPrimerSeq();
				rightPrimerTemperature=rightPrimer.elementAt(m).getTemperature();
				tempCheck = this.tempCheck(leftPrimerTemperature, rightPrimerTemperature);
				if(tempCheck){
							seqCheck = this.seqCheck(leftPrimerSeq, rightPrimerSeq);
							if(seqCheck&&!pairsFirstLeftPrimer.containsKey(a)){
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
			if(b<rightPrimer.size()){
				countRight++;
			rightPrimerSeq=rightPrimer.elementAt(b).getPrimerSeq();
			rightPrimerTemperature=rightPrimer.elementAt(b).getTemperature();
			for(int m = 0; m<leftPrimer.size();m++){
				leftPrimerSeq=leftPrimer.elementAt(m).getPrimerSeq();
				leftPrimerTemperature=leftPrimer.elementAt(m).getTemperature();
				tempCheck = this.tempCheck(leftPrimerTemperature, rightPrimerTemperature);
				if(tempCheck){
					seqCheck = this.seqCheck(rightPrimerSeq, leftPrimerSeq);
					//b position im Rechten Primer Vektor und m position im linken Primer Vektor
							if(seqCheck&&!pairsFirstRightPrimer.containsKey(b)){
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
		
/*		System.out.println("countLeft: "+countLeft);
		System.out.println("countRight: "+countRight);
		System.out.println("kein partner links: "+noPartnerLeft);
		System.out.println("kein partner rechts: "+noPartnerRight);
		System.out.println("kein paar: "+notPairedPrimer.size());
		System.out.println("paar2: "+pairsFirstRightPrimer.size());
		System.out.println("paar: "+pairsFirstLeftPrimer.size());*/
	}
		
	}
	
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
