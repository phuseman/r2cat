package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.HashMap;
/**
 * This class includes methods to calculate the melting-temperature of a given 
 * nucleotide sequence.
 * 
 * It is a reproduction of the function (Function.pm) used in a perl-script written by Jochen Blom and Dr. Christian Rueckert.
 * 
 * @author yherrmann
 *
 */
public class MeltingTemperature {

	private double oligoConc = 0;
	private char base;
	private HashMap<String,Double> enthalpie=new HashMap<String,Double>();
	private HashMap<String,Double> entropie=new HashMap<String,Double>();

	/**
	 * In the constructor the oligoConc variable is set and the HashMaps enthalpie and entropie are set up through the method
	 * fillingEnthalpieAndEntropieParam.
	 */
	public MeltingTemperature(){
	this.fillEnthalpieAndEntropieParam();
	this.oligoConc = 0.0000025;
	
}
	
	/**
	 *	This method fills HashMaps with the numerical values for a given pair of nucleotides regarding
	 *	the enthalpie and the entropie. 
	 */
	public void fillEnthalpieAndEntropieParam(){
		enthalpie.put("AA", -8.4);
		enthalpie.put("AT", -6.5);
		enthalpie.put("AC", -8.6);
		enthalpie.put("AG", -6.1);
		enthalpie.put("CA", -7.4);
		enthalpie.put("CT", -6.1);
		enthalpie.put("CC", -6.7);
		enthalpie.put("CG", -10.1);
		enthalpie.put("GA", -7.7);
		enthalpie.put("GT", -8.6);
		enthalpie.put("GC", -11.1);
		enthalpie.put("GG", -6.7);
		enthalpie.put("TA", -6.3);
		enthalpie.put("TT", -8.4);
		enthalpie.put("TC", -7.7);
		enthalpie.put("TG", -7.4);
		
		entropie.put("AA", -23.6);
		entropie.put("AT", -18.8);
		entropie.put("AC", -23.0);
		entropie.put("AG", -16.1);
		entropie.put("CA", -19.3);
		entropie.put("CT", -16.1);
		entropie.put("CC", -15.6);
		entropie.put("CG", -25.5);
		entropie.put("GA", -20.3);
		entropie.put("GT", -23.0);
		entropie.put("GC", -28.4);
		entropie.put("GG", -15.6);
		entropie.put("TA", -18.5);
		entropie.put("TT", -23.6);
		entropie.put("TC", -20.3);
		entropie.put("TG", -19.3);
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
		base = seq[0];
		double temparture = 0;
		double errors = 0;
		double ent = 0;
		double enp = 0;
		String tupel = null;
		boolean firstBaseSet = false;
		
		for(char b : seq){
			//needs to be true so the first base is set of the nucleotide tupel
			if(firstBaseSet){
				char[] tupelArray = new char[2];
				tupelArray[0] = base;
				tupelArray[1] = b;
				tupel = new String(tupelArray);
					if(enthalpie.containsKey(tupel)&&entropie.containsKey(tupel)){
							ent	+= enthalpie.get(tupel);
							enp += entropie.get(tupel);
					} else{
						errors++;
					}
					base=b;
			} else{
				firstBaseSet=true;
					}
		}
		
		if(errors == 0){
			//calculation of the melting temperature
			temparture = (ent*1000/(enp+(1.987*Math.log((oligoConc/4.0))))) - 273.15 - 21.59;
			//temparture = (ent*1000/(enp+(1.987*Math.log((oligoConc/2000000000))))) - 273.15;
			return temparture;
		} else {
			return -1.0;
		}
	}
}
