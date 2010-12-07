package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.util.ArrayList;

import javax.swing.UIManager;

/*
 * Übernimmt die Benutzereingaben und manipuliert dann das Model 
 * sodass auf die benutzereingaben ein neues model entsteht und 
 * dieses dann die View verändert!
 * 
 * TODO diese klasse kann in die view integriert werden
 */
public class Controller {
	
	private CagCreator model;
	private CAGWindow window;
	private ArrayList <CagEventListener> listeners;
	private Controller control;
	
	public Controller(CagCreator cagCreator){
		
		listeners = new ArrayList <CagEventListener>();
		model = cagCreator;
		control = this;
		
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
					// "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
					// UIManager.getCrossPlatformLookAndFeelClassName());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				window = new CAGWindow(model, control);
			}
		});
	}
	
	/*
	 * Methode soll im Model den wert des aktuellen Contigs ändern
	 */
	public void selectContig(String contigName, String isReverse){
		model.changeContigs(contigName, isReverse);
		//model.sendCurrentContig();
	}
	
//	/**
//	 * Hier werden alle Klassen die sich registrien in der
//	 * ArrayList gespeichert.
//	 */
//	public void addEventListener (CagEventListener listener) {
//		listeners.add(listener);
//	}
//	
//	/**
//	 * Hier werden alle Klasse die sich registriet haben aus 
//	 * der ArrayList gelöscht.
//	 */
//	public void removeEventListener(CagEventListener listener) {
//		listeners.remove(listener);
//	}
	
}