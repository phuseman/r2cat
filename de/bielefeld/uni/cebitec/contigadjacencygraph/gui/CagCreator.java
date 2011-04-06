package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.naming.CannotProceedException;
import javax.swing.UIManager;

import de.bielefeld.uni.cebitec.common.SimpleProgressReporter;
import de.bielefeld.uni.cebitec.common.Timer;
import de.bielefeld.uni.cebitec.contigadjacencygraph.ContigAdjacencyGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.NeatoOutputType;
import de.bielefeld.uni.cebitec.qgram.DNASequence;
import de.bielefeld.uni.cebitec.treecat.TreebasedContigSorterProject;

/*
 * ist mein model 
 * hier werden die Daten- Zustands- und Anwendungslogik implementiert
 * 
 */
public class CagCreator {

	private static CAGWindow window;
	private static CagCreator model;

	private LayoutGraph graph;
	private DNASequence[] contigs;
	private Vector<AdjacencyEdge>[] leftNeighbours;
	private Vector<AdjacencyEdge>[] rightNeighbours;

	private int currentContigIndex;
	private boolean currentContigIsReverse = false;
	private DNASequence currentContigObject;
	//private int neighbourNumber = 5;

	private ArrayList<CagEventListener> listeners;
	private long maxSizeOfContigs;
	private long minSizeOfContigs;
	private double minSupport;
	private double maxSupport;
	private double[] meanForLeftNeigbours;
	private double[] sDeviationsForLeftNeigbours;
	private double[] meanForRightNeigbours;
	private double[] sDeviationsForRightNeigbours;

	// private CAGWindow window;
	public CagCreator(LayoutGraph g) {
		this.graph = g;
		listeners = new ArrayList<CagEventListener>();

		leftAndRightNeighbour();
		createContigList();
		
		calculateMinSizeOfContigs(contigs);
		calculateMaxSizeOfContigs(contigs);
		
		calculateMaxSupport(g);
		calculateMinSupport(g);

		calculateMeanAndSDeviationForLeftNeigbours(leftNeighbours);
		calculateMeanAndSDeviationForRightNeigbours(rightNeighbours);
	}


