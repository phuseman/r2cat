package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Vector;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;

/**
 * This class generates the primer candidate sequences given a contig-sequence.
 * The sequence of primer candidates has to be checked on certain biological 
 * properties and are scored according to the scoring-scheme from the "other" class.
 * 
 * @author yherrmann	
 *
 */
public class PrimerGenerator {
	class Bases{
		private final static char A ='A',a='a',G ='G',g='g', C='C',c='c',T='T',t='t',N='N', n='n';
	}

	private char[] seq;
	private int[] offsetsInInput;
	private Vector<DNASequence> sequences;
	private String[] markedSeq;
	private HashMap<String, char[]> templateSeq = new HashMap<String,char[]>();
	private HashMap<String, Integer> contigAndPrimerInfo = new HashMap<String, Integer>();
	private SaveParamAndCalc scoring = new SaveParamAndCalc();
	private Vector<Primer> primerCandidates;
	private Vector<Primer> leftPrimer;
	private Vector<Primer> rightPrimer;


	private int maxLength = 24;
	private int miniLength = 19;
	private int max = maxLength+2;
	private int minBorderOffset = 80;
	private int maxBorderOffset =400;
	
	/**
	 * 
	 */
	public PrimerGenerator(File fasta, File xml,String[] marked, HashMap<String, Integer> contigPrimerInfo) throws Exception{
	
	FastaFileReader fastaParser = new FastaFileReader(fasta);
	FileReader inXML = new FileReader(xml);
	XMLParser xmlParser = new XMLParser();
	xmlParser.parse(scoring,inXML);
	
	contigAndPrimerInfo = contigPrimerInfo;
	markedSeq = marked;
	seq = fastaParser.getCharArray();
	offsetsInInput = fastaParser.getOffsetsArray();
	sequences =fastaParser.getSequences();
	primerCandidates = new Vector<Primer>();
	leftPrimer = new Vector<Primer>();
	rightPrimer = new Vector<Primer>();
	this.getPrimerCanidates();
	
	char[] test = new char[21];
	test[0] = 'T';
	test[1] ='C';
	test[2] ='C';
	test[3] ='G';
	test[4] ='A';
	test[5] ='G';
	test[6] ='C';
	test[7] ='G';
	test[8] ='G';
	test[9] ='C';
	test[10] ='C';
	test[11] ='T';
	test[12] ='A';
	test[13] ='T';
	test[14] ='C';
	test[15] ='A';
	test[16] ='A';
	test[17] ='T';
	test[18] ='C';
	test[19] ='A';
	test[20] ='T';

	PrimerPairs pp = new PrimerPairs(leftPrimer,rightPrimer,contigAndPrimerInfo);
}

	
	public void calcScoreEachPrimerCanidate(){
		double primerScore = 0;
		char[] primerSeq = null;
		Integer direction = 0;
		int primerLength = 0;
		int start = 0;
		int contigLength = 0;
		int offset = 0;
		String contigID = null;
		String plus1 = null;
		String plus2 = null;
		double temperature = 0;
		
		double scoreFirstLastBase = 0;
		double scoreGCTotal = 0;
		double scoreBackfold = 0;
		double scoreLength = 0;
		double scoreLast6 = 0;
		double scoreGC0207 = 0;
		double scoreOffset = 0;
		double scorePlus1Plus2 = 0;
		double scoreTemp = 0;
		double scoreNPenalty = 0;
		double scoreHomopoly = 0;
	
		for(int i = 0; i<primerCandidates.size();i++){
			contigID = primerCandidates.elementAt(i).getContigID();
			primerLength = primerCandidates.elementAt(i).getPrimerLength();
			primerSeq = primerCandidates.elementAt(i).getPrimerSeq();
			direction = primerCandidates.elementAt(i).getDirection();
			start = primerCandidates.elementAt(i).getStart();
			contigLength =  primerCandidates.elementAt(i).getContigLength();
			plus1 = primerCandidates.elementAt(i).getLastPlus1();
			plus2 = primerCandidates.elementAt(i).getLastPlus2();
			offset = primerCandidates.elementAt(i).getOffset();
			
			scoreLength = this.getLengthScore(primerLength);
			scoreGCTotal = this.getGCScore(primerSeq, true,direction);
			scoreFirstLastBase = this.getFirstAndLastBaseScore(primerSeq, direction);
			scoreBackfold = this.getBackfoldScore(primerSeq);
			scoreLast6 = this.getLast6Score(primerSeq,direction);
			scoreGC0207 = this.getGCScore(primerSeq, false,direction);
			scorePlus1Plus2 = this.getPlus1Plus2Score(plus1, plus2);
			scoreOffset = this.getOffsetsScore(offset,primerLength,direction);
			scoreNPenalty = this.getNPenalty(primerSeq);
			scoreTemp = this.getTempScore(primerSeq);
			scoreHomopoly = this.getHomopolyScore(primerSeq);
			primerScore = scoreGCTotal+scoreFirstLastBase+scoreNPenalty+scoreBackfold+scoreLength+scoreLast6+scoreGC0207+scoreOffset+scorePlus1Plus2+scoreTemp+scoreHomopoly;
			temperature = scoring.getTemperature();
			
			//Stichproben Test
			if(offset==153&&start==642&&primerLength==21){
				System.out.println("Total Primer score: "+primerScore);
				System.out.println("length score "+scoreLength);
				System.out.println("temperature score " +scoreTemp);
				System.out.println("temperature "+temperature);
				System.out.println("Offset score: "+scoreOffset);
				System.out.println(plus1+" "+ plus2);
				System.out.println("plus1plus2 "+scorePlus1Plus2);
				System.out.println("GC0207 "+scoreGC0207);
				System.out.println("AT score: "+scoreLast6);
				System.out.println("backfold "+scoreBackfold);
				System.out.println("first/last"+scoreFirstLastBase);
				System.out.println("total GC "+scoreGCTotal);
				System.out.println("Total Primer score: "+primerScore);
				System.out.println("contig "+contigID);
				System.out.println("direction "+direction);
				System.out.println("primer length "+primerLength);
				System.out.println("start "+start);
				System.out.println("seqLength "+contigLength);
				System.out.println("homopolyscore: "+scoreHomopoly);
				int	offset2 = offset - primerLength;
				System.out.println("offset "+ offset2);
				for(int j=0; j<primerSeq.length;j++){
					System.out.print(primerSeq[j]);
				}
				System.out.println("/n");
			}
			
			if(primerScore>0){
				if(direction == 1){
					leftPrimer.add(new Primer(contigID,primerSeq,start,direction,primerLength,primerScore,temperature));
				} else{
					rightPrimer.add(new Primer(contigID,primerSeq,start,direction,primerLength,primerScore,temperature));
				}
			}
		}
		System.out.println("left primer: "+leftPrimer.size());
		System.out.println("right primer: "+rightPrimer.size());
	}
	
