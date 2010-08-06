package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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
		JPanel controlPanel = new JPanel();
		JPanel results = new JPanel();
		JTabbedPane tabbedPane = new JTabbedPane();
		//VECTOR?!
		JPanel[] panelTemp = new JPanel[primerResults.size()];


		for(int j = 0; j<primerResults.size();j++){
			//tabbedPane.add("TAB "+j,results);
			JComponent tabPanel = new JPanel();
			
			tabbedPane.add("Primer Results for Contigs "+primerResults.elementAt(j).getContigIDs(),new JTextArea(primerResults.elementAt(j).toString()));
			tabPanel.setOpaque(true); 
			tabPanel.add(tabbedPane);
			this.add(tabPanel,BorderLayout.NORTH);
			panelTemp[j] = (JPanel) tabPanel;
		}
		
		JTextArea resultText = new JTextArea();
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<primerResults.size();i++){
			sb.append(primerResults.elementAt(i).toString());
		}
		
		resultText.setText(sb.toString());
		results.add(resultText,BorderLayout.CENTER);
		
		JButton exportCurrentResult = new JButton("Save current result");
		exportCurrentResult.setActionCommand("saveCurrentResult");
		exportCurrentResult.addActionListener(this);
		controlPanel.add(exportCurrentResult);
		
		JButton exportAllResults = new JButton("Save all results to one file");
		exportAllResults.setActionCommand("saveAllResultsToOneFile");
		exportAllResults.addActionListener(this);
		controlPanel.add(exportAllResults);
		
		JButton exportEachResultToSeperateFile = new JButton("Save all results to different files");
		exportEachResultToSeperateFile.setActionCommand("saveAllResultsToSeperateFile");
		exportEachResultToSeperateFile.addActionListener(this);
		controlPanel.add(exportEachResultToSeperateFile);
		
		this.add(controlPanel,BorderLayout.SOUTH);
		//this.add(new JScrollPane(results), BorderLayout.CENTER);
		this.pack();
		
		int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

		this.setSize(new Dimension(width/3,height));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand().equals("saveCurrentResult")){
			
		} else if(e.getActionCommand().equals("Save all results to one file")){
			
		}else if(e.getActionCommand().equals("Save all results to different files")){
			
		}
		
	}
}
