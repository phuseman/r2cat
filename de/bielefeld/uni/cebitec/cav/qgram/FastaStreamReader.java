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
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;

/**
 * @author phuseman
 * 
 */
public class FastaStreamReader {
	File source = null;
	char[] chararray;
	Vector<DNASequence> sequences;

	public FastaStreamReader(File input) {
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
		long lastOffsetInFile=0;
		
		String identLine = "";
		long validCharCounter = 0;
		long lastValidCharCounter = 0;
		boolean firstSequence = true;
		StringBuilder sb=new StringBuilder();
		try {
			in = new BufferedReader(new FileReader(source));

			if (createCharArray) {
				sb.setLength((int) (source.length()));
			}

			while ((character = in.read()) != -1) {
				offsetInFile++;

				if ( Character.isLetter((char) character) && character != (int) '>'){
					validCharCounter++;
					if (createCharArray) {
						sb.append((char)character);
					}
				}

				// read id and description
				if (character == (int) '>') {
					identLine = in.readLine();
					offsetInFile += identLine.length();
					lastOffsetInFile = offsetInFile;

					if (!firstSequence) {
						lastSequenceLength = validCharCounter - lastValidCharCounter;
						System.out.println(" lastSequenceLength="
								+ lastSequenceLength);
						
						sequences.add( new DNASequence(source, lastSequenceId, lastSequenceDescription, lastSequenceLength, lastOffsetInFile));
						
					} else {
						firstSequence = false;
					}
					lastValidCharCounter = validCharCounter;

					int idCommentBoundary = 0;
					
					// separate id and description if possible
					if ((idCommentBoundary = identLine.indexOf((int) ' ', 1)) >= 0) {
						
						lastSequenceId = identLine.substring(0, idCommentBoundary);
						lastSequenceDescription = identLine.substring(idCommentBoundary + 1);

					} else {
						lastSequenceId = identLine;
						lastSequenceDescription = "";

					} // id/description separation

				} // read id line 
				

			} // read the whole file
			
			if(createCharArray){
				sb.trimToSize();
			chararray = sb.toString().toCharArray();
			}
		
			lastSequenceLength = validCharCounter - lastValidCharCounter;
			System.out.println(" lastSequenceLength=" + lastSequenceLength);
			
			// add last sequence
			sequences.add( new DNASequence(source, lastSequenceId, lastSequenceDescription, lastSequenceLength, lastOffsetInFile));


		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
}
