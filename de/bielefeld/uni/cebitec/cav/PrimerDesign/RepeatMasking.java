package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
	RunBlast ctb =null;
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
		ctb = new RunBlast(tempFileToBlast,dir);
		blastOutput = ctb.getBlastOutput();
		this.blastOutputParsen();
		this.setUp(dir);
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

	public File makeDir(){
		
		File dir = new File(dirName);
		if(dir.isDirectory()){
			System.out.println("Directory " +dir.getName()+ " already exists");
			return null;
		} else{
			dir.mkdir();
			return dir;
		}
	}
	
	
	
	public void blastOutputParsen() throws IOException{
		//File f = new File("C:\\Users\\Mini-Yvi\\blastout.txt");
		//BufferedReader in = new BufferedReader(new FileReader(f));
		BufferedReader in = new BufferedReader(new FileReader(blastOutput));
		String currentLine = null;
		String[] tab = null;
		while((currentLine = in.readLine()) != null){
				tab = currentLine.split("\t");
				String contigID=tab[0];
				String contigID2 = tab[1];
				//String tab4 = tab[4];
				if(!contigID.equals(contigID2)){
					//System.out.println(contigID+" "+contigID2);
					int length = Integer.valueOf(tab[3]).intValue();
					int startPosContig1 = Integer.valueOf(tab[6]).intValue();
					int endPosContig1 = Integer.valueOf(tab[7]).intValue();
					int startPosContig2 = Integer.valueOf(tab[8]).intValue();
					int endPosContig2 = Integer.valueOf(tab[9]).intValue();
					//System.out.println(length+" "+startPosContig1+" "+endPosContig1);
					this.setRepeatsToLowerLetters(length,startPosContig1, endPosContig1);
					this.setRepeatsToLowerLetters(length, startPosContig2, endPosContig2);
					
				}
		}

	/*	for(int i = 0; i<tab.length;i++){
		System.out.print(tab[i]+"\t");
		}*/
		//System.out.println(tab.length);
	}
	
	public File writeTempFile(String fileName, File dir) throws IOException{
		
		File temp_file = File.createTempFile(fileName, ".fas",dir);
		PrintWriter buffer = new PrintWriter(new FileWriter(temp_file));
		String description = null;
		String id = null;
		int offset = 0;
		int size = 0;
		for (int i = 0;i<sequences.size();i++){
			id = sequences.elementAt(i).getId();
			description = sequences.elementAt(i).getDescription();
			offset = (int) sequences.elementAt(i).getOffset();
			size = (int) sequences.elementAt(i).getSize();
			buffer.print(">"+id+" "+description);
			buffer.write('\n');
			for(int j=offset+size-1;j>=offset;j--){
				buffer.write(seq[j]);
			}
			buffer.write('\n');
		}
		
		buffer=null;
		return temp_file;
	}

	public void setSeqToCapLetters() throws IOException{
		seq = ffr.getCharArray();
		String temp = new String(seq);
		temp = temp.toUpperCase();
		seq = temp.toCharArray();	
	}
	
	public void setRepeatsToLowerLetters(int repeatLength, int repeatStartPos,int repeatEndPos){
		char[] repeat = new char[repeatLength];
		int m =0;
		for(int j = repeatStartPos;j<repeatStartPos+repeatLength;j++,m++){
			repeat[m] = seq[j];
		}
		String tempRepeat = new String(repeat);
		String temp = new String(seq);
		String repeatLowerCase = tempRepeat.toLowerCase();
		temp = temp.replace(tempRepeat, repeatLowerCase);
		
		
/*		char[] charAtPos = new char[1];
			for(int i =repeatStartPos;i<repeatEndPos-1;i++){
				charAtPos[0] = seq[i];
				String testing = new String(charAtPos);
				testing = testing.toLowerCase();
				charAtPos = testing.toCharArray();
				temp = temp.replace(temp.charAt(i), charAtPos[0]);
				
		}*/
			//seq = temp.toCharArray();
		
		this.setSeq(temp.toCharArray());
	/*	for(int i=393;i<393+71;i++){
			System.out.print(seq[i]);
		}*/
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
