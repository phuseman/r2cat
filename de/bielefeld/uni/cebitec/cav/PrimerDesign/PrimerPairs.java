package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

public class PrimerPairs {
	private Vector<Primer> leftPrimer = null;
	private Vector<Primer> rightPrimer = null;
	HashMap<String, Integer> contigPrimerInfo = new HashMap<String,Integer>();


	public PrimerPairs(Vector<Primer> leftP,Vector<Primer> rightP,HashMap<String, Integer> contigPrimer){

		contigPrimerInfo = contigPrimer;
		leftPrimer = sortPrimer(leftP);
		rightPrimer = sortPrimer(rightP);
		
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
}
