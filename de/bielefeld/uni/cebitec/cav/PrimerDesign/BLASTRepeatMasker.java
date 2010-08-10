package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;

/**
 * This class is needed to mask the repeats of the given fasta file which
 * include the selected contig sequences. It sets up the fasta file and a
 * working directory for the BLAST programms.
 * 
 * It includes getter and setter methods for needed objects in the
 * PrimerGenerator class.
 * 
 * @author yherrman
 * 
 */
public class BLASTRepeatMasker implements RepeatMasker {
	private FastaFileReader fastaFile = null;
	private File tempDir = null;

	/**
	 * Constructor to set up the working directory and the fasta file. It also
	 * starts the RunBlast class in order to run the BLAST programms and parses
	 * the BLAST output in order to mask the bases of the repeats to lower
	 * cases.
	 * 
	 * @param fasta
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public BLASTRepeatMasker(FastaFileReader fasta) {
		fastaFile = fasta;
	}

	@Override
	public FastaFileReader doRepeatMasking() throws IOException,
			InterruptedException {
		try {
			fastaFile.checkInitialisation();
			fastaFile.setAllToUppercaseLetters();
			tempDir = this.createTempDir();
			BLASTExecutor runBlast = new BLASTExecutor(
					fastaFile.getSource(), tempDir);
			runBlast.makeBlastDB();
			runBlast.runBlastCommand();
			this.markRepeatsFromBlastOutput(runBlast.getBlastOutput());
		} finally {
			this.deleteTempDir();
		}

		return this.fastaFile;
	}

	/**
	 * This method is called to delete the temporary directory with its
	 * temporary files.
	 * 
	 * @param dir
	 */
	private void deleteTempDir() {
		if (tempDir.getName().contains("tempDirectoryForBlast")
				&& tempDir.exists()) {
			File[] files = tempDir.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
			}
			tempDir.delete();
		}
	}

	/**
	 * This method sets up a temporary directory where the files of the BLAST
	 * programms are put.
	 * 
	 * @return dir
	 * @throws IOException
	 */
	public File createTempDir() throws IOException {
		File tempDirectory;
		tempDirectory = File.createTempFile("r2cat_temp_BLAST_dir", Long.toString(System
				.nanoTime()));
		tempDirectory.delete();
		tempDirectory.mkdir();
		return tempDirectory;
	}

	/**
	 * This method parses the output of the blastall programm in order to mask
	 * the repeats to lower cases.
	 * 
	 * @throws IOException
	 */

	public void markRepeatsFromBlastOutput(File BLASTOutput) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(BLASTOutput));
		String currentLine = null;
		String[] tab = null;
		while ((currentLine = in.readLine()) != null) {
			tab = currentLine.split("\t");
			String queryID = tab[0];
			String subjectID = tab[1];
			if (!queryID.equals(subjectID)) {
				// int alignmentLength = Integer.valueOf(tab[3]).intValue();
				int startQuery = Integer.valueOf(tab[6]).intValue();
				int endQuery = Integer.valueOf(tab[7]).intValue();
				int startSubject = Integer.valueOf(tab[8]).intValue();
				int endSubject = Integer.valueOf(tab[9]).intValue();

				//the -1 is because blast positions start at 1 while ours start at zero.
				fastaFile.setRegionToLowercaseLetters(queryID, startQuery-1,
						endQuery-1);
				fastaFile.setRegionToLowercaseLetters(subjectID, startSubject-1,
						endSubject-1);

			}

		}
	}

}
