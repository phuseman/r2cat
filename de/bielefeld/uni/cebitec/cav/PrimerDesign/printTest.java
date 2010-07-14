package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

public class printTest {
	
	public static void main(String args[]) throws Exception{
		File xml = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config.xml");
		//File xml = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config_original.xml");
		File fasta = new File("C:/Users/Yvisunshine/Uni/contigs.fas");
		HashMap<String,Integer> contigAndDirectionInfo = new HashMap<String, Integer>();
		HashMap<String, String> pairSetUp = new HashMap<String, String>();
		Date start=new Date();
		String[] forwardPrimer = new String[2];
		forwardPrimer[0] = "1";
		forwardPrimer[1] = "2";
		
		String[] reversePrimer = new String[1];
		reversePrimer[0] ="0";
		
		pairSetUp.put(forwardPrimer[0],reversePrimer[0]);
		pairSetUp.put(forwardPrimer[1], reversePrimer[0]);
		
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

		System.out.println("Anzahl Sekunden: " + (System.currentTimeMillis() - start.getTime())/1000);
	}

}

