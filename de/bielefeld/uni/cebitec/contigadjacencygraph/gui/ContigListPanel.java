package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class ContigListPanel extends JScrollPane implements ListSelectionListener{

	private ContigListPanel contigList;
	private LayoutGraph graph;
	private String[] dataForList;
	private CagController controller;
	private JList list;
	private CagCreator myModel;

	public ContigListPanel(CagController controller, CagCreator model) {
		contigList = this;
		graph = model.getGraph();
		this.controller = controller;
		this.myModel = model;
		
		this.setToolTipText("<html>Choose a contig<br>"
				+ " by a click on a name.</html>");
		this.setBorder(BorderFactory.createTitledBorder("Contig List"));
		this.setPreferredSize(new Dimension(100, 400));
		this.setVerticalScrollBarPolicy
			(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setHorizontalScrollBarPolicy
			(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setAlignmentY(RIGHT_ALIGNMENT);
		this.setVisible(true);
		this.validate();
	}
	
	

	public void createList(){
		int i = 0;
		dataForList = new String[graph.getNodes().size()];
		for (DNASequence c : graph.getNodes()) {
			String id = c.getId();
			dataForList[i] = id;
			i++;
		}
		
		list = new JList(dataForList);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		list.setToolTipText("<html>Choose a contig<br>"+
				" by a click on a name.</html>");

		this.getViewport().setView(list);
		int width = graph.getNodes().firstElement().getId().getBytes().length;

		if (width * 20 <= 100) {
			width = 100;
		} else if (width * 20 >= 200) {
			width = 200;
		} else {
			width = width * 20;
		}
		this.setPreferredSize(new Dimension(width, 400));
		
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		

		boolean selectionByUpdate = controller.isSelectionByUpdate();
		
		list =(JList) e.getSource();
		
		if (e.getValueIsAdjusting() == false&& !selectionByUpdate) {

			int index = 0;
			String selection = (String) list.getSelectedValue();

			for (int i = 0; i < dataForList.length; i++) {
				if (dataForList[i].equals(selection)) {
					index = i;
				}
			}

			/*
			 * Eigentlich sollte hier eine Methode des Controllers aufgerufen werden,
			 * wenn das MVC Pattern sehr konsequent umgesetzt werden soll.
			 * Habe mich aber entschieden auf direktem weg mit dem model 
			 * zu komunizieren. Code wird lesbarer, Controller nicht ueberladen
			 */
			myModel.changeContigs(index, false);

		}
			
	}
	public JList getList() {
		return list;
	}

}
