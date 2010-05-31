package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;
import de.bielefeld.uni.cebitec.cav.qgram.QGramIndex;

public class PrimerDesignTest {
	
	private static char[] input = null;
	private static int[] offsetsInInput;
	private static int qGramCode= 86276;
	private static int seqNumLength =0;
	private static Vector<DNASequence> sequences;


	/**
	 * @param args
	 * @throws IOException 
	 * @throws IOException 
	 */
	
public PrimerDesignTest() throws IOException{
	
	File f = new File("C:/Users/Yvisunshine/Uni/erstesContig.fas");
	FastaFileReader fastaFileReader = new FastaFileReader(f);
	fastaOutput(fastaFileReader);

	}
	
	public static void fastaOutput(FastaFileReader f) throws IOException{
		
		FastaFileReader filereader = f;
		input = filereader.getCharArray();
		offsetsInInput = filereader.getOffsetsArray();
		sequences = filereader.getSequences();
		
		QGramIndex in = new QGramIndex();
		
		in.generateIndex(f);
		
		int[] hash = in.getHashTable();
		int[] occurrence = in.getOccurrenceTable();

		for (int i = hash[qGramCode]; i < hash[qGramCode + 1]; i++) {
			
			System.out.print(" Startposition:" + occurrence[i] + " ");
			System.out.print("Sequenz:");
			
		for (int j = 0; j < 11; j++) {
			
			System.out.print(input[occurrence[i] + j]);
			
			}
		}

			System.out.println("\n");
			System.out.println("Die ersten 11 Basen: ");
	
				 for(int k = 0; k<11; k++){
						System.out.print(input[k+getSeqLength(0)]);
				}
			 }
	
	public static int getSeqLength(int n){
	
			seqNumLength=offsetsInInput[n];
		return seqNumLength;
	}
	
	public static void main(String[] args) throws IOException {
		
		PrimerDesignTest test = new PrimerDesignTest();
		
		System.out.println(sequences);

	}

}
