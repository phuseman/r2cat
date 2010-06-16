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
	private HashMap<String, Integer> primerDirection = new HashMap<String, Integer>();
	private SaveParamAndCalc scoring = new SaveParamAndCalc();
	private Vector<Primer> primerCanidates;
	private Vector<Primer> primer;
	private int maxLength = 24;
	private int miniLength = 19;
	private int max = maxLength+2;
	private double meltTemperature;
	MeltingTemp meltTemp = new MeltingTemp();
	//private ArrayList markedSeq = new ArrayList();
	
	/**
	 * 
	 */
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
	primerCanidates = new Vector<Primer>();
	primer = new Vector<Primer>();
	this.getPrimerCanidates();
}
	
	
	public void calcScoreEachPrimerCanidate(){
		double score = 0;
		char[] seq;
		Integer direction;
		int length;
		int start;
		int seqLength;
		String contigID;
		String plus1;
		String plus2;
		
		double scoreFirstLastBase = 0;
		double scoreGCTotal = 0;
		double scoreBackfold = 0;
		double scoreLength = 0;
		double scoreLast6 = 0;
		double scoreGC0207 = 0;
		double scoreOffset = 0;
		double scorePlus1Plus2 = 0;
		double scoreTemp = 0;
		
		for(int i = 0; i<primerCanidates.size();i++){
			contigID = primerCanidates.elementAt(i).getContigID();
			length = primerCanidates.elementAt(i).getLength();
			seq = primerCanidates.elementAt(i).getSeq();
			direction = primerCanidates.elementAt(i).getForward();
			start = primerCanidates.elementAt(i).getStart();
			seqLength =  primerCanidates.elementAt(i).getSeqLength();
			plus1 = primerCanidates.elementAt(i).getLastPlus1();
			plus2 = primerCanidates.elementAt(i).getLastPlus2();

			scoreLength = this.getLengthScore(length);
			scoreGCTotal = this.getGCScore(seq, true);
			scoreFirstLastBase = this.getFirstAndLastBaseScore(seq, direction);
			scoreBackfold = this.getBackfoldScore(seq);
			scoreLast6 = this.getLast6Score(seq);
			scoreGC0207 = this.getGCScore(seq, false);
			scoreOffset = 0;//this.getOffsetsScore(start, seqLength, length, direction);
			scorePlus1Plus2 = this.getPlus1Plus2Score(plus1, plus2);
			scoreTemp = this.getTempScore(seq);
			if(scoreTemp!=-1){
				score = scoreGCTotal+scoreFirstLastBase+scoreBackfold+scoreLength+scoreLast6+scoreGC0207+scoreOffset+scorePlus1Plus2+scoreTemp;
				boolean filter = this.filter(0, meltTemperature);
			
			//filter =true;
			if(filter){
			//System.out.println(score);
			//System.out.println(scoreLength+scoreGCTotal+scoreFirstLastBase+scoreLast6+scoreGC0207+scorePlus1Plus2+scoreTemp);
			primer.add(new Primer(contigID,seq,start,direction,length,score,meltTemperature));
			}
			}
			
		}
		for(int j = 0;j<primer.size();j++){
			System.out.println(primer.elementAt(j).getAnnelTemp());
		}
		System.out.println(primer.size());
	}
	
	
	public double getTempScore(char[] seq){
		double score = 0;
		score = scoring.calcScoreAnnealTemp(seq);
		meltTemperature = scoring.getMintemp();
		return score;
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
			boolean size =false;
			int nCount =0;
			//int repeatCount = 0;
			String lastPlus12 = null;
			String lastPlus22 = null;
			for(String contigID : markedSeq){
				Integer direction = primerDirection.get(contigID);
				char[] tempSeqChar;
				tempSeqChar = templateSeq.get(contigID);
				int seqLength = tempSeqChar.length;
				String templateSeqString = new String(tempSeqChar);
				if(direction == 1){
					//forward Primer
					for(int start =0;start<=(templateSeqString.length()-max);start++){
						int end = start+maxLength;
						int offset=templateSeqString.length()-start;
						String canidate = templateSeqString.substring(start,end);
						String lastPlus1 = templateSeqString.substring(end, end+1);
						String lastPlus2 = templateSeqString.substring(end+1, end+2);
						char[] canidateArray = canidate.toCharArray();
						char[] canidateSeq = getComplement(canidateArray);
					for(char i :canidateSeq){
							if(i==Bases.N|| i==Bases.n){
								nCount++;
						}
					}
						if(nCount==0){
							//String temp = new String(canidateSeq);
							//System.out.println(temp);
							//ContigID, primersequenz, startpunkt, forward length
							boolean off = this.filter(offset,0);
							size =true;
							if(off||size){
							primerCanidates.add(new Primer(contigID,seqLength,canidateSeq,start,direction,maxLength,lastPlus1, lastPlus2,offset));
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
							primerCanidates.add(new Primer(contigID,seqLength,canidateSeq2,start,direction,length,lastPlus12, lastPlus22,offset));		
							}
							}
						}
					}
				}if(direction ==-1){
					//reverse Primer
					for(int start = templateSeqString.length();start>max;start--){
						int end = start-maxLength;
						int offset=start;
						//System.out.println(start);
						//System.out.println(end);
						String canidate = templateSeqString.substring(end, start);
						String lastPlus1 = templateSeqString.substring(end-1, end);
						String lastPlus2 = templateSeqString.substring(end-2, end-1);
						char[] canidateSeq = canidate.toCharArray();
						for(char i :canidateSeq){
							if(i==Bases.N|| i==Bases.n){
								nCount++;
						} /*if(i == Bases.a||i==Bases.t||i==Bases.g||i==Bases.c){
							repeatCount++; //abspeichern zum abfragen???
						}*/
					}
		
						if(nCount==0){
							boolean off = this.filter(offset, 0);
							//String temp = new String(canidateSeq);
							//System.out.println(temp);
							//ContigID, primersequenz, startpunkt, forward length
							size = false;
							if(off||size){
							primerCanidates.add(new Primer(contigID,seqLength,canidateSeq,end,direction,maxLength,lastPlus1, lastPlus2,offset));
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
							primerCanidates.add(new Primer(contigID,seqLength,canidateSeq2,end,direction,length,lastPlus12, lastPlus22,offset));		
								}
							}
						}
						}
					}
			}
			this.calcScoreEachPrimerCanidate();
			System.out.println(primerCanidates.size());
	}
	
	public boolean filter(int offset,double meltingTemp){
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
	System.arraycopy(PrimerSeq, (PrimerSeq.length-4), temp, 0, 3);
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
	double ATLevelAtLast6 =0;
	
	for(int i = 1; i<=6;i++){
		if(PrimerSeq[(PrimerSeq.length-i)]==Bases.A || PrimerSeq[(PrimerSeq.length-i)]==Bases.a || PrimerSeq[(PrimerSeq.length-i)]==Bases.T || PrimerSeq[(PrimerSeq.length-i)]==Bases.t){
			ATLevelAtLast6++;
		}
	}
	last6Ratio = (ATLevelAtLast6/6*100);
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
	//System.out.println(score);
	return score;
}

	public double getPlus1Plus2Score(String Plus1,String Plus2){
		double score = 0;
		score = scoring.calcScorePlus1(Plus1, Plus2);
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
