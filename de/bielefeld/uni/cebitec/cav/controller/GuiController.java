/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.cav.controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.bielefeld.uni.cebitec.cav.ComparativeAssemblyViewer;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.SequenceOrderTableModel;
import de.bielefeld.uni.cebitec.cav.gui.AlignmentTable;
import de.bielefeld.uni.cebitec.cav.gui.CustomFileFilter;
import de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin;
import de.bielefeld.uni.cebitec.cav.gui.DotPlotVisualisation;
import de.bielefeld.uni.cebitec.cav.gui.DotPlotVisualisationActionListener;
import de.bielefeld.uni.cebitec.cav.gui.MainMenu;
import de.bielefeld.uni.cebitec.cav.gui.MainWindow;
import de.bielefeld.uni.cebitec.cav.gui.MatchDialog;
import de.bielefeld.uni.cebitec.cav.gui.SequenceOrderTable;

public class GuiController {

	private MainWindow mainWindow = null;

	private DotPlotVisualisationActionListener dotPlotVisualisationActionListener = null;

	private MainMenu mainMenu = null;

	// not used at the moment. should store different types of visualisation
	// for a tabbed view
	// private Vector<DataViewPlugin> dataViews;

	private AlignmentTable alignmentTable = null;

	private JFrame tableFrame = null;

	private DotPlotVisualisation dotPlotVisualisation;

	/**
	 * 
	 */
	public GuiController() {
		// not used - see above
		// dataViews = new Vector<DataViewPlugin>();
	}

	public void createMainWindow() {
		mainWindow = new MainWindow(this);
	}

	public void showMainWindow() {
		mainWindow.setVisible(true);
	}

	public DataViewPlugin createDotPlotVisualisation(
			AlignmentPositionsList alignmentPositionsList) {
		dotPlotVisualisation = new DotPlotVisualisation(alignmentPositionsList);

		DotPlotVisualisationActionListener dotPlotVisualisationListener = new DotPlotVisualisationActionListener(
				this, dotPlotVisualisation);

		dotPlotVisualisation
				.addMouseMotionListener(dotPlotVisualisationListener);
		dotPlotVisualisation.addMouseListener(dotPlotVisualisationListener);
		dotPlotVisualisation
				.addMouseWheelListener(dotPlotVisualisationListener);
		dotPlotVisualisation.addKeyListener(dotPlotVisualisationListener);

		// load the previous state from prefs
		dotPlotVisualisation.drawGrid(ComparativeAssemblyViewer.preferences
				.getDisplayGrid());
		dotPlotVisualisation.getAlignmentPositionDisplayerList()
				.showReversedComplements(
						ComparativeAssemblyViewer.preferences
								.getDisplayReverseComplements());

		dotPlotVisualisation.getAlignmentPositionDisplayerList()
				.setDisplayOffsets(
						ComparativeAssemblyViewer.preferences
								.getDisplayOffsets());

		return dotPlotVisualisation;
	}

	public void setVisualisation(DataViewPlugin vis) {
		mainWindow.setVisualisation((DotPlotVisualisation) vis);
		mainWindow.validate();
	}

	/**
	 * Sets that the AlignmentPositionDisplayers need to be regenerated
	 */
	public void setVisualisationNeedsUpdate() {
		if (visualisationInitialized()) {
			dotPlotVisualisation.getAlignmentPositionDisplayerList()
					.setNeedsRegeneration(true);
		}
	}

	public void createAlignmentsPositionTableFrame(
			AlignmentPositionsList alignmentPositionsList) {
		if (alignmentPositionsList != null) {
			tableFrame = new JFrame("Matching positions");
			AlignmentTable at = new AlignmentTable(alignmentPositionsList);
			JScrollPane tp = new JScrollPane(at);
			tableFrame.add(tp);
			
			
			int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
			int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

			tableFrame.setPreferredSize(new Dimension(width/2,height));
			tableFrame.setSize(new Dimension(width/2,height));

			
			tableFrame.pack();
			tableFrame.setLocationByPlatform(true);
		}
	}

	public void showAlignmentsPositionTableFrame() {
		if (tableFrame == null) {
			this
					.createAlignmentsPositionTableFrame(ComparativeAssemblyViewer.dataModelController
							.getAlignmentPositionsList());
		}
		if (tableFrame != null) {
			tableFrame.setVisible(true);
		}
	}

