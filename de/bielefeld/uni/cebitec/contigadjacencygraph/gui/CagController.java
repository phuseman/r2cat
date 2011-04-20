package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;
import javax.swing.JLabel;

public class CagController  {

  private CagCreator cagModel;

  private JPanel contigViewPanel=null;
  private JPanel listViewPanel=null;
  private JPanel legendViewPanel=null;

  private contigView contigView=null;
  private ContigListPanel listView=null;
  private LegendAndInputOptionPanel legendView=null;



  private boolean selectionByUpdate = false;
  private boolean leftNeigboursReady;
  private boolean rightNeighboursReady;
  private boolean centralContigThere;

  public CagController() {
    this.cagModel = new CagCreator();
    this.cagModel.addObserver(this);

    contigViewPanel = new JPanel();


    this.setNeutral();
  }

  private void setNeutral() {
    contigViewPanel.removeAll();
    contigViewPanel.add(new JLabel("not set yet"));
    contigView=null;
    // die anderen auch noch
  }

  public void setLayoutGraph(LayoutGraph graph) {

    if (graph == null) {
      this.setNeutral();
    } else {
      cagModel.setLayoutGraph(graph);


      contigView = new ChooseContigPanel(cagModel);

      contigViewPanel.removeAll();
      contigViewPanel.add(contigView);

// andere panels auch




    }
  }

  public void showContig(DNASequence c) {
    ;
  }

  public JPanel getContigView() {
    return contigViewPanel;
  }

  public JPanel getLegendView() {
    return legendViewPanel;
  }

  public JPanel getListView() {
    return listViewPanel;
  }

//  private void repaintWindowAndSubcomponents() {
//
//    if (this.isRightNeighboursReady && this.isLeftNeigboursReady() && this.centralContigThere) {
//      this.getcontigView().setFlag(true);
//      this.getcontigView().repaint();
//      setLeftNeigboursReady(false);
//      setRightNeighboursReady(false);
//      centralContigThere = false;
//    }
//  }

  private int setTerminator(Vector<AdjacencyEdge> neighbourVector) {

    int value = neighbourVector.size();
    if (cagModel.getNumberOfNeighbours() < neighbourVector.size()) {
      value = cagModel.getNumberOfNeighbours();
    } else if (cagModel.getNumberOfNeighbours() > neighbourVector.size()) {
      value = neighbourVector.size();
    }

    return value;
  }

  private double calculateZScore(AdjacencyEdge edge, int centralContigIndex,
          double[] meanForNeighbours, double[] sDeviationForNeighbours) {

    double zScore = 0;

    zScore = (edge.getSupport() - meanForNeighbours[centralContigIndex])
            / sDeviationForNeighbours[centralContigIndex];

    return zScore;

  }

  private int indexOfNeighbourContig(AdjacencyEdge edge) {

    int index;

    if (edge.geti() == cagModel.getCurrentContigIndex()) {
      index = edge.getj();
    } else {
      index = edge.geti();
    }

    return index;
  }

  private int indexOfCentralContig(AdjacencyEdge edge) {

    int index;

    if (edge.geti() == cagModel.getCurrentContigIndex()) {
      index = edge.geti();
    } else {
      index = edge.getj();
    }

    return index;
  }

  /*
   * If a not repetitiv contig is used in an another adjacency, the flag will be
   * set on true.
   */
  private boolean ulteriorSelected(boolean isLeft, int indexOfNeighbour, AdjacencyEdge edge) {//, Vector<Vector<AdjacencyEdge>> selectedEdges){//, boolean isCentralContigReverse) {
    boolean isSelected = false;

    if (isLeft) {
      if (!cagModel.getSelectedLeftEdges().get(indexOfNeighbour).isEmpty()
              && !cagModel.getGraph().getNodes().get(indexOfNeighbour).isRepetitive()) {

        AdjacencyEdge other = cagModel.getSelectedLeftEdges().get(indexOfNeighbour).firstElement();

        int i = indexOfCentralContig(other);
        isSelected = true;
        if (i == cagModel.getCurrentContigIndex()) {
          isSelected = false;
        }

      }
    } else {
      if (!cagModel.getSelectedRightEdges().get(indexOfNeighbour).isEmpty()
              && !cagModel.getGraph().getNodes().get(indexOfNeighbour).isRepetitive()) {

        AdjacencyEdge other = cagModel.getSelectedRightEdges().get(indexOfNeighbour).firstElement();

        int i = indexOfCentralContig(other);
        isSelected = true;

        if (i == cagModel.getCurrentContigIndex()) {
          isSelected = false;
        }
      }
    }

    return isSelected;
  }

