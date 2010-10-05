/***************************************************************************
 *   Copyright (C) 07.08.2007 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.r2cat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.bielefeld.uni.cebitec.qgram.DNASequence;
import de.bielefeld.uni.cebitec.qgram.Match;
import de.bielefeld.uni.cebitec.qgram.MatchList;

/**
 * @author Peter Husemann
 * 
 */

public class CSVParser {
	private String delimiter = "\t";

	BufferedReader input;
	
	private int queryIdField=0;
	private int queryLengthField=1;
	private int targetIdField=2;
	private int targetLengthField=3;
	private int queryStartField=4;
	private int queryEndField=5;
	private int targetStartField=6;
	private int targetEndField=7;
	
	private String mode="none";

	public CSVParser(File file) {
		try {
			input = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.err.println("Could not open file for parsing: " + file.getAbsolutePath());
		}
	}

	/**
	 * Sets the parsing mode compatible to the swift output (with -compassemb).
	 *  Tab is expected as field delimiter.
	 * 	The parsed fields are expected at the following positions:
	 * <br>0 Id/Name of the query/contig sequence
	 * <br>1 Length of the query/contig sequence
	 * <br>2 Id/Name of the target/reference sequence
	 * <br>3 Length of the target/reference sequence
	 * <br>8 query start position of this match
	 * <br>9 query end position of this match
	 * <br>10 target start position of this match
	 * <br>11 target end position of this match
	 */
	public void setSwiftMode() {
		queryIdField=0;
		queryLengthField=1;
		targetIdField=2;
		targetLengthField=3;
		queryStartField=8;
		queryEndField=9;
		targetStartField=10;
		targetEndField=11;
		delimiter = "\t";
		
		mode="swift";
	}
	
	/**
	 * Sets the parsing mode compatible to the swift output (with -compassemb).
	 *  Tab is expected as field delimiter.
	 * 	The parsed fields are expected at the following positions:
	 * <br>9 Id/Name of the query/contig sequence
	 * <br>10 Length of the query/contig sequence
	 * <br>13 Id/Name of the target/reference sequence
	 * <br>14 Length of the target/reference sequence
	 * <br>11 query start position of this match
	 * <br>12 query end position of this match
	 * <br>15 target start position of this match
	 * <br>16 target end position of this match
	 */
	public void setBlatMode() {
		queryIdField=9;
		queryLengthField=10;
		targetIdField=13;
		targetLengthField=14;
		queryStartField=11;
		queryEndField=12;
		targetStartField=15;
		targetEndField=16;
		delimiter = "\t";
		mode="blat";
	}
	
	/**
	 * Parses the contents of a csv file into a MatchList.<br>
	 * By default is tab expected as field delimiter. Lines starting with # are ignored.
	 * <br>
	 * The parsed fields are expected at the following positions:
	 * <br>0 Id/Name of the query/contig sequence
	 * <br>1 Length of the query/contig sequence
	 * <br>2 Id/Name of the target/reference sequence
	 * <br>3 Length of the target/reference sequence
	 * <br>4 query start position of this match
	 * <br>5 query end position of this match
	 * <br>6 target start position of this match
	 * <br>7 target end position of this match
	 * <br>
	 * <br> the order can be changes with the presets setSwiftMode or setBlatMode.
	 * 
	 * @return
	 */
	public MatchList parse() {
		String line;
		String[] tokens;
		int lineNumber = 1;
		int errors = 0;
		StringBuilder errorlines=new StringBuilder();

		MatchList matches = new MatchList();

		try {
			while (input.ready()) {
				line = input.readLine();
				lineNumber++;

				if (line.startsWith("#") || line.startsWith("\"#")) {
					continue;
				}

				tokens = line.split(delimiter);

				if (tokens.length < 5) {
					continue;
				}

				// for (int i = 0; i < tokens.length; i++) {
				// System.out.println(i + " " + tokens[i]);
				// }

				try {

					String queryId = tokens[queryIdField];
					long queryLength = Long.parseLong(tokens[queryLengthField]);

					String targetId = tokens[targetIdField];
					long targetLength = Long.parseLong(tokens[targetLengthField]);

					DNASequence query = null;
					DNASequence target = null;

					try {
						query = matches.getQuery(queryId, queryLength);
						target = matches.getTarget(targetId, targetLength);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (query == null) {
						query = new DNASequence(queryId, queryLength);
					}
					if (target == null) {
						target = new DNASequence(targetId, targetLength);
					}

					long queryStart = Long.parseLong(tokens[queryStartField]);
					long queryEnd = Long.parseLong(tokens[queryEndField]);

					if( mode.equals("blat")) {
						//blat has a special flag for reverse hits.
						//we expect that queryStart>queryEnd for reversed hits.
						if(tokens[8].matches("-")) {
							queryStart=queryLength-queryStart;
							queryEnd=queryLength - queryEnd;
						}
					}

					
					long targetStart = Long.parseLong(tokens[targetStartField]);
					long targetEnd = Long.parseLong(tokens[targetEndField]);

					Match ap = new Match(target,
							targetStart, targetEnd, query, queryStart, queryEnd);
					matches.addMatch(ap);

				} catch (NumberFormatException e) {
					errors++;
					errorlines.append(lineNumber + " ");
				}
			}
		} catch (IOException e) {
			System.err.println(this.getClass().getName()
					+ " Error reading line " + lineNumber + " : "
					+ e.getCause());
		}

		matches.setInitialTargetOrder();

		if (errors > 0) {
			System.err.println(this.getClass().getName() + ": There were "
					+ errors + " parse errors! Lines: " + errorlines.toString());
			System.err
					.println(String.format("long integers are expected at positions %d, %d, %d and %d", queryLengthField,targetLengthField,queryStartField,queryEndField,targetStartField,targetEndField));
		}
		
		if (matches.size()>0) {
			return matches;
		} else {
			return null;
		}
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
