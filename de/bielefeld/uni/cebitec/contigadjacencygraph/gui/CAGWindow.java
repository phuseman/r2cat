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

import javax.swing.BorderFactory;
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
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.bielefeld.uni.cebitec.qgram.DNASequence;

/*
 * Ist das Abbild vom Model
 * 
 * TODO heute die contigs im Genompanel sollten die Größe wie im chooseContigPanel haben
 * und auch die Beschriftung der Contigs sollte sich nicht ändern
 */
public class CAGWindow extends JFrame implements CagEventListener {// ActionListener{
	// CagEventListener{

	private CAGWindow window;
	private JScrollPane listScroller;
	private JScrollPane genomeScroller;
	private JList list;
	private JPanel listContainer;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;
	// private ChooseContigPanel chooseContigPanel;
	private JPanel chooseContigPanel;
	private GroupLayout layout;
	private JPanel genomePanel;
	private String[] listData;
	private CagCreator model;
	private Controller control;

	private ContigPanel[] leftContigs;
	private ContigPanel[] rightContigs;
	private JLabel[] leftContigLabels;
	private JLabel[] rightContigLabels;
	
	private ContigPanel selectedContig;
	private ArrayList<ContigPanel> selectedContigs;

	private ContigPanel leftContig1;
	private JLabel contigLabel1;
	private ContigPanel leftContig2;
	private JLabel contigLabel2;
	private ContigPanel leftContig3;
	private JLabel contigLabel3;
	private ContigPanel leftContig4;
	private JLabel contigLabel4;
	private ContigPanel leftContig5;
	private JLabel contigLabel5;

	private ContigPanel centralContig;
	private JLabel centralContigLabel;

	private ContigPanel rightContig1;
	private JLabel rightcontigLabel1;
	private ContigPanel rightContig2;
	private JLabel rightcontigLabel2;
	private ContigPanel rightContig3;
	private JLabel rightcontigLabel3;
	private ContigPanel rightContig4;
	private JLabel rightcontigLabel4;
	private ContigPanel rightContig5;
	private JLabel rightcontigLabel5;


