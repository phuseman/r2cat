package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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
	private Checkbox repeatMaskingCheckBox;
	private JProgressBar progressBar;
	private JButton select;
	private JButton remove;
	private PrimerTableModel model;
	private JPanel controlPanel;

	public PrimerFrame(AlignmentPositionsList alignmentPositionsList) {
		this.alignmentPositionsList = alignmentPositionsList;
		primer = new PrimerTable(alignmentPositionsList);
		model = (PrimerTableModel) primer.getModel();
		// uebpruefen ob leer... dann FILE
		Vector<DNASequence> seq = alignmentPositionsList.getQueries();
		init();
	}

	private void init() {
		this.setTitle("Generate Primers");
		this.setLayout(new BorderLayout());
		JScrollPane tp = new JScrollPane(primer);
		this.add(tp, BorderLayout.CENTER);

		controlPanel = new JPanel();
		// controlPanel.add(new JLabel("Select"));
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
		controlPanel.add(setConfigButton);
		setConfigButton.addActionListener(this);

		repeatMaskingCheckBox = new Checkbox("Repeat Masking");
		controlPanel.add(repeatMaskingCheckBox);

		progressBar = new JProgressBar();
		progressBar.setToolTipText("Generating possible primer pairs for the contigs");
		progressBar.setStringPainted(true);

		// controlPanel.add(new JLabel("Generate Primers"));
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("generate_primer")) {

			if (alignmentPositionsList.getQueries() != null
					&& !alignmentPositionsList.getQueries().isEmpty()) {
				contigs = alignmentPositionsList.getQueries().get(0);
			} else {
				contigs = new DNASequence("dummy");
			}

			PrimerGeneratorTask pgT = new PrimerGeneratorTask();
			pgT.addPropertyChangeListener(this);
			contigPairs = ((PrimerTableModel) primer
					.getModel()).getSelectedPairs();
			if(contigPairs.size()>0) {
				pgT.execute();
			}

		} else if (e.getActionCommand().matches("setConfig")) {
			File config = this.chooseFile(configFile,
					"Select config (xml format)");
			this.setConfig(config, false);
			if (config != null) {
				this.setConfigButton.setText(configFile.getName());
				this.setConfigButton.setBackground(Color.decode("#90EE90"));
			} else {
				this.setConfigButton.setText("Set Config");
				this.setConfigButton.setBackground(null);
				configFile = null;
			}
		}
		this.invalidate();
		this.repaint();
	}

	public void showResults(Vector<PrimerResult> pResult) {
		PrimerResultFrame pr = new PrimerResultFrame(pResult);
		pr.pack();
		pr.setLocationByPlatform(true);
		pr.setVisible(true);
	}

	class PrimerGeneratorTask extends SwingWorker<Vector<PrimerResult>, String>
			implements AbstractProgressReporter {

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

			if (repeatMaskingCheckBox.getState()) {
				PrimerFrame.this.progressBar.setIndeterminate(true);
				PrimerFrame.this.progressBar
						.setString("Repeat masking using BLAST");
				pg.runRepeatMasking();
				PrimerFrame.this.progressBar.setString(null);
				PrimerFrame.this.progressBar.setIndeterminate(false);
			}
			pg.setParameters(configFile);
			Vector<PrimerResult> primerResult = pg.generatePrimers(contigPairs);
			/*long runningTime = new Date().getTime() - start; 
			System.out.println(runningTime/1000);*/
			return primerResult;

		}

		@Override
		protected void process(List<String> chunks) {
			//this method is called occasionally when a few temporary results were publish() ed. 
			progressBar.setString( chunks.get(chunks.size()-1));
		}

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
					// pg =null;
				} else {
					// No Primers found
				}
			}
		}
		
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
				progressBar.setIndeterminate(true);
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

	private void errorAlert(String error) {
		JOptionPane.showMessageDialog(this, error, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

}