package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;


public class RepeatMasking {
	private char[] seq;
	FastaFileReader ffr= null;
	Vector<DNASequence> sequences=null;
	ConnectToBlast ctb =null;
	
	public RepeatMasking(File fasta) throws IOException {
		ffr = new FastaFileReader(fasta);
		sequences = ffr.getSequences();
		setSeqToCapLetters();
		File tempFile=writeTempFile();
		ctb = new ConnectToBlast(tempFile);
	}

	public File writeTempFile() throws IOException{
		File temp_file = File.createTempFile("toBlast", ".fas",new File("C:\\Users\\Mini-Yvi\\Uni"));
		PrintWriter buffer = new PrintWriter(new FileWriter(temp_file));
		String description = null;
		String id = null;
		for (int i = 0;i<sequences.size();i++){
			id = sequences.elementAt(i).getId();
			description = sequences.elementAt(i).getDescription();
			buffer.print(">"+id+" "+description);
			buffer.write('\n');
			buffer.write(seq);
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
	
	public void setRepeatsToLowerLetters(String repeatSeq){
		String temp = new String(seq);
		if(temp.equals(repeatSeq)){
			for(int i =0;i<repeatSeq.length();i++){
				char[] t = new char[1];
				t[0] = temp.charAt(i);
				String test = new String(t);
				test = test.toLowerCase();
				t = test.toCharArray();
				temp = temp.replace(temp.charAt(i), t[0]);
			}
		}
		seq = temp.toCharArray();
	}
	
	public FastaFileReader getFfr() {
		return ffr;
	}

	public void setFfr(FastaFileReader ffr) {
		this.ffr = ffr;
	}
}
