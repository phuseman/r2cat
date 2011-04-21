package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class CagController {

	private CagCreator cagModel;

	private JPanel contigViewPanel = null;
	private JPanel listViewPanel = null;
	private JPanel legendViewPanel = null;

	private ChooseContigPanel contigView = null;
	private ContigListPanel listView = null;
	private LegendAndInputOptionPanel legendView = null;

	public CagController() {

		this.cagModel = new CagCreator();
		contigViewPanel = new JPanel();

		this.setNeutral();

	}

	private void setNeutral() {

		if (contigViewPanel != null) {
			contigViewPanel.removeAll();
			contigViewPanel.add(new JLabel("not set yet"));
			contigView = null;
		}
		if (listViewPanel != null) {
			listViewPanel.removeAll();
			listViewPanel.add(new JLabel(" No graph! "));
			listView = null;
		}
		if (legendViewPanel != null) {
			legendViewPanel.removeAll();
			legendViewPanel.add(new JLabel(" No graph! "));
			legendView = null;
		}
	}

	public void setLayoutGraph(LayoutGraph graph) {

		if (graph == null) {
			this.setNeutral();
		} else {
			cagModel.setLayoutGraph(graph);

			contigView = new ChooseContigPanel(cagModel);
			contigView.createPanel();
			
			if (contigViewPanel != null) {
				contigViewPanel.removeAll();
				contigViewPanel.add(contigView);
			}
			
			listView = new ContigListPanel(cagModel);
			listView.createList();
			
			if (listViewPanel != null) {
				listViewPanel.removeAll();
				listViewPanel.add(listView);
			}
			
			legendView = new LegendAndInputOptionPanel(cagModel);
			legendView.createLegendAndInputOption();
			
			if (legendViewPanel != null) {
				legendViewPanel.removeAll();
				legendViewPanel.add(legendView);
			}
		}
	}

	public void showContig(DNASequence c) {
		int index = cagModel.getGraph().getNodes().indexOf(c);
		cagModel.changeContigs(index, false);
	}

	public JPanel getContigView() {
		return contigViewPanel;
	}

	public JPanel getLegendView() {
		return legendViewPanel;
	}

	public JPanel getListView() {
		return listViewPanel;
	}

}
