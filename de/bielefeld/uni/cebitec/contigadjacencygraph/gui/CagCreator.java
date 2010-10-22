package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.io.File;
import java.io.IOException;

import javax.naming.CannotProceedException;

import de.bielefeld.uni.cebitec.common.MiscFileUtils;
import de.bielefeld.uni.cebitec.common.SimpleProgressReporter;
import de.bielefeld.uni.cebitec.common.Timer;
import de.bielefeld.uni.cebitec.contigadjacencygraph.ContigAdjacencyGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.treecat.TreebasedContigSorterProject;

public class CagCreator {
	public static void main(String[] args) {
		TreebasedContigSorterProject project = new TreebasedContigSorterProject();
		
		project.register(new SimpleProgressReporter());
		try {
		try {
			
			boolean projectParsed=project.readProject(new File(
					"/homes/aseidel/testdaten/treecat_project/Corynebacterium_urealyticum_DSM_7109_454LargeContigs_renumbered_repeatmarked.tcp"
			));
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
		t.startTimer();

		project.generateMatches();

		t.restartTimer("matches");

		LayoutGraph layoutGraph = project.sortContigs();
		
		
		ContigAdjacencyGraph cag = project.getContigAdjacencyGraph();
		cag.makeSymmetrical();
		//-------------------->
		
		System.out.println(cag.adjacencyWeightMatrix[0][0]);
		
		
//		System.out.println(layoutGraph);
//		layoutGraph.writeLayoutAsNeato(new File(project.suggestOutputFile()), LayoutGraph.NeatoOutputType.ONENODE);
///		System.out.println("wrote "+project.suggestOutputFile());
		

		t.stopTimer("sorting");


		t.stopTimer("Total time");
		} catch( CannotProceedException e) {
			System.err.println("Programm failed:\n" + e.getMessage());
		}
	}

}
