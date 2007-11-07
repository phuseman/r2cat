package de.bielefeld.uni.cebitec.cav;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.CSVParser;
import de.bielefeld.uni.cebitec.cav.gui.AlignmentTable;
import de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin;
import de.bielefeld.uni.cebitec.cav.gui.MainWindow;
import de.bielefeld.uni.cebitec.cav.utils.CAVPrefs;
import de.bielefeld.uni.cebitec.cav.utils.SwiftExternal;

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

/**
 * This is the class which is used to start the whole program.
 * 
 * @author Peter Husemann
 */
public class ComparativeAssemblyViewer {
	public static CAVPrefs preferences;

	/**
	 * The usual main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		preferences = new CAVPrefs();
		
		
		SwiftExternal s = new SwiftExternal();
		
		
		
		CSVParser csvParser = new CSVParser(new File(preferences.getLastFile()));
		AlignmentPositionsList apl = csvParser.parse();
		



		MainWindow main = new MainWindow();
		DataViewPlugin view = new DataViewPlugin(apl);
		main.setVisualisation(view);

		// use the preferences: with offsets?
		if (ComparativeAssemblyViewer.preferences.getDisplayOffsets()) {
			apl.generateStatistics(); // this sets the center of masses for each query
			apl.addOffsets();
		}
		
		JFrame tframe = new JFrame();
		AlignmentTable at = new AlignmentTable(apl);
		JScrollPane tp = new JScrollPane(at);
		tframe.add(tp);
		tframe.pack();
		tframe.setVisible(true);

		main.setVisible(true);
	}
	
	public static CAVPrefs getPrefs() {
		return preferences;
	}

}