	public double getTempScore(char[] seq){
		double scoreTemperature = 0;
		scoreTemperature = scoring.calcScoreAnnealTemp(seq);
		return scoreTemperature;
	}

	public void getMarkedSeq(){
		
		for(String s:markedSeq){
			for(int i = 0; i<sequences.size();i++){
				if(sequences.get(i).getId().matches(s)){
					int length = (int) sequences.get(i).getSize();
					int start = (int) sequences.get(i).getOffset();
					char[] temp = new char[length];
					System.arraycopy(seq, start, temp, 0, length);
					templateSeq.put(s, temp);
				}
			}
		}
	}
	
	//SEQUENZEN CHECKEN!!!!
	public void getPrimerCanidates(){
			getMarkedSeq();
			int nCount =0;
			//int repeatCount = 0;
			String lastPlus12 = null;
			String lastPlus22 = null;
			for(String contigID : markedSeq){
				Integer direction = contigAndPrimerInfo.get(contigID);
				char[] tempSeqChar;
				tempSeqChar = templateSeq.get(contigID);
				int seqLength = tempSeqChar.length;
				String templateSeqString = new String(tempSeqChar);
				if(direction == 1){
					//left primer
					for(int start =0;start<=(templateSeqString.length()-max);start++){
						int end = start+maxLength;
						int offset=templateSeqString.length()-start;
						String canidate = templateSeqString.substring(start,end);
						String lastPlus1 = templateSeqString.substring(end, end+1);
						String lastPlus2 = templateSeqString.substring(end+1, end+2);
						char[] canidateSeq = canidate.toCharArray();
					for(char i :canidateSeq){
							if(i==Bases.N|| i==Bases.n){
								nCount++;
						}
					}
						if(nCount<2){
							if(offset>minBorderOffset&&offset<maxBorderOffset){
							primerCandidates.add(new Primer(contigID,seqLength,canidateSeq,start,direction,maxLength,lastPlus1, lastPlus2,offset));
						for(int length = miniLength; length<canidate.length();length++){
							String canidate2 = canidate.substring(0, length);
							if(length ==23){
								lastPlus12 = canidate.substring(length, length+1);
								lastPlus22 = lastPlus1;
							} else{
								lastPlus12 = canidate.substring(length, length+1);
								lastPlus22 = canidate.substring(length+1,length+2);
							}
							char[] canidateSeq2 = canidate2.toCharArray();
							primerCandidates.add(new Primer(contigID,seqLength,canidateSeq2,start,direction,length,lastPlus12, lastPlus22,offset));		
							}
							}
						}
					}
				}if(direction ==-1){
					//right primer
					for(int start = templateSeqString.length();start>max;start--){
						int end = start-maxLength;
						int offset=start;
						String canidate = templateSeqString.substring(end, start);
						String lastPlus1 = templateSeqString.substring(end-1, end);
						String lastPlus2 = templateSeqString.substring(end-2, end-1);
						
						char[] canidateArray = canidate.toCharArray();
						char[] canidateSeq = getReverseComplement(canidateArray);
	
						for(char i :canidateSeq){
							if(i==Bases.N|| i==Bases.n){
								nCount++;
						}
					}
		
						if(nCount<2){
							if(offset>minBorderOffset&&offset<maxBorderOffset){
							primerCandidates.add(new Primer(contigID,seqLength,canidateSeq,end,direction,maxLength,lastPlus1, lastPlus2,offset));
						for(int length = miniLength; length<canidate.length();length++){
							String canidate2 = canidate.substring(canidate.length()-length,canidate.length());
							if(length == 23){
								lastPlus12 = canidate.substring(length-1, length);
								lastPlus22 = lastPlus1;
							} else {
								lastPlus12 = canidate.substring(length-1, length);
								lastPlus22 = canidate.substring(length-2 ,length-1);
							}
							char[] canidateArray2 = canidate2.toCharArray();
							char[] canidateSeq2 = getReverseComplement(canidateArray2);
							primerCandidates.add(new Primer(contigID,seqLength,canidateSeq2,end,direction,length,lastPlus12, lastPlus22,offset));		
								}
							}
						}
						}
					}
			}
			this.calcScoreEachPrimerCanidate();
			System.out.println("kandidaten: "+primerCandidates.size());
	}

