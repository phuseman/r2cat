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
		ContigPair pair = new ContigPair("000","001");
		ContigPair pair2 = new ContigPair("003","004");
		Vector<ContigPair> pairVe = new Vector<ContigPair>();
		pairVe.add(pair);
		pairVe.add(pair2);
		pg.setParameters(configFile);
		pg.generatePrimers(pairVe);
		long runningTime = new Date().getTime() - start; 
		System.out.println(runningTime);
	}

}

