package de.bielefeld.uni.cebitec.cav.primerdesign;

import java.util.Comparator;

/**
 * This class is for sorting the primer candidates (primer objects) according to its scores.
 * 
 * @author yherrman
 */

public class PrimerScoreComparator implements Comparator<Primer>{

/**
 * Override compare method
 * scores of each primer object are compared and order by the highest to lowest score
 */
	@Override
	public int compare(Primer primer1, Primer primer2) {
		if(primer1.getPrimerScore()<primer2.getPrimerScore()){
			return 1;
	} if(primer1.getPrimerScore()>primer2.getPrimerScore()){
		return -1;
	}
	return 0;
	}

}