package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
	private int neighbourNumber = 5;
	
	private Vector<AdjacencyEdge> selectedContigs;
	private ArrayList<CagEventListener> listeners;
	private long maxSizeOfContigs;
	private long minSizeOfContigs;
	private double minSupport;
	private double maxSupport;


	// private CAGWindow window;
	public CagCreator(LayoutGraph g) {
		this.graph = g;
		listeners = new ArrayList<CagEventListener>();
		selectedContigs = new Vector<AdjacencyEdge>();
		leftAndRightNeighbour();
		createContigList();
		calculateMinSizeOfContigs(contigs);
		calculateMaxSizeOfContigs(contigs);
		calculateMaxSupport(g);
		calculateMinSupport(g);
		System.out.println("min support "+minSupport+" max support "+maxSupport);
	}

	/*
	 * Diese Main methode verschwindet zum Schluss!
	 */
	public static void main(String[] args) {

		TreebasedContigSorterProject project = new TreebasedContigSorterProject();

		project.register(new SimpleProgressReporter());
		try {
			try {

				boolean projectParsed = project
						.readProject(new File(
						// "/homes/aseidel/testdaten/treecat_project/Corynebacterium_urealyticum_DSM_7109_454LargeContigs_renumbered_repeatmarked.tcp"));
						//"/homes/aseidel/testdaten/treecat_project/Corynebacterium_urealyticum_DSM_7109_454LargeContigs.tcp"));
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
	 * Diese Hashmaps speichern die linken und rechten Nachbarn aller Contigs
	 */
	private void leftAndRightNeighbour() {

		leftNeighbours = new Vector[graph.getNodes().size()];
		rightNeighbours = new Vector[graph.getNodes().size()];

		for (int i = 0; i < graph.getNodes().size(); i++) {
			leftNeighbours[i] = new Vector<AdjacencyEdge>();
			rightNeighbours[i] = new Vector<AdjacencyEdge>();
		}

		// fuer alle Kanten im layout Graphen
		for (AdjacencyEdge e : graph.getEdges()) {

			// hole fuer die aktuelle Kante den Knoten i und j
			int i = e.geti();// ist das der Index?
			int j = e.getj();

			// wenn die aktuelle Kante der linke konnektor von i
			if (e.isLeftConnectori()) {
				// dann gehört der Knoten zu den linken Nachbarn
				leftNeighbours[i].add(e);
				// wenn nicht dann ist sie die verbindung zum rechten Knoten i
			} else {
				rightNeighbours[i].add(e);
			}

			// wenn diese Kante der linke konnektor von j ist
			if (e.isLeftConnectorj()) {
				leftNeighbours[j].add(e);
			} else {
				rightNeighbours[j].add(e);
			}
		}
		for (int x = 0; x < graph.getNodes().size(); x++) {
			Collections.sort(leftNeighbours[x]);
			Collections.sort(rightNeighbours[x]);
		}
	}

	public LayoutGraph getGraph() {
		return graph;
	}
	
	
	/**
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
	
	private long calculateMaxSizeOfContigs (DNASequence[] contigList){
		
		maxSizeOfContigs = 0;
		
		for (DNASequence dnaSequence : contigList) {
			long size = dnaSequence.getSize();
			if (size > maxSizeOfContigs){
				maxSizeOfContigs = size;
			}
		}		
		return maxSizeOfContigs;
	}
	
	private long calculateMinSizeOfContigs (DNASequence[] contigList){
		
		minSizeOfContigs = contigList[0].getSize();
		
		for (DNASequence dnaSequence : contigList) {
			long size = dnaSequence.getSize();
			if (size < minSizeOfContigs){
				minSizeOfContigs = size;
			}
		}		
		return minSizeOfContigs;
	}
private double calculateMinSupport (LayoutGraph graph){
		
		minSupport = graph.getEdges().firstElement().getSupport();
		
		Vector<AdjacencyEdge> kanten = graph.getEdges();
		for (AdjacencyEdge adjacencyEdge : kanten) {
			double support = adjacencyEdge.getSupport();
			if(support < minSupport){
				minSupport  = support;
			}
		}
		return minSupport;
	}
	
	private double calculateMaxSupport(LayoutGraph graph){
		maxSupport = 0;
		
		Vector<AdjacencyEdge> kanten = graph.getEdges();
		for (AdjacencyEdge adjacencyEdge : kanten) {
			double support = adjacencyEdge.getSupport();
			if(support > maxSupport){
				maxSupport  = support;
			}
		}
		return maxSupport;
	}

	public double getMinSupport() {
		return minSupport;
	}

	public double getMaxSupport() {
		return maxSupport;
	}

	
	/*
	 * TODO 
	 * Hier auch noch mal schauen ob ich wirklich so viele Variblen als
	 * Klassenvariblen def muss. Währe glaube ich besser wenn ich das local
	 * gestallten würde. Auch währe es besser wenn ich hier was basteln würde,
	 * womit ich indices und neighbours gleichzeitig zurückgeben kann.
	 */

	

	private Vector<AdjacencyEdge> calculateFiveMostLikleyRightNeighbours(
			int cContigIndex, boolean isReverse) {

		/*
		 * hier muss unterschieden werden, ob das zentrale Contig reverse
		 * dargestellt wird dann muss ich den nachbarnvektor entsprechend
		 * waehelen die Richtung fuer das Contig bestimmt die sicht auf die
		 * nachbarn die Spitze(rechter connector) zeigt wenn es reverse ist ja
		 * in die andere Richtung
		 */
		Vector<AdjacencyEdge> test = isReverse ? leftNeighbours[cContigIndex]
				: rightNeighbours[cContigIndex];

		Vector<AdjacencyEdge> fiveMostLikleyRightNeighbours = new Vector<AdjacencyEdge>();
		DNASequence neighbourContigObject;

		double support;
		
		int counter = 0;
		int terminator = 0;

		if (neighbourNumber < test.size()) {
			terminator = neighbourNumber;
		} else if (neighbourNumber > test.size()) {
			terminator = test.size();
		}

		for (AdjacencyEdge edge : test) {

			if(counter<terminator){

				if (edge.geti() == cContigIndex) {// j ist der Nachbar
					neighbourContigObject = edge.getContigj();
					support = edge.getRelativeSupportj();
				} else {// i ist der Nachbar
					neighbourContigObject = edge.getContigi();
					support = edge.getRelativeSupportj();
				}

				neighbourContigObject
						.setSupportComparativeToCentralContig(support);

				fiveMostLikleyRightNeighbours.add(edge);
				counter++;
			}
			if(counter == terminator){

				break;
			}
		}
		return fiveMostLikleyRightNeighbours;

	}

	private Vector<AdjacencyEdge> calculateFiveMostLikleyLeftNeighbours(
			int centralContigIndex, boolean currentContigIsReverse) {
		
		System.out.println("Berechne linke Nachbarn");
		/*
		 * hier muss unterschieden werden, ob das zentrale Contig reverse
		 * dargestellt wird dann muss ich den nachbarnvektor entsprechend
		 * waehelen die Richtung fuer das Contig bestimmt die sicht auf die
		 * nachbarn die Spitze(rechter connector) zeigt wenn es reverse ist ja
		 * in die andere Richtung
		 */
		Vector<AdjacencyEdge> test = currentContigIsReverse ? rightNeighbours[centralContigIndex]
				: leftNeighbours[centralContigIndex];

		Vector<AdjacencyEdge> fiveMostLikleyLeftNeighbours = new Vector<AdjacencyEdge>();
		DNASequence neighbourContigObject;

		double support;

		int counter = 0;
		int terminator = 0;
		if (neighbourNumber < test.size()) {
			terminator = neighbourNumber;
		} else if (neighbourNumber > test.size()) {
			terminator = test.size();
		}

		for (AdjacencyEdge edge : test) {
			if(counter<terminator){
				if (edge.geti() == centralContigIndex) {// j ist der Nachbar
					neighbourContigObject = edge.getContigj();
					support = edge.getRelativeSupportj();
				} else {// i ist der Nachbar
					neighbourContigObject = edge.getContigi();
					support = edge.getRelativeSupportj();
				}

				neighbourContigObject
						.setSupportComparativeToCentralContig(support);

				fiveMostLikleyLeftNeighbours.add(edge);
				counter ++;
				
			}
			if(counter == terminator){
				break;
			}
		}
		return fiveMostLikleyLeftNeighbours;

	}


	public Vector<AdjacencyEdge> addSelectedContig(AdjacencyEdge selectedEdge) {
		System.out.println("Vor dem hinzufügen der Kante "
				+ selectedContigs.size());
		for (Iterator<AdjacencyEdge> iterator = selectedContigs.iterator(); iterator
				.hasNext();) {
			AdjacencyEdge edge = iterator.next();

			if (!edge.equals(selectedEdge)) {
				selectedContigs.add(selectedEdge);
				break;
			}
		}
		if (selectedContigs.size() == 0) {
			selectedContigs.add(selectedEdge);
		}
		System.out.println("nach dem hinzufügen der Kante "
				+ selectedContigs.size());
		return selectedContigs;
	}

	public Vector<AdjacencyEdge> removeSelectedEdge(AdjacencyEdge selectedEdge) {

		boolean flag = false;
		System.out.println("Vor dem Löschen der Kante "
				+ selectedContigs.size());
		for (AdjacencyEdge edge : selectedContigs) {
			if (edge.equals(selectedEdge)) {

				selectedContigs.removeElement(selectedEdge);

				sendCurrentContig();
				sendLeftNeighbours();
				sendRightNeighbours();
				flag = true;
				break;
			}
		}
		System.out.println("Nach dem Löschen der Kante "
				+ selectedContigs.size());
		if (!flag) {
			javax.swing.JOptionPane.showMessageDialog(window, "Sorry.\n"
					+ "Can't remove this neighbour\n"
					+ "Probably you didn't selected this neighbour.");
		}
		return selectedContigs;
	}

	/*
	 * Send an event, if the user selected a contig
	 */
	public void sendCurrentContig() {
		System.out.println("sende aktuelles contig");
		CagEvent event = new CagEvent(EventType.EVENT_CHOOSED_CONTIG,
				currentContigObject, currentContigIndex, currentContigIsReverse);
		fireEvent(event);
	}

	/**
	 * Send an event. If the user selected an node(contig) the neighbours will
	 * be caculated. This Event carries an Array with the most likely left
	 * neighbours.
	 */
	public void sendLeftNeighbours() {
		System.out.println("sende linke nachbarn");
		CagEvent event = new CagEvent(EventType.EVENT_SEND_LEFT_NEIGHBOURS,
				calculateFiveMostLikleyLeftNeighbours(currentContigIndex,
						currentContigIsReverse));
		fireEvent(event);
	}

	/**
	 * Send an event. If the user selected an node(contig) the neighbours will
	 * be caculated. This Event carries an Array with the most likely right
	 * neighbours.
	 */
	public void sendRightNeighbours() {
		System.out.println("sende rechte nachbarn");
		CagEvent event = new CagEvent(EventType.EVENT_SEND_RIGHT_NEIGHBOURS,
				calculateFiveMostLikleyRightNeighbours(currentContigIndex,
						currentContigIsReverse));
		fireEvent(event);
	}

	/**
	 * Hier werden alle Klassen die sich registrieren in der ArrayList
	 * gespeichert.
	 */
	public void addEventListener(CagEventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Hier werden alle Klasse die sich registriert haben aus der ArrayList
	 * gelöscht.
	 */
	public void removeEventListener(CagEventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * "Feuert" die Briefe(Events) mit Inhalt (Daten) an alle Listener.
	 * 
	 * @param event
	 *            (Das Event, das gefeuert wird muss angegben werden.)
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

	public void setNeighbourNumber(int number) {
		this.neighbourNumber = number;
	}
	public long getMaxSizeOfContigs() {
		return maxSizeOfContigs;
	}

	public long getMinSizeOfContigs() {
		return minSizeOfContigs;
	}

	public void changeContigs(int index, boolean isReverse) {

		this.currentContigIndex = index;
		this.currentContigIsReverse = isReverse;
		this.currentContigObject = graph.getNodes().get(index);
		sendCurrentContig();
	}

}