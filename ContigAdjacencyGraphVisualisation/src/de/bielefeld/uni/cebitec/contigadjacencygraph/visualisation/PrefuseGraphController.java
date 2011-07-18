package de.bielefeld.uni.cebitec.contigadjacencygraph.visualisation;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import javax.swing.JLabel;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import prefuse.Display;
import prefuse.data.Graph;
import prefuse.util.ui.JSearchPanel;

public class PrefuseGraphController implements ActionListener, ComponentListener {

  private JPanel dispPanel = null;
  private JPanel ctrlPanel = null;
  private Graph graph = null;
  private JSearchPanel searchPanel = null;
  private Display graphDisplay = null;
  private Dimension d = null;
  private GRAPH_TYPE currentLayoutType = PrefuseGraphController.GRAPH_TYPE.FORCE;

  @Override
  public void componentResized(ComponentEvent e) {
    Component parent = e.getComponent();
    if (parent != null && parent.isVisible()) {
      this.setDisplayDimensions(parent.getSize());
    }
  }

  @Override
  public void componentMoved(ComponentEvent e) {
    ;
  }

  @Override
  public void componentShown(ComponentEvent e) {
    ;
  }

  @Override
  public void componentHidden(ComponentEvent e) {
    ;
  }

  public static enum GRAPH_TYPE {

    FORCE, RADIAL, FRUCHTERMAN
  };

  public PrefuseGraphController() {
    dispPanel = new JPanel();
    ctrlPanel = new JPanel();

    //fail save: create at least a graph of this size
    d = new Dimension(640, 400);
    this.setNeutral();
  }

  public void setDisplayDimensions(Dimension d) {
    if (d != null) {

      if(d.width == 0 || d.height == 0) {
        return;
      }

      this.d = d;
      if(d.width<100) {
        d.width=100;
      }
      if(d.height<100) {
        d.height = 100;
      }
      if (graphDisplay != null) {
        graphDisplay.setSize(d);
      }
    }
  }

  public JPanel getControlPanel() {
    return this.ctrlPanel;
  }

  public JPanel getDispPanel() {
    return this.dispPanel;
  }

  public void setLayoutGraph(LayoutGraph lg) {
    if (lg == null) {
      this.setNeutral();
    } else {
      graph = new ContigGraph(lg);
      ctrlPanel.removeAll();
      addControlButtons();
      dispPanel.removeAll();
      setDisplayAndSearchPanel();
    }
  }

  public void setNeutral() {
    this.dispPanel.removeAll();
    this.dispPanel.add(new JLabel("no content set"));

    this.ctrlPanel.removeAll();
    this.ctrlPanel.add(new JLabel("no content set"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    if ("fdl".equals(e.getActionCommand())) {
      setForceLayout();
    }
    if ("rl".equals(e.getActionCommand())) {
      setRadialLayout();
    }
    if ("frl".equals(e.getActionCommand())) {
      setFruchtermanReingoldLayout();
    }
    if ("exppng".equals(e.getActionCommand())) {
      exportAsPNG();
    }

  }

  private void addControlButtons() {
    JButton fdlbtn = new JButton("Force - Directed - Layout");
    fdlbtn.setBackground(new Color(255, 255, 255));

    JButton rlbtn = new JButton("Radial - Layout");
    rlbtn.setBackground(new Color(255, 255, 255));

    JButton frbtn = new JButton("Fruchterman - Reingold - Layout");
    frbtn.setBackground(new Color(255, 255, 255));

    JButton exportbtn = new JButton("Export Graph");
    exportbtn.setBackground(new Color(255, 255, 255));

    fdlbtn.setActionCommand("fdl");
    rlbtn.setActionCommand("rl");
    frbtn.setActionCommand("frl");
    exportbtn.setActionCommand("exppng");

    fdlbtn.addActionListener(this);
    rlbtn.addActionListener(this);
    frbtn.addActionListener(this);
    exportbtn.addActionListener(this);

    ctrlPanel.add(fdlbtn);
    ctrlPanel.add(rlbtn);
    ctrlPanel.add(frbtn);
    ctrlPanel.add(exportbtn);
  }

  public void setForceLayout() {
    currentLayoutType = GRAPH_TYPE.FORCE;
    setDisplayAndSearchPanel();
  }

  public void setFruchtermanReingoldLayout() {
    currentLayoutType = GRAPH_TYPE.FRUCHTERMAN;
    setDisplayAndSearchPanel();

  }

  public void setRadialLayout() {
    currentLayoutType = GRAPH_TYPE.RADIAL;
    setDisplayAndSearchPanel();
  }

  private void setDisplayAndSearchPanel() {
    if (searchPanel != null) {
      ctrlPanel.remove(searchPanel);
    }
    if (graphDisplay != null) {
      dispPanel.remove(graphDisplay);
    }

    if (currentLayoutType == GRAPH_TYPE.FORCE) {
      PrefuseForceDirectedGraph pfdg = new PrefuseForceDirectedGraph(graph, d);
      graphDisplay = pfdg.getOutputDisplay();
      graphDisplay.pan(d.width / 2., d.height / 2.);
      this.searchPanel = pfdg.getPanel();
    } else if (currentLayoutType == GRAPH_TYPE.RADIAL) {
      PrefuseRadialGraph prg = new PrefuseRadialGraph(graph, d);
      graphDisplay = prg.getOutputDisplay();
      this.searchPanel = prg.getPanel();
    } else if (currentLayoutType == GRAPH_TYPE.FRUCHTERMAN) {
      PrefuseFruchtermanReingoldGraph pfrg = new PrefuseFruchtermanReingoldGraph(graph, d);
      this.graphDisplay = pfrg.getOutputDisplay();
      this.searchPanel = pfrg.getPanel();
    }

    ctrlPanel.add(searchPanel);
    ctrlPanel.validate();
    dispPanel.add(graphDisplay);
    dispPanel.validate();
  }

  private void exportAsPNG() {
    try {
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      fc.setMultiSelectionEnabled(false);
      fc.setFileFilter(new FileFilter() {

        public boolean accept(File f) {
          return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
        }

        public String getDescription() {
          return "portable network graphics (*.png)";
        }
      });
      int selection = fc.showSaveDialog(null);

      if (selection == fc.APPROVE_OPTION) {
        FileOutputStream fout = new FileOutputStream(fc.getSelectedFile());
        graphDisplay.saveImage(fout, "PNG", 2.0);
      }
    } catch (IOException e) {
      System.out.println(e + "file not found");
    }
  }
}
