package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;

/**
 * This class generates the primer candidate sequences given a contig-sequence.
 * The sequence of primer candidates has to be checked on certain biological 
 * properties and are scored according to the scoring-scheme from the "SaveParamAndCalc" class.
 * 
 * @author yherrmann	
 *
 */
public class PrimerGenerator {
	class Bases{
		private final static char A ='A',a='a',G ='G',g='g', C='C',c='c',T='T',t='t',N='N', n='n';
	}

	private char[] seq;
	private Vector<DNASequence> sequences;
	private String[] markedSeq = null;
	private SaveParamAndCalc scoring = null;
	FastaFileReader fastaParser = null;
	private int maxLength = 24;
	private int miniLength = 19;
	private int max = maxLength+5;
	private int minBorderOffset = 80;
	private int maxBorderOffset =400;
	private int realstart = 0;
	File directory = null;
	File outputFile = null;
	
	private HashMap<Integer,Integer> pairsFirstLeftPrimer = new HashMap<Integer,Integer>();
	private HashMap<Integer,Integer> pairsFirstRightPrimer = new HashMap<Integer,Integer>();
	private ArrayList<Integer> noPartnerLeft = new ArrayList<Integer>();
	private ArrayList<Integer> noPartnerRight = new ArrayList<Integer>();
	
	
	/**
	 * 
	 * @param fastaFile
	 * @param configFile
	 * @param repeatMasking
	 * @throws Exception
	 */
	public PrimerGenerator(File fastaFile, File configFile,
			boolean repeatMasking) throws Exception {
		if(repeatMasking){
			RepeatMasking rm = new RepeatMasking(fastaFile);
			directory = rm.getDir();
			fastaParser = rm.getFfrForpreprocessed();
			seq = fastaParser.getCharArray();
			sequences = fastaParser.getSequences();
		} else{
			fastaParser = new FastaFileReader(fastaFile);
			seq = fastaParser.getCharArray();
			sequences = fastaParser.getSequences();
		}
		scoring = new SaveParamAndCalc();
		FileReader inConfig = new FileReader(configFile);
		ConfigParser configParser= new ConfigParser();
		configParser.parse(scoring, inConfig);
	}
	
	
/*	public PrimerGenerator(File fastaFile, boolean repeatMasking) throws IOException, InterruptedException{
		if(repeatMasking){
			RepeatMasking rm = new RepeatMasking(fastaFile);
			directory = rm.getDir();
			fastaParser = rm.getFfrForpreprocessed();
			seq = fastaParser.getCharArray();
			sequences = fastaParser.getSequences();
		} else{
			fastaParser = new FastaFileReader(fastaFile);
			seq = fastaParser.getCharArray();
			sequences = fastaParser.getSequences();
		}
		scoring = new SaveParamAndCalc();
	}*/

	public boolean idCheck(String[] contigID) throws IOException{
		boolean checked = false;
		for(int j = 0;j<contigID.length;j++){
			if(fastaParser.containsId(contigID[j])){
				checked =true;
			} else{
				checked = false;
			}
		}
		return checked;
	}
	
	public void generatePrimers(Vector<String[]> contigPair) throws IOException{
		int directionContig1 = 0;
		int directionContig2 = 0;
		boolean idCheck =false;
		HashMap<String, Integer> contigAndDirectionInfo = new HashMap<String,Integer>();
		for(int i = 0; i<contigPair.size();i++){
			String[] tempPair = contigPair.elementAt(i);
			if(tempPair.length==4){
			markedSeq = new String[2];
			markedSeq[0] = tempPair[0];
			markedSeq[1] = tempPair[2];
			idCheck = idCheck(markedSeq);
			if(idCheck){
			directionContig1 = this.setPrimerDirection(tempPair[1].toString());
			directionContig2 = this.setPrimerDirection(tempPair[3].toString());
			contigAndDirectionInfo.put(markedSeq[0],directionContig1);
			contigAndDirectionInfo.put(markedSeq[1],directionContig2);
			this.getPrimerCandidates(markedSeq, contigAndDirectionInfo);
				}
			} else if(tempPair.length==2){
				markedSeq = new String[1];
				markedSeq[0] = tempPair[0];
				idCheck = idCheck(markedSeq);
				if(idCheck){
				directionContig1 = this.setPrimerDirection(tempPair[1].toString());
				contigAndDirectionInfo.put(markedSeq[0],directionContig1);
				this.getPrimerCandidates(markedSeq, contigAndDirectionInfo);
				}
			}
		}
	}
	
