package de.bielefeld.uni.cebitec.cav.PrimerDesign;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 
 * @author yherrmann
 *
 */
	final class PrimerScoringScheme implements DocumentHandler {
		private DefaultMutableTreeNode root, currentNode, currentParent;
		private String currentTag = null;
		private String value = null;
		private HashMap<Character, Double> firstBase = new HashMap<Character, Double>();
		private HashMap<Character, Double> lastBase = new HashMap<Character, Double>();
		private HashMap<Character, Double>	plus1Base= new HashMap<Character, Double>();
		private HashMap<Character, Double>	plus2Base= new HashMap<Character, Double>();
		private HashMap<String, Double>	length= new HashMap<String, Double>();
		private HashMap<String, Double>	homopoly= new HashMap<String, Double>();
		private HashMap<String, Double>	maxOffset= new HashMap<String, Double>();
		private HashMap<Double, Double> gc = new HashMap<Double, Double>();
		private HashMap<Double, Double> offset = new HashMap<Double, Double>();
		private HashMap<Double, Double> atLast6 = new HashMap<Double, Double>();
		private HashMap<Double, Double> gc0207 = new HashMap<Double, Double>();
		private HashMap<Double, Double> anneal = new HashMap<Double, Double>	();
		private HashMap<String, Double> repeatAndBackfoldAndNPenalty= new HashMap<String, Double>();
		private ArrayList<Double> gcArrayList = new ArrayList<Double>();
		private ArrayList<Double> annealArrayList = new ArrayList<Double>();
		private ArrayList<Double> atLast6ArrayList = new ArrayList<Double>();
		private ArrayList<Double> gc0207ArrayList = new ArrayList<Double>();
		private ArrayList<Double> offsetArrayList = new ArrayList<Double>();
		private Stack stack = null;
		double temperature = 0;
		
		/**
		 * Constructor of the class.
		 * Fills the parameter containers with default values.
		 */
		
		public PrimerScoringScheme(){
				this.defaultParameters();
		}

		public double calculatePrimerScore(Primer primer) {
				char[] primerSeq = primer.getPrimerSeq();
				
				double scoreTemp = this.calcScoreMeltingTemperature(primer.getPrimerTemperature());
				if (scoreTemp != -1) {
					double scoreLength = this.getLengthScore(primer.getPrimerLength());
					double scoreGCTotal = this.getGCScore(primerSeq, true);
					//merge these
					double scoreGC0207 = this.getGCScore(primerSeq, false);
					double scoreFirstLastBase = this.getFirstAndLastBaseScore(primerSeq);
					double scoreBackfold = this.getBackfoldScore(primerSeq);
					double scoreLast6 = this.getLast6Score(primerSeq);
					double scorePlus1Plus2 = this.getPlus1Plus2Score(primer.getLastPlus1(), primer.getLastPlus2());
					double scoreOffset = this.getOffsetsScore(primer.getDistanceFromContigBorder());
					double scoreNPenalty = this.getNPenalty(primerSeq);
					double scoreHomopoly = this.getHomopolyScore(primerSeq);
					double scoreRepeat = this.getRepeatScore(primerSeq);
					double primerScore = scoreGCTotal + scoreRepeat
							+ scoreFirstLastBase + scoreNPenalty + scoreBackfold
							+ scoreLength + scoreLast6 + scoreGC0207 + scoreOffset
							+ scorePlus1Plus2 + scoreTemp + scoreHomopoly;
					return primerScore;
			}
				return -Double.MAX_VALUE;
		}

		/**
		 * This method loads default parameters which are based on the primer_search_default_config file.
		 * Parameters are chosen by Jochen Blom and Dr. Christian Rueckert.
		 */
		private void defaultParameters(){
			
			gc.put(10.0, 100.0);
			gc.put(15.0, -100.0);
			gc.put(20.0, -300.0);
			gc.put(25.0, -800.0);
			gc.put(50.0, -1500.0);
			
			gcArrayList = this.fillArrayListWithDefaultValues(gc);
			
			firstBase.put('A', 45.0);
			firstBase.put('T', 45.0);
			firstBase.put('C', 0.0);
			firstBase.put('G', 0.0);
			firstBase.put('N', -1500.0);
			
			lastBase.put('A', 130.0);
			lastBase.put('T', 100.0);
			lastBase.put('C', 0.0);
			lastBase.put('G', 0.0);
			lastBase.put('N', -1500.0);
			
			plus1Base.put('A', 130.0);
			plus1Base.put('T', 100.0);
			plus1Base.put('C', 0.0);
			plus1Base.put('G', 0.0);
			plus1Base.put('N', 0.0);
			
			plus2Base.put('A', 80.0);
			plus2Base.put('T', 80.0);
			plus2Base.put('C', 0.0);
			plus2Base.put('G', 0.0);
			plus2Base.put('N', 0.0);
			
			offset.put(0.0, -1000.0);
			offset.put(30.0, -600.0);
			offset.put(50.0, -500.0);
			offset.put(80.0, -400.0);
			offset.put(110.0, -50.0);
			offset.put(150., 250.0);
			
			offsetArrayList = this.fillArrayListWithDefaultValues(offset);
			
			gc0207.put(79.0, 156.0);
			gc0207.put(60.0, -83.0);
			gc0207.put(50.0, -320.0);
			gc0207.put(0.0, -1500.0);
			
			gc0207ArrayList = this.fillArrayListWithDefaultValues(gc0207);
			
			atLast6.put(79.0, 156.0);
			atLast6.put(60.0, -83.0);
			atLast6.put(50.0, -320.0);
			atLast6.put(0.0, -1500.0);
			
			atLast6ArrayList = this.fillArrayListWithDefaultValues(atLast6);
			
			maxOffset.put("DISTANCE", 150.0);
			maxOffset.put("MULT",-2.0);
			
			repeatAndBackfoldAndNPenalty.put("REPEAT", -78.0);
			repeatAndBackfoldAndNPenalty.put("N_PENALTY", -1500.0);
			repeatAndBackfoldAndNPenalty.put("BACKFOLD", -1500.0);
			
			homopoly.put("CNT", 3.0);
			homopoly.put("SCORE", -200.0);
			
			anneal.put(2.0, 200.0);
			anneal.put(6.0,0.0);
			anneal.put(100.0, -1500.0);
			
			annealArrayList = this.fillArrayListWithDefaultValues(anneal);
			
			length.put("IDEAL", 20.5);
			length.put("SCORE", -2.0);
		}
		
		/**
		 * This method fills an ArrayList with the keys of the given HashMap
		 * 
		 * @param map
		 * @return keys
		 */
		private ArrayList<Double> fillArrayListWithDefaultValues(HashMap<Double,Double> map){
			ArrayList<Double> keys = new ArrayList<Double>();
			Iterator iterator = (map.keySet()).iterator();
			while(iterator.hasNext()){
				keys.add((Double) iterator.next());
			}
			return keys;
		}
	
		/**
		 * This method loads the parameters which are given by a config XML file given by the user.
		 * 
		 * @param key
		 * @param value
		 */
		private void loadParameters(String key, String value){
			
			if(currentParent.toString().equals("GC")){
				gc.put(Double.parseDouble(key), Double.parseDouble(value));
				gcArrayList.add(Double.parseDouble(key));
			}if(currentParent.toString().equals("FIRST")){
				firstBase.put(key.charAt(0), Double.parseDouble(value));
			}if(currentParent.toString().equals("LAST")){
				lastBase.put(key.charAt(0), Double.parseDouble(value));
			}if(currentParent.toString().equals("PLUS_1")){
				plus1Base.put(key.charAt(0), Double.parseDouble(value));
			}if(currentParent.toString().equals("PLUS_2")){
				plus2Base.put(key.charAt(0), Double.parseDouble(value));
			}if(currentParent.toString().equals("LENGTH")){
				length.put(currentTag, Double.parseDouble(value));
			}if(currentParent.toString().equals("HOMOPOLY")){
				homopoly.put(currentTag, Double.parseDouble(value));
			}if(currentParent.toString().equals("MAX_OFFSET")){
				maxOffset.put(currentTag, Double.parseDouble(value));
			}if(currentParent==currentNode){
				String cP = currentParent.toString();
				repeatAndBackfoldAndNPenalty.put(cP, Double.parseDouble(value));		
			}if(currentParent.toString().equals("OFFSET")){
				offset.put(Double.parseDouble(key), Double.parseDouble(value));
				offsetArrayList.add(Double.parseDouble(key));
			}if(currentParent.toString().equals("GC_0207")){
				gc0207.put(Double.parseDouble(key), Double.parseDouble(value));
				gc0207ArrayList.add(Double.parseDouble(key));
			}if(currentParent.toString().equals("AT_LAST6")){
				atLast6.put(Double.parseDouble(key), Double.parseDouble(value));
				atLast6ArrayList.add(Double.parseDouble(key));
			}if(currentParent.toString().equals("ANNEAL")){
				anneal.put(Double.parseDouble(key), Double.parseDouble(value));
				annealArrayList.add(Double.parseDouble(key));
			}
		}
		
		
		/**
		 * @see DocumentHandler
		 */
		@Override
		public void startDocument() throws Exception {
			stack = new Stack();
			
		}
		
		/**
		 * @see DocumentHandler
		 */
		@Override
		public void endDocument() throws Exception {
			stack = null;
			
		}
		
		@Override
		public void startElement(String tag, Hashtable hash) throws Exception {
			String keyV;
			DefaultMutableTreeNode newNode= new DefaultMutableTreeNode(tag);
			if(stack.isEmpty()){
				stack.push(this);
			} else{
				stack.push(this);
			
			if(currentNode ==null){
				root=newNode;
			} else{
				currentNode.add(newNode);
			}
			currentNode=newNode;
			if(currentNode.getParent()==null){
				currentParent =root;
			} else{
			currentParent = (DefaultMutableTreeNode) currentNode.getParent();
			}
			currentTag = currentNode.toString();
		
			}
			Enumeration e=hash.keys();
			while(e.hasMoreElements()){
				keyV = (String)e.nextElement();
				value = (String)hash.get(keyV);
			}
			
	}
		
		/**
		 * @see DocumentHandler
		 */
		@Override
		public void endElement(String st) throws Exception {
			if(currentNode==null){	
			} else{
			currentNode =(DefaultMutableTreeNode) currentNode.getParent();
			}
			stack.pop();
		}

		/**
		 * @see DocumentHandler
		 */
		@Override
		public void value(String s) throws Exception {
			if(!s.isEmpty()&&!s.contains(" ")&&!s.matches("\r\n")){
				String score = s;
				String keyValue = value;
				if(currentNode.getParent()==currentParent&&!value.isEmpty()){
					loadParameters(keyValue, score);
				} else{
					loadParameters(currentTag,score);
				}
			}
		}
	
		/**
		 * This method parses the string objects in the given array
		 * to integers and returns them in an array.
		 * 
		 * @param object
		 * @return array
		 */
		public Integer[] makeIntArray(Object[] object){
			Integer[] intArray = new Integer[object.length];
			for(int i = 0; i<object.length;i++){
			String currentObject =	object[i].toString();
			int temp = Integer.valueOf(currentObject).intValue();
			intArray[i] = temp;
			
			}
			return intArray ;
		}
		
		/**
		 * This method calculates the score for the bases which are in position +1 and +2 after the
		 * primer sequence ended.
		 * 
		 * @param lastPlus1
		 * @param lastPlus2
		 * @return scorePlus1Plus2
		 */
		public double calcScorePlus1(char lastPlus1, char lastPlus2){
			double scorePlus1Plus2 = 0;
			double plus1 = this.plus1Base.get(lastPlus1);
			double plus2 = this.plus2Base.get(lastPlus2);
			scorePlus1Plus2 = plus1 + plus2;
			return scorePlus1Plus2;
		}
		
	
		
		/**
		 * This method gets the ratio of G and C in the whole primer sequence and returns a score
		 * according to the given scoring theme.
		 * 
		 * @param gcRatio
		 * @return GC-Level score
		 */
		public double calcScoreTotalGCLevel(double gcRatio){
			double scoreGCTotal = 0;
			Integer[] gcArray;
			Object[] tempArray=this.gcArrayList.toArray();
			gcArray = makeIntArray(tempArray);
			Arrays.sort(gcArray,Collections.reverseOrder());
			for (Integer interval : gcArray){
			if(gcRatio >=(50-interval)&&gcRatio<=(50+interval)){
						scoreGCTotal =Double.valueOf((gc.get(interval.toString())));
				}
			}
			return scoreGCTotal;
		}

		/**
		 * This method gets the primer sequence and retrieves the melting temperature for this sequence.
		 * According to the given scoring theme a score for the melting temperature is returned.
		 * 
		 * @param primerSeq
		 * @return melting temperature score
		 */
		public double calcScoreMeltingTemperature(double temperature){
			double scoreTemperature = 0;
			double minBorder = 0;
			double maxBorder = 0;
			Integer[] annealArray;
			
			Object[] tempArray = this.annealArrayList.toArray();
			annealArray = makeIntArray(tempArray);
			Arrays.sort(annealArray,Collections.reverseOrder());
			for(Integer interval : annealArray){
				minBorder = 60-interval;
				maxBorder = 60+interval;
			if(temperature>=minBorder&&temperature<=maxBorder){
				scoreTemperature = Double.valueOf(anneal.get(interval.toString())).doubleValue();
				}
			}
			return scoreTemperature;
		}
		
  

		
		/**
		 * This method gets the number of repeats and calculates a score.
		 * 
		 * @param repeatCount
		 * @return repeat-score
		 */

		public double calcScoreRepeat(double repeatCount){
			double score = 0;
			score = this.repeatAndBackfoldAndNPenalty.get("REPEAT");
			score = (score*repeatCount);
			return score;
		}
		
		/**
		 * This method gets the number of homopolys in the primer sequence and calculates a 
		 * score.
		 * 
		 * @param homopolyCount
		 * @return homopoly-score
		 */
		public double calcScoreHomopoly(int homopolyCount){
			double scoreHomopoly = 0;
			double cnt = this.homopoly.get("CNT");
			double score = this.homopoly.get("SCORE");
			if(homopolyCount >= cnt){
				scoreHomopoly = score;
			}
			return scoreHomopoly;
		}
		
		/**
		 * This method examines the given ratio of the bases G and C at the position 2 till 7 and returns a score
		 * according to the given scoring scheme.
		 * 
		 * @param gcRatio2A7
		 * @return GC-Level at position 2 and 7 score 
		 */
		public double calcScoreGCLevel2A7(double gcRatio2A7){
			double scoreGC2A7 = 0;
			Integer[] gc0207Array;
			Object[] tempArray = this.gc0207ArrayList.toArray();
			gc0207Array = makeIntArray(tempArray);
			Arrays.sort(gc0207Array);
			for(Integer border : gc0207Array){
				if(gcRatio2A7>=border){
					scoreGC2A7 = Double.valueOf((this.gc0207.get(border.toString())));
				}
			}
			return scoreGC2A7;
		}
		
		/**
		 * This method examines the given ratio for AT in the last 6 positions of the primer sequence
		 * and returns a score according to the given score-scheme.
		 * 
		 * @param ATLast6Ratio
		 * @return AT at last 6 Base score
		 */
		public double calcScoreLast6(double ATLast6Ratio){
			double scoreLast6Bases =0;
			Integer[] ATLast6Array;
			Object[] tempArray = this.atLast6ArrayList.toArray();
			ATLast6Array = makeIntArray(tempArray);
			Arrays.sort(ATLast6Array);
			for(Integer border : ATLast6Array){
				if(ATLast6Ratio>= border){
					scoreLast6Bases = Double.valueOf((this.atLast6.get(border.toString())));
				}
			}
			return scoreLast6Bases;
		}
		
		public double calcScoreOffset(int realstart){
			double score =0;
			Integer[] offsetArray;
			Object[] tempArray = this.offsetArrayList.toArray();
			offsetArray = makeIntArray(tempArray);
			Arrays.sort(offsetArray);
			for(Integer border : offsetArray){
				if(realstart>= border){
					score = Double.valueOf((this.offset.get(border.toString())));
				}
			}
		
			return score;
		}
		
		/**
		 * This method returns the score for the given first base and last base in the primer sequence
		 * according to the given scoring scheme.
		 * 
		 * @param firstBase
		 * @param lastBase
		 * @return first and last base score
		 */
		public double calcScoreFirstBaseAndLastBase(char firstBase, char lastBase){
					double scoreFirstLastBase = 0;
					double firstScore =  this.firstBase.get(firstBase);
					double lastScore = this.lastBase.get(lastBase);
					scoreFirstLastBase=firstScore+lastScore;
					return scoreFirstLastBase;
		}
		
		/**
		 * This method tests if the primer sequence can perform a backfold. If that is the case a score
		 * is set.
		 * @param last4Base
		 * @param leftseq
		 * @return backfold-score
		 */
		public double calcScoreBackfold(char[] last4Base,char[] leftseq){
			double scoreBackfold = 0;
			String scoreString = null;
			String last4Bases = new String(last4Base);
			last4Bases = last4Bases.toUpperCase();
			String primer = new String(leftseq);
			primer.toUpperCase();
			if(primer.contains(last4Bases)){
				scoreBackfold = this.repeatAndBackfoldAndNPenalty.get("BACKFOLD");
			}
			return scoreBackfold;
		}
		
		/**
		 * This method gets the starting position of the primer in the sequence and returns a score 
		 * when the distance to the end of the contig is higher than a certain value.
		 * 
		 * @param realstart
		 * @return max-offset score
		 */
		public double calcScoreMaxOffset(int realstart){
			double scoreMaxOffset = 0;
			double mult = this.maxOffset.get("MULT");
			double distance = this.maxOffset.get("DISTANCE");
			if(realstart > distance){
				double maxOffset = realstart - distance;
				scoreMaxOffset = (maxOffset * mult);
			}
			return scoreMaxOffset;
		}
		
/**
 * This method gets the number of 'N's occuring in the primer sequence and returns a penalty for each N.
 * @param nCount
 * @return nPenalty
 */
		public double calcNPenalty(int nCount) {
			double nPenalty = 0;
			String penaltyString = this.repeatAndBackfoldAndNPenalty.get("N_PENALTY").toString();
			nPenalty = (Double.parseDouble(penaltyString))*nCount;
			return nPenalty;
		}
		
		/**
		 * This method gets the length of the primer sequence and calculates the length score
		 * according to the given scoring scheme.
		 * 
		 * @param length
		 * @return length-score
		 */
		public double calcLengthScore(int length){
			double scoreLength = 0;
			double factor = this.length.get("SCORE");
			double idealLength = this.length.get("IDEAL");
			double distance = Math.abs(idealLength - length);
			scoreLength = (distance*factor);
			return scoreLength;
		}
		public double getTemperature() {
			return temperature;
		}


		public void setTemperature(double temperature) {
			this.temperature = temperature;
		}
		


		/**
		 * This method checks the following bases in the primer sequences and
		 * retrieves the score for homopoly.
		 * 
		 * @param primerSeq
		 * @return scoreHomopoly
		 */

		public double getHomopolyScore(char[] primerSeq) {
			double scoreHomopoly = 0;
			double temp = 0;
			int homCount = 0;
			char prevBase = 'X';
			char currentBase;
			for (int i = 0; i < primerSeq.length; i++) {
				currentBase = primerSeq[i];
				if (currentBase == prevBase) {
					homCount++;
				} else {
					homCount = 0;
				}
				prevBase = currentBase;
				temp += this.calcScoreHomopoly(homCount);

			}
			scoreHomopoly = temp;
			return scoreHomopoly;
		}

		/**
		 * This method retrieves each ends of the primer sequence and turns the last
		 * four bases into the reverse complement, so the possibilty of a backfold
		 * within the primer can be scored.
		 * 
		 * @param primerSeq
		 * @return scoreBackfold
		 */

		public double getBackfoldScore(char[] primerSeq) {
//			double scoreBackfold = 0;
//			char[] last4 = new char[4];
//			char[] last4Bases;
//			char[] primerSeqMinusEight = new char[primerSeq.length - 8];
//			System.arraycopy(primerSeq, (primerSeq.length - 4), last4, 0, 4);
//			System.arraycopy(primerSeq, 0, primerSeqMinusEight, 0,
//					(primerSeq.length - 8));
//			last4Bases = base.getReverseComplement(last4);
//			char[] leftSeq = primerSeqMinusEight;
//			scoreBackfold = this.calcScoreBackfold(last4Bases, leftSeq);
			//TODO auch mit smith waterman berechnen
			return -100000;
		}

		/**
		 * This method retrieves the first and the last base of a primer sequence
		 * according to its direction and then returns the score for the given bases
		 * at those positions.
		 * 
		 * @param primerSeq
		 * @param direction
		 * @return scoreFirstLastBase
		 */

		public double getFirstAndLastBaseScore(char[] primerSeq) {
			double scoreFirstLastBase = 0;
			char first = primerSeq[0];
			char last = primerSeq[primerSeq.length - 1];
			first = Character.toUpperCase(first);
			last = Character.toUpperCase(last);
			scoreFirstLastBase = this.calcScoreFirstBaseAndLastBase(first, last);
			return scoreFirstLastBase;
		}

		/**
		 * This method counts the 'N's in a given primer sequence and returns the
		 * score for the N-penalty.
		 * 
		 * @param PrimerSeq
		 * @return scoreNPenalty
		 */
		public double getNPenalty(char[] PrimerSeq) {
			double scoreNPenalty = 0;
			int count = 0;
			for (char i : PrimerSeq) {
				if (i == 'N' || i == 'n') {
					count++;
				}
			}
			scoreNPenalty = this.calcNPenalty(count);
			return scoreNPenalty;
		}

		/**
		 * This method retrieves the score for the given primer length
		 * 
		 * @param primerLength
		 * @return
		 */

		public double getLengthScore(int primerLength) {
			double scoreLength = 0;
			scoreLength = this.calcLengthScore(primerLength);
			return scoreLength;
		}

		/**
		 * This method calculates the ratio of AT at the last six bases and then
		 * retrieves the score for the ratio of the given primer sequences according
		 * to its direction.
		 * 
		 * @param primerSeq
		 * @param direction
		 * @return scoreLast6Bases
		 */

		public double getLast6Score(char[] primerSeq) {
			double scoreLast6Bases = 0;
			double last6Ratio = 0;
			double ATLevelAtLast6 = 0;
			for (int i = 1; i <= 6; i++) {
				if (primerSeq[(primerSeq.length - i)] == 'A'
						|| primerSeq[(primerSeq.length - i)] == 'a'
						|| primerSeq[(primerSeq.length - i)] == 'T'
						|| primerSeq[(primerSeq.length - i)] == 't') {
					ATLevelAtLast6++;
				}
			}
			last6Ratio = (ATLevelAtLast6 / 6 * 100);
			scoreLast6Bases = this.calcScoreLast6(last6Ratio);
			return scoreLast6Bases;
		}

		/**
		 * This method calculated the total GC-ratio and the ratio or the ratio for
		 * GC at positions 2-7 and then retrieves the score for one of those ratios
		 * of the given sequence depending on the boolean totalGC
		 * 
		 * @param primerSeq
		 * @param totalGC
		 * @param direction
		 * @return scoreTotalGC/scoreGC2A7
		 */
		public double getGCScore(char[] primerSeq, boolean totalGC) {
			double scoreTotalGC = 0;
			double scoreGC2A7 = 0;
			int gcLevel = 0;
			int gcLevel2A7 = 0;
			double gcRatio = 0;
			double gcRatio2A7 = 0;
			for (int i = 0; i < primerSeq.length; i++) {
				if (primerSeq[i] == 'G' || primerSeq[i] == 'g'
						|| primerSeq[i] == 'C' || primerSeq[i] == 'c') {
					gcLevel++;
					if (i > 0 && i < 7) {
						gcLevel2A7++;
					}
				}
			}

			if (totalGC) {
				gcRatio = (float) gcLevel / (float) (primerSeq.length + 1) * 100;
				scoreTotalGC = this.calcScoreTotalGCLevel(gcRatio);
				return scoreTotalGC;
			} else {
				gcRatio2A7 = (float) gcLevel2A7 / 6 * 100;
				scoreGC2A7 = this.calcScoreGCLevel2A7(gcRatio2A7);
				return scoreGC2A7;
			}
		}

		/**
		 * This method calculates the realstart position of the primer in the contig
		 * sequence and retrieves the score for the offset of the given primer.
		 * 
		 * @param offset
		 * @param primerLength
		 * @param direction
		 * @return scoreOffset
		 */
		public double getOffsetsScore(int distanceToBorder) {
			double scoreOffset = 0;
				scoreOffset = this.calcScoreOffset(distanceToBorder)
						+ this.calcScoreMaxOffset(distanceToBorder);
				return scoreOffset;
		}

		/**
		 * This method retrieves the score for the two bases which follow after the
		 * primer sequence.
		 * 
		 * @param plus1
		 * @param plus2
		 * @return scorePlus1Plus2
		 */
		public double getPlus1Plus2Score(char plus1, char plus2) {
			double scorePlus1Plus2 = 0;
			plus1 = Character.toUpperCase(plus1);
			plus2 = Character.toUpperCase(plus2);
			scorePlus1Plus2 = this.calcScorePlus1(plus1, plus2);
			return scorePlus1Plus2;
		}

		/**
		 * This method counts the lowercase letters, which represents the repeats in
		 * the sequence and retrieves the score for those repeats.
		 * 
		 * @param primerSeq
		 * @return scoreRepeat
		 */

		public double getRepeatScore(char[] primerSeq) {
			double scoreRepeat = 0;
			double repeatCount = 0;
			for (int i = 0; i < primerSeq.length; i++) {
				if (primerSeq[i] == 'a' || primerSeq[i] == 't'
						|| primerSeq[i] == 'g' || primerSeq[i] == 'c') {
					repeatCount++;
				}
			}
			scoreRepeat = this.calcScoreRepeat(repeatCount);
			return scoreRepeat;
		}
}
	
