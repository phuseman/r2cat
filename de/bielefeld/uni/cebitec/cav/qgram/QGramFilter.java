package de.bielefeld.uni.cebitec.cav.qgram;

import java.io.File;
import java.io.IOException;

import de.bielefeld.uni.cebitec.cav.utils.Timer;

public class QGramFilter {

	private QGramIndex qGramIndex;
	private FastaFileReader query;

	/**
	 * @param target
	 * @param query
	 */
	public QGramFilter(QGramIndex targetIndex, FastaFileReader query) {
		this.qGramIndex = targetIndex;
		this.query = query;
	}

	public static void main(String[] args) throws Exception {

		Timer t = Timer.getInstance();
		t.startTimer();

		File target = new File("/homes/phuseman/compassemb/DSM7109.fasta");
		File query = new File("/homes/phuseman/compassemb/cur7111_min500.fas");

		t.startTimer();

		FastaFileReader targetFasta = new FastaFileReader(target);
		targetFasta.scanContents(true);
		t.stopTimer("reading the target fasta file");

		t.startTimer();
		QGramIndex qi = new QGramIndex();
		qi.generateIndex(targetFasta);
		t.stopTimer("total for indexing the content");

		t.startTimer();

		t.startTimer();
		FastaFileReader queryFasta = new FastaFileReader(query);
		queryFasta.scanContents(true);
		t.stopTimer("reading the query fasta");

		QGramFilter qf = new QGramFilter(qi, queryFasta);
		qf.match();

		t.stopTimer("matching");

		t.stopTimer("Total");
		System.exit(0);
	}

	/**
	 * The Match function uses the q-gram index to filter the search space and
	 * find regions where a good alignment is probable. For details see:<br>
	 * K. Rasmussen, J. Stoye, and E. W. Myers.<br>
	 * Efficient q-Gram Filters for Finding All epsilon-Matches over a Given
	 * Length<br>
	 * J. Comp. Biol., 13(2):296-308, 2006.<br>
	 * 
	 * 
	 */
	public void match() {
		int[] hashTable = qGramIndex.getHashTable();
		int[] occurrenceTable = qGramIndex.getOccurrenceTable();
		QGramCoder coder = new QGramCoder(qGramIndex.getQLength());

		char[] querySequencesArray = null;
		int[] queriesOffsets = null;

		try {
			querySequencesArray = query.getCharArray();
			queriesOffsets = query.getOffsetsArray();

		} catch (IOException e) {
			System.err.println("Error reading Fasta file:" + query.getSource());
			e.printStackTrace();
		}

		int code = 0;
		
		// go through all queries in foreward direction
		for (int queryNumber = 0; queryNumber < queriesOffsets.length - 1; queryNumber++) {
			for (int offsetInQueries = queriesOffsets[queryNumber]; offsetInQueries < queriesOffsets[queryNumber + 1]; offsetInQueries++) {
				// get code for next qgram
				code = coder.updateEncoding(querySequencesArray[offsetInQueries]);

				System.out.println("code:" + code + " -> "
						+ coder.decodeQgramCode(code));

				// if the code is valid process each occurrence position
				if (code != -1) {
					for (int occOffset = hashTable[code]; occOffset < hashTable[code + 1]; occOffset++) {
						System.out.print(" pos:" + occurrenceTable[occOffset] + " ");

						System.out.println();
					}
				}

			}
			// next query: reset coder and buckets
			coder.reset();
			//TODO reset buckets
		}
		
		//TODO go through all queries in backward direction

	}

}
