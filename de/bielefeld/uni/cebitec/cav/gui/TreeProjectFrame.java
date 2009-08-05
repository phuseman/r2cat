/***************************************************************************
 *   Copyright (C) 2009 by Peter Husemann                                  *
 *   phuseman  a t  cebitec.uni-bielefeld.de                               *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.naming.CannotProceedException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import de.bielefeld.uni.cebitec.cav.treebased.MultifurcatedTree;
import de.bielefeld.uni.cebitec.cav.treebased.TreebasedContigSorterProject;
import de.bielefeld.uni.cebitec.cav.treebased.MultifurcatedTree.UnproperTreeException;
import de.bielefeld.uni.cebitec.cav.utils.AbstractProgressReporter;
import de.bielefeld.uni.cebitec.cav.utils.MiscFileUtils;
import de.bielefeld.uni.cebitec.cav.utils.ProgressMonitorReporter;
import de.bielefeld.uni.cebitec.cav.utils.Timer;

/**
 * 
 * @author phuseman
 */
public class TreeProjectFrame extends javax.swing.JFrame implements PropertyChangeListener {
	public class ReferenceSelection extends JPanel implements ActionListener,
			FocusListener, KeyListener {
		private JTextField tfReference;
		private JButton buReference;
		private String initialText = "Enter a path to a reference genome in FASTA format";

		public ReferenceSelection() {
			tfReference = new JTextField(initialText);
			tfReference.addKeyListener(this);
			tfReference.addFocusListener(this);

			buReference = new JButton("Remove");
			buReference.addActionListener(this);

			init();
		}

		public String getInitialText() {
			return initialText;
		}

		public boolean hasInitialText() {
			return this.tfReference.getText().equals(initialText);
		}

		public String getFile() {
			if(this.tfReference.getText().matches(initialText)) {
				return "";
			} else {
				return this.tfReference.getText();
			}
			
		}

		public ReferenceSelection(File f) {
			tfReference = new JTextField(f.getAbsolutePath());
			tfReference.setCaretPosition(tfReference.getText().length());
			tfReference.addKeyListener(this);
			tfReference.addFocusListener(this);

			buReference = new JButton("Remove");
			buReference.addActionListener(this);

			init();
		}

		private void init() {
			buReference.setMaximumSize(new java.awt.Dimension(116, 26));
			buReference.setMinimumSize(new java.awt.Dimension(116, 26));

			javax.swing.GroupLayout referencePanelLayout = new javax.swing.GroupLayout(
					this);
			this.setLayout(referencePanelLayout);
			referencePanelLayout
					.setHorizontalGroup(referencePanelLayout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
									javax.swing.GroupLayout.Alignment.TRAILING,
									referencePanelLayout
											.createSequentialGroup()
											.addContainerGap()
											.addComponent(
													tfReference,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													465, Short.MAX_VALUE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(
													buReference,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													121,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addContainerGap()));
			referencePanelLayout
					.setVerticalGroup(referencePanelLayout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
									referencePanelLayout
											.createParallelGroup(
													javax.swing.GroupLayout.Alignment.BASELINE)
											.addComponent(
													tfReference,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addComponent(
													buReference,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													21,
													javax.swing.GroupLayout.PREFERRED_SIZE)));

		}

		public void actionPerformed(ActionEvent e) {
			references.remove(this);
			setReferenceSelectionFromVector();
		}

		public void markNonexistingFiles() {
			if (hasInitialText()) {
				tfReference.setBackground(Color.WHITE);
			} else {
				File f = new File(tfReference.getText());
				if (f != null && f.isFile() && f.canRead()) {
					tfReference.setBackground(Color.WHITE);
				} else {
					tfReference.setBackground(wrong);
				}

			}

		}

		@Override
		public void focusGained(FocusEvent e) {
			if (((JTextField) e.getSource()).getText().equalsIgnoreCase(
					initialText)) {
				tfReference.selectAll();
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			// if(!checkFile(((JTextField) e.getSource()).getText())) {
			// errorAlert("FocusFile is not readable.");
			// }
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				File file = new File(((JTextField) e.getSource()).getText());

				// if the string is not a file, or cannot be read, issue an
				// error
				if (file == null || !file.canRead()) {
					errorAlert("Sorry, the given file is not readable.");
					// if the string is a directory, open a dialog to specify a
					// file in that directory
				} else if (file.isDirectory()) {
					File referenceFastaFile = MiscFileUtils.chooseFile(this,
							"Select a FASTA file containing one reference genome",
							file,true, new CustomFileFilter(".fas,.fna,.fasta", "FASTA File"));
					if (referenceFastaFile != null
							&& referenceFastaFile.canRead()) {
						lastDir=referenceFastaFile.getParentFile();
						tfReference.setText(referenceFastaFile
								.getAbsolutePath());
						setReferenceSelectionFromVector();
					}
					// if it is a file that can be read, enter it into the
					// textfield
				} else if (file.canRead()) {
					setReferenceSelectionFromVector();
				}

			}

		}

		@Override
		public void keyTyped(KeyEvent e) {
             ;//unsused
		}

		@Override
		public void keyPressed(KeyEvent e) {
			;//unsused
		}
	}

	public class  TreebasedContigSorterTask extends SwingWorker<File, String> implements AbstractProgressReporter{

		TreebasedContigSorterProject tcsp;
		ProgressMonitorReporter secondLevelProgressMonitor;
		
		public TreebasedContigSorterTask(TreebasedContigSorterProject tscp) {
			this.tcsp=tscp;
			tscp.register(this);
		}

		@Override
		protected File doInBackground() {
			File result=null;
			try {

			Timer t = Timer.getInstance();
			t.startTimer();
			
			t.startTimer();
			publish("Generating matches");
				tcsp.generateMatches();

			
			publish(t.stopTimer());
			
			t.startTimer();
			publish("Constructing layout graph");
			result = tcsp.sortContigs();
			publish(t.stopTimer());
			publish("Total time: " + t.stopTimer());
			
			//Catch if the memory is exhausted. If so display a message and return
			} catch (OutOfMemoryError e) {
				progress.append(e.getMessage());
				errorAlert("The heap memory was exhausted.\nTry to start this program with '-Xmx400m'.");
				return null;
			} catch (CannotProceedException e) {
				errorAlert("Could not finish task.\nDetails see progress log");
				return null;
			}
			return result;
		}

		@Override
		protected void done() {
			File result=null;
			try {
				result = this.get();
			} catch (InterruptedException e) {
				progress.append(e.getMessage()+"\n");
			} catch (ExecutionException e) {
				progress.append(e.getMessage()+"\n");
			}
			
			if(result!=null && result.exists()) {
				progressBar.setValue(100);
				progress.append("************************************\n");
				progress.append("The layout graph has been written to\n"+ result.getAbsolutePath() + "\n");
				progress.append("The graph can be displayed using neato of the GraphViz package:\n" +
						"neato -Tps -o graph.ps "+ result.getName() + "\n");
				JOptionPane.showMessageDialog(TreeProjectFrame.this, "The resulting layout has been written in a \n*.neato file into the projects directory.", "Success",
						JOptionPane.INFORMATION_MESSAGE);

			}

		}

		@Override
		protected void process(List<String> chunks) {
		     for (String comment : chunks) {
	             progress.append(comment + "\n");
	         }
             progress.setCaretPosition(progress.getDocument().getLength());

		}

		@Override
		public void reportProgress(double percentDone, String comment) {
			if(percentDone>=0 && percentDone<=1) {
				setProgress((int) (percentDone*100.));
			}
			if (comment != null && !comment.isEmpty()) {
				publish(comment);
			}
		}
		

		/**
		 * Returns a progress monitor that implements the {@link AbstractProgressReporter}.
		 * It can be used to display the progress while matching with another progressbar.
		 * @param title string to display in the window
		 * @return returns an ProgressMonitor, that only displays the progres from 0-100.
		 */
		public ProgressMonitorReporter showSecondLevelProgressMonitor(String title) {
			secondLevelProgressMonitor = new ProgressMonitorReporter(TreeProjectFrame.this,title,null);
			
			return secondLevelProgressMonitor;

		}

		
	}
	

	


	
	
	private Vector<ReferenceSelection> references;

	private File lastDir;
	
	//light red, as background for wrong entries
	private Color wrong = new Color(255,131,131);
	
	
	



	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// these events are generated by the SwingWorker thread
		//
		// progress integer values from 0 - 100
		if (evt.getPropertyName().matches("progress")) {
			progressBar.setValue((Integer) evt.getNewValue());
			
			// or state changes, see SwingWorker.StateValue
		} else if (evt.getPropertyName().matches("state")) {
			if ((SwingWorker.StateValue) evt.getNewValue() == SwingWorker.StateValue.STARTED) {
				progress.append("\nStarting algorithm\n");
				progress.setCaretPosition(progress.getDocument().getLength());
				progressBar.setValue(0);
				progressBar.setIndeterminate(false);
				runButton.setEnabled(false);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			} else if ((SwingWorker.StateValue) evt.getNewValue() == SwingWorker.StateValue.DONE) {
				// progressBar.setValue(100);
				progressBar.setIndeterminate(false);
				progress.setCaretPosition(progress.getDocument().getLength());
				runButton.setEnabled(true);
				setCursor(null);
			}
		}
	}



