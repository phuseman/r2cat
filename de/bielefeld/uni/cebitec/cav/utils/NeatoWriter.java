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

package de.bielefeld.uni.cebitec.cav.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This can write a neato graph into a file.
 * The result can be used to visualize the graph with the program neato of the GraphViz package.
 * The header of the written file is fixed, but
 * some convenience functions allow to describe nodes or to add edges to the graph.
 * In the end finish() has to be called to close the graph and the file.
 * 
 * @author phuseman
 * 
 */
public class NeatoWriter {
	private BufferedWriter fileWriter;
	private File neatoFile;
	private boolean initialized = false;

	/**
	 * @param neatoFile
	 */
	public NeatoWriter(File neatoFile) {
		this.neatoFile = neatoFile;
		this.init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable, IOException {
		this.finish();
	}

	/**
	 * Opens the given file and writes a graph header for a neato undirected
	 * graph.
	 */
	private void init() {
		if (!initialized && neatoFile != null) {
			try {
				fileWriter = new BufferedWriter(new FileWriter(neatoFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this
					.write("# type the following to generate a graph:\n"
							+ "# neato -Tps -o graph.ps thisfile.neato\n#\n"
							+ "graph ContigAdjacencies {\n"
							+ "# you might want to try overlap=false; if the graph is too cluttered\n"
							+ " graph [splines=true, size=\"7,10\"];\n"
							+ " edge [len=\"1.5\", fontsize=\"10\"];\n"
							+ " node [ fontsize=\"14\"];\n\n");
			initialized = true;
		}

	}

	/**
	 * Closes the graph and the file.
	 */
	public void finish() {
		if (initialized) {
			try {
				fileWriter.write("}\n");
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.initialized = false;
		}
	}

	/**
	 * Adds an arbitrary string to the graph.
	 * 
	 * @param s
	 *            string to write
	 * @return if the writing was successful
	 */
	public boolean write(String s) {
		try {
			fileWriter.write(s);
		} catch (IOException e) {
			System.err.println("Could not write neato file: " + e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Adds a connection of the following form to the graph: 
	 * node1 -- node2 [ params ];
	 * 
	 * @param node1
	 *            first node
	 * @param node2
	 *            second node
	 * @param params
	 *            parameters of this connection. For possibilities see the neato
	 *            manual.
	 * @return if the writing was successful
	 */
	public boolean addConnection(String node1, String node2, String params) {
		if (!initialized) {
			return false;
		}

		return this.write(node1
				+ " -- "
				+ node2
				+ ((params != null && !params.isEmpty()) ? " [" + params + "]"
						: "") + ";\n");

	}

	/**
	 * Writes a node description to create enhanced nodes. The paramerter can
	 * influence the color, the shape and various other things. Read the neato
	 * manual!
	 * 
	 * Writes something like: node [ params ];
	 * 
	 * @param node
	 *            name of the node
	 * @param params
	 *            parameters for enhancement For possibilities see the neato
	 *            manual.
	 * @return if the writing was successful
	 */
	public boolean nodeDescription(String node, String params) {
		if (!initialized) {
			return false;
		}
		return this.write(node
				+ ((params != null && !params.isEmpty()) ? " [" + params + "]"
						: "") + ";\n");
	}

}
