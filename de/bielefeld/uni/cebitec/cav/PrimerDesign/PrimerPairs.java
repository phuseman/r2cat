package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

public class PrimerPairs {
	private Vector<Primer> primer = null;
	private Vector<Primer> leftPrimer = null;
	private Vector<Primer> rightPrimer = null;
	HashMap<String, Integer> contigPrimerInfo = new HashMap<String,Integer>();
	PriorityQueue prioQueue = new PriorityQueue();


	public PrimerPairs(Vector<Primer> p1,Vector<Primer> p2,HashMap<String, Integer> contigPrimer){
		leftPrimer = p1;
		rightPrimer = p2;
		contigPrimerInfo = contigPrimer;
		for(int i = 0; i<leftPrimer.size();i++){
			System.out.println(leftPrimer.elementAt(i).getPrimerScore());
		}
		sortPrimer();
	}
	
	public void sortPrimer(){
		Comparator<Primer> comparator = new PrimerScoreComparator();
		PriorityQueue<Primer> queue = new PriorityQueue<Primer>(100,comparator);
		for(int j = 0;j<leftPrimer.size();j++){
			queue.add(leftPrimer.elementAt(j));
		}
		leftPrimer.clear();
		while(queue.size()!=0){
			leftPrimer.add(queue.remove());
		
		}
			for(int i = 0; i<leftPrimer.size();i++){
		System.out.println(leftPrimer.elementAt(i).getPrimerScore());
	}
	}
}
