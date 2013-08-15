package de.bielefeld.uni.cebitec.treecat;

import java.io.File;
import java.io.IOException;

import de.bielefeld.uni.cebitec.treecat.gui.TreeProjectFrame;

/***************************************************************************
 *   Copyright (C) 2009 by Peter Husemann                                  *
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
 * @author phuseman
 * 
 */
public class Treecat {

	/**
	 * Displays a frame for a treecat project. Order contigs based on their
	 * matches to several reference genomes and with the help of a phylogenetic
	 * tree.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {

		TreeProjectFrame treecat = new TreeProjectFrame();
		treecat.setVisible(true);

		if (args.length >= 1 && args[0].endsWith(".tcp")) {
			File initialFile = new File(args[0]);
			try {
				if (!initialFile.exists()) {
					// try the current working directory
					initialFile = new File(System.getProperty("user.dir")
							+ args[0]);
				}
				if (initialFile.canRead()) {
					treecat.loadProjectFromFile(initialFile);
				}
			} catch (IOException e) {
				System.err
						.println("Cannot open file: " + initialFile.getName());
			}
		}
	}

}
