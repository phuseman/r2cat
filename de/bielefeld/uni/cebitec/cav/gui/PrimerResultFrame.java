package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
	private JButton exportEachResultToSeperateFile;
	private JButton exportAllResults;
	private JButton exportCurrentResult ;
	
	public PrimerResultFrame(Vector<PrimerResult> pr){
		primerResults = pr;
		init();
	}
	
	
	public void init(){
		this.setTitle("Primer Results");
		this.setLayout(new BorderLayout());
		JPanel controlPanel = new JPanel();
		JTabbedPane tabbedPane = new JTabbedPane();
		//VECTOR?!
		JPanel[] tabPanelArray = new JPanel[primerResults.size()];

		for(int j = 0; j<primerResults.size();j++){
			JTextArea primerResultText = new JTextArea(primerResults.elementAt(j).toString());
			String tabName = "Primer Results for Contigs "+primerResults.elementAt(j).getContigIDs();
			JScrollPane test = new JScrollPane(primerResultText);
			tabbedPane.add(tabName,test);
			//tabPanelArray[j] = (JPanel) tabPanel;
		}
		this.add(tabbedPane);
		
		exportCurrentResult = new JButton("Save current result");
		exportCurrentResult.setActionCommand("saveCurrentResult");
		exportCurrentResult.addActionListener(this);
		controlPanel.add(exportCurrentResult);
		
		exportAllResults = new JButton("Save all results to one file");
		exportAllResults.setActionCommand("saveAllResultsToOneFile");
		exportAllResults.addActionListener(this);
		controlPanel.add(exportAllResults);
		
		exportEachResultToSeperateFile = new JButton("Save all results to different files");
		exportEachResultToSeperateFile.setActionCommand("saveAllResultsToSeperateFile");
		exportEachResultToSeperateFile.addActionListener(this);
		controlPanel.add(exportEachResultToSeperateFile);
		
		//this.add(controlPanel,BorderLayout.SOUTH);

		/*		for(int k = 0; k<tabPanelArray.length;k++){
			System.out.println(tabPanelArray[k].getComponentCount());
			//this.add(new JScrollPane(tabPanelArray[k].getComponent(0)));
		}*/

		this.pack();
		
		int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

		this.setSize(new Dimension(width/3,height));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand().equals("saveCurrentResult")){
			exportCurrentResult.setMnemonic(KeyEvent.VK_O);
		} else if(e.getActionCommand().equals("Save all results to one file")){
			exportAllResults.setMnemonic(KeyEvent.VK_O);
		}else if(e.getActionCommand().equals("Save all results to different files")){
			exportEachResultToSeperateFile.setMnemonic(KeyEvent.VK_O);
		}
		
	}
}
