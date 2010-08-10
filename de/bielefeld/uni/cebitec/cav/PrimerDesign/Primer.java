package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.Comparator;

/**
 * This class represents a primer object. This object includes information about
 * the following primer properties:
 * length, startposition,endposition, melting temperature, offset, primer sequence, direction of the primer,
 * score, two bases following the primer in the given sequence and the contig id.
 * 
 * This class includes getter and setter methods for each property.
 * 
 * @author yherrmann
 */
public class Primer{
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
	private int realstart = 0;
	
	/**
	 * Constructor for the primer candidates with following parameters given.
	 * 
	 * @param contigID
	 * @param seqLength
	 * @param seq
	 * @param start
	 * @param direction
	 * @param length
	 * @param lastPlus1
	 * @param lastPlus2
	 * @param offset
	 */
	
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
	
/**
 * constructor for the primer candidates with the following parameters given.
 * 
 * @param contigID
 * @param seq
 * @param start
 * @param direction
 * @param length
 * @param score
 * @param meltTemperature
 * @param realstart
 */
	public Primer(String contigID, char[] seq, int start, Integer direction, int length, double score,double meltTemperature,int realstart){
		this.contigID = contigID;
		this.direction = direction;
		this.primerLength = length;
		this.primerSeq = seq;
		this.start = start;
		this.primerScore = score;
		this.temperature = meltTemperature;
		this.realstart = realstart;
	}
	
	/**
	 * Override the toString() method of this object
	 * Setting up the output of the primer object
	 */
	@Override public String toString(){
		StringBuilder result = new StringBuilder();
		String TAB = "\t";
		String seq = new String(this.getPrimerSeq());
		double temperature = this.getTemperature();
		double temp = Math.round(temperature*100.0)/100.0;
		double scorePrimer=this.getPrimerScore();
		double score = Math.round(scorePrimer*100.0)/100.0;
		result.append(this.getStart()+TAB+this.getPrimerLength()+TAB+this.getRealstart()+TAB+temp+TAB+score+TAB+seq);
		return result.toString();
	}
	
	public int getRealstart() {
		return realstart;
	}


	public void setRealstart(int realstart) {
		this.realstart = realstart;
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

}
