package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

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
    contigView = new ChooseContigPanel(cagModel);

    listViewPanel = new JPanel();
    listView = new ContigListPanel(cagModel);

    legendViewPanel = new JPanel();
    legendView = new LegendAndInputOptionPanel(cagModel);

    this.setNeutral();

  }



private void setNeutral() {

    contigViewPanel.removeAll();
    contigViewPanel.add(new JLabel("not set yet"));
    contigView = null;

    listViewPanel.removeAll();
    listViewPanel.add(new JLabel(" No graph! "));
    listView = null;


    legendViewPanel.removeAll();
    legendViewPanel.add(new JLabel(" No graph! "));
    legendView = null;

    //remove the observers that were set in the constructors of the two panels.
    cagModel.deleteObservers();

  }

  public void setLayoutGraph(LayoutGraph graph) {

    if (graph == null) {
      this.setNeutral();
    } else {
      cagModel.setLayoutGraph(graph);

      contigView = new ChooseContigPanel(cagModel);
      contigView.createPanel();
      contigViewPanel.removeAll();
      contigViewPanel.add(contigView);

      listView = new ContigListPanel(cagModel);
      listView.createList();
      listViewPanel.removeAll();
      listViewPanel.add(listView);

      legendView = new LegendAndInputOptionPanel(cagModel);
      legendView.createLegendAndInputOption();
      legendViewPanel.removeAll();
      legendViewPanel.add(legendView);

      cagModel.addObserver(contigView);
      cagModel.addObserver(listView);

      //initially select the first contig
      cagModel.changeContigs(0, false);
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
