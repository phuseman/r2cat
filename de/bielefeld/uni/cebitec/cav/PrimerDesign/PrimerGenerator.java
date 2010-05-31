package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Vector;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;


public class PrimerGenerator {
	class Bases{
		private final static char A ='A',a='a',G ='G',g='g', C='C',c='c',T='T',t='t',N='N', n='n';
	}

	private char[] seq;
	private int[] offsetsInInput;
	private Vector<DNASequence> sequences;
	private String[] markedSeq;
	private HashMap<String, char[]> templateSeq = new HashMap<String,char[]>();
	private HashMap<String, Integer> primerDirection = new HashMap<String, Integer>();
	private SaveParamAndCalc scoring = new SaveParamAndCalc();
	private Vector<Primer> primer;
	private int maxLength = 24;
	private int miniLength = 19;
	private int max = maxLength+2;
	//private ArrayList markedSeq = new ArrayList();
	
	
	public PrimerGenerator(File fasta, File xml,String[] marked, HashMap<String, Integer> primerDir) throws Exception{
	
	FastaFileReader fastaParser = new FastaFileReader(fasta);
	FileReader inXML = new FileReader(xml);
	XMLParser xmlParser = new XMLParser();
	xmlParser.parse(scoring,inXML);
	
	primerDirection = primerDir;
	markedSeq = marked;
	seq = fastaParser.getCharArray();
	offsetsInInput = fastaParser.getOffsetsArray();
	sequences =fastaParser.getSequences();
	primer = new Vector<Primer>();
	getPrimerCanidates();
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
			int repeatCount = 0;
			String lastPlus12 = null;
			String lastPlus22 = null;
			for(String s : markedSeq){
				Integer direction = primerDirection.get(s);
				char[] tempChar;
				tempChar = templateSeq.get(s);
				String tempString = new String(tempChar);
				if(direction == 1){
					//forward Primer
					for(int start =0;start<=(tempString.length()-max);start++){
						int end = start+maxLength;
						String canidate = tempString.substring(start,end);
						String lastPlus1 = tempString.substring(end, end+1);
						String lastPlus2 = tempString.substring(end+1, end+2);
						char[] canidateArray = canidate.toCharArray();
						char[] canidateSeq = getComplement(canidateArray);
					for(char i :canidateSeq){
							if(i==Bases.N|| i==Bases.n){
								nCount++;
						} if(i == Bases.a||i==Bases.t||i==Bases.g||i==Bases.c){
							repeatCount++; //abspeichern zum abfragen???
						}
					}
						if(nCount<2){
							//String temp = new String(canidateSeq);
							//System.out.println(temp);
							//ContigID, primersequenz, startpunkt, forward length
							primer.add(new Primer(s,canidateSeq,start,direction,maxLength,lastPlus1, lastPlus2));
						for(int length = miniLength; length<canidate.length();length++){
							String canidate2 = canidate.substring(0, length);
							if(length ==23){
								lastPlus12 = canidate.substring(length, length+1);
								lastPlus22 = lastPlus1;
							} else{
								lastPlus12 = canidate.substring(length, length+1);
								lastPlus22 = canidate.substring(length+1,length+2);
							}
							char[] canidateArray2 = canidate2.toCharArray();
							char[] canidateSeq2 = getComplement(canidateArray2);
							primer.add(new Primer(s,canidateSeq2,start,direction,length,lastPlus12, lastPlus22));		
							}
						}
					}
				}if(direction ==-1){
					//reverse Primer
					for(int start = tempString.length();start>max;start--){
						int end = start-maxLength;
						//System.out.println(start);
						//System.out.println(end);
						String canidate = tempString.substring(end, start);
						String lastPlus1 = tempString.substring(end-1, end);
						String lastPlus2 = tempString.substring(end-2, end-1);
						char[] canidateSeq = canidate.toCharArray();
						for(char i :canidateSeq){
							if(i==Bases.N|| i==Bases.n){
								nCount++;
						} if(i == Bases.a||i==Bases.t||i==Bases.g||i==Bases.c){
							repeatCount++; //abspeichern zum abfragen???
						}
					}
						if(nCount<2){
							//String temp = new String(canidateSeq);
							//System.out.println(temp);
							//ContigID, primersequenz, startpunkt, forward length
							primer.add(new Primer(s,canidateSeq,end,direction,maxLength,lastPlus1, lastPlus2));
						for(int length = miniLength; length<canidate.length();length++){
							String canidate2 = canidate.substring(canidate.length()-length,canidate.length());
							if(length == 23){
								lastPlus12 = canidate.substring(length-1, length);
								lastPlus22 = lastPlus1;
							} else {
								lastPlus12 = canidate.substring(length-1, length);
								lastPlus22 = canidate.substring(length-2 ,length-1);
							}
							char[] canidateSeq2 = canidate2.toCharArray();
							primer.add(new Primer(s,canidateSeq2,end,direction,length,lastPlus12, lastPlus22));		
								}
							}
						}
					}
			}
			System.out.println(primer.size());
	}
	
