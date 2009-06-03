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

package de.bielefeld.uni.cebitec.cav.treebased;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;
import de.bielefeld.uni.cebitec.cav.qgram.QGramFilter;
import de.bielefeld.uni.cebitec.cav.qgram.QGramIndex;
import de.bielefeld.uni.cebitec.cav.treebased.MultifurcatedTree.Node;
import de.bielefeld.uni.cebitec.cav.treebased.MultifurcatedTree.NodeVisitor;
import de.bielefeld.uni.cebitec.cav.utils.Timer;

/**
 * This bundles all information needed to use several references to find a
 * layout for a set of contigs. The information are read from a file which
 * contains several key=value pairs.
 * 
 * The usual procedure should be: readProject(file); generateMatches();
 * sortContigs();
 * 
 * 
 * @author phuseman
 * 
 */
public class TreebasedContigSorterProject {

	// the fasta file that contains the contigs
	private File contigs = null;
	// the files containing the references
	private Vector<File> references = null;
	// directory where the matches are cached and the results are written
	private File projectDir = null;

	//the matches to each reference
	private Vector<AlignmentPositionsList> contigsToReferencesMatchesList = null;
	//the phylogenetic distance of each reference (with the same index as above) to the contigs genome
	private Vector<Double> contigsToReferencesTreeDistanceList = null;
	//the same, but in a hash
	private HashMap<String, Double> contigsToReferencesTreeDistanceHashmap = null;

	//the sorter instance
	private TreebasedContigSorter treebasedSorter = null;

	/**
	 * Main method to try with a hard coded file.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TreebasedContigSorterProject project = new TreebasedContigSorterProject();
		try {
			
			boolean projectParsed=project.readProject(new File(
					// contigs
//							 "/homes/phuseman/compassemb/treebased/Corynebacterium_kroppenstedtii_DSM44385_454AllContigs.rtc"));
							// assembled genome against all
							// "/homes/phuseman/compassemb/treebased/Corynebacterium_kroppenstedtii_DSM44385.rtc"));
							 "/homes/phuseman/compassemb/treebased/Corynebacterium_urealyticum_DSM_7109_454AllContigs.rtc"));
							// "/homes/phuseman/compassemb/treebased/Corynebacterium_urealyticum_DSM_7109.rtc"));
							// "/homes/phuseman/compassemb/treebased/Corynebacterium_aurimucosum_454AllContigs.rtc"));
							// "/homes/phuseman/compassemb/treebased/Corynebacterium_aurimucosum.rtc"));
							//"/homes/phuseman/compassemb/treebased/Corynebacterium_urealyticum_DSM_7111_454AllContigs.rtc"));

			
			if(!projectParsed) {
				System.err.println("The given project file was not sucessfully parsed");
				System.exit(1);
			}
			
			
		} catch (IOException e) {
			System.err.println("The given project file was not sucessfully parsed:\n"+ e.getMessage());
			System.exit(1);
		}
		
		

		Timer t = Timer.getInstance();
		t.startTimer();

		project.generateMatches();

		t.restartTimer("matches");

		project.sortContigs();

		t.stopTimer("sorting");

		System.out.println("Done");
	}

	/**
	 * Constructor. Does nothing spectacular, just initializes some Verctors
	 */
	public TreebasedContigSorterProject() {
		references = new Vector<File>();
		contigsToReferencesTreeDistanceList = new Vector<Double>();
		contigsToReferencesMatchesList = new Vector<AlignmentPositionsList>();
	}

	/**
	 * This constructor tries to parse the given project file. After that
	 * generateMatches() and sortContigs() have to be called.
	 * 
	 * @param configFile
	 * @throws IOException
	 */
	public TreebasedContigSorterProject(File configFile) throws IOException {
		this();
		this.readProject(configFile);
	}

