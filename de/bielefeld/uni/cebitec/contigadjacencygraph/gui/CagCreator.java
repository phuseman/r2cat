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
 */
public class CagCreator {

	private LayoutGraph graph;
	private Vector<DNASequence> contigs;
	private String[] listData;
	private ArrayList<CagEventListener> listeners;
	private Vector<AdjacencyEdge>[] leftNeighbours;
	private Vector<AdjacencyEdge>[] rightNeighbours;
	private String currentContig;
	private String contigId;
	private int contigIndex;
	private long contigSize = 0;
	private DNASequence contig;
	private boolean contigIsRepetitiv = false;
	private boolean contigIsReverse = false;
	private DNASequence[] fiveMostLikleyLeftNeighbours;
	private DNASequence[] fiveMostLikleyRightNeighbours;

	// private CAGWindow window;
	public CagCreator(LayoutGraph g) {
		this.graph = g;
		listeners = new ArrayList<CagEventListener>();
		leftAndRightNeighbour();
		createContigList();
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
								"/homes/aseidel/testdaten/treecat_project/Corynebacterium_urealyticum_DSM_7109_454LargeContigs_renumbered_repeatmarked.tcp"));
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

			CagCreator model = new CagCreator(completeGraph);
			Controller controller = new Controller(model);

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
			int i = e.geti();
			int j = e.getj();

			// wenn diese Kante der linke konnektor von i ist
			if (e.isLeftConnectori()) {
				// dann ist sie die verbindung zum linken Knoten von i
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

	/*
	 * Wenn ich die 5 wahrscheinlichsten Nachbarn aus der Map hole sollten auch
	 * noch die Informationen zu dem Contig geholt werden, damit spaeter das
	 * Contig auch richt dargestellet werden kann.
	 */
	public DNASequence[] calculateFiveMostLikleyLeftNeighbours(int index) {
		fiveMostLikleyLeftNeighbours = new DNASequence[5];


		int t = 0;
		double support = 0;
		boolean isReversei = false;
		boolean isReversej = false;
		DNASequence neighbour;
		String name;
		long length;
		boolean isRepetitiv = false;
		boolean isReverse = false;

		for (Iterator<AdjacencyEdge> iterator = leftNeighbours[index]
				.iterator(); iterator.hasNext();) {

			AdjacencyEdge edge = iterator.next();

			int nodeI = edge.geti();
			int nodeJ = edge.getj();

			if (edge.isLeftConnectori() == false
					&& edge.isRightConnectori() == false) {
				isReversej = true;
			}
			if (edge.isLeftConnectori() == true) {
				if (edge.isRightConnectori() == true) {
					isReversei = true;
				} else {
					isReversei = true;
					isReversej = true;
				}
			}
			if (nodeI == contigIndex) {
				support = edge.getRelativeSupportj();
				System.out.println("Support of "+nodeJ+" is: "+support*100);
				neighbour = contigs.get(nodeJ);
				name = neighbour.getId();
				length = neighbour.getSize();
				isRepetitiv = neighbour.isRepetitive();
				if (isReversej == true) {
					isReverse = true;
				}
			} else {
				support = edge.getRelativeSupporti();
				System.out.println("Support of "+nodeI+" is: "+support*100);
				neighbour = contigs.get(nodeI);
				name = neighbour.getId();
				length = neighbour.getSize();
				isRepetitiv = neighbour.isRepetitive();
				if (isReversei == true) {
					isReverse = true;
				}
			}
			// TODO und der support muss bestimmte groesse haben  support*1000 >2
			if (t < 5 && (support*100) >1 ) {
				fiveMostLikleyLeftNeighbours[t] = new DNASequence(name, length,
						isRepetitiv, isReverse);
			}
			t++;
		}

		return fiveMostLikleyLeftNeighbours;

	}
	public DNASequence[] calculateFiveMostLikleyRightNeighbours(int index) {
		fiveMostLikleyRightNeighbours = new DNASequence[5];
		
		int t = 0;
		double support = 0;
		boolean isReversei = false;
		boolean isReversej = false;
		DNASequence neighbour;
		String name;
		long length;
		boolean isRepetitiv = false;
		boolean isReverse = false;
		
		for (Iterator<AdjacencyEdge> iterator = rightNeighbours[index]
		                                                       .iterator(); iterator.hasNext();) {
			
			AdjacencyEdge edge = iterator.next();
			
			int nodeI = edge.geti();
			int nodeJ = edge.getj();
			
			if (edge.isLeftConnectori() == false
					&& edge.isRightConnectori() == false) {
				isReversej = true;
			}
			if (edge.isLeftConnectori() == true) {
				if (edge.isRightConnectori() == true) {
					isReversei = true;
				} else {
					isReversei = true;
					isReversej = true;
				}
			}
			if (nodeI == contigIndex) {
				System.out.println("Support of "+nodeJ+" is: "+support*100);
				support = edge.getRelativeSupportj();
				neighbour = contigs.get(nodeJ);
				name = neighbour.getId();
				length = neighbour.getSize();
				isRepetitiv = neighbour.isRepetitive();
				if (isReversej == true) {
					isReverse = true;
				}
			} else {
				support = edge.getRelativeSupporti();
				System.out.println("Support of "+nodeI+" is: "+support*100);
				neighbour = contigs.get(nodeI);
				name = neighbour.getId();
				length = neighbour.getSize();
				isRepetitiv = neighbour.isRepetitive();
				if (isReversei == true) {
					isReverse = true;
				}
			}
			// TODO und der support muss bestimmte groesse haben
			if (t < 5 && (support*100) >1 ) {
				fiveMostLikleyRightNeighbours[t] = new DNASequence(name, length,
						isRepetitiv, isReverse);				
			}
			t++;
		}
		
		return fiveMostLikleyRightNeighbours;
		
	}

	/**
	 * Create a List of all Nodes(Contigs) of LayoutGraph
	 */
	private String[] createContigList() {

		contigs = graph.getNodes();
		listData = new String[contigs.size()];

		for (int i = 0; i < contigs.size(); i++) {
			DNASequence contig = contigs.get(i);
			listData[i] = contig.getId();
		}
		return listData;
	}

	/*
	 * Method to select some informations for the current contig. They will be
	 * displayed in the view of the contig
	 */
	private void idOfCurrentContig(String name) {

		contigId = currentContig.replace("Contig ", "");

		for (DNASequence c : contigs) {
			contigIndex = contigs.indexOf(c);
			if (c.getId().equals(contigId)) {

				contigSize = c.getSize();
				contigIsRepetitiv = c.isRepetitive();
				break;
			}
		}
	}

	/*
	 * Send an event, if the user selected a contig
	 */
	public void sendCurrentContig() {

		CagEvent event = new CagEvent(EventType.EVENT_CHOOSED_CONTIG,
				currentContig, contigSize, contigIsRepetitiv, contigIsReverse);
		/*
		 * CagEvent event = new CagEvent(EventType.EVENT_CHOOSED_CONTIG, new
		 * Contig(contigId)); solls spaeter mal werden dazu in contig klasse
		 * TODO
		 */
		fireEvent(event);
	}

	/**
	 * Send an event. If the user selected an node(contig) the neighbours will
	 * be caculated. This Event carries an Array with the most likely left
	 * neighbours.
	 */
	public void sendLeftNeighbours() {
		System.out.println("linke nachbarn ");

		CagEvent event = new CagEvent(EventType.EVENT_SEND_LEFT_NEIGHBOURS,
				calculateFiveMostLikleyLeftNeighbours(contigIndex));
		fireEvent(event);
	}

	/**
	 * Send an event. If the user selected an node(contig) the neighbours will
	 * be caculated. This Event carries an Array with the most likely right
	 * neighbours.
	 */
	public void sendRightNeighbours() {
		System.out.println("rechte nachbarn ");

		CagEvent event = new CagEvent(EventType.EVENT_SEND_RIGHT_NEIGHBOURS,
				calculateFiveMostLikleyRightNeighbours(contigIndex));
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

	public Vector<AdjacencyEdge>[] getLeftNeighbours() {
		return leftNeighbours;
	}

	public Vector<AdjacencyEdge>[] getRightNeighbours() {
		return rightNeighbours;
	}

	/*
	 * This list has all names of contigs, which will be display in the
	 * scrollbar / window the user is able to select a contig, such that this
	 * contig will be display at the central contig
	 */
	public String[] getListData() {
		this.createContigList();
		return listData;
	}

	/*
	 * Return the current Contig Id/Name
	 */
	public String getCurrentContig() {
		return currentContig;
	}

	/*
	 * TODO Wenn schon einer dieser Nachbarn ausgewählt wurde sollte dieser
	 * Nachbar eine andere Erscheinung haben als alle anderen Nachbarn.
	 */
	public void changeContigs(String currentContig, String isReverse) {

		this.currentContig = currentContig;
		this.contigIsReverse = Boolean.parseBoolean(isReverse);
		idOfCurrentContig(currentContig);

		sendCurrentContig();
		sendLeftNeighbours();
		sendRightNeighbours();
	}
}