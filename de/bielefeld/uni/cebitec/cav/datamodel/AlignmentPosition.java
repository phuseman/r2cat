package de.bielefeld.uni.cebitec.cav.datamodel;

public class AlignmentPosition implements Comparable<AlignmentPosition> {
	private DNASequence target;

	private long targetStart;

	private long targetEnd;

	private DNASequence query;

	private long queryStart;

	private long queryEnd;

	private boolean selected = false;

	private static AlignmentPositionsList parentList;
	
	//-1 means not set
	private float variance=-1;
	
	private int numberOfQHits=-1;

private int repeatCount=0;

	/**
	 * @param target
	 * @param targetStart
	 * @param targetEnd
	 * @param query
	 * @param queryStart
	 * @param queryEnd
	 */
	public AlignmentPosition(DNASequence target, long targetStart,
			long targetEnd, DNASequence query, long queryStart, long queryEnd) {
		this.target = target;
		this.targetStart = targetStart;
		this.targetEnd = targetEnd;
		this.query = query;
		this.queryStart = queryStart;
		this.queryEnd = queryEnd;
	}

	public DNASequence getQuery() {
		return query;
	}

	public long getQueryEnd() {
		return queryEnd;
	}

	public long getQueryStart() {
		return queryStart;
	}
	
	public long getQuerySmallerIndex() {
		return (queryStart < queryEnd) ? queryStart : queryEnd;
	}
	
	public long getQueryLargerIndex() {
		return (queryStart > queryEnd) ? queryStart : queryEnd;
	}

	public double getQueryCenter() {
		return (queryStart + queryEnd) / 2.;
	}


	public DNASequence getTarget() {
		return target;
	}

	public long getTargetEnd() {
		return targetEnd;
	}

	public long getTargetStart() {
		return targetStart;
	}
	
	public long getTargetSmallerIndex() {
		return (targetStart < targetEnd) ? targetStart : targetEnd;
	}

	public long getTargetLargerIndex() {
		return (targetStart > targetEnd) ? targetStart : targetEnd;
	}
	
	public double getTargetCenter() {
		return ((targetStart + targetEnd) / 2.) + target.getOffset();
	}
	
	public float getVariance() {
		return variance;
	}

	public void setVariance(float variance) {
		this.variance = variance;
	}

	@Override
	public String toString() {
		String out = "";

		out += "Q[" + queryStart + "," + queryEnd + "]; ";
		out += "T[" + targetStart + "," + targetEnd + "]";

		return out;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		this.alignmentModified();
	}

	//FIXME can be a problem when using multiple alignment positions lists
	public static void setParentList(AlignmentPositionsList parentList) {
		AlignmentPosition.parentList = parentList;
	}

	private void alignmentModified() {
		parentList.alignmentChanged();
	}


	public boolean hasSameTarget(AlignmentPosition pos) {
		return this.target.equals(pos.target);
	}

	public boolean hasSameQuery(AlignmentPosition pos) {
		return this.query.equals(pos.query);
	}

	public long size() {
		return queryEnd - queryStart >= 0 ? queryEnd - queryStart : queryStart
				- queryEnd;
	}
	
	
	/**
	 * Checks if this AlignmentPosition is included in another one. A slack of 10% of this AlignmentPositions size is allowed.
	 * 
	 * If the AlignmentPositions belog to different queries, then an IllegalArgumentException is thrown.
	 * 
	 * @param other The other AlignmentPosition with which this one is compared.
	 * @return If this one is included in the other with a certain slack.
	 */
	public boolean includedInOtherAlignmentPosition(AlignmentPosition other) {
		if(! this.hasSameQuery(other)) {
			throw(new IllegalArgumentException("Matches refer to distinct queries."));
		}

		int slack = (int)(this.size() * 0.1);
		
		if((other.getQuerySmallerIndex()-slack)<this.getQuerySmallerIndex()
				&& (other.getQueryLargerIndex()+slack)>this.getQueryLargerIndex()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gives the distance of two alignment positions with respect to the target.
	 * If the matches are overlapping, then we define the distance to be zero.
	 * 
	 * If the matches belong to different references, then an IllegalArgumentException is thrown.
	 * 
	 * @param other the other AlignmentPosition
	 * @return distance with respect to the reference
	 */
	public int distanceOnTarget(AlignmentPosition other) {
		int distance = 0;
		
		if(! this.hasSameTarget(other)) {
			throw(new IllegalArgumentException("Matches refer to distinct references."));
		}
		
		if(this.targetStart < other.targetStart) {
			distance = (int) (other.targetStart - this.targetEnd);
		} else if ( other.targetStart <  this.targetStart) {
			distance = (int) (this.targetStart -  other.targetEnd);
		}
		
		return Math.max(distance, 0);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( Object obj ) {
		AlignmentPosition other = (AlignmentPosition) obj;
		
		if (this.target == other.target
				&& this.queryStart == other.queryStart 
				&& this.queryEnd == other.queryEnd
				&& this.targetStart == other.targetStart
				&& this.targetEnd == other.targetEnd) {
			return true;
		} else {
			return false;
		}
	}
	

	public int getNumberOfQHits() {
		return numberOfQHits;
	}

	public void setNumberOfQHits(int numberOfQHits) {
		this.numberOfQHits = numberOfQHits;
	}
	
	public boolean isReverseHit() {
		return queryStart > queryEnd;
	}

	@Override
	public int compareTo(AlignmentPosition o) {
		int out = 0;
	
		if (this.size() > o.size()) {
			out = -1;
		} else {
			out = 1;
		}
	
		if (this.getQuery().equals(o.getQuery())) {
			out = 0;
		}
		return out;
	}

	/**
	 * This method is called from the alignmentpositionstatistics object, if it found that a match is repeating.
	 */
	protected void increaseRepeatCount() {
		this.repeatCount++;
	}
	
	/**
	 * Gives the number of times this match is repeated. Make sure that this property was computed with a {@link AlignmentPositionsStatistics} Object.
	 * @return
	 */
	public int getRepeatCount() {
		return this.repeatCount;
	}
	
	

}