	/**
	 * Method to read all necessary information from a file.
	 * It contains several key=value pairs. # marks a comment line.
	 * The following keys are recognized:
	 *  projectdir - a directory where the matches are cached
	 *  newicktree - a newick tree of the incorporated species. The name 
	 *  	of a species must be the filename, without path and extension.
	 *  	If the contigs are for example in /path/to/myContigs.fas
	 *  	then in the tree the contigs must have the name myContigs
	 *  contigs - path and filename to the file containing the contigs in multiple FASTA format.
	 *  reference - (several times) path and filename to the reference genomes in multiple fasta
	 *  
	 * A complete file could look like the following:
<code>
# Project: Treebased contig arrangement
# r tree cat - reference tree contig arrangement tool

# directory where to cache the matchings between contigs and references
projectdir="/homes/phuseman/compassemb/treebased/contig_matches"


# phylogenetic tree of the species. the names have to be the same as the reference files without extension
newicktree=(Corynebacterium_efficiens_YS-314:0.083380,(Corynebacterium_diphtheriae:0.162190,(Corynebacterium_aurimucosum:0.190600,((Corynebacterium_jeikeium_K411:0.142030,(Corynebacterium_urealyticum_DSM_7109:0.002730,Corynebacterium_urealyticum_DSM_7111_454AllContigs:0.003990):0.159950):0.074310,Corynebacterium_kroppenstedtii_DSM44385:0.236550):0.053480):0.008930):0.076060,Corynebacterium_glutamicum_ATCC_13032_Bielefeld:0.082080);


# contigs in multiple fasta format
contigs="contigs/Corynebacterium_urealyticum_DSM_7111_454AllContigs.fna"


# reference genomes. one entry per genome.
#reference="genomes/Corynebacterium_aurimucosum.fas"
#reference="genomes/Corynebacterium_diphtheriae.fna"
#reference="genomes/Corynebacterium_efficiens_YS-314.fna"
#reference="genomes/Corynebacterium_glutamicum_ATCC_13032_Bielefeld.fna"
##reference="genomes/Corynebacterium_glutamicum_ATCC_13032_Kitasato.fna"
##reference="genomes/Corynebacterium_glutamicum_R.fna"
reference="genomes/Corynebacterium_jeikeium_K411.fna"
#reference="genomes/Corynebacterium_kroppenstedtii_DSM44385.fas"
reference="genomes/Corynebacterium_urealyticum_DSM_7109.fna"


</code>
	 * @param f file to read the information from
	 * @return boolean if it was successfull to parse the file.
	 * @throws IOException if something is wrong with the file
	 */
	public boolean readProject(File f) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(f));
		MultifurcatedTree phylogeneticTree = null;
		String line;
		String[] propertyValue;
		String value;

		String currentWorkingDirectory = f.getParent();

		while (in.ready()) {
			line = in.readLine();

			// ignore comments
			if (line.startsWith("#") || line.startsWith("\"#")
					|| line.isEmpty()) {
				continue;
			}

			// split at the = sign into the pair property=value.
			propertyValue = line.split("=");

			if (propertyValue.length != 2) {
				System.err
						.println("Error, too much or too less '=' signs: "
								+ line
								+ "\nLine was ignored. Please correct this line, it should have exactly one '='.");
				continue;
			}

			// remove trailing and leading whitespaces
			value = propertyValue[1].trim();
			// remove trailing quotation marks
			if (value.charAt(0) == '"'
					&& value.charAt(value.length() - 1) == '"') {
				value = value.substring(1, value.length() - 1);
			}

			//check different keywords:
			//********************contigs*********************
			if (propertyValue[0].matches("contigs")) {
				if (contigs == null) {
					File file = new File(value);
					// if not existant, try relative path
					if (!file.exists()) {
						file = new File(currentWorkingDirectory
								+ File.separator + value);
					} else {
						contigs = file;
					}
					//if still not existant, give error
					if (!file.exists()) {
						System.err.println("Error: " + line
								+ "\nFile not found");
					} else {
						contigs = file;
					}
				} else { //contigs were allready specified
					System.err
							.println("Property contigs was given more often than once. Using first occurrence.");
				}

			}

			//********************reference*********************
			if (propertyValue[0].matches("reference")) {
				File file = new File(value);
				// if not existant, try relative path
				if (!file.exists()) {
					file = new File(currentWorkingDirectory + File.separator
							+ value);
				} else {
					references.add(file);
				}
				//if still not existant, give error
				if (!file.exists()) {
					System.err.println("Error: " + line + "\nFile not found");
				} else {
					references.add(file);
				}
			}

			//********************newicktree*********************
			if (propertyValue[0].matches("newicktree")) {
				if (phylogeneticTree == null) {
					// rolands newicktree parser
					try {
						phylogeneticTree = new MultifurcatedTree(value);
					} catch (Exception e) {
						System.err.println("Error: " + line + "\n" + e.getMessage());
						phylogeneticTree=null;
					}
				} else {
					System.err
							.println("Found several newick trees. Using the first one!");
				}

			}

			//********************projectdir*********************
			if (propertyValue[0].matches("projectdir")) {
				if (projectDir == null) {
					File dir = new File(value);
					if(dir.isDirectory() && dir.canWrite()) {
					projectDir = dir;
					} else {
						System.err.println("Error: " + line + "\nNo directory, or dir not writable.");
					}
				} else {
					System.err
							.println("Property projectDir should occur only once. Using first occurrence.");
				}
			}

		} // file has been parsed completely

		// fill helping hash map which contains the names of the references and the distance to the contigs genome
		if (phylogeneticTree != null && contigs != null) {
			this.contigsToReferencesTreeDistanceHashmap = getTreeDistances(
					getFileNameWithoutExtension(contigs), phylogeneticTree);
		}

		if (contigsToReferencesTreeDistanceHashmap != null) {
			// check if all references occur
			for (int i = 0; i < references.size(); i++) {
				if (!contigsToReferencesTreeDistanceHashmap
						.containsKey(getFileNameWithoutExtension(references
								.get(i)))) {
					System.err.println("Reference not in the tree: "
							+ getFileNameWithoutExtension(references.get(i)));
					// TODO set some distance if reference is missing in the
					// tree
				}
			}
		} else {
			// if the distances cannot be aquired, set uniform distances of one
			System.err
					.println("Distances could not be aquired. Setting each reference to distance 1.");
			for (int i = 0; i < references.size(); i++) {
				contigsToReferencesTreeDistanceHashmap.put(
						getFileNameWithoutExtension(references.get(i)), 1.);
			}

		}

		// sanity checks
		boolean parsedSuccessfully = true;

		// failsafe. if not set, take the directory of the project file as
		// project directory
		if (projectDir == null) {
			projectDir = new File(f.getParent());
		}

		// is a contigs file given?
		if (contigs == null) {
			parsedSuccessfully = false;
			System.err
					.println("No contig file give. Please specify contigs=/path/file.fas.");
		} else {
			if (!contigs.exists() || !contigs.canRead()) {
				System.err.println("Contig file is not readable:\n"
						+ contigs.getCanonicalFile());
				parsedSuccessfully = false;
			}
		}

		// are references given?
		if (references.isEmpty()) {
			parsedSuccessfully = false;
			System.err
					.println("No reference file was given. Please specify at least one line with reference=/path/file.fas");
		} else {
			for (int i = 0; i < references.size(); i++) {
				if (!references.get(i).exists() || !references.get(i).canRead()) {
					parsedSuccessfully = false;
					System.err.println("Can't open reference file:\n"
							+ references.get(i).getCanonicalFile());
				}
			}
		}

		return (parsedSuccessfully);
	}

	
	
	/** Extracts the pairwise distances from the contigs genome to every reference from the tree.
	 * @param contigsLeafLabel the name of the contigs genome. should be filename without extension.
	 * @param phylogeneticTree the phylogenetic tree parsed from newick format.
	 * @return the pairwise distances in a hash map (key=reference, label=distance), but null if the contigsleaflabel was not present as a leaf in the tree.
	 * 
	 */
	public HashMap<String, Double> getTreeDistances(String contigsLeafLabel,
			MultifurcatedTree phylogeneticTree) {
		HashMap<String, Double> contigToRefDist = new HashMap<String, Double>();

		// traverse tree to get edge weights

		// the leaf collector gives a list with all leaves. these correspond to
		// the species in the tree.
		LeafCollector<String> lc = new LeafCollector<String>();
		phylogeneticTree.bottomUp(lc);
		Vector<Node<String>> leaves = lc.getLeaves();

		// first we find the leaf with a label that matches the contigs and
		// remove it from the list.
		Node<String> contigsLeaf = null;
		for (int i = 0; i < leaves.size(); i++) {
			if (leaves.get(i).getName().matches(contigsLeafLabel)) {
				contigsLeaf = leaves.get(i);
				leaves.remove(i);
				break;
			}
		}
		if (contigsLeaf == null) {
			System.err
					.println("The contigs genome\n"
							+ contigsLeafLabel
							+ " was not found as a leaf in the tree.\n"
							+ "Make sure that one leaf of the tree has a label that is the same as the filename\n"
							+ "of the contigs file without extension.\n");
			return null;
		}

		// store the distances from the contig to each ancestor node in a
		// hashmap.
		HashMap<Node<String>, Double> distanceContigToNode = new HashMap<Node<String>, Double>();
		double distance = contigsLeaf.getIncomingEdgeLength();
		while (contigsLeaf.getParent() != null) {
			contigsLeaf = contigsLeaf.getParent();
			distanceContigToNode.put(contigsLeaf, distance);
			distance += contigsLeaf.getIncomingEdgeLength();
		}
		distanceContigToNode.put(contigsLeaf, distance);

		// now go with all other leaves to their parents until contigs and other
		// leaf share a common ancestor.
		// add the distances and store the distance in the resulting hashmap
		Node<String> node = null;
		String species = null;
		for (int i = 0; i < leaves.size(); i++) {
			// for all other species
			node = leaves.get(i);
			species = node.getName();
			distance = node.getIncomingEdgeLength();
			// go up in the tree and add the edge distances
			while (node.getParent() != null) {
				node = node.getParent();

				// until the node is shared with one of the contigs ancestors
				if (distanceContigToNode.containsKey(node)) {
					break;
				}
				// if not, add the incoming distance and go to the parent.
				distance += node.getIncomingEdgeLength();
			}

			// add this distance to the result
			contigToRefDist.put(species,
					(distanceContigToNode.get(node) + distance));
		}
		return contigToRefDist;

	}

	/**
	 * Tries to generate the matches between contigs and each reference genome.
	 * After that the results are cached onto the file system. The next time
	 * this method is called the cached results are used, if possible.
	 * 
	 * The method sets the contigsToReferencesMatchesList and the
	 * contigsToReferencesDistanceList. First contains a vector, each element
	 * containing matches to a reference genome and the second contains the
	 * normalized tree distance for the references distinguishable by their index.
	 * Normalized means that the smallest distance will be one, all other are scaled accordingly.
	 * 
	 * @return if it was successful.
	 */
	public boolean generateMatches() {
		boolean success = true;

		//don't know if this works, should set the workind directory to the project dir.
		System.setProperty("user.dir", projectDir.getAbsolutePath());

		FastaFileReader contigsFasta = new FastaFileReader(contigs);
		try {
			contigsFasta.scanContents(true);
		} catch (IOException e) {
			System.err.println("Could not open contigs for reading:\n"
					+ contigs.getName());
			success = false;
			// if the contigs are not available it makes no sense to match.
			// return here.
			return success;
		}

		FastaFileReader reference;

		// for all reference files
		// check if there are chached matches
		for (int i = 0; i < references.size(); i++) {

			File output = new File(projectDir + File.separator
					+ getFileNameWithoutExtension(contigs) + "--"
					+ getFileNameWithoutExtension(references.get(i)) + ".r2c");

			if (output.exists()) {
				System.out.println(output.getName()
						+ " was cached.\nUsing cached version.");
				AlignmentPositionsList cache = new AlignmentPositionsList();
				try {
					cache.readFromFile(output);
				} catch (IOException e) {
					System.err.println("Could not read cached matches from:\n"
							+ output.getName());
					success = false;
				}
				if (!cache.isEmpty()) {
					contigsToReferencesMatchesList.add(cache);
					// Double.NaN is a marker for not set
					double distance = Double.NaN;
					if (contigsToReferencesTreeDistanceHashmap
							.containsKey(getFileNameWithoutExtension(references
									.get(i)))) {
						distance = contigsToReferencesTreeDistanceHashmap
								.get(getFileNameWithoutExtension(references
										.get(i)));
					}
					contigsToReferencesTreeDistanceList.add(distance);
				}
				continue;
			} else {
				System.out.println("Matching contigs against: "
						+ getFileNameWithoutExtension(references.get(i)));

				try {
					reference = new FastaFileReader(references.get(i));
					reference.scanContents(true);
				} catch (IOException e) {
					System.err
							.println("Could not open references file for matching:\n"
									+ references.get(i).getName());
					success = false;
					// if one reference is missing, skip it.
					continue;
				}

				QGramIndex qi = new QGramIndex();
				qi.generateIndex(reference);

				QGramFilter qf = new QGramFilter(qi, contigsFasta);
				AlignmentPositionsList apl = qf.match();

				try {
					apl.writeToFile(output);
				} catch (IOException e) {
					System.err
							.println("Could not write the match results to a file. "
									+ e.getLocalizedMessage());
				}

				if (!apl.isEmpty()) {
					contigsToReferencesMatchesList.add(apl);
					// Double.NaN is a marker for not set
					double distance = Double.NaN;
					if (contigsToReferencesTreeDistanceHashmap
							.containsKey(getFileNameWithoutExtension(references
									.get(i)))) {
						distance = contigsToReferencesTreeDistanceHashmap
								.get(getFileNameWithoutExtension(references
										.get(i)));
					}
					contigsToReferencesTreeDistanceList.add(distance);

				}

			}
		}

		// generate statistics -> get repeat count of the matches
		for (int i = 0; i < contigsToReferencesMatchesList.size(); i++) {
			contigsToReferencesMatchesList.get(i).generateNewStatistics();
		}

		// TODO check if all values are NAN
		// normalize the tree distances
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		// find minimum and maximum
		for (int i = 0; i < contigsToReferencesTreeDistanceList.size(); i++) {
			if (contigsToReferencesTreeDistanceList.get(i) != Double.NaN) {
				if (contigsToReferencesTreeDistanceList.get(i) < min) {
					min = contigsToReferencesTreeDistanceList.get(i);
				}
				if (contigsToReferencesTreeDistanceList.get(i) > max) {
					max = contigsToReferencesTreeDistanceList.get(i);
				}

			}
		}

		// failsafe. if the minimum gets negative, move all the values such that
		// the minimum will be around 0.1
		double offset = 0;
		if (min < 0) {
			offset = -min + 0.1;
			min += 0.1;
		}

		// normalize values such that the closest leaf has distance 1
		double value = 0;
		for (int i = 0; i < contigsToReferencesTreeDistanceList.size(); i++) {
			if (contigsToReferencesTreeDistanceList.get(i) == Double.NaN) {
				// if the value is not known. set it to the maximum distance
				value = max;
			} else {
				value = contigsToReferencesTreeDistanceList.get(i);
			}
			value += offset;
			// normalize
			value /= min;
			contigsToReferencesTreeDistanceList.set(i, value);
		}

		return success;

	}

	/**
	 * Sort the contigs, or create a layout graph from the matches.
	 */
	public void sortContigs() {
		if (!contigsToReferencesMatchesList.isEmpty()) {
			treebasedSorter = new TreebasedContigSorter(
					contigsToReferencesMatchesList, new File(projectDir + File.separator
							+ getFileNameWithoutExtension(contigs)+ "_LayoutGraph.neato"));

			//set the tree weights. if these are not given, all distances are set to 1.
			treebasedSorter.setTreeWeights(contigsToReferencesTreeDistanceList);
			
			//fill the weight matrix with the projected contigs, based on the matches
			treebasedSorter.fillWeightMatrix();
			
			//this method computes a path, or a Layout GRaph 
			treebasedSorter.findPath();

			//calculate the 'optimal' path
//			TreebasedContigSorterExact exact = new TreebasedContigSorterExact(contigsToReferencesMatchesList);
//			exact.setTreeWeights(contigsToReferencesDistanceList);
//			exact.fillWeightMatrix();
//			exact.findPath();
		} else {
			System.err.println("No matches found, can't sort contigs.");
		}
	}

	/**
	 * Gives the filename of a file without extension. If the file object is
	 * empty, or belongs to a directory, then null is returned.
	 * 
	 * @param file
	 *            file object of which we need the filename without extension
	 * @return filename without extension
	 */
	public static String getFileNameWithoutExtension(File file) {
		if (file == null || file.isDirectory()) {
			return null;
		}

		// remove the last dot plus extension
		String filename = file.getName();
		int lastDotPosition = filename.lastIndexOf('.');
		if (lastDotPosition > 0) {
			filename = filename.substring(0, lastDotPosition);
		}
		return filename;
	}

	public class LeafCollector<T> implements NodeVisitor<Node<T>> {
		private Vector<Node<T>> leaves;

		public LeafCollector() {
			leaves = new Vector<Node<T>>();
		}

		@Override
		public void visit(Node<T> node) {
			if (node.isLeaf()) {
				leaves.add(node);
			}
		}

		public Vector<Node<T>> getLeaves() {
			return leaves;
		}

	}
}