	/** Creates new form TreeProjectFrame */
	public TreeProjectFrame() {
		references = new Vector<ReferenceSelection>();
		references.add(new ReferenceSelection());
		initComponents();
	}




	/**
	 * Checks all given files and starts a background task to compute a layout.
	 * @param evt
	 */
	private void runAlgorithm(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_runAlgorithm
		boolean errorOccured = false;

		progressBar.setValue(0);
		progressBar.setIndeterminate(true);
		progress.setText("");

		progress.append("Checking files..\n");

		// check project directory
		File projectDir = new File(tfProjectdir.getText());
		if (!projectDir.isDirectory() || !projectDir.canWrite()) {
			tfProjectdir.setBackground(wrong);
			progress
					.append("Project directory is not writable or does not exist.\n");
			errorOccured = true;
		} else {
			tfProjectdir.setBackground(Color.WHITE);
		}

		// check contigs file
		File contigs = new File(tfContigs.getText());
		if (!contigs.isFile() || !contigs.canRead()) {
			tfContigs.setBackground(wrong);
			progress.append("Contigs file is not readable\n");
			errorOccured = true;
		} else {
			tfContigs.setBackground(Color.WHITE);
		}

		// check reference files
		int unreadableReferences = 0;
		int referenceNumber = 0;
		Vector<File> referenceFiles = new Vector<File>();
		for (ReferenceSelection refSel : references) {
			if (!refSel.hasInitialText()) {
				File f = new File(refSel.getFile());
				if (!f.isFile() || !f.canRead()) {
					unreadableReferences++;
					errorOccured = true;
				} else {
					referenceFiles.add(f);
					referenceNumber++;
				}
			}
		}
		if (unreadableReferences > 0) {
			progress.append(unreadableReferences
					+ "References file(s) not readable\n");
		}
		if (referenceNumber == 0) {
			errorOccured = true;
			progress.append("No reference files given\n");

		}

		MultifurcatedTree phylogeneticTree = null;
		try {
			File treeFile = new File(tfPhylogeneticTree.getText());
			if (treeFile.exists() && treeFile.canRead()) {
				phylogeneticTree = new MultifurcatedTree(treeFile);
				tfPhylogeneticTree.setBackground(Color.WHITE);
			} else {
				progress.append("NOT using a phylogenetic tree\n");
				phylogeneticTree = null;
				tfPhylogeneticTree.setBackground(Color.LIGHT_GRAY);
			}
		} catch (IOException e) {

			progress.append("Could not read the tree\n" + e.getMessage()+"\n");
			errorOccured = true;
			tfPhylogeneticTree.setBackground(wrong);
		} catch (UnproperTreeException e) {
			progress.append("Error parsing the phylogenetic tree:\n" + e.getMessage()+"\n");
			errorOccured = true;
			tfPhylogeneticTree.setBackground(wrong);
		}

		if (errorOccured) {
			progressBar.setValue(0);
			progressBar.setIndeterminate(false);
			progress.append("Error(s): Stopping here.\n");

			// this will mark nonexisting files in red
			setReferenceSelectionFromVector();
			return;
		} else {
			// no errors so far
			progress.append("..ok\n");

			// write the project into the projects directory, using the contigs
			// filename as filename.
			File project = new File(projectDir.getAbsolutePath()
					+ File.separator + contigs.getName());
			project = MiscFileUtils.enforceExtension(project, ".tcp");
			if (project != null) {
				if (!project.exists() || project.canWrite()) {
					try {
						writeProjectToFile(project);
						progress.append("Saved this project automatically to\n"
								+ project.getName() + "\n");
					} catch (IOException e) {
						progress
								.append("Could not automatically save this project\n");
					}
				} else {
					progress
							.append("Could not automatically save this project\n");
				}

				// start the algorithm in a background task
				TreebasedContigSorterProject tscp = new TreebasedContigSorterProject(
						contigs, referenceFiles, projectDir, phylogeneticTree);

				TreebasedContigSorterTask backgroundTask = new TreebasedContigSorterTask(
						tscp);
				backgroundTask.addPropertyChangeListener(this);
				backgroundTask.execute();

			}
		}

	}// GEN-LAST:event_runAlgorithm



