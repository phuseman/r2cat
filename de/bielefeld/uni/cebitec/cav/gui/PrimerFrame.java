package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import de.bielefeld.uni.cebitec.cav.controller.SequenceNotFoundException;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.datamodel.PrimerTableModel;
import de.bielefeld.uni.cebitec.cav.primerdesign.ContigPair;
import de.bielefeld.uni.cebitec.cav.primerdesign.PrimerGenerator;
import de.bielefeld.uni.cebitec.cav.primerdesign.PrimerResult;
import de.bielefeld.uni.cebitec.cav.primerdesign.XMLCheck;
import de.bielefeld.uni.cebitec.cav.utils.AbstractProgressReporter;
import de.bielefeld.uni.cebitec.cav.utils.MiscFileUtils;

public class PrimerFrame extends JFrame implements ActionListener,
		PropertyChangeListener {

	private AlignmentPositionsList alignmentPositionsList;
	private DNASequence contigs;
	private PrimerTable primer;
	private JButton setConfigButton;
	private JButton run;
	private File configFile;
	private File lastDir;
	private Vector<ContigPair> contigPairs = null;
	private JProgressBar progressBar;
	private JButton select;
	private JButton remove;
	private JPanel controlPanel;
	private JComboBox repeatMaskingComboBox;
	private double currentWidth;

	public PrimerFrame(AlignmentPositionsList alignmentPositionsList) {
		this.alignmentPositionsList = alignmentPositionsList;
		primer = new PrimerTable(alignmentPositionsList);
		// uebpruefen ob leer... dann FILE
		Vector<DNASequence> seq = alignmentPositionsList.getQueries();
		init();
	}

	/**
	 * Sets up a frame which shows the sorted contigs in a table and gives the user
	 * the chance to select contig pairs, choose to do a repeat masking of the sequences
	 * and to set a config file for new paramters.
	 * 
	 */
	private void init() {
		this.setTitle("Generate Primers");
		this.setLayout(new BorderLayout());
		JScrollPane tp = new JScrollPane(primer);
		this.add(tp, BorderLayout.CENTER);
		controlPanel = new JPanel();
		select = new JButton("Select all");
		select.setActionCommand("select_all");
		controlPanel.add(select);
		select.addActionListener(primer);
		remove = new JButton("Select none");
		remove.setActionCommand("select_none");
		controlPanel.add(remove);
		remove.addActionListener(primer);

		setConfigButton = new JButton("Set Config");
		setConfigButton.setActionCommand("setConfig");
		setConfigButton
				.setToolTipText("Choose a XML file of primer design parameters");
		currentWidth = this.getSize().getWidth();
		controlPanel.add(setConfigButton);
		setConfigButton.addActionListener(this);

		String[] repeatString = { "Repeat Masking with BLAST", "Repeats are already masked", "No Repeat Masking is necessary" };
		repeatMaskingComboBox = new JComboBox(repeatString);
		repeatMaskingComboBox.setSelectedIndex(2);
		repeatMaskingComboBox.setToolTipText("<html>Determines whether a repeat masking step should be performed.<br>" +
				"There are three possibilities:"+
				"<ul>" +
				"<li>Either BLAST can be used to mask repetitive regions</li>" +
				"<li>The FASTA file is already masked: small letters indicate repetitive regions,<br>" +
				"capital letters mean normal</li>" +
				"<li>No repeat masking should be used</li>" +
				"</ul></html>");
		controlPanel.add(repeatMaskingComboBox);
		repeatMaskingComboBox.addActionListener(this);
		
		progressBar = new JProgressBar();
		progressBar.setToolTipText("Generating possible primer pairs for the contigs");
		progressBar.setStringPainted(true);

		run = new JButton("Generate Primers");
		run.setActionCommand("generate_primer");
		controlPanel.add(run);

		run.addActionListener(this);
		this.add(controlPanel, BorderLayout.SOUTH);
		
		this.pack();

		int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

		this.setSize(new Dimension(width / 3, height));

	}

	private File chooseFile(File prevFile, String dialogTitle) {
		if (prevFile != null && prevFile.getParentFile().exists()) {
			lastDir = prevFile.getParentFile();
		}
		return MiscFileUtils.chooseFile(this, dialogTitle, lastDir, true,
				new CustomFileFilter(".xml", "XML File"));
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("generate_primer")) {
			if(!((PrimerTableModel) primer
					.getModel()).getSelectedPairs().isEmpty()){
				try {
					runAlgorithm();
				} catch (IOException e1) {
					if(e1.getMessage().equals("Error! Can not generate primers for this selection!")){
						JOptionPane.showMessageDialog(this,"Error! Can not generate primers for this selection!");
					}
					e1.printStackTrace();
				}
			}else{
				JOptionPane.showMessageDialog(this,"Error! No contig-pair selected!");
			}

		} else if (e.getActionCommand().matches("setConfig")) {

				try {
					setConfig();
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}

		this.invalidate();
		this.repaint();
	}

	/**
	 * Sets and checks the selected config file by the user.
	 * Method does a quick scan through the file in order to make sure it can be
	 * a xml file.
	 * 
	 * @throws NumberFormatException
	 * @throws HeadlessException
	 * @throws IOException
	 */
	private void setConfig() throws NumberFormatException, HeadlessException, IOException {
		File config = this.chooseFile(configFile,
				"Select config (xml format)");
		this.setConfig(config, false);
		if (configFile != null) {
			//file can't be found or doesn't exist anymore
			if(!configFile.canRead()&&!configFile.exists()){
				Object[] options = {"Yes", "Cancel"};
				int jOptionPaneAnswer = JOptionPane.showOptionDialog(null,"Could not find or read selected file! \nDo you want to select a new file?", "Config file not found",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
						null, options, options[0]);

				if(jOptionPaneAnswer == JOptionPane.YES_OPTION) {this.setConfig(MiscFileUtils.chooseFile(null,"Choose a new file", null, true,
									new CustomFileFilter(".xml","XML file")),false);
				}
				
				if(jOptionPaneAnswer==JOptionPane.YES_OPTION){
					if(this.setConfigButton.getText().length()<configFile.getName().length()&&this.getExtendedState()!=6){
					this.setConfigButton.setText(configFile.getName());
					this.setConfigButton.setBackground(Color.decode("#90EE90"));
					this.pack();
					}else{
						this.setConfigButton.setText(configFile.getName());
						this.setConfigButton.setBackground(Color.decode("#90EE90"));
					}
				
	
			} 
			}else{
				//quickScan through the selected file
				XMLCheck xmlParser = new XMLCheck(configFile);
				if(xmlParser.quickScan()){
					if(this.setConfigButton.getText().length()<configFile.getName().length()&&this.getExtendedState()!=6){
						this.setConfigButton.setText(configFile.getName());
						this.setConfigButton.setBackground(Color.decode("#90EE90"));
						this.pack();
						}else{
							this.setConfigButton.setText(configFile.getName());
							this.setConfigButton.setBackground(Color.decode("#90EE90"));
						}

					} else{
					Object[] options = { "Yes", "Cancel"};
					int jOptionPaneAnswer = JOptionPane.showOptionDialog(null,"Sorry! This is not an xml file. \nDo you want to select a new file?", "This is not an xml file!",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
							null, options, options[0]);

					if(jOptionPaneAnswer == JOptionPane.YES_OPTION) {this.setConfig(MiscFileUtils.chooseFile(null,"Choose a new file", null, true,
										new CustomFileFilter(".xml","XML file")),false);
					}
					if(jOptionPaneAnswer==JOptionPane.YES_OPTION){
						if(this.setConfigButton.getText().length()<configFile.getName().length()&&this.getExtendedState()!=6){
							this.setConfigButton.setText(configFile.getName());
							this.setConfigButton.setBackground(Color.decode("#90EE90"));
							this.pack();
							}else{
								this.setConfigButton.setText(configFile.getName());
								this.setConfigButton.setBackground(Color.decode("#90EE90"));
							}
					} 
				}
			}
			} else {
			this.setConfigButton.setText("Set Config");
			this.setConfigButton.setBackground(null);
			configFile = null;
		}
	}
	
	private void runAlgorithm() throws IOException {
		if (alignmentPositionsList.getQueries() != null
				&& !alignmentPositionsList.getQueries().isEmpty()) {
			contigs = alignmentPositionsList.getQueries().get(0);
		} else {
			contigs = new DNASequence("dummy");
		}
		
		PrimerGeneratorTask pgT = new PrimerGeneratorTask();
		pgT.addPropertyChangeListener(this);
		//get the selected contig pairs
		contigPairs = ((PrimerTableModel) primer
				.getModel()).getSelectedPairs();
		//if pairs are selected then check the xml file of right structure
		if(contigPairs.size()>0) {
			if(configFile!= null){
				XMLCheck xmlParser = new XMLCheck(configFile);
				boolean isXML = xmlParser.scanXML();
			if(!isXML){
				//config file doesn't have xml structure! Error message for user
			JOptionPane.showMessageDialog(this,"Error! This is not a xml file!");
					this.configFile = null;
					this.setConfigButton.setText("Set Config");
					this.setConfigButton.setBackground(null);
					this.repaint();
				}else{
					//starts the thread to do primer generating
					pgT.execute();
				}
			}else{
				//starts the thread to do primer generating
				pgT.execute();	
			}
		
		}
	}

	/**
	 * This method sets up a new frame, where the results of the primer generating are shown.
	 * 
	 * @param pResult
	 */
	public void showResults(Vector<PrimerResult> pResult) {
		PrimerResultFrame pr = new PrimerResultFrame(pResult);
		pr.pack();
		pr.setLocationByPlatform(true);
		pr.setVisible(true);
	}
	
	class PrimerGeneratorTask extends SwingWorker<Vector<PrimerResult>, String>
			implements AbstractProgressReporter {

		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Vector<PrimerResult> doInBackground() throws Exception,
				IOException {
			if (!contigs.getFile().exists() || !contigs.getFile().canRead()) {
				throw new SequenceNotFoundException(
						"Could not find or read the contigs file", contigs);
			}
			File fastaFile = contigs.getFile();
			//long start = new Date().getTime();
			PrimerGenerator pg = new PrimerGenerator(fastaFile);
			pg.registerProgressReporter(this);

			//if (repeatMaskingCheckBox.getState()) {
			if(PrimerFrame.this.repeatMaskingComboBox.getSelectedIndex()==0){
				PrimerFrame.this.progressBar.setIndeterminate(true);
				PrimerFrame.this.progressBar
						.setString("Repeat masking using BLAST");
				pg.runRepeatMasking();
				PrimerFrame.this.progressBar.setString(null);
				PrimerFrame.this.progressBar.setIndeterminate(false);
			}else if(PrimerFrame.this.repeatMaskingComboBox.getSelectedIndex()==2){
				pg.setSequenceToUpperCases();
			}else if(PrimerFrame.this.repeatMaskingComboBox.getSelectedIndex()==1){
				//sequences are already masked so nothing will be done with the sequences
			}
			//checks if a configFile is set and gives it to the primerGenerator class
			if(configFile != null){
				pg.setParameters(configFile);
			}else{
				pg.setParameters(null);
			}

			PrimerFrame.this.progressBar.setIndeterminate(false);
			//Generating primers starts
			Vector<PrimerResult> primerResult = pg.generatePrimers(contigPairs);
			return primerResult;
		}

		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#process(java.util.List)
		 */
		@Override
		protected void process(List<String> chunks) {
			//this method is called occasionally when a few temporary results were publish() ed. 
			progressBar.setString( chunks.get(chunks.size()-1));
		}

		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#done()
		 */
		@Override
		public void done() {
			if (this.isCancelled()) {
				PrimerFrame.this.progressBar.setValue(0);
			} else {
				Vector<PrimerResult> pResult = null;
				try {
					pResult = this.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (pResult != null && pResult.size() > 0) {
					showResults(pResult);
					PrimerFrame.this.dispose();
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see de.bielefeld.uni.cebitec.cav.utils.AbstractProgressReporter#reportProgress(double, java.lang.String)
		 */
		@Override
		public void reportProgress(double percentDone, String comment) {
			if (percentDone >= 0 && percentDone <= 1) {
				// set progress fires an propertyChangeEvent to all registered
				// listeners
				setProgress((int) (percentDone * 100.));
			}
			if (comment != null) {
				publish(comment);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// these "progress" events are generated by the SwingWorker thread
		// method setProgress(int)
		// and delivered to any registered propertychangelistener.
		// (progress integer values from 0 - 100)
		if (evt.getPropertyName().matches("progress")) {
			progressBar.setValue((Integer) evt.getNewValue());

			// Additionally these "state" events are generated bay a SwingWorker
			// just before it starts
			// the doInBackground method invoked by execute() and after this
			// method has finished
			// (see SwingWorker.StateValue )
		} else if (evt.getPropertyName().matches("state")) {
			if ((SwingWorker.StateValue) evt.getNewValue() == SwingWorker.StateValue.STARTED) {
//				progressBar.setIndeterminate(true);
				progressBar.setValue(0);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				this.remove(controlPanel);
				this.add(progressBar, BorderLayout.SOUTH);
				progressBar.setVisible(true);
				this.validate();
			} else if ((SwingWorker.StateValue) evt.getNewValue() == SwingWorker.StateValue.DONE) {
				progressBar.setIndeterminate(false);
				setCursor(null);
				this.remove(progressBar);
				this.add(controlPanel, BorderLayout.SOUTH);
				controlPanel.setVisible(true);
				this.validate();
			}
		}
	}
/**
 * Sets a selected xml file
 * @param q
 * @param silent
 */
	private void setConfig(File q, boolean silent) {
		if (q == null || q.getName().equalsIgnoreCase("")) {
			return;
		}
		if (q.canRead()) {
			this.lastDir = q.getParentFile();
			this.configFile = q;
		} else {
			if (!silent) {
				this.errorAlert("File is not readable: " + q.getName());
			}
		}
	}

	/**
	 * Opens a JOptionpane with a error notice.
	 * 
	 * @param error
	 */
	private void errorAlert(String error) {
		JOptionPane.showMessageDialog(this, error, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

}