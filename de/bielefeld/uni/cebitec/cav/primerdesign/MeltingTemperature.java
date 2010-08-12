package de.bielefeld.uni.cebitec.cav.primerdesign;

/**
 * This class includes methods to calculate the melting-temperature of a given 
 * nucleotide sequence.
 * 
 * It is a reproduction of the function (Function.pm) used in a perl-script written by Jochen Blom and Dr. Christian Rueckert.
 * The values of enthalpie and entropie are from the paper Santalucia 1996. !!! 
 * 
 * @author yherrmann
 *
 */
public class MeltingTemperature {

	private final double oligoConc = 0.0000025;
	private static MeltingTemperature instance = null;
	private int[] alphabetMap;
	private double[][] entropyMatrix;
	private double[][] enthalpyMatrix;
	

	/**
	 * In the constructor the oligoConc variable is set and the HashMaps enthalpie and entropie are set up through the method
	 * fillingEnthalpieAndEntropieParam.
	 */
	private MeltingTemperature(){
		init();
}
	
	private void init() {
	//create alphabet map
	alphabetMap = new int[256];
	for (int i = 0; i < alphabetMap.length; i++) {
		alphabetMap[i]=-1;
	}
	//map nucleodide characters to matrix indices
	alphabetMap['a']=0;
	alphabetMap['A']=0;
	alphabetMap['c']=1;
	alphabetMap['C']=1;
	alphabetMap['g']=2;
	alphabetMap['G']=2;
	alphabetMap['t']=3;
	alphabetMap['T']=3;
	
	
	//initialize entropy matrix
	entropyMatrix = new double[4][4];
	entropyMatrix[alphabetMap['A']][alphabetMap['A']] = -23.6;
	entropyMatrix[alphabetMap['A']][alphabetMap['T']] = -18.8;
	entropyMatrix[alphabetMap['A']][alphabetMap['C']] = -23.0;
	entropyMatrix[alphabetMap['A']][alphabetMap['G']] = -16.1;
	entropyMatrix[alphabetMap['C']][alphabetMap['A']] = -19.3;
	entropyMatrix[alphabetMap['C']][alphabetMap['T']] = -16.1;
	entropyMatrix[alphabetMap['C']][alphabetMap['C']] = -15.6;
	entropyMatrix[alphabetMap['C']][alphabetMap['G']] = -25.5;
	entropyMatrix[alphabetMap['G']][alphabetMap['A']] = -20.3;
	entropyMatrix[alphabetMap['G']][alphabetMap['T']] = -23.0;
	entropyMatrix[alphabetMap['G']][alphabetMap['C']] = -28.4;
	entropyMatrix[alphabetMap['G']][alphabetMap['G']] = -15.6;
	entropyMatrix[alphabetMap['T']][alphabetMap['A']] = -18.5;
	entropyMatrix[alphabetMap['T']][alphabetMap['T']] = -23.6;
	entropyMatrix[alphabetMap['T']][alphabetMap['C']] = -20.3;
	entropyMatrix[alphabetMap['T']][alphabetMap['G']] = -19.3;
	
	//initialize enthalpy matrix 
	enthalpyMatrix = new double[4][4];
	enthalpyMatrix[alphabetMap['A']][alphabetMap['A']] = -8.4;
	enthalpyMatrix[alphabetMap['A']][alphabetMap['T']] = -6.5;
	enthalpyMatrix[alphabetMap['A']][alphabetMap['C']] = -8.6;
	enthalpyMatrix[alphabetMap['A']][alphabetMap['G']] = -6.1;
	enthalpyMatrix[alphabetMap['C']][alphabetMap['A']] = -7.4;
	enthalpyMatrix[alphabetMap['C']][alphabetMap['T']] = -6.1;
	enthalpyMatrix[alphabetMap['C']][alphabetMap['C']] = -6.7;
	enthalpyMatrix[alphabetMap['C']][alphabetMap['G']] = -10.1;
	enthalpyMatrix[alphabetMap['G']][alphabetMap['A']] = -7.7;
	enthalpyMatrix[alphabetMap['G']][alphabetMap['T']] = -8.6;
	enthalpyMatrix[alphabetMap['G']][alphabetMap['C']] = -11.1;
	enthalpyMatrix[alphabetMap['G']][alphabetMap['G']] = -6.7;
	enthalpyMatrix[alphabetMap['T']][alphabetMap['A']] = -6.3;
	enthalpyMatrix[alphabetMap['T']][alphabetMap['T']] = -8.4;
	enthalpyMatrix[alphabetMap['T']][alphabetMap['C']] = -7.7;
	enthalpyMatrix[alphabetMap['T']][alphabetMap['G']] = -7.4;
	}
	
	public static MeltingTemperature getInstance() {
		if (instance == null) {
			instance = new MeltingTemperature();
		}
		return instance;
	}
	
	/**
	 *	This method fills HashMaps with the numerical values for a given pair of nucleotides regarding
	 *	the enthalpie and the entropie. 
	 */
	public void fillEnthalpieAndEntropieParam(){
		
		

	}
	

	/**
	 * This method calculates the melting temperature with the base-stacking method.
	 * A pair of bases of the sequence is compared to the entries of the HashMaps in order to
	 * get the level of entropie and enthalpie of the whole sequence. These factors are used in 
	 * the formula of the base-stacking method to calculate the melting temperature.
	 * 
	 * If the sequence contains unknown letters the calculation can not proceed and returns -1.
	 * 
	 * @param seq
	 * @return melting temperature
	 */
	
	public double calculateTemperature(char[] seq){
		boolean error = false;
		double entropy = 0;
		double enthalpy = 0;
		
		for (int i = 0; i < seq.length-1; i++) {
			try {
			entropy += entropyMatrix[alphabetMap[seq[i]]][alphabetMap[seq[i+1]]];
			enthalpy += enthalpyMatrix[alphabetMap[seq[i]]][alphabetMap[seq[i+1]]];
			} catch (ArrayIndexOutOfBoundsException e) {
				error=true;
			}
			
		}
		
		double temparture = -1;
		if(!error){
			//calculation of the melting temperature
			//TODO: fixen anteil der berechnung rausnehmen und vorberechnet speichern!
			temparture = (enthalpy*1000/(entropy+(1.987*Math.log((oligoConc/4.0))))) - 273.15 - 21.59;
			//temparture = (ent*1000/(enp+(1.987*Math.log((oligoConc/2000000000))))) - 273.15;
		}
		return temparture;
	}
}
