package de.bielefeld.uni.cebitec.cav.controller;

import java.io.IOException;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;

/**
 * This exception is used if a fasta file should be written but the source file
 * for a DNASequence object is not known or does not contain the sequence with the given ID
 * 
 * @author phuseman
 *
 */
public class SequenceNotFoundException extends IOException {
	private DNASequence sequence=null;
	public SequenceNotFoundException(DNASequence s) {
		super();
		sequence=s;
	}

	public SequenceNotFoundException(String message, Throwable cause, DNASequence s) {
		super(message, cause);
		sequence=s;
	}

	public SequenceNotFoundException(String message, DNASequence s) {
		super(message);
		sequence=s;
	}

	public SequenceNotFoundException(Throwable cause, DNASequence s) {
		super(cause);
		sequence=s;
	}

	public DNASequence getDNASequence() {
		return sequence;
	}

	public void setDNASequence(DNASequence sequence) {
		this.sequence = sequence;
	}

}