	/**
	 * Creates a dynamic panel which shows the reference genomes from the vector references.
	 * If a file is not existing the textfield will get a light red background.
	 */
	private void setReferenceSelectionFromVector() {
		referenceGenomesFilesPanel.removeAll();

		Vector<ReferenceSelection> croppedList = new Vector<ReferenceSelection>();

		for (int i = 0; i < references.size(); i++) {
			if (!references.get(i).hasInitialText()) {
				croppedList.add(references.get(i));
			}
		}

		croppedList.add(new ReferenceSelection());

		references = croppedList;

		// go through the vector and add each reference panel to the list of
		// panels
		for (int i = 0; i < references.size(); i++) {
			references.get(i).markNonexistingFiles();
			referenceGenomesFilesPanel.add(references.get(i));
		}

		referenceGenomesFilesPanel.revalidate();
		referenceGenomesFilesPanel.repaint();

		// TODO request focus from the last component. below does not work...
		// referenceGenomesFilesPanel.getComponent(referenceGenomesFilesPanel.getComponentCount()-1).requestFocusInWindow();
	}

	private void addContigsFile(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addContigsFile
		File f = MiscFileUtils.chooseFile(this,
				"Select a multi FASTA file that contains the contigs", lastDir, true, new CustomFileFilter(".fas,.fna,.fasta", "FASTA File"));
		if (f != null && f.canRead()) {
			lastDir=f.getParentFile();
			tfContigs.setBackground(Color.WHITE);
			tfContigs.setText(f.getAbsolutePath());
			tfContigs.setCaretPosition(tfContigs.getText().length());
		}
	}// GEN-LAST:event_addContigsFile

