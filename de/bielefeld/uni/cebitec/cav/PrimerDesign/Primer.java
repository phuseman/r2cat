package de.bielefeld.uni.cebitec.cav.PrimerDesign;

/**
 * This class represents a primer object. This object includes information about
 * certain primer properties for example: length, startposition, melting temperature, etc.
 * 
 * @author yherrmann
 *
 */
public class Primer implements Comparable {
	private double primerScore = 0;
	private int primerLength = 0;
	private int contigLength = 0;
	private double temperature = 0;
	private Integer direction = 0;
	private String contigID = null;
	private int start = 0;
	private int end = 0;
	private char[] primerSeq = null;
	private String readDirection = null;
	private String lastPlus1 = null;
	private String lastPlus2 = null;
	private int offset = 0;
	
	
	public Primer(String contigID,int seqLength,char[] seq, int start, Integer direction, int length, String lastPlus1, String lastPlus2, int offset) {
		this.contigID = contigID;
		this.direction = direction;
		this.primerLength = length;
		this.primerSeq = seq;
		this.start = start;
		this.lastPlus1 =lastPlus1;
		this.lastPlus2 = lastPlus2;
		this.offset = offset;
		this.contigLength = seqLength;
	}
	

	public Primer(String contigID, char[] seq, int start, Integer direction, int length, double score,double meltTemperature){
		this.contigID = contigID;
		this.direction = direction;
		this.primerLength = length;
		this.primerSeq = seq;
		this.start = start;
		this.primerScore = score;
		this.temperature = meltTemperature;
	}
	
	public int getContigLength() {
		return contigLength;
	}

	public void setContigLength(int contigLength) {
		this.contigLength = contigLength;
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
	
	public Integer getDirection() {
		return direction;
	}


	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public char[] getPrimerSeq() {
		return primerSeq;
	}
	public void setPrimerSeq(char[] primerSeq) {
		this.primerSeq = primerSeq;
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
	public void setPrimerLength(int primerLength) {
		this.primerLength = primerLength;
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
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


	@Override
	public int compareTo(Object o) {
		Primer p1 = (Primer) o;
		return 0;
	}
}
