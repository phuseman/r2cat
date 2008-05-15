package de.bielefeld.uni.cebitec.cav.qgram;

import java.io.File;

import de.bielefeld.uni.cebitec.cav.utils.Timer;

public class QGramFilter {

	public static void main(String[] args) throws Exception {

		String fileName = "/homes/phuseman/tmp/test.fasta";

		Timer t = Timer.getInstance();
		t.setTimingActive(true);
		t.startTimer();

		FastaFileReader fstr = new FastaFileReader(new File(fileName));
		fstr.scanContents(true);
		t.stopTimer("reading");

		t.startTimer();
		QGramIndex qi = new QGramIndex(fstr);
		qi.generateIndex();
		t.stopTimer("index");

		// qi.getQGramPositions(69);
		t.startTimer();
		// BufferedWriter out = new BufferedWriter(new FileWriter(fileName
		// + ".qhits"));
		//

		t.stopTimer("matching");

		System.exit(0);
	}

}
