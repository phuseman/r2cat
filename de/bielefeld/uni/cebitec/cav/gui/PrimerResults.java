package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerGenerator;

public class PrimerResults extends JFrame implements ActionListener{
	
	private PrimerGenerator primerGenerator;
	private Vector<String> outputVector;
	
	public PrimerResults(PrimerGenerator pg, Vector<String> output){
		primerGenerator = pg;
		outputVector = output;
		init();
	}
	
	public void init(){
		this.setTitle("Primer Results");
		this.setLayout(new BorderLayout());
		JPanel results = new JPanel();
		String out = null;
		JTextArea resultText = new JTextArea();
		
		for(int i=0;i<outputVector.size();i++){
			out = outputVector.elementAt(i).toString();
		}
		resultText.setText(out);
		results.add(resultText,BorderLayout.CENTER);
		
		JButton exportResults = new JButton("Save Results");
		exportResults.setActionCommand("save");
		exportResults.addActionListener(this);
		results.add(exportResults);
		
		this.add(results, BorderLayout.SOUTH);
		this.pack();
		
		int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

		this.setSize(new Dimension(width/3,height));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("save")){
			
		}
		
	}
}
