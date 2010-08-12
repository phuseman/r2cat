/***************************************************************************
 *   Copyright (C) 2010 by Yvonne Hermann, Peter Husemann                  *
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


import java.util.Locale;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;

/**
 * This class represents a primer object. This object includes information about
 * the following primer properties: length, startposition,endposition, melting
 * temperature, offset, primer sequence, direction of the primer, score, two
 * bases following the primer in the given sequence and the contig id.
 * 
 * The information is sometimes generated and cached on access for performance reasons.
 * 
 * @author yherrmann / phuseman
 */
public class Primer implements Comparable<Primer> {
	private String contigID = null;
	private DNASequence contig = null;
	private FastaFileReader contigSequences = null;
	private int primerStart = 0;
	private int primerLength = 0;
	private boolean onRightEnd = false;
	private int offsetInFastaFile = 0;
	private char[] primerSequence = null;
	private double primerScore = 0;
	private Double primerTemperature = null;
	
	private Bases bases;

	/**
	 * Constructor that provides the needed informations.
	 * 
	 * @param contigID
	 *            Fasta ID of the contig
	 * @param contigSequences
	 *            Fasta File Reader that contains the sequence of the contig
	 * @param primerStart
	 *            start position of the 5' end of the primer relative to the
	 *            contigs sequence
	 * @param primerLength
	 *            the length of this primer in the direction given by
	 *            fromLeftSide
	 * @param onRightEnd
	 *            is the primer on the right side of the contig in 5'-3'
	 *            direction? (otherwise it is the reverse complement on the left
	 *            side in the other direction)
	 */
	public Primer(String contigID, FastaFileReader contigSequences,
			int primerStart, int primerLength, boolean onRightEnd) {
		//  onRightEnd = true               |-->
		// -----------------|----------------------|---------------------|
		//                  ^offset         ^primerStart

		//  onRightEnd = false                        <--| (reverse complement)
		// -----------------|----------------------|---------------------|
		//                                   offset^     ^primerStart

		this.contigID = contigID;
		this.contigSequences = contigSequences;
		this.primerStart = primerStart;
		this.primerLength = primerLength;
		this.onRightEnd = onRightEnd;
		this.offsetInFastaFile = this.contigSequences.getOffset(contigID);
		this.contig = this.contigSequences.getSequence(contigID);
		bases = Bases.getInstance();
	}

	/**
	 * Override the toString() method of this object Setting up the output of
	 * the primer object
	 */
	@Override
	public String toString() {
		return toStringWithSeperatorChar('\t');
	}
	
	/**
	 * This method allows to show the primer information with an arbitrary seperating character.
	 * Common choisces are a tab \t oder a colon ,
	 * @param sep
	 * @return
	 */
	private String toStringWithSeperatorChar(char sep) {
		return String.format((Locale) null, "%d%c%c%c%d%c%d%c%.2f%c%.2f%c%s",
				getStart(),
				sep,
				onRightEnd?'>':'<',
				sep,
				getPrimerLength(),
				sep,
				getDistanceFromContigBorder(),
				sep,
				getPrimerTemperature(),
				sep,
				getPrimerScore(),
				sep,
				new String(getPrimerSeq())
				);
	}

	
	
	/**
	 * @return Gets one base behind the primer. This is necessary for the score calculation.
	 */
	protected char getLastPlus1() {
		if (onRightEnd) {
			return contigSequences.charAt(offsetInFastaFile + primerStart + primerLength + 1);
		} else {
			return bases.complementBase(contigSequences.charAt(offsetInFastaFile + primerStart - primerLength - 1));
		}
	}


	/**
	 * @return Gets the second base after the primer. This is necessary for the score calculation.
	 */
	protected Character getLastPlus2() {
		if (onRightEnd) {
			return contigSequences.charAt(offsetInFastaFile + primerStart + primerLength + 2);
		} else {
			return bases.complementBase(contigSequences.charAt(offsetInFastaFile + primerStart - primerLength - 2));
		}
	}


	/**
	 * @return Gives the distance from the end of the contig. (Distance to the last base of the contig, if in forward direction;
	 * distance to the first base if reverse complemented)
	 */
	public int getDistanceFromContigBorder() {
		if (onRightEnd) {
			return (int)contig.getSize() - primerStart;
		} else {
			return primerStart;
		}
	}
	
	public char[] getPrimerSeq() {
		//cache the primer sequences
		//if primerSequence is null, then it was not acesses before
		if (primerSequence == null) {
			if (onRightEnd) {
				primerSequence = contigSequences.getSubstring(offsetInFastaFile
						+ primerStart, primerLength);
			} else {
				primerSequence = bases.getReverseComplement(contigSequences
						.getSubstring(offsetInFastaFile + primerStart
								- primerLength, primerLength));
			}
		}
		return primerSequence;
	}


	public double getPrimerScore() {
		return primerScore;
	}

	public void setPrimerScore(double primerScore) {
		this.primerScore = primerScore;
	}

	public int getPrimerLength() {
		return primerLength;
	}
	public double getPrimerTemperature() {
		if(primerTemperature == null) {
			MeltingTemperature meltingTemperature = MeltingTemperature.getInstance();
			this.primerTemperature = meltingTemperature.calculateTemperature(this.getPrimerSeq());
		}
		
		return primerTemperature;
	}


	public String getContigID() {
		return contigID;
	}

	public int getStart() {
		return primerStart;
	}

	public int compareTo(Primer other) {
		if(this.getPrimerScore()<other.getPrimerScore()){
			return 1;
	} if(this.getPrimerScore()>other.getPrimerScore()){
		return -1;
	}
	return 0;
	}


}
