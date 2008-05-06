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

		LogTimer t =  LogTimer.getInstance();
		t.setTimingActive(true);
		t.startTimer();
		
		FastaStreamReader fstr = new FastaStreamReader(new File(fileName));
		
		fstr.scanContents(false);
		

		t.stopTimer("reading");
		
		System.exit(0);
		
		
		
		
		
			String test = "acggtggaaagtgttgXaaagtttttttttgggggggggggggggggggg";

			QGramCoder coder = new QGramCoder(11);

			QGramIndex qi = new QGramIndex(coder.numberOfPossibleQGrams());

			for (int i = 0; i < test.length(); i++) {

				coder.updateEncoding(test.charAt(i));
				System.out.println(coder.getCurrentEncoding() + " -> " + coder.decodeQgramCode(coder.getCurrentEncoding()));
			}

			

/*		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		SequenceIterator stream = SeqIOTools.readFastaDNA(br);

		// Iterate over all sequences in the stream

		while (stream.hasNext()) {
			Sequence seq = stream.nextSequence();
			
			
			int gc = 0;
						for (Iterator iter = seq.iterator(); iter.hasNext();) {
				Symbol sym = (Symbol) iter.next();
				if (sym == DNATools.g() || sym == DNATools.c())
					++gc;
				
			}

			for (int pos = 1; pos <= seq.length(); ++pos) {
				Symbol sym = seq.symbolAt(pos);
				if (sym == DNATools.g() || sym == DNATools.c())
					++gc;
			}
			System.out.println(seq.getName() + ": "
					+ ((gc * 100.0) / seq.length()) + "%");
		}
		*/
	}
	
	
}
