package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Observable;
import java.util.Vector;

import javax.naming.CannotProceedException;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import com.sun.org.apache.bcel.internal.generic.CPInstruction;

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
public class CagCreator extends Observable {

	private static CagController controller;
	private static CagCreator model;

	private LayoutGraph graph;
	private Vector<AdjacencyEdge>[] leftNeighbours;
	private Vector<AdjacencyEdge>[] rightNeighbours;

	private int currentContigIndex;
	private boolean currentContigIsReverse = false;
	private DNASequence currentContigObject;
	
	private long maxSizeOfContigs;
	private long minSizeOfContigs;
	private double minSupport;
	private double maxSupport;
	private double[] meanForLeftNeigbours;
	private double[] sDeviationsForLeftNeigbours;
	private double[] meanForRightNeigbours;
	private double[] sDeviationsForRightNeigbours;
	private Vector<DNASequence> contigs;

	private int numberOfNeighbours= 5;
	private Vector<Vector<AdjacencyEdge>> selectedLeftEdges = new Vector<Vector<AdjacencyEdge>>();
	private Vector<Vector<AdjacencyEdge>> selectedRightEdges = new Vector<Vector<AdjacencyEdge>>();
	private boolean isZScore;
	private Vector<AdjacencyEdge> currentLeftNeighbours;
	private Vector<AdjacencyEdge> currentRightNeighbours;


	public CagCreator() {
	}

  public CagCreator(LayoutGraph l) {
    this.setLayoutGraph(l);
	}

	public void setLayoutGraph(LayoutGraph g){
		
		this.graph = g;
		leftAndRightNeighbour();
		contigs = graph.getNodes();
		calculateMinSizeOfContigs(contigs);
		calculateMaxSizeOfContigs(contigs);
		
		calculateMaxSupport(g);
		calculateMinSupport(g);

		calculateMeanAndSDeviationForLeftNeigbours(leftNeighbours);
		calculateMeanAndSDeviationForRightNeigbours(rightNeighbours);
		
		selectedLeftEdges.setSize(graph.getNodes().size());
		selectedRightEdges.setSize(graph.getNodes().size());
		int term = selectedLeftEdges.size();
		int term2 = selectedRightEdges.size();

		/*
		 * initialization of the vectors in the vector 
		 */
		for (int i = 0; i < selectedLeftEdges.size(); i++) {
			Vector<AdjacencyEdge> contigVector = new Vector<AdjacencyEdge>();
			
			/*
			 * And figure out which neighbours are already selected
			 */
			Vector<AdjacencyEdge> neighboursForCurrentIndex = leftNeighbours[i];
			for (AdjacencyEdge adjacencyEdge : neighboursForCurrentIndex) {
				if(adjacencyEdge.isSelected()){
					contigVector.add(adjacencyEdge);
				}
			}
			
			selectedLeftEdges.add(i,contigVector);
			if(i == term-1){
				break;
			}
		}		

		for (int i = 0; i < selectedRightEdges.size(); i++) {
			Vector<AdjacencyEdge> contigVector = new Vector<AdjacencyEdge>();
			
			Vector<AdjacencyEdge> neighboursForCurrentIndex = rightNeighbours[i];
			for (AdjacencyEdge adjacencyEdge : neighboursForCurrentIndex) {
				if(adjacencyEdge.isSelected()){
					contigVector.add(adjacencyEdge);
				}
			}
			selectedRightEdges.add(i,contigVector);
			if(i == term2-1){
				break;
			}
		}
	}
	
	
	
