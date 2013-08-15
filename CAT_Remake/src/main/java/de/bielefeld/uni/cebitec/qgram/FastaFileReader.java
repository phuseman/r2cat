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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;


/**
 * @author phuseman
 * 
 */
public class FastaFileReader {
	private boolean initialized = false;

	private File source = null;
	private Vector<DNASequence> sequences;
	private HashMap<String, Integer> sequencesMap;

	private char[] chararray = null;
	private int[] offsets = null;
	private int[] sizes = null;

	public FastaFileReader(File input) {
		this.source = input;
		sequences = new Vector<DNASequence>();
		sequencesMap = new HashMap<String, Integer>();
	}

	
	/**
	 * Opens the file and tries to determine quickly if this is a fasta file.
	 * Checks the first 100 non comment lines for an ident line.
	 * @return
	 */
	public boolean isFastaQuickCheck() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(source));

		Pattern commentLine	= Pattern.compile("^\\s*#");
		Pattern identLine = Pattern.compile("^>\\s*.+");

		
		int nonCommentLines=0;
		String line;
		
		while(nonCommentLines<100 && in.ready()) {
			line = in.readLine();
			if(!commentLine.matcher(line).lookingAt()) {
				nonCommentLines++;
			}
			if(identLine.matcher(line).lookingAt()) {
				in.close();
				return true;
			}
		}

		in.close();
		return false;

		
	}
	
	/**
	 * Scans the contents of the file. Returns if at least one line with ">id" was present
	 * @param createCharArray
	 * @return boolean Id's were found
	 * @throws IOException
	 * @throws NoFastaFileException 
	 */
	public boolean scanContents(boolean createCharArray) throws IOException {
		HashMap<String, Integer> offsetsInCharArray;

		if (!isFastaQuickCheck()) {
			throw new IOException("File seems to be not a fasta file");
		}
		
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
//		try {
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
					
					//remove all leading white spaces
					identLine.replaceAll("^\\s+", "");
										
					if(identLine.matches("^.+$")) {
						idsFound=true;
					} else {
						throw new IOException("Empty Identifier (\">\") in file "+ source.getName());

					}

					if (!firstSequence) {
						lastSequenceLength = validCharCounter
								- lastValidCharCounter;
//						System.out.println(" lastSequenceLength="
//								+ lastSequenceLength);

						//TODO: check if the id was not present until now
						
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

					// remember starting positions in the char array
					// the length of the stringbuffer is the same as the
					// offset of the next sequence
					if(!offsetsInCharArray.containsKey(lastSequenceId)) {
					offsetsInCharArray.put(lastSequenceId, sb.length());
					} else {
						//throw exeption if one identifier is not unique! (this is not allowed according to the fasta standard)
						throw new IOException("Identifier not unique: " + lastSequenceId + " in file "+ source.getName());
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
				// this is not necessary, I guess.
//				sb.trimToSize();
				
				// this should be the same as below, but uses less heap memory!
//				chararray = sb.toString().toCharArray();
				
				chararray = new char[sb.length()];
				sb.getChars( 0, sb.length(), chararray, 0 );
			}

			lastSequenceLength = validCharCounter - lastValidCharCounter;
//			System.out.println(" lastSequenceLength=" + lastSequenceLength);

			// add last sequence
			sequences.add(new DNASequence(source, lastSequenceId,
					lastSequenceDescription, lastSequenceLength));

			if(createCharArray) {
				offsets = new int[sequences.size() + 1];
				sizes = new int[sequences.size()];
				int offset=0;
				for (int i = 0; i < sequences.size(); i++) {
					// set the offset of each sequence object...
					offset = offsetsInCharArray.get(sequences.get(i).getId());
					// as well as in the offsets array...
					offsets[i] = offset;
					// as well as in the sequence object
					sequences.get(i).setOffset(offset);
					// and remember the index of each sequence
					if(!sequencesMap.containsKey(sequences.get(i).getId())){
						sequencesMap.put(sequences.get(i).getId(), i);
					}
					//remember the size of each fasta sequence to speed up the updateBin in the matcher.
					sizes[i]=(int)sequences.get(i).getSize();
				}
				//store in the last field the total size. this eases to go through the sequences.
				offsets[sequences.size()] = chararray.length;
			}

			initialized = true;
			} //if ids were found
			
			
			
			
//		} finally {
			if (in != null) {
				in.close();
			}
//		}
		
			
			
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
	public char[] getCharArray() throws IOException  {
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
	public int[] getOffsetsArray() throws IOException  {
		checkInitialisation();
		return offsets;
	}
	
	public int getOffset(String contigId) {
		return offsets[sequencesMap.get(contigId)];
	}

	public DNASequence getSequence(int index) {
		return sequences.get(index);
	}
	
	public int getSizeOfSequence(int i) {
		return sizes[i];
	}

	/**
	 * Gives the DNASequence Object, if this id exists.
	 * @param id to be found
	 * @return {@link DNASequence} object. if not existent, this method returns null
	 */
	public DNASequence getSequence(String id) {
		Integer index = sequencesMap.get(id);
		if (index != null) {
			return sequences.get(sequencesMap.get(id));
		} else {
			return null;
		}
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
	public boolean writeSequence(String id, BufferedWriter output) throws IOException  {
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
	public boolean writeReverseComplementSequence(String id, BufferedWriter output) throws IOException  {
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
	 * Returns if an id is among those in this fasta file
	 * @param id id to search for
	 * @return existent or not
	 * @throws IOException
	 */
	public boolean containsId(String id) throws IOException  {
		checkInitialisation();
		return sequencesMap.containsKey(id);
	}

	/**
	 * If the contents of the file were not read in, resume this.
	 * @throws IOException
	 */
	public void checkInitialisation() throws IOException {
		if (!initialized || chararray == null) {
			this.scanContents(true);
		}
	}
	
	/**
	 * Sets all sequences to uppercase letters.
	 * This can be used as a kind of repeat masking: Uppercase letters indicate nonrepetitive parts.
	 * In this sense, this function resets the repeatmasking information.
	 * A functionality of the repeatmasked sequences must be implemented in the classes that want to use it.
	 * 
	 * @return returns if this operation was successful. If the internal char array is null, then false is returned. Cal scanContents(true) beforehand.
	 * 
	 */
	public boolean setAllToUppercaseLetters() {
		if(chararray != null) {
			for (int i = 0; i < chararray.length; i++) {
				chararray[i]=Character.toUpperCase(chararray[i]);
			}
			return true;
		}
		return false;
	}

	/**
	 * Sets the characters in a specified region to lowercase.
	 * This can be used as a kind of repeat masking: Lowercase sequences indicate repetitive parts.
	 * In this sense, this function sets the given sequences to repetitive.
	 * A functionality of these repeatmasked sequences must be implemented in the classes that want to use it.
	 * 
	 * @param contigId Identifier of the contig where the region should be set to lowercase.
	 * @param start position of the area to be set
	 * @param stop position of the area to be set
	 * @return returns if this operation was successful. If the internal char array is null, then false is returned. Cal scanContents(true) beforehand.
	 */
	public boolean setRegionToLowercaseLetters(String contigId, int start, int stop) {
		//get the offset in the char array
		Integer offset = this.getOffset(contigId);
		if (chararray != null && offset != null) {
			int realstart = offset + start;
			int realstop = offset + stop;
			
			//swap positions if startposition is bigger than stop position
			if (realstart > realstop) {
				int tmp=realstart;
				realstart = realstop;
				realstop = tmp;
			}
			
			try {
				//set all corresponding letters to lowercase
				for (int i = realstart; i < realstop; i++) {
					chararray[i] = Character.toLowerCase(chararray[i]);
				}
				//if the index is out of bounds, return false
			} catch (IndexOutOfBoundsException e) {
				return false;
			}
			return true;
		}

		return false;
	}
	
	public char charAt(int index) {
		return chararray[index];
	}
	
	public char[] getSubstring(int offset, int length) {
		char[] out = new char[length];
		System.arraycopy(chararray, offset, out, 0, length);
		return out;
	}
	
}
