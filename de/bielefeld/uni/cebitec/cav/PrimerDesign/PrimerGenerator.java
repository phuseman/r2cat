package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;
import de.bielefeld.uni.cebitec.cav.utils.AbstractProgressReporter;

/**
 * This class generates the primer candidates given contig-sequences.
 * The sequence of primer candidates has to be checked on certain biological 
 * properties and are scored according to the scoring-scheme from the "RetrievePArametersAndScores class".
 * 
 * @author yherrmann	
 *
 */
public class PrimerGenerator {
	class Bases{
		private final static char A ='A',a='a',G ='G',g='g', C='C',c='c',T='T',t='t',N='N', n='n';
	}
	private String[] markedSeq = null;
	private RetrieveParametersAndScores scoring = null;
	FastaFileReader fastaParser;
	private int realstart = 0;
	//max length a primer should have
	private int maxLength = 24;
	//min length a primer should have
	private int miniLength = 19;
	//min of how close the offset of a primer to the contig end should be
	private int minBorderOffset = 80;
	//max of how far away the offset of a primer to the contig end should be
	private int maxBorderOffset =400;
	private File temporaryDirectory = null;
	private int max = maxLength+5;
	private FileHandler fHandler;
	private Logger logger;
	private AbstractProgressReporter progress;
	private File fasta;
	
	/**
	 * Constructor when only a fasta file is given.
	 * 
	 * @param fastaFile
	 */
	public PrimerGenerator(File fastaFile){
		fasta = fastaFile;
		fastaParser = new FastaFileReader(fastaFile);
	}
	public void runRepeatMasking() throws IOException, InterruptedException{
		BLASTRepeatMasker rm = new BLASTRepeatMasker(fastaParser);
		fastaParser = rm.doRepeatMasking();
	}
	
	public boolean setParameters(File config) throws Exception{
		if(config!=null){
			this.setUpLogFile();

			scoring = new RetrieveParametersAndScores();
			FileReader inConfig = new FileReader(config);
			XMLParser configParser= new XMLParser();
			configParser.parse(scoring, inConfig);
			}else{
				this.setUpLogFile();
				scoring = new RetrieveParametersAndScores();
			}
		return true;
		}
	
	/**
	 * This methods sets up the logging file.
	 * 
	 * @throws SecurityException
	 * @throws IOException
	 */
	public void setUpLogFile() throws SecurityException, IOException{
		SimpleFormatter formatterLogFile = new SimpleFormatter();
		logger = Logger.getLogger("de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerGenerator");
		fHandler = new FileHandler("r2cat_primerDesign_log");
		logger.setUseParentHandlers(false);

		logger.addHandler(fHandler);
        fHandler.setFormatter(formatterLogFile);

		logger.setLevel(Level.SEVERE);
	}
	

	/**
	 * This method checks if the id of the selected contig is in the fasta file with the sequences of the contigs.
	 * Returns true if the contig ID is in the fasta file.
	 * 
	 * @param contigID
	 * @return checked
	 * @throws IOException 
	 */
	
	public boolean idCheck(String contigID) throws IOException{
		boolean checked = false;
			if(fastaParser.containsId(contigID)){
				checked =true;
			} else{
				checked = false;
		}
		
		return checked;
	}
	
	/**
	 * This method goes through the vector with the selection informations and starts to generate primers
	 * for each contig pair which was selected.
	 * The information which contig was selected and which direction the primer has on the specific contig end
	 * is put in a String Array. The contig IDs of the selected contigs are put in the first and third position
	 * of the array. The direction (forward or reverse" is put in the second position for the first selected Contig
	 * and in the fourth position for the second selected contig.
	 * These information are processed and put into a HashMap where the key is the contigID and the value is the
	 * direction (in form of Integers) of the primer of the selected contig.
	 * 
	 * @param contigPair
	 * @throws IOException 
	 */
	
