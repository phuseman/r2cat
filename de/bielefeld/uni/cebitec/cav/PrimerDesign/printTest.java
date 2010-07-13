package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.util.HashMap;

public class printTest {
	
	public static void main(String args[]) throws Exception{
		File xml = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config.xml");
		File fasta = new File("C:/Users/Yvisunshine/Uni/contigs.fas");
		HashMap<String,Integer> contigAndDirectionInfo = new HashMap<String, Integer>();
		String[] marked = new String[2];
		marked[0] ="1";
		marked[1] ="0";
		int leftPrimer = 1;
		int rightPrimer = -1;
		contigAndDirectionInfo.put(marked[0],leftPrimer);
		contigAndDirectionInfo.put(marked[1],rightPrimer);
		PrimerGenerator t = new PrimerGenerator(fasta, xml,marked,contigAndDirectionInfo);
	}

}

