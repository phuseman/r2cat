package de.bielefeld.uni.cebitec.cav.primerdesign;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;

/**
 * This class represents a primer object. This object includes information about
 * the following primer properties: length, startposition,endposition, melting
 * temperature, offset, primer sequence, direction of the primer, score, two
 * bases following the primer in the given sequence and the contig id.
 * 
 * This class includes getter and setter methods for each property.
 * 
 * @author yherrmann
 */
public class Primer {
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
		//                                  |-->| length & onRightEnd = true
		// -----------------|----------------------|---------------------|
		//                  ^offset         ^primerStart

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
		StringBuilder result = new StringBuilder();
		String TAB = "\t";
		String seq = new String(this.getPrimerSeq());
		double temperature = this.getPrimerTemperature();
		double temp = Math.round(temperature * 100.0) / 100.0;
		double scorePrimer = this.getPrimerScore();
		double score = Math.round(scorePrimer * 100.0) / 100.0;
		result.append(this.getStart() + TAB + this.getPrimerLength() + TAB
				+ this.getDistanceFromContigBorder() + TAB + temp + TAB + score + TAB + seq);
		return result.toString();
	}

	public char getLastPlus1() {
		if (onRightEnd) {
			return contigSequences.charAt(offsetInFastaFile + primerStart + primerLength + 1);
		} else {
			return bases.complementBase(contigSequences.charAt(offsetInFastaFile + primerStart - primerLength - 1));
		}
	}


	public Character getLastPlus2() {
		if (onRightEnd) {
			return contigSequences.charAt(offsetInFastaFile + primerStart + primerLength + 2);
		} else {
			return bases.complementBase(contigSequences.charAt(offsetInFastaFile + primerStart - primerLength - 2));
		}
	}


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


}
