package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerGenerator;
import de.bielefeld.uni.cebitec.cav.controller.SequenceNotFoundException;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.datamodel.PrimerTableModel;
import de.bielefeld.uni.cebitec.cav.utils.MiscFileUtils;
import de.bielefeld.uni.cebitec.cav.utils.ProgressMonitorReporter;

public class PrimerFrame extends JFrame implements ActionListener, ItemListener {
	
	private AlignmentPositionsList alignmentPositionsList;
	private DNASequence contigs;
	private PrimerTableModel model;
	private PrimerTable primer;
	private JButton setConfig;
	private boolean repeatMasking = false;
	private File configFile;
	private File lastDir;
	private PrimerGenerator pg;
	private Vector<String[]> contigPairs = null;
	private Checkbox rp;
	
	public PrimerFrame(AlignmentPositionsList alignmentPositionsList){
		this.alignmentPositionsList = alignmentPositionsList;
		primer = new PrimerTable(alignmentPositionsList);
		model = (PrimerTableModel) primer.getModel();
		init();
	}
	private void init(){
		this.setTitle("Generate Primers");
		this.setLayout(new BorderLayout());
		JScrollPane tp = new JScrollPane(primer);
		this.add(tp,BorderLayout.CENTER);

		JPanel controlPanel = new JPanel();
		controlPanel.add(new JLabel("Select"));
		JButton select = new JButton("all");
		select.setActionCommand("select_all");
		controlPanel.add(select);
		select.addActionListener(primer);
		JButton remove = new JButton("none");
		remove.setActionCommand("select_none");
		controlPanel.add(remove);
		remove.addActionListener(primer);
		
		rp= new Checkbox("Repeat Masking");
		controlPanel.add(rp);
		rp.addItemListener(this);
		
		setConfig = new JButton("Set Config");
		setConfig.setActionCommand("setConfig");
		controlPanel.add(setConfig);
		setConfig.addActionListener(this);
		
		controlPanel.add(new JLabel("Generate Primers"));
		JButton run = new JButton("Run!");
		run.setActionCommand("generate_primer");
		controlPanel.add(run);
		run.addActionListener(this);
		
		this.add(controlPanel,BorderLayout.SOUTH);
		this.pack();
		
		int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

		this.setSize(new Dimension(width/3,height));

	}
	
	private File chooseFile(File prevFile, String dialogTitle) {
		if (prevFile != null && prevFile.getParentFile().exists()) {
			lastDir=prevFile.getParentFile();
		}
		return MiscFileUtils.chooseFile(this, dialogTitle, lastDir, true, new CustomFileFilter(".xml", "XML File"));
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("generate_primer")) {
			
			if(alignmentPositionsList.getQueries() != null && !alignmentPositionsList.getQueries().isEmpty()) {
				contigs = alignmentPositionsList.getQueries().get(0);
			} else {
				contigs = new DNASequence("dummy");
			}
			
			PrimerGeneratorTask pgT = new PrimerGeneratorTask();
			contigPairs = (Vector<String[]>)((PrimerTableModel)primer.getModel()).getSelectedPairs();
			pgT.execute();

		} else if(e.getActionCommand().matches("setConfig")){
			File config = this.chooseFile(configFile,
			"Select config (xml format)");
			this.setConfig(config, false);
			if(config!=null){
				this.setConfig.setText(configFile.getName());
				this.setConfig.setBackground(Color.LIGHT_GRAY);
			} else{
				this.setConfig.setText("Set Config");
				this.setConfig.setBackground(null);
				configFile = null;
			}
		}
		this.invalidate();
		this.repaint();
	}
	
	class PrimerGeneratorTask extends SwingWorker<PrimerGenerator,String>{
		@Override
		protected PrimerGenerator doInBackground() throws Exception,IOException {
			System.out.println("Generating Primers");
			if (!contigs.getFile().exists() || !contigs.getFile().canRead()) {
				throw new SequenceNotFoundException("Could not find or read the contigs file",contigs);
			}

		File fastaFile = contigs.getFile();
		File outputDir = new File(System.getProperty("user.home"));
		boolean rm = false;
		if(repeatMasking){
			rm = repeatMasking;
		if(configFile.exists()){
			pg = new PrimerGenerator(fastaFile,configFile,rm,outputDir);
			}else{
				pg = new PrimerGenerator(fastaFile,rm,outputDir);
			}
		} else{
			rm = repeatMasking;
			if(configFile != null && configFile.exists()){
			pg = new PrimerGenerator(fastaFile,configFile,rm,outputDir);
			}else{
				pg = new PrimerGenerator(fastaFile,rm,outputDir);
			}
		}
		ProgressMonitorReporter progressReporter = new ProgressMonitorReporter(PrimerFrame.this,"Generating Primers","Generating primers");
		pg.registerProgressReporter(progressReporter);
			pg.generatePrimers(contigPairs);
			return pg;
		}
		
	}

	
	private void setConfig(File q, boolean silent){
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
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		}
	}
