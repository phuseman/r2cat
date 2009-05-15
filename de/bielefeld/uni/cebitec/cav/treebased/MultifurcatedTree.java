/***************************************************************************
 *   Copyright (C) 2009 by Pina Krell, Roland Wittler, Peter Husemann      *
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

package de.bielefeld.uni.cebitec.cav.treebased;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Multifurcated tree. Can be parsed from a newick format.
 * 
 * @author Roland Wittler
 * 
 */

public class MultifurcatedTree {
	public interface NodeVisitor<Node> {
		/**
		 * Method to visit a <tt>node</tt> and do something with it.
		 * 
		 * @param node
		 *            The <tt>node</tt> to visit and do something with
		 */
		void visit(Node node);
	}

	public class Node<T> {

		// Vector with the children of a node
		private Vector<Node<T>> nodeChildren;

		// Object to store within the actual node
		private T key;

		// Parent of the Node
		private Node<T> parent;

		// Name (or Content)
		private String name;

		// The incoming edge length of a node
		private double incomingEdgeLength;

		// rolands Kram >>>>>

		public Integer getDepth() {
			Node<T> n = this;
			Integer d = 0;
			while (!n.isRoot()) {
				n = n.getParent();
				d++;
			}
			return d;
		}

		public void bottomUp(NodeVisitor<Node<T>> nodeVisitor) {
			for (Node<T> nodeChild : this.nodeChildren)
				nodeChild.bottomUp(nodeVisitor);
			nodeVisitor.visit(this);
		}

		public void topDown(NodeVisitor<Node<T>> nodeVisitor) {
			nodeVisitor.visit(this);

			for (Node<T> nodeChild : this.nodeChildren)
				nodeChild.topDown(nodeVisitor);
		}

		// <<<

		/**
		 * Constructs a <tt>node</tt> with a given parent and a given
		 * object(key) to store in the <tt>node</tt>.
		 * 
		 * @param key
		 *            A key-object to store in the actual <tt>node</tt>
		 * @param parent
		 *            A <tt>node</tt> which is the parent of the <tt>node</tt>
		 *            to be created
		 */
		public Node(T key, Node<T> parent) {
			this(key, parent, null);
		}

		/**
		 * Constructs a <tt>node</tt> with a given name, parent and key-object
		 * to be stored in the <tt>node</tt>.
		 * 
		 * @param key
		 *            A key-object to store within the actual <tt>node</tt>
		 * @param parent
		 *            A <tt>node</tt> which is the parent of the <tt>node</tt>
		 *            to be created
		 * @param name
		 *            A string or content to be given to the <tt>node</tt>
		 */
		public Node(T key, Node<T> parent, String name) {
			this(key, parent, name, 0);
		}

		/**
		 * Constructs a <tt>node</tt> with a given name, parent, an incoming
		 * edge length and a key-object to be stored in the <tt>node</tt> .
		 * 
		 * @param key
		 *            A key-object to store within the actual <tt>node</tt>
		 * @param parent
		 *            A <tt>node</tt> which is the parent of the <tt>node</tt>
		 *            to be created
		 * @param name
		 *            A string or content to be given to the <tt>node</tt>
		 * @param incomingEdgeLength
		 *            The incoming edge lenght of a <tt>node</tt>
		 */
		public Node(T key, Node<T> parent, String name, int incomingEdgeLength) {
			this.key = key;
			if (parent != null)
				parent.addChild(this);
			this.name = name;
			this.nodeChildren = new Vector<Node<T>>();
			this.incomingEdgeLength = incomingEdgeLength;
		}

		/**
		 * Adds a new child (also a <tt>node</tt>) to a <tt>nodes</tt>
		 * vector of children.
		 * 
		 * @param newChild
		 *            The <tt>node</tt> to be set as a new child
		 */
		public void addChild(Node<T> newChild) {
			nodeChildren.add(newChild);
			newChild.parent = this;
		}

		/**
		 * Deletes all children (<tt>nodes</tt>) of a <tt>node</tt>.
		 */
		public void clearChildren() {
			this.nodeChildren.clear();
		}

		/**
		 * Parses the object stored in a <tt>node</tt> to String
		 * 
		 * @param printEdgeLength
		 *            specifies whether to print the edge length
		 * @return key The key object parsed to a string
		 */
		public String toString(boolean printEdgeLength) {
			String s = "";
			if (!isLeaf()) {
				s += "(";
				for (Node<T> nodeChild : this.nodeChildren) {
					s += nodeChild.toString(printEdgeLength) + ",";
				}
				s = s.substring(0, s.length() - 1);
				s += ")";
			}
			if (name != null) {
				s += name;
			}
			if (printEdgeLength)
				s += ":" + incomingEdgeLength;
			return s;
		}

