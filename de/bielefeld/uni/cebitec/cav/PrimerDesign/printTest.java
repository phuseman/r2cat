package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

public class printTest {
	
	public static void main(String args[]){
		//File configFile = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config.xml");
		File configFile = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config_original.xml");
		File fastaFile = new File("C:/Users/Yvisunshine/Uni/contigs.fas");
		Date start = new Date();
		boolean repeatMasking = false;
		
		repeatMasking = true;
		PrimerGenerator pg;
		pg = new PrimerGenerator(fastaFile,configFile,repeatMasking);
	
		//PrimerGenerator pg = new PrimerGenerator(fastaFile,repeatMasking);
		Vector<String[]> contigPairs = new Vector<String[]>();
		String[] pair1 = new String[4];
		pair1[0] = "1";
		pair1[1] = "forward";
		pair1[2] = "0";
		pair1[3] = "reverse";
		
		String[] pair2 = new String[4];
		pair2[0] = "2";
		pair2[1] = "forward";
		pair2[2] = "0";
		pair2[3] = "reverse";
		
		String[] pair3=new String[2];
		pair3[0] = "1";
		pair3[1] ="forward";
		
		contigPairs.add(pair1);
		contigPairs.add(pair2);
		contigPairs.add(pair3);
		
		
		pg.generatePrimers(contigPairs);
		System.out.println("Anzahl Sekunden: " + (System.currentTimeMillis() - start.getTime())/1000);	
	}

}

