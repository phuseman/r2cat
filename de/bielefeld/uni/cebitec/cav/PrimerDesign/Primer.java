package de.bielefeld.uni.cebitec.cav.PrimerDesign;

public class Primer {
	private double score = 0;
	private int length = 0;
	private double annelTemp = 0;
	private Integer forward = 1,reverse =-1;
	private String contigID = null;
	private int start = 0;
	private int end = 0;
	private char[] seq = null;
	private String readDirection = null;
	private String lastPlus1 = null;
	private String lastPlus2 = null;
	
	
	public Primer(String contigID,char[] seq, int start, Integer forward, int length, String lastPlus1, String lastPlus2) {
		this.contigID = contigID;
		this.forward = forward;
		this.length = length;
		this.seq = seq;
		this.start = start;
		this.lastPlus1 =lastPlus1;
		this.lastPlus2 = lastPlus2;
		
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
		return annelTemp;
	}
	public void setAnnelTemp(double annelTemp) {
		this.annelTemp = annelTemp;
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
