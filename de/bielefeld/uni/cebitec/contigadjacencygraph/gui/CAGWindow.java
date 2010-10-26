package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.*;


public class CAGWindow extends JFrame implements ActionListener, ListSelectionListener{

	private JScrollPane listScroller;
	private JList list;
	private JPanel listContainer;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;
	private ChooseContigPanel chooseContigPanel;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                                  "javax.swing.plaf.metal.MetalLookAndFeel");
                                //  "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                                //UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                new CAGWindow();
            }
        });
	}
	
	public CAGWindow(){
		setTitle("View of contig adjacency graph");
		
		/*
		 * Menu
		 * TODO Was fuer Funktionen sollte das Menu haben??
		 */
		menuBar = new JMenuBar();		
		menu = new JMenu("Menu");	
		menuItem = new JMenuItem("Exit");
		//mnemonic: der erste Buchstabe wird unterstrichen; kann mit einer tastenkombi aktiviert werden
		// accelerator: eine Tastenkobi wird angezeigt und der Menupunkt kann auch mit dieser
		// 						kombi aktiviert werden
		//menu.setMnemonic(KeyEvent.VK_A);
		//menu.getAccessibleContext().setAccessibleDescription( "The only menu in this program that has menu items");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_1, ActionEvent.ALT_MASK));
		//menuItem.getAccessibleContext().setAccessibleDescription("Exit");
		
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
		chooseContigPanel = new ChooseContigPanel();
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
		String[] listData = { "Contig 1", "Contig 2", "Contig 3","Contig 1", "Contig 2", "Contig 3","Contig 1", "Contig 2", "Contig 3","Contig 1", "Contig 2", "Contig 3","Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" ,"Contig 1", "Contig 2", "Contig 3" };
		
		list = new JList(listData);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(10);
		list.addListSelectionListener(this);

		listScroller = new JScrollPane(list);
		listScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listScroller.setAlignmentY(RIGHT_ALIGNMENT);
		
        listContainer.setBorder(BorderFactory.createTitledBorder("Contig List"));
        listContainer.add(listScroller);
		add(listContainer, BorderLayout.EAST);
		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setSize(600, 600);
		setVisible(true);
		
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void valueChanged(ListSelectionEvent e) {
	    if (e.getValueIsAdjusting() == false) {
	    		String selection = (String) list.getSelectedValue();
	    		System.out.print(selection);
	    		
	    }
	}


}
