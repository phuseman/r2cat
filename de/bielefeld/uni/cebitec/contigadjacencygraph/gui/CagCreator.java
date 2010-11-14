package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.naming.CannotProceedException;
import javax.swing.UIManager;

import de.bielefeld.uni.cebitec.common.MiscFileUtils;
import de.bielefeld.uni.cebitec.common.SimpleProgressReporter;
import de.bielefeld.uni.cebitec.common.Timer;
import de.bielefeld.uni.cebitec.contigadjacencygraph.ContigAdjacencyGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;
import de.bielefeld.uni.cebitec.treecat.TreebasedContigSorterProject;

import de.bielefeld.uni.cebitec.contigadjacencygraph.gui.CagEventListener;

/*
 * ist mein model 
 * hier werden die Daten- Zustands- und Anwendungslogik implementiert
 */
public class CagCreator {

	private static LayoutGraph layoutGraph;
	private static ContigAdjacencyGraph cag;
	private static LayoutGraph completeGraph;

	private Vector<DNASequence> contigs;
	private String[] listData;
	private ArrayList<CagEventListener> listeners;
	private HashMap<Integer, Vector<AdjacencyEdge>> leftNeighbours;
	private HashMap<Integer, Vector<AdjacencyEdge>> rightNeighbours;
	private String currentContig;
	private HashMap contigInfo;
	private DNASequence contig;
	private long contigSize = 0;
	private boolean contigIsRepetitiv = false;

	// private CAGWindow window;
	public CagCreator() {
		listeners = new ArrayList<CagEventListener>();
		calculateNeighbourList();
		createContigList();
	}

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

			Timer t = Timer.getInstance();
			t.startTimer();
			t.startTimer();

			project.generateMatches();

			t.restartTimer("matches");

			layoutGraph = project.sortContigs();// noch ungenutzt

			cag = project.getContigAdjacencyGraph();
			completeGraph = cag.getCompleteGraph();

			CagCreator model = new CagCreator();
			Controller controller = new Controller(model);

			t.stopTimer("sorting");

			t.stopTimer("Total time");
		} catch (CannotProceedException e) {
			System.err.println("Programm failed:\n" + e.getMessage());
		}

	}

	/*
	 * Diese Hashmaps speichern die linken und rechten Nachbarn aller Contigs
	 * TODO muss noch aufgerufen werden
	 */
	private void calculateNeighbourList() {
		leftNeighbours = new HashMap<Integer, Vector<AdjacencyEdge>>();
		rightNeighbours = new HashMap<Integer, Vector<AdjacencyEdge>>();

		for (AdjacencyEdge e : completeGraph.getEdges()) {

			int i = e.geti();
			int j = e.getj();

			if (e.isLeftConnectori()) {
				if (!leftNeighbours.containsKey(i)) {
					leftNeighbours.put(i, new Vector<AdjacencyEdge>());
				}
				leftNeighbours.get(i).add(e);
			} else {
				if (!rightNeighbours.containsKey(i)) {
					rightNeighbours.put(i, new Vector<AdjacencyEdge>());
				}
				rightNeighbours.get(i).add(e);
			}

			if (e.isLeftConnectorj()) {
				if (!leftNeighbours.containsKey(j)) {
					leftNeighbours.put(j, new Vector<AdjacencyEdge>());
				}
				leftNeighbours.get(j).add(e);
			} else {
				if (!rightNeighbours.containsKey(j)) {
					rightNeighbours.put(j, new Vector<AdjacencyEdge>());
				}
				rightNeighbours.get(j).add(e);
			}

		}
		System.out.println(rightNeighbours);
	}

	/*
	 * erstellen einer Namensliste aller Contigs
	 */
	private String[] createContigList() {

		contigs = completeGraph.getNodes();
		listData = new String[contigs.size()];

		for (int i = 0; i < contigs.size(); i++) {
			DNASequence contig = contigs.get(i);
			listData[i] = contig.getId();
		}
		return listData;
	}


	private void informationOfCurrentContig(String name) {
		contigInfo = new HashMap<String, String>();
		if (currentContig.contains("Contig r")) {
			int contigId = new Integer(currentContig.replace("Contig r", ""))
					.intValue();
			//TODO abfrage von r!000 nur wie? habe string + int
			//contig = contigs.get(Integer.parseInt("r")+contigId);
		} else {
			int contigId = new Integer(currentContig.replace("Contig ", ""))
					.intValue();
			contig = contigs.get(contigId);

		}
			contigSize = contig.getSize();
			contigIsRepetitiv = contig.isRepetitive();
			System.out.println(contigSize);
			System.out.println(contigIsRepetitiv);

	}

	/*
	 * Send an event, if the user selected an contig TODO noch senden ob revers oder nicht
	 */
	public void sendCurrentContig() {
		informationOfCurrentContig(currentContig);
		CagEvent event = new CagEvent(EventType.EVENT_CHOOSED_CONTIG,
				currentContig,contigSize,contigIsRepetitiv );
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

	public HashMap<Integer, Vector<AdjacencyEdge>> getLeftNeighbours() {
		return leftNeighbours;
	}
	
	public HashMap<Integer, Vector<AdjacencyEdge>> getRightNeighbours() {
		return rightNeighbours;
	}

	/*
	 * This list has all contignames, which will be display in the scrollbar /
	 * window the user is able to select a contig, such that this contig will be
	 * display at the central contig
	 */
	public String[] getListData() {
		this.createContigList();
		return listData;
	}

	public String getCurrentContig() {
		return currentContig;
	}
	/*
	 * TODO Wenn das aktuelle Contig gewechselt hat, sollte nun dadurch das sich
	 * der Zustand vom Model geändert hat auf der Gui in der Mitte dieses
	 * spezifische Contig erscheinen. Sprich es sollte als Reverse, Normal oder
	 * Repetetiv erscheinen mit dem richtigen Namen drauf.
	 * 
	 * TODO Des weiteren sollten die besten 5 Nachbarn ermittelt werden erstelle
	 * Methode findTheBest5Neightbours
	 * 
	 * TODO Wenn schon einer dieser Nachbarn ausgewählt wurde sollte dieser
	 * Nachbar eine andere Erscheinung haben als alle anderen Nachbarn.
	 */
	public void setCurrentContig(String currentContig) {
		this.currentContig = currentContig;
		System.out.println("Changed contig. Current Contig is: "
				+ currentContig);
	}
}