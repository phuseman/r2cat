package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Dimension;

import javax.swing.JFrame;

public class CAGWindow extends JFrame {

	public CAGWindow() {
		
	}
	
	public void initWindow(){
		
		setTitle("View of a contig adjacency graph");
		setName("fenster");
		
		setPreferredSize(new Dimension(800, 400));

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// setSize(Toolkit.getDefaultToolkit().getScreenSize());
		// hier wird das Fenster auf die Größe des Bildschirmes angepasst.
		
		setVisible(true);
		pack();
	}

}