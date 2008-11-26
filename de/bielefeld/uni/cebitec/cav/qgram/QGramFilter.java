/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
 *   phuseman  a t  cebitec.uni-bielefeld.de                               *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package de.bielefeld.uni.cebitec.cav.qgram;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.prefs.Preferences;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPosition;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.gui.MatchDialog;
import de.bielefeld.uni.cebitec.cav.utils.CAVPrefs;
import de.bielefeld.uni.cebitec.cav.utils.Timer;

/**
 * Filters a given target for highly similar regions with query sequences.
 * For the filtering a qgram index is used.
 * The Idea is based on <br>
	 * K. Rasmussen, J. Stoye, and E. W. Myers.<br>
	 * Efficient q-Gram Filters for Finding All epsilon-Matches over a Given
	 * Length<br>
	 * J. Comp. Biol., 13(2):296-308, 2006.<br>
	 * 
 * @author phuseman
 */
public class QGramFilter {
	//qgram index for the target
	private QGramIndex qGramIndex;
	// for speed issues the two main compontents of the index are stored in this class
	private int[] hashTable = null;
	private int[] occurrenceTable = null;

	//the query/queries
	private FastaFileReader query;
	
	//global: number of the query which is actually matched against the target
	private int queryNumber;
	//char array of the sequences. one after each other.
	private char[] querySequencesArray;
	
	
	//global flag if we are matching in foreward or backward direction
	private boolean reverseComplementDirection;


	//the target is divided in overlapping bins. for each bin we are storing different information:
	//number of hits
	private int[] binCounts;
	//smallest query index
	private int[] binMin;
	//biggest query index
	private int[] binMax;
	//mean value of the matching diagonals
	private float[] binMean;
	//variance of the diagonals of the q-hits
	private float[] binVariance;
	


	//	provides some bounds for the matching paralellograms
	private EpsilonZone eZone;
	//transformes the input into q-gram codes to find them in the index
	private QGramCoder coder = null;
	
	
	
	//a list of all found matching regions -- final result
	private AlignmentPositionsList result;

	//in overlapping bins hits can be reported twice. the bucket number is
	// then usually +-1. 
	// here we store store all hits in a hashmap. the key is the bucket index.
	// we can then check if there was a overlapping/too similar hit in bucket+-1.
	// since there can be many hits for one bucket we need a list instead of a single AlignmentPositions.
	// I chose a vector because it is simple to use. 
	private HashMap<Integer, Vector<AlignmentPosition>> temporaryResults;


	
	// buffer for the integer code of the actual qgram;
	private int code = 0; 
	// relative position inside one query sequence
	private int relativeQueryPosition = 0;

	
	
	// all this variables are initialized only once to be more space efficient
	
	// variable names for the matching according to kim
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
	private MatchDialog matchDialog;
	private double progress;
	
	
	//determines if the mean matching diagonal for each contig and variance should be computed
	// it is faster without and the values are not really reliable to 
	//serve as a measure of identity
	private static final boolean computeMean=false;
	

