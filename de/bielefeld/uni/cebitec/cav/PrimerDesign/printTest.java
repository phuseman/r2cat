package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.util.HashMap;

public class printTest {
	
	public static void main(String args[]) throws Exception{
		File xml = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config.xml");
		File fasta = new File("C:/Users/Yvisunshine/Uni/erstesContig.fas");
		/*char[] seq = new char[20];
		seq[0] = 'T';
		seq[1] ='A';
		seq[2] ='G';
		seq[3] ='A';
		seq[4] ='A';
		seq[5] ='C';
		seq[6] ='A';
		seq[7] ='A';
		seq[8] ='A';
		seq[9] ='G';
		seq[10] ='G';
		seq[11] ='G';
		seq[12] ='C';
		seq[13] ='G';
		seq[14] ='C';
		seq[15] ='G';
		seq[16] ='G';
		seq[17] ='G';
		seq[18] ='C';
		seq[19] ='A';*/
		String[] marked = new String[2];
		marked[0] ="r0";
		marked[1] ="0";
		HashMap<String,Integer> primerDirection = new HashMap<String, Integer>();
		int forward = 1;
		int notForward = -1;
		primerDirection.put(marked[0], forward);
		primerDirection.put(marked[1], notForward);
		PrimerGenerator t = new PrimerGenerator(fasta, xml,marked,primerDirection);
	}

}