  private void clearComponets(JPanel contigContainer, JPanel radioButtonContainer) {

    if (contigContainer.getComponentCount() > 0
            || radioButtonContainer.getComponentCount() > 0) {
      contigContainer.removeAll();
      radioButtonContainer.removeAll();
    }
  }


  private void updateCentralContig(int index) {

    JPanel centerContainer = contigView.getCenterContainer();

    if (centerContainer.getComponentCount() > 0) {
      centerContainer.removeAll();
    }

    int centralContigIndex = index;
    DNASequence currentContig = cagModel.getGraph().getNodes().get(centralContigIndex);
    boolean isReverse = cagModel.isCurrentContigIsReverse();
    boolean isSelected = false;

    if (!cagModel.getSelectedLeftEdges().elementAt(centralContigIndex).isEmpty()
            || !cagModel.getSelectedRightEdges().elementAt(centralContigIndex).isEmpty()) {
      isSelected = true;
    }

    ContigAppearance centralContig = new ContigAppearance(currentContig,
            centralContigIndex, isSelected, isReverse,
            cagModel.getMaxSizeOfContigs(), cagModel.getMinSizeOfContigs());


    listView.getList().setSelectedValue(currentContig.getId(), true);
    contigView.setCentralContig(centralContig);
    centerContainer.add(centralContig);
    centerContainer.updateUI();
    centralContigThere = true;

  }