	public int setPrimerDirection(String directionInfo){
		int direction = 0;
		if(directionInfo.equals("forward")){
			direction = 1;
		}else{
			direction = -1;
		}
		return direction;
	}
		
	/**
	 * 
	 */
		public HashMap<String,char[]> getMarkedSeq(String[] markedContig){
			HashMap<String, char[]> templateSeq = new HashMap<String,char[]>();
			for(String s:markedContig){
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
			return templateSeq;
		}
	/**
	 * @throws IOException 
	 * 
	 */
	public void getPrimerCandidates(String[] markedContig,HashMap<String, Integer> contigAndDirectionInfo) throws IOException{
	HashMap<String, char[]> templateSeq = getMarkedSeq(markedContig);
	Vector<Primer> primerCandidates = new Vector<Primer>();
			int nCount =0;
			String lastPlus12 = null;
			String lastPlus22 = null;
			for(String contigID : markedContig){
				Integer direction = contigAndDirectionInfo.get(contigID);
				char[] tempSeqChar = templateSeq.get(contigID);
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
			/*		for(char i :canidateSeq){
							if(i==Bases.N|| i==Bases.n){
								nCount++;
						}
					}*/
						//if(nCount<2){
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
						//}
					}
				}if(direction ==-1){
					//right primer
					for(int end = templateSeqString.length()-2;end>=max;end--){
						int start = end-max;
						int offset=end;
						String canidate = templateSeqString.substring(start, end);
						String lastPlus1= templateSeqString.substring(end,end+1);
						String lastPlus2 = templateSeqString.substring(end+1,end+2);
						char[] canidateArray = canidate.toCharArray();
						char[] canidateSeq = getReverseComplement(canidateArray);
			/*			for(char i :canidateSeq){
							if(i==Bases.N|| i==Bases.n){
								nCount++;
						}
					}*/
		
						//if(nCount<2){
							if(offset>minBorderOffset&&offset<maxBorderOffset){
							primerCandidates.add(new Primer(contigID,seqLength,canidateSeq,start,direction,maxLength,lastPlus1, lastPlus2,offset));
						for(int length = miniLength; length<canidate.length();length++){
							String canidate2 = canidate.substring((canidate.length()-length),canidate.length());
							char[] canidateArray2 = canidate2.toCharArray();
							char[] canidateSeq2 = getReverseComplement(canidateArray2);
							primerCandidates.add(new Primer(contigID,seqLength,canidateSeq2,start,direction,length,lastPlus12, lastPlus22,offset));		
								}
							}
					//	}
						}
					}
			}
			this.calcScoreEachPrimerCandidate(primerCandidates);
	}
	
	
	/**
	 * Methods access each scoring method for each primer object and retrieves 
	 * the whole score for each primer candidate.
	 * @throws IOException 
	 */
	public void calcScoreEachPrimerCandidate(Vector<Primer> primerCandidates) throws IOException{
		Vector<Primer> leftPrimer=new Vector<Primer>();
		Vector<Primer> rightPrimer=new Vector<Primer>();
		double primerScore = 0;
		char[] primerSeq = null;
		Integer direction = 0;
		int primerLength = 0;
		int start = 0;
		int offset = 0;
		int realstart=0;
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
		double scoreRepeat = 0;
	
		for(int i = 0; i<primerCandidates.size();i++){
			contigID = primerCandidates.elementAt(i).getContigID();
			primerLength = primerCandidates.elementAt(i).getPrimerLength();
			primerSeq = primerCandidates.elementAt(i).getPrimerSeq();
			direction = primerCandidates.elementAt(i).getDirection();
			start = primerCandidates.elementAt(i).getStart();
			plus1 = primerCandidates.elementAt(i).getLastPlus1();
			plus2 = primerCandidates.elementAt(i).getLastPlus2();
			offset = primerCandidates.elementAt(i).getOffset();
			
			scoreTemp = this.getTempScore(primerSeq);
			if(scoreTemp!=-1){
			scoreLength = this.getLengthScore(primerLength);
			scoreGCTotal = this.getGCScore(primerSeq, true,direction);
			scoreFirstLastBase = this.getFirstAndLastBaseScore(primerSeq, direction);
			scoreBackfold = this.getBackfoldScore(primerSeq);
			scoreLast6 = this.getLast6Score(primerSeq,direction);
			scoreGC0207 = this.getGCScore(primerSeq, false,direction);
			scorePlus1Plus2 = this.getPlus1Plus2Score(plus1, plus2);
			scoreOffset = this.getOffsetsScore(offset,primerLength,direction);
			scoreNPenalty = this.getNPenalty(primerSeq);
			scoreHomopoly = this.getHomopolyScore(primerSeq);
			scoreRepeat = this.getRepeatScore(primerSeq);
			realstart=this.realstart;
			primerScore = scoreGCTotal+scoreRepeat+scoreFirstLastBase+scoreNPenalty+scoreBackfold+scoreLength+scoreLast6+scoreGC0207+scoreOffset+scorePlus1Plus2+scoreTemp+scoreHomopoly;
			temperature = scoring.getTemperature();
				
		/*	//Stichproben Test leftPrimer
			String temp = new String(primerSeq);
			if(realstart==133&&start==86235&&primerLength==23){
			//if(temp.contains("ACCGCAGAGACCTGCTGTTTA")&&primerLength==21){

			//Stichproben Test right Primer
				
				//if(temp.contains("TGATCAGTGCAGCGGACAATCTT")&&primerLength==23){
			//	if(temp.contains("TGCAGCGGACAATCTTTCACT")&&primerLength==21){
			//if(primerScore==764){
				System.out.println("Total Primer score: "+primerScore);
				System.out.println("length score "+scoreLength);
				System.out.println("temperature score " +scoreTemp);
				System.out.println("Offset score: "+scoreOffset);
				System.out.println("plus1plus2: "+scorePlus1Plus2);
				System.out.println("GC0207 "+scoreGC0207);
				System.out.println("AT score: "+scoreLast6);
				System.out.println("backfold: "+scoreBackfold);
				System.out.println("first/last: "+scoreFirstLastBase);
				System.out.println("total GC "+scoreGCTotal);
				System.out.println("contig "+contigID);
				System.out.println("direction "+direction);
				System.out.println("primer length "+primerLength);
				System.out.println("start "+start);
				//System.out.println("seqLength "+contigLength);
				System.out.println("homopolyscore: "+scoreHomopoly);
				System.out.println("repeatscore: "+scoreRepeat);
				int	offset2 = offset - primerLength;
				System.out.println(plus1+" "+ plus2);
				System.out.println("offset: "+offset);
				System.out.println("real offset: "+ offset2);
				System.out.println("temperature: "+temperature);
				for(int j=0; j<primerSeq.length;j++){
					System.out.print(primerSeq[j]);
				}
				System.out.println(" /n");
			}
			*/
			if(primerScore>-200){
				if(direction == 1){
					leftPrimer.add(new Primer(contigID,primerSeq,start,direction,primerLength,primerScore,temperature,realstart));
				} else{
					realstart=offset-primerLength;
					rightPrimer.add(new Primer(contigID,primerSeq,start,direction,primerLength,primerScore,temperature,realstart));
				}
			}
		}
	}
		this.getPrimerPairs(leftPrimer,rightPrimer);
		System.out.println("left primer: "+leftPrimer.size());
		System.out.println("right primer: "+rightPrimer.size());
	}
	
	/**
	 * Initializes a instance of the class PrimerPairs in order to pair the primer candidates
	 * of each contig end.
	 * 
	 * @throws IOException 
	 */
	public void getPrimerPairs(Vector<Primer> leftPrimer, Vector<Primer> rightPrimer) throws IOException{
		PrimerPairs pp = new PrimerPairs();
		if(!rightPrimer.isEmpty()&&!leftPrimer.isEmpty()){
			leftPrimer = pp.sortPrimer(leftPrimer);
			rightPrimer = pp.sortPrimer(rightPrimer);
			pp.pairPrimer(leftPrimer, rightPrimer);
			pairsFirstLeftPrimer=pp.getPairsFirstLeftPrimer();
			pairsFirstRightPrimer=pp.getPairsFirstRightPrimer();
			noPartnerLeft=pp.getNoPartnerLeft();
			noPartnerRight=pp.getNoPartnerRight();
			output(leftPrimer, rightPrimer);
		} else if(!leftPrimer.isEmpty()){
			leftPrimer = pp.sortPrimer(leftPrimer);
			output(leftPrimer, rightPrimer);
		} else{
			System.out.println("No Primer found");
		}
	}
	
	public void deleteDir(File dir){
		File[] files = dir.listFiles();
		if(files!=null){
			for(int i = 0;i<files.length;i++){
				File tempFile = files[i];
					files[i].delete();
			}
		}
		dir.delete();
	}

	/**
	 * 
	 * @param primerSeq
	 * @return
	 */
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
	
	/**
	 * 
	 * @param seq
	 * @return
	 */
	public double getTempScore(char[] seq){
		double scoreTemperature = 0;
		scoreTemperature = scoring.calcScoreAnnealTemp(seq);
		return scoreTemperature;
	}
	/**
	 * 
	 * @param primerSeq
	 * @return
	 */
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
	/**
	 * 
	 * @param primerSeq
	 * @return
	 */
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
/**
 * 
 * @param primerSeq
 * @param direction
 * @return
 */
	public double getFirstAndLastBaseScore(char[] primerSeq,Integer direction){
	double scoreFirstLastBase = 0;
		Object first = primerSeq[0];
		Object last =primerSeq[primerSeq.length-1];
		String firstBase = first.toString();
		String lastBase = last.toString();
		firstBase = firstBase.toUpperCase();
		lastBase = lastBase.toUpperCase();
		scoreFirstLastBase = scoring.calcScoreFirstBaseAndLastBase(firstBase, lastBase);
		return scoreFirstLastBase;	
}	
/**
 * 
 * @param PrimerSeq
 * @return
 */
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
	/**
	 * 
	 * @param primerLength
	 * @return
	 */
	public double getLengthScore(int primerLength){
		double scoreLength = 0;
		scoreLength = scoring.calcLengthScore(primerLength);
		return scoreLength;
	}
	/**
	 * 
	 * @param primerSeq
	 * @param direction
	 * @return
	 */
	public double getLast6Score(char[] primerSeq, Integer direction){
	double scoreLast6Bases = 0;
	double last6Ratio =0;
	double ATLevelAtLast6 =0;
		for(int i = 1; i<=6;i++){
			if(primerSeq[(primerSeq.length-i)]==Bases.A || primerSeq[(primerSeq.length-i)]==Bases.a || primerSeq[(primerSeq.length-i)]==Bases.T || primerSeq[(primerSeq.length-i)]==Bases.t){
				ATLevelAtLast6++;
			}
		}
	last6Ratio = (ATLevelAtLast6/6*100);
	scoreLast6Bases = scoring.calcScoreLast6(last6Ratio);
	return scoreLast6Bases;
}
/**
 * 
 * @param primerSeq
 * @param totalGC
 * @param direction
 * @return
 */
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
				if(i>0&&i<7){
					gcLevel2A7++;
				}
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

