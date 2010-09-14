/***************************************************************************
 *   Copyright (C) 2010 by Yvonne Herrmann, Peter Husemann                  *
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

package de.bielefeld.uni.cebitec.cav.primerdesign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class XMLCheck{
	private File configFile = null;	
	/**
	 * Constructor of this class. Gets an file, which should be in xml format.
	 * 
	 * @param config
	 * @throws FileNotFoundException 
	 */
	public XMLCheck(File config) throws FileNotFoundException{
			configFile = config;
	}

	/**
	 * This method counts the opening angle bracket and the closing angle
	 * bracket. The number needs to be the same in other to have a rightful XML
	 * structure.
	 * 
	 * @return boolean
	 * @throws IOException
	 */
	public boolean scanXML() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(configFile));
		int countClosing = 0;
		int countOpening = 0;
		String line;
		while ((line = in.readLine()) != null) {
			if (!line.contains("##")) {
				char[] currentLine = line.toCharArray();
				for (int i = 0; i < currentLine.length; i++) {
					if (currentLine[i] == '<') {
						countOpening++;
					}
					if (currentLine[i] == '>') {
						countClosing++;
					}
				}
			}
		}
		if (countOpening == countClosing) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * This method makes a quick scan through the given file and checks whether
	 * after opening angle brackets at least one closing angle bracket follows
	 * in the check lines.
	 * 
	 * @return boolean
	 * @throws IOException
	 */
	public boolean quickScan() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(configFile));
		char[] cbuf = new char[1000];
		int opening = 0, closing = 0;
		in.read(cbuf, 0, 1000);
		for (int i = 0; i < cbuf.length; i++) {
			if (cbuf[i] == '<') {
				opening++;
			}
			if (cbuf[i] == '>') {
				closing++;
			}
		}
		if (opening != 0 && opening >= closing && closing != 0) {
			return true;
		} else {
			return false;
		}
	}

	}

