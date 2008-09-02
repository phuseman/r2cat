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

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import de.bielefeld.uni.cebitec.cav.ComparativeAssemblyViewer;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.gui.AlignmentTable;
import de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin;
import de.bielefeld.uni.cebitec.cav.gui.DotPlotVisualisation;
import de.bielefeld.uni.cebitec.cav.gui.DotPlotVisualisationActionListener;
import de.bielefeld.uni.cebitec.cav.gui.MainMenu;
import de.bielefeld.uni.cebitec.cav.gui.MainWindow;
import de.bielefeld.uni.cebitec.cav.gui.MatchDialog;

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
		dotPlotVisualisation.drawGrid(ComparativeAssemblyViewer.preferences
				.getDisplayGrid());
		return dotPlotVisualisation;
	}

	public void setVisualisation(DataViewPlugin vis) {
		mainWindow.setVisualisation((DotPlotVisualisation) vis);
		mainWindow.validate();
	}

	public void createTableFrame(AlignmentPositionsList alignmentPositionsList) {
		if (alignmentPositionsList != null) {
			tableFrame = new JFrame();
			AlignmentTable at = new AlignmentTable(alignmentPositionsList);
			JScrollPane tp = new JScrollPane(at);
			tableFrame.add(tp);
			tableFrame.pack();
			tableFrame.setLocationByPlatform(true);
		}
	}

	public void showTableFrame() {
		if (tableFrame == null) {
			this.createTableFrame(ComparativeAssemblyViewer.dataModelController
					.getAlignmentPositionsList());
		}
		if (tableFrame != null) {
			tableFrame.setVisible(true);
		}
	}

	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public void loadCSVFile(File file) {
		ComparativeAssemblyViewer.preferences.setLastFile(file
				.getAbsolutePath());
		ComparativeAssemblyViewer.dataModelController
				.setAlignmentsPositonsListFromCSV(file);
	}

	public void displayWithOffsets() {
		//TODO when no apl's are displayed and this method is called it shows the 
		//wrong value after something is loaded...
		if (ComparativeAssemblyViewer.dataModelController
				.getAlignmentPositionsList() != null) {
			dotPlotVisualisation.getAlignmentPositionDisplayerList()
					.toggleOffsets();
			dotPlotVisualisation.repaint();
		}
	}

	public void displayUnidirectional() {
		//TODO when no apl's are displayed and this method is called it shows the 
		//wrong value after something is loaded...
		if (ComparativeAssemblyViewer.dataModelController
				.getAlignmentPositionsList() != null) {

			dotPlotVisualisation.getAlignmentPositionDisplayerList()
					.switchReversed();
			dotPlotVisualisation.repaint();
		}
	}

	public void displayGrid(boolean b) {
		//TODO when no apl's are displayed and this method is called it shows the 
		//wrong value after something is loaded...
		if (ComparativeAssemblyViewer.dataModelController
				.getAlignmentPositionsList() != null) {

			dotPlotVisualisation.drawGrid(b);
			dotPlotVisualisation.repaint();
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

	public void initVisualisation() {
		if (!visualisationInitialized()) {
			if (ComparativeAssemblyViewer.dataModelController
					.getAlignmentPositionsList() != null) {

				DataViewPlugin dotPlotVisualisation = this
						.createDotPlotVisualisation(ComparativeAssemblyViewer.dataModelController
								.getAlignmentPositionsList());

				this.setVisualisation(dotPlotVisualisation);

			}
		}
	}

	public boolean visualisationInitialized() {
		return (dotPlotVisualisation != null);

	}

}
