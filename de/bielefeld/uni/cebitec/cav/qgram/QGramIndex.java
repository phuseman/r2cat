/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
 *   phuseman ät cebitec.uni-bielefeld.de                                     *
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

import de.bielefeld.uni.cebitec.cav.utils.LogTimer;

/**
 * @author phuseman
 * 
 */
public class QGramIndex {
	private int[] hashTable;
	private int[] occurrenceTable;
	private char[] input;
	private int[] offsetsInInput;
	private FastaStreamReader fastaStreamReader;

	private QGramCoder coder;

	public QGramIndex(FastaStreamReader sequence) {
		this.fastaStreamReader = sequence;
		this.input = fastaStreamReader.getCharArray();
		this.offsetsInInput = fastaStreamReader.getOffsetsArray();
		coder = new QGramCoder(11);

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
	 */
	public void generateIndex() {
		hashTable = new int[coder.numberOfPossibleQGrams()];

		// cache the sucessivly computed codes.
		// so that they can be reused on the second run.
		int[] codecache = new int[input.length];

		LogTimer t = LogTimer.getInstance();
		// computeBucketBoundaries

		t.startTimer();

		int code = 0;

		// in offsetsInInput the offsets of the sequences are stored.
		// for convenience in offsetsInInput[n+1] the overall length is stored
		// so that
		// it is easy to iterate through the sequences.
		// after each sequence the coder has to be resetted.
		for (int j = 0; j < offsetsInInput.length - 1; j++) {
			System.out.println("Processing "
					+ fastaStreamReader.getSequence(j).getId() 
					+ " ("
					+ fastaStreamReader.getSequence(j).getDescription()
					+ ")"
//					+ " from " + offsetsInInput[j] + " to "
//					+ (offsetsInInput[j+1]-1)
//					+ " ("
//					+ (offsetsInInput[j+1]-offsetsInInput[j])
//					+ ")"
			);
			coder.reset();

			// encode each q-gram for one sequence
			for (int i = offsetsInInput[j]; i < offsetsInInput[j + 1]; i++) {
				code = coder.updateEncoding(input[i]);
				if (code >= 0) {
					// count the q-grams
					hashTable[code]++;
				}
				// cache the code
				codecache[i] = code;
			}
		}

		t.stopTimer("counting");
		t.startTimer();

		int offset = 0;
		int lastValue = 0;

		// tmparray is used to fill the occurrences table
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

		t.stopTimer("accumulation and copying");
		t.startTimer();

		occurrenceTable = new int[offset];

		// collectQGramIndices
		coder.reset();
		code = 0;

		for (int j = 0; j < offsetsInInput.length - 1; j++) {

			for (int i = offsetsInInput[j]; i < offsetsInInput[j + 1]; i++) {
				// code = coder.updateEncoding(input[i]);
				// if (code >=0 ) {
				// qgramIndices[tmparray[code]++]=i;
				// }
				if (codecache[i] >= 0) {
					// in tmparray the index for a q-gram ist stored and
					// incremented
					// if we add a position.
					// codecache contains the integer codes for all qgrams.
					// the position stored is relative to the actual sequence.
					occurrenceTable[tmparray[codecache[i]]++] = i
							- offsetsInInput[j];
				}
			}
		}

		t.stopTimer("indexpositions");

	}

	public void getQGramPositions(int code) {
		System.out.println("code:" + code + " -> "
				+ coder.decodeQgramCode(code));
		for (int i = hashTable[code] - 1; i < hashTable[code + 1] + 1; i++) {
			System.out.print("pos:" + occurrenceTable[i] + " ");

			for (int j = 0; j < 11; j++) {
				System.out.print(input[occurrenceTable[i] - 10 + j]);
			}

			System.out.println();
		}

	}

}