	public CAGWindow(CagCreator myModel, Controller controller) {
		window = this;
		this.model = myModel;
		listData = model.getListData();
		myModel.addEventListener(this);
		this.control = controller;
		setTitle("View of contig adjacency graph");

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
		layout = new GroupLayout(chooseContigPanel);

		chooseContigPanel.setLayout(layout);
		/*
		 * TODO diese Panels und Label in einer Methode generieren lassen!
		 */
		selectedContigs = new ArrayList<ContigPanel>();
		leftContigs = new ContigPanel[5];

		leftContig1 = new ContigPanel();
		leftContigs[0] = leftContig1;
		leftContig2 = new ContigPanel();
		leftContigs[1] = leftContig2;
		leftContig3 = new ContigPanel();
		leftContigs[2] = leftContig3;
		leftContig4 = new ContigPanel();
		leftContigs[3] = leftContig4;
		leftContig5 = new ContigPanel();
		leftContigs[4] = leftContig5;
		
		centralContig = new ContigPanel();

		rightContigs = new ContigPanel[5];

		rightContig1 = new ContigPanel();
		rightContigs[0] = rightContig1;
		rightContig2 = new ContigPanel();
		rightContigs[1] = rightContig2;
		rightContig3 = new ContigPanel();
		rightContigs[2] = rightContig3;
		rightContig4 = new ContigPanel();
		rightContigs[3] = rightContig4;
		rightContig5 = new ContigPanel();
		rightContigs[4] = rightContig5;

		// leftContig1.setLayout(new BorderLayout().NORTH);
		// GroupLayout lableLayout = new GroupLayout(leftContig1);
		leftContigLabels = new JLabel[5];
		
		contigLabel1 = new JLabel();
		leftContig1.add(contigLabel1);
		leftContig1.addMouseListener(new ContigMouseListener());
		leftContigLabels[0]= contigLabel1;

		contigLabel2 = new JLabel();
		leftContig2.add(contigLabel2);
		leftContig2.addMouseListener(new ContigMouseListener());
		leftContigLabels[1]= contigLabel2;

		contigLabel3 = new JLabel();
		leftContig3.add(contigLabel3);
		leftContig3.addMouseListener(new ContigMouseListener());
		leftContigLabels[2]= contigLabel3;

		contigLabel4 = new JLabel();
		leftContig4.add(contigLabel4);
		leftContig4.addMouseListener(new ContigMouseListener());
		leftContigLabels[3]= contigLabel4;

		contigLabel5 = new JLabel();
		leftContig5.add(contigLabel5);
		leftContig5.addMouseListener(new ContigMouseListener());
		leftContigLabels[4]= contigLabel5;

		centralContigLabel = new JLabel();
		centralContig.add(centralContigLabel);
		centralContig.addMouseListener(new ContigMouseListener());
		
		rightContigLabels= new JLabel[5];

		rightcontigLabel1 = new JLabel();
		rightContig1.add(rightcontigLabel1);
		rightContig1.addMouseListener(new ContigMouseListener());
		rightContigLabels[0]=rightcontigLabel1;

		rightcontigLabel2 = new JLabel();
		rightContig2.add(rightcontigLabel2);
		rightContig2.addMouseListener(new ContigMouseListener());
		rightContigLabels[1]=rightcontigLabel2;

		rightcontigLabel3 = new JLabel();
		rightContig3.add(rightcontigLabel3);
		rightContig3.addMouseListener(new ContigMouseListener());
		rightContigLabels[2]=rightcontigLabel3;

		rightcontigLabel4 = new JLabel();
		rightContig4.add(rightcontigLabel4);
		rightContig4.addMouseListener(new ContigMouseListener());
		rightContigLabels[3]=rightcontigLabel4;

		rightcontigLabel5 = new JLabel();
		rightContig5.add(rightcontigLabel5);
		rightContig5.addMouseListener(new ContigMouseListener());
		rightContigLabels[4]=rightcontigLabel5;

		leftContig1.setBackground(Color.WHITE);
		leftContig2.setBackground(Color.WHITE);
		leftContig3.setBackground(Color.WHITE);
		leftContig4.setBackground(Color.WHITE);
		leftContig5.setBackground(Color.WHITE);
		centralContig.setBackground(Color.WHITE);
		centralContig.setPreferredSize(new Dimension(100, 50));
		centralContig.setMaximumSize(new Dimension(100, 50));
		centralContig.setMinimumSize(new Dimension(100, 50));
		rightContig1.setBackground(Color.WHITE);
		rightContig2.setBackground(Color.WHITE);
		rightContig3.setBackground(Color.WHITE);
		rightContig4.setBackground(Color.WHITE);
		rightContig5.setBackground(Color.WHITE);
		/*
		 * automatic gaps that correspond to preferred distances between
		 * neighboring components (or between a component and container border)
		 */
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(leftContig1).addComponent(leftContig2)
						.addComponent(leftContig3).addComponent(leftContig4)
						.addComponent(leftContig5)).addComponent(centralContig)
				.addGroup(
						layout
								.createParallelGroup(
										GroupLayout.Alignment.CENTER)
								.addComponent(rightContig1).addComponent(
										rightContig2)
								.addComponent(rightContig3).addComponent(
										rightContig4)
								.addComponent(rightContig5)));

		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(leftContig1).addComponent(rightContig1))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(
								leftContig2).addComponent(rightContig2))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(
								leftContig3).addComponent(centralContig)
								.addComponent(rightContig3)).addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(
								leftContig4).addComponent(rightContig4))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(
								leftContig5).addComponent(rightContig5)));

		chooseContigPanel.setBackground(Color.WHITE);
		add(chooseContigPanel, BorderLayout.CENTER);
		/*
		 * Dieses Panel enthaelt alle Contigs dieses Genoms als Liste
		 */
		listContainer = new JPanel();// new GridLayout(1,1)

		/*
		 * Diese Liste sollte alle Contigs beinhalten; muessen keinen Strings
		 * sein JList nimmt einen beleibigen Objekttyp entgegen; werden aber in
		 * der Liste als strings repraesentiert Mittels einer
		 */
		/*
		 * Liste mit den Namen der Contigs
		 * schleife hat Contig vor der id des Contig gesetzt war nur ästhetischer
		 * später werden sowieso nur die Namen aus dem Fasta file verwendet
		 */