  public void updateRightNeighbours() {

    rightNeighboursReady = false;
    int s = 0;

    Vector<AdjacencyEdge> rightNeighbourEdges = cagModel.getCurrentRightNeighbours();
    ContigAppearance contigPanel = null;

    /*
     * Der Terminator muss entweder nach der Anzahl der Nachbarn zum
     * Abbruch führen oder aber, wenn weniger Nachbarn auswählbar sind,
     * nach dieser geringeren Anzahl einen Abbruch herbei führen.
     */
    int terminator = setTerminator(rightNeighbourEdges);

    ContigRadioButton radioButton;
    double[] rightSupport = new double[cagModel.getNumberOfNeighbours()];

    /*
     * Zunächst Löschen aller bisherigen Elemente in der GUI
     */
    JPanel rightContainer = contigView.getRightContainer();
    JPanel rightRadioButtonContainer = contigView.getRightRadioButtonContainer();
    ButtonGroup rightGroup = new ButtonGroup();
    clearComponets(rightContainer, rightRadioButtonContainer);

    // Flag das gesetzt wird sollte einer der Nachbarn schon ausgewählt
    // worden sein.
    boolean isARightNeighourSelected = false;
    AdjacencyEdge neighbourForThisGroup = null;
    /*
     * Setzten des Flags, falls einer der Nachbarn schon ausgewählt ist
     * er das so wird damit kein anderer rechter Nachbarn auswählbar.
     */
    for (AdjacencyEdge e : rightNeighbourEdges) {
      if (e.isSelected()) {
        isARightNeighourSelected = true;
        neighbourForThisGroup = e;
      }
    }

    /*
     * For each adjacency edge here is going to be a contig Panel
     */
    for (AdjacencyEdge edge : rightNeighbourEdges) {
      if (s < terminator) {

        int indexOfContig = indexOfNeighbourContig(edge);

        /*
         * Speichern des relativen oder absoluten Support in einem
         * Array, dieses Array wird später dem GlasPanel übergeben
         * und die Liniendicke berechnet.
         */
        if (cagModel.isZScore()) {
          rightSupport[s] = calculateZScore(edge,
                  cagModel.getCurrentContigIndex(), cagModel.getMeanForRightNeigbours(),
                  cagModel.getsDeviationsForRightNeigbours());
        } else {
          rightSupport[s] = edge.getSupport();
        }
        /*
         * Hier wird für jeden Nachbarn sein Aussehen erstellt.
         *
         * Test, ob der Nachbar schon für einen anderen Knoten im
         * Graphen ausgewählt wurde. Ist dies der Fall bekommt
         * dieses Contig ein anderes Aussehen und kann auch nicht
         * mehr für dieses ausgewählt werden.
         */

        boolean anderweitigAusgewaehlt = ulteriorSelected(false, indexOfContig, edge);

        contigPanel = new ContigAppearance(cagModel.getGraph(), edge,
                indexOfContig, false, cagModel.getMaxSizeOfContigs(),
                cagModel.getMinSizeOfContigs(), anderweitigAusgewaehlt);
//        contigPanel.addMouseListener(new ContigMouseListener());
        contigPanel.setName("contig Panel");

        /*
         * Zu jedem Nachbarn wird auch ein RadioButton erstellt mit
         * dem man einen dieser Contigs auswählen kann. Damit die
         * Button unterscheidbar sind, werden ihm die Kante und der
         * Index des zentralen Contigs übergeben
         */
        radioButton = new ContigRadioButton(edge, contigPanel);

        if (edge.isSelected()) {
          radioButton.setSelected(true);
        }

        if (isARightNeighourSelected) {
          radioButton.setActionCommand("nachbarAusgewaehlt");
          radioButton.setSelectedNeighbourOfButtonGroup(neighbourForThisGroup);

        } else if (!isARightNeighourSelected) {
          radioButton.setActionCommand("noch kein nachbar ausgewaehlt");
        }
        if (anderweitigAusgewaehlt) {
          radioButton.setActionCommand("anderweitigAusgewaehlt");
          AdjacencyEdge otherEdge = cagModel.getSelectedLeftEdges().get(indexOfContig).firstElement();
          radioButton.setNeighboursForTheThisNeighbour(otherEdge);

        }

        radioButton.setNeighbourIndex(indexOfContig);
        radioButton.setCentralIndex(cagModel.getCurrentContigIndex());
        radioButton.setLeft(false);
        radioButton.setOpaque(false);
        radioButton.addActionListener(new RadioButtonActionListener(this, cagModel));


        rightGroup.add(radioButton);
        rightContainer.add(contigPanel);
        rightRadioButtonContainer.add(radioButton);

        if (s < (cagModel.getNumberOfNeighbours() - 1)) {
          rightContainer.add(Box.createVerticalGlue());
          rightRadioButtonContainer.add(Box.createVerticalGlue());
        }
        rightContainer.updateUI();
        rightRadioButtonContainer.updateUI();
        s++;
      }
      if (s == terminator) {
        break;
      }
    }
    rightContainer.add(Box.createVerticalGlue());
    rightRadioButtonContainer.add(Box.createVerticalGlue());
    contigView.setRightSupport(rightSupport);
    rightNeighboursReady = true;
    repaintWindowAndSubcomponents();

  }

