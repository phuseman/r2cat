package de.bielefeld.uni.cebitec.cav.datamodel;

import java.io.File;

public class DNASequence implements Comparable {
	private File file;
	private long offsetInFile=0;

	private String id=null;

	private String description=null;

	private long size = 0;

	private long offset = 0;
	
	protected double centerOfMass = 0;
	
	protected double totalAlignmentLength = 0;
	
	private boolean marked = false;

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

	public DNASequence(File file, String id, String description, long size, long offsetInFile ) {
		this.file = file;
		this.id = id;
		this.description=description;
		this.size = size;
		this.offsetInFile = offsetInFile;
		
	}
	
	public int compareTo(Object o) {
		int out = 0;
		if (centerOfMass > ((DNASequence) o).getCenterOfMass()) {
			out = 1;
		} else {
			out = -1;
		}

		if (id.equals(((DNASequence) o).getId())
				|| centerOfMass == ((DNASequence) o).getCenterOfMass()) {
			out = 0;
		}

		return out;
	}
//	public int compareTo(Object o) {
//		int out = 0;
//		if (size > ((DNASequence) o).getSize()) {
//			out = -1;
//		} else {
//			out = 1;
//		}
//
//		if (id.equals(((DNASequence) o).getId())
//				|| size == ((DNASequence) o).getSize()) {
//			out = 0;
//		}
//
//		return out;
//	}
//
//	
	public File getFile() {
		return file;
	}

	public String getId() {
		return id;
	}

	public long getOffset() {
		return offset;
	}

	public long getSize() {
		return size;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public double getCenterOfMass() {
		return centerOfMass;
	}

	public void setCenterOfMass(double centerOfMass) {
		this.centerOfMass = centerOfMass;
	}

	public double getTotalAlignmentLength() {
		return totalAlignmentLength;
	}

	public void setTotalAlignmentLength(double totalAlignmentLength) {
		this.totalAlignmentLength = totalAlignmentLength;
	}

}
