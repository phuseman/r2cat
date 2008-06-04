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
		System.gc();
		t.stopTimer("garbage collection");

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

		double errorrate = 0.08;
		int minMatchLength = 500;

		int[] hashTable = qGramIndex.getHashTable();
		int[] occurrenceTable = qGramIndex.getOccurrenceTable();

		QGramCoder coder = new QGramCoder(qGramIndex.getQLength());

		EpsilonZone eZone = new EpsilonZone(minMatchLength, qGramIndex
				.getQLength(), errorrate);
		while (!eZone.isValid()) {
			errorrate -= 0.001;
			eZone.init(minMatchLength, qGramIndex.getQLength(), errorrate);
		}

		// a counter for each bucket how many q-grams hit
		int[] buckets = new int[eZone.getNumberOfZones(qGramIndex
				.getInputLength()) + 1];
		// for each bucket store the first and the last position, where this
		// bucket was hit
		// a zero entry means that there is no significant hit at the moment
		int[] bucketFirstOccurrence = new int[buckets.length];
		int[] bucketLastOccurrence = new int[buckets.length];

		int mod = eZone.getDelta();

		char[] querySequencesArray = null;
		int[] queriesOffsets = null;

		try {
			querySequencesArray = query.getCharArray();
			queriesOffsets = query.getOffsetsArray();

		} catch (IOException e) {
			System.err.println("Error reading Fasta file:" + query.getSource());
			e.printStackTrace();
		}

		int code = 0; // buffer for the integer code of the actual
		int bucketindex = 0; // buffer for the bucketindex where the qgram
		// has to be added
		int remainder = 0; // buffer for the distance of the bucket start. used
		// to add qgram in overlapping bucket
		int position = 0; // buffer for the absolute position of the end of a
		// qhit in the target.

		// go through all queries in forward direction
		for (int queryNumber = 0; queryNumber < queriesOffsets.length - 1; queryNumber++) {
			System.out.print("Processing: "
					+ query.getSequence(queryNumber).getId() + " ");
			System.out.print("("
					+ query.getSequences().get(queryNumber).getSize()
					+ ") hits:\t ");

			// each position of the actual query

			for (int offsetInQueries = queriesOffsets[queryNumber], queryRelativePosition = 0; offsetInQueries < queriesOffsets[queryNumber + 1]; offsetInQueries++, queryRelativePosition++) {
				// get code for next qgram
				code = coder
						.updateEncoding(querySequencesArray[offsetInQueries]);

				// System.out.println("code:" + code + " -> "
				// + coder.decodeQgramCode(code));

				// if the code is valid process each occurrence position
				if (code != -1) {
					for (int occOffset = hashTable[code]; occOffset < hashTable[code + 1]; occOffset++) {

						// substract the relative position to get the
						// appropriate diagonal
						// TODO: has to be adjusted with several targets.
						int positionTmp = occurrenceTable[occOffset];
						
	
						position = occurrenceTable[occOffset];
						// FIXME funktioniert noch nicht... diagonalen
						// berechnen?
						// position -= queryRelativePosition;

						bucketindex = position / mod;
						remainder = position % mod;

						buckets[bucketindex]++;
						// if new match zone -> remember startposition
						if (bucketFirstOccurrence[bucketindex] == 0) {
							bucketFirstOccurrence[bucketindex] = occurrenceTable[occOffset];
						} else { // else adjust last occurrence
							bucketLastOccurrence[bucketindex] = occurrenceTable[occOffset];
						}

						// same procedure for overlapping buckets
						if (remainder < eZone.getWidth() && bucketindex > 0) {
							buckets[bucketindex - 1]++;
							// if new match zone -> remember startposition
							if (bucketFirstOccurrence[bucketindex - 1] == 0) {
								bucketFirstOccurrence[bucketindex - 1] = occurrenceTable[occOffset];
							} else { // else adjust last occurrence
								bucketLastOccurrence[bucketindex - 1] = occurrenceTable[occOffset];
							}
						}

						if (buckets[bucketindex] >= eZone.getThreshold()
								&& queryRelativePosition > eZone.getHeight()) {
							// TODO: add or replace new matching zone
							// System.out.println(occurrenceTable[occOffset]);
						}

					}// for each occurrence of this code
				} // fi code was valid

			}// for each query

			// next query: reset coder and buckets
			coder.reset();
			int hits = 0;
			for (int i = 0; i < buckets.length; i++) {
				if (buckets[i] > eZone.getThreshold()) {
					hits++;
				}

				buckets[i] = 0;
				bucketFirstOccurrence[i] = 0;
				bucketLastOccurrence[i] = 0;
			}
			System.out.println(hits);

		}

		// TODO go through all queries in backward direction

	}
}
