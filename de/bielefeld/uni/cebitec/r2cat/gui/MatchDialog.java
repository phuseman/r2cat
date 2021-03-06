/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
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
package de.bielefeld.uni.cebitec.r2cat.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import de.bielefeld.uni.cebitec.common.AbstractProgressReporter;
import de.bielefeld.uni.cebitec.common.CustomFileFilter;
import de.bielefeld.uni.cebitec.common.MiscFileUtils;
import de.bielefeld.uni.cebitec.common.Timer;
import de.bielefeld.uni.cebitec.qgram.DNASequence;
import de.bielefeld.uni.cebitec.qgram.FastaFileReader;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.qgram.QGramFilter;
import de.bielefeld.uni.cebitec.qgram.QGramIndex;
import de.bielefeld.uni.cebitec.r2cat.R2catPrefs;
import de.bielefeld.uni.cebitec.r2cat.R2cat;

/**
 * This is a dialog to select two fasta files in order to match them.
 * @author phuseman, rhilker
 * 
 */
public class MatchDialog extends JDialog implements ActionListener, WindowListener,
		PropertyChangeListener, AbstractProgressReporter{

	private Preferences prefs;

	private JProgressBar progressBar;
	private JButton startButton;
	private JButton saveUnmatchedButton;
	private JTextArea progress;
	private QGramMatcherTask matcherTask;

	private JTextField tfQuery;

	private JTextField tfTarget;

	private JButton buQuery;

	private JButton buTarget;

	private File query;

	private File target;
	
	private File lastDir;

	private MatchList result;

	class QGramMatcherTask extends SwingWorker<MatchList, String> {
		@Override
		protected MatchList doInBackground() {
			try { // catches OutOfMemoryError s

				int nbUnmatched = 0;
				
				progressBar.setValue(0);
				progressBar.setIndeterminate(true);
				progress.setText("");

				Timer t = Timer.getInstance();
				t.startTimer();

				try { // catches io exceptions

					FastaFileReader targetFasta = new FastaFileReader(target);

					boolean targetIsFasta = targetFasta.isFastaQuickCheck();

					if (targetIsFasta) {
						progress.append("Opening target file "
								+ target.getName() + " (" + target.length()
								+ ")");
						t.startTimer();
						targetFasta.scanContents(true);
						progress.append(" ..." + t.stopTimer() + "\n");
					} else {
						progress
								.append("Error: No valid id line (>idtag ...) found within the first 100 lines");
						this.setEndMatching(false);
						return null;
					}

					FastaFileReader queryFasta = new FastaFileReader(query);
					boolean queryIsFasta = queryFasta.isFastaQuickCheck();
					if (queryIsFasta) {
						progress.append("Opening query file " + query.getName()
								+ " (" + query.length() + ")");
						t.startTimer();
						queryFasta.scanContents(true);
						progress.append(" ..." + t.stopTimer() + "\n");

					} else {
						progress
								.append("Error: No valid id line (>idtag ...) found within the first 100 lines");
						this.setEndMatching(false);
						return null;
					}

					// check if targets and queries might be switched
					double averageTargetSize = 0;
					for (DNASequence seq : targetFasta.getSequences()) {
						averageTargetSize += seq.getSize();
					}
					averageTargetSize /= targetFasta.getSequences().size();

					double averageQuerySize = 0;
					for (DNASequence seq : queryFasta.getSequences()) {
						averageQuerySize += seq.getSize();
					}
					averageQuerySize /= queryFasta.getSequences().size();

					String warningMsg = "";
					if (averageQuerySize > averageTargetSize) {
						warningMsg = "The queries are bigger than the target sequences on average.";
					} else if (targetFasta.getSequences().size() > queryFasta
							.getSequences().size()) {
						warningMsg = "There are more targets than queries.";
					}

					// if there is an error, display dialog
					if (!warningMsg.equals("")) {
						progressBar.setIndeterminate(false);
						int warningAnswer = -1;
						warningMsg += "\nThis can lead to problems during the matching phase\n"
								+ "and in the visualization.";
						Object[] options = { "I know, continue!",
								"Switch sequences" };
						warningAnswer = JOptionPane.showOptionDialog(
								MatchDialog.this, warningMsg,
								"Target and queries switched?",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE, null, options,
								options[1]);
						if (warningAnswer == 1) {
							// switch query <-> target
							FastaFileReader tmp;
							tmp = queryFasta;
							queryFasta = targetFasta;
							targetFasta = tmp;
						}// else just continue
						progressBar.setIndeterminate(true);

					}

					progress.append("Generating q-Gram Index\n");
					t.startTimer();
					QGramIndex qi = new QGramIndex();
					qi.register(MatchDialog.this);
					qi.generateIndex(targetFasta);
					progress.append(" Generating q-Gram Index took: "
							+ t.stopTimer() + "\n");

					System.gc();

					progressBar.setIndeterminate(false);

					progress.append("Matching:\n");
					t.startTimer();
					QGramFilter qf = new QGramFilter(qi, queryFasta);
					qf.register(MatchDialog.this);
					result = qf.match();
					progress.append("Matching took: " + t.stopTimer() + "\n");

					progress.append("Total time: " + t.stopTimer() + "\n");
					progressBar.setValue(100);

					if(result!=null 
							&& result.size() > 0 
							&&  queryFasta.getSequences().size() > result.getQueries().size()) {
						//if not all contigs could be matched
						int contigs = queryFasta.getSequences().size();
						int matchedContigs = result.getQueries().size();
						nbUnmatched = contigs - matchedContigs;
						
						List<DNASequence> unmatchedContigs = this.setUnmatchedContigs(queryFasta);
						//the following does only work if there is already a list of matches displayed...
						//R2cat.dataModelController.setUnmatchedContigs(unmatchedContigs);
						// instead, we want to set this in the freshly matched contigs...
						result.setUnmatchedContigs(unmatchedContigs);
						
						informationAlert("There were " + nbUnmatched + " out of "+ contigs+" queries that could not be matched." +
						"\nDetails are shown in the progress log.");
						
					}

					progress.append("Done!\n");

				} catch (IOException e) {
					progress.append("\n"+ e.getMessage()+"\n");
					errorAlert(e.getMessage());
				}


				this.setEndMatching(nbUnmatched > 0);
				
				
				return result;

				// Catch if the memory is exhausted. If so display a message and
				// return
			} catch (OutOfMemoryError e) {
				progressBar.setIndeterminate(false);
				
				int heapmem = (int) Math.ceil((
						Runtime.getRuntime().maxMemory()/(1024.*1024.)));
				
				errorAlert("Sorry, the maximal heap memory (currently ~"+heapmem+"MB) was\n" +
						"exhausted. Probably the reference genome was too big.\n"
						+ "Try to increase the heap size, e.g. to "+(2*heapmem)+"MB, " 
						+ "by using the Java option '-Xmx"+(2*heapmem)+"m'.");

				progress.append(e.toString());

				this.setEndMatching(false);

				return null;
			}
		}
		
		/**
		 * @author Rolf Hilker
		 * 
		 * Identifies all unmatched contigs among the contigs in the
		 * query fasta.
		 * @param queryFasta the query fasta used as input for matching
		 * @return the list of unmatched contigs
		 */
		private List<DNASequence> setUnmatchedContigs(FastaFileReader queryFasta) {
			List<String> ids = new ArrayList<String>(); 
			for (DNASequence seq : result.getQueries()) {
				ids.add(seq.getId());
			}
			
			List<DNASequence> unmatchedContigs = new ArrayList<DNASequence>();
			for (DNASequence seq : queryFasta.getSequences()) {
				if (!ids.contains(seq.getId())) {
					unmatchedContigs.add(seq);
				}
			}
			return unmatchedContigs;
		}

		private void setEndMatching(boolean enableSaveUnmated) {
			startButton.setEnabled(true);
			saveUnmatchedButton.setEnabled(enableSaveUnmated);
			setCursor(null); // turn off the wait cursor
			progress.setCaretPosition(progress.getDocument().getLength());
			progressBar.setIndeterminate(false);

		}

		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#done()
		 */
		public void done() {
			//if the thread was not cancelled get and check the result.
			if(!isCancelled()) {
			try {
				result = this.get();
				if (result!=null) {
					if (result.size()==0) {
						errorAlert("Sorry, no matches were found.\n" +
								"One reason could be that the sequences are too small (<500 bases)\n" +
								"or maybe they are not similar enough.");
						progressBar.setValue(0);
					}
					startButton.setText("Continue");
					startButton.setActionCommand("ok");
					
					//disable choose buttons, to prevent confusing the user
					final String msg = "You need to continue first and open a new match dialog then.";
					buQuery.setEnabled(false);
					buQuery.setToolTipText(msg);
					buTarget.setEnabled(false);
					buTarget.setToolTipText(msg);

					R2cat.dataModelController.setAlignmentsPositonsList(result);
					R2cat.guiController.setVisualisationNeedsUpdate();
					
					MatchDialog.this.validate();
				} else {
					errorAlert("An error happened, no results have been created.");
					progressBar.setValue(0);
				}
			} catch (InterruptedException e) {
				//ignore
				;
			} catch (ExecutionException e) {
				//ignore
				;
			}
		}
		}
	}

	public MatchDialog(Frame parent) {
		super(parent,
				"Find matching between queries (contigs) and a  target (reference genome)",true);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.addWindowListener(this);
		// not used at the moment
		prefs = R2catPrefs.getPreferences();
		init();
//		this.setSize(this.getPreferredSize());
	}

	private void init() {

		GridBagConstraints c = new GridBagConstraints();

		progress = new JTextArea(5, 20);
		progress.setMargin(new Insets(5, 5, 5, 5));
		progress.setEditable(false);
		progress.setWrapStyleWord(true);

		JPanel files = new JPanel();
		files.setBorder(BorderFactory.createTitledBorder("Input Files"));

		tfQuery = new JTextField();

		tfTarget = new JTextField();

		buQuery = new JButton("Set");
		buQuery
				.setToolTipText("Select the query sequence(s) in (multiple) fasta format");
		buQuery.addActionListener(this);

		buTarget = new JButton("Set");
		buTarget
				.setToolTipText("Select the target sequence(s) in (multiple) fasta format");
		buTarget.addActionListener(this);

		this.setQuery(new File(prefs.get("query", "")), true);
		this.setTarget(new File(prefs.get("target", "")), true);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		saveUnmatchedButton = new JButton("Save Unmatched Contigs");
		saveUnmatchedButton.setEnabled(false);
		saveUnmatchedButton.addActionListener(this);

		startButton = new JButton("Start Matching");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);

		JScrollPane logPane = new JScrollPane(progress);
		logPane.setBorder(BorderFactory.createTitledBorder("Progress"));
		logPane.setPreferredSize(new Dimension(600, 400));

		// align files panel
		files.setLayout(new GridBagLayout());
		c.fill = GridBagConstraints.BOTH;

		c.anchor = GridBagConstraints.PAGE_START;
		c.ipadx = 3;
		c.ipady = 3;

		c.gridwidth = 1;
		c.weightx = 0;
		files.add(new JLabel("Query"), c);
		c.weightx = 1;
		files.add(tfQuery, c);
		c.weightx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		files.add(buQuery, c);

		c.gridwidth = 1;
		c.weightx = 0;
		files.add(new JLabel("Target"), c);
		c.weightx = 1;
		files.add(tfTarget, c);
		c.weightx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		files.add(buTarget, c);

		// put files panel on the main window
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.weightx = 0; // do not adyust width and height for the files panel
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		this.add(files, c);

		// add progress textbox
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1; // fill the window with the progress pane if necessary
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		this.add(logPane, c);

		// put the progressbar and the match button in the lower part
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_END;
		c.gridwidth = 2;
		c.weightx = 1; // progress bar as large as possible
		c.weighty = 0; // but only horizontally
		this.add(progressBar, c);
		
		// save unmatched button
		c.weightx = 0; // not broader than necessary
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.EAST;
		this.add(saveUnmatchedButton, c);

		// match button
		c.weightx = 0; // not broader than necessary
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		this.add(startButton, c);
	}

	/**
	 * Displays a file open dialog and returns a selected file. If the dialog
	 * was cancelled, then null is returned
	 * 
	 * @param prevFile
	 *            File that was previously assigned. This is used to determine
	 *            the appropriate directory. If this parameter is null nothing
	 *            happens.
	 * @param dialogTitle
	 *            Gives the dialog a custom title. If null nothing happens.
	 * @return The selected file or directory. null if cancelled.
	 */
	private File chooseFile(File prevFile, String dialogTitle) {
		

		if (prevFile != null && prevFile.getParentFile().exists()) {
			lastDir=prevFile.getParentFile();
		}
		
		return MiscFileUtils.chooseFile(this, dialogTitle, lastDir, true, new CustomFileFilter(".fas,.fna,.fasta", "FASTA File"));
		
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(startButton)) {

			if(e.getActionCommand().equals("start")) {
			// check files
				if(query != null && target != null && query.canRead() && target.canRead()) {
					startButton.setEnabled(false);
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
					matcherTask = new QGramMatcherTask();
					matcherTask.addPropertyChangeListener(this);
					matcherTask.execute();
					} else {
						this.errorAlert("Cannot read query or target!");
					}
			} else if (e.getActionCommand().equals("ok")) {
				this.dispose();
			}
			
		} else if (e.getSource().equals(buQuery)) {
			this.setQuery(this.chooseFile(query,
							"Select query (fasta format)"), false);
			
			
		} else if (e.getSource().equals(buTarget)) {
			this.setTarget(this.chooseFile(target,
					"Select target (fasta format)"), false);
		
		} else if (e.getSource().equals(saveUnmatchedButton)) {
			R2cat.guiController.exportUnmatchedFasta();
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		; // do nothing

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

	/**
	 * Pop up an information message
	 * 
	 * @param info
	 *            Message
	 */
	private void informationAlert(String info) {
		JOptionPane.showMessageDialog(this, info, "Information",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Sets the query to the given file after performing some sanity checks.
	 * Additionally the appropriate textfield is labeled and the path is stored
	 * in the preferences.
	 * 
	 * @param q
	 *            File to set
	 */
	public void setQuery(File q, boolean silent) {
		if (q == null || q.getName().equalsIgnoreCase("")) {
			return;
		}

		if (q.canRead()) {
			this.lastDir = q.getParentFile();
			this.query = q;
			tfQuery.setText(query.getName());
			try {
				tfQuery.setToolTipText(query.getCanonicalPath());
				prefs.put("query", query.getCanonicalPath());
			} catch (IOException e) {
				System.err.println(e);
			}
		} else {
			if (!silent) {
				this.errorAlert("File is not readable: " + q.getName());
			}
		}
	}

	/**
	 * Sets the target to the given file after performing some sanity checks.
	 * Additionally the appropriate textfield is labeled and the path is stored
	 * in the preferences.
	 * 
	 * @param file
	 *            File to set
	 */
	public void setTarget(File t, boolean silent) {
		if (t == null || t.getName().equalsIgnoreCase("")) {
			return;
		}

		if (t.canRead()) {
			this.lastDir = t.getParentFile();
			this.target = t;
			tfTarget.setText(target.getName());
			try {
				tfTarget.setToolTipText(target.getCanonicalPath());

				prefs.put("target", target.getCanonicalPath());
			} catch (IOException e) {
				System.err.println(e);
			}
		} else {
			if (!silent) {
				this.errorAlert("File is not readable: " + t.getName());
			}
		}
	}

	
	@Override
	public void reportProgress(double percentDone, String comment) {
		if(percentDone>=0 && percentDone<=1) {
		progressBar.setValue((int) (percentDone*100.));
		}
		if (comment != null && !comment.isEmpty()) {
			progress.append(comment+"\n");
			progress.setCaretPosition(progress.getDocument().getLength());
		}
	}



	@Override
	public void windowClosing(WindowEvent e) {
		//cancel the matcher task when the window is closed
		// this calls cancel but does not "kill" the thread.
		// TODO: stop the execution of the thread completely
		if(this.matcherTask != null) {
			matcherTask.cancel(true);
		}
	
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
		//I need only the closing event
	}

	@Override
	public void windowClosed(WindowEvent e) {
		//I need only the closing event
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		//I need only the closing event
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		//I need only the closing event
	}

	@Override
	public void windowIconified(WindowEvent e) {
		//I need only the closing event
	}

	@Override
	public void windowOpened(WindowEvent e) {
		//I need only the closing event
	}

}
