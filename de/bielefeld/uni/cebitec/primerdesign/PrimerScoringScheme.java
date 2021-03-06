/***************************************************************************
 *   Copyright (C) 2010 by Yvonne Hermann, Peter Husemann                  *
 *   phuseman  a t  cebitec.uni-bielefeld.de                               *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package de.bielefeld.uni.cebitec.primerdesign;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class contains the parameters an the methods to calculate the score for each primer candidate.
 * It loads default parameters or retrieves parameters from a given XML file with the SAXParser.
 * 
 * **************************************************************************************************
 * 																									*
 * The parameters are from a config file (primer_search_default_config.xml), which was developed	*
 * by Jochen Blom and Dr. Christian Rueckert.														*
 * The methods to calculate the score for a primer are a reproduction of the perl-script 			*
 * (primer_search_confable.pl) developed and written by Jochen Blom and Dr. Christian Rueckert.		*
 * 																									*
 *																									*
 ****************************************************************************************************/

	final class PrimerScoringScheme extends DefaultHandler{
		//The following HashMaps and Arrays save the parameters(needed for the score calculation)
		private HashMap<Character, Double> firstBase = new HashMap<Character, Double>();
		private HashMap<Character, Double> lastBase = new HashMap<Character, Double>();
		private HashMap<Character, Double>	plus1Base= new HashMap<Character, Double>();
		private HashMap<Character, Double>	plus2Base= new HashMap<Character, Double>();
		private HashMap<Double, Double> gc = new HashMap<Double, Double>();
		private HashMap<Double, Double> offset = new HashMap<Double, Double>();
		private HashMap<Double, Double> atLast6 = new HashMap<Double, Double>();
		private HashMap<Double, Double> gc0207 = new HashMap<Double, Double>();
		private HashMap<Double, Double> anneal = new HashMap<Double, Double>();
		private Integer[] gcArray;
		private Integer[] annealArray;
		private Integer[] ATLast6Array;
		private Integer[] gc0207Array;
		private Integer[] offsetArray;
		private Bases base;
		private SimpleSmithWatermanPrimerAligner swa;
		//following global variables are intialized with default parameters.
		// If a XML file is given, these are set to the retrieved parameters.
		private double homopolyCNT = 3.0;
		private double homopolySCORE = -200.0;
		private double lengthIDEAL = 20.5;
		private double lengthSCORE= -2.0;
		private double npenalty = -1500.0;
		private double repeat = -1500.0;
		private double maxoffsetDISTANCE = 150.0;
		private double maxoffsetMULT= -2.0;
		private double backfold = -1500.0;
		//globale variables are needed to parse XML-File
		private File configFile = null;
		private String elementName = null;
		private String parentElementName = null;
		private String key = null;
		private String value = null;
		
		/**
		 * Constructor of the class.
		 * Fills the parameter containers with default values and gets instances of the need 'help' classes.
		 */
		
		public PrimerScoringScheme(){
				this.defaultParameters();
				base = Bases.getInstance();
				swa = new SimpleSmithWatermanPrimerAligner();
		}
		/**
		 * Constructor of the class.
		 * When a config-file is given the parsing of XML file is set up and puts the retrieved parameters in the
		 * parameter containers.
		 */
		public PrimerScoringScheme(File config){
			configFile = config;
			base = Bases.getInstance();
			swa = new SimpleSmithWatermanPrimerAligner();
		}

		/**
		 * This method calculates scores for each attribute of the primer and returns a
		 * total score for it.
		 * 
		 * @param primer
		 * @return primer score
		 */
		public double calculatePrimerScore(Primer primer) {
			char[] primerSeq = primer.getPrimerSeq();
				
				double scoreTemperature = this.calcScoreMeltingTemperature(primer.getPrimerTemperature());
				//when the temperature scores -1 the sequence contains characters which caused an error in the 
				//melting temperature calculation. These primers will not be considered in futher calculations
				if (scoreTemperature != -1) {
					double scoreLength = this.getLengthScore(primer.getPrimerLength());
					double scoreGCTotal = this.getGCScore(primerSeq);
					double scoreFirstLastBase = this.getFirstAndLastBaseScore(primerSeq);
					double scoreBackfold = this.getBackfoldScore(primerSeq);
					double scoreLast6 = this.getLast6Score(primerSeq);
					double scorePlus1Plus2 = this.getPlus1Plus2Score(primer.getLastPlus1(), primer.getLastPlus2());
					double scoreOffset = this.getOffsetsScore(primer.getDistanceFromContigBorder(),primer.getPrimerLength());
					double scoreNPenalty = this.getNPenalty(primerSeq);
					double scoreHomopoly = this.getHomopolyScore(primerSeq);
					double scoreRepeat = this.getRepeatScore(primerSeq);
					double primerScore = scoreGCTotal + scoreRepeat
							+ scoreFirstLastBase + scoreNPenalty + scoreBackfold
							+ scoreLength + scoreLast6 + scoreOffset
							+ scorePlus1Plus2 + scoreTemperature + scoreHomopoly;
					return primerScore;
					
			}
				return -Double.MAX_VALUE;
		}

		/**
		 * This method loads default parameters which are based on the primer_search_default_config file.
		 * Parameters are chosen by Jochen Blom and Dr. Christian Rueckert.
		 */
		private void defaultParameters(){
			ArrayList<Double> gcArrayList = new ArrayList<Double>();
			ArrayList<Double> annealArrayList = new ArrayList<Double>();
			ArrayList<Double> atLast6ArrayList = new ArrayList<Double>();
			ArrayList<Double> gc0207ArrayList = new ArrayList<Double>();
			ArrayList<Double> offsetArrayList = new ArrayList<Double>();
			
			gc.put(10.0, 100.0);
			gc.put(15.0, -100.0);
			gc.put(20.0, -300.0);
			gc.put(25.0, -800.0);
			gc.put(50.0, -1500.0);
	
			gcArrayList = fillArrayListWithDefaultValues(gc);
			this.gcArray =this.makeIntArray((gcArrayList.toArray()));
		
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
			offset.put(150.0, 250.0);
			
			offsetArrayList = fillArrayListWithDefaultValues(offset);
			this.offsetArray =this.makeIntArray((offsetArrayList.toArray()));
			
			gc0207.put(79.0, 156.0);
			gc0207.put(60.0, -83.0);
			gc0207.put(50.0, -320.0);
			gc0207.put(0.0, -1500.0);
			
			gc0207ArrayList = fillArrayListWithDefaultValues(gc0207);
			this.gc0207Array =this.makeIntArray((gc0207ArrayList.toArray()));
			
			atLast6.put(79.0, 156.0);
			atLast6.put(60.0, -83.0);
			atLast6.put(50.0, -320.0);
			atLast6.put(0.0, -1500.0);
			
			atLast6ArrayList = fillArrayListWithDefaultValues(atLast6);
			this.ATLast6Array =this.makeIntArray((atLast6ArrayList.toArray()));
			
			anneal.put(2.0, 200.0);
			anneal.put(6.0,0.0);
			anneal.put(100.0, -1500.0);
			
			annealArrayList = fillArrayListWithDefaultValues(anneal);
			this.annealArray =this.makeIntArray((annealArrayList.toArray()));
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
		 * This method parses the objects in the given array
		 * to integers and returns them in an array.
		 * 
		 * @param object
		 * @return array
		 */
		private Integer[] makeIntArray(Object[] object){
			Integer[] intArray = new Integer[object.length];
			for(int i = 0; i<object.length;i++){
			String currentObject =	object[i].toString();
			double doubleTemp = Double.parseDouble(currentObject);
			int temp = (int) doubleTemp;
			intArray[i] = temp;
			
			}
			return intArray ;
		}
		
	
		
		/**
		 * This method gets the ratio of G and C in the whole primer sequence.
		 * It returns a score for GC-level based on the given parameters.
		 * 
		 * @param gcRatio
		 * @return GC-Level score
		 */
		private double calcScoreTotalGCLevel(double gcRatio){
			double scoreGCTotal = 0;
			Arrays.sort(gcArray,Collections.reverseOrder());
			//Paramters are used to form intervals and gives back the score
			//where the GC-ratio is still in the interval
			for (Integer interval : gcArray){
			if(gcRatio >=(50-interval)&&gcRatio<=(50+interval)){
						scoreGCTotal =gc.get((double)interval);
				}
			}
			return scoreGCTotal;
		}

		/**
		 * This method gets the melting temperature for this primer.
		 * According to the given parameters the score for the melting temperature is returned.
		 * 
		 * @param temperature
		 * @return melting temperature score
		 */
		private double calcScoreMeltingTemperature(double temperature){
			double scoreTemperature = 0;
			double minBorder = 0;
			double maxBorder = 0;
			Arrays.sort(annealArray,Collections.reverseOrder());
			//Paramters are used to form intervals and gives back the score
			//where the temperature is still in the interval
			for(Integer interval : annealArray){
				minBorder = 60-interval;
				maxBorder = 60+interval;
			if(temperature>=minBorder&&temperature<=maxBorder){
				scoreTemperature = anneal.get((double)interval);
				}
			}
			return scoreTemperature;
		}
		
		/**
		 * This method examines the given ratio of the bases G and C at the position 2 till 7.
		 * It returns a score according to the used parameters.
		 * 
		 * @param gcRatio2A7
		 * @return GC-Level at position 2 and 7 score 
		 */
		private double calcScoreGCLevel2A7(double gcRatio2A7){
			double scoreGC2A7 = 0;
			Arrays.sort(gc0207Array);
			//Paramters are used to form intervals and gives back the score
			//where the GC0207-ratio is still in the interval
			for(Integer border : gc0207Array){
				if(gcRatio2A7>=border){
					scoreGC2A7 = this.gc0207.get((double)border);
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
			Arrays.sort(ATLast6Array);
			//Paramters are used to form intervals and gives back the score
			//where the AT-ratio in the last 6 bases is still in the interval
			for(Integer border : ATLast6Array){
				if(ATLast6Ratio>= border){
					scoreLast6Bases = this.atLast6.get((double)border);
				}
			}
			return scoreLast6Bases;
		}
		
		/**
		 * This method calculates the score for the offset of a primer to the end
		 * of the contig.
		 * 
		 * @param realstart
		 * @return offsetScore
		 */
		private double calcScoreOffset(int realstart){
			double score = 0;
			Arrays.sort(offsetArray);
			//Paramters are used to form intervals and gives back the score
			//where the start-position of the primer is still in the interval
			for(Integer border : offsetArray){
				if(realstart>= border){
					score = this.offset.get((double)border);
				}
			}
		
			return score;
		}
	
		
		/**
		 * This method gets the starting position of the primer in the sequence and returns a score 
		 * when the distance to the end of the contig is higher than a certain value.
		 * 
		 * @param realstart
		 * @return max-offset score
		 */
		private double calcScoreMaxOffset(int realstart){
			double scoreMaxOffset = 0;
			if(realstart > maxoffsetDISTANCE){
				double maxOffset = realstart - maxoffsetDISTANCE;
				scoreMaxOffset = (maxOffset * maxoffsetMULT);
			}
			return scoreMaxOffset;
		}

		/**
		 * This method checks the primer sequences and retrieves the score for homopoly.
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
				if(homCount >= homopolyCNT){
					temp += homopolySCORE;
				}
			}
			scoreHomopoly = temp;
			return scoreHomopoly;
		}

		/**
		 * This method checks whether the last 4 Bases of the primer can backfold to any part of the
		 * primer sequence.
		 * 
		 * @param primerSeq
		 * @return scoreBackfold
		 */

		public double getBackfoldScore(char[] primerSeq) {
			double scoreBackfold = 0;
			char[] reverseCompl = base.getReverseComplement(primerSeq);
			double smwScore = swa.getAlignmentScore(primerSeq, reverseCompl,4,(primerSeq.length-8));
			if(smwScore>=8){
			scoreBackfold = backfold;
			}
			return scoreBackfold;
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
			double firstScore =  this.firstBase.get(first);
			double lastScore = this.lastBase.get(last);
			scoreFirstLastBase=firstScore+lastScore;
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
			int nCount = 0;
			for (char i : PrimerSeq) {
				if (i == 'N' || i == 'n') {
					nCount++;
				}
			}
			scoreNPenalty = npenalty*nCount;
			return scoreNPenalty;
		}

		/**
		 * This method calculates the score for the given primer length
		 * 
		 * @param primerLength
		 * @return scoreLength
		 */

		public double getLengthScore(int primerLength) {
			double distance = Math.abs(lengthIDEAL - primerLength);
			double scoreLength = (distance*lengthSCORE);
			return scoreLength;
		}

		/**
		 * This method calculates the ratio of AT at the last six bases and then
		 * retrieves the score for the ratio of the given primer sequences.
		 * 
		 * @param primerSeq
		 * @param direction
		 * @return scoreLast6Bases
		 */

		public double getLast6Score(char[] primerSeq) {
			double ATLevelAtLast6 = 0;
			for (int i = 1; i <= 6; i++) {
				if (primerSeq[(primerSeq.length - i)] == 'A'
						|| primerSeq[(primerSeq.length - i)] == 'a'
						|| primerSeq[(primerSeq.length - i)] == 'T'
						|| primerSeq[(primerSeq.length - i)] == 't') {
					ATLevelAtLast6++;
				}
			}
			double last6Ratio = (ATLevelAtLast6 / 6 * 100);
			double scoreLast6Bases = this.calcScoreLast6(last6Ratio);
			return scoreLast6Bases;
		}

		/**
		 * This method calculated the total GC-ratio and the GC-ratio at positions 2-7.
		 * It returns the score for both GC-ratios based on the given parameters.
		 * 
		 * @param primerSeq
		 * @return allGCScore
		 */
		public double getGCScore(char[] primerSeq) {
			double allGCScore = 0;
			int gcLevel = 0;
			int gcLevel2A7 = 0;
			//going through the primer sequence and count 'G's and 'C's
			for (int i = 0; i < primerSeq.length; i++) {
				if (primerSeq[i] == 'G' || primerSeq[i] == 'g'
						|| primerSeq[i] == 'C' || primerSeq[i] == 'c') {
					gcLevel++;
					//only counts 'G's and 'C's when they occure at these positions
					if (i > 0 && i < 7) {
						gcLevel2A7++;
					}
				}
			}
				//gc ratio for the whole sequence
				double gcRatio = (float) gcLevel / (float) (primerSeq.length + 1) * 100.;
				double scoreTotalGC = this.calcScoreTotalGCLevel(gcRatio);
				//gc ratio only for the specific positions
				double gcRatio2A7 = (float) gcLevel2A7 / 6. * 100.;
				double scoreGC2A7 = this.calcScoreGCLevel2A7(gcRatio2A7);
			allGCScore = scoreTotalGC +scoreGC2A7;
			return allGCScore;
		}

		/**
		 * This method retrieves the score for the offset of the given primer.
		 * 
		 * @param offset
		 * @return scoreOffset
		 */
		public double getOffsetsScore(int distanceToBorder,int primerLength) {
				double scoreOffset = this.calcScoreOffset(distanceToBorder)
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
			plus1 = Character.toUpperCase(plus1);
			plus2 = Character.toUpperCase(plus2);
			double plus1Score = this.plus1Base.get(plus1);
			double plus2Score = this.plus2Base.get(plus2);
			double scorePlus1Plus2 = plus1Score + plus2Score;
			return scorePlus1Plus2;
		}

		/**
		 * This method counts the lowercase letters, which represents the repeats in
		 * the sequence and calculates the score for those repeats.
		 * 
		 * @param primerSeq
		 * @return scoreRepeat
		 */

		public double getRepeatScore(char[] primerSeq) {
			double repeatCount = 0;
			for (int i = 0; i < primerSeq.length; i++) {
				if (primerSeq[i] == 'a' || primerSeq[i] == 't'
						|| primerSeq[i] == 'g' || primerSeq[i] == 'c') {
					repeatCount++;
				}
			}
		
			double repeatScore = (repeat*repeatCount);
			return repeatScore;
		}
		

		/**
		 * This method loads the parameters which are given by a config XML file 
		 * given by the user.
		 * 
		 * @param key
		 * @param value
		 */
		private void loadParameters(String key, String value){
			ArrayList<Double> gcArrayList = new ArrayList<Double>();
			ArrayList<Double> annealArrayList = new ArrayList<Double>();
			ArrayList<Double> atLast6ArrayList = new ArrayList<Double>();
			ArrayList<Double> gc0207ArrayList = new ArrayList<Double>();
			ArrayList<Double> offsetArrayList = new ArrayList<Double>();
			if(parentElementName.equals("GC")){
				gc.put(Double.parseDouble(key), Double.parseDouble(value));
				gcArrayList.add(Double.parseDouble(key));
			}if(parentElementName.equals("FIRST")){
				firstBase.put(key.charAt(0), Double.parseDouble(value));
			}if(parentElementName.equals("LAST")){
				lastBase.put(key.charAt(0), Double.parseDouble(value));
			}if(parentElementName.equals("PLUS_1")){
				plus1Base.put(key.charAt(0), Double.parseDouble(value));
			}if(parentElementName.equals("PLUS_2")){
				plus2Base.put(key.charAt(0), Double.parseDouble(value));
			}if(elementName.equals("IDEAL")){
					lengthIDEAL = Double.parseDouble(value);
			}if(elementName.equals("SCORE")&&parentElementName.equals("LENGTH")){
					lengthSCORE = Double.parseDouble(value);
			}if(elementName.equals("CNT")){
					homopolyCNT = Double.parseDouble(value);
			}if(elementName.equals("SCORE")&&parentElementName.equals("HOMOPOLY")){
					homopolySCORE = Double.parseDouble(value);
			}if(elementName.equals("DISTANCE")){
					maxoffsetDISTANCE = Double.parseDouble(value);
			}if(elementName.equals("MULT")){
					maxoffsetMULT = Double.parseDouble(value);
			}if(parentElementName.equals("N_PENALTY")){
					npenalty = Double.parseDouble(value);
			}if(parentElementName.equals("BACKFOLD")){
					backfold = Double.parseDouble(value);
			}if(parentElementName.equals("REPEAT")){
					repeat = Double.parseDouble(value);
			}if(parentElementName.equals("OFFSET")){
				offset.put(Double.parseDouble(key), Double.parseDouble(value));
				offsetArrayList.add(Double.parseDouble(key));
			}if(parentElementName.equals("GC_0207")){
				gc0207.put(Double.parseDouble(key), Double.parseDouble(value));
				gc0207ArrayList.add(Double.parseDouble(key));
			}if(parentElementName.equals("AT_LAST6")){
				atLast6.put(Double.parseDouble(key), Double.parseDouble(value));
				atLast6ArrayList.add(Double.parseDouble(key));
			}if(parentElementName.equals("ANNEAL")){
				anneal.put(Double.parseDouble(key), Double.parseDouble(value));
				annealArrayList.add(Double.parseDouble(key));
			}
			this.gc0207Array =this.makeIntArray((gc0207ArrayList.toArray()));
			this.ATLast6Array =this.makeIntArray((atLast6ArrayList.toArray()));
			this.annealArray =this.makeIntArray((annealArrayList.toArray()));
			this.gcArray =this.makeIntArray((gcArrayList.toArray()));
			this.offsetArray =this.makeIntArray((offsetArrayList.toArray()));
		}
		
		/**
		 * This methods sets up the needed SAXParser components and parses the given file.
		 * 
		 * @throws SAXException
		 * @throws IOException
		 */
		public void setUpParser() throws SAXException, IOException{
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);
			FileReader file = new FileReader(configFile);
			xr.parse(new InputSource(file));

		}
		/**
		 * This method is called when a new XML-Element starts. 
		 */
		public void startElement (String uri, String name,String qName, Attributes atts){
			if(!name.equals("CONFIG")){
				//needs to be saved since the other element names occure more than once in the config-file
			if(parentElementName==null){
			parentElementName= name;
			}
			 elementName = name;
			 for( int i = 0; i < atts.getLength(); i++ ){
			 key = atts.getValue(i);
			 }
			}
   }

		/**
		 * This method is called when a XML-Element end-tag is found.
		 */
   public void endElement (String uri, String name, String qName){
	   if(parentElementName==name){
		   parentElementName = null; 
	   }
   }
/**
 * This method is called to retrieve the parameters from the XML-Element and calls the method to load
 * the parameters into the needed containers.
 */
   public void characters (char ch[], int start, int length){
	   value = new String(ch,start,length).trim();
	   if(value.length()>0){
		   if(key!=null){
			   this.loadParameters(key, value);
		   }else{
			   this.loadParameters(elementName, value);
		   }
	   }
   }
}
	