		/**
		 * Parses the object stored in a <tt>node</tt> to String
		 * 
		 * @return key The key object parsed to a string
		 */
		public String toString() {
			return toString(true);
		}

		/**
		 * Traverses a <tt>node</tt> with different visitors.
		 * 
		 * @param nodeVisitor
		 *            Visitor-type with which the <tt>node</tt> should be
		 *            traversed
		 */
		public void traverse(NodeVisitor<Node<T>> nodeVisitor) {
			// visits the actual node
			nodeVisitor.visit(this);
			// calls the method traverse on all children of the actual node
			for (Node<T> nodeChild : this.nodeChildren) {
				nodeChild.traverse(nodeVisitor);
			}
		}

		/**
		 * Getter for the parent of a <tt>node</tt>.
		 * 
		 * @return parent The parent <tt>node</tt> of a <tt>node</tt>
		 * @category Getter
		 */
		public Node<T> getParent() {
			return parent;
		}

		/**
		 * Boolean wether a <tt>node</tt> is a <tt>leaf</tt> or not. A
		 * <tt>leaf</tt>does not contain children (<tt>nodes</tt>).
		 * 
		 * @return true if the <tt>node</tt> is a leaf
		 */
		public boolean isLeaf() {
			// return ((this.nodeChildren.isEmpty()) && (!this.isRoot()));
			return ((this.nodeChildren.isEmpty()));
		}

		/**
		 * Boolean wether a <tt>node</tt> is a <tt>root</tt> of a tree or
		 * not. Means that the <tt>node</tt> has no parent.
		 * 
		 * @return true if the <tt>node</tt> is the <tt>root</tt>
		 */
		public boolean isRoot() {
			return this.parent == null;
		}

		/**
		 * Getter for the name (content) of a <tt>node</tt>.
		 * 
		 * @return name The name (content) of the <tt>node</tt>
		 * @category Getter
		 */
		public String getName() {
			return name;
		}

		/**
		 * Set a new name (content) to a <tt>node</tt>.
		 * 
		 * @param newName
		 *            The name (content) to be set for a <tt>node</tt>
		 * @category Setter
		 */
		public void setName(String newName) {
			this.name = newName;
		}

		/**
		 * Sets a specific key-object to store within the actual <tt>node</tt>.
		 * 
		 * @param newKey
		 *            object to store within the <tt>node</tt>
		 * @category Setter
		 */
		public void setKey(T newKey) {
			this.key = newKey;
		}

		/**
		 * Getter for the key-object which is stored in the <tt>node</tt>.
		 * 
		 * @return key A key object which is stored in the <tt>node</tt>
		 * @category Getter
		 */
		public T getKey() {
			return key;
		}

		/**
		 * Getter for the vector of children-nodes of the actual <tt>node</tt>.
		 * 
		 * @return nodeChildren The vector of childrens of a <tt>node</tt>
		 * @category Getter
		 */
		public Vector<Node<T>> getNodeChildren() {
			return nodeChildren;
		}

		/**
		 * Sets a vector of children (<tt>nodes</tt>) to a <tt>node</tt>.
		 * 
		 * @param children
		 *            A vector of children-nodes
		 * @category Setter
		 */
		public void setNodeChildren(Vector<Node<T>> children) {
			this.nodeChildren = children;
		}

		/**
		 * Sets the incoming edge length for a <tt>node</tt>.
		 * 
		 * @param incomingEdgeLength
		 *            The length to be set as the incoming edge length
		 * @category Setter
		 */
		public void setIncomingEdgeLength(double incomingEdgeLength) {
			this.incomingEdgeLength = incomingEdgeLength;
		}

		/**
		 * Gets the incoming edge length for a <tt>node</tt>.
		 * 
		 * @return The incoming edge length of a <tt>node</tt>
		 * @category Getter
		 */
		public double getIncomingEdgeLength() {
			return incomingEdgeLength;
		}
	}

	private Node<?> root;

	/**
	 * Constructor to create a <tt>multifurcated tree</tt>
	 * 
	 * @param root
	 */
	public MultifurcatedTree(Node<?> root) {
		this.root = root;
	}

