package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.Comparator;
/**
 * 
 * 
 * @author yherrman
 *
 */
public class PrimerScoreComparator implements Comparator<Primer>{
/**
 * Override compare method
 * scores of each primer object are compared and order by the highest to lowest score
 */
	@Override
	public int compare(Primer o1, Primer o2) {
		if(o1.getPrimerScore()<o2.getPrimerScore()){
			return 1;
	} if(o1.getPrimerScore()>o2.getPrimerScore()){
		return -1;
	}
	return 0;
	}

}