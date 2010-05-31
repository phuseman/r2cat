package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.HashMap;

public class MeltingTemp {

	private double oligoConc;
	private double preCalc;
	private double minRes = 0;
	private double maxRes = 0;
	private boolean forward =false;
	private double annealTemp = 0;
	private HashMap<String,Double> enthalpie=new HashMap<String,Double>();
	private HashMap<String,Double> entropie=new HashMap<String,Double>();
	
	public MeltingTemp(){
		this.fillEnthalpieAndEntropieParam();
		this.oligoConc = 0.0000025;
		this.preCalc = Math.log((oligoConc/4.0)*1.987);
	}
	public void fillEnthalpieAndEntropieParam(){
		enthalpie.put("aa", -8.4);
		enthalpie.put("at", -6.5);
		enthalpie.put("ac", -8.6);
		enthalpie.put("ag", -6.1);
		enthalpie.put("ca", -7.4);
		enthalpie.put("ct", -6.1);
		enthalpie.put("cc", -6.7);
		enthalpie.put("cg", -10.1);
		enthalpie.put("ga", -7.7);
		enthalpie.put("gt", -8.6);
		enthalpie.put("gc", -11.1);
		enthalpie.put("gg", -6.7);
		enthalpie.put("ta", -6.3);
		enthalpie.put("tt", -8.4);
		enthalpie.put("tc", -7.7);
		enthalpie.put("tg", -7.4);
		
		entropie.put("aa", -23.6);
		entropie.put("at", -18.8);
		entropie.put("ac", -23.0);
		entropie.put("ag", -16.1);
		entropie.put("ca", -19.3);
		entropie.put("ct", -16.1);
		entropie.put("cc", -15.6);
		entropie.put("cg", -25.5);
		entropie.put("ga", -20.3);
		entropie.put("gt", -23.0);
		entropie.put("gc", -28.4);
		entropie.put("gg", -15.6);
		entropie.put("ta", -18.5);
		entropie.put("tt", -23.6);
		entropie.put("tc", -20.3);
		entropie.put("tg", -19.3);
	}
	
	public void calcMinAndMax(){
		double min = 0;
		double max = 0;
		double newRes = 0;
		if(forward){
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
		this.minRes =min;
		this.maxRes = max;
	}
	
	public void makeNewStringsArrays(){
		
	}
	
	public double calcT1(){
		double temp = 0;
		double errors = 0;
		
		return temp;
	}
	
	public double getAnnealTemp() {
		return annealTemp;
	}
	public void setAnnealTemp(double annealTemp) {
		this.annealTemp = annealTemp;
	}
	
}
