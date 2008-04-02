/***************************************************************************
 *   Copyright (C) 2007 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import de.bielefeld.uni.cebitec.cav.ComparativeAssemblyViewer;
import de.bielefeld.uni.cebitec.cav.controller.GuiController;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.CSVParser;

/**
 * @author Peter Husemann
 * 
 */
public class MainMenu extends JMenuBar implements ActionListener, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4355383922470557796L;
	private GuiController guiController;

	public MainMenu(GuiController guiController) {
		super();
		this.guiController = guiController;
		init();
	}


	private void init() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription(
				"File selection");

		JMenuItem open = new JMenuItem("Open File");
		open.setMnemonic(KeyEvent.VK_O);
		open.getAccessibleContext()
				.setAccessibleDescription("File open dialog");
		open.addActionListener(this);
		fileMenu.add(open);

		this.add(fileMenu);

		JMenu optionsMenu = new JMenu("Options");
		optionsMenu.setMnemonic(KeyEvent.VK_O);
		optionsMenu.getAccessibleContext().setAccessibleDescription("Options");

		JCheckBoxMenuItem reverted = new JCheckBoxMenuItem(
				"Unidirectional Alignments", ComparativeAssemblyViewer.preferences.getDisplayUnidirectional());
		reverted.setMnemonic(KeyEvent.VK_U);
		reverted.getAccessibleContext().setAccessibleDescription(
				"Display the alignments reverted if necessary");
		reverted.addActionListener(this);
		optionsMenu.add(reverted);

		JCheckBoxMenuItem offsets = new JCheckBoxMenuItem(
		"Queries with offsets", ComparativeAssemblyViewer.preferences.getDisplayOffsets());
offsets.setMnemonic(KeyEvent.VK_O);
offsets.getAccessibleContext().setAccessibleDescription(
		"Consecutive Queries vs. all queries start at zero");
offsets.addActionListener(this);
optionsMenu.add(offsets);
		

JCheckBoxMenuItem grid = new JCheckBoxMenuItem(
		"Grid", ComparativeAssemblyViewer.preferences.getDisplayGrid());
grid.setMnemonic(KeyEvent.VK_G);
grid.getAccessibleContext().setAccessibleDescription(
		"Display a grid between queries and targets");
grid.addActionListener(this);
optionsMenu.add(grid);

		this.add(optionsMenu);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().matches("Open File")) {
			openFile();
		} else if (e.getActionCommand().matches("Unidirectional Alignments")) {
			guiController.displayUnidirectional();
		} else if (e.getActionCommand().matches("Queries with offsets")) {
			guiController.displayWithOffsets();
	} else if (e.getActionCommand().matches("Grid")) {
		guiController.displayGrid(((JCheckBoxMenuItem)e.getSource()).getState());
	}
		

	}


	/**
	 * 
	 */
	private void openFile() {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.addChoosableFileFilter(new CSVFileFilter());

		// disable all files filter
		// fileChooser.setAcceptAllFileFilterUsed(false);
		File lastFile = new File(ComparativeAssemblyViewer.preferences.getLastFile());
		File lastDir = lastFile.getParentFile();
		if (lastDir != null){
		fileChooser.setCurrentDirectory(lastDir);
		}
		
		int returnVal = fileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			guiController.loadCSVFile(file);
		}
	}

	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub

	}

}
