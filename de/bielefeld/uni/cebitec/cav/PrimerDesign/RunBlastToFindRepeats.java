package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class handels the execution of the external programms of BLAST 2.2.23.
 * 
 * @author yherrman
 *
 */

public class RunBlastToFindRepeats {
	File contigToBlast = null;
	File directoryForTempFiles = null;
	File blastOutput = null;
/**
 * This method starts to execute formatdb and blastall of BLAST 2.2.23 with the given files in the given directory.
 * 
 * @param tempFile
 * @param tempDir
 * @throws IOException
 * @throws InterruptedException
 */
	public RunBlastToFindRepeats(File tempFile, File tempDir) throws IOException, InterruptedException {
		contigToBlast = tempFile;
		directoryForTempFiles = tempDir;
		makeBlastDB();
		runBlastCommand();
	}
/**
 * This method executes the programm formatdb and makes a nucleotide database from the given
 * fasta file.
 * 
 * @throws IOException
 * @throws InterruptedException
 */
	public void makeBlastDB() throws IOException, InterruptedException{
		contigToBlast.setWritable(true);
		String command = new String("formatdb -i "+contigToBlast.getName()+" -p F");
		Process p = Runtime.getRuntime().exec(command,null,directoryForTempFiles);
		p.waitFor();
		Scanner s = new Scanner(p.getErrorStream()).useDelimiter( "\\Z" ); 
		 if(s.hasNext()){
			 System.out.println(s.next());
		 }
	}
	
	/**
	 * This method runs the blastall command with the made database and the given fasta file in order to
	 * get a output file with the information about the repeats within in the sequences.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	
	public void runBlastCommand() throws IOException, InterruptedException{
		blastOutput = File.createTempFile("blastout",".txt",directoryForTempFiles);
		String command = new String("blastall -p blastn -i "+contigToBlast.getName()+" -d "+contigToBlast.getName()+" -F F -m 8 -e 1e-04 -o " +blastOutput.getName());
		Process p = Runtime.getRuntime().exec(command,null,directoryForTempFiles);
		p.waitFor();
		Scanner s = new Scanner(p.getErrorStream()).useDelimiter( "\\Z" ); 
		 if(s.hasNext()){
			 System.out.println(s.next());
		 }
	}

	public File getBlastOutput() {
		return blastOutput;
	}

	public void setBlastOutput(File blastOutput) {
		this.blastOutput = blastOutput;
	}
}
