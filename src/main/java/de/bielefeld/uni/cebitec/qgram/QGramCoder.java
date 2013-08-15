/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
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
package de.bielefeld.uni.cebitec.qgram;


/**
 * This class can code q-grams of dna sequences into integers.
 * 
 * @author phuseman
 * 
 */
public class QGramCoder {
	private final int q;

	private int alphabetSize = 4;

	private int[] alphabet;
	private int[] alphabetLookup;
	
	private char[] complement;

	
	private int currentEncoding = 0;

	private long maxEncoding = 0;

	private long currentQLength = 1;

	private int sigmaToThePowerOfQMinusOne = 0;

	private boolean valid = false;
	
	private int numberOfInvalid=0;

	/**
	 * Creates an instance of a coder.
	 * @param q Length of the q-grams
	 */
	public QGramCoder(int q) {

		//create alphabet map
		alphabet = new int[256];
		alphabetLookup = new int[256];
		complement = new char[256];
		for (int i = 0; i < alphabet.length; i++) {
			alphabet[i] = -1;
			alphabetLookup[i] = -1;
			complement[i] = 'N';
		}

		alphabet['a'] = 0;
		alphabet['A'] = 0;
		alphabet['c'] = 1;
		alphabet['C'] = 1;
		alphabet['g'] = 2;
		alphabet['G'] = 2;
		alphabet['t'] = 3;
		alphabet['T'] = 3;
		


		complement['a'] = 't';
		complement['A'] = 'T';
		complement['c'] = 'g';
		complement['C'] = 'G';
		complement['g'] = 'c';
		complement['G'] = 'C';
		complement['t'] = 'a';
		complement['T'] = 'A';

		
		//check if values are getting too big
		this.q = q;
		if (q <= 0) {
			throw new IllegalArgumentException("q must be >=0");
		}
		long power = 1;

		for (int i = 1; i <= q - 1; i++) {
			power *= alphabetSize;
			if (power < 0 || power > Integer.MAX_VALUE) {
				throw new IllegalArgumentException("Values are getting to big");
			}
		}

		sigmaToThePowerOfQMinusOne = (int) power; // alphabetsize^(q-1)
		maxEncoding = power * alphabetSize;

		if (power * alphabetSize < 0
				|| power * alphabetSize > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Value alphabetsize^q exceeds maximum integer.");
		}

		
//		//this one is used to avoid a steady recomputation of these values
		//only needed for approach 2) when ranking a qgram
//		alphabetLookup['a'] = alphabet['a'] * sigmaToThePowerOfQMinusOne;
//		alphabetLookup['A'] = alphabet['A'] * sigmaToThePowerOfQMinusOne;
//		alphabetLookup['c'] = alphabet['c'] * sigmaToThePowerOfQMinusOne;
//		alphabetLookup['C'] = alphabet['C'] * sigmaToThePowerOfQMinusOne;
//		alphabetLookup['g'] = alphabet['g'] * sigmaToThePowerOfQMinusOne;
//		alphabetLookup['G'] = alphabet['G'] * sigmaToThePowerOfQMinusOne;
//		alphabetLookup['t'] = alphabet['t'] * sigmaToThePowerOfQMinusOne;
//		alphabetLookup['T'] = alphabet['T'] * sigmaToThePowerOfQMinusOne;

		
		this.reset();
	}

	
	/**
	 * Updates the encoding such that the last character is omitted and the new character is c.
	 * Like a stream coder.
	 * 
	 * Valid q-grams consist only of a,c,g,t or the uppercase letters. They are treaded as equivalent.
	 * If there is any other letter or the qgram has not reached the necessary size than -1 is returned
	 * to show that there is no code vor a valid qgram.
	 * 
	 * @param c next character.
	 * @return code or -1 if the qgram is not valid.
	 */
	public int updateEncoding(char c) {

		// if character is A,T,C,G (upper or lowercase) calculate code...
		if (alphabet[c] != -1) {
			
			//there are two possibilities to rank a q-gram:
// 1) treat the lower indices with a higher weight as in our decimal system:
			currentEncoding %= sigmaToThePowerOfQMinusOne;
			currentEncoding *= alphabetSize;
			currentEncoding += alphabet[c];
//// 2) or treat the higher indices with a lower weight which is more natural for texts:
//			currentEncoding /= alphabetSize;
//			// alphabet[c]*sigmaToThePowerOfQMinusOne is stored in alphabetLookup[c] to avoid the steady re-computation
//			currentEncoding += alphabetLookup[c];
// I would suggest that the second approach is faster, but I measured with the netbeans profiler that 1) needs 156 ms whereas 2 needs 636 ms.			
			
			
			if (currentQLength < q) {
				currentQLength++;
			} else {
				assert(currentEncoding >= 0);
				assert(currentEncoding < maxEncoding);
				valid = true;
			}
		} else {// ... else invalidate code
			currentEncoding = 0;
			currentQLength = 1;
			valid = false;
			numberOfInvalid++;
		}

		if (valid) {
			return currentEncoding;
		} else {
			return -1;
		}
	}

	/**
	 * Genereates a code for the reverse complement if the order of the chars is reversed too.
	 * @param c character to reverse complement
	 * @return
	 */
	public int updateEncodingComplement(char c) {
		return this.updateEncoding(complement[c]);
	}
	


	/**
	 * For debugging purposes. Gives the character sequence of a given integer code.
	 * 
	 * @param qGramCode integer code
	 * @return character sequence
	 */
	public String decodeQgramCode(int qGramCode) {
		if (qGramCode < 0) {return "invalid";}
		if (qGramCode > this.numberOfPossibleQGrams()) {return "codenumber too big";};
		StringBuffer out = new StringBuffer();

		char next;
		for (int i = 0; i < q ; i++) {
			next = charcodeToChar(qGramCode%alphabetSize);
				qGramCode /= alphabetSize;
				out.append(next);
		}
		return out.reverse().toString();
	}



	/**
	 * Necessary for decoding. Gives the charcode of a character
	 * @param charcode
	 * @return
	 */
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


	/**
	 * Gives the last updated encoding. Or -1 if the encoding was not valid.
	 * @return
	 */
	public int getCurrentEncoding() {
		if (valid) {
			return currentEncoding;
		} else {
			return -1;
		}
	}


	/**
	 * Reset the coder. This is necessary if another sequence should be coded.
	 */
	public void reset() {
		currentEncoding = 0;
		currentQLength = 1;
		numberOfInvalid = 0;
		valid=false;
	}

	/**
	 * Gives the highest code that could be generated. Which is the same as the number of different q-grams that can be coded.
	 * 
	 * @return the number of possible q-grams of length q.
	 */
	public int numberOfPossibleQGrams() {
		return (int) maxEncoding;
	}
}