	public Vector<PrimerResult> generatePrimers(Vector<String[]> contigPair) throws IOException{
		Vector<PrimerResult> prV = new Vector<PrimerResult>();
		int directionContig1 = 0;
		int directionContig2 = 0;
		boolean idCheckContig1 =false;
		boolean idCheckContig2 =false;
		String isReverseComContig1 = null;
		String isReverseComContig2 = null;
		HashMap<String, Integer> contigAndDirectionInfo = new HashMap<String,Integer>();
		HashMap<String, String> contigAndisReverseComplementInfo = new HashMap<String,String>();
		
		nextchar : for(int i = 0; i<contigPair.size();i++){
			String[] tempPair = contigPair.elementAt(i);
			markedSeq = new String[2];
			markedSeq[0] = tempPair[0];
			markedSeq[1] = tempPair[3];
			this.reportProgress((double)(i+1)/(contigPair.size()+1), "Generating primers for contig pair "+markedSeq[0] + " and " + markedSeq[1]);
			idCheckContig1 = this.idCheck(markedSeq[0].toString());
			idCheckContig2 = this.idCheck(markedSeq[1].toString());
		if(idCheckContig1&&idCheckContig2){
			if(!(markedSeq[0].toString().equals(markedSeq[1].toString()))){
			directionContig1 = this.setPrimerDirection(tempPair[2].toString());
			directionContig2 = this.setPrimerDirection(tempPair[5].toString());
			isReverseComContig1 = tempPair[1].toString();
			isReverseComContig2 = tempPair[4].toString();
			if(directionContig1!=directionContig2){
			contigAndDirectionInfo.put(markedSeq[0],directionContig1);
			contigAndDirectionInfo.put(markedSeq[1],directionContig2);
			contigAndisReverseComplementInfo.put(markedSeq[0],isReverseComContig1);
			contigAndisReverseComplementInfo.put(markedSeq[1],isReverseComContig2);
			PrimerResult primerResult = this.generatePrimerFor1ContigPair(markedSeq, contigAndDirectionInfo,contigAndisReverseComplementInfo);
			prV.add(primerResult);
			}else{
				throw new IllegalArgumentException("contigs were marked with the same direction for the primer");
			}
			} else{
				throw new IllegalStateException("contig was defined for forward and reverse primer");
			}
			} else{
				throw new NullPointerException("contig id could not be found");
			}
			/*} else if(tempPair.length==3){
				markedSeq = new String[1];
				markedSeq[0] = tempPair[0];
				idCheckContig1 = idCheck(markedSeq[0].toString());
				if(idCheckContig1){
				directionContig1 = this.setPrimerDirection(tempPair[2].toString());
				isReverseComContig1 = tempPair[1].toString();
				contigAndisReverseComplementInfo.put(markedSeq[0],isReverseComContig1);
				contigAndDirectionInfo.put(markedSeq[0],directionContig1);
				this.getPrimerCandidates(markedSeq, contigAndDirectionInfo,contigAndisReverseComplementInfo);
				}else{
					throw new NullPointerException("contig id could not be found");
				}
			}*/
		}
		return prV;
	}
	
	public PrimerResult generatePrimerFor1ContigPair(String[] markedContig,HashMap<String, Integer> contigAndDirectionInfo,HashMap<String, String> contigAndisReverseCompInfo) throws IOException{
		//all possible primers
		DNASequence leftContig = this.fastaParser.getSequence(markedSeq[0]);
		DNASequence rightContig = this.fastaParser.getSequence(markedSeq[1]);
		PrimerResult pr = new PrimerResult(leftContig,rightContig);
		Vector<Primer> primerCandidates = this.getPrimerCandidates(markedSeq, contigAndDirectionInfo,contigAndisReverseCompInfo);
		primerCandidates = this.calcScoreEachPrimerCandidate(primerCandidates);
		pr = this.getPrimerPairs(primerCandidates,pr);
		return pr;	
	}
	
	/**
	 * This methods checks which direction of the primer for the specific contig was selected.
	 * If the primer is the forward primer a 1 is returned and if the primer is on the other contig as the
	 * reverse primer the direction is -1.
	 * @param directionInfo
	 * @return direction
	 */
	
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
	 * This method goes through the sequence information of the fastaFileReader and returns the 
	 * sequences and the id of the marked contigs in a HashMap. It is also checked if the sequences are reverse complemented
	 * if so they get turned into the previous state.
	 * 
	 * @param markedContig
	 * @return templateSeq
	 * @throws IOException 
	 */
	
