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

	private LayoutGraph graph;
	private DNASequence[] contigs;
	private Vector<AdjacencyEdge> selectedContigs;
	private Vector<DNASequence> list;
	private ArrayList<CagEventListener> listeners;
	private Vector<AdjacencyEdge>[] leftNeighbours;
	private Vector<AdjacencyEdge>[] rightNeighbours;
	private String nowCentralContigID;
	private String contigId;
	private int currentContigIndex;
	private boolean currentContigIsReverse = false;

	private DNASequence currentContigObject;
	private Vector<Integer> indexOfFiveMostLikleyLeftNeighbours;
	private Vector<Integer> indexOfFiveMostLikleyRightNeighbours;
	private AdjacencyEdge currentEdge;
	private static CAGWindow window;
	private static CagCreator model;

	private int z;
	private int neighbourNumber = 5;

	// private CAGWindow window;
	public CagCreator(LayoutGraph g) {
		this.graph = g;
		listeners = new ArrayList<CagEventListener>();
		selectedContigs = new Vector<AdjacencyEdge>();
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
						// "/homes/aseidel/testdaten/treecat_project/Corynebacterium_urealyticum_DSM_7109_454LargeContigs_renumbered_repeatmarked.tcp"));
								 "/homes/aseidel/testdaten/treecat_project/Corynebacterium_urealyticum_DSM_7109_454LargeContigs.tcp"));
