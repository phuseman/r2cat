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

import java.util.Iterator;

/**
 * @author phuseman
 * 
 */
public class QGramCoder {
	private int q;

	private int alphabetSize = 4;

	private int[] alphabet;
	
	private int currentEncoding = 0;

	private long maxEncoding = 0;

	private long currentQLength = 0;

	private int divisor = 0;

	private boolean valid = false;

	public QGramCoder(int q) {
		
		
		//create alphabet map
		alphabet = new int[256];
		for (int i = 0; i < alphabet.length; i++) {
			alphabet[i]=-1;
		}
		
		alphabet['a']=0;
		alphabet['A']=0;
		alphabet['c']=1;
		alphabet['C']=1;
		alphabet['g']=2;
		alphabet['G']=2;
		alphabet['t']=3;
		alphabet['T']=4;
		
		
		//check if values are getting too big
		this.q = q;
		if (q <= 0) {
			throw new IllegalArgumentException("q must be >=0");
		}
		long power = 1;
		this.q = q;

		for (int i = 1; i <= q - 1; i++) {
			power *= alphabetSize;
			if (power < 0 || power > Integer.MAX_VALUE) {
				throw new IllegalArgumentException("Values are getting to big");
			}
		}

		divisor = (int) power; // alphabetsize^(q-1)
		maxEncoding = power * alphabetSize;

		if (power * alphabetSize < 0
				|| power * alphabetSize > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Value alphabetsize^q exceeds maximum integer.");
		}
	}

	public int updateEncoding(char c) {
		if (currentQLength < q) {
			currentQLength++;
		}


		currentEncoding %= divisor;
		currentEncoding *= alphabetSize;
		currentEncoding += charToCharcode(c);

		if ((currentQLength >= q) && (currentEncoding < maxEncoding)) {
			valid = true;
		}

		if (valid) {
			return currentEncoding;
		} else {
			return -1;
		}
	}

	private int charToCharcode(char c) {
		if (alphabet[c] != -1) {
			return alphabet[c];
		} else {
			currentEncoding = 0;
			currentQLength = 0;
			valid = false;
			return -1;
		}
	}

	private char charcodeToChar(int charcode) {
		char c = 'N';
		switch (charcode) {
		case 0:
			c = 'a';
			break;
		case 1:
			c = 'c';
			break;
		case 2:
			c = 'g';
			break;
		case 3:
			c = 't';
			break;

		default:
			break;
		}
		return c;
	}

	public String decodeQgramCode(int qGramCode) {
		if (qGramCode < 0) {return "invalid";}
		if (qGramCode > this.numberOfQGrams()) {return "codenumber too big";};
		StringBuffer out = new StringBuffer();
		out.setLength(q);
		char next;
		for (int i = 0; i <= q - 1; i++) {
			next = charcodeToChar(qGramCode%alphabetSize);
				qGramCode /= alphabetSize;
				out.setCharAt(q-i-1, next);
		}
		return out.toString();
	}

	public static void main(String argv[]) {
		String test = "acggtggaaagtgttgXaaagtttttttttgggggggggggggggggggg";

		QGramCoder coder = new QGramCoder(11);

		QGramIndex qi = new QGramIndex(coder.numberOfQGrams());

		for (int i = 0; i < test.length(); i++) {

			coder.updateEncoding(test.charAt(i));
//			System.out.println(" i:" + i + " code:"
//					+ coder.getCurrentEncoding() + " max:"
//					+ coder.numberOfQGrams());

			qi.addPosition(coder.getCurrentEncoding(), i);
			System.out.println(coder.getCurrentEncoding() + " -> " + coder.decodeQgramCode(coder.getCurrentEncoding()));
		}

		qi.print();
	}

	public int getCurrentEncoding() {
		if (valid) {
			return currentEncoding;
		} else {
			return -1;
		}
	}

	public void reset() {
		currentEncoding = 0;
		currentQLength = 0;
	}

	public int numberOfQGrams() {
		return (int) maxEncoding;
	}
}