	public void showQuerySortTable(AlignmentPositionsList alignmentPositionsList) {
		if (alignmentPositionsList != null) {
			JFrame querySort = new JFrame("Query order");
			querySort.setLayout(new BorderLayout());
			SequenceOrderTable qso = new SequenceOrderTable(
					alignmentPositionsList);
			SequenceOrderTableModel model = (SequenceOrderTableModel) qso
					.getModel();
			model.setShowComplementColumn(true);
			JScrollPane tp = new JScrollPane(qso);
			querySort.add(tp,BorderLayout.CENTER);

			JPanel controlPanel = new JPanel();
			controlPanel.add(new JLabel("Move one step towards sequence"));
			JButton up = new JButton("start");
			up.setActionCommand("up");
			controlPanel.add(up);
			up.addActionListener(qso);
			JButton down = new JButton("end");
			down.setActionCommand("down");
			controlPanel.add(down);
			down.addActionListener(qso);

			querySort.add(controlPanel,BorderLayout.SOUTH);
			querySort.pack();
			
			int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
			int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

			querySort.setSize(new Dimension(width/3,height));

			
			querySort.setLocationByPlatform(true);
			querySort.setVisible(true);

		}

	}

	public void showMatchDialog() {
		MatchDialog matchDialog = new MatchDialog(mainWindow);
		if (matchDialog != null) {
			matchDialog.pack();
			matchDialog.setLocationByPlatform(true);
			matchDialog.setVisible(true);
		}
	}

	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public void displayReverseComplements(boolean unidirectional) {
		ComparativeAssemblyViewer.preferences
				.setDisplayReverseComplements(unidirectional);

		if (ComparativeAssemblyViewer.dataModelController
				.isAlignmentpositionsListReady()) {

			dotPlotVisualisation.getAlignmentPositionDisplayerList()
					.showReversedComplements(unidirectional);

			dotPlotVisualisation.repaint();
		}
	}

	public void displayGrid(boolean b) {
		ComparativeAssemblyViewer.preferences.setDisplayGrid(b);
		if (ComparativeAssemblyViewer.dataModelController
				.isAlignmentpositionsListReady()) {
			dotPlotVisualisation.drawGrid(b);
			dotPlotVisualisation.repaint();
		}
	}

	public void displayOffsets(boolean displayOffsets) {
		ComparativeAssemblyViewer.preferences.setDisplayOffsets(displayOffsets);
		if (ComparativeAssemblyViewer.dataModelController
				.isAlignmentpositionsListReady()) {

			dotPlotVisualisation.getAlignmentPositionDisplayerList()
					.setDisplayOffsets(displayOffsets);

			dotPlotVisualisation.repaint();

		}
	}

	public void initVisualisation() {
		if (!visualisationInitialized()) {
			if (ComparativeAssemblyViewer.dataModelController
					.isAlignmentpositionsListReady()) {

				DataViewPlugin dotPlotVisualisation = this
						.createDotPlotVisualisation(ComparativeAssemblyViewer.dataModelController
								.getAlignmentPositionsList());

				this.setVisualisation(dotPlotVisualisation);

			}
		}
	}

	/**
	 * Checks if the visualisation was created
	 * 
	 * @return
	 * 
	 */
	public boolean visualisationInitialized() {
		return (dotPlotVisualisation != null);
	}

	public void loadCSVFile() {
		File csv = this.chooseFile(true, ".csv", "comma separaded values");
		if (csv != null) {
			ComparativeAssemblyViewer.dataModelController
					.setAlignmentsPositonsListFromCSV(csv);
			this.setVisualisationNeedsUpdate();
		}
	}

	/**
	 * Shows a dialog to save the actual displayed hits to a file. This one can
	 * then be loaded with the loadProject() method.
	 */
	public void saveProject() {
		if (!ComparativeAssemblyViewer.dataModelController
				.isAlignmentpositionsListReady()) {
			errorAlert("There is nothing to save!");
			return;
		}

		File f = this.chooseFile(false, ".r2c", "r2cat hits file");
		if (f != null) {
			try {
				if (!f.getName().endsWith(".r2c")) {
					f = new File(f.getAbsolutePath() + ".r2c");
				}
				ComparativeAssemblyViewer.dataModelController
						.writeAlignmentPositions(f);
			} catch (IOException e) {
				this.errorAlert("Unable to open file:" + e);
			}
		}
	}

