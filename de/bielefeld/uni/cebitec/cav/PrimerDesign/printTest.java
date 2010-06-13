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
		seq[2] ='A';
		seq[3] ='T';
		seq[4] ='A';
		seq[5] ='C';
		seq[6] ='G';
		seq[7] ='A';
		seq[8] ='C';
		seq[9] ='T';
		seq[10] ='C';
		seq[11] ='A';
		seq[12] ='C';
		seq[13] ='T';
		seq[14] ='A';
		seq[15] ='T';
		seq[16] ='A';
		seq[17] ='G';
		seq[18] ='G';
		seq[19] ='G';*/
		
		char[] seq = new char[21];
		seq[0] = 'T';
		seq[1] ='C';
		seq[2] ='C';
		seq[3] ='G';
		seq[4] ='A';
		seq[5] ='G';
		seq[6] ='C';
		seq[7] ='G';
		seq[8] ='G';
		seq[9] ='C';
		seq[10] ='C';
		seq[11] ='T';
		seq[12] ='A';
		seq[13] ='T';
		seq[14] ='C';
		seq[15] ='A';
		seq[16] ='A';
		seq[17] ='T';
		seq[18] ='C';
		seq[19] ='A';
		seq[20] ='T';
		String[] marked = new String[2];
		marked[0] ="r0";
		marked[1] ="0";
		HashMap<String,Integer> primerDirection = new HashMap<String, Integer>();
		int forward = 1;
		int notForward = -1;
		primerDirection.put(marked[0], forward);
		primerDirection.put(marked[1], notForward);
		PrimerGenerator t = new PrimerGenerator(fasta, xml,marked,primerDirection);
		//MeltingTemp test = new MeltingTemp(seq);
	}

}

