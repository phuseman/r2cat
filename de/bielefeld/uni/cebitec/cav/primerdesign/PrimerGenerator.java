/***************************************************************************
 *   Copyright (C) 2010 by Yvonne Herrmann, Peter Husemann                  *
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


package de.bielefeld.uni.cebitec.cav.primerdesign;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;
import de.bielefeld.uni.cebitec.cav.utils.AbstractProgressReporter;

/**
 * This class generates the primer candidates based on the given sequence. The
 * sequence of primer candidates has to be checked on certain biological
 * properties and are scored based on its properties.
 * 
 * *****************************************************************************************************
 * 																									   *
 * The methods to generate primer candidates are based on the perl-script (primer_search_confable.pl)  *
 * developed and written by Jochen Blom and Dr. Christian Rueckert.									   *
 * 																									   *
 * The values for the range, where primers should be localized at an contig-end, are proposition by    *
 * Dr. Christian Rueckert.																			   *
 * 																									   *
 * 																									   *
 *******************************************************************************************************/


public class PrimerGenerator {
	//instance of the class which contains the parameters and methods to calculate primer scores
	private PrimerScoringScheme scoring = null;
	private FastaFileReader fastaParser;
	// max length a primer should have
	private final int maxLength = 24;
	// min length a primer should have
	private final int miniLength = 19;
	// min of how close the offset of a primer to the contig end should be
	private final int minBorderOffset = 80;
	// max of how far away the offset of a primer to the contig end should be
	private final int maxBorderOffset = 400;
	
	
	private AbstractProgressReporter progress;
	private double maxProgress = 0;

	/**
	 * Constructor of this class. Needs to get a fastaFile, which contains sorted contigs
	 * 
	 * @param fastaFile
	 */
	public PrimerGenerator(File fastaFile) {
		fastaParser = new FastaFileReader(fastaFile);
	}

	/**
	 * Method is called when the user wishes to mask repeats in his sequences and returns
	 * a fastaFileReader object which includes the masked sequences.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void runRepeatMasking() throws IOException, InterruptedException {
		BLASTRepeatMasker rm = new BLASTRepeatMasker(fastaParser);
		fastaParser = rm.doRepeatMasking();
	}
	
	/**
	 * This method checks if a config file is given and sets up the PrimerScoringScheme with
	 * parameters contained in the config file.
	 * Or it sets up the PrimerScoringScheme with the loaded default parameters.
	 * 
	 * @param config
	 * @throws Exception
	 */
	public void setParameters(File config) throws Exception {
		if (config != null) {
			XMLParser configParser = new XMLParser(config);
			scoring = new PrimerScoringScheme();
			configParser.parse(scoring);
		} else {
			scoring = new PrimerScoringScheme();
		}
	}

	/**
	 * This method checks if the id of the selected contig is in the fasta file
	 * with the sequences of the contigs. Returns true if the contig ID is in
	 * the fasta file.
	 * 
	 * @param contigID
	 * @return checked
	 * @throws IOException
	 */

	public boolean idCheck(String contigID) throws IOException {
		boolean checked = false;
		if (fastaParser.containsId(contigID)) {
			checked = true;
		} else {
			checked = false;
		}

		return checked;
	}
	
	/**
	 * This method gets a vector of contigPair, which contains the IDs of the marked contigs.
	 * For each pair the contig IDs are checked and then the method generatePrimerFor1ContigPair
	 * is called which makes primers for each contig pair and saves the results in a PrimerResult object in a vector.
	 * 
	 * @param contigPair
	 * @return prV
	 * @throws IOException
	 */
	public Vector<PrimerResult> generatePrimers(Vector<ContigPair> contigPair)
			throws IOException {
		
		Vector<PrimerResult> prV = new Vector<PrimerResult>();
		for (int i = 0; i < contigPair.size(); i++) {
			ContigPair pair = contigPair.elementAt(i);
			//Progress is reported to the PrimerFrame
			this.reportProgress((double) (i + 1) / (contigPair.size() + 1),
					"Generating primers for contig pair " + pair.contig1
							+ " and " + pair.contig2);
			//contigID check
			if (this.idCheck(pair.contig1) && this.idCheck(pair.contig2)) {
				if (!(pair.contig1.equals(pair.contig2))) {
					//generate primers for each contig pair
					PrimerResult primerResult = this
							.generatePrimerFor1ContigPair(pair);
					prV.add(primerResult);
				}
				
			} else {
				throw new NullPointerException("contig id could not be found");
			}
		
		}
		return prV;
	}

