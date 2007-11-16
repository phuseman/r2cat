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


package de.bielefeld.uni.cebitec.cav.utils;

import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * @author Peter Husemann
 *
 */

public class CAVPrefs {
	
	private static Preferences preferences = null;

	/**
	 * 
	 */
	public CAVPrefs() {
		if (preferences == null) {
		preferences = Preferences.userNodeForPackage(this.getClass());
		}
	}
	
	
	
	public boolean getDisplayUnidirectional() {
		return preferences.getBoolean("unidirectional", false);
	}

	public void setDisplayUnidirectional(boolean b) {
		preferences.putBoolean("unidirectional", b);
	}
	
	public boolean getDisplayOffsets() {
		return preferences.getBoolean("offsets", false);
	}

	public void setDisplayOffsets(boolean b) {
		preferences.putBoolean("offsets", b);
	}
	
	public String getLastFile() {
		return preferences.get("lastFile", "testdata/query.csv");
	}
	
	public void setLastFile(String s) {
		preferences.put("lastFile", s);
	}

	
	
	
	
	public void savePrefs() {
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			System.err.println("Error when saving preferences:");
			e.printStackTrace();
		}
	}



	/**
	 * @return the preferences
	 */
	public static Preferences getPreferences() {
		return preferences;
	}


}
