package de.bielefeld.uni.cebitec.cav.qgram;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPosition;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.utils.Timer;

public class QGramFilter {

	private QGramIndex qGramIndex;
	private int[] hashTable = null;
	private int[] occurrenceTable = null;

	private FastaFileReader query;
	private int queryNumber;
	private char[] querySequencesArray;
	
	private boolean reverseComplementDirection;


	
	private int[] binCounts;
	private int[] binMin;
	private int[] binMax;
	private float[] binMean;
	private float[] binVariance;
	



	private EpsilonZone eZone;
	private QGramCoder coder = null;
	
	private BufferedWriter logWriter;
	
	private HashMap<Integer, AlignmentPosition> unsortedAlignmentPositions;
	private AlignmentPositionsList result;
	

	
	
	private int code = 0; // buffer for the integer code of the actual qgram;
	// relative position inside one query sequence
	private int relativeQueryPosition = 0;

	
	
	// all this variables are instanceiated only once to be more space efficient
	
	// variable names for matching according to kim
	// TODO give better / talking names
	private int d = 0; // d
	private int b0 = 0; // b0
	private int bm = 0; // bm
	private int numberOfBins = 0; // |Bins|
	private int targetSize = 0; // |A|
	private int z = 0; // z
	private int e = 0; // e
	private int delta = 0; //delta
	private int i = 0; // i
	private int j = 0; // j
	

	
	

