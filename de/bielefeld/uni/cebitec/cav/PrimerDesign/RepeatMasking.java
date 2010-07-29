package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;

/**
 * This class is needed to mask the repeats of the given fasta file which include the selected
 * contig sequences. It sets up the fasta file and a working directory for the BLAST programms.
 * 
 * It includes getter and setter methods for needed objects in the PrimerGenerator class.
 * @author yherrman
 *
 */
public class RepeatMasking {
	private char[] seq;
	FastaFileReader ffr= null;
	FastaFileReader ffrForpreprocessed = null;
	Vector<DNASequence> sequences=null;
	RunBlastToFindRepeats runBlast =null;
	File tempDir = null;
	File blastOutput = null;
	String dirName = "tempDirectoryForBlast";
	String preProcessedFastaFile ="preProcessedFastaFile";
	String toBlast ="toBlast";
	
/**
 * Constructor to set up the working directory and the fasta file.
 * It also starts the RunBlast class in order to run the BLAST programms and parses the BLAST output
 * in order to mask the bases of the repeats to lower cases.
 * @param fasta
 * @throws IOException
 * @throws InterruptedException
 */
	public RepeatMasking(File fasta) throws IOException, InterruptedException {
		ffr = new FastaFileReader(fasta);
		sequences = ffr.getSequences();
		this.setSeqToCapLetters();
		tempDir = this.createTempDir();
		File tempFileToBlast=writeTempFile(toBlast,tempDir);
		runBlast = new RunBlastToFindRepeats(tempFileToBlast,tempDir);
		blastOutput = runBlast.getBlastOutput();
		this.blastOutputParsen();
		this.setUp(tempDir);
	}
	
	/**
	 * This method sets up a temporary directory where the files of the BLAST programms are put.
	 * 
	 * @return dir
	 * @throws IOException 
	 */
		public File createTempDir() throws IOException{
			File tempDirectory;
			tempDirectory = File.createTempFile(dirName, Long.toString(System.nanoTime()));
			tempDirectory.delete();
			tempDirectory.mkdir();
			return tempDirectory;
		}
	
	/**
	 * This method writes a temporary file with the sequences, which are set to capital letters.
	 * 
	 * @param fileName
	 * @param dir
	 * @return temp_file
	 * @throws IOException
	 */
	public File writeTempFile(String fileName, File dir) throws IOException{
		
		File temp_file = File.createTempFile(fileName, ".fas",dir);
		PrintWriter buffer = new PrintWriter(new FileWriter(temp_file));
		for (int i = 0;i<sequences.size();i++){
			String id = sequences.elementAt(i).getId();
			String description = sequences.elementAt(i).getDescription();
			int offset = (int) sequences.elementAt(i).getOffset();
			int size = (int) sequences.elementAt(i).getSize();
			buffer.print(">"+id+" "+description);
			buffer.write('\n');
			for(int j=offset;j<=size+offset-1;j++){
				buffer.write(seq[j]);
			}
			buffer.write('\n');
		}
		
		buffer=null;
		return temp_file;
	}
	
	/**
	 * This method parses the output of the blastall programm in order to mask the repeats to lower cases.
	 * 
	 * @throws IOException
	 */
	
	public void blastOutputParsen() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(blastOutput));
		String currentLine = null;
		String[] tab = null;
		while((currentLine = in.readLine()) != null){
				tab = currentLine.split("\t");
				String queryID=tab[0];
				String subjectID = tab[1];
				if(!queryID.equals(subjectID)){
					//int alignmentLength = Integer.valueOf(tab[3]).intValue();
					int startQuery = Integer.valueOf(tab[6]).intValue();
					int endQuery = Integer.valueOf(tab[7]).intValue();
					int startSubject = Integer.valueOf(tab[8]).intValue();
					int endSubject = Integer.valueOf(tab[9]).intValue();
				
					for(int i = 0; i<sequences.size();i++){
						String currentID = sequences.elementAt(i).getId();
						if(currentID.equals(queryID)){
							int offset = (int) sequences.elementAt(i).getOffset();
							int size = (int) sequences.elementAt(i).getSize();
						
								this.setRepeatsToLowerLetters(startQuery+offset, endQuery+offset);
					
						} if(currentID.equals(subjectID)){
							int offset = (int) sequences.elementAt(i).getOffset();
							int size = (int) sequences.elementAt(i).getSize();
								this.setRepeatsToLowerLetters(startSubject+offset, endSubject+offset);
						}
					}
					
				}
			}
		}
	

	/**
	 * This method sets the sequences of the fasta file to capital letters.
	 * 
	 * @throws IOException
	 */
	public void setSeqToCapLetters() throws IOException{
		seq = ffr.getCharArray();
		String temp = new String(seq);
		temp = temp.toUpperCase();
		seq = temp.toCharArray();	
	}
	
	/**
	 * This method gets the repeat startposition and its end position in order to mask the bases of the repeats
	 * to lower case letters.
	 * 
	 * @param repeatStartPos
	 * @param repeatEndPos
	 */
	public void setRepeatsToLowerLetters(int repeatStartPos,int repeatEndPos){
		if(repeatStartPos<repeatEndPos){
		for(int j = repeatStartPos-1;j<=repeatEndPos;j++){
			seq[j] = Character.toLowerCase(seq[j]);
		}
		} else{
			for(int j = repeatEndPos;j<repeatStartPos;j++){
				seq[j] = Character.toLowerCase(seq[j]);
			}
		}

	}
	/**
	 * This method writes a temporary file with the repeat masked sequences which now can be
	 * processed in the PrimerGenerator class.
	 * 
	 * @param dir
	 * @throws IOException
	 */
	public void setUp(File dir) throws IOException{
		File preProcessed = writeTempFile(preProcessedFastaFile,dir);
		ffrForpreprocessed = new FastaFileReader(preProcessed);
	}
	
	public FastaFileReader getFfrForpreprocessed() {
		return ffrForpreprocessed;
	}

	public void setFfrForpreprocessed(FastaFileReader ffrForpreprocessed) {
		this.ffrForpreprocessed = ffrForpreprocessed;
	}
	
	public FastaFileReader getFfr() {
		return ffr;
	}

	
	public void setFfr(FastaFileReader ffr) {
		this.ffr = ffr;
	}
	
	public File getDir() {
		return tempDir;
	}

	public void setDir(File dir) {
		this.tempDir = dir;
	}
	
	public char[] getSeq() {
		return seq;
	}


	public void setSeq(char[] seq) {
		this.seq = seq;
	}
}
