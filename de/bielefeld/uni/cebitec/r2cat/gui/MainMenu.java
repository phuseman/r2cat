/***************************************************************************
 *   Copyright (C) 2007 by Peter Husemann                                  *
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import de.bielefeld.uni.cebitec.r2cat.R2cat;

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

		fileMenu.addSeparator();
		
		
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
		
		fileMenu.addSeparator();
                
                JMenuItem export = new JMenuItem( de.bielefeld.uni.cebitec.r2cat.UnimogExport.Constants.MENUPOINT);
                export.addActionListener(this);
                fileMenu.add(export);


		//outdated... no need to import swift files
		//TODO: probably change this to import various formats like blast, mummer, blat and so on...
//		JMenuItem swift = new JMenuItem("Import SWIFT csv File");
//		swift.setMnemonic(KeyEvent.VK_O);
//		swift.getAccessibleContext()
//				.setAccessibleDescription("Import SWIFT csv File");
//		swift.setActionCommand("open_csv");
//		swift.addActionListener(this);
//		fileMenu.add(swift);

		JMenuItem contigOrderExport = new JMenuItem("Export contigs order (text)");
		contigOrderExport.setMnemonic(KeyEvent.VK_T);
		contigOrderExport.getAccessibleContext()
				.setAccessibleDescription("Save the contigs in the displayed order and orientation in FASTA format");
		contigOrderExport.setActionCommand("save_order");
		contigOrderExport.addActionListener(this);
		fileMenu.add(contigOrderExport);

		JMenuItem fastaExport = new JMenuItem("Export contigs order (FASTA)");
		fastaExport.setMnemonic(KeyEvent.VK_F);
		fastaExport.getAccessibleContext()
				.setAccessibleDescription("Save the contigs in the displayed order and orientation in FASTA format");
		fastaExport.setActionCommand("save_fasta");
		fastaExport.addActionListener(this);
		fileMenu.add(fastaExport);
		
		JMenuItem unmatchedFastaExport = new JMenuItem("Export unmatched contigs (FASTA)");
		unmatchedFastaExport.setMnemonic(KeyEvent.VK_M);
		unmatchedFastaExport.getAccessibleContext()
				.setAccessibleDescription("Save all unmatched contigs in one file in FASTA format");
		unmatchedFastaExport.setActionCommand("save_unmatched");
		unmatchedFastaExport.addActionListener(this);
		fileMenu.add(unmatchedFastaExport);

		
		JMenuItem imageExport = new JMenuItem("Export image");
		imageExport.getAccessibleContext()
				.setAccessibleDescription("Exports the viewport as image (vector or bitmap)");
		imageExport.setActionCommand("export_image");
		imageExport.addActionListener(this);
		fileMenu.add(imageExport);

		
		fileMenu.addSeparator();
		
		JMenuItem primer = new JMenuItem("Generate Primer");
		primer.getAccessibleContext()
				.setAccessibleDescription("Generates Primer for adjacent contigs");
		primer.setActionCommand("generate_primer");
		primer.addActionListener(this);
		fileMenu.add(primer);

		
		fileMenu.addSeparator();

		
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
				R2cat.preferences
						.getDisplayReverseComplements());
		reverted.setMnemonic(KeyEvent.VK_U);
		reverted.getAccessibleContext().setAccessibleDescription(
				"Display the alignments reverse complemented if necessary");
		reverted.setActionCommand("reverted");
		reverted.addActionListener(this);
		optionsMenu.add(reverted);

		JCheckBoxMenuItem offsets = new JCheckBoxMenuItem(
				"Show queries with offsets",
				R2cat.preferences.getDisplayOffsets());
		offsets.setMnemonic(KeyEvent.VK_O);
		offsets.getAccessibleContext().setAccessibleDescription(
				"Consecutive Queries vs. all queries start at zero");
		offsets.setActionCommand("query_offsets");
		offsets.addActionListener(this);
		optionsMenu.add(offsets);

		JCheckBoxMenuItem grid = new JCheckBoxMenuItem("Show grid",
				R2cat.preferences.getDisplayGrid());
		grid.setMnemonic(KeyEvent.VK_G);
		grid.getAccessibleContext().setAccessibleDescription(
				"Display a grid between queries and targets");
		grid.setActionCommand("grid");
		grid.addActionListener(this);
		optionsMenu.add(grid);
		
		optionsMenu.addSeparator();

		JMenuItem sortTargets = new JMenuItem("Sort targets by size");
		sortTargets.setMnemonic(KeyEvent.VK_T);
		sortTargets.getAccessibleContext().setAccessibleDescription(
				"Sort the targets by their size");
		sortTargets.setActionCommand("sort_targets");
		sortTargets.addActionListener(this);
		optionsMenu.add(sortTargets);

		JMenuItem sortQueriesAuto = new JMenuItem("Sort queries");
		sortQueriesAuto.setMnemonic(KeyEvent.VK_Q);
		sortQueriesAuto.getAccessibleContext().setAccessibleDescription(
				"Sort the queries based on the most matches on their reference");
		sortQueriesAuto.setActionCommand("sort_queries");
		sortQueriesAuto.addActionListener(this);
		optionsMenu.add(sortQueriesAuto);


		this.add(optionsMenu);

		JMenu windowMenu = new JMenu("Window");
		windowMenu.setMnemonic(KeyEvent.VK_W);
		windowMenu.getAccessibleContext().setAccessibleDescription(
				"Open windows");

		JMenuItem tableView = new JMenuItem("Show matches");
		tableView.setMnemonic(KeyEvent.VK_T);
		tableView.getAccessibleContext().setAccessibleDescription(
				"Display a table with the matches");
		tableView.setActionCommand("show_table");
		tableView.addActionListener(this);
		windowMenu.add(tableView);

		JMenuItem sortQueries = new JMenuItem("Show queries/contigs");
		sortQueries.setMnemonic(KeyEvent.VK_Q);
		sortQueries.getAccessibleContext().setAccessibleDescription(
				"Display a table to sort the queries manually");
		sortQueries.setActionCommand("show_query_table");
		sortQueries.addActionListener(this);
		windowMenu.add(sortQueries);


		this.add(windowMenu);
		
		
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		helpMenu.getAccessibleContext().setAccessibleDescription(
				"Get help");
		
		JMenuItem help = new JMenuItem("Show help");
		help.setMnemonic(KeyEvent.VK_E);
		help.getAccessibleContext().setAccessibleDescription(
				"Display a window with helping contents");
		help.setActionCommand("show_help");
		help.addActionListener(this);
		helpMenu.add(help);

		JMenuItem about = new JMenuItem("About");
		about.setMnemonic(KeyEvent.VK_A);
		about.getAccessibleContext().setAccessibleDescription(
				"Display who wrote this program");
		about.setActionCommand("show_about");
		about.addActionListener(this);
		helpMenu.add(about);

		
//FIXME improve the content of the help and uncomment this
		this.add(helpMenu);

		
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
		} else if (e.getActionCommand().matches("save_order")) {
			guiController.exportOrderText();
		} else if (e.getActionCommand().matches("generate_primer")) {
			guiController.showGeneratePrimerFrame(R2cat.dataModelController.getMatchesList());
		} else if (e.getActionCommand().matches("save_fasta")) {
			guiController.exportOrderFasta();
		} else if (e.getActionCommand().matches("save_unmatched")) {
			guiController.exportUnmatchedFasta();
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
			R2cat.guiController.showAlignmentsPositionTableFrame();
		} else if (e.getActionCommand().matches("show_query_table")) {
			R2cat.guiController.showQuerySortTable(R2cat.dataModelController.getMatchesList());
                } else if (e.getActionCommand().matches("sort_queries")) {
                        R2cat.guiController.sortContigs();
                } else if (e.getActionCommand().matches("sort_targets")) {
                        R2cat.guiController.sortTargetsBySize();
                } else if (e.getActionCommand().matches("show_help")) {
                        R2cat.guiController.showHelpFrame();
                } else if (e.getActionCommand().matches("show_about")) {
                        R2cat.guiController.showAbout();
                } else if (e.getActionCommand().matches("exit")) {
                        System.exit(0);
                } else if (e.getActionCommand().matches("export_image")) {
                        guiController.exportMatchesAsImage();
                } else if (e.getActionCommand().equals( de.bielefeld.uni.cebitec.r2cat.UnimogExport.Constants.MENUPOINT)){
                        new  de.bielefeld.uni.cebitec.r2cat.UnimogExport.ExportController();
                }
		
		
	}


	public void itemStateChanged(ItemEvent e) {
		; // do nothing
	}

}
