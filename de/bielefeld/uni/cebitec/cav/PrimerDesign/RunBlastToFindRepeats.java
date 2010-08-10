package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.IOException;

/**
 * This class handels the execution of the external programms of BLAST 2.2.23.
 * 
 * @author yherrman
 *
 */

public class RunBlastToFindRepeats {
	private File contigToBlast = null;
	private File directoryForTempFiles = null;
	private File blastOutput = null;
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
	}
	
/**
 * This method executes the programm formatdb and makes a nucleotide database from the given
 * fasta file.
 * TODO: formatdb creates the database files in the directory of the given file. find a way to do this in the temp dir.
 * @throws IOException
 * @throws InterruptedException
 */
	public void makeBlastDB() throws IOException, InterruptedException{
		String command = new String("formatdb -i "+contigToBlast.getAbsolutePath()+" -p F");
		System.out.println(command);
		Process p = Runtime.getRuntime().exec(command,null,directoryForTempFiles);
		p.waitFor();
	}
	
	/**
	 * This method runs the blastall command with the made database and the given fasta file in order to
	 * get a output file with the information about the repeats within in the sequences.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	
	public void runBlastCommand() throws IOException, InterruptedException{
		blastOutput = new File(directoryForTempFiles,"blastout.txt");
		String command = new String("blastall -p blastn -i "+contigToBlast.getAbsolutePath()+" -d "+contigToBlast.getAbsolutePath()+" -F F -m 8 -e 1e-04 -o " +blastOutput.getName());
		System.out.println(command);
		Process p = Runtime.getRuntime().exec(command,null,directoryForTempFiles);
		p.waitFor();
	}

	public File getBlastOutput() {
		return blastOutput;
	}

	public void setBlastOutput(File blastOutput) {
		this.blastOutput = blastOutput;
	}
}