	/**
	 * @param target
	 * @param query
	 */
	public QGramFilter(QGramIndex targetIndex, FastaFileReader query) {
		this.qGramIndex = targetIndex;
		this.query = query;
		
		
		
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
	 * find regions where a good alignment is possible. For details see:<br>
	 * K. Rasmussen, J. Stoye, and E. W. Myers.<br>
	 * Efficient q-Gram Filters for Finding All epsilon-Matches over a Given
	 * Length<br>
	 * J. Comp. Biol., 13(2):296-308, 2006.<br>
	 * 
	 * 
	 */
	public AlignmentPositionsList match() {

		double errorrate = 0.08;
		int minMatchLength = 450;// not the real minimal match lenght, can be smaller.

		unsortedAlignmentPositions = new HashMap<Integer, AlignmentPosition>();
		result = new AlignmentPositionsList();
		
		hashTable = qGramIndex.getHashTable();
		occurrenceTable = qGramIndex.getOccurrenceTable();

		// use the same q which was used for the index.
		coder = new QGramCoder(qGramIndex.getQLength());

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
		querySequencesArray = null;
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
		d = 0;
		b0 = 0;
		bm = 0;
		numberOfBins = binCounts.length;
		targetSize = qGramIndex.getInputLength();
		z = eZone.getDeltaExponent();
		e = eZone.getWidth();
		delta = eZone.getDelta();
		i = 0;
		j = 0;

		


		// go through all queries
		for (queryNumber = 0; queryNumber < queriesOffsets.length - 1; queryNumber++) {
			//match one query in forward direction
			matchQuery(queriesOffsets[queryNumber],queriesOffsets[queryNumber+1]);
			//and reversed
			matchQuery(queriesOffsets[queryNumber+1],queriesOffsets[queryNumber]);
		}// for each query
		
		//debugging:
//		queryNumber = 0;
//		matchQuery(queriesOffsets[queryNumber+1],queriesOffsets[queryNumber]);
//		queryNumber = 1;
//		matchQuery(queriesOffsets[queryNumber+1],queriesOffsets[queryNumber]);
//		queryNumber = 15;
//		matchQuery(queriesOffsets[queryNumber+1],queriesOffsets[queryNumber]);
//		queryNumber = 66;
//		matchQuery(queriesOffsets[queryNumber],queriesOffsets[queryNumber+1]);
//		queryNumber = 67;
//		matchQuery(queriesOffsets[queryNumber+1],queriesOffsets[queryNumber]);


		// remove references so that the garbage collector can free the space
		binCounts = null;
		binMax = null;
		binMin = null;
		binMean = null;
		binVariance = null;
		
		hashTable=null;
		occurrenceTable=null;
		
		
		try {
			logWriter.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return result;
	}

	
	/**
	 * Do the matching for one query specified by starting and ending position. (all queries are stored
	 * in one array (querySequencesArray). The appropriate offset are stored in the queriesOffsets array.
	 * <br>
	 * If the startPos is bigger than the endPos, then the reverse complement is matched on the fly. 
	 * 
	 * @param startPos Starting position of a query (in querySequencesArray)
	 * @param endPos Ending position of a query (in querySequencesArray)
	 */
	private void matchQuery(int startPos, int endPos) {
		
		reverseComplementDirection = (endPos-startPos)<0;
		
		System.out.println("Processing: " 
				+ "#"+queryNumber + " "
				+ query.getSequence(queryNumber).getId()
				+ (reverseComplementDirection?" (reverse complement)":"") +" ("
				 + query.getSequences().get(queryNumber).getSize()
				 + ")");
		
		
		//the bigger number would be the first character of the next sequence, so reduce it by one;
	if (!reverseComplementDirection) {
		endPos--;
		relativeQueryPosition = 0;
	} else {
		startPos--;
		relativeQueryPosition = startPos-endPos;
	}
		

	int offsetInQueries = startPos;

	//if normal direction go from start to end,
	//if reversed direction go from end to start backwards and reverse complement the sequence
	//the conditions are checked in the end of this while loop
	boolean nextBase=true;
	while(nextBase) {
	

		// get code for next qgram
		if (!reverseComplementDirection){
			code = coder.updateEncoding(querySequencesArray[offsetInQueries]);
		} else  {
			//calculate complement code on the fly
			code = coder.updateEncodingComplement(querySequencesArray[offsetInQueries]);
		} 
		
			// if the code is valid process each occurrence position
			if (code != -1) {
				for (int occOffset = hashTable[code]; occOffset < hashTable[code + 1]; occOffset++) {
					
					i = occurrenceTable[occOffset]; //hitposition in target
					
					//j is the hitposition in the query
					if (!reverseComplementDirection) { //forward
						j = relativeQueryPosition-eZone.getQGramSize();
					} else  {
						// for the reverse complement we have to consider the position of the reversed string.
						// otherwise the hitting diagonals are wrong
						j= startPos-endPos-relativeQueryPosition-eZone.getQGramSize();
					}
					

					// Algorithm 2 page 303 in Rasmussen2006
					d = targetSize + j - i; // diagonal on the j-axis shifted by |Target| to obtain positive values
					b0 = d >> z; // b0= d/2^z
					bm = b0 % numberOfBins; // bucketindex

					
//					System.out.println("Updatebin( Bins[" + bm + "], j="
//							+ j + ", d=" + (b0 << z) + ")");
					updateBin(bm, i, j, (b0 << z));

					// if bins are overlapping
					// delta - 1 looks as bitstring like this: 0000..111111
					// logical 'and' gives  the remainder of a division by delta
					if ((d & (delta - 1)) < e) { 
						//get previous bin
						bm = (bm + numberOfBins - 1) % numberOfBins; // avoid negative value if bm=0
						updateBin(bm, i, j, ((b0 - 1) << z));
					} //fi overlapping hit

				}// for each occurrence of this code


				//report and clean all parallelograms on the right end of the target
				//explanation see comment on checkAndResetBin method
				if (((j - e) % (delta - 1)) == 0) {
					b0 = (j - e) >> z;
					bm = b0 % numberOfBins;
					checkAndResetBin(bm,  (b0 << z));
				}

			} // fi code was valid
			
			
			// check conditions for the while loop
			if(!reverseComplementDirection) {
				//forward direction, forward until the end of a query
				if (offsetInQueries < endPos) {
				offsetInQueries++;
				relativeQueryPosition++;
				} else {
					nextBase=false;
				}
			} else {
				//reverse complement, go backwards through the query
				if (offsetInQueries>endPos) {
					offsetInQueries--;
					relativeQueryPosition--;
					} else {
						nextBase=false;
					}
			}  
		}//while loop: for each position in actual query

	//after each query and each direction:
	resetCountsAndReportRemainingParalellograms();
	
	unsortedAlignmentPositions.clear();

	}

	/**
		 * Increases the hitcount of the given bin, or reports a parallelogram, if the last hit was to far away and the threshold was reached.
		 * 
		 * @param b is the bin index (kim: bm)
		 * @param hitPositionTarget the starting position of the bin in the target sequence (i)
		 * @param hitPositionQuery the actual position in the query sequence (j)
		 * @param offsetDiagonal the (first) diagonal of the parallelogram. (b0<<z)
		 * d = (|Target| + position_in_query - position_in_target)/|Bins|
		 */
		private void updateBin(int b, int hitPositionTarget, int hitPositionQuery, int offsetDiagonal) {
			// the size of the query is needed for the reverse complement
			int querySize = (int)query.getSequence(queryNumber).getSize();
			
			// TODO check correctness with unit test
			// if the last hit was more than w+q away: report and/or start new parallelogram
			if (hitPositionQuery - eZone.getHeight() + eZone.getQGramSize() > binMax[b]) {
				//report only if there are enough hits
				if (binCounts[b] >= eZone.getThreshold()) {
					// kim: d is approx |A|+j-i, so |A|-d is -j+i which is the diagonal on the target
					int left = qGramIndex.getInputLength() - offsetDiagonal; // == index of the offset diagonal on the target
					int top = binMax[b]+eZone.getQGramSize();
					int bottom = binMin[b];
					 /*|\ 
					 * | \
					 * |  \ d=|A|+j-i
					 * |   \ 
					 * |    \ left
					 * |     \|   i (hit target)
					 * |______\___|____________ 
					 * |       \
					 * -bottom  \
					 * |         \
					 * -top       \ new hit j (hit query)
					 * |
					 * j
					 */ 
					if(!reverseComplementDirection){
						//normal direction
					reportMatch(left, top, bottom, b, "normal");
					} else {
						// for the reverse complement the length of the query has to be added to get
						// the intersection of the diagonal on the i axis.
						// since diagonal is i+(|query|-j) =  i-j +|query|
						// also top and bottom have to be flipped
						reportMatch( left+querySize, querySize-top, querySize-bottom, b, "normal");
					}
				}
				binCounts[b] = 0;
			}
	
			
			//start a new paralelogram and initialize mean and variance
			if (binCounts[b] == 0) {
	
				binMin[b] = hitPositionQuery;
				
				//initial values for mean and variance of the hit-diagonal computation
				binMean[b]=hitPositionTarget-hitPositionQuery; // == diagonal
				binVariance[b]=0; // variance for one sample is 0
				
				//adjust the mean if in reverse direction
				// diagonal for reverse complement is i + (|query|-j) == i-j +|query|
				if(reverseComplementDirection) {
					binMean[b]+=querySize;
				}
				
				
			}
	
			//if there was no hit on the same query position
			// this avoids the counting of multiple hits on a homopolymer region, for example "aaaaaaaaaa"
			if (binMax[b] < hitPositionQuery) {
				// enlarge the parallelogram
				binMax[b] = hitPositionQuery;
				//increase the q-hit counter for this parallelogram
				binCounts[b]++;
	
				
				
				
				//compute the mean and the variance recursively
				if(binCounts[b]>=1) {
					int diagonal= hitPositionTarget-hitPositionQuery;
					//adjust the mean if in reverse direction
					// diagonal for reverse complement is i + (|query|-j) == i-j +|query|
					if(reverseComplementDirection) {
						diagonal+=querySize;
					}
					
	
				//compute the mean and the variance for the diagonal values in a recursive fashion
				//(see Musterklassifikation Skript 2006/07 Uni Bi.; Franz Kummert; Page 18
				double factor= 1. / binCounts[b];
				
				//first update the variance because it depends on the mean value of the previous step
				binVariance[b] = (float) ((1. - factor) * (binVariance[b] + factor
						* ((diagonal - binMean[b]) * (diagonal - binMean[b]))));
	
				binMean[b] = (float) ((1. - factor) * binMean[b] + factor
						* diagonal);
				
	//debugging			
//				if (b==1907) {
//					log("i:" +i+ "\tj:" +j+ "\tn-j:" +(querySize-j)+ "\tdiag:" + (querySize-j+i) + "\tmean:" +binMean[b]+ "\tvar:" +binVariance[b]);
//				}
	
				} // mean and variance computation
			} // enlargement of one paralellogram
		}

	/**
	 * Since only |Targetsize|/delta (roughly) bins are used we have to take care that the ones which are shared
	 * do not produce hits which go through both areas.
	 * 
	 *  
	 * |\ d=|A|+j-i
	 * | \
	 * |  \
	 * |   \
	 * |    \
	 * |     \
	 * |______\A i
	 * |\     |\
	 * | \   y| \
	 * | x\   |x'\
	 * B
	 * j
	 * 
	 * 
	 * Diagonals in the area x, where j>i, are projected to x'. So we have to clear the bins of region y
	 * which end on the border of A, because there is no possibility to extend them.  
	 * 
	 * 
	 * @param b bin index
	 * @param offsetDiagonal the first diagonal of this bin
	 */
	private void checkAndResetBin(int b, int offsetDiagonal) {
		if (binCounts[b] >= eZone.getThreshold()) {
			int top = binMax[b]+eZone.getQGramSize();
			int bottom = binMin[b];
			if (top-bottom > eZone.getWidth()) {// normally height
				int left = qGramIndex.getInputLength() - offsetDiagonal; // == index of the offset diagonal on the target
	
				if(!reverseComplementDirection){
					//normal direction
				reportMatch(left, top, bottom, b, "c+r");
				} else  {
					// for the reverse complement the length of the query has to be added to get
					// the intersection of the diagonal on the i axis.
					// since diagonal is i+(|query|-j) =  i-j +|query|
					// also top and bottom have to be flipped
					int querySize = (int)query.getSequence(queryNumber).getSize();
					reportMatch( left+querySize, querySize-top, querySize-bottom, b, "c+r");
				}
			}
		}
		binCounts[b] = 0;
	}

	private void resetCountsAndReportRemainingParalellograms() {
		int top=0;
		int bottom=0;
		int querySize = (int)query.getSequence(queryNumber).getSize();
	
		
		//check parallelograms for hits and reset counters
		for (int bin = 0; bin < binCounts.length; bin++) {
			//check if there are remaining parallelograms that have not been reported yet
			if (binCounts[bin] >= eZone.getThreshold()) {
				top = binMax[bin]+eZone.getQGramSize();
				bottom = binMin[bin];
				
				if (top-bottom > eZone.getWidth()) {//normally height
					
					int left = -(bin << eZone.getDeltaExponent()); 
					/*
					 * ^^^ this should be right, because all diagonals
					 * of the area in x (where  j>i) are projected to x' with the modulo |Bins| operation.
					 * all diagonals in area y, which have the same bin index like the ones in x,
					 * are reported with the function checkAndResetBin, since they can not be extended.
					 * So in the end, when j=|B| only the diagonals from area z and x remain.
					 * 
					 * 
					 * |\ d=|A|+j-i
					 * | \
					 * |  \
					 * |   \
					 * |    \
					 * |     \
					 * |______\A i
					 * |\  \  |\
					 * | \  \y| \
					 * | x\ z\|x'\
					 * B
					 * j
					 */

					if(!reverseComplementDirection){
						//normal direction
						reportMatch(left, top, bottom, bin, "remain");
					} else  {
						// for the reverse complement the length of the query has to be added to get
						// the intersection of the diagonal on the i axis.
						// since diagonal is i+(|query|-j) =  i-j +|query|
						// also top and bottom have to be flipped
						reportMatch( left+querySize, querySize-top, querySize-bottom, bin, "reset");
					}
				}
			}

			// reset coder and buckets for the next query
			coder.reset();
			
			// reset counters after query
			binCounts[bin] = 0;
			binMin[bin] = 0;
			binMax[bin] = 0;
			binMean[bin] = 0;
			binVariance[bin] = 0;
		}
		log("#-------------------reset-----------------------");
	}
	
	private void log(String string) {
		try {
			logWriter.write(string+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void reportMatch(int left, int top, int bottom, int bucketindex, String debugstring) {
		
		//take the mean instead of the left value
		left = (int) binMean[bucketindex];
		
		AlignmentPosition ap;
		long targetStart=0;
		long targetEnd=0;
		long queryStart=0;
		long queryEnd=0;

		
		if (!reverseComplementDirection) {
			// normal hit; go from left (mean) to the hit position
			/*              left
			 * _____________|__________________ A
			 * |             \
			 * |              \- bottom
			 * |               \
			 * |                \- top
			 */
			queryStart=bottom;
			queryEnd=top;
			targetStart=(left + bottom);
			targetEnd=(left + top);
		} else {
			/* reverse complement hit; go from right (mean) to the hit position
			 * 
			 *               right
			 * _____________|__________________ A
			 * |           / 
			 * |          /- bottom
			 * |         /
			 * |        /- top
			 */

			queryStart=bottom;
			queryEnd=top;
			targetStart=(left - bottom);
			targetEnd=(left - top);
		}
		
		
		
//		System.out.println(debugstring
//				+": bin:"+bucketindex
//				+"  left:"+left
//				+ " top:" +  top 
//				+ " bottom:" + bottom 
//				+ " (" + (top-bottom)+")"
//				+ " hits:" + binCounts[bucketindex]
//				+ " Mean:" + binMean[bucketindex]
//				+ " (" + (left-binMean[bucketindex])+ ") "
//				+" Variance:"+binVariance[bucketindex]);
//		

		// to get the query sequence (only if the global queryNumber is set properly)
		//query.getSequence(queryNumber).getId();
//		to get the target sequence:
//		qGramIndex.getSequenceAtPosition(position)

		
		// get the sequence objects
		DNASequence dNASeqQuery = query.getSequence(queryNumber);
		DNASequence dNASeqTarget = qGramIndex.getSequenceAtPosition((int)targetStart);

		
		ap = new AlignmentPosition(dNASeqTarget,
				targetStart, targetEnd, dNASeqQuery, queryStart, queryEnd);


		this.log(String.format("%s\t%d\t%s\t%d\tn/a\t%d\tn/a\tn/a\t%d\t%d\t%d\t%d\t%f\t%d\t***",
				dNASeqQuery.getId(), dNASeqQuery.getSize(),
				dNASeqTarget.getId(), dNASeqTarget.getSize(), Math.abs(top-bottom), queryStart, queryEnd,
				targetStart, targetEnd, binVariance[bucketindex], bucketindex));

//
//		// save match temporary to sort out duplicate matches
		// due to the overlapping parallelograms some hits are recognised twice
		unsortedAlignmentPositions.put(bucketindex, ap);
		
		if (unsortedAlignmentPositions.containsKey(bucketindex+1)) {
			//merge
		} else if (unsortedAlignmentPositions.containsKey(bucketindex-1)) {
			//merge
		} else {
			result.addAlignmentPosition(ap);
		}//TODO
//			

		
		

	}
	

	
}
