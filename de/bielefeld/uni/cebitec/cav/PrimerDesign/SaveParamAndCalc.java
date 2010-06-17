package de.bielefeld.uni.cebitec.cav.PrimerDesign;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Stack;


import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
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
		double mintemp = 0;
		
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
			}if(currentParent.toString().equals("GC0207")){
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
			double score = 0;
			String plus1 = this.plus1Base.get(lastPlus1).toString();
			String plus2 = this.plus2Base.get(lastPlus2).toString();
			score = Double.parseDouble(plus1);
			score = score + Double.parseDouble(plus2);
			return score;
		}
		
		/**
		 * 
		 * @param gcRatio
		 * @return GC-Level score
		 */
		public double calcScoreTotalGCLevel(double gcRatio){
			Object[] tempArray=this.gcArray.toArray();
			Arrays.sort(tempArray,Collections.reverseOrder());
			double score = 0;
			for (Object key : tempArray){
			String temp = key.toString();
			int t = Integer.valueOf(temp).intValue();
			if(gcRatio >=(50-t)&&gcRatio<=(50+t)){
						score =Double.valueOf((gc.get(key)));
				}
			}
			return score;
		}

		/**
		 * 
		 * @param seq
		 * @return melting temperature score
		 */
		public double calcScoreAnnealTemp(char[] seq){
			double score = 0;
			MeltingTemp melt = new MeltingTemp();
			mintemp = melt.calcTemp(seq);
			Object[] tempArray = this.annealArray.toArray();
			Arrays.sort(tempArray,Collections.reverseOrder());
			for(Object key : tempArray){
				String temp = key.toString();
				double interval = Double.valueOf(temp).doubleValue();
				double border = 60-interval;
				double border2 = 60+interval;
			if(mintemp>=border&&mintemp<=border2){
				score = Double.valueOf(anneal.get(temp)).doubleValue();
				} 
			}
			return score;
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
			double score = 0;
			String  tempCNT = this.homopoly.get("CNT");
			String tempScore = this.homopoly.get("SCORE");
			int cnt = Integer.valueOf(tempCNT).intValue();
			int s = Integer.valueOf(tempScore).intValue();
			if(homopolyCount >= cnt){
				score = s;
			}
			return score;
		}
		
		/**
		 * 
		 * @param gcRatio2A7
		 * @return GC-Level at position 2 and 7 score 
		 */
		public double calcScoreGCLevel2A7(double gcRatio2A7){
			double score = 0;
			Object[] tempArray = this.gc0207Array.toArray();
			Arrays.sort(tempArray);
			for(Object key : tempArray){
				String temp = key.toString();
				int t = Integer.valueOf(temp).intValue();
				if(gcRatio2A7>=t){
					score = Double.valueOf((this.gc0207.get(key)));
				}
			}
			return score;
		}
		
		/**
		 * 
		 * @param ATLast6Ratio
		 * @return AT at last 6 Base score
		 */
		public double calcScoreLast6(double ATLast6Ratio){
			double score =0;
			Object[] tempArray = this.atLast6Array.toArray();
			Arrays.sort(tempArray);
			for(Object key : tempArray){
				String temp = key.toString();
				int t = Integer.valueOf(temp).intValue();
				if(ATLast6Ratio>= t){
					score = Double.valueOf((this.atLast6.get(key)));
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
					double score = 0;
					String tempFirst = this.firstBase.get(firstBase);
					double firstScore = Double.parseDouble(tempFirst);
					String tempLast = this.lastBase.get(lastBase);
					double lastScore = Double.parseDouble(tempLast);
					score=firstScore+lastScore;
					return score;
		}
		
		/**
		 * 		
		 * @param last4Base
		 * @param leftseq
		 * @return backfold-score
		 */
		public double calcScoreBackfold(char[] last4Base,char[] leftseq){
			double score = 0;
			String last4Bases = new String(last4Base);
			String primer = new String(leftseq);
			if(primer.contains(last4Bases)){
				String tempScore = this.repeatAndBackfoldAndNPenalty.get("BACKFOLD");
				score = Double.valueOf(tempScore).doubleValue();
			} else{
				score = 0;
			}
			return score;
		}
		
		/**
		 * 
		 * @param realstart
		 * @return offset-score
		 */
		public double calcScoreOffset(int realstart){
			double score = 0;
			Object[] tempArray = this.offsetArray.toArray();
			Arrays.sort(tempArray);
			for(Object key : tempArray){
				String temp = key.toString();
				int t = Integer.valueOf(temp).intValue();
				if(realstart>=t){
					score = Double.valueOf((this.offset.get(key)));
				}
			}
			System.out.println(score);
			return score;
		}
		
		/**
		 * 
		 * @param realstart
		 * @return max-offset score
		 */
		public double calcScoreMaxOffset(int realstart){
			double score = 0;
			String distance = this.maxOffset.get("DISTANCE");
			String mult = this.maxOffset.get("MULT");
			int m = Integer.valueOf(mult).intValue();
			int dis = Integer.valueOf(distance).intValue();
			if(realstart > dis){
				//System.out.println(realstart);
				int temp = realstart - dis;
				//System.out.println("test ="+temp);
				score = (temp * m);
			}
			return score;
		}

		public double calcNPenalty(int nCount) {
			double score = 0;
			String temp = this.repeatAndBackfoldAndNPenalty.get("N_PENALTY").toString();
			score = (Double.parseDouble(temp))*nCount;
			return score;
		}
		
		/**
		 * 
		 * @param length
		 * @return length-score
		 */
		public double calcLengthScore(int length){
			double score = 0;
			String idealTemp = this.length.get("IDEAL");
			String sTemp = this.length.get("SCORE");
			double s = Double.parseDouble(sTemp);
			double ideal = Double.parseDouble(idealTemp);
			double dis = Math.abs(ideal - length);
			score = (dis*s);
			return score;
		}
		
		public double getMintemp() {
			return mintemp;
		}

		public void setMintemp(double mintemp) {
			this.mintemp = mintemp;
		}
	}
	