	public char[] getReverseComplement(char[] primerSeq){

		char[] alphabetMap= new char[256];
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
	/*	for(int m = 0; m<complement.length;m++){
		System.out.print(complement[m]);
		}*/
		
	/*	//nur komplement
	 * for (int j = 0; j<primerSeq.length; j++) {
			complement[j]= alphabetMap[primerSeq[j]];
		}*/
	
		return reverseComplement;
	}
	
	public double getHomopolyScore(char[] primerSeq){
		double scoreHomopoly = 0;
		double temp = 0;
		int homCount = 0;
		char prevBase = 'X';
		char currentBase;
		for(int i=0;i<primerSeq.length;i++){
			currentBase = primerSeq[i];
			if(currentBase == prevBase){
				homCount++;
			} else{
				homCount=0;
			}
			prevBase=currentBase;
			temp += scoring.calcScoreHomopoly(homCount);
		
		}
		scoreHomopoly = temp;
		return scoreHomopoly;
	}
	
	public double getBackfoldScore(char[] primerSeq){
	double scoreBackfold = 0;
	char[] last4 = new char[4];
	char[] last4Bases;
	char[] primerSeqMinusEight = new char[primerSeq.length-8];
	System.arraycopy(primerSeq, (primerSeq.length-4), last4, 0, 4);
	System.arraycopy(primerSeq, 0, primerSeqMinusEight, 0, (primerSeq.length-8));
	last4Bases = getReverseComplement(last4);
	char[] leftSeq = primerSeqMinusEight;
	scoreBackfold = scoring.calcScoreBackfold(last4Bases, leftSeq);
	return scoreBackfold;
}