	public char[] getComplement(char[] PrimerSeq){
		char[] alphabetMap= new char[256];
		char[] complement = new char[PrimerSeq.length];
		
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
		
		for (int j = 0; j<PrimerSeq.length; j++) {
			complement[j]= alphabetMap[PrimerSeq[j]];
		}
		return complement;
	}
	
public double getBackfoldScore(char[] PrimerSeq){
	double score = 0;
	char[] temp = new char[3];
	char[] temp2 = new char[PrimerSeq.length-8];
	System.arraycopy(PrimerSeq, (PrimerSeq.length-4), temp, 0, 4);
	System.arraycopy(PrimerSeq, 0, temp2, 0, (PrimerSeq.length-8));
	char[] last4Bases = getComplement(temp);
	char[] leftSeq = temp2;
	score = scoring.calcScoreBackfold(last4Bases, leftSeq);
	return score;
}

public double getFirstAndLastBaseScore(char[] PrimerSeq,Integer forward){
	double score = 0;
	if(forward == 1){
		Object f = PrimerSeq[PrimerSeq.length-1];
		Object l = PrimerSeq[0];
		String first = f.toString();
		String last = l.toString();
		score = scoring.calcScoreFirstBaseAndLastBase(first, last);
	} else{
		Object f = PrimerSeq[0];
		Object l =PrimerSeq[PrimerSeq.length-1];
		String first = f.toString();
		String last = l.toString();
		score = scoring.calcScoreFirstBaseAndLastBase(first, last);
	}
	return score;	
}	

	public double getNPenalty(char[] PrimerSeq){
		double score = 0;
		int count = 0;
		for(char i : PrimerSeq){
			if(i ==Bases.N|| i== Bases.n){
				count++;
			}
		}
		score = scoring.calcNPenalty(count);
		return score;
	}
	public double getLengthScore(int primerLength){
		double score = 0;
		score = scoring.calcLengthScore(primerLength);
		return score;
	}
	
public double getLast6Score(char[] PrimerSeq){
	double score = 0;
	double last6Ratio =0;
	int ATLevelAtLast6 =0;
	int seqLength = PrimerSeq.length;
	
	for(int i =0; i<6;i++){
		if(PrimerSeq[seqLength-i]==Bases.A || PrimerSeq[seqLength-i]==Bases.a || PrimerSeq[seqLength-i] ==Bases.T || PrimerSeq[seqLength-i] ==Bases.t){
			ATLevelAtLast6++;
		}
		last6Ratio = ((ATLevelAtLast6/6)*100);
	}
	score = scoring.calcScoreLast6(last6Ratio);
	return score;
}

public double getGCScore(char[] PrimerSeq,boolean totalGCRatio){
	double score = 0;
	boolean totalGC = totalGCRatio;
	int gcLevel=0;
	int gcLevel2A7=0;
	double gcRatio=0;
	double gcRatio2A7 =0;
	for(int i =0; i<PrimerSeq.length;i++){
		if(PrimerSeq[i]== Bases.G|| PrimerSeq[i] ==Bases.g|| PrimerSeq[i]==Bases.C || PrimerSeq[i] ==Bases.c){
			gcLevel++;
			if(i>0&&i<7){
				gcLevel2A7++;
			}
		}
	}
	
	if(totalGC){
		gcRatio =(float)gcLevel/(float)(PrimerSeq.length+1)*100;
		score = scoring.calcScoreTotalGCLevel(gcRatio);
		return score;	
	} else{
		gcRatio2A7 = ((gcLevel2A7/6) * 100);
		score = scoring.calcScoreGCLevel2A7(gcRatio2A7);
		return score;
	}
}	

public double getOffsetsScore(int startposition, int seqLength, int primerLength, Integer forward){
	double score = 0;
	int realstart = 0;
	if(forward ==1){
	realstart = seqLength - startposition - primerLength;
	score = scoring.calcScoreOffset(realstart)+scoring.calcScoreMaxOffset(realstart);
	} else{
		realstart = startposition+max-primerLength;
		score = scoring.calcScoreOffset(realstart)+scoring.calcScoreMaxOffset(realstart);
	}
	return score;
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

}
