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

	private final double oligoConcentration = 0.0000025;
	private static MeltingTemperature instance = null;
	
	//to map chars onto matrix indices
	private int[] alphabetMap;
	private double[][] entropyMatrix;
	private double[][] enthalpyMatrix;
        
	private double entropyAdd;
    private double temperatureSubstract;
	
/**
 * Constructor of this class
 */
	private MeltingTemperature(){
		init();
}
	/**
	 * This method sets up the alphabetMap, which sets the bases to a specific number.
	 * This is used to set up the entropie and enthalpy matrix, which contain values for a given
	 * base-tupel.
	 * 
	 * e.g. 'AA' has an entropy value of -23.6 and an enthalpie value of -8.4
	 */
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
	
	
	//this is summand for the melting temperature formula
	entropyAdd = (1.987*Math.log((oligoConcentration/4.0)));
	//this is a term for substraction used in the melting temperature formula
	temperatureSubstract = 273.15 + 21.59;
	}
	
	/**
	 * To avoid reoccurring initialisation of these objects, a single instance shall be used.
	 * @return instance of this class
	 */
	public static MeltingTemperature getInstance() {
		if (instance == null) {
			instance = new MeltingTemperature();
		}
		return instance;
	}
	

	/**
	 * This method calculates the melting temperature with the base-stacking method.
	 * Each tupel of bases in the sequence has a level of entropie and enthalpy, which can be
	 * retrieved from the matrix of entropie and enthalpy. 
	 * The enthalpie and entropie of the sequence is used in the formula of the base-stacking method to calculate the melting temperature.
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
		//go through the given sequence and calculate the entropie and enthalpy of each pair of bases
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
			temparture = (enthalpy*1000/(entropy+entropyAdd)) - temperatureSubstract ;
		}
		return temparture;
	}
}
