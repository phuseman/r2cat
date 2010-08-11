package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.util.Date;
import java.util.Vector;

public class RunSetUp {
	
	public static void main(String args[]) throws Exception{
		//Path to the config file, which contains the parameters.
		File configFile = new File("C:/Users/Mini-Yvi/Uni/primer_search_default_config_original.xml");
		//File configFile = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config.xml");
		
		//Path to fasta file containing a list of contigs
		File fastaFile = new File("C:/Users/Mini-Yvi/Uni/Corynebacterium_urealyticum_DSM_7109_454LargeContigs_renumbered_repeatmarked.fas");
		long start = new Date().getTime();
		
		boolean repeatMasking = false;
		repeatMasking = true;

		PrimerGenerator pg = new PrimerGenerator(fastaFile);
		
		//This vector contains the IDs of the marked sequence for the primer design. For each sequence, the direction of the primer is given.
		//Also only on contig can be selected to generate only possible primers for one of the contig ends.
		Vector<String[]> contigPairs = new Vector<String[]>();
		
		//This String[] are set up to let the programm run. And give 3 output files.
		String[] pair1 = new String[6];
		pair1[0] = "000";
		pair1[1] = "false";
		pair1[2] = "forward";
		pair1[3] = "001";
		pair1[4] = "false";
		pair1[5] = "reverse";
		
		String[] pair2 = new String[6];
		pair2[0] = "003";
		pair2[1] = "false";
		pair2[2] = "forward";
		pair2[3] = "004";
		pair2[4] = "false";
		pair2[5] = "reverse";
		
		contigPairs.add(pair1);
		//contigPairs.add(pair2);
		
		pg.setParameters(configFile);
		pg.generatePrimers(contigPairs);
		long runningTime = new Date().getTime() - start; 
		System.out.println(runningTime);
	}

}

