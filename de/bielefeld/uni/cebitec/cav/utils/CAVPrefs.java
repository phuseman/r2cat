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

package de.bielefeld.uni.cebitec.cav.utils;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * This class should give access to various preferences. There are some
 * predefined functions (get* set*) for an unambigeuos access. But it is also
 * possible to get the sun Preferences object (getPreferences) for custom prefs.
 * 
 * @author Peter Husemann
 * 
 */

public class CAVPrefs {

	private static Preferences preferences = null;

	/**
	 * Initialises the java.util.prefs.Preferences object, so that it uses the default place to store the prefs.
	 * On Windows this is the registry, whereas linux or unix use the ~/.java directory.
	 * @see {@link Preferences}
	 */
	public CAVPrefs() {
		if (preferences == null) {
			preferences = Preferences.userNodeForPackage(this.getClass());
		}
	}

	/**
	 * Get preference: Should the viewer draw all alignments in one direction?
	 * 
	 * @return
	 */
	public boolean getDisplayReverseComplements() {
		return preferences.getBoolean("unidirectional", true);
	}

	/**
	 *  Set preference: Should the viewer draw all alignments in one direction?
	 * @param b true, false
	 */
	public void setDisplayReverseComplements(boolean b) {
		preferences.putBoolean("unidirectional", b);
	}

	/**
	 * Get preference: Should the viewer draw each contig with a offset or not?
	 * @return
	 */
	public boolean getDisplayOffsets() {
		return preferences.getBoolean("offsets", true);
	}

	/**
	 * Set preference: Should the viewer draw each contig with a offset or not?
	 * @param b
	 */
	public void setDisplayOffsets(boolean b) {
		preferences.putBoolean("offsets", b);
	}

	/**
	 * Get preference: The last file with alignments that was opened.
	 * @return
	 */
	public String getLastFile() {
		return preferences.get("lastFile", "");
	}

	/**
	 * Set the last file with alignments that was open. This file will most likely
	 * be opened the next time the viewer starts.
	 * 
	 * @param s
	 */
	public void setLastFile(String s) {
		preferences.put("lastFile", s);
	}

	/**
	 * Convinience nethod. Tries to save the actual preferences.
	 */
	public void savePrefs() {
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			System.err.println("Error when saving preferences:");
			e.printStackTrace();
		}
	}

	/**
	 * If one does not want to use the get* and set* methods of this object, than the underlying
	 * java.util.prefs.Preferences can be accessed for custom needs.
	 * @return {@link Preferences}
	 */
	public static Preferences getPreferences() {
		return preferences;
	}

	
	/**
	 * Get preference: Should the viewer draw a grid between contigs and reference genomes?
	 * 
	 * @return
	 */
	public boolean getDisplayGrid() {
		return preferences.getBoolean("grid", true);
	}

	/**
	 *  Set preference: Should the viewer draw grid between contigs and reference genomes?
	 * @param b true, false
	 */
	public void setDisplayGrid(boolean b) {
		preferences.putBoolean("grid", b);
	}

}