  public void updateLeftNeighbours() {

    leftNeigboursReady = false;

    Vector<AdjacencyEdge> leftNeighbourEdges = cagModel.getCurrentLeftNeighbours();
    ContigAppearance contigPanel = null;

    ContigRadioButton radioButton;
    double[] leftSupport = new double[cagModel.getNumberOfNeighbours()];
    int t = 0;

    JPanel leftContainer = contigView.getLeftContainer();
    JPanel leftRadioButtonContainer = contigView.getLeftRadioButtonContainer();
    ButtonGroup leftGroup = new ButtonGroup();

    clearComponets(leftContainer, leftRadioButtonContainer);

    /*
     * The terminator finish the creation of the layout.
     * it has either to be the number of neighbours
     * or if the number of neighbours, which is choosed from
     * user is bigger than there are neighbours, it should
     * finish earlier.
     */
    int terminator = setTerminator(leftNeighbourEdges);

    boolean isALeftNeighourSelected = false;
    AdjacencyEdge whichNeighbourIsSelected = null;

    /*
     * Figure out, if there is a neighbour already selected
     */
    for (AdjacencyEdge e : leftNeighbourEdges) {
      if (e.isSelected()) {
        isALeftNeighourSelected = true;
        whichNeighbourIsSelected = e;
      }
    }
    /*
     * This is necessary to set the layout of the choosed ContigPanel
     * or rather for the leftcontainer
     */
    for (AdjacencyEdge edge : leftNeighbourEdges) {

      if (t < terminator) {

        int indexOfContig = indexOfNeighbourContig(edge);

        /*
         * Save the support or z-scores here in an array
         * to commit them to the choose contig panel
         * for setting the linestroke
         */
        if (cagModel.isZScore()) {
          leftSupport[t] = calculateZScore(edge,
                  cagModel.getCurrentContigIndex(), cagModel.getMeanForLeftNeigbours(),
                  cagModel.getsDeviationsForLeftNeigbours());
        } else {
          leftSupport[t] = edge.getSupport();
        }

        boolean anderweitigAusgewaehlt = ulteriorSelected(true, indexOfContig, edge);

        /*
         * Set the appearance for each contig
         */
        contigPanel = new ContigAppearance(cagModel.getGraph(), edge,
                indexOfContig, true, cagModel.getMaxSizeOfContigs(),
                cagModel.getMinSizeOfContigs(), anderweitigAusgewaehlt);
//        contigPanel.addMouseListener(new ContigMouseListener());


        /*
         * The radio Button get commands to differentiate between
         * adjacencies which are already selected, or selected somewhere
         * else or not selected
         */
        radioButton = new ContigRadioButton(edge, contigPanel);

        if (isALeftNeighourSelected) {
          radioButton.setActionCommand("nachbarAusgewaehlt");
          radioButton.setSelectedNeighbourOfButtonGroup(whichNeighbourIsSelected);
        } else if (!isALeftNeighourSelected) {
          radioButton.setActionCommand("noch kein nachbar ausgewaehlt");
        }

        if (anderweitigAusgewaehlt) {
          radioButton.setActionCommand("anderweitigAusgewaehlt");
          AdjacencyEdge otherEdgeForThisNeighbour = cagModel.getSelectedLeftEdges().get(indexOfContig).firstElement();
          radioButton.setNeighboursForTheThisNeighbour(otherEdgeForThisNeighbour);
        }
        if (edge.isSelected()) {
          radioButton.setSelected(true);
        }

        radioButton.setLeft(true);
        radioButton.setOpaque(false);
        radioButton.addActionListener(new RadioButtonActionListener(this, cagModel));

        // add here Contigs and RadioButton with 	dynamic space
        leftContainer.add(contigPanel);


        leftGroup.add(radioButton);
        leftRadioButtonContainer.add(radioButton);

        /*
         * There will be added some 	dynamic space
         */
        if (t < (cagModel.getNumberOfNeighbours() - 1)) {
          leftContainer.add(Box.createVerticalGlue());
          leftRadioButtonContainer.add(Box.createVerticalGlue());
        }
        leftContainer.updateUI();
        leftRadioButtonContainer.updateUI();
        t++;
      }
      if (t == terminator) {
        break;
      }
    }
    leftContainer.add(Box.createVerticalGlue());
    leftRadioButtonContainer.add(Box.createVerticalGlue());
    contigView.setLeftSupport(leftSupport);
    leftNeigboursReady = true;
    repaintWindowAndSubcomponents();
  }


}
