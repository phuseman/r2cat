package de.bielefeld.uni.cebitec.cav.qgram;

import java.io.*;
import java.util.Iterator;

import org.biojava.bio.symbol.*;
import org.biojava.bio.seq.*;
import org.biojava.bio.seq.io.*;

import de.bielefeld.uni.cebitec.cav.utils.LogTimer;

public class QGramFilter {

	public static void main(String[] args) throws Exception {
		String fileName = "/homes/phuseman/compassemb/Corynebacterium_glutamicum_R.fna";

		// Set up sequence iterator

		LogTimer t =  LogTimer.getInstance();
		t.setTimingActive(true);
		t.startTimer();
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		SequenceIterator stream = SeqIOTools.readFastaDNA(br);

		// Iterate over all sequences in the stream

		while (stream.hasNext()) {
			Sequence seq = stream.nextSequence();
			
			
			int gc = 0;
			/*			for (Iterator iter = seq.iterator(); iter.hasNext();) {
				Symbol sym = (Symbol) iter.next();
				if (sym == DNATools.g() || sym == DNATools.c())
					++gc;
				
			}
*/
			for (int pos = 1; pos <= seq.length(); ++pos) {
				Symbol sym = seq.symbolAt(pos);
				if (sym == DNATools.g() || sym == DNATools.c())
					++gc;
			}
			System.out.println(seq.getName() + ": "
					+ ((gc * 100.0) / seq.length()) + "%");
		}
		
		t.stopTimer("Einlesen");
	}
	
	
}
