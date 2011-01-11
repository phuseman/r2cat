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

import sun.org.mozilla.javascript.IdScriptableObject;

import de.bielefeld.uni.cebitec.qgram.DNASequence;

/*
 * Ist das Abbild vom Model
 * 
 * TODO heute die contigs im Genompanel sollten die Größe wie im chooseContigPanel haben
 * und auch die Beschriftung der Contigs sollte sich nicht ändern
 */
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
	private String[] listData;
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

	private ButtonGroup leftGroup = new ButtonGroup();
	private ButtonGroup rightGroup = new ButtonGroup();
	private JPanel leftRadioButtonContainer;
	private JPanel rightRadioButtonContainer;

	private Vector<String> selectedRadioButtons;
	private int z = 0;
	private String centralContigName;

	private int numberOfNeighbours = 5;
	private boolean oneOfLeftNeigboursSelected;
	private boolean oneOfRightNeigboursSelected;

	public CAGWindow(CagCreator myModel) {
		window = this;
		this.model = myModel;
		listData = model.getListData();
		myModel.addEventListener(this);
		setTitle("View of contig adjacency graph");

		selectedRadioButtons = new Vector<String>();
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
		 * Parent Panel for all other the container of all neighbours
		 * and central contig.
		 * Used this because so I'm able to change the content of each 
		 * container independently.
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
		listContainer.setMaximumSize(new Dimension(100,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
		listContainer.setPreferredSize(new Dimension(100,400));

		/*
		 * Diese Liste sollte alle Contigs beinhalten; muessen keinen Strings
		 * sein JList nimmt einen beleibigen Objekttyp entgegen; werden aber in
		 * der Liste als strings repraesentiert Mittels einer
		 */

		/*
		 * TODO länge der Liste sollte sich an die größe des Fensters anpassen
		 * Und sie sollte ein wenig breiter sein.
		 */
		list = new JList(listData);
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
			System.out.println(rightSupport[0]);
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

			DNASequence contigNode = event.getContigNode();
			centralContigName = contigNode.getId();
			centralContig = new ContigAppearance(contigNode, centralContigName);
			centralContig.addMouseListener(new ContigMouseListener());

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
			
			Vector<DNASequence> leftNeighbours = null;
			ContigAppearance contigPanel = null;
			oneOfLeftNeigboursSelected = false;
			JRadioButton radioButton;
			leftSupport = new double[numberOfNeighbours];
			leftNeighbours = event.getContigData();
			int t = 0;

			if (leftContainer.getComponentCount() > 0
					|| leftRadioButtonContainer.getComponentCount() > 0) {
				leftContainer.removeAll();
				leftRadioButtonContainer.removeAll();
			}
			int terminator = 1;
			if (numberOfNeighbours < leftNeighbours.size()){
				terminator = numberOfNeighbours;
			}else if(numberOfNeighbours > leftNeighbours.size()){
				terminator = leftNeighbours.size();
			}
			for (Iterator<DNASequence> neighbour = leftNeighbours.iterator(); neighbour
					.hasNext();) {
				while (t < terminator) {
					DNASequence dnaSequence = (DNASequence) neighbour.next();
					
					leftSupport[t] = dnaSequence
							//.getSupportComparativeToCentralContig();
							.getTotalSupport();
					contigPanel = new ContigAppearance(dnaSequence, centralContigName);
					/*
					 * Help that the user is only able to select one 
					 * neighbour for each side.
					 */
					if(dnaSequence.isContigIsSelected()){
						oneOfLeftNeigboursSelected  = true;
					}
					contigPanel.setAlignmentX(RIGHT_ALIGNMENT);

					contigPanel.addMouseListener(new ContigMouseListener());
					contigPanel.setVisible(true);

					radioButton = new JRadioButton();
					radioButton.setBackground(Color.WHITE);
					// ist relevat um nur einen nachbarn des centralen Contigs
					// auswählbar zu machen.
					radioButton.setActionCommand(centralContigName + ":"
							+ dnaSequence.getId() + ":left");
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

					if(t == terminator){
						break;
					}
				}
			}
			leftContainerFull = true;
			setLineInPanel();					
		}

		if (event.getEvent_type().equals(EventType.EVENT_SEND_RIGHT_NEIGHBOURS)) {
			
			int terminator = 1;

			rightContainerFull = false;
			Vector<DNASequence> rightNeighbours = null;
			ContigAppearance contigPanel = null;
			oneOfRightNeigboursSelected = false;
			JRadioButton radioButton;
			rightSupport = new double[numberOfNeighbours];
			rightNeighbours = event.getContigData();
			int s = 0;
			if (rightContainer.getComponentCount() > 0
					|| rightRadioButtonContainer.getComponentCount() > 0) {
				rightContainer.removeAll();
				rightRadioButtonContainer.removeAll();
			}
			if (numberOfNeighbours < rightNeighbours.size()){
				terminator = numberOfNeighbours;
			}else if(numberOfNeighbours > rightNeighbours.size()){
				terminator = rightNeighbours.size();
			}

			for (Iterator<DNASequence> neighbour = rightNeighbours.iterator(); neighbour
					.hasNext();) {
				while (s < terminator) {
					System.out.println(" Terminator  "+terminator);
					DNASequence rightNeighbour = (DNASequence) neighbour.next();

					rightSupport[s] = rightNeighbour
//							.getSupportComparativeToCentralContig();
					.getTotalSupport();
					System.out.println(rightSupport[s]);
					contigPanel = new ContigAppearance(rightNeighbour, centralContigName);
					/*
					 * Help that the user is only able to select one 
					 * neighbour for each side.
					 */
					if(rightNeighbour.isContigIsSelected()){
						oneOfRightNeigboursSelected = true;
					}
					contigPanel.setAlignmentX(LEFT_ALIGNMENT);
					contigPanel.addMouseListener(new ContigMouseListener());
					contigPanel.setVisible(true);

					radioButton = new JRadioButton();
					radioButton.setBackground(Color.WHITE);
					radioButton.setActionCommand(centralContigName + ":"
							+ rightNeighbour.getId() + ":right");
					radioButton
							.addActionListener(new RadioButtonActionListener());
					
					rightContainer.add(contigPanel);
					
					rightGroup.add(radioButton);
					rightRadioButtonContainer.add(radioButton);
					
					if (s < (numberOfNeighbours - 1)) {
						rightContainer.add(Box.createVerticalGlue());
						rightRadioButtonContainer.add(Box.createVerticalGlue());
					}
					rightContainer.updateUI();
					rightRadioButtonContainer.updateUI();
					s++;
					if(s == terminator){
						System.out.println("Breche for ab");
						break;
					}
				}
			}
			System.out.println("Möchte nun linien setzten");
			rightContainerFull = true;
			setLineInPanel();
		}

	}

	/*
	 * Listener für die Elemente der Contig Liste
	 */
	public class ContigChangedListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			/*
			 * Waehle ich aus der Liste ein Contig aus, wird dieses Contig immer
			 * nicht reverse | > angezeigt Da fuer den Benutzter dies iritierend
			 * sein koennte. Dies koennte man auch in der Evaluation abfragen.
			 */
			if (e.getValueIsAdjusting() == false) {
				
				String selection = (String) list.getSelectedValue();
				model.changeContigs(selection, "false");
				getGlassPane().setVisible(false);
				glassPanel.setFlag(false);
				
				SwingWorkerClass threadForLeftNeighbours = new SwingWorkerClass();
				threadForLeftNeighbours.execute();

				ThreadClassForRightNeighours threadForRightNeighbours = new ThreadClassForRightNeighours();
				threadForRightNeighbours.execute();
			}
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
	 * Inner class for radion Buttons if the use click an radio Button the
	 * corresponding contig is marked as selected
	 */
	public class RadioButtonActionListener implements ActionListener {

		private boolean neighbourAlreadySelected;
		private String actionCmd;
		private String test[];
		private String centralContigId;
		private String idOfSelectedContig;
		private String side;

		private String oldCmd;
		private String old[];
		private String oldcentralContigId;
		private String oldId;
		private String oldSide;
		private boolean contigPossibleNeighbour;

		@Override
		public void actionPerformed(ActionEvent e) {

			Vector<String> copyOfSelectedContigs = (Vector<String>) selectedRadioButtons
					.clone();

			neighbourAlreadySelected = false;

			actionCmd = e.getActionCommand();
			test = actionCmd.split(":");
			centralContigId = test[0];
			idOfSelectedContig = test[1];
			side = test[2];
			
			boolean use;
			if(side.equals("left")){
				use = oneOfLeftNeigboursSelected;
			}else{
				use = oneOfRightNeigboursSelected;
			}
			System.out.println(use);
			if (z == 0) {
				z++;
				selectedRadioButtons.add(actionCmd);
				model.addSelectedContig(idOfSelectedContig, side);
			} else {
				/*
				 * Für jedes bisher gespeicherte Contig Paar
				 */
				for (Iterator<String> selectedContig = copyOfSelectedContigs
						.iterator(); selectedContig.hasNext();) {

					oldCmd = (String) selectedContig.next();
					old = oldCmd.split(":");
					oldcentralContigId = old[0];
					oldId = old[1];
					oldSide = old[2];
					/*
					 * Test, ob diese Contig Id schon als Nachbar ausgewählt
					 * worden ist
					 */
					if (oldId.equals(idOfSelectedContig)&& !use) {
						javax.swing.JOptionPane.showMessageDialog(window,
								"Sorry.\n"+"You already selected this neighbour\n");
						neighbourAlreadySelected = true;
						break;
					}
					/*
					 * Test, ob das centrale Contig schon einmal ausgewählt
					 * worden ist.
					 */
					if (oldcentralContigId.equals(centralContigId) ) {

						// Wenn dem zentrale Contig schon auf beiden Seiten
						// einen Nachbar zugewiesen wurde ist es nicht
						// möglich einen weiteren hinzuzufügen.

						if (oldSide.equals(side)) {
							javax.swing.JOptionPane
									.showMessageDialog(window,
											"Sorry.\n"+
											"You already selected a neighbour for this side of central contig\n"+
											"First you have to delete the other Contig");
							break;
						} else {
							contigPossibleNeighbour = true;
						}
					}else{
						contigPossibleNeighbour = true;
					}

				}
				if(!use){
				if (!neighbourAlreadySelected) {
					/*
					 * Entweder ist hier der ausgewählte Nachbar der zweite
					 * Nachbar eines zentralen Contigs
					 */
					if (contigPossibleNeighbour) {
						selectedRadioButtons.add(actionCmd);
						model.addSelectedContig(idOfSelectedContig, side);
					}
					/*
					 * oder die Nachbarn von diesem centralem Contig wurden noch
					 * nicht ausgewählt
					 */
				} else {
					
					selectedRadioButtons.add(actionCmd);
					model.addSelectedContig(idOfSelectedContig, side);
				}
				}else{
					javax.swing.JOptionPane
					.showMessageDialog(window,
							"Sorry.\n"+
							"You already selected a neighbour for this side of central contig\n"+
							"First you have to delete the other Contig");
				}
			}
		}
	}

	public class ContigMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			JPanel jp = (JPanel) e.getSource();
			int subcomponents = jp.getComponentCount();
			JLabel child = null;
			String name = null;
			String isReverse = jp.getName();

			for (int i = 0; i < subcomponents; i++) {
				Component c = jp.getComponent(i);
				if (c.getName().length() > 0) {
					child = (JLabel) c;
					name = c.getName();
				}
			}
			if (child != null && name != null && isReverse != null) {
				
				model.changeContigs(name, isReverse);
				getGlassPane().setVisible(false);
				glassPanel.setFlag(false);
				
				SwingWorkerClass threadForLeftNeighbours = new SwingWorkerClass();
				threadForLeftNeighbours.execute();

				ThreadClassForRightNeighours threadForRightNeighbours = new ThreadClassForRightNeighours();
				threadForRightNeighbours.execute();
			}
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