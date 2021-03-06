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

package de.bielefeld.uni.cebitec.common;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Defines custom file filter for the use with JFileChooser.
 * 
 * @author Peter Husemann
 * 
 */
public class CustomFileFilter extends FileFilter {

	private String[] extensions;
	private String description = "";

	/**
	 * Creates a file filter for the use with JFileChooser.
	 * 
	 * @param extension
	 *            a string giving all extensions separated by comma. i.e.
	 *            ".fna,.fas"
	 * @param description
	 *            a name of this file type
	 */
	public CustomFileFilter(String extensionString, String description) {

		this.extensions = extensionString.split(",");
		this.description = description;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		for (int i = 0; i < extensions.length; i++) {
			if (f.getName().endsWith(extensions[i])) {
				return true;
			}
		}
		return false;
	}

	// The description of this filter
	@Override
	public String getDescription() {
		String out = description + " (";

		for (int i = 0; i < extensions.length; i++) {
			out += "*" + extensions[i] + ", ";
		}
		out = out.substring(0, out.length() - 2);
		return out + ")";
	}
}
