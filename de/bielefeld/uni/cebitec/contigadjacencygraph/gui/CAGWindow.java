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
import javax.swing.plaf.basic.BasicTreeUI.SelectionModelPropertyChangeHandler;
import javax.swing.text.NumberFormatter;

import com.sun.media.sound.RIFFWriter;
import com.sun.rowset.providers.RIOptimisticProvider;
import com.sun.xml.internal.bind.v2.model.core.Adapter;

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
	private DNASequence[] nodes;
	private CagCreator model;

	private ChooseContigPanel chooseContigPanel;
	private JScrollPane scrollPane;

	private double[] leftSupport;
	private double[] rightSupport;

	private ContigAppearance centralContig;

	private ContigButtonGroup leftGroup = new ContigButtonGroup();
	private ContigButtonGroup rightGroup = new ContigButtonGroup();

	private Vector<AdjacencyEdge> selectedRadioButtons;

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

	private Vector<Vector<AdjacencyEdge>> ausgewaehlteLinkeKanten = new Vector<Vector<AdjacencyEdge>>();
	private Vector<Vector<AdjacencyEdge>> ausgewaehlteRechteKanten = new Vector<Vector<AdjacencyEdge>>();

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
		int term = ausgewaehlteLinkeKanten.size();
		int term2 = ausgewaehlteRechteKanten.size();

		for (int i = 0; i < ausgewaehlteLinkeKanten.size(); i++) {
			Vector<AdjacencyEdge> contigVector = new Vector<AdjacencyEdge>();
			ausgewaehlteLinkeKanten.add(i,contigVector);
			if(i == term-1){
				break;
			}
		}		

		for (int i = 0; i < ausgewaehlteRechteKanten.size(); i++) {
			Vector<AdjacencyEdge> contigVector = new Vector<AdjacencyEdge>();
			ausgewaehlteRechteKanten.add(i,contigVector);
			if(i == term2-1){
				break;
			}
		}

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

		chooseContigPanel = new ChooseContigPanel(numberOfNeighbours, isZScore,
				maxSupport, minSupport);
		chooseContigPanel.setNumberOfNeighbours(numberOfNeighbours);
		chooseContigPanel.setZScore(isZScore);
		chooseContigPanel.setMaxSupport(maxSupport);
		chooseContigPanel.setMinSupport(minSupport);
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
		this.setMinimumSize(new Dimension((int) Toolkit.getDefaultToolkit()
				.getScreenSize().getWidth()
				- breite, 500));
		this.setPreferredSize(new Dimension((int) Toolkit.getDefaultToolkit()
				.getScreenSize().getWidth()
				- breite, 500));
		this.setMaximumSize(new Dimension((int) Toolkit.getDefaultToolkit()
				.getScreenSize().getWidth()
				- breite, 500));
		scrollPane = new JScrollPane(chooseContigPanel);
		scrollPane.setPreferredSize(new Dimension((int) Toolkit
				.getDefaultToolkit().getScreenSize().getWidth()
				- breite, 500));
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
		list.setToolTipText("<html>Choose a contig<br>"+
				" by a click on a name.</html>");

		listScroller = new JScrollPane(list);
		listScroller.setToolTipText("<html>Choose a contig<br>"+
				" by a click on a name.</html>");
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
		chooseNumberOfNeighbours.setToolTipText("<html>"
				+ "Here you are able to choose the number of neighbours.<br>"
				+ "Please type a number between 1 and 10 in the textfield<br>"
				+ "and press ENTER."
				+ "</html>");
		NumberFormat nformat = NumberFormat.getNumberInstance();

		inputOptionForNumberOfNeighbours = new JFormattedTextField(nformat);
		inputOptionForNumberOfNeighbours.setValue(new Integer(
				numberOfNeighbours));
		inputOptionForNumberOfNeighbours.setColumns(2);
		inputOptionForNumberOfNeighbours.addPropertyChangeListener("value",
				new NumberOfNeighboursListener());
		inputOptionForNumberOfNeighbours.setToolTipText("<html>"
				+ "Please type a number <br>" +
						"between 1 and 10<br>"
				+ "and press ENTER/Return."
				+ "</html>");
		
		JLabel toggelBwAbsolutAndRelativeSupport = new JLabel("Support");
		toggelBwAbsolutAndRelativeSupport.setToolTipText("Choose a support option.");
		JPanel toggelOption = new JPanel();

		ButtonGroup supportGroup = new ButtonGroup();

		absoluteSupport = new JRadioButton("absolute");
		absoluteSupport.setSelected(true);
		absoluteSupport.setToolTipText("<html>If you choose this option<br> you see at each line <br>" +
				"the likelyhood score.</html> ");
		absoluteSupport.setActionCommand("absolute");
		absoluteSupport.addActionListener(new RadioButtonActionListener());

		relativeSupport = new JRadioButton("z-Score");
		relativeSupport.setToolTipText("<html> If you choose this option <br>you see at each line <br> "
				+" a normalized score.</html>");
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

		setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()
				- breite, 600);
		setVisible(true);

		pack();
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

			if (!ausgewaehlteLinkeKanten.elementAt(centralContigIndex).isEmpty()
					|| !ausgewaehlteRechteKanten.elementAt(centralContigIndex).isEmpty()) {
				isSelected = true;
			}

			centralContig = new ContigAppearance(currentContig,
					centralContigIndex, isSelected, isReverse,
					maxSizeOfContigs, minSizeOfContigs);

			JPanel centerContainer = chooseContigPanel.getCenterContainer();

			if (centerContainer.getComponentCount() > 0) {
				centerContainer.removeAll();
			}
			// centerContainer.setAlignmentY(TOP_ALIGNMENT);
			chooseContigPanel.setCentralContig(centralContig);
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

			JPanel leftContainer = chooseContigPanel.getLeftContainer();
			JPanel leftRadioButtonContainer = chooseContigPanel
					.getLeftRadioButtonContainer();

			clearComponets(leftContainer, leftRadioButtonContainer);

			/*
			 * Der Terminator muss entweder nach der Anzahl der Nachbarn zum
			 * Abbruch führen oder aber, wenn weniger Nachbarn auswählbar sind,
			 * nach dieser geringeren Anzahl einen Abbruch herbei führen.
			 */
			int terminator = setTerminator(leftNeighbourEdges);

			isALeftNeighourSelected = false;
			AdjacencyEdge whichNeighbourIsSelected = null;

			for (AdjacencyEdge e : leftNeighbourEdges) {
				if (e.isSelected()) {
					isALeftNeighourSelected = true;
					whichNeighbourIsSelected = e;
				}
			}

			for (AdjacencyEdge edge : leftNeighbourEdges) {

				if (t < terminator) {

					int indexOfContig = indexOfNeighbourContig(edge);
					

					if (isZScore) {
						leftSupport[t] = calculateZScore(edge,
								centralContigIndex, meanForLeftNeighbours,
								sDeviationForLeftNeighbours);
					} else {
						leftSupport[t] = edge.getSupport();
					}

					Vector<Vector<AdjacencyEdge>> linkeKanten = centralContig.isReverse() ? ausgewaehlteRechteKanten : ausgewaehlteLinkeKanten;
					boolean anderweitigAusgewaehlt = ulteriorSelected(true, indexOfContig, edge, linkeKanten);

					contigPanel = new ContigAppearance(layoutGraph, edge,
							indexOfContig, true, maxSizeOfContigs,
							minSizeOfContigs, anderweitigAusgewaehlt);
					contigPanel.addMouseListener(new ContigMouseListener());
					contigPanel.setAlignmentX(RIGHT_ALIGNMENT);

					radioButton = new ContigRadioButton(edge, contigPanel);

					if (isALeftNeighourSelected) {
						radioButton.setActionCommand("nachbarAusgewaehlt");
						radioButton
						.setSelectedNeighbourOfButtonGroup(whichNeighbourIsSelected);
					}else 	if (!isALeftNeighourSelected) {
						radioButton
						.setActionCommand("noch kein nachbar ausgewaehlt");
					}
					
					
					if (anderweitigAusgewaehlt) {
						radioButton.setActionCommand("anderweitigAusgewaehlt");
						AdjacencyEdge otherEdgeForThisNeighbour = linkeKanten.get(indexOfContig).firstElement();
						radioButton
								.setNeighboursForTheThisNeighbour(otherEdgeForThisNeighbour);
					}
					if (edge.isSelected()) {
						radioButton.setSelected(true);
					}
//					System.out.println("l "+radioButton.getActionCommand());
					radioButton.setLeft(true);
					radioButton.setOpaque(false);
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
			chooseContigPanel.setLeftSupport(leftSupport);
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
			int terminator = setTerminator(rightNeighbourEdges);

			ContigRadioButton radioButton;
			rightSupport = new double[numberOfNeighbours];

			/*
			 * Zunächst Löschen aller bisherigen Elemente in der GUI
			 */
			JPanel rightContainer = chooseContigPanel.getRightContainer();
			JPanel rightRadioButtonContainer = chooseContigPanel
					.getRightRadioButtonContainer();
			clearComponets(rightContainer, rightRadioButtonContainer);

			// Flag das gesetzt wird sollte einer der Nachbarn schon ausgewählt
			// worden sein.
			isARightNeighourSelected = false;
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
					if (isZScore) {
						rightSupport[s] = calculateZScore(edge,
								centralContigIndex, meanForRightNeighbours,
								sDeviationForRightNeighbours);
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
					Vector<Vector<AdjacencyEdge>> rechteKanten = centralContig.isReverse() ? ausgewaehlteLinkeKanten : ausgewaehlteRechteKanten;
					boolean anderweitigAusgewaehlt = ulteriorSelected(false, indexOfContig, edge, rechteKanten);
					
					contigPanel = new ContigAppearance(layoutGraph, edge,
							indexOfContig, false, maxSizeOfContigs,
							minSizeOfContigs, anderweitigAusgewaehlt);
					contigPanel.setAlignmentX(LEFT_ALIGNMENT);
					contigPanel.addMouseListener(new ContigMouseListener());

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
						radioButton
						.setSelectedNeighbourOfButtonGroup(neighbourForThisGroup);
				
					} else if (!isARightNeighourSelected) {
						radioButton
						.setActionCommand("noch kein nachbar ausgewaehlt");
					}
					if (anderweitigAusgewaehlt) {
						radioButton.setActionCommand("anderweitigAusgewaehlt");
						AdjacencyEdge otherEdge = rechteKanten.get(indexOfContig).firstElement();
						radioButton.setNeighboursForTheThisNeighbour(otherEdge);
						
					}

					radioButton.setNachbarIndex(indexOfContig);
					radioButton.setCentralIndex(centralContigIndex);
					radioButton.setLeft(false);
					radioButton.setOpaque(false);
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
			chooseContigPanel.setRightSupport(rightSupport);
		}

	}

	private int setTerminator(Vector<AdjacencyEdge> neighbourVector) {

		int value = neighbourVector.size();
		if (numberOfNeighbours < neighbourVector.size()) {
			value = numberOfNeighbours;
		} else if (numberOfNeighbours > neighbourVector.size()) {
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

		if (edge.geti() == centralContigIndex) {
			index = edge.getj();
		} else {
			index = edge.geti();
		}

		return index;
	}

	private int indexOfCentralContig(AdjacencyEdge edge) {

		int index;

		if (edge.geti() == centralContigIndex) {
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
	private boolean ulteriorSelected(boolean isLeft, int indexOfNeighbour, AdjacencyEdge edge, Vector<Vector<AdjacencyEdge>> selectedEdges){//, boolean isCentralContigReverse) {
		boolean isSelected = false;
		
		if(isLeft){
			if(!selectedEdges.get(indexOfNeighbour).isEmpty() 
					&& !layoutGraph.getNodes().get(indexOfNeighbour).isRepetitive()){

				AdjacencyEdge other = selectedEdges.get(indexOfNeighbour).firstElement();

				int i =indexOfCentralContig(other); 
				isSelected = true;
				 if (i == centralContigIndex){
					 isSelected = false;
				 }
				
			}
		}else{
			if(!selectedEdges.get(indexOfNeighbour).isEmpty()
					&& !layoutGraph.getNodes().get(indexOfNeighbour).isRepetitive()){
				
				AdjacencyEdge other = selectedEdges.get(indexOfNeighbour).firstElement();

				int i =indexOfCentralContig(other);
				isSelected = true;
				
				 if (i == centralContigIndex){
					 isSelected = false;
				 }
			}
		}

		return isSelected;
	}
	
	private void clearComponets(JPanel contigContainer, JPanel radioButtonContainer){
		if (contigContainer.getComponentCount() > 0
				|| radioButtonContainer.getComponentCount() > 0) {
			contigContainer.removeAll();
			radioButtonContainer.removeAll();
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
			chooseContigPanel.setFlag(true);
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
				chooseContigPanel.setFlag(false);

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
			//System.out.println("Neu löse radio button event aus ");
			/*
			 * Hier wird auf den Toggel zwischem relativem und absolutem Support
			 * reagiert.
			 */
			if (e.getActionCommand().equals("absolute")) {
				isZScore = false;
				chooseContigPanel.setZScore(isZScore);
				absoluteSupport.setSelected(true);
				relativeSupport.setSelected(false);

				updateModelAndGui();

			} else if (e.getActionCommand().equals("zScore")) {
				isZScore = true;
				chooseContigPanel.setZScore(isZScore);
				absoluteSupport.setSelected(false);
				relativeSupport.setSelected(true);

				updateModelAndGui();

			} else if (e.getActionCommand().equals(
					"noch kein nachbar ausgewaehlt")) {
			
				ContigRadioButton radioButton = (ContigRadioButton) e
						.getSource();
				AdjacencyEdge selectedEdge = radioButton.getEdge();

				int[] indices = leftAndRightIndex(radioButton, selectedEdge);

				selectEdge(selectedEdge, indices);

			} else if (e.getActionCommand().equals("nachbarAusgewaehlt")) {

				ContigRadioButton radioButton = (ContigRadioButton) e.getSource();
				AdjacencyEdge selectedEdge = radioButton.getEdge();

				int[] indices = leftAndRightIndex(radioButton, selectedEdge);
				AdjacencyEdge oldEdge = radioButton
						.getSelectedNeighbourOfButtonGroup();
				int[] oldIndices = leftAndRightIndex(radioButton, oldEdge);

				deleteEdge(oldEdge, oldIndices);
				if(!oldEdge.equals(selectedEdge)){
					selectEdge(selectedEdge, indices);
				}
			} else if (e.getActionCommand().equals("anderweitigAusgewaehlt")) {
		//		System.out.println("anderweitig ausgewaehlt");
				ContigRadioButton radioButton = (ContigRadioButton) e
						.getSource();
				AdjacencyEdge selectedEdge = radioButton.getEdge();

				int[] indices = leftAndRightIndex(radioButton, selectedEdge);
				AdjacencyEdge otherEdge = radioButton
						.getNeighboursForTheThisNeighbour();
				int[] oldIndices = leftAndRightIndex(radioButton, otherEdge);
				
				AdjacencyEdge neighbourEdge = radioButton.getSelectedNeighbourOfButtonGroup();
				int[] neighbourIndices = null;
				if (neighbourEdge!= null){
					neighbourIndices = leftAndRightIndex(radioButton, neighbourEdge);
				}
				
				
					Object[] options = { "Yes", "No" };

					int n = javax.swing.JOptionPane.showOptionDialog(window,
							"You already selected this neighbour for an another selection.\n"
									+ " Do you want to delete that selection and want to select this selection?",
							"", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);

					if (n == JOptionPane.YES_OPTION) {
						System.out.println("vor dem löschen");
						System.out.println("1 ");
						deleteEdge(otherEdge, oldIndices);

						if(neighbourEdge != null){
							System.out.println("2");
							deleteEdge(neighbourEdge, neighbourIndices);
						}
						System.out.println(" ");
						selectEdge(selectedEdge, indices);
						updateModelAndGui();
					}
				
			}

		}

		private int[] leftAndRightIndex(ContigRadioButton radioButton,
				AdjacencyEdge selectedEdge) {

			/*
			 * at pos 0 left index at pos 1 right index
			 */
			int[] indices = new int[2];

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

			indices[0] = indexLeft;
			indices[1] = indexRight;

			return indices;

		}

		/*
		 * This method delete an edge from the neighbour vectors
		 * and set the edge as deselected
		 */
		private void deleteEdge(AdjacencyEdge oldEdge, int[] indices) {

			Vector<AdjacencyEdge> neighbourl;
			Vector<AdjacencyEdge> neighbourR;

			int leftIndex = indices[0];
			int rightIndex = indices[1];
			
			/*
			 * set edge as not selected
			 */
			oldEdge.deselect();

			neighbourl = ausgewaehlteLinkeKanten.get(leftIndex);
			neighbourl.remove(oldEdge);
			
			neighbourR = ausgewaehlteRechteKanten.get(rightIndex);
			neighbourR.remove(oldEdge);
			
			/*
			 * Sometimes it is possible that I do not calculate the indices right
			 * (when the an ulterior selected edge an neighbour of the right side
			 * but currently the radio button is not right) 
			 * TODO have to change this!
			 * if this is happen the edge can't remove from the vectors
			 * just have to change the side of the contigs.
			 */
			 if (!neighbourl.remove(oldEdge)&& !neighbourR.remove(oldEdge)){
				 neighbourl = ausgewaehlteLinkeKanten.get(rightIndex);
				 neighbourl.remove(oldEdge);
				 neighbourR = ausgewaehlteRechteKanten.get(leftIndex);
				 neighbourR.remove(oldEdge);
			 }

			updateModelAndGui();

		}

		/*
		 * This method add the selected edge in the both neighbour vectors
		 * at the right indices and set the edge as selected
		 */
		private void selectEdge(AdjacencyEdge selectedEdge, int[] indices) {

			Vector<AdjacencyEdge> contigCollectionL;
			Vector<AdjacencyEdge> contigCollectionR;
			
			int leftIndex = indices[0];
			int rightIndex = indices[1];
			
			/*
			 * set Edge as selected and save edge as left and right neighbour
			 */
			selectedEdge.select();
			
			contigCollectionL = ausgewaehlteLinkeKanten.get(leftIndex);
			contigCollectionL.add(selectedEdge);
			
			contigCollectionR = ausgewaehlteRechteKanten.get(rightIndex);		
			contigCollectionR.add(selectedEdge);

			updateModelAndGui();

		}

		private void updateModelAndGui() {
			JPanel rightContainer = chooseContigPanel.getRightContainer();
			JPanel leftContainer = chooseContigPanel.getLeftContainer();
			if (rightContainer.getComponentCount() != 0
					|| leftContainer.getComponentCount() != 0) {
				model.sendCurrentContig();
				model.sendLeftNeighbours();
				model.sendRightNeighbours();
				repaint();
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
			chooseContigPanel.setFlag(false);

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
		//	System.out.println("neue anzahl an nachbarn " + neighboursNumber);

			if (neighboursNumber <= 10 && neighboursNumber > 0) {

				if (neighboursNumber < numberOfNeighbours) {
					int breite = (int) chooseContigPanel.getSize().getWidth();
					chooseContigPanel.setPreferredSize(new Dimension(breite,
							400));
				}

				if (neighboursNumber > 8) {
					int breite = (int) chooseContigPanel.getSize().getWidth();
					chooseContigPanel.setPreferredSize(new Dimension(breite,
							600));
				}

				numberOfNeighbours = neighboursNumber;
				inputOptionForNumberOfNeighbours.setValue(new Integer(
						numberOfNeighbours));

				chooseContigPanel.setNumberOfNeighbours(numberOfNeighbours);

				JPanel rightContainer = chooseContigPanel.getRightContainer();
				JPanel leftContainer = chooseContigPanel.getLeftContainer();
				if (rightContainer.getComponentCount() != 0
						|| leftContainer.getComponentCount() != 0) {
					model.sendLeftNeighbours();
					model.sendRightNeighbours();
				}
				chooseContigPanel.repaint();

			} else if (neighboursNumber == 0) {
				javax.swing.JOptionPane.showMessageDialog(window, "Sorry.\n"
						+ "You can't choose " + neighboursNumber
						+ " Neighbours.\n" + "Please choose between 1 and 10.");

				numberOfNeighbours = 5;
				inputOptionForNumberOfNeighbours.setValue(new Integer(
						numberOfNeighbours));

				chooseContigPanel.setNumberOfNeighbours(numberOfNeighbours);

				JPanel rightContainer = chooseContigPanel.getRightContainer();
				JPanel leftContainer = chooseContigPanel.getLeftContainer();

				if (rightContainer.getComponentCount() != 0
						|| leftContainer.getComponentCount() != 0) {
					model.sendLeftNeighbours();
					model.sendRightNeighbours();
					chooseContigPanel.repaint();
				}
			} else {
				javax.swing.JOptionPane.showMessageDialog(window, "Sorry.\n"
						+ "You can't choose " + neighboursNumber
						+ " Neighbours.\n" + "Please choose between 1 and 10.");

				numberOfNeighbours = 10;
				inputOptionForNumberOfNeighbours.setValue(new Integer(
						numberOfNeighbours));
				int breite = (int) chooseContigPanel.getSize().getWidth();

				chooseContigPanel.setPreferredSize(new Dimension(breite, 600));

				chooseContigPanel.setNumberOfNeighbours(numberOfNeighbours);

				JPanel rightContainer = chooseContigPanel.getRightContainer();
				JPanel leftContainer = chooseContigPanel.getLeftContainer();

				if (rightContainer.getComponentCount() != 0
						|| leftContainer.getComponentCount() != 0) {
					model.sendLeftNeighbours();
					model.sendRightNeighbours();
					chooseContigPanel.repaint();
				}
			}

		}

	}

}