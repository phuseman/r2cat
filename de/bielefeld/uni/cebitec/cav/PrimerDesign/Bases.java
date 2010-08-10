package de.bielefeld.uni.cebitec.cav.PrimerDesign;

public class Bases {
	private final static char A ='A',a='a',G ='G',g='g', C='C',c='c',T='T',t='t',N='N', n='n';
	private char[] alphabetMap= new char[256];
	
	/**
	 * This method retrieves the reverse complement of a given primer sequence.
	 * @param primerSeq
	 * @return
	 */
	public char[] getReverseComplement(char[] primerSeq){
		char[] reverseComplement = new char[primerSeq.length];
		
		for (int j = 0; j < alphabetMap.length; j++) {
			alphabetMap[j]= (char) j;
		}
		alphabetMap[Bases.a]=Bases.t;
		alphabetMap[Bases.A]=Bases.T;
		alphabetMap[Bases.c]=Bases.g;
		alphabetMap[Bases.C]=Bases.G;
		alphabetMap[Bases.g]=Bases.c;
		alphabetMap[Bases.G]=Bases.C;
		alphabetMap[Bases.t]=Bases.a;
		alphabetMap[Bases.T]=Bases.A;
		int m = 0;
		for (int k = primerSeq.length-1; k>=0; k--,m++) {
			reverseComplement[m]= alphabetMap[primerSeq[k]];
		}
		return reverseComplement;
	}
	
	/**
	 * This method returns the complement to one given base.
	 * @param base
	 * @return complementBase
	 */
	public String complementBase(String base){
		String complementBase =null;
	if(base.equals("A")){
		complementBase="T";
	} if(base.equals("a")){
		complementBase="t";
	}
	if(base.equals("t")){
		complementBase="a";
	}if(base.equals("T")){
		complementBase="A";
	}
	if(base.equals("G")){
		complementBase="C";
	}
	if(base.equals("g")){
		complementBase="c";
	}
	if(base.equals("C")){
		complementBase="G";
	}
	if(base.equals("c")){
		complementBase="g";
	}
		return complementBase;
	}
}
