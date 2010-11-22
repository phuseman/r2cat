package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.naming.CannotProceedException;

import de.bielefeld.uni.cebitec.common.SimpleProgressReporter;
import de.bielefeld.uni.cebitec.common.Timer;
import de.bielefeld.uni.cebitec.contigadjacencygraph.ContigAdjacencyGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;
import de.bielefeld.uni.cebitec.treecat.TreebasedContigSorterProject;

/*
 * ist mein model 
 * hier werden die Daten- Zustands- und Anwendungslogik implementiert
 * 
 * TODO heute 5 Warscheinlichsten Nachbarn links und rechts in Variablen verpacken
 * und an gui senden!
 * Button in label ändern!
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
	private TreeMap<Double, AdjacencyEdge> supportOfLeftNeighbours;
	private TreeMap<Double, AdjacencyEdge> supportOfRightNeighbours;
	private String currentContig;
	private DNASequence contig;
	private int contigId;
	private long contigSize = 0;
	private boolean contigIsRepetitiv = false;

	// private CAGWindow window;
	public CagCreator() {
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

			Timer t = Timer.getInstance();
			t.startTimer();
			t.startTimer();

			project.generateMatches();

			t.restartTimer("matches");

			layoutGraph = project.sortContigs();

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
	 * TODO Zur Zeit kann eine Kante sowohl Rechter als auch linker Konnektor
	 * sein soll das so bleiben?
	 */
	private void leftAndRightNeighbour() {
		leftNeighbours = new LinkedHashMap<Integer, Vector<AdjacencyEdge>>();
		rightNeighbours = new LinkedHashMap<Integer, Vector<AdjacencyEdge>>();

		// fuer alle Kanten im layout Graphen
		for (AdjacencyEdge e : completeGraph.getEdges()) {

			// hole fuer die aktuelle Kante den Knoten i und j
			int i = e.geti();
			int j = e.getj();

			// wenn diese Kante der linke konnektor von i ist
			if (e.isLeftConnectori()) {
				if (!leftNeighbours.containsKey(i)) {
					leftNeighbours.put(i, new Vector<AdjacencyEdge>());
				}
				// dann ist sie die verbindung zum linken Knoten von i
				leftNeighbours.get(i).add(e);
				// wenn nicht dann ist sie die verbindung zum rechten Knoten i
			} else {
				if (!rightNeighbours.containsKey(i)) {
					rightNeighbours.put(i, new Vector<AdjacencyEdge>());
				}
				rightNeighbours.get(i).add(e);
			}

			// wenn diese Kante der linke konnektor von j ist
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
		for (Integer id : rightNeighbours.keySet()) {
			// System.out.println("Right neighbours for id : "+id);
			for (AdjacencyEdge edge : rightNeighbours.get(id)) {
				// System.out.println(edge);
				// System.out.println(edge.getRelativeSupporti());
				// System.out.println(edge.getRelativeSupportj());
			}
		}

	}

	/*
	 * TODO Aus der HashMap rightNeighbours sollen alle rechten nachbarn
	 * ausgelesen werden.
	 * 
	 * Jede Kante wird durch zwei Knoten definiert (i,j) in right/leftNeigbours
	 * ist in der Hashmap ist der Knoten i/j mit einem Vektor von Kanten
	 * gespeichert. In dem Vektor sind Knoten (Vektoreinträge) mit den
	 * entsprechenden Nachbarkanten gespeichert 
	 * 0 (0,1) (0,13)(1,0)(11,0)
	 * 1 (1,3)(1,7)(9,1) usw. 
	 * der entsprechende Knoten kann auf beiden Seiten sein
	 * beide varianten sind gleichwertig (i,j)=(j,i) durch islefti/j kann die
	 * Orientierung des Contigs ermittelt werden.
	 */
	/* 
	 * Get the support of all right neighbours from the current contig
	 * Save them in a tree map, which automaticly sort the nodes by their support.
	 * Attention! The most likely neightbours are at the end!
	 */
	private TreeMap<Double, AdjacencyEdge> mostLikelyRightNeighbours() {

		supportOfRightNeighbours = new TreeMap<Double, AdjacencyEdge>();

		for (AdjacencyEdge edge : rightNeighbours.get(1)) {
			if (edge.isRightConnectori()) {
				supportOfRightNeighbours.put(edge.getRelativeSupporti(), edge);
			} else {
				supportOfRightNeighbours.put(edge.getRelativeSupportj(), edge);
			}
		}

		return supportOfRightNeighbours;
		// ! Achtung kl element ist das erste höchste das letzte
	}

	/* 
	 * Get the support of all left neighbours from the current contig
	 * Save them in a tree map, which automaticly sort the nodes by their support.
	 * Attention! The most likely neightbours are at the end!
	 */
	private TreeMap<Double, AdjacencyEdge> mostLikelyLeftNeighbours() {

		supportOfLeftNeighbours = new TreeMap<Double, AdjacencyEdge>();

		for (AdjacencyEdge edge : leftNeighbours.get(9)) {
			if (edge.isRightConnectori()) {
				supportOfLeftNeighbours.put(edge.getRelativeSupporti(), edge);
			} else {
				supportOfLeftNeighbours.put(edge.getRelativeSupportj(), edge);
			}
		}
		return supportOfLeftNeighbours;
		// ! Achtung kl element ist das erste höchste das letzte

	}

	/*
	 * Create a List of all Contigs
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

	/*
	 * Method to select some informations for the current contig.
	 * They will be displayed in the view of the contig
	 */
	private void informationOfCurrentContig(String name) {

		if (currentContig.contains("Contig r")) {
			int contigId = new Integer(currentContig.replace("Contig r", ""))
					.intValue();
			// TODO abfrage von r!000 nur wie? habe string + int
			// contig = contigs.get(Integer.parseInt("r")+contigId);
		} else {
			int contigId = new Integer(currentContig.replace("Contig ", ""))
					.intValue();
			contig = contigs.get(contigId);

		}
		//TODO Noch abfragen ob reverse oder nicht; default sollte nicht reverse sein.
		// das erste Contig, welches ausgewählt wird sollte in normal |> also in 5'-3'? angezeigt werden.
		contigSize = contig.getSize();
		contigIsRepetitiv = contig.isRepetitive();
		// System.out.println("contig"+contig.getId());
		// System.out.println(contigSize);
		// System.out.println(contigIsRepetitiv);

	}

	/*
	 * Send an event, if the user selected an contig TODO noch senden ob revers
	 * oder nicht
	 */
	public void sendCurrentContig() {

		informationOfCurrentContig(currentContig);
		CagEvent event = new CagEvent(EventType.EVENT_CHOOSED_CONTIG,
				currentContig, contigSize, contigIsRepetitiv);
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
	 * This list has all names of contigs, which will be display in the scrollbar /
	 * window the user is able to select a contig, such that this contig will be
	 * display at the central contig
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
	 * TODO Wenn das aktuelle Contig gewechselt hat, sollte nun dadurch
	 * der Zustand vom Model ändern; und dadurch auf der Gui in der Mitte dieses
	 * spezifische Contig erscheinen. Sprich es sollte als Reverse, Normal oder
	 * Repetetiv erscheinen mit dem richtigen Namen drauf.
	 * 
	 * TODO Wenn schon einer dieser Nachbarn ausgewählt wurde sollte dieser
	 * Nachbar eine andere Erscheinung haben als alle anderen Nachbarn.
	 */
	public void changeContigs(String currentContig) {
		this.currentContig = currentContig;
		mostLikelyLeftNeighbours();
		mostLikelyRightNeighbours();
		System.out.println("Changed contig. Current Contig is: "
				+ currentContig);
		sendCurrentContig();
	}
}