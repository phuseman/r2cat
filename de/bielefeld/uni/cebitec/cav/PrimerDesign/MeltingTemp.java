package de.bielefeld.uni.cebitec.cav.PrimerDesign;

public class MeltingTemp {

	private double oligoConc = 0.0000025;
	private double preCalc = Math.log((oligoConc/4.0)*1.987);
	private double minRes = 0;
	private double maxRes = 0;
	private boolean forward =false;
	
	
	public void calcMinAndMax(){
		double min = 0;
		double max = 0;
		double newRes = 0;
		if( forward){
			//calc newRes -> T1
		} else {
			//calc newRes -> T2
		}
		if(min == 0){
			min = newRes;
		}
		if(max == 0){
			max = newRes;
		}
		if(max<newRes){
			max =  newRes;
		}
		if(min >newRes){
			min = newRes;
		}
		minRes =min;
		maxRes = max;
	}
	
	public void makeNewStringsArrays(){
		
	}
	
	public double calcT1(){
		double temp = 0;
		double errors = 0;
		
		return temp;
	}
	
}
