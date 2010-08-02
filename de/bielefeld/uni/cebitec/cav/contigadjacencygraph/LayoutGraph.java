/***************************************************************************
 *   Copyright (C) 2010 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.cav.contigadjacencygraph;

import java.io.File;
import java.util.Locale;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.utils.NeatoWriter;

/**
 * This class represents the layout of a set of contigs. The edges are basically a pair of indices connecting the congigs given (at that position) in the nodes object.
 * Edges feature som other information for example if a contig connection is from the right or left end, if the contigs are repetitive and so on.
 * @author phuseman
 * 
 */
public class LayoutGraph {
	// the contigs that are the nodes in this graph. the indices in this vector
	// determine the numbers that are used to indicate an edge.
	private Vector<DNASequence> nodes;
	// each edge connect one side of a contig with one side of another contig.
	private Vector<AdjacencyEdge> edges;

	// two arrays to store the total support of the nodes. the index corresponds
	// to the one in nodes.
	// seperated for left and right nodes.
	private double[] totalSupportLeftConnectors = null;
	private double[] totalSupportRightConnectors = null;

	private boolean[] nodesUsed = null;
	private int[] nodeCopyNumber = null;

	/**
	 * When exporting the layout to neato format (GraphViz package) this can be used to determine if a contig is represented by one node or by two.
	 * In the underlying layout one contig consts of two nodes, so the onenode style is a little bit too simplified.
	 * 
	 * @author phuseman
	 */
	public static enum NeatoOutputType {
		ONENODE, TWONODES;
	}


	/**
	 * This class models adjacency edges between contigs. The contigs are stored as integers that correspond to
	 * the appropriate {@link DNASequence} objects stored in the nodes vector. Internally it is stored, if left or right ends of the contigs are connected.
	 * Additionally to the edge, a score is stored. The higher the score, the more likely the adjacency.
	 * If the total support (the row sum of the matrix in the contig adjacency graph) is provided in the layout graph, then the relative support can be returned.
	 * It is the relative fraction of an edge weight with respect to all edgeweights of that node.
	 * For repetitive contigs ther is a repeat counter such that a single contig cn be incorporated several times in a graph.
	 * In all of the helper functions functionNamei() refers to the first entry (although the edges are undirected) and functionNamej() addresses the second contig of the edge.
	 * 
	 * 
	 * 
	 * @author phuseman
	 *
	 */
	public class AdjacencyEdge implements Comparable<AdjacencyEdge>, Cloneable {
		private int i = 0; // id of the first node
		private int j = 0; // id of the second node
		private double support = -1.; // support of this connection

		private boolean isLeftConnectori; // does this connection belong to the
		// left
		// connector of contig i?
		private boolean isLeftConnectorj; // dito with contigj
		private int repeatCounti = 0; // if repetitive contigs are involved,
		// what is the
		// copy number of contig i?
		private int repeatCountj = 0; // same for contig j

