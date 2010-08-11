package de.bielefeld.uni.cebitec.cav.primerdesign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;

/**
 * This class can mask the repetitive regions in a given fasta file  object.
 * The object should contain  the contig sequences.
 * After the repeat masking, repetitive regions are in lowercase letters.
 * 
 * @author yherrman / phuseman
 * 
 */
public class BLASTRepeatMasker implements RepeatMasker {
	private FastaFileReader fastaFile = null;

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
		fastaFile.checkInitialisation();
		fastaFile.setAllToUppercaseLetters();

		BLASTExecutor runBlast = new BLASTExecutor(fastaFile.getSource());
		runBlast.createTempDir();
		runBlast.makeBlastDB();
		runBlast.runBlastCommand();
		this.markRepeatsFromBlastOutput(runBlast.getBlastOutput());
		runBlast.deleteTempDir();

		return this.fastaFile;
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

				// the -1 is because blast positions start at 1 while ours start
				// at zero.
				fastaFile.setRegionToLowercaseLetters(queryID, startQuery - 1,
						endQuery - 1);
				fastaFile.setRegionToLowercaseLetters(subjectID,
						startSubject - 1, endSubject - 1);

			}

		}
	}

}
