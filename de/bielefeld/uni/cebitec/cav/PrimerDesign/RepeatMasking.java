package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;
//Corynebacterium urealyticum

public class RepeatMasking {
	private char[] seq;
	FastaFileReader ffr= null;
	Vector<DNASequence> sequences=null;
	
	public RepeatMasking(File fasta) throws IOException {
		ffr = new FastaFileReader(fasta);
		sequences = ffr.getSequences();
		setSeqToCapLetters();
		writeTempFile();
	}

	public void writeTempFile() throws IOException{
		File temp_file = File.createTempFile("toBlast", "temp");
		PrintWriter buffer = new PrintWriter(new FileWriter(temp_file));
		char[] sequence = null;
		int offset = 0;
		int size = 0;
		String id = null;
		for (int i = 0;i<sequences.size();i++){
			offset = (int) sequences.elementAt(i).getOffset();
			size = (int) sequences.elementAt(i).getSize();
			id = sequences.elementAt(i).getId();
			buffer.print(">"+id+" size: "+size);
			buffer.write('\n');
			buffer.write(seq,offset, size);
			buffer.write('\n');
		}
		
		/* ffr = new FastaFileReader(temp_file);
		* sequence = ffr.getCharArray();
		for(int i = 0;i<15;i++){
			System.out.print(sequence[i]);
		}*/
		//System.out.println(temp_file);
		buffer=null;
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
	
	public void repeatMasking(){
		//Runtime.getRuntime().equals(obj);
	}
	public FastaFileReader getFfr() {
		return ffr;
	}

	public void setFfr(FastaFileReader ffr) {
		this.ffr = ffr;
	}
}
