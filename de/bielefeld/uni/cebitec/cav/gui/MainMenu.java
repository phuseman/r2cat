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

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import de.bielefeld.uni.cebitec.cav.ComparativeAssemblyViewer;
import de.bielefeld.uni.cebitec.cav.controller.GuiController;

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

		JMenuItem newMatch = new JMenuItem("Match new");
		newMatch.setMnemonic(KeyEvent.VK_N);
		newMatch.getAccessibleContext().setAccessibleDescription(
				"Match files dialog");
		newMatch.addActionListener(this);
		newMatch.setActionCommand("match_new");
		fileMenu.add(newMatch);

		JMenuItem open = new JMenuItem("Open project");
		open.setMnemonic(KeyEvent.VK_O);
		open.getAccessibleContext()
				.setAccessibleDescription("Opens a previously saved file containing the hits");
		open.setActionCommand("open_project");
		open.addActionListener(this);
		fileMenu.add(open);

		JMenuItem save = new JMenuItem("Save project");
		save.setMnemonic(KeyEvent.VK_O);
		save.getAccessibleContext()
				.setAccessibleDescription("Save the computed hits");
		save.setActionCommand("save_project");
		save.addActionListener(this);
		//save.setEnabled(false);
		fileMenu.add(save);

		
		JMenuItem swift = new JMenuItem("Import SWIFT csv File");
		swift.setMnemonic(KeyEvent.VK_O);
		swift.getAccessibleContext()
				.setAccessibleDescription("Import SWIFT csv File");
		swift.setActionCommand("open_csv");
		swift.addActionListener(this);
		fileMenu.add(swift);

		JMenuItem fastaExport = new JMenuItem("Export contigs as FASTA file");
		fastaExport.setMnemonic(KeyEvent.VK_F);
		fastaExport.getAccessibleContext()
				.setAccessibleDescription("Save the contigs order and orientation in FASTA format");
		fastaExport.setActionCommand("save_fasta");
		fastaExport.addActionListener(this);
		fileMenu.add(fastaExport);

		
		
		JMenuItem exit=new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_E);
		exit.getAccessibleContext()
				.setAccessibleDescription("Quit the program");
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		fileMenu.add(exit);

		this.add(fileMenu);

		JMenu optionsMenu = new JMenu("Options");
		optionsMenu.setMnemonic(KeyEvent.VK_O);
		optionsMenu.getAccessibleContext().setAccessibleDescription("Options");

		JCheckBoxMenuItem reverted = new JCheckBoxMenuItem(
				"Complemented contigs",
				ComparativeAssemblyViewer.preferences
						.getDisplayReverseComplements());
		reverted.setMnemonic(KeyEvent.VK_U);
		reverted.getAccessibleContext().setAccessibleDescription(
				"Display the alignments reverse complemented if necessary");
		reverted.setActionCommand("reverted");
		reverted.addActionListener(this);
		optionsMenu.add(reverted);

		JCheckBoxMenuItem offsets = new JCheckBoxMenuItem(
				"Show queries with offsets",
				ComparativeAssemblyViewer.preferences.getDisplayOffsets());
		offsets.setMnemonic(KeyEvent.VK_O);
		offsets.getAccessibleContext().setAccessibleDescription(
				"Consecutive Queries vs. all queries start at zero");
		offsets.setActionCommand("query_offsets");
		offsets.addActionListener(this);
		optionsMenu.add(offsets);

		JCheckBoxMenuItem grid = new JCheckBoxMenuItem("Show grid",
				ComparativeAssemblyViewer.preferences.getDisplayGrid());
		grid.setMnemonic(KeyEvent.VK_G);
		grid.getAccessibleContext().setAccessibleDescription(
				"Display a grid between queries and targets");
		grid.setActionCommand("grid");
		grid.addActionListener(this);
		optionsMenu.add(grid);

		this.add(optionsMenu);

		JMenu windowMenu = new JMenu("Window");
		windowMenu.setMnemonic(KeyEvent.VK_W);
		windowMenu.getAccessibleContext().setAccessibleDescription(
				"Open windows");

		JMenuItem tableView = new JMenuItem("Show matches as table");
		tableView.setMnemonic(KeyEvent.VK_T);
		tableView.getAccessibleContext().setAccessibleDescription(
				"Display a table with the matches");
		tableView.setActionCommand("show_table");
		tableView.addActionListener(this);
		windowMenu.add(tableView);

		JMenuItem sortQueries = new JMenuItem("Sort queries manually");
		sortQueries.setMnemonic(KeyEvent.VK_Q);
		sortQueries.getAccessibleContext().setAccessibleDescription(
				"Display a table to sort the queries by hand");
		sortQueries.setActionCommand("show_query_table");
		sortQueries.addActionListener(this);
		windowMenu.add(sortQueries);

		
		
		this.add(windowMenu);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().matches("open_csv")) {
			guiController.loadCSVFile();
		} else if (e.getActionCommand().matches("match_new")) {
			guiController.showMatchDialog();
		} else if (e.getActionCommand().matches("open_project")) {
			guiController.loadProject();
		} else if (e.getActionCommand().matches("save_project")) {
			guiController.saveProject();
		} else if (e.getActionCommand().matches("save_fasta")) {
			guiController.exportAsFasta();
		} else if (e.getActionCommand().matches("reverted")) {
			guiController.displayReverseComplements(((JCheckBoxMenuItem) e.getSource())
					.getState());
		} else if (e.getActionCommand().matches("query_offsets")) {
			guiController.displayOffsets(((JCheckBoxMenuItem) e.getSource())
					.getState());
		} else if (e.getActionCommand().matches("grid")) {
			guiController.displayGrid(((JCheckBoxMenuItem) e.getSource())
					.getState());
		} else if (e.getActionCommand().matches("show_table")) {
			ComparativeAssemblyViewer.guiController.showAlignmentsPositionTableFrame();
		} else if (e.getActionCommand().matches("show_query_table")) {
			ComparativeAssemblyViewer.guiController.showQuerySortTable(ComparativeAssemblyViewer.dataModelController.getAlignmentPositionsList());
		} else if (e.getActionCommand().matches("exit")) {
			System.exit(0);
		} 
		

	}


	public void itemStateChanged(ItemEvent e) {
		; // do nothing
	}

}