	//debug
//	public BufferedWriter logfile;
//	
//	private void log(String s) {
//		try {
//			logfile.write(s);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	/**
	 * Creates a filter from
	 * @param target a Q-Gram Index of the Target
	 * @param query and the query sequence(s)
	 */
	public QGramFilter(QGramIndex targetIndex, FastaFileReader query) {
		this.qGramIndex = targetIndex;
		this.query = query;
		
		//debug
//		try {logfile = new BufferedWriter(new FileWriter("log.csv"));}catch(IOException e) {;}
	}

	
	/**
	 * Main Method is here for debugging. the matching can be tested without the gui.
	 */
	public static void main(String[] args) throws Exception {
		CAVPrefs preferences = new CAVPrefs();
		Preferences pref = CAVPrefs.getPreferences();
		
//		
//		File test = new File("test_co.r2c");
//		AlignmentPositionsList apl2=new AlignmentPositionsList();
//		apl2.readFromFile(test);
//		apl2.generateNewStatistics();
//		apl2.writeContigsOrderFasta(new File("test.order"));
//		
//		System.exit(0);
//		
//		
		
		
		
		
		Timer t = Timer.getInstance();
		t.startTimer();//total
//debugging
//		File query = new File("/homes/phuseman/compassemb/test/cur7111_min500contigs.fas");
//		File target = new File("/homes/phuseman/compassemb/test/DSM7109_mod.fas");
//		File target = new File("/homes/phuseman/compassemb/test/DSM7109.fasta");
//match the files stored in the preferences
		File query = new File(pref.get("query", ""));
		File target = new File(pref.get("target", ""));
		

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
		AlignmentPositionsList apl = qf.match();

		t.stopTimer("matching");

		t.stopTimer("Total");
		
//		qf.logfile.flush();
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
		int minMatchLength = 450;// not the real minimal match length, can be smaller.

		result = new AlignmentPositionsList();
		temporaryResults = new HashMap<Integer, Vector<AlignmentPosition>>();
		
		hashTable = qGramIndex.getHashTable();
		occurrenceTable = qGramIndex.getOccurrenceTable();

		// use the same q which was used for the index.
		coder = new QGramCoder(qGramIndex.getQLength());

		// calculates the necessary properties for a matching segment
		eZone = new EpsilonZone(minMatchLength, qGramIndex.getQLength(),
				errorrate);

		while (!eZone.isValid()) {
			errorrate -= 0.001; // reduce the errorrate if no paralelogram-dimensions can
			// be found
			eZone.init(minMatchLength, qGramIndex.getQLength(), errorrate);
		}
		
		this.reportProgress(0, eZone.toString());

		// a counter for each bucket how many q-grams hit
		// was bucket
		binCounts = new int[eZone.getNumberOfZones(qGramIndex.getInputLength())];
		// for each bucket store the first and the last position, where this
		// bucket was hit
		// a zero entry means that there is no significant hit at the moment
		binMin = new int[binCounts.length]; // was bucketFirstOccurrence

		binMax = new int[binCounts.length]; // was bucketLastOccurrence
		
		
		if (computeMean) {
		binMean = new float[binCounts.length];
		binVariance = new float[binCounts.length];
		}

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

		


		int total=(queriesOffsets[queriesOffsets.length-1])*2;
		
//		// go through all queries
		for (queryNumber = 0; queryNumber < queriesOffsets.length - 1; queryNumber++) {
			progress=(2.*queriesOffsets[queryNumber])/total;
			//match one query in forward direction
			matchQuery(queriesOffsets[queryNumber],queriesOffsets[queryNumber+1]);
			generateResultFromTemporary();
			
			progress=((2.*queriesOffsets[queryNumber])+(queriesOffsets[queryNumber+1]-queriesOffsets[queryNumber]))/total;
			//and reversed
			matchQuery(queriesOffsets[queryNumber+1],queriesOffsets[queryNumber]);
			generateResultFromTemporary();

			
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
		
		
		reportProgress(1, result.size() + " hits found.");
		
		return result;
	}

	//TODO return the number of hits found
	private void generateResultFromTemporary() {

		long targetStart = 0;
		long targetEnd = 0;
		long queryStart = 0;
		long queryEnd = 0;

		int qhits = 0;
		float variance = 0;

		DNASequence dNASeqQuery;
		DNASequence dNASeqTarget;

		
		//temporary results is a hashmap of vectors of alignment positions.
		// go through all of these to assemble the final result.
		for (Vector<AlignmentPosition> partialResults : temporaryResults
				.values()) {
			for (AlignmentPosition ap : partialResults) {
				targetStart = ap.getTargetStart();
				targetEnd = ap.getTargetEnd();
				queryStart = ap.getQueryStart();
				queryEnd = ap.getQueryEnd();

				qhits = ap.getNumberOfQHits();
				variance = ap.getVariance();

				dNASeqQuery = ap.getQuery();

				// get index of the target sequence where the start and the end
				// is.
				int firstTarget = qGramIndex
						.getSequenceNumberAtApproximatePosition((int) targetStart);
				int lastTarget = qGramIndex
						.getSequenceNumberAtApproximatePosition((int) targetEnd);

				// match is in only one target
				if (firstTarget == lastTarget) {
					dNASeqTarget = qGramIndex.getSequence(firstTarget);
					ap = new AlignmentPosition(dNASeqTarget, targetStart
							- dNASeqTarget.getOffset(), targetEnd
							- dNASeqTarget.getOffset(), dNASeqQuery,
							queryStart, queryEnd);

					if (computeMean) {
						ap.setVariance(variance);
					}
					ap.setNumberOfQHits(qhits);
					result.addAlignmentPosition(ap);
					// match spans over different targets
				} else {
					// -> split the matches

					// System.err.println(ap+" needs to be split in
					// "+(lastTarget-firstTarget+1)+" parts");

					long tmpQueryStart = queryStart;
					long tmpQueryEnd = queryEnd;
					long sliceLength = 0;
					boolean forwardMatch = queryStart <= queryEnd;

					// split the match in several slices
					for (int i = firstTarget; i <= lastTarget; i++) {
						dNASeqTarget = qGramIndex.getSequence(i);

						// determine slice length
						if (i == firstTarget) {
							long thisTargetEnd = dNASeqTarget.getOffset()
									+ dNASeqTarget.getSize() - 1;
							sliceLength = thisTargetEnd - targetStart;
						} else if (i == lastTarget) {
							sliceLength = targetEnd - dNASeqTarget.getOffset();
						} else { // target in between
							sliceLength = dNASeqTarget.getSize() - 1;
						}

						// determine endposition of this slice
						if (forwardMatch) {
							tmpQueryEnd = tmpQueryStart + sliceLength;
						} else {
							tmpQueryEnd = tmpQueryStart - sliceLength;
						}

						if (i == firstTarget) {
							ap = new AlignmentPosition(dNASeqTarget,
									targetStart - dNASeqTarget.getOffset(),
									dNASeqTarget.getSize() - 1, dNASeqQuery,
									tmpQueryStart, tmpQueryEnd);
						} else if (i == lastTarget) {
							ap = new AlignmentPosition(dNASeqTarget, 0,
									targetEnd - dNASeqTarget.getOffset(),
									dNASeqQuery, tmpQueryStart, tmpQueryEnd);
						} else { // target in between
							ap = new AlignmentPosition(dNASeqTarget, 0,
									dNASeqTarget.getSize() - 1, dNASeqQuery,
									tmpQueryStart, tmpQueryEnd);
						}

						// if the slice is bigger than q, add it
						if (ap.size() > qGramIndex.getQLength()) {
							if (computeMean) {
								ap.setVariance(variance);
							}
							// FIXME: set the number of qhits to the appropriate
							// fraction of that part
							ap.setNumberOfQHits(qhits);
							result.addAlignmentPosition(ap);
						}

						// System.err.println(" " + ap
						// +"\tqs:"+Math.abs(ap.getQueryEnd()-ap.getQueryStart())
						// +"\tts:"+ (ap.getTargetEnd()-ap.getTargetStart())
						// +" query:"+dNASeqTarget);

						// shift the new start to the end of the next
						if (forwardMatch) {
							tmpQueryStart = tmpQueryEnd + 1;
						} else {
							tmpQueryStart = tmpQueryEnd - 1;
						}

					}
				}

			}
		}
		
		//since all results have been added to results this can be cleared.
		// to avoid interferences for the next query this has to be cleared!
		temporaryResults.clear();
	}


	/**
	 * FIXME UNFINISHED!!
	 * Try to find the real target start and end position instead of the bucket positions
	 */
	private void adjustTargetCoordinates(AlignmentPosition ap) {
		QGramCoder targetCoordsCoder= new QGramCoder(qGramIndex.getQLength());
		//TODO implement this
		
		for (AlignmentPosition alignmPos : result) {
			coder.reset();
			int start=0;
			
			if (! alignmPos.isReverseHit()) {
				// do forward search
				start = (int) alignmPos.getQueryStart();

				code=-1;
				while (code==-1) {
					code = coder.updateEncoding(querySequencesArray[start++]);
					}
				if (code != -1) {
					for (int occOffset = hashTable[code]; occOffset < hashTable[code + 1]; occOffset++) {
						System.out.println(occurrenceTable[occOffset]);
					}
				}
			} else {
				// TODO: do backward search

			}
		}
		
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
		
		this.reportProgress( progress , "Processing: " 
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

					// debug - compare code and locations on target
//					System.out.println(code+" " + " " + i);
//					System.out.println(coder.decodeQgramCode(code));
//					for (int p=0; p<11;p++) {
//						System.out.print(qGramIndex.getCharFromInput(i+p));
//					}
//					System.out.println();

					
					//j is the hitposition in the query
					if (!reverseComplementDirection) { //forward
						j = relativeQueryPosition-eZone.getQGramSize();
					} else  {
						// for the reverse complement we have to consider the position of the reversed string.
						// otherwise the hitting diagonals are wrong
						j= startPos-endPos-relativeQueryPosition-eZone.getQGramSize()+1;
					}

					
					// //debug
//					if((bm>=190) && (bm<204)) {
//						log(String.format("%d %d %d %d %d\n",i,j,(j-i),d,bm));
//						}
						
					
					// Algorithm 2 page 303 in Rasmussen2006
					d = targetSize + j - i; // diagonal on the j-axis shifted by |Target| to obtain positive values
					b0 = d >> z; // b0= d/2^z
					bm = b0 % numberOfBins; // bucketindex
					updateBin(bm, i, j, (b0 << z));

					// if bins are overlapping:
					// (delta - 1 looks as bitstring like this: 0000..111111
					// logical 'and' gives  the remainder of a division by delta)
					if ((d & (delta - 1)) < e) { 
						//get previous bin
						bm = (bm + numberOfBins - 1) % numberOfBins; // avoid negative value if bm=0
						updateBin(bm, i, j, ((b0 - 1) << z));
					} //fi overlapping hit

				}// for each occurrence of this code

				
				//report and clean all parallelograms on the right end of the target
				//explanation see comment on checkAndResetBin method
				//short: the last possible j on the right side for that a hit is accounted to this bin.
				//
				// I'm not 100% sure if e has to be added in reverse complement direction.
				if ( ((j - e) % (delta - 1)) == 0) {
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
			}//end report hit if more than w away
	
			
			
			//====== start a new paralelogram and initialize mean and variance
				if (binCounts[b] == 0) {
					binMin[b] = hitPositionQuery;
		
					if (computeMean) {
						// initial values for mean and variance of the hit-diagonal
						// computation
						binMean[b] = hitPositionTarget - hitPositionQuery; // ==
						// diagonal
						binVariance[b] = 0; // variance for one sample is 0
		
						// adjust the mean if in reverse direction
						// diagonal for reverse complement is i + (|query|-j) == i-j
						// +|query|
						if (reverseComplementDirection) {
							binMean[b] += querySize;
						}
					}
				}// new parallelogram
					
					
	
			// if there was no hit on the same query position
			// this avoids the counting of multiple hits on a homopolymer region, for example "aaaaaaaaaa"
			if (binMax[b] < hitPositionQuery) {
				//--------------main algorithm------
				// enlarge the parallelogram
				binMax[b] = hitPositionQuery;
				//increase the q-hit counter for this parallelogram
				binCounts[b]++;
				//------
				
				
			//********* it seems to take longer if here is an additional if clause!
			//********* if this is commented out the matching of one special file takes 16 seconds
			//********* if computeMean is checked it takes 25 seconds
			//********* and if the mean is computed it takes 22 seconds!!
				//TODO find a good way to make this available
//				if(computeMean) {
//				// mean and variance extension
//				//compute the mean and the variance of the matching diagonals recursively
//				if(binCounts[b]>=1) {
//					int diagonal= hitPositionTarget-hitPositionQuery;
//					
//					//adjust the mean if in reverse direction
//					// diagonal for reverse complement is i + (|query|-j) == i-j +|query|
//					if(reverseComplementDirection) {
//						diagonal+=querySize;
//					}
//					
//
//					//TODO sometimes the diagonal of a hit position is quite far away from the mean.
//					// investigate this
////if (Math.abs(binMean[b]-diagonal)>2*eZone.getDelta()) {
////	this.log(String.format("%d %d %d %d %d %f %s\n",hitPositionTarget,hitPositionQuery,offsetDiagonal,diagonal,b,binMean[b],reverseComplementDirection));
////}
//
//					
//				
//
//						//compute the mean and the variance for the diagonal values in a recursive fashion
//						//(see Musterklassifikation Skript 2006/07 Uni Bi.; Franz Kummert; Page 18
//						double factor= 1. / binCounts[b];
//						
//						//first update the variance because it depends on the mean value of the previous step
//						binVariance[b] = (float) ((1. - factor) * (binVariance[b] + factor
//								* ((diagonal - binMean[b]) * (diagonal - binMean[b]))));
//						binMean[b] = (float) ((1. - factor) * binMean[b] + factor
//								* diagonal);
//					
//					} // mean and variance computation
//				}
//				
//				
//				
				
			} // enlargement of one paralellogram
		}

	/**
	 * Since only |Targetsize|/delta (roughly) bins are used we have to take care that the ones which are shared
	 * do not produce hits which go through both areas.<br>
	 * <code>
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
	 * </code>
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

	/**
	 * After each query the bincounts are reseted and it will be checked if there are
	 * any remaining high scoring parallelograms which have not been reported yet.
	 */
	private void resetCountsAndReportRemainingParalellograms() {
		int top=0;
		int bottom=0;
		int left=0;
		int querySize = (int)query.getSequence(queryNumber).getSize();
		int border=(querySize/eZone.getDelta())-1;
		int borderoffset=qGramIndex.getInputLength();
	
		
		//check parallelograms for hits and reset counters
		for (int bin = 0; bin < binCounts.length; bin++) {
			//check if there are remaining parallelograms that have not been reported yet
			if (binCounts[bin] >= eZone.getThreshold()) {
				top = binMax[bin]+eZone.getQGramSize();
				bottom = binMin[bin];
				
				if (top-bottom > eZone.getWidth()) {//normally height

					/*
					 * the calculation from bin index to left value should be right, because all diagonals
					 * of the area in x (where  j>i) are projected to x' with the modulo |Bins| operation.
					 * all diagonals in area y, which have the same bin index like the ones in x,
					 * are reported with the function checkAndResetBin, since they can not be extended.
					 * So in the end, when j=|B| only the diagonals from area z and x remain.
					 * 
					 * 
					 * |\ d=|A|+j-i   0
					 * | \            -d
					 * |  \           -2d
					 * -|B|\          -|B|/d
					 * |\   \         |A|- n*d 
					 * | \   \
					 *_|__\___\A i
					 * |\  \  |\
					 * | \  \y| \
					 * | x\ z\|x'\
					 * B
					 * j
					 */
					//this is for area x
					left = -(bin << eZone.getDeltaExponent());
					if (bin>border) {
						// and for area y |A|+|B| has to be added
						left += borderoffset;
					}

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
			
			if(computeMean) {
			binMean[bin] = 0;
			binVariance[bin] = 0;
			}
		}
	}
	
	
	/**
	 * This remembers the matches of the filtering phase and adds them to an AlignmentPositionsList object.
	 * If a match overlaps with the target boundaries it will be split in two (or several) matches.
	 * If the same match is reported twice this will be filtered.
	 * 
	 * @param left was used by Kim. Here it is not needed, I take the computed mean value.
	 * @param top highest index of the match in the query
	 * @param bottom lowest index of the match in the query
	 * @param bucketindex the bucketindex where the qgrams were counted
	 * @param debugstring string which states in which method the match was reported.
	 * either the normal way via updateBin(), checkAndResetBin() or resetCountsAndReportRemainingParalellograms().
	 * this is only used for debugging.
	 */
	
	private void reportMatch(int left, int top, int bottom, int bucketindex, String debugstring) {
		//debugging	
//		System.out.println(debugstring
//				+": bin:"+bucketindex
//				+"  left:"+left
//				+ " top:" +  top 
//				+ " bottom:" + bottom 
//				+ " (" + (top-bottom)+")"
//				+ " hits:" + binCounts[bucketindex]
////				+ " Mean:" + binMean[bucketindex]
////				+ " (" + (left-binMean[bucketindex])+ ") "
////				+" Variance:"+binVariance[bucketindex]
//				                          );
				
		AlignmentPosition ap;
		long targetStart=0;
		long targetEnd=0;
		long queryStart=0;
		long queryEnd=0;

		
		if (!reverseComplementDirection) {
			// normal hit; go from left to the hit position
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
			/* reverse complement hit; go from right to the hit position
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
		

		
		
		// get the sequence objects - global variable queryNumber has to be set!!
		DNASequence dNASeqQuery = query.getSequence(queryNumber);

		
		DNASequence dNASeqTarget=qGramIndex.getSequenceAtApproximatePosition((int)targetStart);
		
		
		
		ap = new AlignmentPosition(dNASeqTarget, targetStart, targetEnd,
				dNASeqQuery, queryStart, queryEnd);
		// due to the overlapping parallelograms some hits are recognised twice
		// save match temporary to sort out duplicate matches

		ap.setNumberOfQHits(binCounts[bucketindex]);
		if (computeMean) {
			ap.setVariance(binVariance[bucketindex]);
		}

		
		
		// check if a too similar hit (i.e. overlapping) was already added and
		// merge these if necessary.
		boolean alreadyAdded = false;

		Vector<AlignmentPosition> sameBucket = new Vector<AlignmentPosition>();
		AlignmentPosition existing;

		// check if there are matches belonging to bucket +1
		if (temporaryResults.containsKey(bucketindex + 1)) {
			// if so go through all and check if they are "the same" (i.e.
			// overlapping)
			sameBucket = temporaryResults.get(bucketindex + 1);
			for (int i = 0; i < sameBucket.size(); i++) {
				existing = sameBucket.get(i);
				alreadyAdded = checkForSameMatch(ap, existing);

				if (alreadyAdded) {
					// if they are overlapping, extend the one stored
					existing = extendAlignmentPosition(existing, ap);
					temporaryResults.get(bucketindex + 1).set(i, existing);
					break;
				}
			}
			// do the same for -1
			// check if there are matches belonging to bucket -1
		} else if (temporaryResults.containsKey(bucketindex - 1)) {
			sameBucket = temporaryResults.get(bucketindex - 1);
			// if so go through all and check if they are "the same" (i.e.
			// overlapping)
			for (int i = 0; i < sameBucket.size(); i++) {
				existing = sameBucket.get(i);
				alreadyAdded = checkForSameMatch(ap, existing);

				if (alreadyAdded) {
					// if they are overlapping, extend the one stored
					existing = extendAlignmentPosition(existing, ap);
					temporaryResults.get(bucketindex - 1).set(i, existing);
					break;
				}
			}
		}

		if (!alreadyAdded) {
			// if the match was not already added, put it into the temporary
			// results
			if (!temporaryResults.containsKey(bucketindex)) {
				temporaryResults.put(bucketindex,
						new Vector<AlignmentPosition>());
			}
			temporaryResults.get(bucketindex).add(ap);

		}

			
	}
	

	private AlignmentPosition extendAlignmentPosition(
			AlignmentPosition existing, AlignmentPosition ap) {

		long queryStart = 0;
		long queryEnd = 0;
		long targetStart = 0;
		long targetEnd = 0;
		int qhits = 0;
		float variance = 0;

		
		if (!existing.isReverseHit()) {
			if(existing.getQueryStart()<=ap.getQueryStart()) {
				queryStart=existing.getQueryStart();
				targetStart=existing.getTargetStart();
			} else {
				queryStart=ap.getQueryStart();
				targetStart=ap.getTargetStart();
			}
			if(existing.getQueryEnd()>=ap.getQueryEnd()) {
				queryEnd=existing.getQueryEnd();
				targetEnd=existing.getTargetEnd();
			} else {
				queryEnd=ap.getQueryEnd();
				targetEnd=ap.getTargetEnd();
			}
		} else {
			if(existing.getQueryStart()>=ap.getQueryStart()) {
				queryStart=existing.getQueryStart();
				targetStart=existing.getTargetStart();
			} else {
				queryStart=ap.getQueryStart();
				targetStart=ap.getTargetStart();
			}
			if(existing.getQueryEnd()<=ap.getQueryEnd()) {
				queryEnd=existing.getQueryEnd();
				targetEnd=existing.getTargetEnd();
			} else {
				queryEnd=ap.getQueryEnd();
				targetEnd=ap.getTargetEnd();
			}
		}
		
		
		
		AlignmentPosition merged = new AlignmentPosition(existing.getTarget(),targetStart,targetEnd,existing.getQuery(),queryStart,queryEnd);
		//todo find better. weighted by length
		merged.setNumberOfQHits(Math.max(existing.getNumberOfQHits(), ap.getNumberOfQHits()));
		
		if(computeMean) {
			merged.setVariance((float)((existing.getVariance()+ap.getVariance())/2.));
		}
		
		
		System.out.println("Merge:\n "+ existing +"\n+"+ ap + "\n=" + merged);

		return merged;
	}


	/**
	 * Checks if the two alignment positions are basically the same
	 * @param a first
	 * @param b second match
	 * @return if they are overlapping and thus the same, or not.
	 */
	private boolean checkForSameMatch(AlignmentPosition a, AlignmentPosition b) {

		//Sanity check; hits are in same direction
		if ((!a.isReverseHit() && b.isReverseHit())
				|| (a.isReverseHit() && !b.isReverseHit())) {
			System.out.println(a+" "+b +" are NOT the same, matches on opposite strand");
			return false;
		}

		boolean reversed = a.isReverseHit();
		
		long aDiagonal = a.getTargetStart();
		long bDiagonal = b.getTargetStart();
		
		if (!reversed) {
			aDiagonal -= a.getQuerySmallerIndex();
			bDiagonal -= b.getQuerySmallerIndex();
		} else {
			aDiagonal += a.getQuerySmallerIndex();
			bDiagonal += b.getQuerySmallerIndex();
		}
			
		
//		Sanity check: same diagonal
		if (	Math.abs(aDiagonal-bDiagonal) > eZone.getDelta() ) {
			System.out.println(a+" "+b +" are NOT the same, too far away: |" +
					aDiagonal + " - " + bDiagonal + "|=" + Math.abs(aDiagonal-bDiagonal));
					return false;
		}
		

		
		//make sure that a is always the first match
		if (a.getQuerySmallerIndex() > b.getQuerySmallerIndex()) {
			AlignmentPosition tmp = a;
			a = b;
			b=tmp;
		}
		
		if (a.getQueryLargerIndex() < b.getQuerySmallerIndex()) {
			System.out.println(a+" "+b +" are NOT the same, they overlap");
			return false;
			
			
		}

		
		System.out.println(a+" "+b +" are the same");
		return true;

	}
	
	/**
	 * Registers the Match Dialog, to pass progress changes to it.
	 * 
	 * @param matchDialog
	 */
	public void register(MatchDialog matchDialog) {
		this.matchDialog=matchDialog;
	}
	
	/**
	 * If a MatchDialog is registered the output will be written in its textfield.
	 * Otherwise on the command line.
	 * 
	 * @param percentDone how far are we?
	 * @param s explaining sentence
	 */
	public void reportProgress(double percentDone, String s) {
		if (matchDialog != null) {
			matchDialog.setProgress(percentDone, s);
		} else {
			System.out.println(
					(percentDone>0?
							((int)(percentDone*100)+ "% ")
							:"") 
							+ s);
		}
	}
	
}
