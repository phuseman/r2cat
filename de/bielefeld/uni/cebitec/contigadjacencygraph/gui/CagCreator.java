package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.io.File;
import java.io.IOException;
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

public class CagCreator {

	private static Vector<DNASequence> contigs;
	private static String[] listData;

	// private CAGWindow window;

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

			LayoutGraph layoutGraph = project.sortContigs();

			ContigAdjacencyGraph cag = project.getContigAdjacencyGraph();
			LayoutGraph completeGraph = cag.getCompleteGraph();
			
			/*
			 * erstellen einer Namensliste aller Contigs
			 */
			contigs = completeGraph.getNodes();
			listData = new String[contigs.size()];
			
			for (int i = 0 ; i < contigs.size(); i ++){
				 DNASequence contig = contigs.get(i);
				 listData[i]= contig.getId();
			}
			
			//System.out.println(completeGraph.csvOutput());

			/*
			 * Diese Hashmaps speichern die linken und rechten Nachbarn aller Contigs
			 */
			HashMap<Integer, Vector<AdjacencyEdge>> left = new HashMap<Integer, Vector<AdjacencyEdge>>();
			HashMap<Integer, Vector<AdjacencyEdge>> right = new HashMap<Integer, Vector<AdjacencyEdge>>();
			
			for (AdjacencyEdge e : completeGraph.getEdges()) {
				//System.out.println("(" + e.geti() + ", " + e.getj() + ")= "+ e.getSupport());

				int i = e.geti();
				int j = e.getj();

				if (e.isLeftConnectori()) {
					if (!left.containsKey(i)) {
						left.put(i, new Vector<AdjacencyEdge>()); 
					}
					left.get(i).add(e);
				} else {
					if (!right.containsKey(i)) {
						right.put(i, new Vector<AdjacencyEdge>()); 
					}
					right.get(i).add(e);
				}

				if (e.isLeftConnectorj()) {
					if (!left.containsKey(j)) {
						left.put(j, new Vector<AdjacencyEdge>()); 
					}
					left.get(j).add(e);
				} else {
					if (!right.containsKey(j)) {
						right.put(j, new Vector<AdjacencyEdge>()); 
					}
					right.get(j).add(e);
				}

			}

		/*	System.out.println(left.size());
			System.out.println("hashmap linke seite");
			System.out.println(left);
			System.out.println("hashmap rechte seite");
			System.out.println(right);*/
			// System.out.println(layoutGraph);
			// layoutGraph.writeLayoutAsNeato(new
			// File(project.suggestOutputFile()),
			// LayoutGraph.NeatoOutputType.ONENODE);
			// / System.out.println("wrote "+project.suggestOutputFile());

			t.stopTimer("sorting");

			t.stopTimer("Total time");
		} catch (CannotProceedException e) {
			System.err.println("Programm failed:\n" + e.getMessage());
		}

		// Starten des Fensters
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
				new CAGWindow(listData);
			}
		});
	}

}
