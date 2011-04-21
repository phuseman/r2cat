package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import javax.swing.JFrame;

public class CAGWindow extends JFrame {

	public CAGWindow() {
		
	}
	
	public void initWindow(){
		
		setTitle("View of a contig adjacency graph");
		setName("fenster");

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// setSize(Toolkit.getDefaultToolkit().getScreenSize());
		// hier wird das Fenster auf die Größe des Bildschirmes angepasst.
		
		setVisible(true);
		pack();
	}

}