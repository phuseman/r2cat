package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.HashMap;

public class MeltingTemp {

	private double oligoConc;
	private char base;
	private HashMap<String,Double> enthalpie=new HashMap<String,Double>();
	private HashMap<String,Double> entropie=new HashMap<String,Double>();

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

	//testen wegen base[0]?!?!
	public double calcTemp(char[] seq){
		this.fillEnthalpieAndEntropieParam();
		this.oligoConc = 0.0000025;
		base = seq[0];
		double temp = 0;
		double errors = 0;
		double ent = 0;
		double enp = 0;
		String tupel = null;
		boolean test =false;
		for(char b : seq){
			if(test==true){
				char[] t = new char[2];
				t[0] = base;
				t[1] = b;
				tupel = new String(t);
				if(enthalpie.containsKey(tupel)&&entropie.containsKey(tupel)){
					ent	+= enthalpie.get(tupel);
					enp += entropie.get(tupel);
				} else{
					errors++;
				}
				base=b;
			} else{
				test=true;
			}
		}
		if(errors == 0){
			temp = (ent*1000/(enp+(1.987*Math.log((oligoConc/4.0))))) - 273.15- 21.59;
			return temp;
		} else {
			return -1.0;
		}
	}
}


/*public MeltingTemp(){
	this.fillEnthalpieAndEntropieParam();
	this.oligoConc = 0.0000025;
	//base=seq[0];
	//annealTemp = this.calcTemp(seq);
	//this.setAnnealTemp(annealTemp);
	//System.out.println(annealTemp);
}*/
