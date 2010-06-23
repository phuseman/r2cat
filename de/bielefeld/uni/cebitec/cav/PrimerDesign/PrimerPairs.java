package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

public class PrimerPairs {
	private Vector<Primer> leftPrimer = null;
	private Vector<Primer> rightPrimer = null;
	//private ArrayList<Integer> pairs = new ArrayList<Integer>();
	private HashMap<Integer,Integer> pairs = new HashMap<Integer,Integer>();
	//private int[] pairs;
	private ArrayList<Integer> notPairedRightPrimer = new ArrayList<Integer>();
	private ArrayList<Integer> notPairedLeftPrimer = new ArrayList<Integer>();
	HashMap<String, Integer> contigPrimerInfo = new HashMap<String,Integer>();


	public PrimerPairs(Vector<Primer> leftP,Vector<Primer> rightP,HashMap<String, Integer> contigPrimer){

		contigPrimerInfo = contigPrimer;
		leftPrimer = sortPrimer(leftP);
		rightPrimer = sortPrimer(rightP);
		//pairs = new int[leftPrimer.size()];		
		pairPrimer();
		
	/*	for(int i = 0; i<leftPrimer.size();i++){
			System.out.println(leftPrimer.elementAt(i).getPrimerScore());
		}*/
/*		for(int i = 0; i<rightPrimer.size();i++){
			System.out.println(rightPrimer.elementAt(i).getPrimerScore());
		}*/
	}
	
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
	
	public void pairPrimer(){
		char[] leftPrimerSeq;
		char[] rightPrimerSeq;
		boolean seqCheck = false;
		double leftPrimerTemperature=0;
		double rightPrimerTemperature= 0;
		String leftPrimerLastBases = null;
		String rightPrimerLastBases = null;
		String leftPrimerEightBases = null;
		String rightPrimerAsString  = null;
		char[] eightBases = new char[8];
		char[] leftLastBases = new char[4];
		char[] rightLastBases = new char[4];
		double tempDifference = 0;
		int j = 0;
		
		for(int i = 0; i<leftPrimer.size();j++,i++){
			leftPrimerSeq =  leftPrimer.elementAt(i).getPrimerSeq();
			leftPrimerTemperature =  leftPrimer.elementAt(i).getTemperature();
		if(j<rightPrimer.size()){
			rightPrimerSeq = rightPrimer.elementAt(j).getPrimerSeq();
			rightPrimerTemperature =  rightPrimer.elementAt(j).getTemperature();
			tempDifference = Math.abs(leftPrimerTemperature-rightPrimerTemperature);
			//System.out.println(tempDifference);
			if(tempDifference<5){
				rightPrimerAsString = new String(rightPrimerSeq);
				//System.out.println(rightPrimerAsString);
				for(int k = 0;k<leftPrimerSeq.length-8;k++){
					System.arraycopy(leftPrimerSeq, k, eightBases, 0, 8);
					System.arraycopy(leftPrimerSeq, leftPrimerSeq.length-4, leftLastBases, 0, 3);
					System.arraycopy(rightPrimerSeq, rightPrimerSeq.length-4, rightLastBases, 0, 3);
					leftPrimerLastBases = new String(leftLastBases);
					rightPrimerLastBases = new String(rightLastBases);
					leftPrimerEightBases = new String(eightBases);
					//System.out.println(leftPrimerEightBases);
					if(rightPrimerAsString.contains(leftPrimerEightBases)&&leftPrimerLastBases.contains(rightPrimerLastBases)){
						seqCheck =false;
					} else{
						seqCheck = true;
					}
						if(seqCheck&&!pairs.containsKey(i)){
							pairs.put(i, j);
						} if(!seqCheck&&!notPairedRightPrimer.contains(j)){
							notPairedRightPrimer.add(j);
						}
					}
				} else{
					notPairedRightPrimer.add(j);
				}
			} else{
				notPairedLeftPrimer.add(i);
			}
		
		}
		System.out.println("kein paar(rechts): "+notPairedRightPrimer.size());
		System.out.println("kein paar(links): "+notPairedLeftPrimer.size());
		System.out.println("paar "+pairs.size());
	}

}