	public double getFirstAndLastBaseScore(char[] primerSeq,Integer direction){
	double scoreFirstLastBase = 0;
/*	if(direction == 1){
		Object first = primerSeq[primerSeq.length-1];
		Object last = primerSeq[0];
		String firstBase = first.toString();
		String lastBase = last.toString();
		scoreFirstLastBase = scoring.calcScoreFirstBaseAndLastBase(firstBase, lastBase);
		return scoreFirstLastBase;	
	} else{*/
		Object first = primerSeq[0];
		Object last =primerSeq[primerSeq.length-1];
		String firstBase = first.toString();
		String lastBase = last.toString();
		scoreFirstLastBase = scoring.calcScoreFirstBaseAndLastBase(firstBase, lastBase);
		return scoreFirstLastBase;	
	//}
}	

	public double getNPenalty(char[] PrimerSeq){
		double scoreNPenalty = 0;
		int count = 0;
		for(char i : PrimerSeq){
			if(i ==Bases.N|| i== Bases.n){
				count++;
			}
		}
		scoreNPenalty = scoring.calcNPenalty(count);
		return scoreNPenalty;
	}
	
	public double getLengthScore(int primerLength){
		double scoreLength = 0;
		scoreLength = scoring.calcLengthScore(primerLength);
		return scoreLength;
	}
	
	public double getLast6Score(char[] primerSeq, Integer direction){
	double scoreLast6Bases = 0;
	double last6Ratio =0;
	double ATLevelAtLast6 =0;
	//if(direction == -1){
		for(int i = 1; i<=6;i++){
			if(primerSeq[(primerSeq.length-i)]==Bases.A || primerSeq[(primerSeq.length-i)]==Bases.a || primerSeq[(primerSeq.length-i)]==Bases.T || primerSeq[(primerSeq.length-i)]==Bases.t){
				ATLevelAtLast6++;
			}
		}
	//} else{
//		for(int i = 0; i<=6;i++){
//			if(primerSeq[i]==Bases.A || primerSeq[i]==Bases.a || primerSeq[i]==Bases.T || primerSeq[i]==Bases.t){
//				ATLevelAtLast6++;
//			}
//		}
//	}
	last6Ratio = (ATLevelAtLast6/6*100);
	scoreLast6Bases = scoring.calcScoreLast6(last6Ratio);
	return scoreLast6Bases;
}

	public double getGCScore(char[] primerSeq,boolean totalGC,Integer direction){
	double scoreTotalGC = 0;
	double scoreGC2A7 = 0;
	int gcLevel=0;
	int gcLevel2A7=0;
	double gcRatio=0;
	double gcRatio2A7 =0;
	for(int i =0; i<primerSeq.length;i++){
		if(primerSeq[i]== Bases.G|| primerSeq[i] ==Bases.g|| primerSeq[i]==Bases.C || primerSeq[i] ==Bases.c){
			gcLevel++;
			//if(direction == -1){
				if(i>0&&i<7){
					gcLevel2A7++;
				}
			//} else {
			//	if(i<primerSeq.length && i>(primerSeq.length-7)){
			//		gcLevel2A7++;
			//	}
			//}
		}
	}
	
	if(totalGC){
		gcRatio =(float)gcLevel/(float)(primerSeq.length+1)*100;
		scoreTotalGC = scoring.calcScoreTotalGCLevel(gcRatio);
		return scoreTotalGC;	
	} else {
		gcRatio2A7 = (float) gcLevel2A7/6 * 100;
		scoreGC2A7 = scoring.calcScoreGCLevel2A7(gcRatio2A7);
		return scoreGC2A7;
	}
}	

	public double getOffsetsScore(int offset,int primerLength, Integer direction){
	double scoreOffset = 0;
	int realstart = 0;
	if(direction == 1){
	realstart = offset - primerLength;
	scoreOffset = scoring.calcScoreOffset(realstart)+scoring.calcScoreMaxOffset(realstart);
	return scoreOffset;
	} else{
		realstart = offset+max-primerLength;
		scoreOffset = scoring.calcScoreOffset(realstart)+scoring.calcScoreMaxOffset(realstart);
		return scoreOffset;
	}
}

