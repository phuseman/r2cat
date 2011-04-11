package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ContigListChangedListener implements ListSelectionListener{

	private JList list;
	private CagController con;
	private String[] data;
	private boolean selectionByUpdate;

	public ContigListChangedListener(CagController controller, String[] listData){
		this.con = controller;
		this.data = listData;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		selectionByUpdate = con.isSelectionByUpdate();
		list =(JList) e.getSource();

		if (e.getValueIsAdjusting() == false&& !selectionByUpdate) {

			int index = 0;
			String selection = (String) list.getSelectedValue();

			for (int i = 0; i < data.length; i++) {
				if (data[i].equals(selection)) {
					index = i;
				}
			}

			con.changeContigs(index, false);
			con.getChooseContigPanel().setFlag(false);

			ThreadForRightAndLeftNeigbours threadForRightNeighbours = new ThreadForRightAndLeftNeigbours(con, false);
			threadForRightNeighbours.execute();

			ThreadForRightAndLeftNeigbours threadForLeftNeighbours =new ThreadForRightAndLeftNeigbours(con, true);
			threadForLeftNeighbours.execute();

		}
	}

}
