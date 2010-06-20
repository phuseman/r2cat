package de.bielefeld.uni.cebitec.cav.PrimerDesign;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Stack;


import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
/**
 * 
 * @author yherrmann
 *
 */
	final class SaveParamAndCalc implements DocHandler {
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
		private ArrayList<String> gcArray = new ArrayList<String>();
		private ArrayList<String> annealArray = new ArrayList<String>();
		private ArrayList<String> atLast6Array = new ArrayList<String>();
		private ArrayList<String> gc0207Array = new ArrayList<String>();
		private ArrayList<String> offsetArray = new ArrayList<String>();
		private Stack stack = null;
		double temperature = 0;
		
		public double getTemperature() {
			return temperature;
		}


		public void setTemperature(double temperature) {
			this.temperature = temperature;
		}


		/**
		 * 
		 * @param key
		 * @param value
		 */
		private void fillingContainer(String key, String value){
			
			if(currentParent.toString().equals("GC")){
				gc.put(key, value);
				gcArray.add(key);
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
				offsetArray.add(key);
			}if(currentParent.toString().equals("GC_0207")){
				gc0207.put(key, value);
				gc0207Array.add(key);
			}if(currentParent.toString().equals("AT_LAST6")){
				atLast6.put(key, value);
				atLast6Array.add(key);
			}if(currentParent.toString().equals("ANNEAL")){
				anneal.put(key, value);
				annealArray.add(key);
			}
		}
		
		
		/**
		 * @see DocHandler
		 */
		@Override
		public void startDocument() throws Exception {
			stack = new Stack();
			
		}
		
		/**
		 * @see DocHandler
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
		 * @see DocHandler
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
		 * @see DocHandler
		 */
		@Override
		public void score(String s) throws Exception {
			if(!s.isEmpty()&&!s.contains(" ")&&!s.matches("\r\n")){
				String score = s;
				String keyValue = value;
				if(currentNode.getParent()==currentParent&&!value.isEmpty()){
					fillingContainer(keyValue, score);
				} else{
					fillingContainer(currentTag,score);
				}
			}
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
			String keyString = null;
			int borderFactor = 0;
			Object[] tempArray=this.gcArray.toArray();
			Arrays.sort(tempArray,Collections.reverseOrder());
			for (Object key : tempArray){
			keyString = key.toString();
			borderFactor = Integer.valueOf(keyString).intValue();
			if(gcRatio >=(50-borderFactor)&&gcRatio<=(50+borderFactor)){
						scoreGCTotal =Double.valueOf((gc.get(key)));
				}
			}
			return scoreGCTotal;
		}

		/**
		 * 
		 * @param seq
		 * @return melting temperature score
		 */
		//sorting umschreiben?!?!
		public double calcScoreAnnealTemp(char[] seq){
			double scoreTemperature = 0;
			double temperature = 0;
			String keyString = null;
			double border = 0;
			double interval = 0;
			double border2 = 0;
			MeltingTemp melt = new MeltingTemp();
			temperature = melt.calcTemp(seq);
			this.setTemperature(temperature);
			
			Object[] tempArray = this.annealArray.toArray();
			tempArray[0] = annealArray.get(2);
			tempArray[1] = annealArray.get(1);
			tempArray[2] = annealArray.get(0);
			for(Object key : tempArray){
				keyString = key.toString();
				interval = Double.valueOf(keyString).doubleValue();
				border = 60-interval;
				border2 = 60+interval;
			if(temperature>=border&&temperature<=border2){
				scoreTemperature = Double.valueOf(anneal.get(key)).doubleValue();
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
		//noch bearbeiten homopolyCount mit qgramIndex berechnen?
		public double calcScoreHomopoly(int homopolyCount){
			double scoreHomopoly = 0;
			int cnt = 0;
			int score = 0;
			String  cntString = this.homopoly.get("CNT");
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
			String keyString = null;
			int border = 0;
			Object[] tempArray = this.gc0207Array.toArray();
			Arrays.sort(tempArray);
			for(Object key : tempArray){
				keyString = key.toString();
				border= Integer.valueOf(keyString).intValue();
				if(gcRatio2A7>=border){
					scoreGC2A7 = Double.valueOf((this.gc0207.get(key)));
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
			int border = 0;
			String keyString = null;
			Object[] tempArray = this.atLast6Array.toArray();
			Arrays.sort(tempArray);
			for(Object key : tempArray){
				keyString = key.toString();
				border = Integer.valueOf(keyString).intValue();
				if(ATLast6Ratio>= border){
					scoreLast6Bases = Double.valueOf((this.atLast6.get(key)));
				}
			}
			return scoreLast6Bases;
		}
		
		public double calcScoreOffset(int realstart){
			double score =0;
			int border = 0;
			String keyString = null;
			Object[] tempArray = this.offsetArray.toArray();
		/*	//Arrays.sort(tempArray);
			System.out.println(tempArray[0]);
			System.out.println(tempArray[1]);
			System.out.println(tempArray[2]);
			System.out.println(tempArray[3]);
			System.out.println(tempArray[4]);
			System.out.println(tempArray[5]);*/
			for(Object key : tempArray){
				keyString= key.toString();
				border = Integer.valueOf(keyString).intValue();
				if(realstart>= border){
					score = Double.valueOf((this.offset.get(key)));
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
			String primer = new String(leftseq);
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
			int mult = 0;
			int distance = 0;
			String distanceString = this.maxOffset.get("DISTANCE");
			String multString = this.maxOffset.get("MULT");
			mult = Integer.valueOf(multString).intValue();
			distance = Integer.valueOf(distanceString).intValue();
			if(realstart > distance){
				int maxOffset = realstart - distance;
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
	
		
	}
	
