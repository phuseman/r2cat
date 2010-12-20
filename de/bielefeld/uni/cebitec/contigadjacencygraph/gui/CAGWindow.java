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
import java.util.Iterator;
import java.util.Vector;

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
	private JList list;
	private JPanel listContainer;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;
	private JPanel chooseContigPanel;
	private BoxLayout layout;
	private String[] listData;
	private CagCreator model;
	private Controller control;

	private ContigAppearance[] leftContigs;
	private ContigAppearance[] rightContigs;

	private JPanel leftContainer;
	private JPanel centerContainer;
	private JPanel rightContainer;
	
	private double[] leftSupport;
	private double[] rightSupport;

	private GlassPaneWithLines glassPanel;

	private ContigAppearance leftContig1;
	// private JLabel contigLabel1;
	private ContigAppearance leftContig2;
	// private JLabel contigLabel2;
	private ContigAppearance leftContig3;
	// private JLabel contigLabel3;
	private ContigAppearance leftContig4;
	// private JLabel contigLabel4;
	private ContigAppearance leftContig5;
	// private JLabel contigLabel5;

	private JPanel centralContig;
	// private JLabel centralContigLabel;

	private ContigAppearance rightContig1;
	// private JLabel rightcontigLabel1;
	private ContigAppearance rightContig2;
	// private JLabel rightcontigLabel2;
	private ContigAppearance rightContig3;
	// private JLabel rightcontigLabel3;
	private ContigAppearance rightContig4;
	// private JLabel rightcontigLabel4;
	private ContigAppearance rightContig5;
	private boolean rightContainerFull = false;
	private boolean leftContainerFull = false;
	
	private ButtonGroup leftGroup = new ButtonGroup();
	private ButtonGroup rightGroup = new ButtonGroup();
	private JPanel leftRadioButtonContainer;
	private JPanel rightRadioButtonContainer;
	
	private Vector<String> selectedRadioButtons;
	private int z = 0;


	// private JLabel rightcontigLabel5;

	public CAGWindow(CagCreator myModel, Controller controller) {
		window = this;
		this.model = myModel;
		listData = model.getListData();
		myModel.addEventListener(this);
		this.control = controller;
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
		chooseContigPanel.setPreferredSize(new Dimension(1000, 400));
		// layout = new GroupLayout(chooseContigPanel);
		chooseContigPanel.setBackground(Color.WHITE);
		add(chooseContigPanel, BorderLayout.CENTER);

		leftContigs = new ContigAppearance[5];
	/*	leftContigs[0] = leftContig1;
		leftContigs[1] = leftContig2;
		leftContigs[2] = leftContig3;
		leftContigs[3] = leftContig4;
		leftContigs[4] = leftContig5;*/

		rightContigs = new ContigAppearance[5];
		/*rightContigs[0] = rightContig1;
		rightContigs[1] = rightContig2;
		rightContigs[2] = rightContig3;
		rightContigs[3] = rightContig4;
		rightContigs[4] = rightContig5;*/

		layout = new BoxLayout(chooseContigPanel, BoxLayout.LINE_AXIS);
		chooseContigPanel.setLayout(layout);

		leftContainer = new JPanel();
		leftRadioButtonContainer = new JPanel();
		centerContainer = new JPanel();
		rightRadioButtonContainer = new JPanel();
		rightContainer = new JPanel();

		BoxLayout leftBoxLayout = new BoxLayout(leftContainer,
				BoxLayout.PAGE_AXIS);
		BoxLayout leftRadioBoxLayout = new BoxLayout(leftRadioButtonContainer,
				BoxLayout.PAGE_AXIS);
		BoxLayout centerBoxLayout = new BoxLayout(centerContainer,
				BoxLayout.PAGE_AXIS);
		BoxLayout rightRadioBoxLayout = new BoxLayout(rightRadioButtonContainer,
				BoxLayout.PAGE_AXIS);
		BoxLayout rightBoxLayout = new BoxLayout(rightContainer,
				BoxLayout.PAGE_AXIS);

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

		centerContainer.setLayout(centerBoxLayout);
		centerContainer.setBackground(Color.WHITE);
		centerContainer.setPreferredSize(new Dimension(310, 1000));
		centerContainer.setMinimumSize(new Dimension(310, 1000));
		centerContainer.setMaximumSize(new Dimension(310, 1000));

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
		listContainer.setPreferredSize(new Dimension(100, 450));

		/*
		 * Diese Liste sollte alle Contigs beinhalten; muessen keinen Strings
		 * sein JList nimmt einen beleibigen Objekttyp entgegen; werden aber in
		 * der Liste als strings repraesentiert Mittels einer
		 */
		/*
		 * Liste mit den Namen der Contigs schleife hat Contig vor der id des
		 * Contig gesetzt war nur ästhetischer später werden sowieso nur die
		 * Namen aus dem Fasta file verwendet
		 */
		// for (int i = 0; i < listData.length; i++) {
		// String contigname = listData[i];
		// listData[i] = "Contig " + contigname;
		// }

		/*
		 * TODO länge der Liste sollte sich an die größe des Fensters
		 * anpassen Und sie sollte ein wenig breiter sein.
		 */
		list = new JList(listData);
		/* list.setPreferredSize(new Dimension(85, 490)); */
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(20);
		list.addListSelectionListener(new ContigChangedListener());

		listScroller = new JScrollPane(list);
		listScroller
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScroller
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listScroller.setAlignmentY(RIGHT_ALIGNMENT);

		listContainer.setPreferredSize(new Dimension(90, 500));
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

	private void setLineInPanel() {
		if(rightContainerFull && leftContainerFull){
			glassPanel.setLine(leftContainer,rightContainer, centralContig, leftContainerFull, rightContainerFull,leftSupport, rightSupport );
			glassPanel.setOpaque(false);
			glassPanel.setPreferredSize(chooseContigPanel.getSize());
			getGlassPane().setVisible(true);
			rightContainerFull=false;
			leftContainerFull=false;
		}
	}

	/*
	 * Listener für die Elemente der Contig Liste
	 */
	public class ContigChangedListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
				String selection = (String) list.getSelectedValue();
				control.selectContig(selection, "false");

				SwingWorkerClass threadForLeftNeighbours = new SwingWorkerClass();
				threadForLeftNeighbours.execute();

				ThreadClassForRightNeighours threadForRightNeighbours = new ThreadClassForRightNeighours();
				threadForRightNeighbours.execute();
			}
		}
	}

	class SwingWorkerClass extends SwingWorker<String, String> {

		@Override
		protected String doInBackground() {
			System.out.println("starte thread fuer linke Nachbarn");
			model.sendLeftNeighbours();
			return null;
		}

		@Override
		protected void done() {
			super.done();
		}
	}

	class ThreadClassForRightNeighours extends SwingWorker<String, String> {

		@Override
		protected String doInBackground() {
			System.out.println("starte thread fuer rechte Nachbarn");
			model.sendRightNeighbours();
			return null;
		}

		@Override
		protected void done() {
			super.done();
		}
	}
	
	public class RadioButtonActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (z == 0){
				z++;
				System.out.println("Ein contig wurde ausgewählt");
				String idOfSelectedContig = e.getActionCommand();
				selectedRadioButtons.add(idOfSelectedContig);
				model.addSelectedContig(idOfSelectedContig);
			}else{
				for (String actionCommandOfContig : selectedRadioButtons) {
					System.out.println("prüfe nun ob die Strings im Vektor mit dem ausgewähltem übereinstimmen.");
					if(!actionCommandOfContig.equals(e.getActionCommand())){
						System.out.println("Ein contig wurde ausgewählt");
						String idOfSelectedContig = e.getActionCommand();
						selectedRadioButtons.add(idOfSelectedContig);
						model.addSelectedContig(idOfSelectedContig);
					}
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
				control.selectContig(name, isReverse);

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

	/*
	 * Fange Events ab
	 */
	@Override
	public void event_fired(CagEvent event) {

		if (event.getEvent_type().equals(EventType.EVENT_CHOOSED_CONTIG)) {

			DNASequence contigNode = event.getContigNode();
			centralContig = new ContigAppearance(contigNode);
			centralContig.addMouseListener(new ContigMouseListener());
			
			if (centerContainer.getComponentCount() > 0) {
				centerContainer.removeAll();
			}		
			centerContainer.add(centralContig);
			centerContainer.updateUI();
		}

		if (event.getEvent_type().equals(EventType.EVENT_SEND_LEFT_NEIGHBOURS)) {

			Vector<DNASequence> leftNeighbours = null;
			ContigAppearance contigPanel = null;
			leftNeighbours = event.getContigData();
			JRadioButton radioButton;
			leftSupport = new double[5];

			if (leftContainer.getComponentCount() > 0&& leftRadioButtonContainer.getComponentCount() > 0) {
				leftContainer.removeAll();
				leftRadioButtonContainer.removeAll();
			}
			int t = 0;
			for (Iterator<DNASequence> neighbour = leftNeighbours.iterator(); neighbour
					.hasNext();) {
				while (t < 5) {
					DNASequence dnaSequence = (DNASequence) neighbour.next();
					leftSupport[t] = dnaSequence.getSupportComparativeToCentralContig();
					contigPanel = new ContigAppearance(dnaSequence);
					contigPanel.setAlignmentX(RIGHT_ALIGNMENT);
					contigPanel.addMouseListener(new ContigMouseListener());
					contigPanel.setVisible(true);
					
					radioButton = new JRadioButton();
					radioButton.setBackground(Color.WHITE);
					radioButton.setActionCommand(dnaSequence.getId());
					radioButton.addActionListener(new RadioButtonActionListener());
				
					leftGroup.add(radioButton);
					leftRadioButtonContainer.add(radioButton);
					
					leftContainer.add(contigPanel);
					if(t <4){
						leftContainer.add(Box.createVerticalGlue());
						leftRadioButtonContainer.add(Box.createVerticalGlue());
					}
					leftContainer.updateUI();
					t++;
				}
				leftContainerFull = true;

				setLineInPanel();
				break;
			}
		}

		if (event.getEvent_type().equals(EventType.EVENT_SEND_RIGHT_NEIGHBOURS)) {
	
			Vector<DNASequence> rightNeighbours = null;
			ContigAppearance contigPanel = null;
			JRadioButton radioButton;
			rightSupport = new double[5];
			rightNeighbours = event.getContigData();
			int s = 0;
			if (rightContainer.getComponentCount() > 0 && rightRadioButtonContainer.getComponentCount() > 0) {
				rightContainer.removeAll();
				rightRadioButtonContainer.removeAll();
			}

			for (Iterator<DNASequence> neighbour = rightNeighbours.iterator(); neighbour
					.hasNext();) {
				while (s < 5) {
					DNASequence dnaSequence = (DNASequence) neighbour.next();
					rightSupport[s] = dnaSequence.getSupportComparativeToCentralContig();
					contigPanel = new ContigAppearance(dnaSequence);
					contigPanel.setAlignmentX(LEFT_ALIGNMENT);
					contigPanel.addMouseListener(new ContigMouseListener());
					contigPanel.setVisible(true);
					
					radioButton = new JRadioButton();
					radioButton.setBackground(Color.WHITE);
					radioButton.setActionCommand(dnaSequence.getId());
					radioButton.addActionListener(new RadioButtonActionListener());
					
					rightGroup.add(radioButton);
					rightRadioButtonContainer.add(radioButton);
					
					rightContainer.add(contigPanel);
					if(s<4){
						rightContainer.add(Box.createVerticalGlue());
						rightRadioButtonContainer.add(Box.createVerticalGlue());
					}
					rightContainer.updateUI();
					s++;
				}

				rightContainerFull =true;
				setLineInPanel();
				break;
			}
		}

	}

}