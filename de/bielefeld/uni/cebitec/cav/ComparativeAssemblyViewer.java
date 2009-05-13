package de.bielefeld.uni.cebitec.cav;

import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;

import de.bielefeld.uni.cebitec.cav.controller.DataModelController;
import de.bielefeld.uni.cebitec.cav.controller.GuiController;
import de.bielefeld.uni.cebitec.cav.utils.CAVPrefs;

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

/**
 * This is the class which is used to start the whole program.
 * 
 * @author Peter Husemann
 */
public class ComparativeAssemblyViewer {
	public static CAVPrefs preferences;

	public static DataModelController dataModelController;

	public static GuiController guiController;

	/**
	 * The usual main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		preferences = new CAVPrefs();
		dataModelController = new DataModelController();
		guiController = new GuiController();

		clearPreferences(args);

		guiController.createMainWindow();
		guiController.showMainWindow();

		if (args.length >= 1 && args[0].endsWith(".r2c")) {
			File initialFile = new File(args[0]);
			try {
				if (!initialFile.exists()) {
					//try the current working directory
					initialFile = new File(System.getProperty("user.dir")
							+ args[0]);
				}

				if (initialFile.canRead()) {
					dataModelController.readAlignmentPositions(initialFile);
					guiController.setVisualisationNeedsUpdate();
				}
			} catch (IOException e) {
				System.err.println("Cannot open file: " + initialFile.getName());
			}
		}

		// testing
		 try {
			 File last = new File(preferences.getLastFile());
			 if(last.exists() && last.canRead()) {
		 dataModelController.readAlignmentPositions(new
		 File(preferences.getLastFile()));
		 guiController.setVisualisationNeedsUpdate();
			 }
		 } catch (IOException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }
//		 guiController.showQuerySortTable(dataModelController.getAlignmentPositionsList());
		//		
	}

	private static void clearPreferences(String[] args) {
		// option to remove all preferences
		if (args.length >= 1 && args[0].matches("clearprefs")) {
			try {
				preferences.getPreferences().clear();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

	public static CAVPrefs getPrefs() {
		return preferences;
	}

}
