package de.bielefeld.uni.cebitec.cav.primerdesign;

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
 * This class generates the primer candidates given contig-sequences. The
 * sequence of primer candidates has to be checked on certain biological
 * properties and are scored according to the scoring-scheme from the
 * "RetrievePArametersAndScores class".
 * 
 * @author yherrmann
 * 
 */
public class PrimerGenerator {
	private String[] markedSeq = null;
	private PrimerScoringScheme scoring = null;
	private FastaFileReader fastaParser;
	private int realstart = 0;
	// max length a primer should have
	private int maxLength = 24;
	// min length a primer should have
	private int miniLength = 19;
	// min of how close the offset of a primer to the contig end should be
	private int minBorderOffset = 80;
	// max of how far away the offset of a primer to the contig end should be
	private int maxBorderOffset = 400;
	private File temporaryDirectory = null;
	private int max = maxLength + 5;
	private FileHandler fHandler;
	private Logger logger;
	private AbstractProgressReporter progress;
	private File fasta;
	private Bases base = null;


	/**
	 * Constructor when only a fasta file is given.
	 * 
	 * @param fastaFile
	 */
	public PrimerGenerator(File fastaFile) {
		fasta = fastaFile;
		fastaParser = new FastaFileReader(fastaFile);
		base = Bases.getInstance();
	}

	public void runRepeatMasking() throws IOException, InterruptedException {
		BLASTRepeatMasker rm = new BLASTRepeatMasker(fastaParser);
		fastaParser = rm.doRepeatMasking();
	}

	public boolean setParameters(File config) throws Exception {
		if (config != null) {
			this.setUpLogFile();
			scoring = new PrimerScoringScheme();
			FileReader inConfig = new FileReader(config);
			XMLParser configParser = new XMLParser();
			configParser.parse(scoring, inConfig);
		} else {
			this.setUpLogFile();
			scoring = new PrimerScoringScheme();
		}
		return true;
	}

	/**
	 * This methods sets up the logging file. ?! am ende noch genutzt ?!
	 * 
	 * @throws SecurityException
	 * @throws IOException
	 */
	public void setUpLogFile() throws SecurityException, IOException {
		SimpleFormatter formatterLogFile = new SimpleFormatter();
		logger = Logger
				.getLogger("de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerGenerator");
		fHandler = new FileHandler("r2cat_primerDesign_log");
		logger.setUseParentHandlers(false);

		logger.addHandler(fHandler);
		fHandler.setFormatter(formatterLogFile);

		logger.setLevel(Level.SEVERE);
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
	 * This method goes through the vector with the selection informations and
	 * starts to generate primers for each contig pair which was selected. The
	 * information which contig was selected and which direction the primer has
	 * on the specific contig end is put in a String Array. The contig IDs of
	 * the selected contigs are put in the first and third position of the
	 * array. The direction (forward or reverse" is put in the second position
	 * for the first selected Contig and in the fourth position for the second
	 * selected contig. These information are processed and put into a HashMap
	 * where the key is the contigID and the value is the direction (in form of
	 * Integers) of the primer of the selected contig.
	 * 
	 * @param contigPair
	 * @throws IOException
	 */

	public Vector<PrimerResult> generatePrimers(Vector<ContigPair> contigPair)
			throws IOException {
		Vector<PrimerResult> prV = new Vector<PrimerResult>();

		for (int i = 0; i < contigPair.size(); i++) {
			ContigPair pair = contigPair.elementAt(i);
			this.reportProgress((double) (i + 1) / (contigPair.size() + 1),
					"Generating primers for contig pair " + pair.contig1
							+ " and " + pair.contig2);
			if (this.idCheck(pair.contig1) && this.idCheck(pair.contig2)) {
				if (!(pair.contig1.equals(pair.contig2))) {
					PrimerResult primerResult = this
							.generatePrimerFor1ContigPair(pair);
					prV.add(primerResult);
				} else {
					throw new IllegalStateException(
							"contig was defined for forward and reverse primer");
				}
			} else {
				throw new NullPointerException("contig id could not be found");
			}
		}
		return prV;
	}

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
		PrimerResult pr = new PrimerResult(leftContig, rightContig);
		pr = this.getPrimerPairs(primerCandidates1, primerCandidates2, pr);
		return pr;
	}

	/**
	 * This methods checks which direction of the primer for the specific contig
	 * was selected. If the primer is the forward primer a 1 is returned and if
	 * the primer is on the other contig as the reverse primer the direction is
	 * -1.
	 * 
	 * @param directionInfo
	 * @return direction
	 */

	public int setPrimerDirection(String directionInfo) {
		int direction = 0;
		if (directionInfo.equals("forward")) {
			direction = 1;
		} else {
			direction = -1;
		}
		return direction;
	}

	/**
	 * This method goes through the sequence information of the fastaFileReader
	 * and returns the sequences and the id of the marked contigs in a HashMap.
	 * It is also checked if the sequences are reverse complemented if so they
	 * get turned into the previous state.
	 * 
	 * @param markedContig
	 * @return templateSeq
	 * @throws IOException
	 */

	public HashMap<String, char[]> getMarkedSeq(String[] markedContig,
			HashMap<String, String> contigAndisReverseCompInfo)
			throws IOException {
		HashMap<String, char[]> templateSeq = new HashMap<String, char[]>();
		boolean isReverseComplemented = false;
		String isReverseCom = null;
		char[] seq = fastaParser.getCharArray();
		Vector<DNASequence> sequences = fastaParser.getSequences();
		for (String s : markedContig) {
			isReverseCom = contigAndisReverseCompInfo.get(s);
			isReverseComplemented = this.stringToBoolean(isReverseCom);
			for (int i = 0; i < sequences.size(); i++) {
				if (sequences.get(i).getId().matches(s)) {
					int length = (int) sequences.get(i).getSize();
					int start = (int) sequences.get(i).getOffset();
					char[] temp = new char[length];
					System.arraycopy(seq, start, temp, 0, length);
					if (isReverseComplemented) {
						base.getReverseComplement(temp);
						templateSeq.put(s, temp);
					} else {
						templateSeq.put(s, temp);
					}
				}
			}
		}
		return templateSeq;
	}

	public boolean stringToBoolean(String s) {
		boolean bool = false;
		if (s.equals("true")) {
			bool = true;
			return bool;
		} else {
			bool = false;
			return bool;
		}

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
		int start = 0;
		int stop = 0;
		if (onRightEnd) {
			int rightEndOffset = contigs.getOffset(contigId)
					+ (int) contig.getSize();
			start = rightEndOffset - maxBorderOffset;
			stop = rightEndOffset - minBorderOffset;
		} else {
			int leftEndOffset = contigs.getOffset(contigId);
			start = leftEndOffset + minBorderOffset;
			stop = leftEndOffset + maxBorderOffset;

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
	 * This methods access each scoring method for each primer object and
	 * retrieves the whole score for each primer candidate. The primer
	 * candidates with a score higher than -200 are saved up in a vector
	 * according to the direction of the primer.
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
				scoredPrimerCandidates.add(candidate);
			}
		}
		System.out.println(scoredPrimerCandidates.size());
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
		PrimerPairs pp = new PrimerPairs();
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