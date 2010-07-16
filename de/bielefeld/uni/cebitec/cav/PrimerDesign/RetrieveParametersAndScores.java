package de.bielefeld.uni.cebitec.cav.PrimerDesign;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;


import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
/**
 * 
 * @author yherrmann
 *
 */
	final class RetrieveParametersAndScores implements DocumentHandler {
		private DefaultMutableTreeNode root, currentNode, currentParent;
		private String currentTag = null;
		private String value = null;
		private HashMap<String, String> firstBase = new HashMap<String, String>();
		private HashMap<String, String> lastBase = new HashMap<String, String>();
		private HashMap<String, String>	plus1Base= new HashMap<String, String>();
		private HashMap<String, String>	plus2Base= new HashMap<String, String>();
		private HashMap<String, String>	length= new HashMap<String, String>();
		private HashMap<String, String>	homopoly= new HashMap<String, String>();
		private HashMap<String, String>	maxOffset= new HashMap<String, String>();
		private HashMap<String, String> gc = new HashMap<String, String>();
		private HashMap<String, String> offset = new HashMap<String, String>();
		private HashMap<String, String> atLast6 = new HashMap<String, String>();
		private HashMap<String, String> gc0207 = new HashMap<String, String>();
		private HashMap<String, String> anneal = new HashMap<String, String>();
		private HashMap<String, String> repeatAndBackfoldAndNPenalty= new HashMap<String, String>();
		private ArrayList<String> gcArrayList = new ArrayList<String>();
		private ArrayList<String> annealArrayList = new ArrayList<String>();
		private ArrayList<String> atLast6ArrayList = new ArrayList<String>();
		private ArrayList<String> gc0207ArrayList = new ArrayList<String>();
		private ArrayList<String> offsetArrayList = new ArrayList<String>();
		private Stack stack = null;
		double temperature = 0;
		
		public RetrieveParametersAndScores(){
				this.defaultParameters();
		}

		private void defaultParameters(){
			
			gc.put("10", "100");
			gc.put("15", "-100");
			gc.put("20", "-300");
			gc.put("25", "-800");
			gc.put("50", "-1500");
			
			gcArrayList = this.fillArrayListWithDefaultValues(gc);
			
			firstBase.put("A", "45");
			firstBase.put("T", "45");
			firstBase.put("C", "0");
			firstBase.put("G", "0");
			firstBase.put("N", "-1500");
			
			lastBase.put("A", "130");
			lastBase.put("T", "100");
			lastBase.put("C", "0");
			lastBase.put("G", "0");
			lastBase.put("N", "-1500");
			
			plus1Base.put("A", "130");
			plus1Base.put("T", "100");
			plus1Base.put("C", "0");
			plus1Base.put("G", "0");
			plus1Base.put("N", "0");
			
			plus2Base.put("A", "80");
			plus2Base.put("T", "80");
			plus2Base.put("C", "0");
			plus2Base.put("G", "0");
			plus2Base.put("N", "0");
			
			offset.put("0", "-1000");
			offset.put("30", "-600");
			offset.put("50", "-500");
			offset.put("80", "-400");
			offset.put("110", "-50");
			offset.put("150", "250");
			
			offsetArrayList = this.fillArrayListWithDefaultValues(offset);
			
			gc0207.put("79", "156");
			gc0207.put("60", "-83");
			gc0207.put("50", "-320");
			gc0207.put("0", "-1500");
			
			gc0207ArrayList = this.fillArrayListWithDefaultValues(gc0207);
			
			atLast6.put("79", "156");
			atLast6.put("60", "-83");
			atLast6.put("50", "-320");
			atLast6.put("0", "-1500");
			
			atLast6ArrayList = this.fillArrayListWithDefaultValues(atLast6);
			
			maxOffset.put("DISTANCE", "150");
			maxOffset.put("MULT","-2");
			
			repeatAndBackfoldAndNPenalty.put("REPEAT", "-78");
			repeatAndBackfoldAndNPenalty.put("N_PENALTY", "-1500");
			repeatAndBackfoldAndNPenalty.put("BACKFOLD", "-1500");
			
			homopoly.put("CNT", "3");
			homopoly.put("SCORE", "-200");
			
			anneal.put("2", "200");
			anneal.put("6","0");
			anneal.put("100", "-1500");
			
			annealArrayList = this.fillArrayListWithDefaultValues(anneal);
			
			length.put("IDEAL", "20.5");
			length.put("SCORE", "-2");
		}
		
		private ArrayList<String> fillArrayListWithDefaultValues(HashMap<String,String> map){
			ArrayList<String> keys = new ArrayList<String>();
			Iterator iterator = (map.keySet()).iterator();
			while(iterator.hasNext()){
				keys.add(iterator.next().toString());
			}
			return keys;
		}
		/**
		 * 
		 * @param key
		 * @param value
		 */
		private void loadParameters(String key, String value){
			
			if(currentParent.toString().equals("GC")){
				gc.put(key, value);
				gcArrayList.add(key);
			}if(currentParent.toString().equals("FIRST")){
				firstBase.put(key, value);
			}if(currentParent.toString().equals("LAST")){
				lastBase.put(key, value);
			}if(currentParent.toString().equals("PLUS_1")){
				plus1Base.put(key, value);
			}if(currentParent.toString().equals("PLUS_2")){
				plus2Base.put(key, value);
			}if(currentParent.toString().equals("LENGTH")){
				length.put(currentTag, value);
			}if(currentParent.toString().equals("HOMOPOLY")){
				homopoly.put(currentTag, value);
			}if(currentParent.toString().equals("MAX_OFFSET")){
				maxOffset.put(currentTag, value);
			}if(currentParent==currentNode){
				String cP = currentParent.toString();
				repeatAndBackfoldAndNPenalty.put(cP, value);		
			}if(currentParent.toString().equals("OFFSET")){
				offset.put(key, value);
				offsetArrayList.add(key);
			}if(currentParent.toString().equals("GC_0207")){
				gc0207.put(key, value);
				gc0207ArrayList.add(key);
			}if(currentParent.toString().equals("AT_LAST6")){
				atLast6.put(key, value);
				atLast6ArrayList.add(key);
			}if(currentParent.toString().equals("ANNEAL")){
				anneal.put(key, value);
				annealArrayList.add(key);
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
	
		
		public Integer[] makeIntArray(Object[] o){
			Integer[] array = new Integer[o.length];
			for(int i = 0; i<o.length;i++){
			String t =	o[i].toString();
			int temp = Integer.valueOf(t).intValue();
			array[i] = temp;
			}
			return array ;
		}
		
		/**
		 * 
		 * @param lastPlus1
		 * @param lastPlus2
		 * @return score for base at position +1
		 */
		public double calcScorePlus1(String lastPlus1, String lastPlus2){
			double scorePlus1Plus2 = 0;
			String plus1 = this.plus1Base.get(lastPlus1).toString();
			String plus2 = this.plus2Base.get(lastPlus2).toString();
			scorePlus1Plus2 = Double.parseDouble(plus1);
			scorePlus1Plus2 = scorePlus1Plus2 + Double.parseDouble(plus2);
			return scorePlus1Plus2;
		}
		
	
		
		/**
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
		 * 
		 * @param seq
		 * @return melting temperature score
		 */
		public double calcScoreMeltingTemperature(char[] seq){
			double scoreTemperature = 0;
			double temperature = 0;
			double minBorder = 0;
			double maxBorder = 0;
			Integer[] annealArray;
			MeltingTemperature melt = new MeltingTemperature();
			temperature = melt.calcTemp(seq);
			this.setTemperature(temperature);
			
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
		 * 
		 * @param repeatCount
		 * @return repeat-score
		 */
		//noch bearbeiten
		public double calcScoreRepeat(double repeatCount){
			double score = 0;
			String tempScore = this.repeatAndBackfoldAndNPenalty.get("REPEAT");
			double s = Double.parseDouble(tempScore);
			score = (s*repeatCount);
			return score;
		}
		
		/**
		 * 
		 * @param homopolyCount
		 * @return homopoly-score
		 */
		public double calcScoreHomopoly(int homopolyCount){
			double scoreHomopoly = 0;
			int cnt = 0;
			int score = 0;
			String cntString = this.homopoly.get("CNT");
			String scoreString = this.homopoly.get("SCORE");
			cnt = Integer.valueOf(cntString).intValue();
			score = Integer.valueOf(scoreString).intValue();
			if(homopolyCount >= cnt){
				scoreHomopoly = score;
			}
			return scoreHomopoly;
		}
		
		/**
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
		 * 
		 * @param firstBase
		 * @param lastBase
		 * @return first and last base score
		 */
		public double calcScoreFirstBaseAndLastBase(String firstBase, String lastBase){
					double scoreFirstLastBase = 0;
					String firstString = this.firstBase.get(firstBase);
					double firstScore = Double.parseDouble(firstString);
					String lastString = this.lastBase.get(lastBase);
					double lastScore = Double.parseDouble(lastString);
					scoreFirstLastBase=firstScore+lastScore;
					return scoreFirstLastBase;
		}
		
		/**
		 * 		
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
				scoreString = this.repeatAndBackfoldAndNPenalty.get("BACKFOLD");
				scoreBackfold = Double.valueOf(scoreString).doubleValue();
			} else{
				scoreBackfold = 0;
			}
			return scoreBackfold;
		}
		
		/**
		 * 
		 * @param realstart
		 * @return max-offset score
		 */
		public double calcScoreMaxOffset(int realstart){
			double scoreMaxOffset = 0;
			double mult = 0;
			double distance = 0;
			String distanceString = this.maxOffset.get("DISTANCE");
			String multString = this.maxOffset.get("MULT");
			mult = Double.parseDouble(multString);
			distance = Double.parseDouble(distanceString);
			if(realstart > distance){
				double maxOffset = realstart - distance;
				scoreMaxOffset = (maxOffset * mult);
			}
			return scoreMaxOffset;
		}

		public double calcNPenalty(int nCount) {
			double nPenalty = 0;
			String penaltyString = this.repeatAndBackfoldAndNPenalty.get("N_PENALTY").toString();
			nPenalty = (Double.parseDouble(penaltyString))*nCount;
			return nPenalty;
		}
		
		/**
		 * 
		 * @param length
		 * @return length-score
		 */
		public double calcLengthScore(int length){
			double scoreLength = 0;
			double idealLength = 0;
			double factor = 0;
			double distance = 0;
			String idealString = this.length.get("IDEAL");
			String factorString = this.length.get("SCORE");
			factor = Double.parseDouble(factorString);
			idealLength = Double.parseDouble(idealString);
			distance = Math.abs(idealLength - length);
			scoreLength = (distance*factor);
			return scoreLength;
		}
		public double getTemperature() {
			return temperature;
		}


		public void setTemperature(double temperature) {
			this.temperature = temperature;
		}
		
	}
	
