/***************************************************************************
 *   Copyright (C) 07.11.2007 by Peter Husemann                                  *
 *   phuseman@cebitec.uni-bielefeld.de                                     *
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

package de.bielefeld.uni.cebitec.cav.utils;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.bielefeld.uni.cebitec.cav.ComparativeAssemblyViewer;

/**
 * @author Peter Husemann
 * 
 */
public class SwiftExternal extends JFrame implements ActionListener,
		KeyListener, Observer {
	private Process swiftProcess;

	private Preferences prefs;

	private File swiftExecutable;

	private File query;

	private File target;

	private File output;

	private JTextField tfSwiftExecutable;

	private JButton buSwiftExecutable;

	private JTextField tfQuery;

	private JButton buQuery;

	private JTextField tfTarget;

	private JButton buTarget;

	private JTextField tfOutput;

	private JButton buOutput;

	private JButton run;

	private JTextArea log;

	private boolean running;

	/**
	 * @param swiftProcess
	 */
	public SwiftExternal() {
		super();
		prefs = CAVPrefs.getPreferences();
		init();

		this.setSize(this.getPreferredSize());
		this.pack();
		this.setLocationByPlatform(true);
		this.setVisible(true);

	}

	/**
	 * Initialisations of the gui. Buttons and so on.
	 */
	private void init() {

		GridBagConstraints c = new GridBagConstraints();

		JPanel files = new JPanel();
		files.setBorder(BorderFactory.createTitledBorder("Paths and Files"));

		tfSwiftExecutable = new JTextField();
		tfSwiftExecutable.addKeyListener(this);

		tfQuery = new JTextField();
		tfQuery.addKeyListener(this);

		tfTarget = new JTextField();
		tfTarget.addKeyListener(this);

		tfOutput = new JTextField();
		tfOutput.addKeyListener(this);

		buSwiftExecutable = new JButton("Set");
		buSwiftExecutable.addActionListener(this);

		buQuery = new JButton("Set");
		buQuery.addActionListener(this);

		buTarget = new JButton("Set");
		buTarget.addActionListener(this);

		buOutput = new JButton("Set");
		buOutput.addActionListener(this);

		// initialize with prefs if possible
		this.setSwiftExecutable(new File(prefs.get("swiftExecutable", "")));
		this.setQuery(new File(prefs.get("query", "")));
		this.setTarget(new File(prefs.get("target", "")));
		this.setOutput(new File(prefs.get("output", "")));

		run = new JButton("Run");
		run.addActionListener(this);

		files.setLayout(new GridBagLayout());
		c.fill = GridBagConstraints.BOTH;

		c.anchor = GridBagConstraints.PAGE_START;
		c.ipadx = 3;
		c.ipady = 3;

		c.weightx = 0;
		files.add(new JLabel("Swift Executable"), c);
		c.weightx = 1;
		files.add(tfSwiftExecutable, c);
		c.weightx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		files.add(buSwiftExecutable, c);

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

		c.gridwidth = 1;
		c.weightx = 0;
		files.add(new JLabel("Output"), c);
		c.weightx = 1;
		files.add(tfOutput, c);
		c.weightx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		files.add(buOutput, c);

		this.setLayout(new GridBagLayout());

		// reset the constraints
		c = new GridBagConstraints();
		c.weightx = 5;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;

		this.add(files, c);
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		this.add(run, c);

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.gridwidth = GridBagConstraints.RELATIVE;

		log = new JTextArea();
		log.setLineWrap(true);

		JScrollPane logPane = new JScrollPane(log);
		logPane.setBorder(BorderFactory.createTitledBorder("log"));
		logPane.setPreferredSize(new Dimension(200, 100));

		this.add(logPane, c);
	}

	private File chooseFile() {
		JFileChooser fileChooser = new JFileChooser();

		// disable all files filter
		// fileChooser.setAcceptAllFileFilterUsed(false);

		int returnVal = fileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}

	public void setSwiftExecutable(File exec) {
		if (exec == null || exec.getName().equalsIgnoreCase("")) {
			return;
		}
		if (exec.canRead()) {
			swiftExecutable = exec;
			tfSwiftExecutable.setText(swiftExecutable.getName());
			try {
				prefs
						.put("swiftExecutable", swiftExecutable
								.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this, "Datei ist nicht lesbar: "
					+ exec.getName(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void setQuery(File q) {
		if (q == null || q.getName().equalsIgnoreCase("")) {
			return;
		}

		if (q.canRead()) {
			this.query = q;
			tfQuery.setText(query.getName());
			try {
				prefs.put("query", query.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this, "Datei ist nicht lesbar: "
					+ q.getName(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void setTarget(File t) {
		if (t == null || t.getName().equalsIgnoreCase("")) {
			return;
		}

		if (t.canRead()) {
			this.target = t;
			tfTarget.setText(target.getName());
			try {
				prefs.put("target", target.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this, "Datei ist nicht lesbar: "
					+ t.getName(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void setOutput(File o) {
		if (o == null || o.getName().equalsIgnoreCase("")) {
			return;
		}
		if (!o.exists() || o.canWrite()) {
			this.output = o;
			tfOutput.setText(output.getName());
			try {
				prefs.put("output", output.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this, "Datei ist nicht schreibbar: "
					+ o.getName(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void errorAlert(String error) {
		JOptionPane.showMessageDialog(this, error, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private void log(String txt) {
		this.log.append(txt);
	}

	// TODO fix!!
	// JTextArea is only capable of one global layout.
	// maybe use JTextPane
	private void logBold(String txt) {
		Font current = log.getFont();
		Font bold = current.deriveFont(Font.BOLD);
		log.setFont(bold);
		this.log.append(txt);
		log.setFont(current);
	}

	public boolean excecuteSwift() {
		String errorMsg = "";
		boolean error = false;

		// test for errors
		if (swiftExecutable == null || !swiftExecutable.canRead()) {
			error = true;
			errorMsg += "Swift not set or found\n";
		}

		// TODO add some more checks

		if (error) {
			errorAlert(errorMsg);
			return false;
		}

		try {
			String swift = swiftExecutable.getCanonicalPath();
			String swift_db = "database_" + this.target.getName();
			String swift_query = "query_" + this.query.getName();
			double swift_maxerror = 0.08;
			int swift_minlen = 500;
			int swift_gramlen = 11;

			// the directory where the swift databases are stored
			// should be the one where the result is stored.
			File outputdir = this.output.getParentFile();

			String command_queryindex_createdb = swift + " create "
					+ swift_query + " " + this.query.getCanonicalPath();
			String command_queryindex_createindex = swift + " index "
					+ swift_query + " --gram " + swift_gramlen;

			String command_dbindex_createdb = swift + " create " + swift_db
					+ " " + this.target.getCanonicalPath();
			String command_dbindex_createindex = swift + " index " + swift_db
					+ " --gram " + swift_gramlen;

			String command_doMatching = swift + " query --maxerror "
					+ swift_maxerror + " --minlen " + swift_minlen + " --gram "
					+ swift_gramlen + " " + swift_db + " " + swift_query
					+ " --output " + this.output.getCanonicalPath()
					+ " --compassemb";

			// run the commands
			SwiftExecutor swiftExec = new SwiftExecutor(this);
			swiftExec.setOutputDir(outputdir);

//			swiftExec.addCommand(command_queryindex_createdb);
//			swiftExec.addCommand(command_queryindex_createindex);
//			swiftExec.addCommand(command_dbindex_createdb);
//			swiftExec.addCommand(command_dbindex_createindex);
			swiftExec.addCommand(command_doMatching);

			Thread t = new Thread(swiftExec);
			t.run();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(buSwiftExecutable)) {
			this.setSwiftExecutable(this.chooseFile());
		} else if (e.getSource().equals(buQuery)) {
			this.setQuery(this.chooseFile());
		} else if (e.getSource().equals(buTarget)) {
			this.setTarget(this.chooseFile());
		} else if (e.getSource().equals(buOutput)) {
			this.setOutput(this.chooseFile());
		} else if (e.getSource().equals(run)) {
			this.excecuteSwift();
		}

	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void update(Observable o, Object arg) {
		log.append((String) arg);
	}

}
