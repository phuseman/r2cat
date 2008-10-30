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
package de.bielefeld.uni.cebitec.cav.qgram;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

	/**
	 * Scans the contents of the file. Returns if a line ">id" was present
	 * @param createCharArray
	 * @return boolean Id's were found
	 * @throws IOException
	 */
	public boolean scanContents(boolean createCharArray) throws IOException {
		BufferedReader in = null;
		int character = 0;

		String lastSequenceId = "";
		String lastSequenceDescription = "";
		long lastSequenceLength = 0;

		String identLine = "";
		long validCharCounter = 0;
		long lastValidCharCounter = 0;
		boolean firstSequence = true;
		boolean idsFound=false;
		
		StringBuilder sb = new StringBuilder();
		offsetsInCharArray = new HashMap<String, Integer>();
		try {
			in = new BufferedReader(new FileReader(source), 1024 * 16);

			if (createCharArray) {
				sb = new StringBuilder((int) source.length());
			}

			nextchar: while ((character = in.read()) != -1) {

				// remove lines with comments
				if (character == (int) '#') {
					String comment = in.readLine();
					continue nextchar;
				}

				// read id and description
				if (character == (int) '>') {
					identLine = in.readLine();
					
					if(identLine.matches("^.+$")) {
						idsFound=true;
					}

					if (!firstSequence) {
						lastSequenceLength = validCharCounter
								- lastValidCharCounter;
//						System.out.println(" lastSequenceLength="
//								+ lastSequenceLength);

						sequences.add(new DNASequence(source, lastSequenceId,
								lastSequenceDescription, lastSequenceLength));
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

			if (idsFound) {
			if (createCharArray) {
				sb.trimToSize();
				chararray = sb.toString().toCharArray();
			}

			lastSequenceLength = validCharCounter - lastValidCharCounter;
//			System.out.println(" lastSequenceLength=" + lastSequenceLength);

			// add last sequence
			sequences.add(new DNASequence(source, lastSequenceId,
					lastSequenceDescription, lastSequenceLength));

			// set the offset of each sequence object
			for (DNASequence seq : sequences) {
				// key is id, value is offset in a contiguous sequence
				long offset = offsetsInCharArray.get(seq.getId());
				seq.setOffset(offset);
			}

			initialized = true;
			}
			
			
			
			
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		return idsFound;
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
		checkInitialisation();
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
		checkInitialisation();

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

	
	/**
	 * Writes out the sequence of this fasta file which matches the given id.
	 * @param id identifier of the fastasequence to write
	 * @param output writer to put the sequence in.
	 * @return true if the operation was successful
	 * @throws IOException
	 */
	public boolean writeSequence(String id, BufferedWriter output) throws IOException {
		checkInitialisation();
		//go through all sequences and find the one with the same id
		for (int i = 0; i < sequences.size(); i++) {
			if(sequences.get(i).getId().matches(id)) {
				output.write(chararray, (int)sequences.get(i).getOffset(), (int)sequences.get(i).getSize());
				output.write('\n');
				return true;
			}
		}

		return false;
	}
	

	/**
	 * Writes the reverse complement of sequence id to a file. there are no newlines etc.
	 * @param id identifier of the sequence
	 * @param output output writer
	 * @return failure or success
	 * @throws IOException
	 */
	public boolean writeReverseComplementSequence(String id, BufferedWriter output) throws IOException {
		checkInitialisation();
		//go through all sequences and find the one with the same id
		for (int i = 0; i < sequences.size(); i++) {
			if(sequences.get(i).getId().matches(id)) {
				
				int length=(int)sequences.get(i).getSize();
				int offset=(int)sequences.get(i).getOffset();
				
				//translation map
				char[] alphabetMap= new char[256];
				
				
				// all characters other than a t c g are not changed. 
				for (int j = 0; j < alphabetMap.length; j++) {
					alphabetMap[j]= (char) j;
				}
				// a t c g are mapped to the reverse complement
				alphabetMap['a']='t';
				alphabetMap['A']='T';
				alphabetMap['c']='g';
				alphabetMap['C']='G';
				alphabetMap['g']='c';
				alphabetMap['G']='C';
				alphabetMap['t']='a';
				alphabetMap['T']='A';

				char[] buffer = new char[length];
				
				int bi =0;
				for (int j = offset+length-1; j>=offset; j-- , bi++) {
					buffer[bi]= alphabetMap[chararray[j]];
				}

				output.write(buffer, 0, buffer.length);
				output.write('\n');
				buffer = null;
				return true;
			}
		}

		return false;
	}

	
	/**
	 * Returnes if an id is among those in this fasta file
	 * @param id id to search for
	 * @return existent or not
	 * @throws IOException
	 */
	public boolean containsId(String id) throws IOException {
		checkInitialisation();
		return offsetsInCharArray.containsKey(id);
	}

	/**
	 * If the contents of the file were not read in, resume this.
	 * @throws IOException
	 */
	private void checkInitialisation() throws IOException {
		if (!initialized || chararray == null) {
			this.scanContents(true);
		}
	}

}
