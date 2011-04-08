package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ContigListChangedListener implements ListSelectionListener{

	private JList list;
	private CAGWindow controller;
	private String[] data;
	private boolean selectionByUpdate;

	public ContigListChangedListener(CAGWindow cagw, String[] listData){
		this.controller = cagw;
		this.data = listData;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		selectionByUpdate = controller.selectionByUpdate;
		list =(JList) e.getSource();

		if (e.getValueIsAdjusting() == false&& !selectionByUpdate) {

			int index = 0;
			String selection = (String) list.getSelectedValue();

			for (int i = 0; i < data.length; i++) {
				if (data[i].equals(selection)) {
					index = i;
				}
			}

			controller.model.changeContigs(index, false);
			controller.chooseContigPanel.setFlag(false);

			ThreadForRightAndLeftNeigbours threadForRightNeighbours = new ThreadForRightAndLeftNeigbours(controller, false);
			threadForRightNeighbours.execute();

			ThreadForRightAndLeftNeigbours threadForLeftNeighbours =new ThreadForRightAndLeftNeigbours(controller, true);
			threadForLeftNeighbours.execute();

		}
	}

}
