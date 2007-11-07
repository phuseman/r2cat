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

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.bielefeld.uni.cebitec.cav.ComparativeAssemblyViewer;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.CSVParser;
import de.bielefeld.uni.cebitec.cav.gui.CSVFileFilter;
import de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin;

/**
 * @author Peter Husemann
 * 
 */
public class SwiftExternal extends JFrame implements ActionListener,
		KeyListener {
	private Process swiftProcess;

	private File swiftExecutable;

	private boolean swiftExecutableOk = false;

	private JTextField tfSwiftExecutable;

	private JButton buSwiftExecutable;

	private JTextField tfQuery;

	private JButton buQuery;

	private JTextField tfTarget;

	private JButton buTarget;

	private JTextField tfOutput;

	private JButton buOutput;

	private JButton run;

	/**
	 * @param swiftProcess
	 */
	public SwiftExternal() {
		super();
		init();
		this.setSize(400, 200);
		this.pack();
		this.setVisible(true);
	}

	private void init() {

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

		swiftExecutable = new File(ComparativeAssemblyViewer.preferences
				.getSwiftExecutable());
		swiftExecutableOk = swiftExecutable.canExecute();

		if (swiftExecutableOk) {
			tfSwiftExecutable.setText(swiftExecutable.getName());
		}

		
		run = new JButton("Run");
		run.addActionListener(this);
		
		this.setLayout(new GridLayout(4, 2, 10, 10));

		this.add(tfSwiftExecutable);
		this.add(buSwiftExecutable);
		this.add(tfQuery);
		this.add(buQuery);
		this.add(tfTarget);
		this.add(buTarget);
		this.add(tfOutput);
		this.add(buOutput);
		this.add(run);

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

		if (exec.canExecute()) {
			swiftExecutable = exec;
			swiftExecutableOk = swiftExecutable.canExecute();
			tfSwiftExecutable.setText(swiftExecutable.getName());
			try {
				ComparativeAssemblyViewer.preferences
						.setSwiftExecutable(swiftExecutable.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this, "Datei nicht ausführbar",
					"Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	public boolean excecuteSwift(File query, File target)  {
		try {
			swiftProcess = Runtime.getRuntime().exec(swiftExecutable.getCanonicalPath());


		BufferedReader in = new BufferedReader(new InputStreamReader(
				swiftProcess.getInputStream()));
		
	    for ( String s; (s = in.readLine()) != null; )
	        System.out.println( s );

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(buSwiftExecutable)) {
			this.setSwiftExecutable(this.chooseFile());
		} else if (e.getSource().equals(run)) {
			excecuteSwift(null, null);
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

}
