package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class printTest {
	
	public static void main(String args[]) throws Exception{
		File xml = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config.xml");
		File fasta = new File("C:/Users/Yvisunshine/Uni/contigs.fas");
		
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
		HashMap<String,Integer> contigAndDirectionInfo = new HashMap<String, Integer>();
		int leftPrimer = 1;
		int rightPrimer = -1;
		contigAndDirectionInfo.put(marked[0],leftPrimer);
		contigAndDirectionInfo.put(marked[1],rightPrimer);
		PrimerGenerator t = new PrimerGenerator(fasta, xml,marked,contigAndDirectionInfo);
	}

}

