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
		for(int i = 0;i<leftPrimer.size();i++){
		
/*		Collections.sort(leftPrimer,new Comparator(){
			public int compare(Object o1, Object o2){
				Primer p1 = (Primer) o1;
				Primer p2 = (Primer) o2;
				return p1.getPrimerScore().compareTo(getPrimerScore());
				}
		} );*/
		}
	}
}
