package de.bielefeld.uni.cebitec.cav.PrimerDesign;

/**
 * This class represents a primer object. This object includes information about
 * certain primer properties for example: length, startposition, melting temperature, etc.
 * 
 * @author yherrmann
 *
 */
public class Primer {
	private double score = 0;
	private int length = 0;
	private int seqLength = 0;
	private double meltTemp = 0;
	private Integer forward = 1,reverse =-1;
	private String contigID = null;
	private int start = 0;
	private int end = 0;
	private char[] seq = null;
	private String readDirection = null;
	private String lastPlus1 = null;
	private String lastPlus2 = null;
	private int offset = 0;
	
	
	public Primer(String contigID,int seqLength,char[] seq, int start, Integer direction, int length, String lastPlus1, String lastPlus2, int offset) {
		this.contigID = contigID;
		this.forward = direction;
		this.length = length;
		this.seq = seq;
		this.start = start;
		this.lastPlus1 =lastPlus1;
		this.lastPlus2 = lastPlus2;
		this.offset = offset;
		this.seqLength = seqLength;
	}
	

	public Primer(String contigID, char[] seq, int start, Integer direction, int length, double score,double meltTemperature){
		this.contigID = contigID;
		this.forward = direction;
		this.length = length;
		this.seq = seq;
		this.start = start;
		this.score = score;
		this.meltTemp = meltTemperature;
	}
	
	public int getSeqLength() {
		return seqLength;
	}

	public void setSeqLength(int seqLength) {
		this.seqLength = seqLength;
	}
	public String getReadDirection() {
		return readDirection;
	}

	public void setReadDirection(String readDirection) {
		this.readDirection = readDirection;
	}

	public String getLastPlus1() {
		return lastPlus1;
	}

	public void setLastPlus1(String lastPlus1) {
		this.lastPlus1 = lastPlus1;
	}

	public String getLastPlus2() {
		return lastPlus2;
	}

	public void setLastPlus2(String lastPlus2) {
		this.lastPlus2 = lastPlus2;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	public Integer getForward() {
		return forward;
	}
	public void setForward(Integer forward) {
		this.forward = forward;
	}
	public Integer getReverse() {
		return reverse;
	}
	public void setReverse(Integer reverse) {
		this.reverse = reverse;
	}
	public char[] getSeq() {
		return seq;
	}
	public void setSeq(char[] seq) {
		this.seq = seq;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public double getAnnelTemp() {
		return meltTemp;
	}
	public void setAnnelTemp(double annelTemp) {
		this.meltTemp = annelTemp;
	}

	public String getContigID() {
		return contigID;
	}
	public void setContigID(String contigID) {
		this.contigID = contigID;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
}