	/**
	 * Shows a dialog to load a project file
	 */
	public void loadProject() {
		File f = this.chooseFile(true, ".r2c", "r2cat hits file");
		if (f != null) {
			try {
				ComparativeAssemblyViewer.dataModelController
						.readAlignmentPositions(f);
				this.setVisualisationNeedsUpdate();
			} catch (IOException e) {
				this.errorAlert("Unable to open file:" + e);
			}
		}

	}

	/**
	 * Gives a dialog to save the contigs in the displayed order and orientation
	 * as fasta files.
	 */
	public void exportAsFasta() {
		if (!ComparativeAssemblyViewer.dataModelController
				.isAlignmentpositionsListReady()) {
			errorAlert("There is nothing to save!");
			return;
		}

		File f = this.chooseFile(false, ".fas", "fasta file");
		if (f != null) {
			this.exportAsFastaFile(f, false);
		} else {
			return;
		}
	}

	private void exportAsFastaFile(File f, boolean ignoreMissingFiles) {
		try {
			if (!f.getName().endsWith(".fas")) {
				f = new File(f.getAbsolutePath() + ".fas");
			}

			// TODO check if all files are existent

			// mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			int contigsWritten = ComparativeAssemblyViewer.dataModelController
					.writeOrderOfContigsFasta(f, ignoreMissingFiles);
			 int totalContigs = ComparativeAssemblyViewer.dataModelController.getAlignmentPositionsList().getQueries().size();
			JOptionPane.showMessageDialog(getMainWindow(),
					"Wrote " + contigsWritten 
					+ (contigsWritten!=totalContigs?(" out of " + totalContigs):"")
					+ " contigs into file:\n"+(f.getAbsolutePath()) ,
					"File written", JOptionPane.INFORMATION_MESSAGE);
			// mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		} catch (IOException e) {
			if (e.getClass() == SequenceNotFoundException.class) {
				
				SequenceNotFoundException seqNotFoundException = (SequenceNotFoundException)e;

				Object[] options = { "Yes", "No, leave out missing sequences", "Abort" };
				int n = JOptionPane.showOptionDialog(this.getMainWindow(),
						e.getMessage()+"\nDo you want to select a file?",
						"Sequence not found", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE, null,
						options,
						options[0]);
				
				switch (n) {
				case JOptionPane.YES_OPTION:
					seqNotFoundException.getDNASequence().setFile(this.chooseFile(true, ".fas", "Fasta file"));
					this.exportAsFastaFile(f, ignoreMissingFiles);
					break;
				case JOptionPane.NO_OPTION:
					this.exportAsFastaFile(f, true);
					break;
				case JOptionPane.CANCEL_OPTION:
					return;

				default:
					return;
				}

			} else {
				this.errorAlert(e.getMessage() + "\nNothing was saved");
			}
		}

	}

	/**
	 * Shows a dialog to select files for opening or saving data
	 * 
	 * @param open
	 *            true means show open dialog; false show save dialog
	 * @param extension
	 *            extension to filter for. null or empty is all
	 * @param description
	 *            description of this kind of files
	 * @return
	 */
	public File chooseFile(boolean open, String extension, String description) {
		JFileChooser fileChooser = new JFileChooser();

		if (extension != null && !extension.isEmpty()) {
			fileChooser.addChoosableFileFilter(new CustomFileFilter(extension,
					description));
		}

		File lastFile = new File(ComparativeAssemblyViewer.preferences
				.getLastFile());
		File lastDir = lastFile.getParentFile();
		if (lastDir != null) {
			fileChooser.setCurrentDirectory(lastDir);
		}

		int returnVal;

		if (open) {
			returnVal = fileChooser.showOpenDialog(mainWindow);
		} else {
			returnVal = fileChooser.showSaveDialog(mainWindow);
		}

		File file = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			ComparativeAssemblyViewer.preferences.setLastFile(file
					.getAbsolutePath());
		}

		return file;
	}

	/**
	 * Pop up an error message
	 * 
	 * @param error
	 *            Message
	 */
	private void errorAlert(String error) {
		JOptionPane.showMessageDialog(mainWindow, error, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

}