	private void addProjectDir(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addProjectDir
		File dir = MiscFileUtils.chooseDir(this,"Select a project directory", lastDir);
		if (dir != null && dir.isDirectory() && dir.canWrite()) {
			lastDir=dir;
			tfProjectdir.setBackground(Color.WHITE);
			tfProjectdir.setText(dir.getAbsolutePath());
			tfProjectdir.setCaretPosition(tfProjectdir.getText().length());
		}
	}// GEN-LAST:event_addProjectDir

	private void addPhylogeneticTree(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addPhylogeneticTree
		File f = MiscFileUtils.chooseFile(this,"Select phylogenetic tree in Newick format", lastDir, true, new CustomFileFilter(".newick,.txt,.tree,.phylip", "Newick Tree"));
		if (f != null && f.canRead()) {
			lastDir=f.getParentFile();
			tfPhylogeneticTree.setBackground(Color.WHITE);
			tfPhylogeneticTree.setText(f.getAbsolutePath());
			tfPhylogeneticTree.setCaretPosition(tfPhylogeneticTree.getText()
					.length());
		}
	}// GEN-LAST:event_addPhylogeneticTree

	private void addReferenceFile(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addReferenceFile
		File f = MiscFileUtils.chooseFile(this,
				"Select a FASTA file containing one reference genome",
				lastDir,true, new CustomFileFilter(".fas,.fna,.fasta", "FASTA File"));
		if (f != null && f.canRead()) {
			lastDir=f.getParentFile();
			references.add(new ReferenceSelection(f));
			setReferenceSelectionFromVector();
		}

	}// GEN-LAST:event_addReferenceFile