  public static void main(String[] args) {

    TreebasedContigSorterProject project = new TreebasedContigSorterProject();

    project.register(new SimpleProgressReporter());
    try {
      try {

        boolean projectParsed = project.readProject(new File(
                // "/homes/aseidel/testdaten/treecat_project/Corynebacterium_urealyticum_DSM_7109_454LargeContigs_renumbered_repeatmarked.tcp"));
                //								 "/homes/aseidel/testdaten/treecat_project/Corynebacterium_urealyticum_DSM_7109_454LargeContigs.tcp"));
                "/homes/aseidel/testdaten/perfekt/Corynebacterium_urealyticum_DSM_7109_454LargeContigs_renumbered_repeatmarked.tcp"));

        if (!projectParsed) {
          System.err.println("The given project file was not sucessfully parsed");
          System.exit(1);
        }

      } catch (IOException e) {
        System.err.println("The given project file was not sucessfully parsed:\n"
                + e.getMessage());
        System.exit(1);
      }

      LayoutGraph layoutGraph;
      ContigAdjacencyGraph cag;
      final LayoutGraph completeGraph;

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
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            // "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            // UIManager.getCrossPlatformLookAndFeelClassName());
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          controller = new CagController();
          controller.setLayoutGraph(completeGraph);

          CAGWindow win = new CAGWindow();
          win.initWindow();

          ChooseContigPanel contigView = controller.getContigView();
          contigView.setMinimumSize(new Dimension(400, 220));
          contigView.setSizeOfPanel(300, 100);
          LegendAndInputOptionPanel legendView = controller.getLegendView();
          ContigListPanel listView = controller.getListView();

          contigView.setVisible(true);
          legendView.setVisible(true);
          listView.setVisible(true);

   
          
          JScrollPane scroll = new JScrollPane(contigView);
          scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
          scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
          
          win.add(scroll, BorderLayout.CENTER);
          win.add(legendView, BorderLayout.SOUTH);
          win.add(listView, BorderLayout.EAST);



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
	 * Calculate the max size of a given list of contigs
	 */
	private long calculateMaxSizeOfContigs(Vector<DNASequence> contigList) {

		maxSizeOfContigs = contigList.firstElement().getSize();

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
	private long calculateMinSizeOfContigs(Vector<DNASequence> contigList) {

		minSizeOfContigs = contigList.firstElement().getSize();

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
	 * necessary to change the current contig and also the
	 * to get the neighbours of these contig 
	 */
	public void changeContigs(int index, boolean isReverse) {

		setChanged();
		this.currentContigIndex = index;
		this.currentContigIsReverse = isReverse;
		this.currentContigObject = graph.getNodes().get(index);
		currentLeftNeighbours = fiveMostLikleyLeftNeighbours(currentContigIndex, isReverse);
		currentRightNeighbours = fiveMostLikleyRightNeighbours(currentContigIndex, isReverse);
		notifyObservers(currentContigObject);
		
	}
	

	public boolean isCurrentContigIsReverse() {
		return currentContigIsReverse;
	}


	public Vector<AdjacencyEdge> getCurrentLeftNeighbours() {
		return currentLeftNeighbours;
	}


	public Vector<AdjacencyEdge> getCurrentRightNeighbours() {
		return currentRightNeighbours;
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
	
	


	public int getNumberOfNeighbours() {
		return numberOfNeighbours;
	}


	public void setNumberOfNeighbours(int neighbourOfNumbers) {
		setChanged();
		this.numberOfNeighbours = neighbourOfNumbers;
		notifyObservers();
	}


	public Vector<Vector<AdjacencyEdge>> getSelectedLeftEdges() {
		return selectedLeftEdges;
	}


	public void setSelectedLeftEdges(Vector<Vector<AdjacencyEdge>> selectedLeftEdges) {
		this.selectedLeftEdges = selectedLeftEdges;
	}


	public Vector<Vector<AdjacencyEdge>> getSelectedRightEdges() {
		return selectedRightEdges;
	}


	public void setSelectedRightEdges(
			Vector<Vector<AdjacencyEdge>> selectedRightEdges) {
		this.selectedRightEdges = selectedRightEdges;
	}
	public int getCurrentContigIndex() {
		return currentContigIndex;
	}


	public DNASequence getCurrentContigObject() {
		return currentContigObject;
	}
	
	public boolean isZScore() {
		return isZScore;
	}


	public void setZScore(boolean isZScore) {
		setChanged();
		this.isZScore = isZScore;
		notifyObservers();
	}



}