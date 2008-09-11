package de.bielefeld.uni.cebitec.cav.datamodel;

public class AlignmentPosition implements Comparable {
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
		return this.query.equals(pos.target);
	}

	public long size() {
		return queryEnd - queryStart >= 0 ? queryEnd - queryStart : queryStart
				- queryEnd;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		int out = 0;

		AlignmentPosition comp = (AlignmentPosition) o;

		if (this.size() > comp.size()) {
			out = -1;
		} else {
			out = 1;
		}

		if (this.getQuery().equals(comp.getQuery())) {
			out = 0;
		}

		return out;
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
	
	/**
	 * The target positions of this match are as close as 10 bases to the compared match.
	 * @param obj other match
	 * @return
	 */
	public boolean similarTargetPosition( Object obj ) {
		AlignmentPosition other = (AlignmentPosition) obj;
		
		if (Math.abs(this.targetStart-other.targetStart)<10) {
			return true;
		}
		if (Math.abs(this.targetStart-other.targetStart)<10) {
			return true;
		}
		
		return false;
	}

	public int getNumberOfQHits() {
		return numberOfQHits;
	}

	public void setNumberOfQHits(int numberOfQHits) {
		this.numberOfQHits = numberOfQHits;
	}

}