	/**
	 * This method generates primer candidates for each contig and then calculates the score
	 * for each primer and put them into a PrimerResult object.
	 * This object is given to the PrimerPairing class to pair the primers and then is returned.
	 * 
	 * @param pair
	 * @return primerResult
	 * @throws IOException
	 */
	public PrimerResult generatePrimerFor1ContigPair(ContigPair pair)
			throws IOException {
		// create all possible primers for both contigs
		Vector<Primer> primerCandidates1 = this.generatePrimerCandidates(
				fastaParser, pair.contig1, pair.onRightEnd1);
		Vector<Primer> primerCandidates2 = this.generatePrimerCandidates(
				fastaParser, pair.contig2, pair.onRightEnd2);
		// score the primers
		primerCandidates1 = this
				.calcScoreEachPrimerCandidate(primerCandidates1);
		primerCandidates2 = this
				.calcScoreEachPrimerCandidate(primerCandidates2);

		// pair the primer ant add them to a primer result
		DNASequence leftContig = this.fastaParser.getSequence(pair.contig1);
		DNASequence rightContig = this.fastaParser.getSequence(pair.contig2);
		PrimerResult primerResult = new PrimerResult(leftContig, rightContig);
		primerResult = this.getPrimerPairs(primerCandidates1, primerCandidates2, primerResult);
		if(primerCandidates1.isEmpty()) {
			primerResult.addAdditionalComment("No primer could be generated for the first contig. Maybe it is to small.");
		}
		if(primerCandidates2.isEmpty()) {
			primerResult.addAdditionalComment("No primer could be generated for the first contig. Maybe it is to small.");
		}
		return primerResult;
	}


	/**
	 * Generates a set of possible primers for a given contig. The primers
	 * length is between minLength and maxLength and the Primers are from the
	 * contig border between minBorderOffset and maxBorderOffset apart.
	 * 
	 * @param contigs
	 *            Fasta File containing the contig
	 * @param contigId
	 *            fasta ID of the contig
	 * @param onRightEnd
	 *            shall the primers be generated for the right end in forward
	 *            direction, or for the left end in backward direction?
	 * @return a collection of possible primers for this contig.
	 */
	public Vector<Primer> generatePrimerCandidates(FastaFileReader contigs,
			String contigId, boolean onRightEnd) {
		Vector<Primer> primerCandidates = new Vector<Primer>();
		DNASequence contig = contigs.getSequence(contigId);
		int contigSize = (int) contig.getSize();
		int start = 0;
		int stop = 0;
		if (onRightEnd) {
			int rightEndOffset = contigSize;
			start = rightEndOffset - maxBorderOffset;
			stop = rightEndOffset - minBorderOffset;
		} else {
			int leftEndOffset = 0;
			start = leftEndOffset + minBorderOffset;
			stop = leftEndOffset + maxBorderOffset;

		}
		
		//if the contig is smaller than the expected bounds, correct the bounds
		if (start<0 || start>contigSize) {
			start=0;
		}
		//leave space for the two bases after the primer that are needed for score computation.
		if (stop >= (contigSize-2) || stop <= 2) {
			stop = contigSize-2;
		}
		
		//if the sequence is to small, return empty primer vector
		if(Math.abs(start-stop)<400) {
			return primerCandidates;
		}
		

		for (int i = start; i < stop; i++) {
			for (int j = miniLength; j <= maxLength; j++) {
				primerCandidates.add(new Primer(contigId, contigs, i, j,
						onRightEnd));
			}

		}
	
		return primerCandidates;
	}

	/**
	 * This methods access the scoring method from the PrimerScoringScheme, where the primer
	 * scores are calculated, and only saves those primers which have higher scores than -200.
	 * 
	 * @throws IOException
	 */

	public Vector<Primer> calcScoreEachPrimerCandidate(
			Vector<Primer> primerCandidates) throws IOException {
		Vector<Primer> scoredPrimerCandidates = new Vector<Primer>();
		double score = 0;
		for (int i = 0; i < primerCandidates.size(); i++) {
			Primer candidate = primerCandidates.get(i);
			score = scoring.calculatePrimerScore(candidate);
			if (score > -200) {
				candidate.setPrimerScore(score);
				scoredPrimerCandidates.add(candidate);
			}
		}
		//System.out.println("size"+scoredPrimerCandidates.size());
		return scoredPrimerCandidates;
	}

	/**
	 * Initializes a instance of the class PrimerPairs in order to pair the
	 * primer candidates from each contig end.
	 * 
	 * @throws IOException
	 */
	public PrimerResult getPrimerPairs(Vector<Primer> primerCandidatesLeft,
			Vector<Primer> primerCandidatesRight, PrimerResult primerResult)
			throws IOException, NullPointerException {
		PrimerPairing pp = new PrimerPairing();
		primerResult = pp.pairPrimer(primerCandidatesLeft,
				primerCandidatesRight, primerResult);
		return primerResult;
	}

	/**
	 * Registers a ProgressReporter for this class.
	 * 
	 * @param progressReporter
	 */
	public void registerProgressReporter(
			AbstractProgressReporter progressReporter) {
		this.progress = progressReporter;
	}

	/**
	 * If a progress reporter is registered progress changes are shown with is.
	 * 
	 * @param percentDone
	 *            how far are we?
	 * @param s
	 *            explaining sentence
	 */
	public void reportProgress(double percentDone, String s) {
		if (progress != null) {
			progress.reportProgress(percentDone, s);
		}

	}

}
