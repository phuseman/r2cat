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

package de.bielefeld.uni.cebitec.cav.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Peter Husemann
 * 
 */
public class CustomFileFilter extends FileFilter {

	private String extension="";
	private String description="";
	
	
	
	/**
	 * Creates a file filter for the use with JFileChooser.
	 * @param extension the extension like .csv
	 * @param description the description explaining this extension
	 */
	public CustomFileFilter(String extension, String description) {
		this.extension = extension;
		this.description = description;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		if (f.getName().endsWith(extension)) {
			return true;
		} else {
			return false;
		}
	}

	// The description of this filter
	@Override
	public String getDescription() {
		 return description + " (*"+ extension +")";
	}
}
