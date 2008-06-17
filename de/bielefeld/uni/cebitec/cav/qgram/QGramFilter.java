package de.bielefeld.uni.cebitec.cav.qgram;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.utils.Timer;

public class QGramFilter {

	private QGramIndex qGramIndex;
	private FastaFileReader query;
	private int[] binCounts;
	private int[] binMin;
	private int[] binMax;
	private float[] binMean;
	private float[] binVariance;
	

	private Vector<int[]> hits;
	private EpsilonZone eZone;
	
	private BufferedWriter logWriter;

	/**
	 * @param target
	 * @param query
	 */
	public QGramFilter(QGramIndex targetIndex, FastaFileReader query) {
		this.qGramIndex = targetIndex;
		this.query = query;
		hits = new Vector<int[]>();
		
		
		
		try {
			logWriter=new BufferedWriter(new FileWriter( new File("log.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public static void main(String[] args) throws Exception {

		Timer t = Timer.getInstance();
		t.startTimer();

		File target = new File("target.fasta");
		File query = new File("query.fasta");

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
		
		binMean = new float[binCounts.length];
		binVariance = new float[binCounts.length];

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

		// relative position inside one query sequence
		int relativeQueryPosition = 0;

		// go through all queries in forward direction
		for (int queryNumber = 0; queryNumber < queriesOffsets.length - 1; queryNumber++) {
			relativeQueryPosition = 0;

			System.out.println("Processing: " 
					+ "#"+queryNumber + " "
					+ query.getSequence(queryNumber).getId() + " ("
					 + query.getSequences().get(queryNumber).getSize()
					 + ")");

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
						i = occurrenceTable[occOffset];
						j = relativeQueryPosition-eZone.getQGramSize();

						// Algorithm 2 page 303 in Rasmussen2006
						d = targetSize + j - i; // diagonal on the j-axis shifted by |Target| to obtain positive values
						b0 = d >> z; // b0= d/2^z
						bm = b0 % numberOfBins; // bucketindex

//						System.out.println("Updatebin( Bins[" + bm + "], j="
//								+ j + ", d=" + (b0 << z) + ")");
						updateBin(queryNumber, bm, j, (b0 << z), i);

						// if bins are overlapping
						// delta - 1 looks as bitstring like this: 0000..111111
						// logical 'and' gives  the remainder of a division by delta
						if ((d & (delta - 1)) < e) { 
							bm = (bm + numberOfBins - 1) % numberOfBins;

//							System.out.println("Updatebin( Bins[" + bm
//									+ "], j=" + j + ", d=" + ((b0 - 1) << z)
//									+ ")");
							updateBin(queryNumber, bm, j, ((b0 - 1) << z), i);
						} //fi overlapping hit

					}// for each occurrence of this code

//					System.out.println((j - e) % (delta - 1) + "==0");
					if (((j - e) % (delta - 1)) == 0) {
						b0 = (j - e) >> z;
						bm = b0 % numberOfBins;
//						System.out.println("CheckAndResetBin( Bins[" + bm
//								+ "], j=" + j + ", d=" + (b0 << z) + ")");
												
						checkAndResetBin(queryNumber, bm, j, (b0 << z));
					}

				} // fi code was valid
			}// for each position in actual query

			// next query: reset coder and buckets
			coder.reset();
			
			//check parallelograms for hits and reset counters
			for (int k = 0; k < binCounts.length; k++) {
				
				
				//check if there are remaining parallelograms that have not been reported yet
				if (binCounts[k] >= eZone.getThreshold()) {
					
					// FIXME I'm not sure if this is right
					// this IS wrong! how to get the left value from the bin index? compensate modulo
					int left = k << eZone.getDeltaExponent(); 
					// FIXME!! FIX!!
					
					int top = binMax[k]+eZone.getQGramSize();
					int bottom = binMin[k];
					

					if (top-bottom > eZone.getHeight()) {
						reportMatch(queryNumber, left, top, bottom, k, "remain");
					}

				}

				
				// reset counters after query
				binCounts[k] = 0;
				binMin[k] = 0;
				binMax[k] = 0;
				binMean[k] = 0;
				binVariance[k] = 0;
				
			}
			log("#-------------------reset-----------------------");


		}// for each query

		
		System.out.println("Hits:" + hits.size());
		
		// TODO go through all queries in backward direction

		// remove references so that the garbage collector can free the space
		binCounts = null;
		binMax = null;
		binMin = null;
		binMean = null;
		binVariance = null;
		
		
		try {
			logWriter.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void updateBin(int querynumber, int bin, int hitPosition, int offsetDiagonal, int hitPositionTarget) {
		int r = bin;
		int j = hitPosition;
		int d = offsetDiagonal;
		int q = eZone.getQGramSize();
		
		int i = hitPositionTarget;
		int diagonal= i-j;


		// TODO check correctness with unit test
		if (j - eZone.getHeight() + q > binMax[r]) {
			if (binCounts[r] >= eZone.getThreshold()) {
				int left = qGramIndex.getInputLength() - d;
				int top = binMax[r]+q;
				int bottom = binMin[r];
				reportMatch(querynumber, left, top, bottom, bin, "normal");
			}
			binCounts[r] = 0;
		}

		if (binCounts[r] == 0) {
			binMin[r] = j;
			
			//initial values for mean and variance of the hit-diagonal computation
			binMean[r]=diagonal;
			binVariance[r]=0;
		}

		if (binMax[r] < j) {

			
			if(binCounts[r]>=1) {
			//compute the mean and the variance for the diagonal values in a recursive fashion
			//(see Musterklassifikation Skript 2006/07 Uni Bi.; Franz Kummert; Page 18
			double factor= 1. / binCounts[r];
			
			binVariance[r] = (float) ((1. - factor) * (binVariance[r] + factor
					* ((diagonal - binMean[r]) * (diagonal - binMean[r]))));

			binMean[r] = (float) ((1. - factor) * binMean[r] + factor
					* diagonal);
			
//debugging			
//			if (r==1822) {
//				log(i+ "\t" +j+ "\tdiag:" + diagonal + "\tmean:" +binMean[r]+ "\tvar:" +binVariance[r]);
//			}

			}
			
			

			// enlarge the parallelogram
			binMax[r] = j;
			//increase the q-hit counter for this parallelogram
			binCounts[r]++;

		}
		

	}

	private void checkAndResetBin(int querynumber, int bin, int hitPosition, int offsetDiagonal) {
		int r = bin;
		int j = hitPosition;
		int d = offsetDiagonal;
		int q = eZone.getQGramSize();

		// TODO +q?
		if (binCounts[r] >= eZone.getThreshold()) {
			int left = qGramIndex.getInputLength() - d;
			int top = binMax[r]+q;
			int bottom = binMin[r];
			reportMatch(querynumber, left, top, bottom, bin, "c&r");
		}
		binCounts[r] = 0;
	}

	private void log(String string) {
		try {
			logWriter.write(string+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void reportMatch(int querynumber, int left, int top, int bottom, int bucketindex, String debugstring) {
		int[] parallelogram = { left, top, bottom };
		hits.add(parallelogram);

		this.log(bottom +"\t"+ top +"\t"+ (left+bottom) +"\t"+ (left+top) +"\t"+ binCounts[bucketindex] +"\t"+ binMean[bucketindex] +"\t"+ +binVariance[bucketindex] );

//		System.out.println(debugstring
//				+": bin:"+bucketindex
//				+"  left:"+left
//				+ " top:" +  top 
//				+ " bottom:" + bottom 
//				+ " (" + (top-bottom)+")"
//				+ " hits:" + binCounts[bucketindex]
//				+ " Mean:" + binMean[bucketindex]
//				+" Variance:"+binVariance[bucketindex]);
	}
	
}