//								"/homes/aseidel/testdaten/perfekt/Corynebacterium_urealyticum_DSM_7109_454LargeContigs_renumbered_repeatmarked.tcp"));
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
		System.out.println(leftNeighbours[0]);
		System.out.println(rightNeighbours[0]);
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


	/*
	 * TODO 
	 * Hier auch noch mal schauen ob ich wirklich so viele Variblen als
	 * Klassenvariblen def muss. Währe glaube ich besser wenn ich das local
	 * gestallten würde. Auch währe es besser wenn ich hier was basteln würde,
	 * womit ich indices und neighbours gleichzeitig zurückgeben kann.
	 */

	private Vector<AdjacencyEdge> calculateFiveMostLikleyRightNeighbours(
			int cContigIndex, boolean IsReverse) {
		/*
		 * hier muss unterschieden werden, ob das zentrale Contig reverse
		 * dargestellt wird dann muss ich den nachbarnvektor entsprechend
		 * waehelen die Richtung fuer das Contig bestimmt die sicht auf die
		 * nachbarn die Spitze(rechter connector) zeigt wenn es reverse ist ja
		 * in die andere Richtung
		 */
		Vector<AdjacencyEdge> test = IsReverse ? leftNeighbours[cContigIndex]
				: rightNeighbours[cContigIndex];

		Vector<AdjacencyEdge> fiveMostLikleyRightNeighbours = new Vector<AdjacencyEdge>();
		DNASequence neighbourContigObject;

		boolean neighbourIsReverse = false;
		boolean isNeighbourSelected;

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
					neighbourIsReverse = edge.isRightConnectorj(); // stelle
					// fest, ob
					// der
					// Nachbar
					// reverse
					// dargestellt
					// werden
					// muss
					support = edge.getRelativeSupportj();
				} else {// i ist der Nachbar
					neighbourContigObject = edge.getContigi();
					neighbourIsReverse = edge.isRightConnectori();
					support = edge.getRelativeSupportj();
				}

				isNeighbourSelected = neighbourIsAlreadySelected(edge);
				neighbourContigObject.setReverse(neighbourIsReverse);
				neighbourContigObject
						.setSupportComparativeToCentralContig(support);
				neighbourContigObject.setContigIsSelected(isNeighbourSelected);

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

		boolean neighbourIsReverse = false;
		boolean isNeighbourSelected;

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
					neighbourIsReverse = edge.isLeftConnectorj(); // stelle
																	// fest, ob
					// der Nachbar
					// reverse
					// dargestellt
					// werden muss
					support = edge.getRelativeSupportj();
				} else {// i ist der Nachbar
					neighbourContigObject = edge.getContigi();
					neighbourIsReverse = edge.isLeftConnectori();
					support = edge.getRelativeSupportj();
				}
				/*
				 * totalSupport = edge.getSupport();
				 * neighbourContigObject.setTotalSupport(totalSupport);
				 */
				isNeighbourSelected = neighbourIsAlreadySelected(edge);
				neighbourContigObject.setContigIsSelected(isNeighbourSelected);

				neighbourContigObject.setReverse(neighbourIsReverse);
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

	private boolean detectIfContigIsReverse(AdjacencyEdge edge,
			boolean centralContigIsReverse, boolean neighbourIsContigI) {
		boolean solution = false;

		boolean iIsReverse = false;
		boolean jIsReverse = false;

		/*
		 * System.out.println(edge);
		 * System.out.println("linker konnektor i: "+edge.isLeftConnectori());
		 * System.out.println("rechter konnektor i: "+edge.isRightConnectori());
		 */

		/*
		 * Abfragen der 4 Moeglichkeiten, wie die Contigs zu einander orientiert
		 * sein koennen. Ist es keine von den folgenden 3 if dann tritt default
		 * in kraft beide false was die 4 moeglichkeit darstellt.
		 */
		if (edge.isLeftConnectori() == true && edge.isRightConnectori() == true
				&& edge.isLeftConnectorj() == false
				&& edge.isRightConnectorj() == false) {

			iIsReverse = true;
			jIsReverse = false;

		} else if (edge.isLeftConnectori() == true
				&& edge.isRightConnectori() == false
				&& edge.isLeftConnectorj() == false
				&& edge.isRightConnectorj() == true) {

			iIsReverse = true;
			jIsReverse = true;

		} else if (edge.isLeftConnectori() == false
				&& edge.isRightConnectori() == false
				&& edge.isLeftConnectorj() == true
				&& edge.isRightConnectorj() == true) {

			iIsReverse = false;
			jIsReverse = true;

		}

		if (neighbourIsContigI) {
			solution = iIsReverse;
		} else {
			solution = jIsReverse;
		}

		return solution;
	}

	private boolean neighbourIsAlreadySelected(AdjacencyEdge neighbour) {

		boolean flag = false;
		// System.out.println(" Bei der Abfrage welche contigs ausgewählt werden "+selectedContigs.size());
		for (AdjacencyEdge edge : selectedContigs) {

			if (edge.equals(neighbour)) {
				System.out.println( neighbour);
				flag = true;
				break;
			}
		}
		return flag;
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

		CagEvent event = new CagEvent(EventType.EVENT_CHOOSED_CONTIG,
				currentContigObject, currentContigIndex);
		fireEvent(event);
	}

	/**
	 * Send an event. If the user selected an node(contig) the neighbours will
	 * be caculated. This Event carries an Array with the most likely left
	 * neighbours.
	 */
	public void sendLeftNeighbours() {

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
	

	/*
	 * TODO Wenn schon einer dieser Nachbarn ausgewählt wurde sollte dieser
	 * Nachbar eine andere Erscheinung haben als alle anderen Nachbarn.
	 * 
	 * Im CagWindow wird schon eine Liste mit ausgewählten Contigs erstellt.
	 * Diese könnte an diese Klasse gesendet werden und ständig aktualisiert
	 * werden. Oder Besser ich könnte hier im Model diese Liste erstellen. Aus
	 * dem immer wieder gegebenen currentContigs und diese Liste von Instanzen
	 * später mit den Nachbarn abgleichen. Sollte zu einem aktuellem Contig
	 * schon ein Nachbar ausgewählt sein, kann diese Info auch an das Window
	 * übergeben werden und der Hintergrund des Contigs in einer Anderen Farbe
	 * erscheinen. Oder die Border in einer anderen Farbe: noch zu wählen in
	 * schwarz schon gewählt in rot oder so.
	 */


	public void changeContigs(int index, boolean isReverse) {

		this.currentContigIndex = index;
		this.currentContigIsReverse = isReverse;
		this.currentContigObject = graph.getNodes().get(index);
		sendCurrentContig();
	}

}