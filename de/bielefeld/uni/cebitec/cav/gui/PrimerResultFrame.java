package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerGenerator;
import de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerResult;
import de.bielefeld.uni.cebitec.cav.utils.MiscFileUtils;

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
	
	private JTabbedPane tabbedPane;
	public void init(){
		this.setTitle("Primer Results");
		this.setLayout(new BorderLayout());
		JPanel controlPanel = new JPanel();
		 tabbedPane= new JTabbedPane();
		//VECTOR?!
		//JTabbedPane[] tabPanelArray = new JTabbedPane[primerResults.size()];

		for(int j = 0; j<primerResults.size();j++){
			JTextArea primerResultText = new JTextArea(primerResults.elementAt(j).toString());
			String tabName = "Primer Results for Contigs "+primerResults.elementAt(j).getContigIDs();
			JScrollPane scrollPane = new JScrollPane(primerResultText);
			tabbedPane.add(tabName,scrollPane);
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
		
		this.add(controlPanel,BorderLayout.SOUTH);
		this.pack();
		
		int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

		this.setSize(new Dimension(width/3,height));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean allResultsToFile = false;
		
		if(e.getActionCommand().equals("saveCurrentResult")){
			saveOneFile(allResultsToFile);
		} else if(e.getActionCommand().equals("saveAllResultsToOneFile")){
			allResultsToFile = true;
			saveOneFile(allResultsToFile);
		}else if(e.getActionCommand().equals("saveAllResultsToSeperateFile")){
			try {
				saveMoreFiles();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	protected void saveMoreFiles() throws IOException{
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnValue = fc.showDialog(this, "Save to Directory");
		 if (returnValue == JFileChooser.APPROVE_OPTION) {
			 File file = fc.getSelectedFile();
			 this.writeResultFiles(file);
		 }

	}
	
	private void writeResultFiles(File file) throws IOException {
		for(int i = 0;i<primerResults.size();i++){
			File resultFile = new File(file,"r2cat_Primerlist_for_contigs_"+primerResults.get(i).getContigIDs()+".txt");
			FileWriter writeToFile = new FileWriter(resultFile);
			writeToFile.write(primerResults.elementAt(i).toString());
			writeToFile.flush();
			writeToFile.close();
		}
		
		
	}

	protected void saveOneFile(boolean allResultsToFile) {
	    final JFileChooser fc = new JFileChooser();
	    int returnValue = fc.showSaveDialog(this);
	 
	      if (returnValue == JFileChooser.APPROVE_OPTION) {
	          File file = fc.getSelectedFile();
	        //  MiscFileUtils.enforceExtension(file, ".txt");
	          saveText(file, allResultsToFile);
	      }
	}
	      
	      private void saveText(File file, boolean allResultsToFile) {
	    	  String NEW_LINE = System.getProperty("line.separator");
	    	    try {
	    	        FileWriter writeFile = new FileWriter(file);
	    	        StringBuffer sb = new StringBuffer();
	    	        if(allResultsToFile){
	    	        	for(int i= 0; i<primerResults.size();i++){
	    	        		String result = primerResults.elementAt(i).toString();
	    	        		sb.append(result+NEW_LINE+NEW_LINE);
	    	        	}
	    	        	String allResults = sb.toString();
	 	    	        writeFile.write(allResults);
	 	    	        writeFile.flush( );
	 	    	        writeFile.close( );
	    	        }else{
	    	        	String tempName = this.tabbedPane.getName();
	    	        	System.out.println("tempName "+tempName);
	    	        	Component tempComponent = tabbedPane.getComponent( tabbedPane.indexOfTab(tempName));
	    	        	JTextArea tempText = (JTextArea) tempComponent;
	    	        	writeFile.write(tempText.toString());
	    	        	writeFile.flush( );
		 	    	    writeFile.close( );
		    	    }
	    	    }
	    	    catch (IOException e) {
	    	        e.printStackTrace( );
	    	    }

	}

}
