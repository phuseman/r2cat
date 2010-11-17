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

/*
 * Ist das Abbild vom Model
 */
public class CAGWindow extends JFrame implements CagEventListener{//  ActionListener{  CagEventListener{
	
	private CAGWindow window;
	private JScrollPane listScroller;
	private JList list;
	private JPanel listContainer;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;
//	private ChooseContigPanel chooseContigPanel;
	private JPanel chooseContigPanel;
	private GroupLayout layout;
	private JPanel genomePanel;
	private String[] listData;
	private CagCreator model;
	private Controller control;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
	private ContigBorder border;
	private ContigBorder reverseBorder;
	private ContigBorder repeatBorder;
	private ContigBorder reverseRepeatBorder;
	
	private JPanel leftContig1;
	private JButton leftContig2 ;
	private JButton leftContig3 ;
	private JButton leftContig4 ;
	private JButton leftContig5 ;

	private JButton centralContig;

	private JButton rightContig1 ;
	private JButton rightContig2 ;
	private JButton rightContig3 ;
	private JButton rightContig4 ;
	private JButton rightContig5 ;

	
	
	public CAGWindow(CagCreator myModel, Controller controller){
		window = this;
		this.model = myModel;
		listData = model.getListData();
		myModel.addEventListener(this);
		this.control = controller;
		setTitle("View of contig adjacency graph");
		
		/*
		 * Menu
		 * TODO Was fuer Funktionen sollte das Menu haben??
		 */
		menuBar = new JMenuBar();		
		menu = new JMenu("Menu");	
		menuItem = new JMenuItem("Exit");
		menuItem.addActionListener(new ExitItemListener());
		
		/*a group of JMenuItems
		 * Hier werden die Menupunkte hinzugefuegt
		 * TODO menupunkte hinzufuegen
		 */
		menu.add(menuItem);		
		menuBar.add(menu);
		add(menuBar, BorderLayout.NORTH);
		
		/*
		 * Dieses Panel enhaelt das Contig das Ausgewaehlt wurde und deren moegliche Nachbarn
		 */
//		chooseContigPanel = new ChooseContigPanel();
		chooseContigPanel = new JPanel();
			
			layout = new GroupLayout(chooseContigPanel);
			
			chooseContigPanel.setLayout(layout);
			
			border = new ContigBorder(false , false);
			reverseBorder = new ContigBorder(false, true);
			repeatBorder = new ContigBorder(true, false);
			reverseRepeatBorder = new ContigBorder(true, true);
			


			leftContig1 = new JPanel();
			leftContig2 = new JButton();
			leftContig3 = new JButton();
			leftContig4 = new JButton();
			leftContig5 = new JButton();

			centralContig = new JButton();

			rightContig1 = new JButton();
			rightContig2 = new JButton();
			rightContig3 = new JButton();
			rightContig4 = new JButton();
			rightContig5 = new JButton();
			
			JLabel contigLabel = new JLabel("Contig 1");
			contigLabel.setName("contigLabel");
			leftContig1.add(contigLabel);
			//GroupLayout lableLayout = new GroupLayout(leftContig1);
			leftContig1.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseClicked(MouseEvent e) {
					
					JPanel jp = (JPanel)e.getSource();
					int subcomponents = jp.getComponentCount();
					JLabel child = null;
					for(int i = 0;i<subcomponents;i++) {
						Component c = jp.getComponent(i);
						if(c.getName().equals("contigLabel")) {
							System.out.println("Found contig label!");
							child = (JLabel)c;
						}
					}
					if(child!=null) {
						JOptionPane.showMessageDialog(jp.getTopLevelAncestor(), "Click auf "+child.getClass().getCanonicalName()+": "+child.getText());
					}
				}
			});
			//leftContig1.setText("Contig 1");
			//leftContig1.setContentAreaFilled(false);
			setGroupLayoutForContigPanel(leftContig1,contigLabel);
			leftContig1.setBorder(border);
			leftContig1.setOpaque(false);
			leftContig1.setPreferredSize(new Dimension(100,50));
			leftContig1.setMaximumSize(new Dimension(100,50));
			leftContig1.setMinimumSize(new Dimension(100,50));
			
			leftContig2.setText("Contig 2");
			leftContig2.setContentAreaFilled(false);
			leftContig2.setBorder(repeatBorder);
			leftContig3.setText("Contig 3");
			leftContig3.setBorder(border);
			leftContig3.setContentAreaFilled(false);
			leftContig4.setText("Contig 4");
			leftContig4.setBorder(border);
			leftContig4.setContentAreaFilled(false);
			leftContig5.setText("Contig 5");
			leftContig5.setBorder(reverseBorder);
			leftContig5.setContentAreaFilled(false);
			
			//centralContig.setText("aktuelles Contig");
			//centralContig.setBorder(reverseRepeatBorder);
			centralContig.setContentAreaFilled(false);
			
			rightContig1.setText("Contig 1");
			rightContig1.setContentAreaFilled(false);
			rightContig1.setBorder(reverseBorder);
			rightContig2.setText("Contig 2");
			rightContig2.setContentAreaFilled(false);
			rightContig2.setBorder(border);
			rightContig3.setText("Contig 3");
			rightContig3.setContentAreaFilled(false);
			rightContig3.setBorder(border);
			rightContig4.setText("Contig 4");
			rightContig4.setContentAreaFilled(false);
			rightContig4.setBorder(repeatBorder);
			rightContig5.setText("Contig 5");
			rightContig5.setContentAreaFilled(false);
			rightContig5.setBorder(border);
			/*
			 * automatic gaps that correspond to preferred distances between
			 * neighboring components (or between a component and container border)
			 */
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			layout.setHorizontalGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(leftContig1).addComponent(leftContig2)
							.addComponent(leftContig3).addComponent(leftContig4)
							.addComponent(leftContig5))
					.addComponent(centralContig)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(rightContig1).addComponent(	rightContig2)
							.addComponent(rightContig3).addComponent(rightContig4)
							.addComponent(rightContig5))
					);

			layout.setVerticalGroup(layout.createSequentialGroup()
					.addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.BASELINE).addComponent(leftContig1)
									.addComponent(rightContig1))
					.addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.BASELINE).addComponent(
									leftContig2).addComponent(rightContig2))
					.addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.BASELINE).addComponent(
									leftContig3).addComponent(centralContig)
									.addComponent(rightContig3))
					.addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.BASELINE).addComponent(
									leftContig4).addComponent(rightContig4))
					.addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.BASELINE).addComponent(
									leftContig5).addComponent(rightContig5)));
		
		chooseContigPanel.setBackground(Color.WHITE);
		add(chooseContigPanel , BorderLayout.CENTER);
		/*
		 * Dieses Panel enthaelt alle Contigs dieses Genoms als Liste
		 * Spaeter sollte eine Eintrag waehlbar sein.
		 * TODO Dieses Contig sollte dann in der Mitte des oberen Panels auftauchen 
		 */
		listContainer = new JPanel();//new GridLayout(1,1)
		
		/*TODO
		 * Diese Liste sollte alle Contigs beinhalten; muessen keinen Strings sein
		 * JList nimmt einen beleibigen Objekttyp entgegen; werden aber in der Liste als strings repraesentiert
		 * Mittels einer 
		 */
		/*
		 * Liste mit den Namen der Contigs 
		 */

		for (int i = 0; i < listData.length ; i++){
			String contigname = listData[i];
			listData[i] = "Contig "+contigname;
		}
		
		list = new JList(listData);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(10);
		list.addListSelectionListener(new ContigChangedListener());

		listScroller = new JScrollPane(list);
		listScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listScroller.setAlignmentY(RIGHT_ALIGNMENT);
		
        listContainer.setBorder(BorderFactory.createTitledBorder("Contig List"));
        listContainer.add(listScroller);
		add(listContainer, BorderLayout.EAST);
		
		// Hier sollen spaeter die ausgewaehlten Contigs angezeigt werden
		genomePanel = new JPanel();
		genomePanel.setBackground(Color.WHITE);
		add(genomePanel, BorderLayout.SOUTH);
		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//setSize(Toolkit.getDefaultToolkit().getScreenSize()); // hier wird das Fenster auf die Größe des Bildschirmes angepasst.
		setSize(900, 900);
		setVisible(true);
		
		pack();
	}

	private void setGroupLayoutForContigPanel(JPanel contigPanel, JLabel contigLabel) {
		GroupLayout layout = new GroupLayout(contigPanel);
		contigPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(contigLabel))
				);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING).addComponent(contigLabel)
								));
	}
	
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
	public class ContigChangedListener implements ListSelectionListener{

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
				System.out.println("Selected value is: "+list.getSelectedValue());
				String selection = (String) list.getSelectedValue();
				control.selectContig(selection);				
			}
			
		}
	}
	
	public class ExitItemListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			window.dispose();
		}
		
	}
	@Override
	public void event_fired(CagEvent event) {
		
		if(event.getEvent_type().equals(EventType.EVENT_CHOOSED_CONTIG)){
			String contigName = event.getData();
			long size = event.getSize();
			boolean isRepeat = event.isRepetitiv();
			centralContig.setText(contigName+" \n\r"+"Laenge: "+size+" bp");
			if (isRepeat == true){				
				centralContig.setBorder(new ContigBorder(true, false));
			}else{
				centralContig.setBorder(new ContigBorder(false, false));
			}
		}
		
	}


}