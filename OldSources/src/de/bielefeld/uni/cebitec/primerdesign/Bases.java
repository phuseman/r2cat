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

package de.bielefeld.uni.cebitec.primerdesign;
/**
 * This class holds methods to turn given sequences in its reverse complement.
 *
 */

public class Bases {
	private static Bases instance = null;
	private char[] complementMap=null; 
	//DNA sequence alphabet
	private final static char A ='A',a='a',G ='G',g='g', C='C',c='c',T='T',t='t',N='N', n='n';
	
	/**
	 * Constructor for this class.
	 * Sets up the complementMap with the information what the complement of a given
	 * base is. 
	 */
	private Bases() {
		complementMap=new char[256];
		for (int j = 0; j < complementMap.length; j++) {
			complementMap[j]= (char) j;
		}
		complementMap[Bases.a]=Bases.t;
		complementMap[Bases.A]=Bases.T;
		complementMap[Bases.c]=Bases.g;
		complementMap[Bases.C]=Bases.G;
		complementMap[Bases.g]=Bases.c;
		complementMap[Bases.G]=Bases.C;
		complementMap[Bases.t]=Bases.a;
		complementMap[Bases.T]=Bases.A;

	}
	/**
	 * This method checks if an instance of this class already exists, if not an
	 * instance is created and returned.
	 * 
	 * @return instance of bases
	 */
	public static Bases getInstance() {
		if (instance == null) {
			instance = new Bases();
		}
		return instance;
	}

	
	/**
	 * This method retrieves the reverse complement of a given primer sequence.
	 * 
	 * @param primerSeq
	 * @return reverseComplement
	 */
	public char[] getReverseComplement(char[] primerSeq){
		char[] reverseComplement = new char[primerSeq.length];
		int m = 0;
		for (int k = primerSeq.length-1; k>=0; k--,m++) {
			reverseComplement[m]= complementMap[primerSeq[k]];
		}
		return reverseComplement;
	}
	
	/**
	 * This method returns the complement to one given base.
	
	 * @param base
	 * @return complementBase
	 */
	public char complementBase(char base){
		return complementMap[base];
	}
}