		/**
		 * Constructor of an adjacency edge.
		 * 
		 * @param i
		 *            The id (integer) of one of the nodes. The appropriate
		 *            contig can be found in the nodes array.
		 * @param isLeftConnectori
		 *            boolean if this is a left or right connector of that node.
		 * @param j
		 *            The id of the other node.
		 * @param isLeftConnectorj
		 *            boolean if this is a left or right connector of that node.
		 * @param support
		 *            the score of this connection.
		 */
		public AdjacencyEdge(int i, boolean isLeftConnectori, int j,
				boolean isLeftConnectorj, double support) {
			this.i = i;
			this.j = j;
			this.isLeftConnectori = isLeftConnectori;
			this.isLeftConnectorj = isLeftConnectorj;
			this.support = support;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#clone()
		 */
		public LayoutGraph.AdjacencyEdge clone() {
			return new AdjacencyEdge(i, isLeftConnectori, j, isLeftConnectorj,
					support);
		}

		/**
		 * @return if the first node in this connection is a left connector
		 */
		public boolean isLeftConnectori() {
			return isLeftConnectori;
		}

		/**
		 * @return if the first node in this connection is a right connector
		 */
		public boolean isRightConnectori() {
			return !isLeftConnectori;
		}

		/**
		 * @return if the second node in this connection is a left connector
		 */
		public boolean isLeftConnectorj() {
			return isLeftConnectorj;
		}

		/**
		 * @return if the second node in this connection is a right connector
		 */
		public boolean isRightConnectorj() {
			return !isLeftConnectorj;
		}

		/**
		 * @return if this conection involves repetitive contigs
		 */
		public boolean isRepeatConnection() {
			if (nodes.get(this.i).isRepetitive()
					|| nodes.get(this.j).isRepetitive()) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public int compareTo(AdjacencyEdge other) {
			if (this.support == other.support) {
				return 0;
			}
			if (this.support > other.support) {
				return -1;
			} else {
				return 1;
			}
		}

		/**
		 * Displays the adjacency edge in an ascii representation like <code>|contig1><contig2|</code>
		 * @return the ascii representation
		 */
		public String showContigConnectionAsciiString() {
			String connection = "";
			if (isLeftConnectori) {
				connection = "<" + nodes.get(i).getId() + "|";
			} else {
				connection = "|" + nodes.get(i).getId() + ">";
			}

			if (isLeftConnectorj) {
				connection += "|" + nodes.get(j).getId() + ">";
			} else {
				connection += "<" + nodes.get(j).getId() + "|";
			}

			return connection;

		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			// TODO include repeat count
			return String.format("%s %.2f", showContigConnectionAsciiString(),
					Math.log10(support));
		}

		/**
		 * Tells if two connections are the same.
		 * Here the connection a-b would be considered as equal to either b-a or a-b
		 * @param other edge to compare to
		 * @return if the connections are to the same contigs (note: not to the same contig connectors)
		 */
		public boolean equals(AdjacencyEdge other) {
			if (this.i == other.i && this.j == other.j) {
				return true;
			} else if (this.i == other.j && this.j == other.i) {
				return true;
			}
			return false;
		}

		/**
		 * Computes the relative support of the i node of this edge. If the
		 * relative support is not set in the LayoutGraph class, then null is
		 * returned.
		 * 
		 * @return relative support with respect to node i
		 */
		public Double getRelativeSupporti() {
			if (isLeftConnectori) {
				if (totalSupportLeftConnectors != null) {
					return this.support / totalSupportLeftConnectors[this.i];
				} else {
					return null;
				}
			} else { // is right connector
				if (totalSupportRightConnectors != null) {
					return this.support / totalSupportRightConnectors[this.i];
				} else {
					return null;
				}
			}
		}

		/**
		 * Computes the relative support of the j node of this edge. If the
		 * relative support is not set in the LayoutGraph class, then null is
		 * returned.
		 * 
		 * @return relative support with respect to node j
		 */
		public Double getRelativeSupportj() {
			if (isLeftConnectorj) {
				if (totalSupportLeftConnectors != null) {
					return this.support / totalSupportLeftConnectors[this.j];
				} else {
					return null;
				}
			} else { // is right connector
				if (totalSupportRightConnectors != null) {
					return this.support / totalSupportRightConnectors[this.j];
				} else {
					return null;
				}
			}
		}

		/**
		 * Gets the support of this connection. It is a score how likely the specified contigs are adjacent.
		 * @return
		 */
		public double getSupport() {
			return this.support;
		}

		/**
		 * This is a convenience function to operate on the matrix of the contig adjacency graph.
		 * The contig adjacency matrix contains all connections from distinct contigs and has the dimension (2n,2n).
		 * It is organized as follows:
		 *    r            | l 
		 * ---------------------------- j
		 * r| right->right | right->left
		 * l| left->right  | left->left
		 * i
		 *
		 * The right connectors go from 0 to n-1 and the left connectors from n to 2n-1. 
		 * @return This method returns the apropriate matrix index.
		 */
		public int getMatrixIdi() {
			if (isLeftConnectori) {
				return i+ nodes.size();
			} else {
				return i ;
			}
		}

		/**
 		 * This is a convenience function to operate on the matrix of the contig adjacency graph.
		 * The contig adjacency matrix contains all connections from distinct contigs and has the dimension (2n,2n).
		 * It is organized as follows:
		 *    r            | l 
		 * ---------------------------- j
		 * r| right->right | right->left
		 * l| left->right  | left->left
		 * i
		 *
		 * The right connectors go from 0 to n-1 and the left connectors from n to 2n-1. 
		 * @return This method returns the apropriate matrix index.
		 */
		public int getMatrixIdj() {
			if (isLeftConnectorj) {
				return j + nodes.size();
			} else {
				return j;
			}
		}

		/**
		 * @return The associated contig (first component)
		 */
		public DNASequence getContigi() {
			return nodes.get(i);
		}

		/**
		 * @return The associated contig (second component)
		 */
		public DNASequence getContigj() {
			return nodes.get(j);
		}

		/**
		 * This method swaps the contigs. This is used in the repeat aware stuff to ensure that the repetitive contig is always contig i.
		 * In general the edges are undirected so this method should not be applied so often.
		 */
		protected void swapij() {
			int tmpinteger = i;
			i = j;
			j = tmpinteger;

			tmpinteger = repeatCounti;
			repeatCounti = repeatCountj;
			repeatCountj = tmpinteger;

			boolean tmpboolean = isLeftConnectori;
			isLeftConnectori = isLeftConnectorj;
			isLeftConnectorj = tmpboolean;

		}


		/**
		 * (Repetitive) contigs can be included several times into a layout. To make them distinctively, this method returns which instance the first contig belongs to.
		 * @return
		 */
		public int getRepeatCounti() {
			return repeatCounti;
		}

		/**
		 * Set the instance number of the first contig
		 * @param repeatCounti
		 */
		public void setRepeatCounti(int repeatCounti) {
			this.repeatCounti = repeatCounti;
		}

		/**
		 * (Repetitive) contigs can be included several times into a layout. To make them distinctively, this method returns which instance the second contig belongs to.
		 * @return
		 */
		public int getRepeatCountj() {
			return repeatCountj;
		}

		/**
 		 * Set the instance number of the second contig
		 * @param repeatCountj
		 */
		public void setRepeatCountj(int repeatCountj) {
			this.repeatCountj = repeatCountj;
		}

		/**
		 * @return the index of the first node
		 */
		public int geti() {
			return i;
		}

		/**
		 * @return the index of the second node
		 */
		public int getj() {
			return j;
		}
	} // adjacency edge sub-class

	
	/**
	 * To create a layout graph, a set of nodes (i.e. contigs) have to be provided. The edges can be added one by one with the addEdge method.
	 * @param nodes
	 */
	public LayoutGraph(Vector<DNASequence> nodes) {
		edges = new Vector<AdjacencyEdge>();
		this.nodes = nodes;
		nodesUsed = new boolean[nodes.size()];
		nodeCopyNumber = new int[nodes.size()];
	}

	/**
	 * @return A vetor containing all nodes (contigs).
	 */
	public Vector<DNASequence> getNodes() {
		return nodes;
	}

	/**
	 * Set the contigs of this layout graph. Be careful that indices of the nodes vector correspond to the edges. This method should only be called in the beginning.
	 * @param nodes
	 */
	public void setNodes(Vector<DNASequence> nodes) {
		this.nodes = nodes;
	}

	/**
	 * Get a vector of all edges.
	 * @return
	 */
	public Vector<AdjacencyEdge> getEdges() {
		return edges;
	}

	/**
	 * Adds an edge to the layout. Pleas assure that the indices used in the edges correspond to the nodes vector!
	 * @param AdjacencyEdge to add to the layout.
	 */
	public void addEdge(AdjacencyEdge edge) throws IllegalArgumentException {
		if (edge.i > nodes.size() || edge.j > nodes.size()) {
			throw new IllegalArgumentException("Error: Tried to add an edge with an index that was bigger than the number of nodes.");
		}
		nodesUsed[edge.i] = true;
		if (nodeCopyNumber[edge.i] < edge.getRepeatCounti()) {
			nodeCopyNumber[edge.i] = edge.getRepeatCounti();
		}

		nodesUsed[edge.j] = true;
		if (nodeCopyNumber[edge.j] < edge.getRepeatCountj()) {
			nodeCopyNumber[edge.j] = edge.getRepeatCountj();
		}
		this.edges.add(edge);
	}

	/**
	 * Writes the layout graph of this object in DOT format into a file.<br>
	 * 
	 * WARNING: The layout graph contains the relative support of both sides of
	 * an edge. For this headlabel and taillabel is used. Unfortunately, in the
	 * neato language it is not well defined to wich side this label is put
	 * since the edges are undirected. Neato chooses the head and tail of an
	 * edge according to the first occurrence. Quote from the manual:
	 * <quote> Some attributes, such as
	 * dir or arrowtail, are ambiguous when used in DOT with an undirected graph
	 * since the head and tail of an edge are meaningless. As a convention, the
	 * first time an undirected edge appears, the DOT parser will assign the
	 * left node as the tail node and the right node as the head. For example,
	 * the edge A -- B will have tail A and head B. It is the user's
	 * responsibility to handle such edges consistently. If the edge appears
	 * later, in the format B -- A [taillabel = "tail"] the drawing will attach
	 * the tail label to node A. To avoid possible confusion when such
	 * attributes are required, the user is encouraged to use a directed graph.
	 * If it is important to make the graph appear undirected, this can be done
	 * using the dir, arrowtail or arrowhead attributes. </quote>
	 * Thus it can happen that a relative support value is displayed at the wrong node.
	 * In the DOT source however, if it is written like this:
	 * tail -- head [ taillabel="tail", headlabel="head"]
	 * the lables are everytime in this order. 
	 * In future this function is hopefully not any longer necessary, when an interactive
	 * javabased display of the graph is implemented.
	 * 
	 * 
	 * @param f
	 *            file to write in.
	 * @param type
	 *            display one or two nodes per contig?
	 */
	public void writeLayoutAsNeato(File f, NeatoOutputType type) {
		NeatoWriter neato = new NeatoWriter(f);
		neato.write(getNodesAsNeato(type));
		neato.write(getEdgesAsNeato(type));
		neato.finish();
	}

	/**
	 * This method produces a header for the GraphViz DOT format. Depending on
	 * the NeatoOutputType either each contig is represented by two nodes or the
	 * left and right connectors are joint to one node for each contig. Two
	 * nodes take much more time for layouting in GraphViz for a dozen of nodes!
	 * 
	 * @param type
	 *            One or two nodes per contig?
	 * @return string representing the nodes in DOT format
	 */
	private String getNodesAsNeato(NeatoOutputType type) {
		// write all connectors as neato output
		StringBuilder neatoHeader = new StringBuilder();

		if (type == NeatoOutputType.TWONODES) {
			// include special format if both contig connetors are displayed.
			neatoHeader
					.append("{ node [label=\"\", shape=circle,height=0.12,width=0.12,fontsize=1]\n");
			for (int i = 0; i < nodes.size(); i++) {
				if (nodesUsed[i] && nodeCopyNumber[i] == 0) {
					neatoHeader.append(" l" + i + " r" + i);
				}
			}
			neatoHeader.append("}\n");

			// repeat intances
			neatoHeader
					.append("{ node [label=\"\", shape=circle,height=0.12,width=0.12,fontsize=1,shape=box]\n");
			for (int i = 0; i < nodes.size(); i++) {
				if (nodesUsed[i] && nodeCopyNumber[i] > 0) {
					for (int r = 1; r <= nodeCopyNumber[i]; r++) {
						neatoHeader.append(" l" + i + String.format("%04d", r)
								+ " r" + i + String.format("%04d", r));
					}
				}
			}
			neatoHeader.append("}\n\n");
		}

		// write a node definition for each node.
		for (int i = 0; i < nodes.size(); i++) {
			if (nodesUsed[i]) {
				String id = "";
				String label = "";
				String length = String.format((Locale) null, "%.1f", nodes.get(
						i).getSize() / 1000.)
						+ "kb\",";
				String parameters = "";
				if (nodes.get(i).isRepetitive()) {
					parameters += "shape=box,";
				}
				if (nodes.get(i).getSize() < 3500) {
					parameters += "color=gray,fontcolor=gray20,";
				}
				if (type == NeatoOutputType.TWONODES) {
					parameters += " len=0.4, arrowsize=2., arrowhead=normal];\n";
				} else if (type == NeatoOutputType.ONENODE) {
					parameters += "];\n";
				}

				if (nodeCopyNumber[i] == 0) { // copy number zero: contig is not
												// repetitive / occures once
					if (type == NeatoOutputType.TWONODES) {
						id = " l" + i + " -- r" + i + " ";
					} else if (type == NeatoOutputType.ONENODE) {
						id = Integer.toString(i) + " ";
					}
					// set the label for this node
					label = "[label=\"" + nodes.get(i).getId() + "\\n";
					// append the whole line to the neato node output
					neatoHeader.append(id + label + length + parameters);
				} else { // copy number greater zero: contig is repetitive /
							// occures several times
					for (int r = 1; r <= nodeCopyNumber[i]; r++) { // put a node
																	// line for
																	// each
																	// occurrence
						// the ids of repetitive contigs have 000n as prefix
						if (type == NeatoOutputType.TWONODES) {
							// in two nodes per contig mode this is a connection
							// from the left to the right connector
							id = " l" + i + String.format("%04d", r) + " -- r"
									+ i + String.format("%04d", r) + " ";
						} else if (type == NeatoOutputType.ONENODE) {
							id = Integer.toString(i) + String.format("%04d", r)
									+ " ";
						}
						// the label contains the repeat count
						label = "[label=\"" + nodes.get(i).getId() + "(" + r
								+ ")\\n";
						;
						neatoHeader.append(id + label + length + parameters);
					}
				}

			} // all incorporated nodes

		}

		return neatoHeader.toString();
	}

	/**
	 * Writes all edges given to the neato output. 
	 * 
	 * @param type
	 *            Should each contig be represented by one or by two nodes in
	 *            the graph?
	 * @return Returns a string containing the neato links.
	 */
	private String getEdgesAsNeato(NeatoOutputType type) {
		StringBuffer output = new StringBuffer();
		// *** write all connections as neato output
		boolean connectionToSmallContig = false;

		String leftID = "";
		String rightID = "";
		String description = "";
		Double relativeSupportLeft = null;
		Double relativeSupportRight = null;

		for (AdjacencyEdge element : edges) {

			if (type == NeatoOutputType.ONENODE) {
				leftID = Integer.toString(element.i);
				rightID = Integer.toString(element.j);
			} else if (type == NeatoOutputType.TWONODES) {
				leftID = (element.isLeftConnectori() ? "l" : "r")
						+ Integer.toString(element.i);
				rightID = (element.isLeftConnectorj() ? "l" : "r")
						+ Integer.toString(element.j);
			}

			if (element.repeatCounti > 0) {
				leftID += String.format("%04d", element.repeatCounti);
			}
			if (element.repeatCountj > 0) {
				rightID += String.format("%04d", element.repeatCountj);
			}

			relativeSupportLeft = element.getRelativeSupporti();
			relativeSupportRight = element.getRelativeSupportj();

			if (nodes.get(element.i).getSize() < 3500
					|| nodes.get(element.j).getSize() < 3500) {
				connectionToSmallContig = true;
			} else {
				connectionToSmallContig = false;
			}

			if (relativeSupportLeft != null && relativeSupportRight != null) {
				// the (Locale) null is used to force the numbers to have a dot
				// as float seperator
				// because depending on the systems locale setting either 3.12
				// or 3,12 is written.
				description = "taillabel="
						+ String.format((Locale) null, "%.1f",
								(100. * relativeSupportLeft))
						+ ",headlabel="
						+ String.format((Locale) null, "%.1f",
								(100. * relativeSupportRight))
						+ (connectionToSmallContig ? ",color=gray,fontcolor=gray20,"
								: "");
			} else {
				description = "label="
						+ String.format((Locale) null, "%.2f", Math
								.log10(element.support))
						+ (connectionToSmallContig ? ",color=gray,fontcolor=gray20,"
								: "");
			}

			output.append(NeatoWriter.getConnectionString(leftID, rightID,
					description));
		}
		return output.toString();
	}

	/**
	 * Sets the total support (row/column sum of scores) for all left connectors.
 	 * Due to the internal format of the adjacency edges, left and right connectors need a seperate array of the values.
	 * @param totalSupportLeftConnectors
	 */
	public void setTotalSupportLeftConnectors(
			double[] totalSupportLeftConnectors) {
		this.totalSupportLeftConnectors = totalSupportLeftConnectors;
	}

	/**
	 * Sets the total support (row/column sum of scores) for all right connectors.
	 * Due to the internal format of the adjacency edges, left and right connectors need a seperate array of the values.
	 * @param totalSupportRightConnectors
	 */
	public void setTotalSupportRightConnectors(
			double[] totalSupportRightConnectors) {
		this.totalSupportRightConnectors = totalSupportRightConnectors;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * 
	 * Creates a csv like string representation of this layout.
	 */
	public String toString() {
		return this.csvOutput();
	}
	
	/**
	 * Provides the edges of this layout in a csv like output. The fields outputted are in order:
	 * 1. the FASTA ID of the first contig
	 * 2. the connected side of the first contig with respect to the FASTA file
	 * 3. the relative support of the first contig connector
	 * 4. the FASTA ID of the second contig
	 * 5. the connected side of the second contig with respect to the FASTA file
	 * 6. the relative support of the second contig connector
	 * 7. the absolute support of this connection
	 * @return edges in csv format
	 */
	public String csvOutput() {
		StringBuilder out = new StringBuilder();
		out.append("#contigID1, contigEnd1, relativeSupport1, contigID2, contigEnd2, relativeSupport2, absoluteSupport\n");
		
		AdjacencyEdge e=null;
		for (int i = 0; i < edges.size(); i++) {
			e=edges.get(i);
			out.append(String.format((Locale) null, 
					"%s, %s, %.2f, %s, %s, %.2f, %.2f\n",
					e.getContigi().getId(),
					e.isLeftConnectori()?"left":"right",
					e.getRelativeSupporti()*100.,
					e.getContigj().getId(),
					e.isLeftConnectorj()?"left":"right",
					e.getRelativeSupportj()*100.,
					e.getSupport()
					));
		}
		
		return out.toString();

	}

}