/**
 * 
 * @param offset
 * @param primerLength
 * @param direction
 * @return
 */
	public double getOffsetsScore(int offset,int primerLength, Integer direction){
	double scoreOffset = 0;

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
/**
 * 
 * @param plus1
 * @param plus2
 * @return
 */
	public double getPlus1Plus2Score(String plus1,String plus2){
		double scorePlus1Plus2 = 0;
		plus1 = plus1.toUpperCase();
		plus2 = plus2.toUpperCase();
		scorePlus1Plus2 = scoring.calcScorePlus1(plus1, plus2);
		return scorePlus1Plus2;
	}
	
	/**
	 * 
	 * @param primerSeq
	 * @return
	 */
	public double getRepeatScore(char[] primerSeq){
		double scoreRepeat = 0;
		double repeatCount = 0;
		for(int i = 0; i<primerSeq.length;i++){
			if(primerSeq[i] == Bases.a||primerSeq[i] == Bases.t||primerSeq[i] == Bases.g||primerSeq[i] == Bases.c){
			repeatCount++;
			}
		}
		scoreRepeat = scoring.calcScoreRepeat(repeatCount);
		return scoreRepeat;
	}
	
	/**
	 * sets up the output of the primer objects
	 * @throws IOException 
	 * 
	 */
 	public void output(Vector<Primer> leftPrimer, Vector<Primer> rightPrimer) throws IOException{
		String NEW_LINE = System.getProperty("line.separator");
		String TAB = "\t";
		File outputDir = new File("C:\\Users\\Yvisunshine\\");
		if(!rightPrimer.isEmpty()&&!leftPrimer.isEmpty()){
			outputFile = File.createTempFile("r2cat Primerlist for contigs "+markedSeq[0]+" and "+markedSeq[1]+" ", ".txt",outputDir);
			PrintWriter buffer = new PrintWriter(new FileWriter(outputFile));
			buffer.write("primer picking results for contig "+markedSeq[0]+" and "+markedSeq[1]+":");
			buffer.write(NEW_LINE);
			buffer.write(NEW_LINE);
		for(int i = 0; i<pairsFirstLeftPrimer.size();i++){
			if(i<=100){
			buffer.write("oligo "+TAB+TAB+TAB+"start "+TAB+"length "+TAB+"offset "+TAB+"Tm"+TAB+"score"+TAB+"sequence"+"\n");
			buffer.write(NEW_LINE);
			buffer.write("forward primer: "+TAB+leftPrimer.elementAt(i).toString());
			buffer.write(NEW_LINE);
			buffer.write("reverse primer: "+TAB+rightPrimer.elementAt(pairsFirstLeftPrimer.get(i)).toString());
			buffer.write(NEW_LINE);
			buffer.write(NEW_LINE);
				}
			if(!noPartnerLeft.isEmpty()&&!noPartnerRight.isEmpty()){
				buffer.write("Could not find fitting pair for following primer candidates: ");
				buffer.write(NEW_LINE);
				buffer.write("oligo "+TAB+TAB+TAB+"start "+TAB+"length "+TAB+"offset "+TAB+"Tm"+TAB+"score"+TAB+"sequence");
				buffer.write(NEW_LINE);
				buffer.write(NEW_LINE);
				for(Integer a : noPartnerLeft){
					buffer.write("forward primer for contig "+leftPrimer.elementAt(a).getContigID() +": "+TAB+leftPrimer.elementAt(a).toString());
					buffer.write(NEW_LINE);
				}
				for(Integer b : noPartnerRight){
					buffer.write("reverse primer for contig "+rightPrimer.elementAt(b).getContigID()+": "+TAB+rightPrimer.elementAt(b).toString());
					buffer.write(NEW_LINE);
					}
				}
			}
		} if(!leftPrimer.isEmpty()&&rightPrimer.isEmpty()){
			outputFile = File.createTempFile("r2cat Primerlist for contig "+markedSeq[0], ".txt",outputDir);
			PrintWriter buffer = new PrintWriter(new FileWriter(outputFile));
			buffer.write("primer picking results for contig "+markedSeq[0]+":");
			buffer.write(NEW_LINE);
			buffer.write(NEW_LINE);
			buffer.write("oligo "+TAB+TAB+TAB+"start "+TAB+"length "+TAB+"offset "+TAB+"Tm"+TAB+"score"+TAB+"sequence");
			buffer.write(NEW_LINE);
			for(int j = 0; j<leftPrimer.size();j++){
				if(j<=100){
				buffer.write("forward primer: "+TAB+leftPrimer.elementAt(j).toString());
				buffer.write(NEW_LINE);
				buffer.write(NEW_LINE);
				}
			}
		}
		if(!rightPrimer.isEmpty()&&leftPrimer.isEmpty()){
			outputFile = File.createTempFile("r2cat Primerlist for contig "+markedSeq[0], ".txt",outputDir);
			PrintWriter buffer = new PrintWriter(new FileWriter(outputFile));
			buffer.write("primer picking results for contig "+markedSeq[0]+":");
			buffer.write(NEW_LINE);
			buffer.write("oligo "+TAB+TAB+TAB+"start "+TAB+"length "+TAB+"offset "+TAB+"Tm"+TAB+"score"+TAB+"sequence");
			buffer.write(NEW_LINE);
			for(int j = 0; j<leftPrimer.size();j++){
				if(j<=100){
				buffer.write("reverse primer: "+TAB+leftPrimer.elementAt(j).toString());
				buffer.write(NEW_LINE);
				buffer.write(NEW_LINE);
				}
			}
		}
		this.deleteDir(directory);
	}
}
