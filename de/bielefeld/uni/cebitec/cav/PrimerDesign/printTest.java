package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class printTest {
	
	public static void main(String args[]) throws Exception{
		File xml = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config.xml");
		File fasta = new File("C:/Users/Yvisunshine/Uni/contigs.fas");
		HashMap<String,Integer> contigAndDirectionInfo = new HashMap<String, Integer>();
		HashMap<String, String> pairSetUp = new HashMap<String, String>();
		
		String[] forwardPrimer = new String[2];
		forwardPrimer[0] = "1";
		forwardPrimer[1] ="2";
		
		pairSetUp.put(forwardPrimer[0],"0");
		pairSetUp.put(forwardPrimer[1], "0");
		
		for(String key : forwardPrimer){
			String[] marked = new String[2];
			marked[0] = key;
			marked[1] = pairSetUp.get(key);
			int leftPrimer = 1;
			int rightPrimer = -1;
			contigAndDirectionInfo.put(marked[0],leftPrimer);
			contigAndDirectionInfo.put(marked[1],rightPrimer);
			PrimerGenerator t = new PrimerGenerator(fasta, xml,marked,contigAndDirectionInfo);
		}
	}

}

