package de.bielefeld.uni.cebitec.cav.primerdesign;


public class Bases {
	private static Bases instance = null;
	
	private final static char A ='A',a='a',G ='G',g='g', C='C',c='c',T='T',t='t',N='N', n='n';
	private char[] complementMap=null; 
	
	
	private Bases() {
		complementMap=new char[256];
		for (int j = 0; j < complementMap.length; j++) {
			complementMap[j]= (char) j;
		}
		complementMap[Bases.a]=Bases.t;
		complementMap[Bases.A]=Bases.T;
		complementMap[Bases.c]=Bases.g;
		complementMap[Bases.C]=Bases.G;
		complementMap[Bases.g]=Bases.c;
		complementMap[Bases.G]=Bases.C;
		complementMap[Bases.t]=Bases.a;
		complementMap[Bases.T]=Bases.A;

	}
	
	public static Bases getInstance() {
		if (instance == null) {
			instance = new Bases();
		}
		return instance;
	}

	
	/**
	 * This method retrieves the reverse complement of a given primer sequence.
	 * @param primerSeq
	 * @return
	 */
	public char[] getReverseComplement(char[] primerSeq){
		char[] reverseComplement = new char[primerSeq.length];
		
		int m = 0;
		for (int k = primerSeq.length-1; k>=0; k--,m++) {
			reverseComplement[m]= complementMap[primerSeq[k]];
		}
		return reverseComplement;
	}
	
	/**
	 * This method returns the complement to one given base.
	 * @param base
	 * @return complementBase
	 */
	public char complementBase(char base){
		return complementMap[base];
	}
}
