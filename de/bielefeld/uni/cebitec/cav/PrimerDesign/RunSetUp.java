package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

public class RunSetUp {
	
	public static void main(String args[]){
		//Path to the config file, which contains the parameters.
		File configFile = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config_original.xml");
		//File configFile = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config.xml");
		
		//Path to fasta file containing a list of contigs
		File fastaFile = new File("C:/Users/Yvisunshine/Uni/contigs.fas");
		
		//location of output files
		File outputDir = new File(System.getProperty("user.home"));
		
		//marks if the user wants to do preprocessing step for the masking of repeats.
		//If so, the user has to have BLAST 2.2.23 installed and included in the path-variable
		//Note: While doing this step a temporary directory for the BLAST output files is generated.
		boolean repeatMasking = false;
		
		repeatMasking = true;

		//Creating a new instance of the PrimerGenerator class.
		PrimerGenerator pg;
		pg = new PrimerGenerator(fastaFile,configFile,repeatMasking,outputDir);
	
		//Creating a new instance of the PrimerGenerator with using a different constructor. In this case default
		//parameters are used to calculate the primer scores.
		//PrimerGenerator pg = new PrimerGenerator(fastaFile,repeatMasking,outputDir);
		
		//This vector contains the IDs of the marked sequence for the primer design. For each sequence, the direction of the primer is given.
		//Also only on contig can be selected to generate only possible primers for one of the contig ends.
		Vector<String[]> contigPairs = new Vector<String[]>();
		
		//This String[] are set up to let the programm run. And give 3 output files.
		String[] pair1 = new String[6];
		pair1[0] = "0";
		pair1[1] = "false";
		pair1[2] = "forward";
		pair1[3] = "1";
		pair1[4] = "false";
		pair1[5] = "reverse";
		
		String[] pair2 = new String[6];
		pair2[0] = "3";
		pair2[1] = "false";
		pair2[2] = "forward";
		pair2[3] = "4";
		pair2[4] = "false";
		pair2[5] = "reverse";
		
		String[] pair3=new String[3];
		pair3[0] = "1";
		pair3[1] = "false";
		pair3[2] ="forward";
		
		contigPairs.add(pair1);
		contigPairs.add(pair2);
		contigPairs.add(pair3);
		
		
		pg.generatePrimers(contigPairs);
	}

}

