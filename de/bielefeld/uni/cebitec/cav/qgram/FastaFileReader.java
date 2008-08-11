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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;

/**
 * @author phuseman
 * 
 */
public class FastaFileReader {
	private boolean initialized = false;

	private File source = null;
	private Vector<DNASequence> sequences;

	private char[] chararray;
	private HashMap<String, Integer> offsetsInCharArray;

	public FastaFileReader(File input) {
		this.source = input;
		sequences = new Vector<DNASequence>();
	}

	public void scanContents(boolean createCharArray) throws IOException {
		BufferedReader in = null;
		int character = 0;
		long offsetInFile = 0;

		String lastSequenceId = "";
		String lastSequenceDescription = "";
		long lastSequenceLength = 0;
		long lastOffsetInFile = 0;

		String line = "";
		String identLine = "";
		long validCharCounter = 0;
		long lastValidCharCounter = 0;
		boolean firstSequence = true;
		StringBuilder sb = new StringBuilder();
		offsetsInCharArray = new HashMap<String, Integer>();
		try {
			in = new BufferedReader(new FileReader(source), 1024 * 16);

			if (createCharArray) {
				sb = new StringBuilder((int) source.length());
			}

			nextchar: while ((character = in.read()) != -1) {
				offsetInFile++;

				// remove lines with comments
				if (character == (int) '#') {
					String comment = in.readLine();
					offsetInFile += comment.length();
					continue nextchar;
				}

				// read id and description
				if (character == (int) '>') {
					identLine = in.readLine();
					offsetInFile += identLine.length();
					lastOffsetInFile = offsetInFile;
					// TODO: offset in file is buggy

					if (!firstSequence) {
						lastSequenceLength = validCharCounter
								- lastValidCharCounter;
//						System.out.println(" lastSequenceLength="
//								+ lastSequenceLength);

						sequences.add(new DNASequence(source, lastSequenceId,
								lastSequenceDescription, lastSequenceLength,
								lastOffsetInFile));
					} else {
						firstSequence = false;
					}
					lastValidCharCounter = validCharCounter;

					int idCommentBoundary = 0;

					// separate id and description if possible
					if ((idCommentBoundary = identLine.indexOf((int) ' ', 1)) >= 0) {

						lastSequenceId = identLine.substring(0,
								idCommentBoundary);
						lastSequenceDescription = identLine
								.substring(idCommentBoundary + 1);

					} else {
						lastSequenceId = identLine;
						lastSequenceDescription = "";

					} // end: id/description separation

					if (createCharArray) {
						// remember starting positions in the char array
						// the length of the stringbuffer is the same as the
						// offset of the next sequence
						offsetsInCharArray.put(lastSequenceId, sb.length());
					}

				}// end: read id line
				else if (Character.isLetter((char) character)) {
					// count valid characters / sequence characters
					validCharCounter++;
					if (createCharArray) {
						sb.append((char) character);
					}

					// line = in.readLine();
					// offsetInFile += line.length();
					// sb.append(line.trim());
					// continue nextchar;
				}

			} // end: read the whole file

			if (createCharArray) {
				sb.trimToSize();
				chararray = sb.toString().toCharArray();
			}

			lastSequenceLength = validCharCounter - lastValidCharCounter;
//			System.out.println(" lastSequenceLength=" + lastSequenceLength);

			// add last sequence
			sequences.add(new DNASequence(source, lastSequenceId,
					lastSequenceDescription, lastSequenceLength,
					lastOffsetInFile));

			// set the offset of each sequence object
			for (DNASequence seq : sequences) {
				// key is id, value is offset in a contiguous sequence
				long offset = offsetsInCharArray.get(seq.getId());
				seq.setOffset(offset);
			}

			initialized = true;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Gives all dna sequences of a fasta file as a char array. there are no
	 * separators between sequences. The method getOffsetsArray() gives the
	 * bounds.
	 * 
	 * @return char array containing all dna sequences of the fasta file
	 * @throws IOException
	 */
	public char[] getCharArray() throws IOException {
		if (!initialized || chararray == null) {
			this.scanContents(true);
		}
		return chararray;
	}
	
	
	/**
	 * Gives the bounds of the sequences in the char array from getCharArray().
	 * For convenience the last entry is the size of the char array. This way it
	 * is possible to iterate through all the sequences like <code>
	for (int j = 0; j < offsetsInInput.length - 1; j++) {
		for (int i = offsetsInInput[j]; i < offsetsInInput[j + 1]; i++) {
			chararray[i] ...
		}
	}
	 *  </code>
	 * 
	 * @return the bounds of all sequences in the char array
	 * @throws IOException
	 */
	public int[] getOffsetsArray() throws IOException {
		if (!initialized || chararray == null) {
			this.scanContents(true);
		}

		int[] out = new int[offsetsInCharArray.size() + 1];

		for (int i = 0; i < sequences.size(); i++) {
			out[i] = (int) sequences.get(i).getOffset();
		}

		out[sequences.size()] = chararray.length;

		return out;
	}

	public DNASequence getSequence(int index) {
		return sequences.get(index);
	}

	public File getSource() {
		return source;
	}

	public Vector<DNASequence> getSequences() {
		return sequences;
	}

}