		public HashMap<String,char[]> getMarkedSeq(String[] markedContig,HashMap<String, String> contigAndisReverseCompInfo) throws IOException{
			HashMap<String, char[]> templateSeq = new HashMap<String,char[]>();
			boolean isReverseComplemented = false;
			String isReverseCom = null;
			char[] seq = fastaParser.getCharArray();
			Vector<DNASequence> sequences = fastaParser.getSequences();
			for(String s:markedContig){
				isReverseCom = contigAndisReverseCompInfo.get(s);
				isReverseComplemented = this.stringToBoolean(isReverseCom);
				for(int i = 0; i<sequences.size();i++){
					if(sequences.get(i).getId().matches(s)){
						int length = (int) sequences.get(i).getSize();
						int start = (int) sequences.get(i).getOffset();
						char[] temp = new char[length];
						System.arraycopy(seq, start, temp, 0, length);
						if(isReverseComplemented){
							this.getReverseComplement(temp);
							templateSeq.put(s, temp);
						} else{
						templateSeq.put(s, temp);
						}
					}
				}
			}
			return templateSeq;
		}
		
		public boolean stringToBoolean(String s){
			boolean bool = false;
			if(s.equals("true")){
				bool = true;
				return bool;
			} else{
				bool = false;
				return bool;
			}
			
		}
		
	/**
	 * This method goes through the marked sequences of the selected contigs and fills a vector
	 * with primer objects which are possible to be primers for each contig.
	 * 
	 * @param markedContig
	 * @param contigAndDirectionInfo
	 * @throws IOException
	 */
		
