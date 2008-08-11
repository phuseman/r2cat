/***************************************************************************
 *   Copyright (C) 07.08.2007 by Peter Husemann                                  *
 *   phuseman@cebitec.uni-bielefeld.de                                     *
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

package de.bielefeld.uni.cebitec.cav.datamodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Peter Husemann
 * 
 */

public class CSVParser {
	static final String delimiter = "\t";

	BufferedReader input;

	public CSVParser(File file) {
		try {
			input = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public AlignmentPositionsList parse() {
		String line;
		String[] tokens;
		int lineNumber = 1;
		int errors = 0;

		AlignmentPositionsList apl = new AlignmentPositionsList();

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

					String queryId = tokens[0];
					long queryLength = Long.parseLong(tokens[1]);

					String targetId = tokens[2];
					long targetLength = Long.parseLong(tokens[3]);

					DNASequence query = null;
					DNASequence target = null;

					try {
						query = apl.getQuery(queryId, queryLength);
						target = apl.getTarget(targetId, targetLength);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (query == null) {
						query = new DNASequence(queryId, queryLength);
					}
					if (target == null) {
						target = new DNASequence(targetId, targetLength);
					}

					long queryStart = Long.parseLong(tokens[8]);
					long queryEnd = Long.parseLong(tokens[9]);

					long targetStart = Long.parseLong(tokens[10]);
					long targetEnd = Long.parseLong(tokens[11]);

					AlignmentPosition ap = new AlignmentPosition(target,
							targetStart, targetEnd, query, queryStart, queryEnd);
					apl.addAlignmentPosition(ap);

				} catch (NumberFormatException e) {
					errors++;
				}
			}
		} catch (IOException e) {
			System.err.println(this.getClass().getName()
					+ " Error reading line " + lineNumber + " : "
					+ e.getCause());
		}

		apl.addOffsetsToTargets();

		if (errors > 0) {
			System.err.println(this.getClass().getName() + ": There were "
					+ errors + " parse errors!");
			System.err
					.println("long integers are expected at positions 1,3,8-11");
		}
		
		if (apl.size()>0) {
			return apl;
		} else {
			return null;
		}
	}

}
