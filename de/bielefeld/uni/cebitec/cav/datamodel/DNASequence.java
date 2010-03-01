package de.bielefeld.uni.cebitec.cav.datamodel;

import java.io.File;

public class DNASequence implements Comparable {
	private File file;

	private String id=null;

	private String description=null;

	private long size = 0;

	private long offset = 0;
	

	
	protected double totalAlignmentLength = 0;
	protected double reverseAlignmentLength = 0;
	
	private boolean marked = false;
	
	private boolean reverseComplement = false;

	private boolean repetitive = false;

	private double repetitivePercentage=0;

	//how to order the sequences. a smaller numer = earlier
	private int sortKey;




	/**
	 * @param id
	 */
	public DNASequence(String id) {
		this.id = id;
	}

	public DNASequence(String id, long size) {
		this.id = id;
		this.size = size;
	}

	public DNASequence(File file, String id, String description, long size) {
		this.file = file;
		this.id = id;
		this.description=description;
		this.size = size;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		int out = 0;
		
		//first sort by the sortkey
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
		
		//if the ids of the sequences are equal, say they are the same
		if (id.equals(((DNASequence) o).getId())
				|| this.sortKey == ((DNASequence) o).sortKey) {
			out = 0;
		}

		return out;
	}

	
	public File getFile() {
		return file;
	}

	public String getId() {
		return id;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getSize() {
		return size;
	}


	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}


	public double getTotalAlignmentLength() {
		return totalAlignmentLength;
	}

	public void setTotalAlignmentLength(double totalAlignmentLength) {
		this.totalAlignmentLength = totalAlignmentLength;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return String.format("%s size:%d offs:%d", id, size,offset);
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isReverseComplemented() {
		return reverseComplement;
	}

	public void setReverseComplemented(boolean reverseComplement) {
		this.reverseComplement = reverseComplement;
	}

	/**
	 * This method should be used by the {@link AlignmentPositionsStatistics} object only.
	 * Set a marker for repetitiveness if one hit covers 95 percent of this contig and matches several times on a reference genome
	 * @param b is repetitive or not
	 */
	public void setRepetitive(boolean b) {
		this.repetitive =b;
	}
	/**
	 * Tells whether or not this contig is repetitive. the {@link AlignmentPositionsStatistics} object will set this property.
	 * 
	 * @return
	 */
	public boolean isRepetitive() {
		return this.repetitive;
	}

	
	/**
	 * 	This method should be used by the {@link AlignmentPositionsStatistics} object only.
	 * Remember the highest percentage of a repetitive match in this sequence.
	 * @param perc
	 */
	public void setRepetitivePercent(double perc) {
		if (perc > this.repetitivePercentage) {
			this.repetitivePercentage = perc;
			if (perc >= .95) {
				this.repetitive = true;
			}
		}
	}

	/**
	 * Gives the percentage of this sequence which occurs at least twice
	 * @return
	 */
	public double getRepetitivePercent() {
		return this.repetitivePercentage;
	}

	
	
	public void setSortKey(int sortKey) {
		this.sortKey = sortKey;
	}

	public int getSortKey() {
		return this.sortKey;
	}
	
}
