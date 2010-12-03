package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
 * TODO heute methode die die contigs erzeugt und
 * eine Mehtode um die groesse des Contigs zu veraendern
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
	// private ChooseContigPanel chooseContigPanel;
	private JPanel chooseContigPanel;
	private GroupLayout layout;
	private JPanel genomePanel;
	private String[] listData;
	private CagCreator model;
	private Controller control;

	private ContigBorder contigBorder1;
	private ContigBorder contigBorder2;
	private ContigBorder contigBorder3;
	private ContigBorder contigBorder4;
	private ContigBorder contigBorder5;
	private ContigBorder centralContigBorder;
	private ContigBorder rightcontigBorder1;
	private ContigBorder rightcontigBorder2;
	private ContigBorder rightcontigBorder3;
	private ContigBorder rightcontigBorder4;
	private ContigBorder rightcontigBorder5;

	private JPanel leftContig1;
	private JLabel contigLabel1;
	private JPanel leftContig2;
	private JLabel contigLabel2;
	private JPanel leftContig3;
	private JLabel contigLabel3;
	private JPanel leftContig4;
	private JLabel contigLabel4;
	private JPanel leftContig5;
	private JLabel contigLabel5;

	private JPanel centralContig;
	private JLabel centralContigLabel;

	private JPanel rightContig1;
	private JLabel rightcontigLabel1;
	private JPanel rightContig2;
	private JLabel rightcontigLabel2;
	private JPanel rightContig3;
	private JLabel rightcontigLabel3;
	private JPanel rightContig4;
	private JLabel rightcontigLabel4;
	private JPanel rightContig5;
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
		leftContig1 = new JPanel();
		leftContig2 = new JPanel();
		leftContig3 = new JPanel();
		leftContig4 = new JPanel();
		leftContig5 = new JPanel();
		
		/*JPanel[] panel = new JPanel[5];
		for (int i=0; i!=5 ;i++) { 
			panel[i] = new JPanel();
			
		}*/

		centralContig = new JPanel();

		rightContig1 = new JPanel();
		rightContig2 = new JPanel();
		rightContig3 = new JPanel();
		rightContig4 = new JPanel();
		rightContig5 = new JPanel();

		// leftContig1.setLayout(new BorderLayout().NORTH);
		// GroupLayout lableLayout = new GroupLayout(leftContig1);
		contigLabel1 = new JLabel();
		leftContig1.add(contigLabel1);
		leftContig1.addMouseListener(new ContigMouseListener());

		contigLabel2 = new JLabel();
		leftContig2.add(contigLabel2);
		leftContig2.addMouseListener(new ContigMouseListener());

		contigLabel3 = new JLabel();
		leftContig3.add(contigLabel3);
		leftContig3.addMouseListener(new ContigMouseListener());

		contigLabel4 = new JLabel();
		leftContig4.add(contigLabel4);
		leftContig4.addMouseListener(new ContigMouseListener());

		contigLabel5 = new JLabel();
		leftContig5.add(contigLabel5);
		leftContig5.addMouseListener(new ContigMouseListener());

		centralContigLabel = new JLabel();
		centralContig.add(centralContigLabel);
		centralContig.addMouseListener(new ContigMouseListener());

		rightcontigLabel1 = new JLabel();
		rightContig1.add(rightcontigLabel1);
		rightContig1.addMouseListener(new ContigMouseListener());

		rightcontigLabel2 = new JLabel();
		rightContig2.add(rightcontigLabel2);
		rightContig2.addMouseListener(new ContigMouseListener());

		rightcontigLabel3 = new JLabel();
		rightContig3.add(rightcontigLabel3);
		rightContig3.addMouseListener(new ContigMouseListener());

		rightcontigLabel4 = new JLabel();
		rightContig4.add(rightcontigLabel4);
		rightContig4.addMouseListener(new ContigMouseListener());

		rightcontigLabel5 = new JLabel();
		rightContig5.add(rightcontigLabel5);
		rightContig5.addMouseListener(new ContigMouseListener());

		leftContig1.setBackground(Color.WHITE);
		leftContig1.setPreferredSize(new Dimension(100, 50));
		leftContig1.setMaximumSize(new Dimension(100, 50));
		leftContig1.setMinimumSize(new Dimension(100, 50));

		leftContig2.setBackground(Color.WHITE);
		leftContig2.setPreferredSize(new Dimension(100, 50));
		leftContig2.setMaximumSize(new Dimension(100, 50));
		leftContig2.setMinimumSize(new Dimension(100, 50));

		leftContig3.setBackground(Color.WHITE);
		leftContig3.setPreferredSize(new Dimension(100, 50));
		leftContig3.setMaximumSize(new Dimension(100, 50));
		leftContig3.setMinimumSize(new Dimension(100, 50));

		leftContig4.setBackground(Color.WHITE);
		leftContig4.setPreferredSize(new Dimension(100, 50));
		leftContig4.setMaximumSize(new Dimension(100, 50));
		leftContig4.setMinimumSize(new Dimension(100, 50));

		leftContig5.setBackground(Color.WHITE);
		leftContig5.setPreferredSize(new Dimension(100, 50));
		leftContig5.setMaximumSize(new Dimension(100, 50));
		leftContig5.setMinimumSize(new Dimension(100, 50));

		centralContig.setBackground(Color.WHITE);
		centralContig.setPreferredSize(new Dimension(100, 50));
		centralContig.setMaximumSize(new Dimension(100, 50));
		centralContig.setMinimumSize(new Dimension(100, 50));

		rightContig1.setBackground(Color.WHITE);
		rightContig1.setPreferredSize(new Dimension(100, 50));
		rightContig1.setMaximumSize(new Dimension(100, 50));
		rightContig1.setMinimumSize(new Dimension(100, 50));

		rightContig2.setBackground(Color.WHITE);
		rightContig2.setPreferredSize(new Dimension(100, 50));
		rightContig2.setMaximumSize(new Dimension(100, 50));
		rightContig2.setMinimumSize(new Dimension(100, 50));

		rightContig3.setBackground(Color.WHITE);
		rightContig3.setPreferredSize(new Dimension(100, 50));
		rightContig3.setMaximumSize(new Dimension(100, 50));
		rightContig3.setMinimumSize(new Dimension(100, 50));

		rightContig4.setBackground(Color.WHITE);
		rightContig4.setPreferredSize(new Dimension(100, 50));
		rightContig4.setMaximumSize(new Dimension(100, 50));
		rightContig4.setMinimumSize(new Dimension(100, 50));

		rightContig5.setBackground(Color.WHITE);
		rightContig5.setPreferredSize(new Dimension(100, 50));
		rightContig5.setMaximumSize(new Dimension(100, 50));
		rightContig5.setMinimumSize(new Dimension(100, 50));

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
		 * Liste mit den Namen der Contigs TODO am Ende sollte diese for
		 * schleife verschwinden, da "Contig" schon im Namen sein sollte nur bei
		 * diesen Testdaten ist das anders, weil die Contigs umbenannt wurden.
		 */
		for (int i = 0; i < listData.length; i++) {
			String contigname = listData[i];
			listData[i] = "Contig " + contigname;
		}

		list = new JList(listData);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(10);
		list.addListSelectionListener(new ContigChangedListener());

		listScroller = new JScrollPane(list);
		listScroller
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScroller
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listScroller.setAlignmentY(RIGHT_ALIGNMENT);

		listContainer
				.setBorder(BorderFactory.createTitledBorder("Contig List"));
		listContainer.add(listScroller);
		add(listContainer, BorderLayout.EAST);

		// Hier sollen spaeter die ausgewaehlten Contigs angezeigt werden
		genomePanel = new JPanel();
		genomePanel.setBackground(Color.WHITE);
		add(genomePanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// setSize(Toolkit.getDefaultToolkit().getScreenSize()); // hier wird
		// das Fenster auf die Größe des Bildschirmes angepasst.
		setSize(900, 900);
		setVisible(true);

		pack();
	}

	private Dimension calculateSizeOfContig(long sizeInBp) {
		Dimension size = null;
		int lenght = 100;
		lenght = (int) Math.log((double) sizeInBp);
		size = new Dimension(lenght, 50);
		if (size == null) {
			size = new Dimension(100, 50);
		}
		return size;
	}

	/*
	 * Dieses Layout für die Panel ist im Moment überflüssig; den
	 * umgebrochenen Text die die contigs tragen wird durch
	 * "<html><font size = -2><u>"
	 * +contigName+"</u><br>length: "+size+" bp </html>" erzeugt. TODO sollte
	 * ich diese Methode behalten?
	 */
	private void setGroupLayoutForContigPanel(JPanel contigPanel,
			JLabel contigLabel) {
		GroupLayout layout = new GroupLayout(contigPanel);
		contigPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(contigLabel)));

		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(contigLabel)));
	}

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

			JPanel jp = (JPanel) e.getSource();
			int subcomponents = jp.getComponentCount();
			JLabel child = null;
			String name = null;
			String isReverse = jp.getName();
			System.out.println(jp.getName());
			for (int i = 0; i < subcomponents; i++) {
				Component c = jp.getComponent(i);
				if (c.getName().length() > 0) {
					System.out.println("Found contig label!");
					child = (JLabel) c;
					name = c.getName();
				}
			}
			if (child != null && name != null && isReverse != null) {
				// JOptionPane.showMessageDialog(jp.getTopLevelAncestor(),
				// "Click auf "+child.getClass().getCanonicalName()+": "+child.getText());
				control.selectContig(name, isReverse);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
		}
	}

	public class ExitItemListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			window.dispose();
		}

	}

	/*
	 * TODO fange weitere Events ab
	 */
	@Override
	public void event_fired(CagEvent event) {

		if (event.getEvent_type().equals(EventType.EVENT_CHOOSED_CONTIG)) {
			this.chooseContigPanel.repaint();
			String contigName = event.getData();
			long size = event.getSize();
			boolean isRepeat = event.isRepetitive();
			boolean isReverse = event.isReverse();

			centralContigLabel.setText("<html><font size = -2><u>" + contigName
					+ "</u><br>length: " + size / 1000 + " kb </html>");
			centralContigLabel.setName(contigName);
			centralContig.setName(new Boolean(isReverse).toString());
			centralContig.setBorder(new ContigBorder(isRepeat, isReverse));
		}

		if (event.getEvent_type().equals(EventType.EVENT_SEND_LEFT_NEIGHBOURS)) {
			this.centralContigLabel.validate();		
			DNASequence[]leftNeighbours = null;
			leftNeighbours = event.getContigData();

			DNASequence contig1 = leftNeighbours[0];
			if (contig1 == null) {
				System.out.println("leftContig1 = null");
				leftContig1.setVisible(false);	
			} else {
				contigLabel1.setName(contig1.getId());
				contigLabel1.setText("<html><font size = -2><u>"
						+ contig1.getId() + "</u>" + "<br>length: "
						+ contig1.getSize() / 1000 + " kb </html>");
				leftContig1.setBorder(new ContigBorder(contig1.isRepetitive(),
						contig1.isReverseComplemented()));
				leftContig1
						.setName(new Boolean(contig1.isReverseComplemented())
								.toString());
				leftContig1.setVisible(true);
			}

			DNASequence contig2 = leftNeighbours[1];
			if (contig2 == null) {
				leftContig2.setVisible(false);	
			} else {
				contigLabel2.setName(contig2.getId());
				contigLabel2.setText("<html><font size = -2><u>"
						+ contig2.getId() + "</u>" + "<br>length: "
						+ contig2.getSize() / 1000 + " kb </html>");
				leftContig2.setBorder(new ContigBorder(contig2.isRepetitive(),
						contig2.isReverseComplemented()));
				leftContig2
						.setName(new Boolean(contig2.isReverseComplemented())
								.toString());
				leftContig2.setVisible(true);
			}

			DNASequence contig3 = leftNeighbours[2];
			if (contig3 == null) {
				leftContig3.setVisible(false);	
			} else {
				contigLabel3.setName(contig3.getId());
				contigLabel3.setText("<html><font size = -2><u>"
						+ contig3.getId() + "</u>" + "<br>length: "
						+ contig3.getSize() / 1000 + " kb </html>");
				leftContig3.setBorder(new ContigBorder(contig3.isRepetitive(),
						contig3.isReverseComplemented()));
				leftContig3
						.setName(new Boolean(contig3.isReverseComplemented())
								.toString());
				leftContig3.setVisible(true);
			}

			DNASequence contig4 = leftNeighbours[3];
			if (contig4 == null) {
				leftContig4.setVisible(false);				
			} else {
				contigLabel4.setName(contig4.getId());
				contigLabel4.setText("<html><font size = -2><u>"
						+ contig4.getId() + "</u>" + "<br>length: "
						+ contig4.getSize() / 1000 + " kb </html>");
				leftContig4.setBorder(new ContigBorder(contig4.isRepetitive(),
						contig4.isReverseComplemented()));
				leftContig4
						.setName(new Boolean(contig4.isReverseComplemented())
								.toString());
				leftContig4.setVisible(true);
				
			}

			DNASequence contig5 = leftNeighbours[4];
			if (contig5 == null) {
				leftContig5.setVisible(false);
			} else {
				contigLabel5.setName(contig5.getId());
				contigLabel5.setText("<html><font size = -2><u>"
						+ contig5.getId() + "</u>" + "<br>length: "
						+ contig5.getSize() / 1000 + " kb </html>");
				leftContig5.setBorder(new ContigBorder(contig5.isRepetitive(),
						contig5.isReverseComplemented()));
				leftContig5
						.setName(new Boolean(contig5.isReverseComplemented())
								.toString());
				leftContig5.setVisible(true);
			}
			//this.chooseContigPanel.updateUI();	
		}

		if (event.getEvent_type().equals(EventType.EVENT_SEND_RIGHT_NEIGHBOURS)) {
			this.chooseContigPanel.validate();	
			DNASequence[] rightNeighbours = null;
			rightNeighbours = event.getContigData();

			DNASequence contig1 = rightNeighbours[0];
			if (contig1 == null) {
				rightContig1.setVisible(false);
			} else {
				rightcontigLabel1.setName(contig1.getId());
				rightcontigLabel1.setText("<html><font size = -2><u>"
						+ contig1.getId() + "</u>" + "<br>length: "
						+ contig1.getSize() / 1000 + " kb </html>");
				rightContig1.setBorder(new ContigBorder(contig1.isRepetitive(),
						contig1.isReverseComplemented()));
				rightContig1.setName(new Boolean(contig1
						.isReverseComplemented()).toString());
				rightContig1.setVisible(true);
			}

			DNASequence contig2 = rightNeighbours[1];
			if (contig2 == null) {
				rightContig2.setVisible(false);
			} else {
				rightcontigLabel2.setName(contig2.getId());
				rightcontigLabel2.setText("<html><font size = -2><u>"
						+ contig2.getId() + "</u>" + "<br>length: "
						+ contig2.getSize() / 1000 + " kb </html>");
				rightContig2.setBorder(new ContigBorder(contig2.isRepetitive(),
						contig2.isReverseComplemented()));
				rightContig2.setName(new Boolean(contig2
						.isReverseComplemented()).toString());
				rightContig2.setVisible(true);
			}

			DNASequence contig3 = rightNeighbours[2];
			if (contig3 == null) {
				rightContig3.setVisible(false);
			} else {
				rightcontigLabel3.setName(contig3.getId());
				rightcontigLabel3.setText("<html><font size = -2><u>"
						+ contig3.getId() + "</u>" + "<br>length: "
						+ contig3.getSize() / 1000 + " kb </html>");
				rightContig3.setBorder(new ContigBorder(contig3.isRepetitive(),
						contig3.isReverseComplemented()));
				rightContig3.setName(new Boolean(contig3
						.isReverseComplemented()).toString());
				rightContig3.setVisible(true);
			}

			DNASequence contig4 = rightNeighbours[3];
			if (contig4 == null) {
				rightContig4.setVisible(false);
			} else {
				rightcontigLabel4.setName(contig4.getId());
				rightcontigLabel4.setText("<html><font size = -2><u>"
						+ contig4.getId() + "</u>" + "<br>length: "
						+ contig4.getSize() / 1000 + " kb </html>");
				rightContig4.setBorder(new ContigBorder(contig4.isRepetitive(),
						contig4.isReverseComplemented()));
				rightContig4.setName(new Boolean(contig4
						.isReverseComplemented()).toString());
				rightContig4.setVisible(true);
			}

			DNASequence contig5 = rightNeighbours[4];
			if (contig5 == null) {
				rightContig5.setVisible(false);
			} else {
				rightcontigLabel5.setName(contig5.getId());
				rightcontigLabel5.setText("<html><font size = -2><u>"
						+ contig5.getId() + "</u>" + "<br>length: "
						+ contig5.getSize() / 1000 + " kb </html>");
				rightContig5.setBorder(new ContigBorder(contig5.isRepetitive(),
						contig5.isReverseComplemented()));
				rightContig5.setName(new Boolean(contig5
						.isReverseComplemented()).toString());
				rightContig5.setVisible(true);
			}			
		}

	}

}