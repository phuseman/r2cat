package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.IOException;

import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;


public class RepeatMasking {
	private char[] seq;
	FastaFileReader ffr= null;

	public RepeatMasking(FastaFileReader fastaParser) {
		ffr=fastaParser;
		
	}
	
	public void setSeqToCapLetters() throws IOException{
		seq = ffr.getCharArray();
		String temp = new String(seq);
		temp.toUpperCase();
		seq = temp.toCharArray();
	}
	
	public void repeatMasing(){
		//Runtime.getRuntime().equals(obj);
	}

}
