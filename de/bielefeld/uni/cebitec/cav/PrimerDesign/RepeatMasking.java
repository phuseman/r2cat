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


public class RepeatMasking {
	private char[] seq;
	FastaFileReader ffr= null;
	FastaFileReader ffrForpreprocessed = null;
	Vector<DNASequence> sequences=null;
	RunBlastToFindRepeats ctb =null;
	File dir = null;
	File blastOutput = null;
	String dirName = "C:\\Users\\Yvisunshine\\r2catPrimer";
	String preProcessedFastaFile ="preProcessedFastaFile";
	String toBlast ="toBlast";
	

	public RepeatMasking(File fasta) throws IOException, InterruptedException {
		ffr = new FastaFileReader(fasta);
		sequences = ffr.getSequences();
		this.setSeqToCapLetters();
		dir = this.makeDir();
		File tempFileToBlast=writeTempFile(toBlast,dir);
		ctb = new RunBlastToFindRepeats(tempFileToBlast,dir);
		blastOutput = ctb.getBlastOutput();
		this.blastOutputParsen();
		this.setUp(dir);
	}
	
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

	public File makeDir(){
		File dir = new File(dirName);
		if(dir.isDirectory()){
			File[] files = dir.listFiles();
			if(files!=null){
				for(int i = 0;i<files.length;i++){
					File tempFile = files[i];
						files[i].delete();
				}
			}
			dir.delete();
			dir.mkdir();
			return dir;
		} else{
			dir.mkdir();
			return dir;
		}
	}
	
	public void blastOutputParsen() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(blastOutput));
		String currentLine = null;
		String[] tab = null;
		while((currentLine = in.readLine()) != null){
				tab = currentLine.split("\t");
				String queryID=tab[0];
				String subjectID = tab[1];
				if(!queryID.equals(subjectID)){
					int alignmentLength = Integer.valueOf(tab[3]).intValue();
					int startQuery = Integer.valueOf(tab[6]).intValue();
					int endQuery = Integer.valueOf(tab[7]).intValue();
					int startSubject = Integer.valueOf(tab[8]).intValue();
					int endSubject = Integer.valueOf(tab[9]).intValue();
				
					for(int i = 0; i<sequences.size();i++){
						String currentID = sequences.elementAt(i).getId();
						if(currentID.equals(queryID)){
							int offset = (int) sequences.elementAt(i).getOffset();
							int size = (int) sequences.elementAt(i).getSize();
						
								this.setRepeatsToLowerLetters(alignmentLength, startQuery+offset, endQuery+offset);
					
						} if(currentID.equals(subjectID)){
							int offset = (int) sequences.elementAt(i).getOffset();
							int size = (int) sequences.elementAt(i).getSize();
								this.setRepeatsToLowerLetters(alignmentLength, startSubject+offset, endSubject+offset);
						}
					}
					
				}
			}
		}
	


	public void setSeqToCapLetters() throws IOException{
		seq = ffr.getCharArray();
		String temp = new String(seq);
		temp = temp.toUpperCase();
		seq = temp.toCharArray();	
	}
	
	public void setRepeatsToLowerLetters(int repeatLength, int repeatStartPos,int repeatEndPos){
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
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}
	
	public char[] getSeq() {
		return seq;
	}


	public void setSeq(char[] seq) {
		this.seq = seq;
	}
}