	public Vector<Primer> getPrimerCandidates(String[] markedContig,HashMap<String, Integer> contigAndDirectionInfo,HashMap<String, String> contigAndisReverseCompInfo) throws IOException{
		HashMap<String, char[]> templateSeq = getMarkedSeq(markedContig,contigAndisReverseCompInfo);
		Vector<Primer> primerCandidates = new Vector<Primer>();
			for(String contigID : markedContig){
				Integer directionOfPrimer = contigAndDirectionInfo.get(contigID);
				char[] tempSeqChar = templateSeq.get(contigID);
				int seqLength = tempSeqChar.length;
				String templateSeqString = new String(tempSeqChar);
				if(directionOfPrimer == 1){
					
					String nextPlus1Base = null;
					String nextPlus2Base =null;
					String nextPlus1BaseForShortPrimers = null;
					String nextPlus2BaseForShortPrimers = null;
					//left primer
					for(int start =0;start<=(templateSeqString.length()-max);start++){
						
						int end = start+maxLength;
						int offset = templateSeqString.length()-start;
						String candidateForwardPrimerMaxLength = templateSeqString.substring(start,end);
						nextPlus1Base = templateSeqString.substring(end, end+1);
						nextPlus2Base = templateSeqString.substring(end+1, end+2);
						char[] candidateSeqForward = candidateForwardPrimerMaxLength.toCharArray();
							if(offset>minBorderOffset&&offset<maxBorderOffset){
							primerCandidates.add(new Primer(contigID,seqLength,candidateSeqForward,start,directionOfPrimer,maxLength,nextPlus1Base, nextPlus2Base,offset));
						for(int length = miniLength; length<candidateForwardPrimerMaxLength.length();length++){
							String candidateForwardPrimer = candidateForwardPrimerMaxLength.substring(0, length);
							if(length ==23){
								nextPlus1BaseForShortPrimers = candidateForwardPrimerMaxLength.substring(length, length+1);
								nextPlus2BaseForShortPrimers = nextPlus1Base;
							} else{
								nextPlus1BaseForShortPrimers = candidateForwardPrimerMaxLength.substring(length, length+1);
								nextPlus2BaseForShortPrimers = candidateForwardPrimerMaxLength.substring(length+1,length+2);
							}
							char[] candidateSeqForwardPrimer = candidateForwardPrimer.toCharArray();
							primerCandidates.add(new Primer(contigID,seqLength,candidateSeqForwardPrimer,start,directionOfPrimer,length,nextPlus1BaseForShortPrimers, nextPlus2BaseForShortPrimers,offset));		
							}
							}
					}
				}if(directionOfPrimer ==-1){
					
					//right primer
					String nextPlus1Base = null;
					String nextPlus2Base =null;
					String nextPlus1BaseForShortPrimers = null;
					String nextPlus2BaseForShortPrimers = null;
					for(int end = templateSeqString.length();end>=max+2;end--){
						int start = end-max;
						int offset = end;
						String candidateReversePrimerMaxLength = templateSeqString.substring(start, end);
						nextPlus1Base = this.complementBase(templateSeqString.substring(start-1,start));
						nextPlus2Base = this.complementBase(templateSeqString.substring(start-2,start-1));
						char[] candidateReversePrimerToArray = candidateReversePrimerMaxLength.toCharArray();
						char[] candidateReversePrimerSeqReverseComplement = getReverseComplement(candidateReversePrimerToArray);
							if(offset>minBorderOffset&&offset<maxBorderOffset){
							primerCandidates.add(new Primer(contigID,seqLength,candidateReversePrimerSeqReverseComplement,start,directionOfPrimer,maxLength,nextPlus1Base, nextPlus2Base,offset));
						for(int length = miniLength; length<candidateReversePrimerMaxLength.length()-1;length++){
							String candidateReversePrimer = candidateReversePrimerMaxLength.substring((candidateReversePrimerMaxLength.length()-length),candidateReversePrimerMaxLength.length());
							if(length == 23){
								nextPlus1BaseForShortPrimers = this.complementBase(candidateReversePrimerMaxLength.substring(candidateReversePrimerMaxLength.length()-length-1, candidateReversePrimerMaxLength.length()-length));
								nextPlus2BaseForShortPrimers = nextPlus1Base;				
						
							} else{
								nextPlus1BaseForShortPrimers = this.complementBase(candidateReversePrimerMaxLength.substring(candidateReversePrimerMaxLength.length()-length-1,candidateReversePrimerMaxLength.length()-length));
								nextPlus2BaseForShortPrimers = this.complementBase(candidateReversePrimerMaxLength.substring(candidateReversePrimerMaxLength.length()-length-2,candidateReversePrimerMaxLength.length()-length-1));
							}
							char[] candidateReversePrimerArray = candidateReversePrimer.toCharArray();
							char[] candidateReversePrimerSeq = getReverseComplement(candidateReversePrimerArray);
							primerCandidates.add(new Primer(contigID,seqLength,candidateReversePrimerSeq,start,directionOfPrimer,length,nextPlus1BaseForShortPrimers, nextPlus2BaseForShortPrimers,offset));		
								}
							}
						}
					}
			}
			return primerCandidates;
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
	
	
	/**
	 * This methods access each scoring method for each primer object and retrieves 
	 * the whole score for each primer candidate.
	 * The primer candidates with a score higher than -200 are saved up in a vector according to
	 * the direction of the primer.
	 * 
	 * @throws IOException 
	 */
	
	public Vector<Primer> calcScoreEachPrimerCandidate(Vector<Primer> primerCandidates) throws IOException{
		Vector<Primer> leftRightPrimerVector = new Vector<Primer>();	
		for(int i = 0; i<primerCandidates.size();i++){
			
			String contigID = primerCandidates.elementAt(i).getContigID();
			int primerLength = primerCandidates.elementAt(i).getPrimerLength();
			char[] primerSeq = primerCandidates.elementAt(i).getPrimerSeq();
			int direction = primerCandidates.elementAt(i).getDirection();
			int start = primerCandidates.elementAt(i).getStart();
			String plus1 = primerCandidates.elementAt(i).getLastPlus1();
			String plus2 = primerCandidates.elementAt(i).getLastPlus2();
			int offset = primerCandidates.elementAt(i).getOffset();
			
			double scoreTemp = this.getTempScore(primerSeq);
			if(scoreTemp!=-1){
			double scoreLength = this.getLengthScore(primerLength);
			double scoreGCTotal = this.getGCScore(primerSeq, true,direction);
			double scoreFirstLastBase = this.getFirstAndLastBaseScore(primerSeq, direction);
			double scoreBackfold = this.getBackfoldScore(primerSeq);
			double scoreLast6 = this.getLast6Score(primerSeq,direction);
			double scoreGC0207 = this.getGCScore(primerSeq, false,direction);
			double scorePlus1Plus2 = this.getPlus1Plus2Score(plus1, plus2);
			double scoreOffset = this.getOffsetsScore(offset,primerLength,direction);
			double scoreNPenalty = this.getNPenalty(primerSeq);
			double scoreHomopoly = this.getHomopolyScore(primerSeq);
			double scoreRepeat = this.getRepeatScore(primerSeq);
			int realstart=this.realstart;
			double primerScore = scoreGCTotal+scoreRepeat+scoreFirstLastBase+scoreNPenalty+scoreBackfold+scoreLength+scoreLast6+scoreGC0207+scoreOffset+scorePlus1Plus2+scoreTemp+scoreHomopoly;
			double temperature = scoring.getTemperature();
			
			if(primerScore>-200){
				leftRightPrimerVector.add(new Primer(contigID,primerSeq,start,direction,primerLength,primerScore,temperature,realstart));
			}
		}
		}
		return leftRightPrimerVector;
	}
	
	/**
	 * Initializes a instance of the class PrimerPairs in order to pair the primer candidates
	 * from each contig end.
	 * 
	 * @throws IOException 
	 */
	public PrimerResult getPrimerPairs(Vector<Primer> primerCandidates, PrimerResult primerResult) throws IOException,NullPointerException{
		PrimerPairs pp = new PrimerPairs();
		Vector<HashMap<Integer,Integer>> pairsIndexMaps =new Vector<HashMap<Integer, Integer>>();
			primerCandidates = pp.sortPrimer(primerCandidates);
			primerResult = pp.pairPrimer(primerCandidates,primerResult, markedSeq);
			return primerResult;
	}
	

	/**
	 * This method is called to delete the temporary directory with its temporary files.
	 * 
	 * @param dir
	 */
	
	public void deleteDir(File dir){
		if(dir.getName().contains("tempDirectoryForBlast")&&dir.exists()){
			File[] files = dir.listFiles();
		if(files!=null){
			for(int i = 0;i<files.length;i++){
				File tempFile = files[i];
					files[i].delete();
			}
		}
		dir.delete();
		}
	}

	/**
	 * This method retrieves the reverse complement of a given primer sequence.
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
	
		return reverseComplement;
	}
	
	/**
	 * This method returns the score for the temperature a given primer sequences has.
	 * 
	 * @param seq
	 * @return scoreTemperature
	 */
	
	public double getTempScore(char[] seq){
		double scoreTemperature = 0;
		scoreTemperature = scoring.calcScoreMeltingTemperature(seq);
		return scoreTemperature;
	}
	
	/**
	 * This method checks the following bases in the primer sequences and retrieves the score for
	 * homopoly.
	 * 
	 * @param primerSeq
	 * @return scoreHomopoly
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
	 * This method retrieves each ends of the primer sequence and turns the last four bases into
	 * the reverse complement, so the possibilty of a backfold within the primer can be scored.
	 * 
	 * @param primerSeq
	 * @return scoreBackfold
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
 * This method retrieves the first and the last base of a primer sequence according to its direction
 * and then returns the score for the given bases at those positions.
 * 
 * @param primerSeq
 * @param direction
 * @return scoreFirstLastBase
 */
	
	public double getFirstAndLastBaseScore(char[] primerSeq,Integer direction){
	double scoreFirstLastBase = 0;
		Object first = primerSeq[0];
		Object last = primerSeq[primerSeq.length-1];
		String firstBase = first.toString();
		String lastBase = last.toString();
		firstBase = firstBase.toUpperCase();
		lastBase = lastBase.toUpperCase();
		scoreFirstLastBase = scoring.calcScoreFirstBaseAndLastBase(firstBase, lastBase);
		return scoreFirstLastBase;	
}	
	
/**
 * This method counts the 'N's in a given primer sequence and returns the score for the N-penalty.
 * 
 * @param PrimerSeq
 * @return scoreNPenalty
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
	 * This method retrieves the score for the given primer length
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
	 * This method calculates the ratio of AT at the last six bases and then retrieves the score
	 * for the ratio of the given primer sequences according to its direction.
	 * 
	 * @param primerSeq
	 * @param direction
	 * @return scoreLast6Bases
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
 * This method calculated the total GC-ratio and the ratio or the ratio for GC at positions 2-7 and then 
 * retrieves the score for one of those ratios of the given sequence depending on the boolean totalGC
 * 
 * @param primerSeq
 * @param totalGC
 * @param direction
 * @return scoreTotalGC/scoreGC2A7
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
 * This method calculates the realstart position of the primer in the contig sequence
 * and retrieves the score for the offset of the given primer.
 * 
 * @param offset
 * @param primerLength
 * @param direction
 * @return scoreOffset
 */
	public double getOffsetsScore(int offset,int primerLength, Integer direction){
	double scoreOffset = 0;

	if(direction == 1){
	realstart = offset - primerLength;
	scoreOffset = scoring.calcScoreOffset(realstart)+scoring.calcScoreMaxOffset(realstart);
	return scoreOffset;
	} else{
		realstart = offset-primerLength;
		scoreOffset = scoring.calcScoreOffset(realstart)+scoring.calcScoreMaxOffset(realstart);
		return scoreOffset;
	}
}
	
/**
 * This method retrieves the score for the two bases which follow after the primer sequence.
 * 
 * @param plus1
 * @param plus2
 * @return scorePlus1Plus2
 */
	public double getPlus1Plus2Score(String plus1,String plus2){
		double scorePlus1Plus2 = 0;
		plus1 = plus1.toUpperCase();
		plus2 = plus2.toUpperCase();
		scorePlus1Plus2 = scoring.calcScorePlus1(plus1, plus2);
		return scorePlus1Plus2;
	}
	
	/**
	 * This method counts the lowercase letters, which represents the repeats in the sequence and retrieves the
	 * score for those repeats.
	 * 
	 * @param primerSeq
	 * @return scoreRepeat
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

	/*public PrimerResult setResult(Vector<Primer> leftPrimer, Vector<Primer> rightPrimer, Vector<HashMap<Integer,Integer>> primerPairIndexMaps){
		PrimerResult primerResult = new PrimerResult();
		HashMap<Integer,Integer> primerPairIndexLeftPrimerFirst = primerPairIndexMaps.elementAt(0);
		HashMap<Integer,Integer> primerPairIndexRightPrimerFirst = primerPairIndexMaps.elementAt(1);
		
		
		primerResult.addContigs(leftContig, rightContig);
		
		for(int i=0;i<primerPairIndexLeftPrimerFirst.size();i++){
			primerResult.addPair(leftPrimer.elementAt(i), rightPrimer.elementAt(primerPairIndexLeftPrimerFirst.get(i)));
		}
		if(primerPairIndexRightPrimerFirst!=null&&primerPairIndexRightPrimerFirst.size()>0){
			
			Iterator iterator = (primerPairIndexRightPrimerFirst.keySet()).iterator();
			while(iterator.hasNext()) {
			int key = Integer.parseInt(iterator.next().toString());
			int value = Integer.parseInt(primerPairIndexRightPrimerFirst.get(key).toString());
			primerResult.addPair(leftPrimer.elementAt(value), rightPrimer.elementAt(key));
			}
		}
		return primerResult;
	}
*/
	/**
	 * Registers a ProgressReporter for this class.
	 * @param progressReporter
	 */
	public void registerProgressReporter(
			AbstractProgressReporter progressReporter) {
		this.progress = progressReporter;
	}
	/**
	 * If a progress reporter is registered progress changes are shown with is.
	 * @param percentDone how far are we?
	 * @param s explaining sentence
	 */
	public void reportProgress(double percentDone, String s) {
		if (progress != null) {
			progress.reportProgress(percentDone, s);
		}
	
	}
	
}
