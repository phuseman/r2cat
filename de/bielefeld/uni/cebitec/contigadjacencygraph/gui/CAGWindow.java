package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
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
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//import sun.org.mozilla.javascript.IdScriptableObject;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class CAGWindow extends JFrame implements CagEventListener {

	private CAGWindow window;
	private JScrollPane listScroller;
	private JList list;
	private JPanel listContainer;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;
	private JPanel chooseContigPanel;
	private BoxLayout layout;
	private DNASequence[] nodes;
	private CagCreator model;

	private JPanel leftContainer;
	private JPanel centerContainer;
	private JPanel rightContainer;

	private double[] leftSupport;
	private double[] rightSupport;

	private GlassPaneWithLines glassPanel;

	private JPanel centralContig;

	private boolean rightContainerFull = false;
	private boolean leftContainerFull = false;

	private ContigButtonGroup leftGroup = new ContigButtonGroup();
	private ContigButtonGroup rightGroup = new ContigButtonGroup();
	private JPanel leftRadioButtonContainer;
	private JPanel rightRadioButtonContainer;

	private Vector<AdjacencyEdge> selectedRadioButtons;
	private int z = 0;
	private String centralContigName;

	private int numberOfNeighbours = 5;
	private boolean oneOfLeftNeigboursSelected;
	private boolean oneOfRightNeigboursSelected;

	private LayoutGraph layoutGraph;
	private int centralContigIndex;
	private String[] dataForList;

	public CAGWindow(CagCreator myModel) {
		window = this;
		this.model = myModel;
		layoutGraph = model.getGraph();
		nodes = model.getListData();
		myModel.addEventListener(this);
		setTitle("View of a contig adjacency graph");

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
		chooseContigPanel.setPreferredSize(new Dimension(1000, 400));

		chooseContigPanel.setBackground(Color.WHITE);
		add(chooseContigPanel, BorderLayout.CENTER);

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
		leftContainer.setBackground(Color.WHITE);
		leftContainer.setPreferredSize(new Dimension(310, 1000));
		leftContainer.setMinimumSize(new Dimension(310, 1000));
		leftContainer.setMaximumSize(new Dimension(310, 1000));

		leftRadioButtonContainer.setLayout(leftRadioBoxLayout);
		leftRadioButtonContainer.setBackground(Color.WHITE);
		leftRadioButtonContainer.setPreferredSize(new Dimension(20, 1000));
		leftRadioButtonContainer.setMinimumSize(new Dimension(20, 1000));
		leftRadioButtonContainer.setMaximumSize(new Dimension(20, 1000));

		/*
		 * Container for central contig
		 */
		centerContainer.setLayout(centerBoxLayout);
		centerContainer.setBackground(Color.WHITE);
		centerContainer.setPreferredSize(new Dimension(310, 1000));
		centerContainer.setMinimumSize(new Dimension(310, 1000));
		centerContainer.setMaximumSize(new Dimension(310, 1000));

		/*
		 * Container for all right neigbors
		 */
		rightContainer.setLayout(rightBoxLayout);
		rightContainer.setBackground(Color.WHITE);
		rightContainer.setPreferredSize(new Dimension(310, 1000));
		rightContainer.setMinimumSize(new Dimension(310, 1000));
		rightContainer.setMaximumSize(new Dimension(310, 1000));

		rightRadioButtonContainer.setLayout(rightRadioBoxLayout);
		rightRadioButtonContainer.setBackground(Color.WHITE);
		rightRadioButtonContainer.setPreferredSize(new Dimension(20, 1000));
		rightRadioButtonContainer.setMinimumSize(new Dimension(20, 1000));
		rightRadioButtonContainer.setMaximumSize(new Dimension(20, 1000));

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

		/*
		 * Dieses Panel enthaelt alle Contigs dieses Genoms als Liste
		 */
		listContainer = new JPanel();// new GridLayout(1,1)
		listContainer.setMinimumSize(new Dimension(100, 400));
		listContainer.setMaximumSize(new Dimension(100, (int) Toolkit
				.getDefaultToolkit().getScreenSize().getHeight()));
		listContainer.setPreferredSize(new Dimension(100, 400));

		/*
		 * Diese Liste sollte alle Contigs beinhalten; muessen keinen Strings
		 * sein JList nimmt einen beleibigen Objekttyp entgegen; werden aber in
		 * der Liste als strings repraesentiert Mittels einer
		 */

		/*
		 * TODO länge der Liste sollte sich an die größe des Fensters anpassen
		 * Und sie sollte ein wenig breiter sein.
		 */
		dataForList = new String[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			String id = nodes[i].getId();
			dataForList[i] = id;
		}

		list = new JList(dataForList);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(20);
		list.addListSelectionListener(new ContigChangedListener());

		listScroller = new JScrollPane(list);
		listScroller
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScroller
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listScroller.setAlignmentY(RIGHT_ALIGNMENT);

		listContainer
				.setBorder(BorderFactory.createTitledBorder("Contig List"));
		listContainer.add(listScroller, BorderLayout.CENTER);
		add(listContainer, BorderLayout.EAST);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// setSize(Toolkit.getDefaultToolkit().getScreenSize());
		// hier wird das Fenster auf die Größe des Bildschirmes angepasst.

		glassPanel = new GlassPaneWithLines();
		setGlassPane(glassPanel);
		setSize(1000, 500);
		setVisible(true);

		pack();
	}

	/*
	 * create lines between the central contig and its neighbours
	 */
	private void setLineInPanel() {

		if (rightContainerFull && leftContainerFull) {

			glassPanel.setOpaque(false);
			glassPanel.setPreferredSize(chooseContigPanel.getSize());
			glassPanel.setLine(leftContainer, rightContainer, centralContig,
					leftSupport, rightSupport);
			getGlassPane().setVisible(true);

			rightContainerFull = false;
			leftContainerFull = false;
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

			AdjacencyEdge edge = event.getEdge();
			LayoutGraph graph = this.layoutGraph;
			centralContigIndex = event.getIndex();
			centralContig = new ContigAppearance(graph, edge,
					centralContigIndex);

			if (centerContainer.getComponentCount() > 0) {
				centerContainer.removeAll();
			}
			centerContainer.add(centralContig);
			centerContainer.updateUI();
		}
		/*
		 * Auch die Nachbarn änders sich, wenn das Contig sich ändert.
		 */
		if (event.getEvent_type().equals(EventType.EVENT_SEND_LEFT_NEIGHBOURS)) {

			leftContainerFull = false;

			Vector<AdjacencyEdge> neighbourEdges = event.getEdges();

			ContigAppearance contigPanel = null;
			oneOfLeftNeigboursSelected = false;
			ContigRadioButton radioButton;
			leftSupport = new double[numberOfNeighbours];
			int t = 0;

			if (leftContainer.getComponentCount() > 0
					|| leftRadioButtonContainer.getComponentCount() > 0) {
				leftContainer.removeAll();
				leftRadioButtonContainer.removeAll();
			}
			int terminator = 1;
			if (numberOfNeighbours < neighbourEdges.size()) {
				terminator = numberOfNeighbours;
			} else if (numberOfNeighbours > neighbourEdges.size()) {
				terminator = neighbourEdges.size();
			}

			for (Iterator adjacencyEdge = neighbourEdges.iterator(); adjacencyEdge
					.hasNext();) {

				while (t < terminator) {
					AdjacencyEdge edge = (AdjacencyEdge) adjacencyEdge.next();
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
					 * System.out.println("links " + dnaSequence.getId() + ":  "
					 * + dnaSequence.getTotalSupport());
					 */
					leftSupport[t] = dnaSequence
					// .getSupportComparativeToCentralContig();
							.getTotalSupport();

					contigPanel = new ContigAppearance(layoutGraph, edge,
							indexOfContig);
					/*
					 * Help that the user is only able to select one neighbour
					 * for each side.
					 */
					contigPanel.setAlignmentX(RIGHT_ALIGNMENT);
					contigPanel.addMouseListener(new ContigMouseListener());
					contigPanel.setVisible(true);

					radioButton = new ContigRadioButton(edge, indexOfContig);
					radioButton.getModel().setSelected(false);
					radioButton.setBackground(Color.WHITE);
					radioButton
							.addActionListener(new RadioButtonActionListener());

					if (dnaSequence.isContigIsSelected()) {
						radioButton.getModel().setSelected(true);
						leftGroup.setAllRadioButtonAsSelected();
						oneOfLeftNeigboursSelected = true;
					}
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
			leftContainerFull = true;
			setLineInPanel();
		}

		if (event.getEvent_type().equals(EventType.EVENT_SEND_RIGHT_NEIGHBOURS)) {

			int terminator = 1;

			rightContainerFull = false;
			Vector<AdjacencyEdge> neighbourEdges = event.getEdges();
			ContigAppearance contigPanel = null;
			oneOfRightNeigboursSelected = false;
			ContigRadioButton radioButton;
			rightSupport = new double[numberOfNeighbours];

			int s = 0;
			if (rightContainer.getComponentCount() > 0
					|| rightRadioButtonContainer.getComponentCount() > 0) {
				rightContainer.removeAll();
				rightRadioButtonContainer.removeAll();
			}
			if (numberOfNeighbours < neighbourEdges.size()) {
				terminator = numberOfNeighbours;
			} else if (numberOfNeighbours > neighbourEdges.size()) {
				terminator = neighbourEdges.size();
			}

			for (Iterator adjacencyEdge = neighbourEdges.iterator(); adjacencyEdge
					.hasNext();) {

				while (s < terminator) {
					AdjacencyEdge edge = (AdjacencyEdge) adjacencyEdge.next();

					DNASequence dnaSequence;
					int indexOfContig;
					if (edge.geti() == centralContigIndex) {
						dnaSequence = edge.getContigj();
						indexOfContig = edge.getj();
					} else {
						dnaSequence = edge.getContigi();
						indexOfContig = edge.geti();
					}

					rightSupport[s] = dnaSequence
					// .getSupportComparativeToCentralContig();
							.getTotalSupport();

					contigPanel = new ContigAppearance(layoutGraph, edge,
							indexOfContig);
					contigPanel.setAlignmentX(LEFT_ALIGNMENT);
					contigPanel.addMouseListener(new ContigMouseListener());
					contigPanel.setVisible(true);

					radioButton = new ContigRadioButton(edge, indexOfContig);
					radioButton.getModel().setActionCommand("notSelected");
					radioButton.setBackground(Color.WHITE);
					radioButton
							.addActionListener(new RadioButtonActionListener());
					rightGroup.add(radioButton);
					/*
					 * Help that the user is only able to select one neighbour
					 * for each side.
					 */
					if (dnaSequence.isContigIsSelected()) {
						radioButton.getModel().setActionCommand("isSelected");
						rightGroup.setAllRadioButtonAsSelected();
						oneOfRightNeigboursSelected = true;
					}

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
			rightContainerFull = true;
			setLineInPanel();
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
		}
	}

	/*
	 * Listener für die Elemente der Contig Liste
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
				getGlassPane().setVisible(false);
				glassPanel.setFlag(false);

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

		private boolean neighbourAlreadySelected = false;

		@Override
		public void actionPerformed(ActionEvent e) {

			Vector<AdjacencyEdge> copyOfSelectedContigs = (Vector<AdjacencyEdge>) selectedRadioButtons
					.clone();

			ContigRadioButton radioButton = (ContigRadioButton) e.getSource();
			AdjacencyEdge edge = radioButton.getEdge();

			boolean isANeighourSelected = radioButton
					.isOneNeighbourOfThisSideAlreadySelected();
			/*
			 * Vllt ist die Seite noch relevant dann in der radionButtonClass
			 * hinzufügen
			 */

			for (AdjacencyEdge test : copyOfSelectedContigs) {
				if (!isANeighourSelected
						&& radioButton.getModel().getActionCommand() == "notSelected") {

					if (!edge.equals(test)) {
						selectedRadioButtons.add(edge);
						/*
						 * TODO Bis jetzt wird nur dieser eine radio Button
						 * gesetzt. Möchte aber das alle button der group als
						 * ausgewählt gesetzt werden. Und dadurch kein weiterer
						 * Ausgewählt werden kann.
						 */
						radioButton
								.setOneNeighbourOfThisSideAlreadySelected(true);
						model.addSelectedContig(edge);
						break;
					}
				} else if (edge.equals(test)) {
					neighbourAlreadySelected = true;
					
					Object[] options = {"Yes",  "No"};

					int n = javax.swing.JOptionPane.showOptionDialog(window,
							"You already selected this neighbour.\n" +
							" Do you want to delete this selection?",
							"", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					
					if(n == JOptionPane.YES_OPTION){
						model.removeSelectedEdge(edge);
						copyOfSelectedContigs.remove(edge);
						radioButton.setOneNeighbourOfThisSideAlreadySelected(false);
						neighbourAlreadySelected= false;
						
						window.repaint();
					}
					break;

				} else {

					javax.swing.JOptionPane
							.showMessageDialog(
									window,
									"Sorry.\n"
											+ "You already selected a neighbour for this contig.\n");
					break;
				}
			}
			if (selectedRadioButtons.size() == 0) {
				selectedRadioButtons.add(edge);
				/*
				 * TODO Bis jetzt wird nur dieser eine radio Button gesetzt.
				 * Möchte aber das alle button der group als ausgewählt gesetzt
				 * werden. Und dadurch kein weiterer Ausgewählt werden kann.
				 */
				radioButton.setOneNeighbourOfThisSideAlreadySelected(true);
				model.addSelectedContig(edge);
			}
		}
	}

	public class ContigMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			ContigAppearance contigPanel = (ContigAppearance) e.getSource();

			int index = contigPanel.getI();
			boolean isReverse = contigPanel.isReverse();

			model.changeContigs(index, isReverse);
			getGlassPane().setVisible(false);
			glassPanel.setFlag(false);

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

}