	public double getPlus1Plus2Score(String plus1,String plus2){
		double scorePlus1Plus2 = 0;
		scorePlus1Plus2 = scoring.calcScorePlus1(plus1, plus2);
		return scorePlus1Plus2;
	}

	
	/*	private char[] copyEachSequ(int start, int length){
char[] tempSequ = new char[length];
System.arraycopy(seq, start, tempSequ, 0, length-start);
return tempSequ;

}
private void getEachSequ(){
ArrayList test = new ArrayList();
for(int i=0; i<offsetsInInput.length-1;i++){
	char[] tempS=new char[offsetsInInput[i+1]];
	//char[] sequ = copyEachSequ(offsetsInInput[i], offsetsInInput[i+1]);
	System.arraycopy(seq, offsetsInInput[i], tempS, 0, (offsetsInInput[i+1]-offsetsInInput[i]));
	test.add(tempS);
}	
}*/


//info ob forward oder reverse

/*public void getMarkedSeq(String[] m, boolean forward){
	marked =m;
	for(String i : marked){
		for(int j =0;j<sequences.size();j++){
			if(sequences.get(j).getId().matches(i)){
				int length = (int)sequences.get(j).getSize();
				int offset = (int)sequences.get(j).getOffset();
				//getEachSequ(length, offset);
				getPrimerCandidates(getEachSequ(length, offset));
				}
			}
		}
	}

private char[] getEachSequ(int length, int offset){
	char[] tempS = new char[length];
	System.arraycopy(seq, offset, tempS, 0, length);
	return tempS;
}
private int count2 =0;
//Primer genierieren und als array zurückgeben
public void getPrimerCandidates(char[] sequence){
	
	
	int[] lengthen =new int[6];
	lengthen[0] =19;
	lengthen[1] =20;
	lengthen[2] =21;
	lengthen[3] =22;
	lengthen[4] =23;
	lengthen[5] =24;
	
	count2 =0;
	String t = new String(sequence);
	String[] test = new String[10000];
	for(int i : lengthen){
		System.out.println(i);
		for(int j = 0;j<sequence.length;i++){
			if(j+i<sequence.length){
				test[i] =t.substring(j, i);
			}
		}
		//System.out.println(test[0]);
	}
	
/*	for(int i =0; i<sequence.length;i++){
		if(i+17<sequence.length){
		test[i] = t.substring(i, i+17);
		}if(i+18<sequence.length){
		test[i+1] = t.substring(i, i+18);
		}if(i+19<sequence.length){
		test[i+2] = t.substring(i, i+19);
		}if(i+20<sequence.length){
		test[i+3] = t.substring(i, i+20);
		}if(i+21<sequence.length){
		test[i+4] = t.substring(i, i+21);
		}if(i+22<sequence.length){
		test[i+5] = t.substring(i, i+22);
		}if(i+23<sequence.length){
		test[i+6] = t.substring(i, i+23);
		}if(i+24<sequence.length){
		test[i+7] = t.substring(i, i+24);
		}
		System.out.println(test[i]);
	}

	//return test;
}

public void scorePrimer(char[] seq){
scoring.calcScoreTotalGCLevel(getGCRatio(seq, true));
scoring.calcScoreGCLevel2A7(getGCRatio(seq, false));
}*/
	
	/*public boolean filter(int offset,double meltingTemp){
	boolean off = false;
	int mu = 0;
	double z = 0;
	int sigma =0;
	if(offset!=0){
		mu =200;
		sigma = 150;
		z = offset;
	} else{
		mu = 60;
		sigma = 20;
		z = meltingTemp;
	}
	double phi = Phi((z - mu) / sigma);
	if(phi<=0.7&&phi>=0.3){
		off=true;
		return off;
	} else{
		return off;
	}
}

public double Phi(double z) {
    if (z < -8.0) return 0.0;
    if (z >  8.0) return 1.0;
    double sum = 0.0, term = z;
    for (int i = 3; sum + term != sum; i += 2) {
        sum  = sum + term;
        term = term * z * z / i;
    }
    return 0.5 + sum * Math.exp(-z*z / 2) / Math.sqrt(2 * Math.PI);
}*/

}
