package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerGenerator;
import de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerResult;

public class PrimerResultFrame extends JFrame implements ActionListener{
	
	private PrimerGenerator primerGenerator;
	private Vector<PrimerResult> primerResults;
	
	public PrimerResultFrame(Vector<PrimerResult> pr){
		primerResults = pr;
		init();
	}
	
	public void init(){
		this.setTitle("Primer Results");
		this.setLayout(new BorderLayout());
		JPanel results = new JPanel();
	
		String out = null;
		JTextArea resultText = new JTextArea();
		
		for(int i=0;i<primerResults.size();i++){
			out = primerResults.elementAt(i).toString();
		}
		resultText.setText(out);
		
		results.add(resultText,BorderLayout.CENTER);
		
		JButton exportResults = new JButton("Save Results");
		exportResults.setActionCommand("save");
		exportResults.addActionListener(this);
		results.add(exportResults);
		
		this.add(new JScrollPane(results), BorderLayout.CENTER);
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