	public static void main(String[] args) {

		TreebasedContigSorterProject project = new TreebasedContigSorterProject();
		
		project.register(new SimpleProgressReporter());
		try {
			try {

				boolean projectParsed = project
						.readProject(new File(
						// "/homes/aseidel/testdaten/treecat_project/Corynebacterium_urealyticum_DSM_7109_454LargeContigs_renumbered_repeatmarked.tcp"));
//								 "/homes/aseidel/testdaten/treecat_project/Corynebacterium_urealyticum_DSM_7109_454LargeContigs.tcp"));
								"/homes/aseidel/testdaten/perfekt/Corynebacterium_urealyticum_DSM_7109_454LargeContigs_renumbered_repeatmarked.tcp"));
				
				if (!projectParsed) {
					System.err
							.println("The given project file was not sucessfully parsed");
					System.exit(1);
				}

			} catch (IOException e) {
				System.err
						.println("The given project file was not sucessfully parsed:\n"
								+ e.getMessage());
				System.exit(1);
			}

			LayoutGraph layoutGraph;
			ContigAdjacencyGraph cag;
			LayoutGraph completeGraph;

			Timer t = Timer.getInstance();
			t.startTimer();
			t.startTimer();

			project.generateMatches();

			t.restartTimer("matches");

			layoutGraph = project.sortContigs();
			layoutGraph.writeLayoutAsNeato(new File("test.dot"),
					NeatoOutputType.ONENODE);

			cag = project.getContigAdjacencyGraph();
			completeGraph = cag.getCompleteGraph();
			// model = new CagCreator(layoutGraph);
			model = new CagCreator(completeGraph);

			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						UIManager
								.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
						// "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
						// UIManager.getCrossPlatformLookAndFeelClassName());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					window = new CAGWindow(model);
				}
			});

			t.stopTimer("sorting");

			t.stopTimer("Total time");
		} catch (CannotProceedException e) {
			System.err.println("Programm failed:\n" + e.getMessage());
		}

	}

	/*
	 * Separate edges of the graph in left and right edges of 
	 * each contig
	 */
	private void leftAndRightNeighbour() {

		leftNeighbours = new Vector[graph.getNodes().size()];
		rightNeighbours = new Vector[graph.getNodes().size()];

		for (int i = 0; i < graph.getNodes().size(); i++) {
			leftNeighbours[i] = new Vector<AdjacencyEdge>();
			rightNeighbours[i] = new Vector<AdjacencyEdge>();
		}

		// for all edges in the graph
		for (AdjacencyEdge e : graph.getEdges()) {

			// get indices for current edge
			int i = e.geti();
			int j = e.getj();

			// if current edge is the left connector of i
			if (e.isLeftConnectori()) {
				// then the node belong to the left neighbours
				leftNeighbours[i].add(e);
				// if not, then it belongs to the right neighbours
			} else {
				rightNeighbours[i].add(e);
			}

			// same with j
			if (e.isLeftConnectorj()) {
				leftNeighbours[j].add(e);
			} else {
				rightNeighbours[j].add(e);
			}
		}
		/*
		 * In the end each vector will be sort by its support
		 */
		for (int x = 0; x < graph.getNodes().size(); x++) {
			Collections.sort(leftNeighbours[x]);
			Collections.sort(rightNeighbours[x]);
		}
	}

	/*
	 * Create a List of all Nodes(Contigs) of LayoutGraph
	 */
	private DNASequence[] createContigList() {

		contigs = new DNASequence[graph.getNodes().size()];

		for (AdjacencyEdge e : graph.getEdges()) {

			int i = e.geti();
			if (contigs[i] == null) {
				contigs[i] = e.getContigi();
			}
		}
		return contigs;
	}

	/*
	 * Calculate the max size of a given list of contigs
	 */
	private long calculateMaxSizeOfContigs(DNASequence[] contigList) {

		maxSizeOfContigs = 0;

		for (DNASequence dnaSequence : contigList) {
			long size = dnaSequence.getSize();
			if (size > maxSizeOfContigs) {
				maxSizeOfContigs = size;
			}
		}
		return maxSizeOfContigs;
	}

	/*
	 * Calculate the min size of a given list of contigs
	 */
	private long calculateMinSizeOfContigs(DNASequence[] contigList) {

		minSizeOfContigs = contigList[0].getSize();

		for (DNASequence dnaSequence : contigList) {
			long size = dnaSequence.getSize();
			if (size < minSizeOfContigs) {
				minSizeOfContigs = size;
			}
		}
		return minSizeOfContigs;
	}

	/*
	 * Calculate the min support of all edges of the given graph
	 */
	private double calculateMinSupport(LayoutGraph graph) {

		minSupport = graph.getEdges().firstElement().getSupport();

		Vector<AdjacencyEdge> kanten = graph.getEdges();
		for (AdjacencyEdge adjacencyEdge : kanten) {
			double support = adjacencyEdge.getSupport();
			if (support < minSupport) {
				minSupport = support;
			}
		}
		return minSupport;
	}

	/*
	 * Calculate the max support of all edges of the given graph
	 */
	private double calculateMaxSupport(LayoutGraph graph) {
		maxSupport = 0;

		Vector<AdjacencyEdge> kanten = graph.getEdges();
		for (AdjacencyEdge adjacencyEdge : kanten) {
			double support = adjacencyEdge.getSupport();
			if (maxSupport < support) {
				maxSupport = support;
			}
		}
		return maxSupport;
	}


	/*
	 * Calculate Mean and s Deviation for all contigs in a given list
	 */
	private void calculateMeanAndSDeviationForLeftNeigbours(Vector<AdjacencyEdge>[] neighbours) {

		meanForLeftNeigbours = new double[graph.getNodes().size()];
		sDeviationsForLeftNeigbours = new double[graph.getNodes().size()];
		
		for (int i = 0; i < graph.getNodes().size(); i++) {
			Vector<AdjacencyEdge> neighbour = neighbours[i];

			double summe = 0;
			for (AdjacencyEdge adjacencyEdge : neighbour) {
				summe = summe + adjacencyEdge.getSupport();
			}
		
			meanForLeftNeigbours[i] = summe / (graph.getNodes().size()*2);
			
			double summe1 = 0;
			for (AdjacencyEdge adjacencyEdge : neighbour) {
				summe1 = summe1
						+ Math.pow(adjacencyEdge.getSupport()
								- meanForLeftNeigbours[i],2);
			}

			if (neighbour.size() > 1) {
				sDeviationsForLeftNeigbours[i] = Math
				.sqrt((1.0 / ((double)(graph.getNodes().size()*2)- 1.0 ) * (summe1)));
			}
			
		}
	}

	private void calculateMeanAndSDeviationForRightNeigbours(
			Vector<AdjacencyEdge>[] rightNeighbours) {

		meanForRightNeigbours = new double[graph.getNodes().size()];
		sDeviationsForRightNeigbours = new double[graph.getNodes().size()];

		for (int i = 0; i < graph.getNodes().size(); i++) {
			Vector<AdjacencyEdge> neighbourR = rightNeighbours[i];

			double summe = 0;

			for (AdjacencyEdge adjacencyEdge : neighbourR) {
				summe = summe + adjacencyEdge.getSupport();
			}

			meanForRightNeigbours[i] = summe / (graph.getNodes().size()*2);
			
			double summe2 = 0;
			for (AdjacencyEdge adjacencyEdge : neighbourR) {
				summe2 = summe2
						+ Math.pow(adjacencyEdge.getSupport()
								- meanForRightNeigbours[i],2);
			}
			
			if (neighbourR.size() > 1) {
				sDeviationsForRightNeigbours[i] = Math
						.sqrt((1.0 / ((double)(graph.getNodes().size()*2)- 1.0 ) * (summe2)));
			}
		}
	}

	
	/*
	 * If the central contig is reverse the right neighbours are the 
	 * left ones
	 */
	private Vector<AdjacencyEdge> fiveMostLikleyRightNeighbours(
			int cContigIndex, boolean isReverse) {

		Vector<AdjacencyEdge> test = isReverse ? leftNeighbours[cContigIndex]
				: rightNeighbours[cContigIndex];

		Vector<AdjacencyEdge> fiveMostLikleyRightNeighbours = new Vector<AdjacencyEdge>();
		
		fiveMostLikleyRightNeighbours = test;

		return fiveMostLikleyRightNeighbours;

	}

	/*
	 * If the central contig is reverse the left neighbours are the 
	 * right ones
	 */
	private Vector<AdjacencyEdge> fiveMostLikleyLeftNeighbours(
			int centralContigIndex, boolean currentContigIsReverse) {

		Vector<AdjacencyEdge> test = currentContigIsReverse ? rightNeighbours[centralContigIndex]
				: leftNeighbours[centralContigIndex];

		Vector<AdjacencyEdge> fiveMostLikleyLeftNeighbours = new Vector<AdjacencyEdge>();
		
		fiveMostLikleyLeftNeighbours = test;
		
		return fiveMostLikleyLeftNeighbours;

	}

	/*
	 * Send an event, if the user selected a contig
	 */
	public void sendCurrentContig() {

		CagEvent event = new CagEvent(EventType.EVENT_CHOOSED_CONTIG,
				currentContigObject, currentContigIndex, currentContigIsReverse);
		fireEvent(event);
	}

	/*
	 * Send an event. If the user selected an node(contig) the neighbours will
	 * be caculated. This Event carries an Array with the most likely left
	 * neighbours.
	 */
	public void sendLeftNeighbours() {

		CagEvent event = new CagEvent(EventType.EVENT_SEND_LEFT_NEIGHBOURS,
				fiveMostLikleyLeftNeighbours(currentContigIndex,
						currentContigIsReverse));
		fireEvent(event);
	}

	/*
	 * Send an event. If the user selected an node(contig) the neighbours will
	 * be caculated. This Event carries an Array with the most likely right
	 * neighbours.
	 */
	public void sendRightNeighbours() {

		CagEvent event = new CagEvent(EventType.EVENT_SEND_RIGHT_NEIGHBOURS,
				fiveMostLikleyRightNeighbours(currentContigIndex,
						currentContigIsReverse));
		fireEvent(event);
	}

	/*
	 * Save class (listener) which register itself 
	 */
	public void addEventListener(CagEventListener listener) {
		listeners.add(listener);
	}

	/*
	 * Delete class from the list
	 */
	public void removeEventListener(CagEventListener listener) {
		listeners.remove(listener);
	}

	/*
	 * Send events with data and eventtype
	 */
	private void fireEvent(CagEvent event) {

		ArrayList<CagEventListener> copyList = new ArrayList<CagEventListener>(
				listeners);
		for (CagEventListener listener : copyList) {
			listener.event_fired(event);

		}
	}

	/*
	 * This list has all names of contigs, which will be display in the
	 * scrollbar / window the user is able to select a contig, such that this
	 * contig will be display at the central contig
	 */
	public DNASequence[] getListData() {
		return contigs;
	}


	/*
	 * necessary to change the current contig and also the
	 * to get the neighbours of these contig 
	 */
	public void changeContigs(int index, boolean isReverse) {

		this.currentContigIndex = index;
		this.currentContigIsReverse = isReverse;
		this.currentContigObject = graph.getNodes().get(index);
		sendCurrentContig();
	}

	public long getMaxSizeOfContigs() {
		return maxSizeOfContigs;
	}

	public long getMinSizeOfContigs() {
		return minSizeOfContigs;
	}
	public double[] getMeanForLeftNeigbours() {
		return meanForLeftNeigbours;
	}

	public double[] getMeanForRightNeigbours() {
		return meanForRightNeigbours;
	}

	public double[] getsDeviationsForLeftNeigbours() {
		return sDeviationsForLeftNeigbours;
	}

	public double[] getsDeviationsForRightNeigbours() {
		return sDeviationsForRightNeigbours;
	}

	public double getMinSupport() {
		return minSupport;
	}

	public double getMaxSupport() {
		return maxSupport;
	}
	public LayoutGraph getGraph() {
		return graph;
	}
}