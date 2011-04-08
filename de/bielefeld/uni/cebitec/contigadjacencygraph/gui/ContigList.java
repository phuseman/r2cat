package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class ContigList extends JScrollPane {

	private ContigList contigList;
	private LayoutGraph graph;
	private String[] dataForList;
	private CAGWindow controller;

	public ContigList(LayoutGraph g, CAGWindow cagw) {
		contigList = this;
		graph = g;
		this.controller = cagw;

		int i = 0;
		dataForList = new String[graph.getNodes().size()];
		for (DNASequence c : graph.getNodes()) {
			String id = c.getId();
			dataForList[i] = id;
			i++;
		}

		int width = graph.getNodes().firstElement().getId().getBytes().length;

		if (width * 20 <= 100) {
			width = 100;
		} else if (width * 20 >= 200) {
			width = 200;
		} else {
			width = width * 20;
		}

		JList list = new JList(dataForList);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ContigListChangedListener(controller,
				dataForList));
		list.setToolTipText("<html>Choose a contig<br>"
				+ " by a click on a name.</html>");


		this.setToolTipText("<html>Choose a contig<br>"
				+ " by a click on a name.</html>");
		this.setBorder(BorderFactory.createTitledBorder("Contig List"));
		this.setPreferredSize(new Dimension(width, 400));
		this
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.setAlignmentY(RIGHT_ALIGNMENT);
		this.setVisible(true);
		this.validate();
	}
}
