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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.utils.CAVPrefs;

/**
 * @author phuseman
 * 
 */
public class MatchDialog extends JFrame implements ActionListener,
		PropertyChangeListener {

	private Preferences prefs;

	private JProgressBar progressBar;
	private JButton startButton;
	private JTextArea progress;
	private QGramMatcherTask matcherTask;

	private File query;
	private File target;

	private JTextField tfQuery;

	private JTextField tfTarget;

	private JButton buQuery;

	private JButton buTarget;

	class QGramMatcherTask extends SwingWorker<AlignmentPositionsList, String> {
		@Override
		protected AlignmentPositionsList doInBackground() {
			System.out.println("Thread");
			progressBar.setIndeterminate(true);
			try {
				progress.append("Generating q-Gram Index\n");
				Thread.sleep(1000);
				progressBar.setValue(25);

				progress.append("Second Pass\n");
				Thread.sleep(1000);
				progressBar.setValue(50);

				progress.append("Matching Foreward Strand\n");
				Thread.sleep(1000);
				progressBar.setValue(75);

				progress.append("Matching Backward Strand\n");
				Thread.sleep(1000);
				progressBar.setValue(100);

			} catch (InterruptedException e) {
			}
			return null;
		}

		public void done() {
			Toolkit.getDefaultToolkit().beep();
			startButton.setEnabled(true);
			setCursor(null); // turn off the wait cursor
			progress.append("Done!\n");
			progressBar.setIndeterminate(false);
		}
	}

	public MatchDialog() {
		super("Find matching between queries (contigs) and a  target (reference genome)");
		this.setLayout(new BorderLayout());

		// not used at the moment
		prefs = CAVPrefs.getPreferences();

		init();

		this.setSize(this.getPreferredSize());
		this.pack();
		this.setLocationByPlatform(true);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		startButton = new JButton("Start Matching");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);

		JScrollPane logPane = new JScrollPane(progress);
		logPane.setBorder(BorderFactory.createTitledBorder("Progress"));
		logPane.setPreferredSize(new Dimension(400, 200));

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
		if (e.getActionCommand().equals("start")) {
			System.out.println("Go!");
			startButton.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			matcherTask = new QGramMatcherTask();
			matcherTask.addPropertyChangeListener(this);
			matcherTask.execute();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	public static void main(String args[]) {
		MatchDialog d = new MatchDialog();
	}

}