//		for (int i = 0; i < listData.length; i++) {
//			String contigname = listData[i];
//			listData[i] = "Contig " + contigname;
//		}

		/*
		 * TODO länge der Liste sollte sich an die größe des Fensters anpassen
		 * Und sie sollte ein wenig breiter sein.
		 */
		list = new JList(listData);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//list.getPreferredScrollableViewportSize(); 
		list.setVisibleRowCount(35);
		list.addListSelectionListener(new ContigChangedListener());

		listScroller = new JScrollPane(list);
		listScroller
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScroller
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listScroller.setAlignmentY(RIGHT_ALIGNMENT);

		listContainer
				.setBorder(BorderFactory.createTitledBorder("Contig List"));
		listContainer.add(listScroller,BorderLayout.CENTER);
		add(listContainer, BorderLayout.EAST);

		// Hier sollen spaeter die ausgewaehlten Contigs angezeigt werden
		genomePanel = new JPanel();
		genomeScroller = new JScrollPane();
		/*
		 * TODO die Scrollbar ist noch nicht da
		 */
		genomeScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		genomeScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		genomeScroller.setAlignmentX(TOP_ALIGNMENT);
		genomePanel.setBorder(BorderFactory.createTitledBorder("Sorted Genome"));
		genomePanel.add(genomeScroller);
		genomePanel.setBackground(Color.WHITE);
		add(genomePanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
//		 setSize(Toolkit.getDefaultToolkit().getScreenSize());
		// hier wird das Fenster auf die Größe des Bildschirmes angepasst.
		setSize(900, 900);
		setVisible(true);

		pack();
	}

	private void setContigAppearance(String contigId, String isReverseToString,
			long size, ContigBorder border, boolean isLeftNeighbour,
			int contigIndex) {
		
		ContigPanel contig;
		JLabel contigLabel;

		if (isLeftNeighbour == true) {
			contig = leftContigs[contigIndex];
			contigLabel = leftContigLabels[contigIndex];
		} else {
			contig = rightContigs[contigIndex];
			contigLabel = rightContigLabels[contigIndex];
		}
		
		contigLabel.setName(contigId);
		contigLabel.setText("<html><font size = -2><u>"
				+ contigId + "</u>" + "<br>length: "
				+ size / 1000 + " kb </html>");
		contig.setBorder(border);
		contig.setName(isReverseToString);
	}

	private void setSizeOfContig(int contigIndex, long size,
			boolean isLeftNeighbour) {

		ContigPanel contig;
		int wSize = (int) ((0.01 * size) / 4);
		if (isLeftNeighbour == true) {
			contig = leftContigs[contigIndex];
		} else {
			contig = rightContigs[contigIndex];
		}
		// int wSize =(int) Math.log((double)size)*10;
		if (wSize < 80) {
			contig.setPreferredSize(new Dimension(80, 50));
			contig.setMaximumSize(new Dimension(80, 50));
			contig.setMinimumSize(new Dimension(80, 50));
		} else if (wSize > 400){
			contig.setPreferredSize(new Dimension(400, 50));
			contig.setMaximumSize(new Dimension(400, 50));
			contig.setMinimumSize(new Dimension(400, 50));
		}else {
			contig.setPreferredSize(new Dimension(wSize, 50));
			contig.setMaximumSize(new Dimension(wSize, 50));
			contig.setMinimumSize(new Dimension(wSize, 50));
		}
	}
	
	/*
	 * Listener für die Elemente der Contig Liste
	 */
	public class ContigChangedListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
				System.out.println("Selected value is: "
						+ list.getSelectedValue());
				String selection = (String) list.getSelectedValue();
				control.selectContig(selection, "false");
			}
		}
	}

	public class ContigMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			ContigPanel jp = (ContigPanel) e.getSource();
			int subcomponents = jp.getComponentCount();
			JLabel child = null;
			String name = null;
			String isReverse = jp.getName();
			System.out.println(jp.getName());
			for (int i = 0; i < subcomponents; i++) {
				Component c = jp.getComponent(i);
				if (c.getName().length() > 0) {
					child = (JLabel) c;
					name = c.getName();
				}
			}
			if (child != null && name != null && isReverse != null) {
				control.selectContig(name, isReverse);
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

	/*
	 * Fange Events ab
	 */
	@Override
	public void event_fired(CagEvent event) {

		if (event.getEvent_type().equals(EventType.EVENT_CHOOSED_CONTIG)) {
			//this.chooseContigPanel.repaint();
			String contigName = event.getData();
			long size = event.getSize();
			boolean isRepeat = event.isRepetitive();
			boolean isReverse = event.isReverse();

			centralContigLabel.setText("<html><font size = -2><u>" + contigName
					+ "</u><br>length: " + size / 1000 + " kb </html>");
			centralContigLabel.setName(contigName);
			centralContig.setName(new Boolean(isReverse).toString());
			centralContig.setBorder(new ContigBorder(isRepeat, isReverse));
//	TODO
//			selectedContig = new ContigPanel();
//			selectedContig = centralContig.clone();
//			genomePanel.add(selectedContig);
			}

		if (event.getEvent_type().equals(EventType.EVENT_SEND_LEFT_NEIGHBOURS)) {

			this.centralContigLabel.validate();
			DNASequence[] leftNeighbours = null;
			leftNeighbours = event.getContigData();

			DNASequence contig1 = leftNeighbours[0];
			if (contig1 == null) {
				System.out.println("leftContig1 = null");
				leftContig1.setVisible(false);
			} else {
				String contigId = contig1.getId();
				long size = contig1.getSize();
				ContigBorder border = new ContigBorder(contig1.isRepetitive(),	contig1.isReverseComplemented());
				String isReverseToString = new Boolean(contig1.isReverseComplemented()).toString();				
				setContigAppearance(contigId, isReverseToString, size, border, true, 0);
				setSizeOfContig(0, contig1.getSize(), true);
				leftContig1.setVisible(true);
			}

			DNASequence contig2 = leftNeighbours[1];
			if (contig2 == null) {
				leftContig2.setVisible(false);
			} else {
				String contigId = contig2.getId();
				ContigBorder border = new ContigBorder(contig2.isRepetitive(),	contig2.isReverseComplemented());
				String isReverseToString = new Boolean(contig2.isReverseComplemented()).toString();
				
				setContigAppearance(contigId, isReverseToString, contig2.getSize(), border, true, 1);
				setSizeOfContig(1,contig2.getSize(), true);
				leftContig2.setVisible(true);
			}

			DNASequence contig3 = leftNeighbours[2];
			if (contig3 == null) {
				leftContig3.setVisible(false);
			} else {
				String contigId = contig3.getId();
				ContigBorder border = new ContigBorder(contig3.isRepetitive(),	contig3.isReverseComplemented());
				String isReverseToString = new Boolean(contig3.isReverseComplemented()).toString();
				
				setContigAppearance(contigId, isReverseToString, contig3.getSize(), border, true, 2);
				setSizeOfContig(2, contig3.getSize(), true);
				leftContig3.setVisible(true);
			}

			DNASequence contig4 = leftNeighbours[3];
			if (contig4 == null) {
				leftContig4.setVisible(false);
			} else {
				String contigId = contig4.getId();
				ContigBorder border = new ContigBorder(contig4.isRepetitive(),	contig4.isReverseComplemented());
				String isReverseToString = new Boolean(contig4.isReverseComplemented()).toString();
				
				setContigAppearance(contigId, isReverseToString, contig4.getSize(), border, true, 3);
				setSizeOfContig(3, contig4.getSize(), true);
				leftContig4.setVisible(true);

			}

			DNASequence contig5 = leftNeighbours[4];
			if (contig5 == null) {
				leftContig5.setVisible(false);
			} else {
				String contigId = contig5.getId();
				ContigBorder border = new ContigBorder(contig5.isRepetitive(),	contig5.isReverseComplemented());
				String isReverseToString = new Boolean(contig5.isReverseComplemented()).toString();
				
				setContigAppearance(contigId, isReverseToString, contig5.getSize(), border, true, 4);
				setSizeOfContig(4, contig5.getSize(), true);
				leftContig5.setVisible(true);
			}
		}

		if (event.getEvent_type().equals(EventType.EVENT_SEND_RIGHT_NEIGHBOURS)) {
			this.chooseContigPanel.validate();
			DNASequence[] rightNeighbours = null;
			rightNeighbours = event.getContigData();

			DNASequence contig1 = rightNeighbours[0];
			if (contig1 == null) {
				rightContig1.setVisible(false);
			} else {
				String contigId = contig1.getId();
				ContigBorder border = new ContigBorder(contig1.isRepetitive(),	contig1.isReverseComplemented());
				String isReverseToString = new Boolean(contig1.isReverseComplemented()).toString();
				
				setContigAppearance(contigId, isReverseToString, contig1.getSize(), border, false, 0);
				setSizeOfContig(0, contig1.getSize(), false);
				rightContig1.setVisible(true);
			}

			DNASequence contig2 = rightNeighbours[1];
			if (contig2 == null) {
				rightContig2.setVisible(false);
			} else {
				String contigId = contig2.getId();
				ContigBorder border = new ContigBorder(contig2.isRepetitive(),	contig2.isReverseComplemented());
				String isReverseToString = new Boolean(contig2.isReverseComplemented()).toString();
				
				setContigAppearance(contigId, isReverseToString, contig2.getSize(), border, false, 1);
				setSizeOfContig(1, contig2.getSize(), false);
				rightContig2.setVisible(true);
			}

			DNASequence contig3 = rightNeighbours[2];
			if (contig3 == null) {
				rightContig3.setVisible(false);
			} else {
				String contigId = contig3.getId();
				ContigBorder border = new ContigBorder(contig3.isRepetitive(),	contig3.isReverseComplemented());
				String isReverseToString = new Boolean(contig3.isReverseComplemented()).toString();
				
				setContigAppearance(contigId, isReverseToString, contig3.getSize(), border, false, 2);
				setSizeOfContig(2, contig3.getSize(), false);
				rightContig3.setVisible(true);
			}

			DNASequence contig4 = rightNeighbours[3];
			if (contig4 == null) {
				rightContig4.setVisible(false);
			} else {
				String contigId = contig4.getId();
				ContigBorder border = new ContigBorder(contig4.isRepetitive(),	contig4.isReverseComplemented());
				String isReverseToString = new Boolean(contig4.isReverseComplemented()).toString();
				
				setContigAppearance(contigId, isReverseToString, contig4.getSize(), border, false, 3);
				setSizeOfContig(3, contig4.getSize(), false);
				rightContig4.setVisible(true);
			}

			DNASequence contig5 = rightNeighbours[4];
			if (contig5 == null) {
				rightContig5.setVisible(false);
			} else {
				String contigId = contig5.getId();
				ContigBorder border = new ContigBorder(contig5.isRepetitive(),	contig5.isReverseComplemented());
				String isReverseToString = new Boolean(contig5.isReverseComplemented()).toString();
				
				setContigAppearance(contigId, isReverseToString, contig5.getSize(), border, false, 4);
				setSizeOfContig(4, contig5.getSize(), false);
				rightContig5.setVisible(true);
			}
		}

	}

}