	private void loadProject(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_loadProject
		File load = MiscFileUtils.chooseFile(this,
				"Select a treecat project file to load", lastDir, true,
				new CustomFileFilter(".tcp", "treecat project"));
		if (load != null) {
			try {
				loadProjectFromFile(load);
			} catch (IOException e) {
				errorAlert("Could not open file " + load.getName());
			}
		}
	}// GEN-LAST:event_loadProject

	private void saveProject(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveProject
		File save = MiscFileUtils.chooseFile(this,
				"Select a treecat project file to save", lastDir, false,
				new CustomFileFilter(".tcp", "treecat project"));
		if (save != null) {
			save = MiscFileUtils.enforceExtension(save, ".tcp");
			try {
				writeProjectToFile(save);
			} catch (IOException e) {
				errorAlert("Could not write to file " + save.getName());
			}
		}
	}// GEN-LAST:event_saveProject

	private void writeProjectToFile(File f) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(f));

		out.write("# treecat: Treebased contig arrangement tool\n"
		+"# tcp - (t)ree(c)at (p)roject\n\n");
		out.write("# directory where to cache the matchings between contigs and references\n");
		out.write("projectdir=\"");
		if(new File(tfProjectdir.getText()).exists()) {
			out.write(tfProjectdir.getText());
		}
		out.write("\"\n");

		
		out.write("\n# Phylogenetic tree of the species.\n# The species must have as name the filename of the fasta file without extension!\n");
		out.write("newicktreefile=\"");
		if(new File(tfPhylogeneticTree.getText()).exists()) {
			out.write(tfPhylogeneticTree.getText());
		}
		out.write("\"\n");

		out.write("\n# Contigs in multi fasta format\n");
		out.write("contigs=\"");
		if(new File(tfContigs.getText()).exists()) {
			out.write(tfContigs.getText());
		}
		out.write("\"\n");

		out.write("\n# Reference genomes in (multi) fasta format. One entry per genome.\n");
		for (int i = 0; i < references.size(); i++) {
			if(new File(references.get(i).getFile()).exists()) {
				out.write("reference=\"");
				out.write(references.get(i).getFile());
				out.write("\"\n");
			}
		}
		out.write(" ");
		out.close();
	}
	
	
	/**
	 * Pop up an error message
	 * 
	 * @param error
	 *            Message
	 */
	private void errorAlert(String error) {
		JOptionPane.showMessageDialog(this, error, "Error",
				JOptionPane.ERROR_MESSAGE);
	}



	public void loadProjectFromFile(File f) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(f));
		String line;
		String[] propertyValue;
		String value;

		String projectDirectoryFileString = "";
		String contigsFileString = "";
		Vector<File> referenceFileStrings = new Vector<File>();
		String phylogeneticTreeFileString = "";

		String currentWorkingDirectory = f.getParent();

		while (in.ready()) {
			line = in.readLine();

			// ignore comments
			if (line.startsWith("#") || line.startsWith("\"#")
					|| line.isEmpty()) {
				continue;
			}

			// split at the = sign into the pair property=value.
			propertyValue = line.split("=");

			// skip if more than one '=' sign
			if (propertyValue.length != 2) {
				continue;
			}

			// remove trailing and leading whitespaces
			value = propertyValue[1].trim();
			// remove trailing quotation marks
			if (value.charAt(0) == '"'
					&& value.charAt(value.length() - 1) == '"') {
				value = value.substring(1, value.length() - 1);
			}

			// check different keywords:
			// ********************contigs*********************
			if (propertyValue[0].matches("contigs")) {
				if( value.isEmpty()) {continue;}
				if (contigsFileString.isEmpty()) {
					File file = new File(value);

					// if not existant, try relative path
					if (!file.exists()) {
						file = new File(currentWorkingDirectory
								+ File.separator + value);
					}

					if (file.exists()&& file.isFile()) {
						contigsFileString = file.getAbsolutePath();
					} else {
						progress
								.append("Specified contig file could not be found: "
										+ file.getAbsoluteFile()+"\n");
					}

				} else { // contigs were already specified
					progress
							.append("A contig file was given more often than once. Using first occurrence.\n");
				}

			}

			// ********************reference*********************
			if (propertyValue[0].matches("reference")) {
				if( value.isEmpty()) {continue;}

				File file = new File(value);
				// if not existant, try relative path
				if (!file.exists()) {
					file = new File(currentWorkingDirectory + File.separator
							+ value);
				}

				if (file.exists() && file.isFile()) {
					referenceFileStrings.add(file);
				} else {
					progress
							.append("Specified reference file could not be found: "
									+ file.getAbsoluteFile()+"\n");
				}
				

			}

			// ********************newicktree*********************
			if (propertyValue[0].matches("newicktreefile")) {
				if( value.isEmpty()) {continue;}
				if (phylogeneticTreeFileString.isEmpty()) {
					File file = new File(value);

					// if not existant, try relative path
					if (!file.exists()) {
						file = new File(currentWorkingDirectory
								+ File.separator + value);
					}

					if (file.exists() && file.isFile()) {
						phylogeneticTreeFileString = file.getAbsolutePath();
					} else {
						progress
								.append("Specified newick file could not be found: "
										+ file.getAbsoluteFile()+"\n");
					}

				} else { // contigs were already specified
					progress
							.append("A newick file was given more often than once. Using first occurrence.\n");
				}

			}

			// ********************projectdir*********************
			if (propertyValue[0].matches("projectdir")) {
				if( value.isEmpty()) {continue;}

				if (projectDirectoryFileString.isEmpty()) {
					File dir = new File(value);
					if (dir.isDirectory() && dir.canWrite()) {
						projectDirectoryFileString = dir.getAbsolutePath();
					} else {
						progress
								.append("Project Directory not present or not writable: "
										+ dir +"\n");
					}
				} else {
					progress
							.append("A project was given more often than once. Using first occurrence.\n");
				}
			}

		} // file has been parsed completely

		tfProjectdir.setText(projectDirectoryFileString);
		tfProjectdir.setBackground(Color.WHITE);
		tfContigs.setText(contigsFileString);
		tfContigs.setBackground(Color.WHITE);
		tfPhylogeneticTree.setText(phylogeneticTreeFileString);
		tfPhylogeneticTree.setBackground(Color.WHITE);
		
		references.clear();
		for (File file : referenceFileStrings) {
			references.add(new ReferenceSelection(file));
		}

		setReferenceSelectionFromVector();

	}

	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buContigs;
    private javax.swing.JButton buPhylogeneticTree;
    private javax.swing.JButton buProjectDir;
    private javax.swing.JButton buReferences;
    private javax.swing.JPanel contigPanel;
    private javax.swing.JButton loadButton;
    private javax.swing.JLabel phylogeneticTreeLabel;
    private javax.swing.JPanel phylogeneticTreePanel;
    private javax.swing.JTextArea progress;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JScrollPane progressScrollPane;
    private javax.swing.JPanel projectDirPanel;
    private javax.swing.JPanel referenceGenomesFilesPanel;
    private javax.swing.JPanel referenceGenomesPanel;
    private javax.swing.JScrollPane referenceGenomesScrollPane;
    private javax.swing.JButton runButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField tfContigs;
    private javax.swing.JTextField tfPhylogeneticTree;
    private javax.swing.JTextField tfProjectdir;
    // End of variables declaration//GEN-END:variables

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectDirPanel = new javax.swing.JPanel();
        tfProjectdir = new javax.swing.JTextField();
        buProjectDir = new javax.swing.JButton();
        contigPanel = new javax.swing.JPanel();
        buContigs = new javax.swing.JButton();
        tfContigs = new javax.swing.JTextField();
        phylogeneticTreePanel = new javax.swing.JPanel();
        tfPhylogeneticTree = new javax.swing.JTextField();
        buPhylogeneticTree = new javax.swing.JButton();
        phylogeneticTreeLabel = new javax.swing.JLabel();
        progressPanel = new javax.swing.JPanel();
        progressScrollPane = new javax.swing.JScrollPane();
        progress = new javax.swing.JTextArea();
        runButton = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        referenceGenomesPanel = new javax.swing.JPanel();
        buReferences = new javax.swing.JButton();
        referenceGenomesScrollPane = new javax.swing.JScrollPane();
        referenceGenomesFilesPanel = new javax.swing.JPanel();
        loadButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Phylogenetic Comparative Assembly");
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(600, 400));
        setName("Phylogenetic Comparative Assembly"); // NOI18N

        projectDirPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Project Directory"));
        projectDirPanel.setMinimumSize(new java.awt.Dimension(160, 47));

        tfProjectdir.setText("Project Directory");
        tfProjectdir.setToolTipText("Select a directory where several files can be stored");
        tfProjectdir.setMinimumSize(new java.awt.Dimension(50, 20));

        buProjectDir.setText("Select Dir");
        buProjectDir.setToolTipText("Select a directory where several files can be stored");
        buProjectDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProjectDir(evt);
            }
        });

        javax.swing.GroupLayout projectDirPanelLayout = new javax.swing.GroupLayout(projectDirPanel);
        projectDirPanel.setLayout(projectDirPanelLayout);
        projectDirPanelLayout.setHorizontalGroup(
            projectDirPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(projectDirPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tfProjectdir, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buProjectDir, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        projectDirPanelLayout.setVerticalGroup(
            projectDirPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(projectDirPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(tfProjectdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(buProjectDir, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        contigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Contigs"));
        contigPanel.setMinimumSize(new java.awt.Dimension(160, 47));

        buContigs.setText("Select Contigs");
        buContigs.setToolTipText("Select a FASTA file containing the contigs");
        buContigs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addContigsFile(evt);
            }
        });

        tfContigs.setText("Contigs");
        tfContigs.setToolTipText("Select a FASTA file containing the contigs");
        tfContigs.setMinimumSize(new java.awt.Dimension(50, 20));

        javax.swing.GroupLayout contigPanelLayout = new javax.swing.GroupLayout(contigPanel);
        contigPanel.setLayout(contigPanelLayout);
        contigPanelLayout.setHorizontalGroup(
            contigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tfContigs, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buContigs, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        contigPanelLayout.setVerticalGroup(
            contigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(tfContigs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(buContigs, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        phylogeneticTreePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Phylogenetic Tree (optional)"));
        phylogeneticTreePanel.setMinimumSize(new java.awt.Dimension(160, 82));

        tfPhylogeneticTree.setText("Phylogenetic tree in Newick format");
        tfPhylogeneticTree.setToolTipText("Enter the path to a phylogenetic tree in Newick format");
        tfPhylogeneticTree.setMinimumSize(new java.awt.Dimension(50, 20));

        buPhylogeneticTree.setText("Select Tree");
        buPhylogeneticTree.setToolTipText("Enter the path to a phylogenetic tree in Newick format");
        buPhylogeneticTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPhylogeneticTree(evt);
            }
        });

        phylogeneticTreeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        phylogeneticTreeLabel.setText("Note: The species name in the tree must be the name of the above files, without ending.");

        javax.swing.GroupLayout phylogeneticTreePanelLayout = new javax.swing.GroupLayout(phylogeneticTreePanel);
        phylogeneticTreePanel.setLayout(phylogeneticTreePanelLayout);
        phylogeneticTreePanelLayout.setHorizontalGroup(
            phylogeneticTreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(phylogeneticTreePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(phylogeneticTreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, phylogeneticTreePanelLayout.createSequentialGroup()
                        .addComponent(tfPhylogeneticTree, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buPhylogeneticTree, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(phylogeneticTreePanelLayout.createSequentialGroup()
                        .addComponent(phylogeneticTreeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                        .addGap(162, 162, 162))))
        );
        phylogeneticTreePanelLayout.setVerticalGroup(
            phylogeneticTreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(phylogeneticTreePanelLayout.createSequentialGroup()
                .addGroup(phylogeneticTreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfPhylogeneticTree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buPhylogeneticTree, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(phylogeneticTreeLabel))
        );

        progressPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Progress"));
        progressPanel.setMinimumSize(new java.awt.Dimension(160, 158));

        progress.setColumns(20);
        progress.setEditable(false);
        progress.setLineWrap(true);
        progress.setRows(5);
        progress.setTabSize(2);
        progress.setWrapStyleWord(true);
        progress.setMargin(new java.awt.Insets(5, 5, 5, 5));
        progressScrollPane.setViewportView(progress);

        runButton.setText("Run");
        runButton.setToolTipText("<html>\nRuns the Phylogenetic Comparative Assembly algorithm to devise a layout graph for the contigs.<br />\nThe layout as well as the matchings are saved in the project directory.\n</html>");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runAlgorithm(evt);
            }
        });

        progressBar.setStringPainted(true);

        javax.swing.GroupLayout progressPanelLayout = new javax.swing.GroupLayout(progressPanel);
        progressPanel.setLayout(progressPanelLayout);
        progressPanelLayout.setHorizontalGroup(
            progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, progressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                    .addComponent(progressScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addComponent(runButton)
                .addContainerGap())
        );
        progressPanelLayout.setVerticalGroup(
            progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(progressPanelLayout.createSequentialGroup()
                .addGroup(progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                    .addComponent(runButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        referenceGenomesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reference Genomes"));

        buReferences.setText("Add Reference");
        buReferences.setToolTipText("<html>Click here to add another reference genome.<br />\nPlease give each reference genome in a seperate file.</html>");
        buReferences.setPreferredSize(new java.awt.Dimension(116, 26));
        buReferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addReferenceFile(evt);
            }
        });

        referenceGenomesScrollPane.setBorder(null);
        referenceGenomesScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        referenceGenomesScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        referenceGenomesScrollPane.setHorizontalScrollBar(null);

        referenceGenomesFilesPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 2));
        referenceGenomesScrollPane.setViewportView(referenceGenomesFilesPanel);
        setReferenceSelectionFromVector();

        javax.swing.GroupLayout referenceGenomesPanelLayout = new javax.swing.GroupLayout(referenceGenomesPanel);
        referenceGenomesPanel.setLayout(referenceGenomesPanelLayout);
        referenceGenomesPanelLayout.setHorizontalGroup(
            referenceGenomesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(referenceGenomesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, referenceGenomesPanelLayout.createSequentialGroup()
                .addContainerGap(531, Short.MAX_VALUE)
                .addComponent(buReferences, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        referenceGenomesPanelLayout.setVerticalGroup(
            referenceGenomesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(referenceGenomesPanelLayout.createSequentialGroup()
                .addComponent(buReferences, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(referenceGenomesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
        );

        loadButton.setText("Load");
        loadButton.setToolTipText("Load a treecat project from file");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadProject(evt);
            }
        });

        saveButton.setText("Save");
        saveButton.setToolTipText("Saves a the given information to a file");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProject(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loadButton)
                    .addComponent(saveButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(phylogeneticTreePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(referenceGenomesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(contigPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(projectDirPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(projectDirPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(contigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(referenceGenomesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(phylogeneticTreePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(loadButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

}
