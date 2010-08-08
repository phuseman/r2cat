package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerGenerator;
import de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerResult;

public class PrimerResultFrame extends JFrame implements ActionListener,ChangeListener{
	
	private PrimerGenerator primerGenerator;
	private Vector<PrimerResult> primerResults;
	private JButton exportEachResultToSeperateFile;
	private JButton exportAllResults;
	private JButton exportCurrentResult ;
	private int selectedTab;
	private String tabName;
	
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

		for(int j = 0; j<primerResults.size();j++){
			JTextArea primerResultText = new JTextArea(primerResults.elementAt(j).toString());
			tabName = "Primer Results for Contigs "+primerResults.elementAt(j).getContigIDs();
			primerResultText.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(primerResultText);
			tabbedPane.addTab(tabName,scrollPane);
		}
		
		tabbedPane.addChangeListener(this);
		tabbedPane.setSelectedIndex(0);
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
			allResultsToFile = false;
			saveOneFile(allResultsToFile);

		} else if(e.getActionCommand().equals("saveAllResultsToOneFile")){
			//Output in Files überprüfen
			allResultsToFile = true;
			saveOneFile(allResultsToFile);
		}else if(e.getActionCommand().equals("saveAllResultsToSeperateFile")){
			try {
				//Output in Files überprüfen
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
		popUpDialog();
		
	}

	private void popUpDialog() {
		      JOptionPane.showMessageDialog(this, "Saving of Files is done",
						                       "Saving Status", JOptionPane.INFORMATION_MESSAGE);
	}

	protected void saveOneFile(boolean allResultsToFile) {
	    final JFileChooser fc = new JFileChooser();
	    int returnValue = fc.showSaveDialog(this);
	 
	      if (returnValue == JFileChooser.APPROVE_OPTION) {
	          File file = fc.getSelectedFile();
	          saveResult(file, allResultsToFile);
	      }
	}
	      
	      private void saveResult(File file, boolean allResultsToFile) {
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
	    	        }else if(!allResultsToFile){
	    						JScrollPane selectedPanel = (JScrollPane) tabbedPane.getComponentAt(selectedTab);
	    						JViewport currentView = (JViewport)selectedPanel.getComponent(0);
	    						JTextArea currentTextArea = (JTextArea) currentView.getComponent(0);
	    						String tempText = currentTextArea.getText();
	    	    	        	writeFile.write(tempText);
	    	    	        	writeFile.flush( );
	    		 	    	    writeFile.close( );
		    	    }
	    	    }
	    	    catch (IOException e) {
	    	        e.printStackTrace( );
	    	    }

	}
	      

		
@Override
		public void stateChanged(ChangeEvent e) {
			JTabbedPane pane = (JTabbedPane)e.getSource();
			selectedTab = pane.getSelectedIndex();
		}

}
