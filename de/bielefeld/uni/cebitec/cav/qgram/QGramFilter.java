package de.bielefeld.uni.cebitec.cav.qgram;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.utils.Timer;

public class QGramFilter {

	private QGramIndex qGramIndex;
	private FastaFileReader query;
	private int[] binCounts;
	private int[] binMin;
	private int[] binMax;

	private Vector<int[]> hits;
	private EpsilonZone eZone;

	/**
	 * @param target
	 * @param query
	 */
	public QGramFilter(QGramIndex targetIndex, FastaFileReader query) {
		this.qGramIndex = targetIndex;
		this.query = query;
		hits = new Vector<int[]>();
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
		int minMatchLength = 450;

		int[] hashTable = qGramIndex.getHashTable();
		int[] occurrenceTable = qGramIndex.getOccurrenceTable();

		// use the same q which was used for the index.
		QGramCoder coder = new QGramCoder(qGramIndex.getQLength());

		// calculates the necessary properties for a matching segment
		eZone = new EpsilonZone(minMatchLength, qGramIndex.getQLength(),
				errorrate);

		while (!eZone.isValid()) {
			errorrate -= 0.001; // reduce the errorrate if no paralelogram can
			// be found
			eZone.init(minMatchLength, qGramIndex.getQLength(), errorrate);
		}

		// a counter for each bucket how many q-grams hit
		// was bucket
		binCounts = new int[eZone.getNumberOfZones(qGramIndex.getInputLength())];
		// for each bucket store the first and the last position, where this
		// bucket was hit
		// a zero entry means that there is no significant hit at the moment
		binMin = new int[binCounts.length]; // was bucketFirstOccurrence

		binMax = new int[binCounts.length]; // was bucketLastOccurrence

		// get the query sequences
		char[] querySequencesArray = null;
		int[] queriesOffsets = null;
		try {
			querySequencesArray = query.getCharArray();
			queriesOffsets = query.getOffsetsArray();
		} catch (IOException e) {
			System.err.println("Error reading Fasta file:" + query.getSource());
			e.printStackTrace();
		}

		// variable names according to kim
		// TODO give better / talking names
		int d = 0;
		int b0 = 0;
		int bm = 0;
		int numberOfBins = binCounts.length;
		int targetSize = qGramIndex.getInputLength();
		int z = eZone.getDeltaExponent();
		int e = eZone.getWidth();
		int w = eZone.getHeight();
		int delta = eZone.getDelta();
		int i = 0;
		int j = 0;

		int code = 0; // buffer for the integer code of the actual
		int bucketindex = 0; // buffer for the bucketindex where the qgram
		// has to be added
		int remainder = 0; // buffer for the distance of the bucket start. used
		// to add qgram in overlapping bucket
		int position = 0; // buffer for the absolute position of the end of a
		// qhit in the target.

		// relative position inside one query sequence
		int relativeQueryPosition = 0;

		// go through all queries in forward direction
		for (int queryNumber = 0; queryNumber < queriesOffsets.length - 1; queryNumber++) {
			relativeQueryPosition = 0;

			System.out.print("Processing: "
					+ query.getSequence(queryNumber).getId() + " ");
			System.out.println();
			// System.out.print("("
			// + query.getSequences().get(queryNumber).getSize()
			// + ") hits:\t ");

			// each position of the actual query
			for (int offsetInQueries = queriesOffsets[queryNumber]; offsetInQueries < queriesOffsets[queryNumber + 1]; offsetInQueries++, relativeQueryPosition++) {
				// get code for next qgram
				code = coder
						.updateEncoding(querySequencesArray[offsetInQueries]);

				// System.out.println("code:" + code + " -> "
				// + coder.decodeQgramCode(code));

				// if the code is valid process each occurrence position
				if (code != -1) {
					for (int occOffset = hashTable[code]; occOffset < hashTable[code + 1]; occOffset++) {
						i = occurrenceTable[occOffset]-eZone.getQGramSize();
						j = relativeQueryPosition;

						// Algorithm 2 page 303 in Rasmussen2006
						d = targetSize + j - i;
						b0 = d >> z;
						bm = b0 % numberOfBins;

//						System.out.println("Updatebin( Bins[" + bm + "], j="
//								+ j + ", d=" + (b0 << z) + ")");
						updateBin(bm, j, (b0 << z));

						// System.out.println((d & (delta-1))+"<"+e);
						if ((d & (delta - 1)) < e) {
							bm = (bm + numberOfBins - 1) % numberOfBins;

//							System.out.println("Updatebin( Bins[" + bm
//									+ "], j=" + j + ", d=" + ((b0 - 1) << z)
//									+ ")");
							updateBin(bm, j, ((b0 - 1) << z));

						}

					}// for each occurrence of this code

//					System.out.println((j - e) % (delta - 1) + "==0");
					if ((j - e) % (delta - 1) == 0) {
						b0 = (j - e) >> z;
						bm = b0 % numberOfBins;
//						System.out.println("CheckAndResetBin( Bins[" + bm
//								+ "], j=" + j + ", d=" + (b0 << z) + ")");
						checkAndResetBin(bm, j, (b0 << z));
					}

				} // fi code was valid

			}// for each position in actual query

			// next query: reset coder and buckets
			coder.reset();
			for (int k = 0; k < binCounts.length; k++) {
				binCounts[k] = 0;
				binMin[k] = 0;
				binMax[k] = 0;
			} // reset counters after query

		}// for each query

		
		System.out.println("Hits:" + hits.size());
		
		// TODO go through all queries in backward direction

		// remove references so that the garbage collector can free the space
		binCounts = null;
		binMax = null;
		binMin = null;
	}

	private void updateBin(int bin, int hitPosition, int offsetDiagonal) {
		int r = bin;
		int j = hitPosition;
		int d = offsetDiagonal;
		int q = eZone.getQGramSize();

		//FIXME this is not working correctly at the moment. check indices
		
		// TODO +q?
		if (j - eZone.getHeight() + q > binMax[r]) {
			if (binCounts[r] >= eZone.getThreshold()) {
				int left = qGramIndex.getInputLength() - d;
				int top = binMax[r];
				int bottom = binMin[r];
				int[] parallelogram = { left, top, bottom };
				System.out.println("left:"+left+ " top:" +  top + " bottom:" + bottom);
				hits.add(parallelogram);
			}
			binCounts[r] = 0;
		}

		if (binCounts[r] == 0) {
			binMin[r] = j;
		}

		if (binMax[r] < j) {
			binMax[r] = j;
			binCounts[r]++;
		}

	}

	private void checkAndResetBin(int bin, int hitPosition, int offsetDiagonal) {
		int r = bin;
		int j = hitPosition;
		int d = offsetDiagonal;
		int q = eZone.getQGramSize();

		// TODO +q?
		if (binCounts[r] >= eZone.getThreshold()) {
			int left = qGramIndex.getInputLength() - d;
			int top = binMax[r];
			int bottom = binMin[r];
			int[] parallelogram = { left, top, bottom };
			System.out.println("left:"+left+ " top:" +  top + " bottom:" + bottom);
			hits.add(parallelogram);
		}
		binCounts[r] = 0;
	}
}