	/**
	 * Constructor to create a <tt>multifurcated tree</tt>
	 * 
	 * @param root
	 */
	public MultifurcatedTree() {
		this.root = null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isTreeEmpty() {
		if (root.getParent() == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public Node<?> getRoot() {
		return root;
	}

	public void setRoot(Node<?> r) {
		this.root = r;
	}

	/**
	 * Inherited method of the interface <tt>Traverser</tt> Implements the
	 * <tt>traversal</tt> of a Tree (means the traversal of a <tt>node</tt>,
	 * e.g. the <tt>root</tt>
	 */
	public void traverse(NodeVisitor nodeVisitor) {
		this.getRoot().topDown(nodeVisitor);
	}

	public void bottomUp(NodeVisitor nodeVisitor) {
		this.getRoot().bottomUp(nodeVisitor);
	}

	public void topDown(NodeVisitor nodeVisitor) {
		this.getRoot().topDown(nodeVisitor);
	}

	// Rolands neue Implementation eines Newick parsers

	/**
	 * Parse a newick tree. Exits if tree is unproper.
	 * 
	 * @param s
	 *            String containing a newick tree
	 * @return multifurcated tree defined by s
	 */
	/**
	 * @param s
	 * @return
	 */
	private Node parseNode(String s) throws Exception {
		// if no node name is specified in the string a default name will be
		// used:
		// node_0, node_1, ...
		int nameCount = 0;
		// create the root (there has to be one, because ; is already found.)
		Node p = new Node(null, null);
		// scan the string...
		StringTokenizer tokenizer = new StringTokenizer(s, "(),;", true);
		String lastToken, token = "";
		while (tokenizer.hasMoreTokens()) {
			lastToken = token;
			token = tokenizer.nextToken();
			if (token.equals("(")) {
				if (lastToken.equals("(") || lastToken.equals(",")
						|| lastToken.equals("")) {
					p = new Node(null, p);
					// determine children in next loop runs
				} else {
					throw new Exception("*** error: tree unproper.");
				}
			}

			if (token.equals(",") || token.equals(")") || token.equals(";")) {
				if (p == null) {
					throw new Exception("*** error: tree unproper.");
				}
				// finish node
				// has this node a name or edge length?
				if (!lastToken.equals(",") && !lastToken.equals(")")
						&& !lastToken.equals("(")) {
					// edge length?
					int sepPos = lastToken.indexOf(':');
					if (sepPos >= 0) {
						try {
							p.setIncomingEdgeLength(Double
									.parseDouble(lastToken
											.substring(sepPos + 1)));
						} catch (NumberFormatException e) {
							throw new Exception(
									"*** error: tree unproper: unproper edge length.");
						}
						lastToken = lastToken.substring(0, sepPos);
					}
					if (lastToken.length() > 0) {
						p.setName(lastToken);
					} else {
						p.setName("node_" + nameCount++);
					}
				} else {
					p.setName("node_" + nameCount++);
				}

			}
			if (token.equals(",")) {
				p = new Node(null, p.getParent());
				// detemine name and edge length in next loop run
			}
			if (token.equals(")")) {
				p = p.getParent();
				// detemine name and edge length in next loop run
			}
		}
		// created a proper tree?
		if (token.equals(";") && p.isRoot()) {
			return p;
		} else {
			throw new Exception("*** error: tree unproper.");
		}
	}

	/**
	 * parse a newick tree. First clean the string (remove white spaces etc.)
	 * 
	 * @param input
	 *            the string containing a newick tree.
	 */
	public MultifurcatedTree(String input) throws Exception {
		input = input.replaceAll("\\s", "");
		if (input.indexOf(';') < 0) {
			// error
			throw new Exception("*** error: tree unproper: no ; found.");
		} else {
			String nodeString = input.substring(0, input.indexOf(';') + 1);
			Node root = parseNode(nodeString);
			this.root = root;
		}

	}

	/**
	 * Reads the tree structure (newick format) from the given file
	 * 
	 * @param file
	 *            the file to read the tree from
	 * @return the tree
	 */
	public static MultifurcatedTree readTree(String file) throws Exception {
		return new MultifurcatedTree(readTreeString(file));
	}

	/**
	 * Reads the tree string from the given file
	 * 
	 * @param file
	 *            the file to read the tree from
	 * @return a string containing the tree
	 */
	public static String readTreeString(String file) {
		// input tree
		File inFile = new File(file);
		String readString = "";
		if (!inFile.exists() && !inFile.canRead()) {
			System.out.println("Can not open file " + inFile.getAbsolutePath()
					+ "!");
			System.exit(1);
		}
		// parse the file
		try {
			BufferedReader in = new BufferedReader(new FileReader(inFile));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.length() > 0 && line.charAt(0) != '#') {
					readString += line;
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return readString;
	}

	public String toString(boolean printEdgeLength) {
		if (root != null)
			return root.toString(printEdgeLength) + ";";
		else
			return "nulltree";
	}

	public String toString() {
		return toString(true);
	}
}
