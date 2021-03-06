/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
 *   phuseman a t  cebitec.uni-bielefeld.de                                *
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

package de.bielefeld.uni.cebitec.qgram;

import java.io.IOException;
import java.util.Vector;

import de.bielefeld.uni.cebitec.common.AbstractProgressReporter;
import de.bielefeld.uni.cebitec.common.Timer;

/**
 * @author phuseman
 * 
 */
public class QGramIndex {
	private final int qLength = 11;

	private int[] hashTable;
	private int[] occurrenceTable;

	private Vector<DNASequence> sequences;
	private int[] offsetsInInput;
	private int inputLength;

	private FastaFileReader fastaFileReader;

	private boolean indexGenerated = false;

	private char[] input = null; //TODO make local in generateIndex if not needed otherwise
	private QGramCoder coder;

	private AbstractProgressReporter progressReporter;

	public QGramIndex() {
		coder = new QGramCoder(qLength);
	}

	
	/**
	 * Gives the heap memory consumption in MByte. Does not call the garbage collector before!
	 * @return
	 */
	public static int getHeapMemConsumption() {
		return (int) Math.ceil((
						(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
						/ (1024.*1024.)));
	}

	
	/**
	 * Generates a q-gram index. In the end you'll have one array (hashTable)
	 * with pointers for each q-gram to another array (occurrencesTable) where
	 * the positions are stored, where the q-gram occurs.<br>
	 * The positions of each q-gram are then accessible with the q-gram code<br>
	 * The generation is done in two steps: First each q-gram is counted. By
	 * this also the number of valid q-grams is aquired.<br>
	 * In a second run the occurrenceTable is filled. The q-Gram codes are
	 * cached, to be used again in the second step. <br>
	 * 
	 * <br>Memory consumption<br>
	 * Besides the memory of the string to be indexed (n bytes) 4xn bytes will be occupied for the
	 * occurrenceTable. 16 MByte will be used for the hashIndex, as well as the tmparray.
	 * Another 4 x n Bytes will be used for the code cache. This could be avoided but it would make
	 * the indexing slower.<br>Together: 9 n Bytes + 32 Mbyte
	 * 
	 */
	public void generateIndex(FastaFileReader fastaFileReader) {
		try {
			input = fastaFileReader.getCharArray();
			offsetsInInput = fastaFileReader.getOffsetsArray();
			sequences = fastaFileReader.getSequences();
			inputLength = input.length;

		} catch (IOException e) {
			System.err.println("Error reading Fasta file:"
					+ fastaFileReader.getSource());
			e.printStackTrace();
		}

		// for q=11 this occupies 16 MByte
		hashTable = new int[coder.numberOfPossibleQGrams()+1];

		// cache the sucessivly computed codes.
		// so that they can be reused on the second run.
		//!!this costs 4 Bytes per input character!!
		//on the other hand this saves time.
		int[] codecache = new int[input.length];
		

		Timer t = Timer.getInstance();
		// computeBucketBoundaries

		t.startTimer();

		int code = 0;

		// in offsetsInInput the offsets of the sequences are stored.
		// for convenience in offsetsInInput[n+1] the overall length is stored
		// so that
		// it is easy to iterate through the sequences.
		// after each sequence the coder has to be resetted.
		for (int j = 0; j < offsetsInInput.length - 1; j++) {
			this.reportProgress(0, "Indexing Sequence: "
					+ fastaFileReader.getSequence(j).getId() + " ("
					+ fastaFileReader.getSequence(j).getSize() + ")" + " from "
					+ offsetsInInput[j] + " to " + (offsetsInInput[j + 1] - 1));
			coder.reset();

			// encode each q-gram for one sequence
			for (int i = offsetsInInput[j]; i < offsetsInInput[j + 1]; i++) {
				code = coder.updateEncoding(input[i]);
				/*	if(offsetsInInput[j]==0){
					System.out.print(code);
					System.out.println(coder.decodeQgramCode(code));
				}*/
				if (code >= 0) {// if code is valid
					// count the q-grams
					hashTable[code]++;
				}
				/*if(offsetsInInput[j]==0){
					System.out.print(code);
					System.out.println(coder.decodeQgramCode(code));
				}*/
			
				assert(code >= -1);

				// cache the code
				codecache[i] = code;
			}
		
		}
		
		this.reportProgress(0, "counting the qgrams took:" + t.restartTimer());
		this.reportProgress(0, "current heap memory usage: " + getHeapMemConsumption() + " MB");
		
		
		int offset = 0;
		int lastValue = 0;

		// tmparray is used to fill the occurrences table
		//occupies 16 MByte heap memory
		int[] tmparray = new int[hashTable.length];

		// compute the pointers for the hashTable
		for (int i = 0; i < hashTable.length; i++) {
			if (hashTable[i] >= 0) {
				lastValue = hashTable[i];
				hashTable[i] = offset;
				offset += lastValue;
			} else {
				hashTable[i] = lastValue;
			}

			// make a copy for the later step of filling the occurrences table
			tmparray[i] = hashTable[i];
		}

		this.reportProgress(0, "accumulation and copying took:"
				+ t.restartTimer());
		this.reportProgress(0, "current heap memory usage: " + getHeapMemConsumption() + " MB");
		
		
		// approximately 4bytes per input character
		occurrenceTable = new int[offset];

		// collectQGramIndices
		coder.reset();
		code = 0;

		for (int j = 0; j < offsetsInInput.length - 1; j++) {

			for (int i = offsetsInInput[j]; i < offsetsInInput[j + 1]; i++) {
				
				if (codecache[i] >= 0) {
					// in tmparray the index for a q-gram is stored and
					// incremented
					// if we add a position.
					// codecache contains the integer codes for all qgrams.
					// the position stored is *not* relative to the actual sequence.
					//all sequences are concatenated together
					occurrenceTable[tmparray[codecache[i]]++] = i
							 - getQLength() + 1;
					// minus q to get the starting index of the q-gram instead
					// of the end position
				}
			}
		}
	
		tmparray=null;
                codecache=null;

		this.reportProgress(0, "filling in the qgram occurrences took:"
				+ t.stopTimer());
		this.reportProgress(0, "current heap memory usage: " + getHeapMemConsumption() + " MB");
		indexGenerated = true;
	}
	/* public void getQGramPositions(int code) {
	 if (! indexGenerated) {
	 this.generateIndex();
	 }
			
	 System.out.println("code:" + code + " -> "
	 + coder.decodeQgramCode(code));
	 for (int i = hashTable[code] - 1; i < hashTable[code + 1] + 1; i++) {
	 System.out.print("pos:" + occurrenceTable[i] + " ");
	
	for (int j = 0; j < 11; j++) {
	System.out.print(input[occurrenceTable[i] -10 + j]);
	 }
	
		System.out.println();
	 }
	 //public void getQGramPositions(int code) {
	 //if (! indexGenerated) {
	 //this.generateIndex();
	 //}
			
	 //System.out.println("code:" + code + " -> "
	 //+ coder.decodeQgramCode(code));
	 //for (int i = hashTable[code] - 1; i < hashTable[code + 1] + 1; i++) {
	 //System.out.print("pos:" + occurrenceTable[i] + " ");
	
	// for (int j = 0; j < 11; j++) {
	 //System.out.print(input[occurrenceTable[i] -10 + j]);
	//}
	
	//System.out.println();
	 //}
	
	 //}

	 }*/

	public char getCharFromInput(int pos) {
		return input[pos];
	}

	public int[] getHashTable() {
		return hashTable;
	}

	public int[] getOccurrenceTable() {
		return occurrenceTable;
	}

	public boolean isIndexGenerated() {
		return indexGenerated;
	}

	public int getQLength() {
		return qLength;
	}

	public int getInputLength() {
		return inputLength;
	}

	public Vector<DNASequence> getSequences() {
		return sequences;
	}
	
	public DNASequence getSequence( int i) {
		return sequences.get(i);
	}

	/**
	 * Returns the Sequence object at a given position for which the index was
	 * built.<br>
	 * If no sequence matches the method returns null.
	 * 
	 * @param position
	 *            Position of a character
	 * @return Sequence object
	 */
	public DNASequence getSequenceAtPosition(int position) {

		for (int j = 0; j < offsetsInInput.length - 1; j++) {
			if (position >= offsetsInInput[j]
					&& position < offsetsInInput[j + 1]) {
				return sequences.get(j);
			}
		}

		return null;
	}
	
	public int getSequenceNumberAtPosition(int position) {

		for (int j = 0; j < offsetsInInput.length - 1; j++) {
			if (position >= offsetsInInput[j]
					&& position < offsetsInInput[j + 1]) {
				return j;
			}
		}

		return -1;
	}

	/**
	 * Same as getSequenceAtPosition(), but if the position is too large or too
	 * small then it will return the last/first sequence.
	 * 
	 * @param position
	 * @return
	 */
	public DNASequence getSequenceAtApproximatePosition(int position) {
		DNASequence out = getSequenceAtPosition(position);
		if (out == null) {
			if (position <= 0) {
				out = sequences.get(0);
			} else if (position >= inputLength) {
				out = sequences.get(sequences.size() - 1);
			}
		}
		return out;
	}
	
	public int getSequenceNumberAtApproximatePosition(int position) {
		int out = getSequenceNumberAtPosition(position);
		if (out == -1) {
			if (position <= 0) {
				out = 0;
			} else if (position >= inputLength) {
				out = sequences.size() - 1;
			}
		}
		return out;
	}

	public int[] getOffsetsInInput() {
		return offsetsInInput;
	}

	/**
	 * Registers the a progress reporter, to pass progress changes to it.
	 * 
	 * @param matchDialog
	 */
	public void register(AbstractProgressReporter progressReporter) {
		this.progressReporter=progressReporter;
	}
	
	/**
	 * If a progress reporter is registered progress changes are shown with is.
	 * @param percentDone how far are we?
	 * @param s explaining sentence
	 */
	public void reportProgress(double percentDone, String s) {
		if (progressReporter != null) {
			progressReporter.reportProgress(percentDone, s);
		}

	}
}
