package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class ContigListPanel extends JScrollPane implements ListSelectionListener, Observer{

	private LayoutGraph graph;
	private String[] dataForList;
	private JList list;
	private CagCreator myModel;
	private boolean selectionByUpdate;

	public ContigListPanel(CagCreator model) {
		this.myModel = model;
		graph = myModel.getGraph();
		
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


	/*
	 * create the list with data
	 * and size
	 */
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


	/*
	 * If the user select a contig name will this be register by this 
	 * method
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		
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
		
		selectionByUpdate = false;
			
	}
	public JList getList() {
		return list;
	}


	/*
	 * If the user selected an another contig in the panel
	 * the selection of the list have to be changed.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		
		selectionByUpdate = true;

    if(arg instanceof DNASequence){
		DNASequence c = (DNASequence) arg;		
		list.setSelectedValue(c.getId(), true);
    }
		
	}

}
