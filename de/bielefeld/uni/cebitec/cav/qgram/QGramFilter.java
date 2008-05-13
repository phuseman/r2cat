package de.bielefeld.uni.cebitec.cav.qgram;

import java.io.*;
import java.util.Iterator;

import org.biojava.bio.symbol.*;
import org.biojava.bio.seq.*;
import org.biojava.bio.seq.io.*;

import de.bielefeld.uni.cebitec.cav.utils.LogTimer;

public class QGramFilter {

	public static void main(String[] args) throws Exception {

		String fileName = "/homes/phuseman/tmp/test.fasta";

		LogTimer t = LogTimer.getInstance();
		t.setTimingActive(true);
		t.startTimer();

		FastaStreamReader fstr = new FastaStreamReader(new File(fileName));
		fstr.scanContents(true);
		t.stopTimer("reading");

		t.startTimer();
		QGramIndex qi = new QGramIndex(fstr);
		qi.generateIndex();
		t.stopTimer("index");

		// qi.getQGramPositions(69);
		t.startTimer();
//		BufferedWriter out = new BufferedWriter(new FileWriter(fileName
//				+ ".qhits"));
//
		
		
		
		
		t.stopTimer("matching");

		System.exit(0);
	}

}
