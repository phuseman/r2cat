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

import java.io.IOException;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.gui.MatchDialog;
import de.bielefeld.uni.cebitec.cav.utils.Timer;

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

	private char[] input = null; //FIXME make local in generateIndex if not needed otherwise
	private QGramCoder coder;

	private MatchDialog matchDialog;

	public QGramIndex() {
		coder = new QGramCoder(qLength);
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

		hashTable = new int[coder.numberOfPossibleQGrams()+1];

		// cache the sucessivly computed codes.
		// so that they can be reused on the second run.
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
				if (code >= 0) {// if code is valid
					// count the q-grams
					hashTable[code]++;
				}
				// cache the code
				codecache[i] = code;
			}
		}

		this.reportProgress(0, "counting the qgrams took:" + t.restartTimer());

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

		this.reportProgress(0, "accumulation and copying took:"
				+ t.restartTimer());

		occurrenceTable = new int[offset];

		// collectQGramIndices
		coder.reset();
		code = 0;

		for (int j = 0; j < offsetsInInput.length - 1; j++) {

			for (int i = offsetsInInput[j]; i < offsetsInInput[j + 1]; i++) {
				
				if (codecache[i] >= 0) {
					// in tmparray the index for a q-gram ist stored and
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
		this.reportProgress(0, "filling in the qgram occurrences took:"
				+ t.restartTimer());
		indexGenerated = true;
	}

	// public void getQGramPositions(int code) {
	// if (! indexGenerated) {
	// this.generateIndex();
	// }
	//		
	// System.out.println("code:" + code + " -> "
	// + coder.decodeQgramCode(code));
	// for (int i = hashTable[code] - 1; i < hashTable[code + 1] + 1; i++) {
	// System.out.print("pos:" + occurrenceTable[i] + " ");
	//
	// for (int j = 0; j < 11; j++) {
	// System.out.print(input[occurrenceTable[i] - 10 + j]);
	// }
	//
	// System.out.println();
	// }
	//
	// }

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
				out = sequences.get(offsetsInInput.length - 1);
			}
		}
		return out;
	}

	public int[] getOffsetsInInput() {
		return offsetsInInput;
	}

	/**
	 * Registers the Match Dialog, to pass progress changes to it.
	 * 
	 * @param matchDialog
	 */
	public void register(MatchDialog matchDialog) {
		this.matchDialog = matchDialog;
	}

	/**
	 * If a MatchDialog is registered the output will be written in its
	 * textfield. Otherwise on the command line.
	 * 
	 * @param percentDone
	 *            how far are we?
	 * @param s
	 *            explaining sentence
	 */
	public void reportProgress(double percentDone, String s) {
		if (matchDialog != null) {
			matchDialog.setProgress(percentDone, s);
		} else {
			System.out
					.println((percentDone > 0 ? ((int) (percentDone * 100) + "% ")
							: "")
							+ s);
		}
	}

}
