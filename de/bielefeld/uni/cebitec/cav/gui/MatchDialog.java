/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
 *   phuseman ät cebitec.uni-bielefeld.de                                     *
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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import de.bielefeld.uni.cebitec.cav.ComparativeAssemblyViewer;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;
import de.bielefeld.uni.cebitec.cav.qgram.QGramFilter;
import de.bielefeld.uni.cebitec.cav.qgram.QGramIndex;
import de.bielefeld.uni.cebitec.cav.utils.CAVPrefs;
import de.bielefeld.uni.cebitec.cav.utils.Timer;

/**
 * @author phuseman
 * 
 */
public class MatchDialog extends JDialog implements ActionListener,
		PropertyChangeListener {

	private Preferences prefs;

	private JProgressBar progressBar;
	private JButton startButton;
	private JTextArea progress;
	private QGramMatcherTask matcherTask;

	private JTextField tfQuery;

	private JTextField tfTarget;

	private JButton buQuery;

	private JButton buTarget;

	private File query;

	private File target;
	
	private File lastDir;

	private AlignmentPositionsList result;

	class QGramMatcherTask extends SwingWorker<AlignmentPositionsList, String> {
		@Override
		protected AlignmentPositionsList doInBackground() {
			progressBar.setValue(0);
			progressBar.setIndeterminate(true);
			progress.setText("");

			Timer t = Timer.getInstance();
			t.startTimer();

			try {
			progress.append("Opening target file " + target.getName() + " (" + target.length()+ ")");
			t.startTimer();
			FastaFileReader targetFasta = new FastaFileReader(target);
			targetFasta.scanContents(true);
			progress.append(" ..."+t.stopTimer()+"\n");

			
			progress.append("Opening query file"+ query.getName() + " (" + query.length()+ ")");
			t.startTimer();
			FastaFileReader queryFasta = new FastaFileReader(query);
			queryFasta.scanContents(true);
			progress.append(" ..."+t.stopTimer()+"\n");

			
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
			progress.append(" Generating q-Gram Index took: "+t.stopTimer()+"\n");

			System.gc();

			
			progressBar.setIndeterminate(false);

			progress.append("Matching:\n");
			t.startTimer();
			QGramFilter qf = new QGramFilter(qi, queryFasta);
			qf.register(MatchDialog.this);
			result = qf.match();
			progress.append("Matching took: "+t.stopTimer()+"\n");

			progress.append("Total time: "+t.stopTimer()+"\n");
			progressBar.setValue(100);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			startButton.setEnabled(true);
			setCursor(null); // turn off the wait cursor
			progress.append("Done!\n");
			progress.setCaretPosition(progress.getDocument().getLength());
			progressBar.setIndeterminate(false);

			return result;
		}

		public void done() {
			try {
				result = this.get();
				if (result!=null) {
					if (result.size()==0) {
						errorAlert("Sorry, no matches were found. Change the files and try again");
						progressBar.setValue(0);
					}
					startButton.setText("Continue");
					startButton.setActionCommand("ok");

					ComparativeAssemblyViewer.dataModelController.setAlignmentsPositonsList(result);
					ComparativeAssemblyViewer.guiController.setVisualisationNeedsUpdate();


					
					MatchDialog.this.validate();
				} else {
					errorAlert("An error happened, change the files and try again");
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

	public MatchDialog(Frame parent) {
		super(parent,
				"Find matching between queries (contigs) and a  target (reference genome)",true);
		this.setLayout(new BorderLayout());

		// not used at the moment
		prefs = CAVPrefs.getPreferences();
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
	 *            File that was previously assinged. This is used to determine
	 *            the apropriate directory. If this parameter is null nothing
	 *            happens.
	 * @param dialogTitle
	 *            Gives the dialog a custom title. If null nothing happens.
	 * @return The selected file or directory. null if cancelled.
	 */
	private File chooseFile(File prevFile, String dialogTitle) {
		JFileChooser fileChooser = new JFileChooser();

		if (dialogTitle != null) {
			fileChooser.setDialogTitle(dialogTitle);
		}

		
		if (prevFile != null && prevFile.getParentFile().exists()) {
			fileChooser.setCurrentDirectory(prevFile.getParentFile());
		}

		if (lastDir != null && lastDir.getParentFile().exists()) {
			fileChooser.setCurrentDirectory(lastDir);
		}

		
		// disable all files filter
		// fileChooser.setAcceptAllFileFilterUsed(false);

		int returnVal = fileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
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
			this
					.setQuery(this.chooseFile(query,
							"Select query (fasta format)"), false);
			
			
		} else if (e.getSource().equals(buTarget)) {
			this.setTarget(this.chooseFile(target,
					"Select target (fasta format)"), false);
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
	 * Sets the query to the given file after performing some sanity checks.
	 * Additionally the appropriate textfield is labeled and the path is stored
	 * in the preferences.
	 * 
	 * @param file
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

	
	/**
	 * Set the achieved progress with optional message
	 * @param message
	 * @param percentDone
	 */
	public void setProgress( double percentDone, String message) {
		progressBar.setValue((int) (percentDone*100.));
			if (message != null && !message.equals("")) {
				progress.append(message+"\n");
				progress.setCaretPosition(progress.getDocument().getLength());
			}
		}

}
