package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
	private DNASequence contig;
	private long contigSize = 0;
	private boolean contigIsRepetitiv = false;

	// private CAGWindow window;
	public CagCreator() {
		listeners = new ArrayList<CagEventListener>();
		leftAndRightNeighbour();
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
	 * TODO Zur zeit ist es moeglich, dass die Kante sowohl rechter als auch linker 
	 * Konnektor sein kann. soll das so bleiben??
	 */
	private void leftAndRightNeighbour() {
		leftNeighbours = new LinkedHashMap<Integer, Vector<AdjacencyEdge>>();
		rightNeighbours = new LinkedHashMap<Integer, Vector<AdjacencyEdge>>();

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
		for (Integer id : rightNeighbours.keySet()) {
			System.out.println("Right neighbours for id : "+id);
			for(AdjacencyEdge edge : rightNeighbours.get(id)) {
				System.out.println(edge);
			}
		}
//		System.out.println(rightNeighbours);
		
	}
	/*
	 * TODO ist es notwendig, dass ich double erhalte?
	 */
	private double[] fiveMostLikelyRightNeighbours(){
		double[] sortierArray = new double[rightNeighbours.size()];
		for (AdjacencyEdge edge : rightNeighbours.get(currentContig)){
				
		}
		return sortierArray;
	
	}
	
	// sortiert ein Zahlen-Array mit CountingSort
	// erwartet als Parameter ein int-Array und gibt dieses sortiert wieder zurück
	private  double[] countingSort(double[] numbers) {
		// Maximum der Zahlen berechnen
		double max = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			// wenn es größeres als das aktuelle gibt, ist das nun das neue größte
			if (numbers[i] > max)
				max = numbers[i];
		}
	 
		// temporäres Array erzeugen mit: Länge = Maximum des Zahlenarrays + die "0"
		double[] sortedNumbers = new double[(int)max+1];
	 
		// Indizes des Zahlen-Arrays durchgehen
		for (int i = 0; i < numbers.length; i++) {
			// wir zählen, wie oft jede Zahl aus numbers vorkommt und
			// speichern diese Anzahl in sortedNumbers[] bei Index number[i]
			sortedNumbers[(int)numbers[i]]++;
		}
	 
		// insertPosition steht für die Schreib-Position im Ausgabe-Array
		int insertPosition = 0;
	 
		// Indizes von sortedNumbers[] durchgehen, um zu sehen, wie oft jede Zahl vorkommt
		for (int i = 0; i <= max; i++) {
			// Anzahl von i durchgehen, um gleiche Zahlen hintereinander einzutragen
			for (int j = 0; j < sortedNumbers[i]; j++) {
				// das Zahlen-Array wird jetzt sortiert neu geschrieben für jedes
				// Auftreten von i
				numbers[insertPosition] = i;
				insertPosition++;
			}
		}
		return numbers;
	}

	private void fiveMostLikelyLeftNeighbours(){
		
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
	 * gelÃ¶scht.
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
	 * der Zustand vom Model geÃ¤ndert hat auf der Gui in der Mitte dieses
	 * spezifische Contig erscheinen. Sprich es sollte als Reverse, Normal oder
	 * Repetetiv erscheinen mit dem richtigen Namen drauf.
	 * 
	 * TODO Des weiteren sollten die besten 5 Nachbarn ermittelt werden erstelle
	 * Methode findTheBest5Neightbours
	 * 
	 * TODO Wenn schon einer dieser Nachbarn ausgewÃ¤hlt wurde sollte dieser
	 * Nachbar eine andere Erscheinung haben als alle anderen Nachbarn.
	 */
	public void setCurrentContig(String currentContig) {
		this.currentContig = currentContig;
		System.out.println("Changed contig. Current Contig is: "
				+ currentContig);
	}
}