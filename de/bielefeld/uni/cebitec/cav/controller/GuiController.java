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

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.gui.AlignmentTable;
import de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin;
import de.bielefeld.uni.cebitec.cav.gui.DotPlotVisualisation;
import de.bielefeld.uni.cebitec.cav.gui.MainMenu;
import de.bielefeld.uni.cebitec.cav.gui.MainWindow;
import de.bielefeld.uni.cebitec.cav.gui.MainWindowActionListener;
import de.bielefeld.uni.cebitec.cav.utils.SwiftExternal;

public class GuiController {

	private MainWindow mainWindow;

	private MainWindowActionListener mainWindowActionListener;

	private MainMenu mainMenu;

	private Vector<DataViewPlugin> dataViews;

	private AlignmentTable alignmentTable;

	private JFrame tableFrame;

	/**
	 * 
	 */
	public GuiController() {
		dataViews = new Vector<DataViewPlugin>();
	}

	public void createSwiftCall() {
		//testing
		SwiftExternal s = new SwiftExternal();		
	}

	public void createMainWindow() {
		mainWindow = new MainWindow();		
	}

	public DataViewPlugin createDotPlotVisualisation(AlignmentPositionsList alignmentPositionsList) {
		DotPlotVisualisation dotPlotVisualisation = new DotPlotVisualisation(alignmentPositionsList);
		return dotPlotVisualisation;
	}

	public void setVisualisation(DataViewPlugin vis) {
		mainWindow.setVisualisation( (DotPlotVisualisation) vis);
	}

	public void createTableFrame(AlignmentPositionsList alignmentPositionsList) {
		tableFrame = new JFrame();
		AlignmentTable at = new AlignmentTable(alignmentPositionsList);
		JScrollPane tp = new JScrollPane(at);
		tableFrame.add(tp);
		tableFrame.pack();
		tableFrame.setLocationByPlatform(true);
		tableFrame.setVisible(true);
	}

	public void showMainWindow() {
		mainWindow.setVisible(true);
		
	}

}
