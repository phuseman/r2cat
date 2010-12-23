package de.bielefeld.uni.cebitec.qgram;

import java.io.File;


/**
 * This Objects represent DNA sequences typically based on fasta files. Usually
 * this class is used to store the meta information for contigs and/or reference
 * genomes. Some additional functionalities (offset, repetitiveness, reverse
 * complementarity) are helpful when visualizing these sequences.
 * 
 * @author phuseman
 * 
 */
public class DNASequence implements Comparable {
	// filename of the underlying fasta file
	private File file;

	// the id of that sequence (in fasta the first word after a >). it should be
	// unique, however ther is no check implemented.
	private String id = null;

	// corresponds to the fasta description
	private String description = null;

	// the length of this sequence
	private long size = 0;

	// the offset of this sequence, if many sequences were stored in a single
	// file.
	// also used to paint the contig stacked
	private long offset = 0;

	// this is used it more matches are on the positive strand...
	private double totalAlignmentLength = 0;
	// ...or on the negative strand.
	private double reverseAlignmentLength = 0;

	// mark this sequence (and all matches belonging to it)
	private boolean marked = false;

	// this sequence should be displayed / treated as a reverse complement
	private boolean reverseComplement = false;

	// this sequence is repetitive
	private boolean repetitive = false;
	
	// this sequence is reverse
	private boolean reverse = false;
	private double supportComparativeToCentralContig = 0;

	public double getSupportComparativeToCentralContig() {
		return supportComparativeToCentralContig;
	}

	public void setSupportComparativeToCentralContig(
			double supportComparativeToCentralContig) {
		this.supportComparativeToCentralContig = supportComparativeToCentralContig;
	}
	private boolean ContigIsSelected;

	public boolean isContigIsSelected() {
		return ContigIsSelected;
	}

	public void setContigIsSelected(boolean contigIsSelected) {
		ContigIsSelected = contigIsSelected;
	}
	// the percentage of the length of this sequence that occurs more than once
	// on a reference genome
	private double repetitivePercentage = 0;

	// how to order the sequences. a smaller numer = earlier
	private int sortKey;

	/**
	 * Creates a new sequence object. At least an unique ID has to be given.
	 * However, there is no check that this ID is unique!
	 * 
	 * @param id
	 */
	public DNASequence(String id) {
		this.id = id;
	}

	/**
	 * Creates a sequence object with unique ID and size of the sequence. There
	 * is no check that the ID is unique!
	 * 
	 * @param id
	 * @param size
	 */
	public DNASequence(String id, long size) {
		this.id = id;
		this.size = size;
	}
	
	public DNASequence(String id, long size, boolean isRepetitiv, boolean isReverse) {
		this.id = id;
		this.size = size;
		this.repetitive = isRepetitiv;
		this.reverse = isReverse;
	}

	/**
	 * The commonly used constructor providing several meta information:
	 * 
	 * @param file
	 *            that contains the sequence
	 * @param id
	 *            unique id of this sequence object
	 * @param description
	 *            description from the fasta file
	 * @param size
	 *            length of the sequence
	 */
	public DNASequence(File file, String id, String description, long size) {
		this.file = file;
		this.id = id;
		this.description = description;
		this.size = size;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		int out = 0;

		// first sort by the sortkey
		if (sortKey > ((DNASequence) o).sortKey) {
			out = 1;
		} else {
			out = -1;
		}

		// if the sort keys are the same, sort by by size
		if (sortKey == ((DNASequence) o).sortKey) {
			if (size > ((DNASequence) o).size) {
				out = 1;
			} else {
				out = -1;
			}
		}

		// if the ids of the sequences are equal, say they are the same
		if (id.equals(((DNASequence) o).getId())
				|| this.sortKey == ((DNASequence) o).sortKey) {
			out = 0;
		}

		return out;
	}

	/**
	 * @return Get the file where this sequence can be found.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return Gives the ID of this sequence object.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return The offset of this sequence within the fasta file, or within a
	 *         collection of sequences.
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * Sets the offset within a collection of sequences. There is no check if
	 * these offset make sense!
	 * 
	 * @param offset
	 */
	public void setOffset(long offset) {
		this.offset = offset;
	}

	/**
	 * @return The length of this sequence.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @return Was this sequence marked in a visual representation?
	 */
	public boolean isMarked() {
		return marked;
	}

	/**
	 * Mark this sequence (and all matches that belong to it)
	 * 
	 * @param marked
	 */
	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	/**
	 * @return The description (i.e. from the underlying fasta file)
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a description for this sequence.
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format("%s size:%d offs:%d", id, size, offset);
	}

	/**
	 * Sets the file from which this sequence object was derived.
	 * 
	 * @param file
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Sets the size of this sequence.
	 * 
	 * @param size
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * This has an influence on the display of dotplots or the export of fasta
	 * files.
	 * 
	 * @return Tells if this sequence should be treated as reverse complemented.
	 */
	public boolean isReverseComplemented() {
		return reverseComplement;
	}

	/**
	 * Sets if this sequence should be treated as reverse complemented.
	 * 
	 * @param reverseComplement
	 */
	public void setReverseComplemented(boolean reverseComplement) {
		this.reverseComplement = reverseComplement;
	}

	/**
	 * This method should be used by the {@link MatchStatistics} object only.
	 * Set a marker for repetitiveness if one hit covers 95 percent of this
	 * contig and matches several times on a reference genome
	 * 
	 * @param b
	 *            is repetitive or not
	 */
	public void setRepetitive(boolean b) {
		this.repetitive = b;
	}

	/**
	 * Tells whether or not this contig is repetitive. the
	 * {@link MatchStatistics} object will set this property.
	 * 
	 * @return
	 */
	public boolean isRepetitive() {
		return this.repetitive;
	}
	
	public void setReverse(boolean b) {
		this.reverse = b;
	}

	public boolean isReverse() {
		return this.reverse;
	}

	/**
	 * This method should be used by the {@link MatchStatistics} object only.
	 * Remember the highest percentage of a repetitive match in this sequence.
	 * 
	 * @param perc
	 */
	public void setRepetitivePercent(double perc) {
		if (perc > this.repetitivePercentage) {
			this.repetitivePercentage = perc;
			if (perc >= .90) {
				this.repetitive = true;
			}
		}
	}

	/**
	 * Gives the percentage of this sequence which occurs at least twice
	 * 
	 * @return
	 */
	public double getRepetitivePercent() {
		return this.repetitivePercentage;
	}

	/**
	 * The sort key should determine how to order a collection of sequences, for
	 * example contigs.
	 * 
	 * @param sortKey
	 */
	public void setSortKey(int sortKey) {
		this.sortKey = sortKey;
	}

	/**
	 * The sort key should determine how to order a collection of sequences, for
	 * example contigs.
	 * 
	 * @return
	 */
	public int getSortKey() {
		return this.sortKey;
	}

	/**
	 * @param totalAlignmentLength
	 *            the totalAlignmentLength to set
	 */
	public void setTotalAlignmentLength(double totalAlignmentLength) {
		this.totalAlignmentLength = totalAlignmentLength;
	}

	/**
	 * @return the totalAlignmentLength
	 */
	public double getTotalAlignmentLength() {
		return totalAlignmentLength;
	}

	/**
	 * @param reverseAlignmentLength
	 *            the reverseAlignmentLength to set
	 */
	public void setReverseAlignmentLength(double reverseAlignmentLength) {
		this.reverseAlignmentLength = reverseAlignmentLength;
	}

	/**
	 * @return the reverseAlignmentLength
	 */
	public double getReverseAlignmentLength() {
		return reverseAlignmentLength;
	}

}
