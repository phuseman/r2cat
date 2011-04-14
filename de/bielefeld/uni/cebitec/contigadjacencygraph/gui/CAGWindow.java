package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class CAGWindow extends JFrame {

	private CAGWindow window;
	public CagCreator model;
	
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;

	
	public CAGWindow(CagController controller, CagCreator myModel) {

		window = this;
		this.model = myModel;		
	}
	
	public void initWindow(){
		
		setTitle("View of a contig adjacency graph");
		setName("fenster");
		
		/*
		 * Menu with only one function: To finish the program
		 */
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		menuItem = new JMenuItem("Exit");
		menuItem.addActionListener(new ExitItemListener());
		menu.add(menuItem);
		menuBar.add(menu);
		add(menuBar, BorderLayout.NORTH);		

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// setSize(Toolkit.getDefaultToolkit().getScreenSize());
		// hier wird das Fenster auf die Größe des Bildschirmes angepasst.
		
		setVisible(true);
		pack();
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