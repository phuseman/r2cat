package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.Comparator;

public class PrimerScoreComparator implements Comparator<Primer>{

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