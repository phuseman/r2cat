package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.print.DocFlavor;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.NumberFormatter;

//import sun.org.mozilla.javascript.IdScriptableObject;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class CAGWindow extends JFrame implements CagEventListener {

	private CAGWindow window;
	private JScrollPane listScroller;
	private JList list;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;
	private BoxLayout layout;
	private DNASequence[] nodes;
	private CagCreator model;

	private JPanel chooseContigPanel;
	private JPanel leftContainer;
	private JPanel centerContainer;
	private JPanel rightContainer;

	private JScrollPane scrollPane;

	private double[] leftSupport;
	private double[] rightSupport;

	private ContigAppearance centralContig;

	private ContigButtonGroup leftGroup = new ContigButtonGroup();
	private ContigButtonGroup rightGroup = new ContigButtonGroup();
	private JPanel leftRadioButtonContainer;
	private JPanel rightRadioButtonContainer;

	private Vector<AdjacencyEdge> selectedRadioButtons;
	private LinkedList<AdjacencyEdge> selectedContigs;

	private int numberOfNeighbours = 5;

	private LayoutGraph layoutGraph;
	private int centralContigIndex;
	private String[] dataForList;

	private JPanel inputOption;
	private JFormattedTextField inputOptionForNumberOfNeighbours;
	private Vector<AdjacencyEdge> rightNeighbourEdges;
	private Vector<AdjacencyEdge> leftNeighbourEdges;
	private boolean isZScore;
	private boolean isARightNeighourSelected;
	private boolean isALeftNeighourSelected;

	private long maxSizeOfContigs;
	private long minSizeOfContigs;
	private double maxSupport;
	private double minSupport;
	private double[] meanForRightNeighbours;
	private double[] meanForLeftNeighbours;
	private double[] sDeviationForLeftNeighbours;
	private double[] sDeviationForRightNeighbours;

	private Vector<DNASequence> ausgewaehlteContigs = new Vector<DNASequence>();
	private Vector<AdjacencyEdge> ausgewaehlteLinkeKanten = new Vector<AdjacencyEdge>();
	private Vector<AdjacencyEdge> ausgewaehlteRechteKanten = new Vector<AdjacencyEdge>();

	private Point[] leftComponentPositions;
	private Point[] rightComponentPositions;
	private Point[] centralPosition;
	private boolean flag;
	private JRadioButton relativeSupport;
	private JRadioButton absoluteSupport;

	public CAGWindow(CagCreator myModel) {

		window = this;
		this.model = myModel;
		layoutGraph = model.getGraph();
		nodes = model.getListData();
		maxSizeOfContigs = model.getMaxSizeOfContigs();
		minSizeOfContigs = model.getMinSizeOfContigs();
		maxSupport = model.getMaxSupport();
		minSupport = model.getMinSupport();

		meanForRightNeighbours = model.getMeanForRightNeigbours();
		meanForLeftNeighbours = model.getMeanForLeftNeigbours();
		sDeviationForRightNeighbours = model.getsDeviationsForRightNeigbours();
		sDeviationForLeftNeighbours = model.getsDeviationsForLeftNeigbours();

		ausgewaehlteLinkeKanten.setSize(layoutGraph.getNodes().size());
		ausgewaehlteRechteKanten.setSize(layoutGraph.getNodes().size());

		myModel.addEventListener(this);
		setTitle("View of a contig adjacency graph");
		setName("fenster");
		selectedRadioButtons = new Vector<AdjacencyEdge>();
		/*
		 * Menu TODO Was fuer Funktionen sollte das Menu haben??
		 */
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		menuItem = new JMenuItem("Exit");
		menuItem.addActionListener(new ExitItemListener());

		/*
		 * a group of JMenuItems Hier werden die Menupunkte hinzugefuegt TODO
		 * menupunkte hinzufuegen
		 */
		menu.add(menuItem);
		menuBar.add(menu);
		add(menuBar, BorderLayout.NORTH);

		/*
		 * Dieses Panel enhaelt das Contig das Ausgewaehlt wurde und deren
		 * moegliche Nachbarn
		 */
		chooseContigPanel = new JPanel();
		chooseContigPanel.setName("ChooseContigPanel");
		chooseContigPanel.setMinimumSize(new Dimension(1000, 400));
		chooseContigPanel.setPreferredSize(new Dimension(1000, 400));
		chooseContigPanel.setMaximumSize(new Dimension(1000, 400));

		chooseContigPanel.setBackground(Color.WHITE);

		layout = new BoxLayout(chooseContigPanel, BoxLayout.LINE_AXIS);
		chooseContigPanel.setLayout(layout);

		leftContainer = new JPanel();
		leftContainer.setName("leftContainer");
		leftRadioButtonContainer = new JPanel();
		centerContainer = new JPanel();
		rightRadioButtonContainer = new JPanel();
		rightContainer = new JPanel();
		rightContainer.setName("rightContainer");

		BoxLayout leftBoxLayout = new BoxLayout(leftContainer,
				BoxLayout.PAGE_AXIS);
		BoxLayout leftRadioBoxLayout = new BoxLayout(leftRadioButtonContainer,
				BoxLayout.PAGE_AXIS);
		BoxLayout centerBoxLayout = new BoxLayout(centerContainer,
				BoxLayout.PAGE_AXIS);
		BoxLayout rightRadioBoxLayout = new BoxLayout(
				rightRadioButtonContainer, BoxLayout.PAGE_AXIS);
		BoxLayout rightBoxLayout = new BoxLayout(rightContainer,
				BoxLayout.PAGE_AXIS);

		/*
		 * Container for all left neighbors
		 */
		leftContainer.setLayout(leftBoxLayout);
		leftContainer.setOpaque(false);
		leftContainer.setPreferredSize(new Dimension(310, 400));
		leftContainer.setMinimumSize(new Dimension(310, 400));
//		leftContainer.setMaximumSize(new Dimension(310, 400));

		leftRadioButtonContainer.setLayout(leftRadioBoxLayout);
		leftRadioButtonContainer.setOpaque(false);
		leftRadioButtonContainer.setPreferredSize(new Dimension(20, 400));
		leftRadioButtonContainer.setMinimumSize(new Dimension(20, 400));
//		leftRadioButtonContainer.setMaximumSize(new Dimension(20, 400));

		/*
		 * Container for central contig
		 */
		centerContainer.setLayout(centerBoxLayout);
		centerContainer.setOpaque(false);
		centerContainer.setMinimumSize(new Dimension(310, 400));
		centerContainer.setPreferredSize(new Dimension(310, 400));
		centerContainer.setMaximumSize(new Dimension(310, 400));

		/*
		 * Container for all right neigbors
		 */
		rightContainer.setLayout(rightBoxLayout);
		rightContainer.setOpaque(false);
		rightContainer.setPreferredSize(new Dimension(310, 400));
		rightContainer.setMinimumSize(new Dimension(310, 400));
//		rightContainer.setMaximumSize(new Dimension(310, 400));

		rightRadioButtonContainer.setLayout(rightRadioBoxLayout);
		rightRadioButtonContainer.setOpaque(false);
		rightRadioButtonContainer.setPreferredSize(new Dimension(20, 400));
		rightRadioButtonContainer.setMinimumSize(new Dimension(20, 400));
//		rightRadioButtonContainer.setMaximumSize(new Dimension(20, 400));

		/*
		 * Parent Panel for all other the container of all neighbours and
		 * central contig. Used this because so I'm able to change the content
		 * of each container independently.
		 */
		chooseContigPanel.add(leftContainer);
		chooseContigPanel.add(leftRadioButtonContainer);
		chooseContigPanel.add(Box.createHorizontalGlue());
		chooseContigPanel.add(centerContainer);
		chooseContigPanel.add(Box.createHorizontalGlue());
		chooseContigPanel.add(rightRadioButtonContainer);
		chooseContigPanel.add(rightContainer);

		scrollPane = new JScrollPane(chooseContigPanel);
		scrollPane.setPreferredSize(new Dimension(1000, 400));
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setName("scroll pane");
		scrollPane.setAlignmentX(TOP_ALIGNMENT);
		scrollPane.setVisible(true);
		scrollPane.setOpaque(false);
		scrollPane.validate();
		add(scrollPane, BorderLayout.CENTER);

		/*
		 * Dieses Panel enthaelt alle Contigs dieses Genoms als Liste
		 */

		int breite = nodes[1].getId().getBytes().length;

		if (breite * 20 <= 100) {
			breite = 100;
		} else if (breite * 20 >= 200) {
			breite = 200;
		} else {
			breite = breite * 20;
		}

		/*
		 * In dieser For werden alle Contig ids gesammelt und gespeichert.
		 */
		dataForList = new String[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			String id = nodes[i].getId();
			dataForList[i] = id;
		}

		list = new JList(dataForList);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// list.setVisibleRowCount(10);
		list.addListSelectionListener(new ContigChangedListener());

		listScroller = new JScrollPane(list);
		listScroller.setBorder(BorderFactory.createTitledBorder("Contig List"));
		listScroller.setPreferredSize(new Dimension(breite, 400));
		listScroller
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScroller
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listScroller.setAlignmentY(RIGHT_ALIGNMENT);
		listScroller.setVisible(true);
		listScroller.validate();
		add(listScroller, BorderLayout.EAST);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// setSize(Toolkit.getDefaultToolkit().getScreenSize());
		// hier wird das Fenster auf die Größe des Bildschirmes angepasst.

		FlowLayout inputOptionLayout = new FlowLayout();

		inputOption = new JPanel();
		inputOption.setLayout(inputOptionLayout);

		JLabel chooseNumberOfNeighbours = new JLabel("number of neighbors");
		NumberFormat nformat = NumberFormat.getNumberInstance();

		inputOptionForNumberOfNeighbours = new JFormattedTextField(nformat);
		inputOptionForNumberOfNeighbours.setValue(new Integer(
				numberOfNeighbours));
		inputOptionForNumberOfNeighbours.setColumns(2);
		inputOptionForNumberOfNeighbours.addPropertyChangeListener("value",
				new NumberOfNeighboursListener());

		JLabel toggelBwAbsolutAndRelativeSupport = new JLabel("Support");
		JPanel toggelOption = new JPanel();

		ButtonGroup supportGroup = new ButtonGroup();

		absoluteSupport = new JRadioButton("absolute");
		absoluteSupport.setSelected(true);
		absoluteSupport.setToolTipText("  ");
		absoluteSupport.setActionCommand("absolute");
		absoluteSupport.addActionListener(new RadioButtonActionListener());
		
		relativeSupport = new JRadioButton("z-Score");
		relativeSupport.setToolTipText("  ");
		relativeSupport.setActionCommand("zScore");
		relativeSupport.addActionListener(new RadioButtonActionListener());

		supportGroup.add(absoluteSupport);
		supportGroup.add(relativeSupport);

		toggelOption.add(absoluteSupport);
		toggelOption.add(relativeSupport);

		inputOption.add(chooseNumberOfNeighbours);
		inputOption.add(inputOptionForNumberOfNeighbours);

		inputOption.add(Box.createHorizontalGlue());

		inputOption.add(toggelBwAbsolutAndRelativeSupport);
		inputOption.add(toggelOption);

		add(inputOption, BorderLayout.SOUTH);

		// glassPanel = new GlassPaneWithLines();
		// glassPanel.setName("glass pane ");
		// // glassPanel.setMaximumSize(new Dimension(1000, 400));
		// glassPanel.setPreferredSize(new Dimension(1000, 400));
		// glassPanel.setMaximumSize(new Dimension(600, (int) Toolkit
		// .getDefaultToolkit().getScreenSize().getHeight()));

		// setGlassPane(glassPanel);

		setSize(1000, 500);
		setVisible(true);

		pack();
	}

	/*
	 * create lines between the central contig and its neighbours
	 */
	@Override
	public void paint(Graphics gr) {

		super.paint(gr);
		Graphics2D g = (Graphics2D) gr;

		/*
		 * Sicherung der Positionen der Punkte, damit es keine Probleme beim
		 * Zeichnen der Linien gibt.
		 */
		leftComponentPositions = new Point[numberOfNeighbours];
		rightComponentPositions = new Point[numberOfNeighbours];

		/*
		 * Damit die Kanten "weich" gezeichnet werden.
		 */
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		/*
		 * Dieser Teil wird nur dann nicht ausgeführt, wenn die neuen Nachbarn
		 * berechnet werden.
		 */
		if (flag) {
			int laenge2 = (int) centralContig.getSize().getWidth();
			int höhe2 = (int) centralContig.getHeight();

			/*
			 * For wird hier eingesetzt um die beiden Container nacheinander zu
			 * beladen; zuerst wird diese Methode mit den rechten nachbarn und
			 * dann mit den linken nachbarn gefüttert.
			 */
			boolean left = false;
			for (int s = 0; s < 2; s++) {
				if (s == 1) {
					left = true;
				} else if (s == 0) {
					left = false;
				}

				int x2;
				int y2;
				centralPosition = new Point[2];
				if (left) {
					x2 = (int) centralContig.getX()
							+ (int) centralContig.getParent().getX();
					y2 = (int) centralContig.getY() + höhe2 + höhe2 / 2;
					centralPosition[0] = new Point(x2, y2);
				} else {
					y2 = (int) centralContig.getY() + höhe2 + höhe2 / 2;
					x2 = (int) centralContig.getX() + laenge2
							+ (int) centralContig.getParent().getX();
					centralPosition[1] = new Point(x2, y2);
				}

				/*
				 * Berechnen der Linien zwischen den linken Nachbarn und dem
				 * zentralem Contig
				 */
				if (left) {

					int z = 1;
					int zaehler = 0;

					for (Component co : leftContainer.getComponents()) {
						/*
						 * Berechnen der Liniendicke, abhängig davon ob der
						 * Nutzer den relativen oder absoluten Support wählt.
						 */
						float lineStrokeLeft;
						if (isZScore) {
							if (leftSupport[zaehler] > 0) {
								lineStrokeLeft = (float) (leftSupport[zaehler]);// /10.0);
							} else {
								lineStrokeLeft = (float) 0.01;
							}
						} else {
							float nenner = (float) (leftSupport[zaehler] - minSupport);
							float counter = (float) (maxSupport - minSupport);
							float xInIntervall = nenner / counter;

							lineStrokeLeft = (float) ((xInIntervall * 3) + 1);
						}
						z++;
						if (z % 2 == 0) {

							Point point = co.getLocation();
							ContigAppearance test2 = (ContigAppearance)co;
							/*
							 * Arbeite hier mit verschiedenen Panel. Es ist
							 * nötig sich die richtigen Positionen der Contig
							 * Panel zu berechnen, da sonst die koordinaten in
							 * einer subkomponente auf das ursprungspanel
							 * projiziert wird.
							 */
							int laenge = (int) co.getSize().getWidth();
							int x = (int) point.getX();
							int y = (int) point.getY()
									+ (int) (co.getHeight() * 1.5);
							Point currentPoint = new Point(x, y);

							leftComponentPositions[zaehler] = currentPoint;

							if(test2.isAnderweitigAusgewaehlt()){
								float[] dash2 = {30,10};
								g.setColor(Color.DARK_GRAY);
								g.setStroke(new BasicStroke(lineStrokeLeft, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash2 , 0));
							}else if(test2.isSelected()){
								g.setColor(Color.BLACK);
								g.setStroke(new BasicStroke(lineStrokeLeft));
							}else{
								float[] dash = {2,2};
								g.setColor(Color.GRAY);
								g.setStroke(new BasicStroke(lineStrokeLeft, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1 , dash, 0));
							}
							g.drawLine(x + laenge, y, x2, y2);

							zaehler++;
						}
					}

				} else {
					/*
					 * äquivalent zu oben, nur das dies hier für die rechten
					 * Nachbarn ist.
					 */
					int z = 1;
					int c = 0;

					for (Component co : rightContainer.getComponents()) {

						float lineStroke;
						if (isZScore) {
							if (rightSupport[c] > 0) {
								lineStroke = (float) (rightSupport[c]);// /10.0);
							} else {
								lineStroke = (float) 0.01;
							}
						} else {
							float nenner = (float) (rightSupport[c] - minSupport);
							float counter = (float) (maxSupport - minSupport);
							float xInIntervall = nenner / counter;
							lineStroke = (float) ((xInIntervall * 3) + 1);
						}
						z++;
						if (z % 2 == 0) {

							Point point = co.getLocation();
							ContigAppearance test = (ContigAppearance)co;
							
							/*
							 * Arbeite hier mit verschiedenen Panel. Es ist
							 * nötig sich die richtigen Positionen der Contig
							 * Panel zu berechnen, da sonst die koordinaten in
							 * einer subkomponente auf das ursprungspanel
							 * projiziert wird.
							 */
							int x = (int) co.getParent().getX();
							int y = (int) point.getY()
									+ (int) (1.5 * co.getHeight());
							Point currentPoint = new Point(x, y);

							rightComponentPositions[c] = currentPoint;

							if(test.isAnderweitigAusgewaehlt()){
								float[] dash2 = {30,10};
								g.setColor(Color.DARK_GRAY);
								g.setStroke(new BasicStroke(lineStroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash2 , 0));
							}else if(test.isSelected()){
								g.setColor(Color.BLACK);
								g.setStroke(new BasicStroke(lineStroke));
							}else{
								float[] dash = {2,2};
								g.setColor(Color.GRAY);
								g.setStroke(new BasicStroke(lineStroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1 , dash, 0));
							}
							g.drawLine(x, y, x2, y2);
							c++;
						}
					}
				}
			}
		}
	}

	/*
	 * Fange Events ab
	 */
	@Override
	public void event_fired(CagEvent event) {

		/*
		 * Wenn ein Contig aus der Liste oder aus dem Panel ausgewählt wurde ist
		 * es das Contig das nun im Mittelpunkt der Betrachtung steht- das
		 * aktuelle Contig. Dies muss auch in der Ansicht angezeigt werden. Wozu
		 * das abgefange Event dienlich ist.
		 */
		if (event.getEvent_type().equals(EventType.EVENT_CHOOSED_CONTIG)) {

			DNASequence currentContig = event.getContigNode();
			LayoutGraph graph = this.layoutGraph;
			centralContigIndex = event.getIndex();
			boolean isReverse = event.isReverse();
			boolean isSelected = false;
			for (AdjacencyEdge edge : selectedRadioButtons) {
				if (edge.geti() == centralContigIndex
						|| edge.getj() == centralContigIndex) {
					isSelected = true;
					break;
				}
			}

			centralContig = new ContigAppearance(currentContig,
					centralContigIndex, isSelected, isReverse,
					maxSizeOfContigs, minSizeOfContigs);

			if (centerContainer.getComponentCount() > 0) {
				centerContainer.removeAll();
			}
			// centerContainer.setAlignmentY(TOP_ALIGNMENT);
			centerContainer.add(centralContig);
			centerContainer.updateUI();
		}
		/*
		 * Auch die Nachbarn ändern sich, wenn das Contig sich ändert.
		 */
		if (event.getEvent_type().equals(EventType.EVENT_SEND_LEFT_NEIGHBOURS)) {

			leftNeighbourEdges = event.getEdges();
			ContigAppearance contigPanel = null;

			ContigRadioButton radioButton;
			leftSupport = new double[numberOfNeighbours];
			int t = 0;

			if (leftContainer.getComponentCount() > 0
					|| leftRadioButtonContainer.getComponentCount() > 0) {
				leftContainer.removeAll();
				leftRadioButtonContainer.removeAll();
			}

			/*
			 * Der Terminator muss entweder nach der Anzahl der Nachbarn zum
			 * Abbruch führen oder aber, wenn weniger Nachbarn auswählbar sind,
			 * nach dieser geringeren Anzahl einen Abbruch herbei führen.
			 */
			int terminator = leftNeighbourEdges.size();
			if (numberOfNeighbours <= leftNeighbourEdges.size()) {
				terminator = numberOfNeighbours;
			} else if (numberOfNeighbours > leftNeighbourEdges.size()) {
				terminator = leftNeighbourEdges.size();
			}

			isALeftNeighourSelected = false;

			for (AdjacencyEdge e : leftNeighbourEdges) {
				if (e.isSelected()) {
					isALeftNeighourSelected = true;
				}
			}

			for (AdjacencyEdge edge : leftNeighbourEdges) {

				if (t < terminator) {

					DNASequence dnaSequence;
					int indexOfContig;
					if (edge.geti() == centralContigIndex) {
						dnaSequence = edge.getContigj();
						indexOfContig = edge.getj();
					} else {
						dnaSequence = edge.getContigi();
						indexOfContig = edge.geti();
					}

					if (isZScore) {
						leftSupport[t] = ((edge.getSupport() - meanForLeftNeighbours[centralContigIndex])
								/ sDeviationForLeftNeighbours[centralContigIndex]);
					} else {
						leftSupport[t] = edge.getSupport();
					}

					boolean anderweitigAusgewaehlt = false;
					AdjacencyEdge test = ausgewaehlteLinkeKanten
							.get(indexOfContig);

					if (test != null && !dnaSequence.isRepetitive()) {
						anderweitigAusgewaehlt = true;
					}

					contigPanel = new ContigAppearance(layoutGraph, edge,
							indexOfContig, true, maxSizeOfContigs,
							minSizeOfContigs, anderweitigAusgewaehlt);
					/*
					 * Help that the user is only able to select one neighbour
					 * for each side.
					 */
					contigPanel.setAlignmentX(RIGHT_ALIGNMENT);
					contigPanel.addMouseListener(new ContigMouseListener());
					contigPanel.setVisible(true);

					radioButton = new ContigRadioButton(edge, contigPanel);

					if (anderweitigAusgewaehlt) {
						radioButton.setActionCommand("anderweitigAusgewaehlt");
					}
					if(isALeftNeighourSelected){
						radioButton.setActionCommand("nachbarAusgewaehlt");
					}
					radioButton.setLeft(true);
					radioButton.setBackground(Color.WHITE);
					radioButton
							.addActionListener(new RadioButtonActionListener());

					// add here Contigs and RadioButton with automatical space
					leftContainer.add(contigPanel);

					leftGroup.add(radioButton);
					leftRadioButtonContainer.add(radioButton);

					if (t < (numberOfNeighbours - 1)) {
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
		}

		/*
		 * Hier kommen die rechten Nachbarn an:
		 */
		if (event.getEvent_type().equals(EventType.EVENT_SEND_RIGHT_NEIGHBOURS)) {

			int s = 0;

			rightNeighbourEdges = event.getEdges();
			ContigAppearance contigPanel = null;

			/*
			 * Der Terminator muss entweder nach der Anzahl der Nachbarn zum
			 * Abbruch führen oder aber, wenn weniger Nachbarn auswählbar sind,
			 * nach dieser geringeren Anzahl einen Abbruch herbei führen.
			 */
			int terminator = rightNeighbourEdges.size();
			if (numberOfNeighbours < rightNeighbourEdges.size()) {
				terminator = numberOfNeighbours;
			} else if (numberOfNeighbours > rightNeighbourEdges.size()) {
				terminator = rightNeighbourEdges.size();
			}

			ContigRadioButton radioButton;
			rightSupport = new double[numberOfNeighbours];

			/*
			 * Zunächst Löschen aller bisherigen Elemente in der GUI
			 */
			if (rightContainer.getComponentCount() > 0
					|| rightRadioButtonContainer.getComponentCount() > 0) {
				rightContainer.removeAll();
				rightRadioButtonContainer.removeAll();
			}

			// Flag das gesetzt wird sollte einer der Nachbarn schon ausgewählt
			// worden sein.
			isARightNeighourSelected = false;
			/*
			 * Setzten des Flags, falls einer der Nachbarn schon ausgewählt ist
			 * er das so wird damit kein anderer rechter Nachbarn auswählbar.
			 */
			for (AdjacencyEdge e : rightNeighbourEdges) {
				if (e.isSelected()) {
					isARightNeighourSelected = true;
				}
			}

			for (AdjacencyEdge edge : rightNeighbourEdges) {
				if (s < terminator) {
					DNASequence dnaSequence;
					int indexOfContig;

					if (edge.geti() == centralContigIndex) {
						dnaSequence = edge.getContigj();
						indexOfContig = edge.getj();
					} else {
						dnaSequence = edge.getContigi();
						indexOfContig = edge.geti();
					}

					/*
					 * Speichern des relativen oder absoluten Support in einem
					 * Array, dieses Array wird später dem GlasPanel übergeben
					 * und die Liniendicke berechnet.
					 */
					if (isZScore) {
						rightSupport[s] =((edge.getSupport() - meanForRightNeighbours[centralContigIndex]) 
										/ sDeviationForRightNeighbours[centralContigIndex]);
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
					boolean anderweitigAusgewaehlt = false;
					AdjacencyEdge test = ausgewaehlteLinkeKanten
							.get(indexOfContig);

					if (test != null && !dnaSequence.isRepetitive()) {
						anderweitigAusgewaehlt = true;
					}
					contigPanel = new ContigAppearance(layoutGraph, edge,
							indexOfContig, false, maxSizeOfContigs,
							minSizeOfContigs, anderweitigAusgewaehlt);
					contigPanel.setAlignmentX(LEFT_ALIGNMENT);
					contigPanel.addMouseListener(new ContigMouseListener());
					contigPanel.setVisible(true);

					/*
					 * Zu jedem Nachbarn wird auch ein RadioButton erstellt mit
					 * dem man einen dieser Contigs auswählen kann. Damit die
					 * Button unterscheidbar sind, werden ihm die Kante und der
					 * Index des zentralen Contigs übergeben
					 */
					radioButton = new ContigRadioButton(edge, contigPanel);

					if (anderweitigAusgewaehlt) {
						radioButton.setActionCommand("anderweitigAusgewaehlt");
					}
					if(isARightNeighourSelected){
						radioButton.setActionCommand("nachbarAusgewaehlt");
					}
					radioButton.setLeft(false);
					radioButton.setBackground(Color.WHITE);
					radioButton
							.addActionListener(new RadioButtonActionListener());

					rightGroup.add(radioButton);
					rightContainer.add(contigPanel);
					rightRadioButtonContainer.add(radioButton);

					if (s < (numberOfNeighbours - 1)) {
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
			flag = true;
		}

	}

	/*
	 * Inner class for starting an thread for calculation of left neighbours
	 */
	class SwingWorkerClass extends SwingWorker<String, String> {

		@Override
		protected String doInBackground() {
			model.sendLeftNeighbours();
			return null;
		}

		@Override
		protected void done() {
			super.done();
			window.repaint();
		}
	}

	/*
	 * Inner class for starting an thread for calculation of left neighbours
	 */
	class ThreadClassForRightNeighours extends SwingWorker<String, String> {

		@Override
		protected String doInBackground() {
			model.sendRightNeighbours();
			return null;
		}

		@Override
		protected void done() {
			super.done();
			window.repaint();
		}
	}

	/*
	 * Listener für die Elemente der Contig Liste. D.h. wenn auf ein Element in
	 * der Contig Liste geklickt wird, dann wird dieses Element als das
	 * zentrales Contig dargestellt und die dazugehörigen Nachbarn berechnet.
	 */
	public class ContigChangedListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			if (e.getValueIsAdjusting() == false) {

				int index = 0;
				String selection = (String) list.getSelectedValue();

				for (int i = 0; i < dataForList.length; i++) {
					if (dataForList[i].equals(selection)) {
						index = i;
					}
				}

				model.changeContigs(index, false);
				flag = false;

				ThreadClassForRightNeighours threadForRightNeighbours = new ThreadClassForRightNeighours();
				threadForRightNeighbours.execute();

				SwingWorkerClass threadForLeftNeighbours = new SwingWorkerClass();
				threadForLeftNeighbours.execute();
			}
		}
	}

	/*
	 * Inner class for radion Buttons if the use click an radio Button the
	 * corresponding contig is marked as selected
	 */
	public class RadioButtonActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Neu löse radio button event aus ");
			/*
			 * Hier wird auf den Toggel zwischem relativem und absolutem Support
			 * reagiert.
			 */
			if (e.getActionCommand().equals("absolute")) {
				isZScore = false;
				absoluteSupport.setSelected(true);
				relativeSupport.setSelected(false);
				if (rightContainer.getComponentCount() != 0
						|| leftContainer.getComponentCount() != 0) {
					model.sendCurrentContig();
					model.sendLeftNeighbours();
					model.sendRightNeighbours();
					repaint();
				}
			} else if (e.getActionCommand().equals("zScore")) {
				isZScore = true;
				absoluteSupport.setSelected(false);
				relativeSupport.setSelected(true);
				if (rightContainer.getComponentCount() != 0
						|| leftContainer.getComponentCount() != 0) {
					model.sendCurrentContig();
					model.sendLeftNeighbours();
					model.sendRightNeighbours();
					repaint();
				}
//			} if(e.getActionCommand().equals("nachbarAusgewaehlt")) {
			}else{
				Vector<AdjacencyEdge> copyOfSelectedContigs = (Vector<AdjacencyEdge>) selectedRadioButtons
						.clone();

				ContigRadioButton radioButton = (ContigRadioButton) e
						.getSource();
				AdjacencyEdge selectedEdge = radioButton.getEdge();

				System.out.println("edge " + selectedEdge + " i "
						+ selectedEdge.geti() + " "
						+ selectedEdge.getContigi().getId() + " j "
						+ selectedEdge.getj() + " "
						+ selectedEdge.getContigj().getId());

				int indexLeft = -1;
				int indexRight = -1;

				if ((radioButton.isLeft() && !centralContig.isReverse())
						|| (!radioButton.isLeft() && centralContig.isReverse())) {

					if (centralContigIndex == selectedEdge.geti()) {
						indexLeft = selectedEdge.getj();
						indexRight = selectedEdge.geti();
					} else {
						indexLeft = selectedEdge.geti();
						indexRight = selectedEdge.getj();
					}

				} else if (!radioButton.isLeft() && !centralContig.isReverse()
						|| (radioButton.isLeft() && centralContig.isReverse())) {

					if (centralContigIndex == selectedEdge.geti()) {
						indexLeft = selectedEdge.geti();
						indexRight = selectedEdge.getj();
					} else {
						indexLeft = selectedEdge.getj();
						indexRight = selectedEdge.geti();
					}
				}

				/*
				 * Mein Initialfall: wurden bisher keine Contigs ausgewählt oder
				 * alle bisher ausgewählten Contigs gelöscht, wird dieser Teil
				 * aufgerufen. Danach ist eine Differenzierung notwenig.
				 */
				if (selectedRadioButtons.size() == 0) {
					// System.out.println("Contig wird hinzugefügt");
					selectedEdge.select();
					
					selectedRadioButtons.add(selectedEdge);
//					selectedContigs.add(radioButton.getContigObject().getI(), selectedEdge);

					ausgewaehlteLinkeKanten.add(indexLeft, selectedEdge);
					ausgewaehlteRechteKanten.add(indexRight, selectedEdge);

					model.addSelectedContig(selectedEdge);
//					model.addSelectedContig(selectedEdge,radioButton.getContigObject().getI());
					model.sendCurrentContig();
					model.sendLeftNeighbours();
					model.sendRightNeighbours();
					repaint();
				}
				/*
				 * Vllt ist die Seite noch relevant dann in der
				 * radionButtonClass hinzufügen
				 */
				// für jede bisher ausgewählte Kante
				boolean selbeKante = false;
				boolean linkeKante = false;
				boolean rechteKante = false;
				boolean nichtMöglich = false;
				/*
				 * Zunächst wird für alle bisher gespeicherten Contigs
				 * festgestellt ob
				 */
				for (AdjacencyEdge savedEdge : copyOfSelectedContigs) {
					// schon dieselbe Kante ausgewählt wurde
					if (selectedEdge.equals(savedEdge)) {
						selbeKante = true;
						// oder ob auf der rechten Seite des zentralen Contigs
						// ein Nachbar eingefügt werden kann
					} else if (!isARightNeighourSelected
							&& !radioButton.isLeft()) {
						rechteKante = true;
						// oder ob auf der rechten Seite des zentralen Contigs
						// ein Nachbar eingefügt werden kann
					} else if (!isALeftNeighourSelected && radioButton.isLeft()) {
						linkeKante = true;
						// oder ob ein anderer Nachbar schon ausgewählt wurde
					} else if (isALeftNeighourSelected
							|| isARightNeighourSelected) {
						nichtMöglich = true;
					}
				}
				/*
				 * Nun weiß ich als ich die jetzt ausgwählte Kante behandeln
				 * muss und kann entsprechend reagieren. Ist es dieselbe Kante:
				 * Gebe ich dem Benutzer die Möglichkeit die Kante zu löschen,
				 * wenn er dies wünsch.
				 */
				if (selbeKante) {
					Object[] options = { "Yes", "No" };

					int n = javax.swing.JOptionPane.showOptionDialog(window,
							"You already selected this neighbour.\n"
									+ " Do you want to delete this selection?",
							"", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);

					if (n == JOptionPane.YES_OPTION) {

						ausgewaehlteLinkeKanten.remove(selectedEdge);
						ausgewaehlteRechteKanten.remove(selectedEdge);
						selectedEdge.deselect();
						selectedRadioButtons.remove(selectedEdge);
						model.removeSelectedEdge(selectedEdge);
						repaint();
					}
				}

				/*
				 * Oder füge die Kante zu den Ausgewählten Contigs hinzu
				 */
				else if (rechteKante) {
					System.out
							.println("füge rechte kante ein und ein rechter nachbar ist ausgewählt "
									+ isARightNeighourSelected);
					selectedEdge.select();
					selectedRadioButtons.add(selectedEdge);
					model.addSelectedContig(selectedEdge);

					ausgewaehlteLinkeKanten.add(indexLeft, selectedEdge);
					ausgewaehlteRechteKanten.add(indexRight, selectedEdge);
					System.out
							.println("linke ausgewählte nachbarn kante an index "
									+ ausgewaehlteLinkeKanten
											.indexOf(selectedEdge));
					System.out
							.println("rechte ausgewählte nachbarn kante an index "
									+ ausgewaehlteRechteKanten
											.indexOf(selectedEdge));

					model.sendCurrentContig();
					model.sendLeftNeighbours();
					model.sendRightNeighbours();
					repaint();
				}

				else if (linkeKante) {
					System.out
							.println("füge linke kante ein und ein linken nachbar ist ausgewählt "
									+ isALeftNeighourSelected);
					selectedEdge.select();
					selectedRadioButtons.add(selectedEdge);
					model.addSelectedContig(selectedEdge);

					ausgewaehlteLinkeKanten.add(indexLeft, selectedEdge);
					ausgewaehlteRechteKanten.add(indexRight, selectedEdge);
					System.out
							.println("linke ausgewählte nachbarn kante an index "
									+ ausgewaehlteLinkeKanten
											.indexOf(selectedEdge));
					System.out
							.println("rechte ausgewählte nachbarn kante an index "
									+ ausgewaehlteRechteKanten
											.indexOf(selectedEdge));

					model.sendCurrentContig();
					model.sendLeftNeighbours();
					model.sendRightNeighbours();
					repaint();
				}
				/*
				 * Oder aber ich sage dem Benutzer, dass er das angeklickte
				 * Contig nicht auswählen kann, weil er bereits einen Nachbar
				 * erwählt hat.
				 */
				else if (nichtMöglich && !selbeKante) {
					javax.swing.JOptionPane
							.showMessageDialog(
									window,
									"Sorry.\n"
											+ "You already selected a neighbour for this contig.\n");
				}

			}
			if (e.getActionCommand().equals("anderweitigAusgewaehlt")) {

				javax.swing.JOptionPane
						.showMessageDialog(
								window,
								"Sorry.\n"
										+ "You already selected this neighbour for an another contig.\n"
										+ "If you want to delete this selection, you have to click"
										+ "on this contig and delete that selection.\n"
										+ "Then you are able to select this selection");

				/*
				 * TODO Löschoption Hier kann erweitert werden, dass die andere
				 * Kante gelöscht wird. Dazu muss erst im linken und rechten
				 * Nachbarvektor ermittelt werden, in welchem der Index des
				 * Nachbarn belegt ist. Darüber kann dann die Kante erhalten
				 * werden und damit der Index des anderen contig im anderen
				 * Vektor. Dann muss aus beiden Vektoren der Index frei gemacht
				 * werden.
				 * 
				 * 
				 * Bem für Repeats: Hier muss ich noch einen nachschauen, ob im
				 * Vektor an einem index mehrere kanten gespeichert werden
				 * können. und dann muss hier beim löschen auch darauf geachtet
				 * werden, dass auch die richtige kante gelöscht wird.
				 */
				// Object[] options = { "Yes", "No" };
				//		
				// int n = javax.swing.JOptionPane.showOptionDialog(window,
				// "You already selected this neighbour for an another contig..\n"
				// + " Do you want to delete that selection?",
				// "", JOptionPane.YES_NO_OPTION,
				// JOptionPane.QUESTION_MESSAGE, null, options,
				// options[0]);
				//
				// if (n == JOptionPane.YES_OPTION) {
				//
				// ausgewaehlteLinkeKanten.remove(andereselectedEdge);
				// ausgewaehlteRechteKanten.remove(andereselectedEdge);
				// selectedEdge.deselect();
				// selectedRadioButtons.remove(andereselectedEdge);
				// model.removeSelectedEdge(andereselectedEdge);
				// repaint();
			}

		}
	}

	public class ContigMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			/*
			 * Dieses Event wird ausgelöst, wenn auf ein Contig geklickt wird.
			 * Das angeklickte Contig wird zu dem zentralem Contig und die neuen
			 * Nachbarn werden berechnet.
			 */
			ContigAppearance contigPanel = (ContigAppearance) e.getSource();

			int index = contigPanel.getI();
			boolean currentContigIsReverse = contigPanel.isReverse();

			model.changeContigs(index, currentContigIsReverse);
			flag = false;

			ThreadClassForRightNeighours threadForRightNeighbours = new ThreadClassForRightNeighours();
			threadForRightNeighbours.execute();

			SwingWorkerClass threadForLeftNeighbours = new SwingWorkerClass();
			threadForLeftNeighbours.execute();

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// Auto-generated method stub
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// Auto-generated method stub
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// Auto-generated method stub
		}
	}

	/*
	 * Listener für MenuItems
	 */
	public class ExitItemListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			window.dispose();
		}
	}

	public class NumberOfNeighboursListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			/*
			 * Dieses Event wird ausgelöst, wenn der Benutzer eine andere Anzahl
			 * an Nachbarn sehen möchte. Allerdings habe ich diese Anzahl auf 10
			 * beschränkt.
			 */
			int neighboursNumber = ((Number) evt.getNewValue()).intValue();
			System.out.println("neue anzahl an nachbarn " + neighboursNumber);

			if (neighboursNumber <= 10 && neighboursNumber > 0) {

				if (neighboursNumber < numberOfNeighbours) {
					chooseContigPanel
							.setPreferredSize(new Dimension(1000, 400));
				}

				if (neighboursNumber > 8) {
					chooseContigPanel
							.setPreferredSize(new Dimension(1000, 600));
				}
				
				numberOfNeighbours = neighboursNumber;
				inputOptionForNumberOfNeighbours.setValue(new Integer(
						numberOfNeighbours));
				model.setNeighbourNumber(numberOfNeighbours);

				if (rightContainer.getComponentCount() != 0
						|| leftContainer.getComponentCount() != 0) {
					model.sendLeftNeighbours();
					model.sendRightNeighbours();
				}
				repaint();
			} else if (neighboursNumber == 0) {
				javax.swing.JOptionPane.showMessageDialog(window, "Sorry.\n"
						+ "You can't choose " + neighboursNumber
						+ " Neighbours.\n" + "Please choose between 1 and 10.");
				numberOfNeighbours = 5;
				inputOptionForNumberOfNeighbours.setValue(new Integer(
						numberOfNeighbours));

				model.setNeighbourNumber(numberOfNeighbours);
				if (rightContainer.getComponentCount() != 0
						|| leftContainer.getComponentCount() != 0) {
					model.sendLeftNeighbours();
					model.sendRightNeighbours();
					repaint();
				}
			} else {
				javax.swing.JOptionPane.showMessageDialog(window, "Sorry.\n"
						+ "You can't choose " + neighboursNumber
						+ " Neighbours.\n" + "Please choose between 1 and 10.");

				numberOfNeighbours = 10;
				inputOptionForNumberOfNeighbours.setValue(new Integer(
						numberOfNeighbours));
				model.setNeighbourNumber(numberOfNeighbours);
				chooseContigPanel
				.setPreferredSize(new Dimension(1000, 600));

				if (rightContainer.getComponentCount() != 0
						|| leftContainer.getComponentCount() != 0) {
					model.sendLeftNeighbours();
					model.sendRightNeighbours();
					repaint();
				}
			}

		}

	}

}