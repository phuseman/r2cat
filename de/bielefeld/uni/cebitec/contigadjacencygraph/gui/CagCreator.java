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
	private Vector<DNASequence> contigs;
	private Vector<AdjacencyEdge> selectedContigs;
	private String[] listData;
	private ArrayList<CagEventListener> listeners;
	private Vector<AdjacencyEdge>[] leftNeighbours;
	private Vector<AdjacencyEdge>[] rightNeighbours;
	private String nowCentralContigID;
	private String contigId;
	private int contigIndex;
	private boolean contigIsReverse = false;

	private Vector<DNASequence> fiveMostLikleyLeftNeighbours;
	private Vector<DNASequence> fiveMostLikleyRightNeighbours;
	
	private DNASequence currentContigObject;
	private DNASequence neighbourContigObject;
	private static CAGWindow window;
	private static CagCreator model;

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
				//				"/homes/aseidel/testdaten/treecat_project/Corynebacterium_urealyticum_DSM_7109_454LargeContigs_renumbered_repeatmarked.tcp"));
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

//			model = new CagCreator(layoutGraph);
			model = new CagCreator(completeGraph);

			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
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
			int i = e.geti();
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
	 * Wenn ich die 5 wahrscheinlichsten Nachbarn aus der Map hole sollten auch
	 * noch die Informationen zu dem Contig geholt werden, damit spaeter das
	 * Contig auch richt dargestellet werden kann.
	 * 
	 * TODO Es besteht hier folgendes Problem: Die Arrays leftNeighbours und
	 * rightNeighbours werden nach dem support sortiert. Dieser Support nehme
	 * ich an besteht aus relativeSupporti und relativeSupportj. Wenn ich jetzt
	 * die Nachbarn anzeige und berechne, dann greife ich auf den
	 * relativeSupport zurück. Hier kann die Sortierung aber anders sein. Frage:
	 * wie hängen diese beiden supports zusammen?
	 * 
	 * Mögliche Lösung: ich könnte immer den support nehmen statt
	 * relativeSupport Wäre das sinnvoll? 
	 */

	public synchronized Vector<DNASequence> calculateFiveMostLikleyNeighbours(
			int index, boolean isLeft) {

		Vector<AdjacencyEdge>[] neighbours;
		Vector<DNASequence> fiveNeighbours;
		
		if (isLeft) {
			System.out.println("linke nachbarn ");
			fiveMostLikleyLeftNeighbours = new Vector<DNASequence>();
			fiveNeighbours = fiveMostLikleyLeftNeighbours;
			neighbours = leftNeighbours;
			
		} else {
			System.out.println("rechte nachbarn");
			fiveMostLikleyRightNeighbours = new Vector<DNASequence>();
			fiveNeighbours = fiveMostLikleyRightNeighbours;
			neighbours = rightNeighbours;
		}

		double support;
		double totalSupport;
		boolean is_I_equals_X = false;

		for (Iterator<AdjacencyEdge> iterator = neighbours[index].iterator(); iterator
				.hasNext();) {
			
				AdjacencyEdge edge = iterator.next();
				totalSupport = edge.getSupport();
				System.out.println(edge.getContigi().getId()+"  "+edge.getContigj().getId()+"  " +totalSupport);

				if (edge.getContigi().getId() == currentContigObject.getId()) {
					is_I_equals_X = true;
					neighbourContigObject = edge.getContigj();
					support = edge.getRelativeSupportj();
				} else {
					is_I_equals_X = false;
					neighbourContigObject = edge.getContigi();
					support = edge.getRelativeSupporti();
				}
				/*
				 * Notwendig weil ein Contig mehrfach als Nachbar in verschiedenen Orientierungen 
				 * vorkommen kann.
				 * Die folgenden Nachbarn ueberschreiben dann die Infos zu dem eigentlich interessanten
				 * Nachbarn.
				 * Im Moment behandel ich den Nachbarn nur einmal.
				 * TODO 
				 * Besser waehre aber wenn ich diesen Nachbarn auch als moeglichen anderen Nachbarn
				 * in Erwaegung ziehe aber auch kennlich mache das dieser Nachbar ein anderer ist,
				 * indem die Orientierung dieses Nachbarn anders ist.
				 */
				if(!fiveNeighbours.contains(neighbourContigObject)){
				if (is_I_equals_X) {// j ist der nachbar

					boolean flag = neighbourIsAlreadySelected(edge);
					/*
					 * Hier finde ich herraus, ob der Nachbar reverse angezeigt
					 * werden muss oder nicht.
					 */
					if (edge.isLeftConnectorj() == true
							&& edge.isRightConnectorj() == true) {
						neighbourContigObject.setReverse(true);
					} else if (edge.isLeftConnectorj() == false
							&& edge.isLeftConnectorj() == true) {
						neighbourContigObject.setReverse(true);
					} else {
						neighbourContigObject.setReverse(false);
					}

					neighbourContigObject
							.setSupportComparativeToCentralContig(support);
					neighbourContigObject.setTotalSupport(totalSupport);
					neighbourContigObject.setContigIsSelected(flag);
					
					fiveNeighbours.add(neighbourContigObject);
					
				} else {// i ist der nachbar
					boolean flag = neighbourIsAlreadySelected(edge);
					/*
					 * Hier finde ich herraus, ob der Nachbar reverse angezeigt
					 * werden muss oder nicht.
					 */
					if (edge.isLeftConnectori() == true
							&& edge.isRightConnectori() == true) {
						neighbourContigObject.setReverse(true);
					} else if (edge.isLeftConnectori() == true
							&& edge.isRightConnectori() == false) {
						neighbourContigObject.setReverse(true);
					} else {
						neighbourContigObject.setReverse(false);
					}
					neighbourContigObject
							.setSupportComparativeToCentralContig(support);
					neighbourContigObject.setTotalSupport(totalSupport);
					neighbourContigObject.setContigIsSelected(flag);
					
					fiveNeighbours.add(neighbourContigObject);
					
				}
			}
		}
		if (isLeft) {
			
			for (DNASequence dnaSequence : fiveMostLikleyLeftNeighbours) {
				System.out.println("links total support in cag "+dnaSequence.getTotalSupport());
			}
			
			return fiveMostLikleyLeftNeighbours;
		} else {
			for (DNASequence dnaSequence : fiveMostLikleyRightNeighbours) {
				System.out.println("rechts total support in cag "+dnaSequence.getTotalSupport());
			}
			return fiveMostLikleyRightNeighbours;
		}

	}
	
	/*
	 * Method to select some informations for the current contig. They will be
	 * displayed in the view of the contig
	 */
	private synchronized DNASequence detectCurrentContigObject(String name) {

		contigId = name;

		for (DNASequence c : contigs) {
			contigIndex = contigs.indexOf(c);
			if (c.getId().equals(contigId)) {
				currentContigObject = c;
				/*
				 * TODO hier muss ich noch feststellen ob das mittlere Contig 
				 * reverse dargestellt werden muss.
				 */
				if (contigIsReverse == true) {
					currentContigObject.setReverse(true);
				} else {
					currentContigObject.setReverse(false);
				}
				break;
			}
		}
		return currentContigObject;
	}

	private boolean neighbourIsAlreadySelected(AdjacencyEdge neighbour) {
		
		boolean flag = false;

		for (AdjacencyEdge edge : selectedContigs) {
			if (edge.equals(neighbour)) {
				flag = true;
			}
		}
		return flag;
	}
	

	public Vector<AdjacencyEdge> addSelectedContig(String neighbourName,
			String side) {

		if (side.equals("left")) {
			//System.out.println("cag: links");
			for (Iterator<AdjacencyEdge> iterator = leftNeighbours[contigIndex]
					.iterator(); iterator.hasNext();) {
				AdjacencyEdge edge = iterator.next();
				
				/*
				 * TODO erst abfragen, ob in dieser Kante zu dem Aktuellem
				 * Contig schon ein Eintrag vorhanden ist wenn ja sollte es
				 * nicht moeglich sein einen neuen Eintrag zu machen dann muss
				 * erst der Benutzer gefrage werden ob er die bisher
				 * ausgewaehlte kante loeschen moechte ist das der fall so
				 * loeschen dann erst neue Kante hinzufuegen lassen.
				 */
			
				if (edge.getContigi().getId().equals(neighbourName) ) {
				//	System.out.println(edge + " support of edge, i "+edge.getRelativeSupporti());
					selectedContigs.add(edge);
					break;
				}
				if (edge.getContigj().getId().equals(neighbourName)) {
				//	System.out.println(edge + " support of edge, j "+edge.getRelativeSupportj());
					selectedContigs.add(edge);
					break;

				}
				
			}
		} else if (side.equals("right")) {
	//		System.out.println("cag: rechts");
			for (Iterator<AdjacencyEdge> iterator = rightNeighbours[contigIndex]
					.iterator(); iterator.hasNext();) {
				AdjacencyEdge edge = iterator.next();
				
				if (edge.getContigi().getId().equals(neighbourName)) {
		//			System.out.println(edge + " support of edge, i "+edge.getRelativeSupporti());
					selectedContigs.add(edge);
					break;
				}
				if (edge.getContigj().getId().equals(neighbourName)) {
				//	System.out.println(edge + " support of edge, j "+edge.getRelativeSupportj());
					selectedContigs.add(edge);
					break;
				}
			}
		}
		return selectedContigs;
	}

	/*
	 * Send an event, if the user selected a contig
	 */
	public void sendCurrentContig() {
	//	System.out.println("cag: Sende aktuelles Contig");
		CagEvent event = new CagEvent(EventType.EVENT_CHOOSED_CONTIG,
				currentContigObject);
		fireEvent(event);
	}

	/**
	 * Send an event. If the user selected an node(contig) the neighbours will
	 * be caculated. This Event carries an Array with the most likely left
	 * neighbours.
	 */
	public void sendLeftNeighbours() {
//		System.out.println("cag: linke nachbarn ");

		CagEvent event = new CagEvent(EventType.EVENT_SEND_LEFT_NEIGHBOURS,
				calculateFiveMostLikleyNeighbours(contigIndex, true));
		fireEvent(event);
	}

	/**
	 * Send an event. If the user selected an node(contig) the neighbours will
	 * be caculated. This Event carries an Array with the most likely right
	 * neighbours.
	 */
	public void sendRightNeighbours() {
//		System.out.println("cag: rechte nachbarn ");

		CagEvent event = new CagEvent(EventType.EVENT_SEND_RIGHT_NEIGHBOURS,
				calculateFiveMostLikleyNeighbours(contigIndex, false));
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
	public String[] getListData() {
		this.createContigList();
		return listData;
	}

	/*
	 * Return the current Contig Id/Name
	 */
	public String getCurrentContig() {
		return nowCentralContigID;
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
	public void changeContigs(String currentContig, String isReverse) {

		this.nowCentralContigID = currentContig;
		this.contigIsReverse = Boolean.parseBoolean(isReverse);
		detectCurrentContigObject(currentContig);
		sendCurrentContig();
	}
}