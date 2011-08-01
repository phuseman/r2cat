/*
 *  Copyright (C) 2011 phuseman
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.bielefeld.uni.cebitec.contigadjacencyvisualization;

import de.bielefeld.uni.cebitec.contigadjacencygraph.ContigAdjacencyGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.layouter.GreedyTreebasedLayouter;
import de.bielefeld.uni.cebitec.contigorderingproject.ContigOrderingProject;
import de.bielefeld.uni.cebitec.contigorderingproject.ContigOrderingProjectLogicalView;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.referencematches.MatchListNBApiObject;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.swing.outline.OutlineModel;
import org.openide.util.Exceptions;
import org.openide.util.LookupEvent;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.OutlineView;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import javax.swing.table.TableColumn;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.Visualizer;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.ChangeSupport;
import org.openide.windows.TopComponentGroup;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.bielefeld.uni.cebitec.contigadjacencyvisualization//ContigAdjacencyProperties//EN",
autostore = false)
public final class ContigAdjacencyPropertiesTopComponent extends TopComponent implements ExplorerManager.Provider, LookupListener {

  private static ContigAdjacencyPropertiesTopComponent instance;
  /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
  private static final String PREFERRED_ID = "ContigAdjacencyPropertiesTopComponent";
  private Lookup.Result<ContigOrderingProject> result = null;
  private final ExplorerManager explorerManager = new ExplorerManager();
  OutlineView outlineView = new OutlineView();
  private ProjectDependantCAGInformation projectAndSettings;
  //remember for each project its settings and an already computed layout
  private HashMap<ContigOrderingProject, ProjectDependantCAGInformation> projectDependantSettings = new HashMap<ContigOrderingProject, ProjectDependantCAGInformation>();
  private ChangeSupport changeSupport;

  @Override
  public ExplorerManager getExplorerManager() {
    return explorerManager;
  }

  public ContigAdjacencyPropertiesTopComponent() {
    initComponents();

    changeSupport = new ChangeSupport(this);

    setName(NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "CTL_ContigAdjacencyPropertiesTopComponent"));
    setToolTipText(NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "HINT_ContigAdjacencyPropertiesTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));



    outlineView.getOutline().setRootVisible(false);

    outlineView.setPropertyColumns(MatchListNBApiObject.PROP_SELECTEDFORCAG, "",
            MatchListNBApiObject.PROP_TREEDISTANCE, "Tree Distance");


    //remove the first columnt that contains the node itself
//    TableColumn nodeColumn = outlineView.getOutline().getColumnModel().getColumn(0);
//    outlineView.getOutline().removeColumn(nodeColumn);

    //the first column contains the nodes; switch them such that the selection is the first column
    outlineView.getOutline().moveColumn(0, 1);
    // after this set the width of the selection column
    TableColumn selectionColumn = outlineView.getOutline().getColumnModel().getColumn(0);
    selectionColumn.setMaxWidth(30);
    selectionColumn.setMinWidth(15);
    selectionColumn.setMaxWidth(25);

    references.getViewport().add(outlineView);

    //Put the Nodes into the Lookup of the TopComponent,
    //so that the Properties window will be synchronized:
    associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));


  }

  //add support to notify ChangeListeners
  void addChangeListener(ChangeListener listener) {
    changeSupport.addChangeListener(listener);
  }

  void fireChange() {
    changeSupport.fireChange();
  }

  void removeChangeListener(ChangeListener listener) {
    changeSupport.removeChangeListener(listener);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    run = new javax.swing.JButton();
    references = new javax.swing.JScrollPane();
    parametersPanel = new javax.swing.JPanel();
    sigmaLabel = new javax.swing.JLabel();
    sigmaTextfield = new javax.swing.JTextField();
    muLabel = new javax.swing.JLabel();
    muTextfield = new javax.swing.JTextField();

    setMinimumSize(new java.awt.Dimension(100, 100));
    setPreferredSize(new java.awt.Dimension(200, 400));

    org.openide.awt.Mnemonics.setLocalizedText(run, org.openide.util.NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "ContigAdjacencyPropertiesTopComponent.run.text")); // NOI18N
    run.setMinimumSize(new java.awt.Dimension(50, 25));
    run.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        runActionPerformed(evt);
      }
    });

    references.setViewportBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "ContigAdjacencyPropertiesTopComponent.references.viewportBorder.title"))); // NOI18N

    parametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "ContigAdjacencyPropertiesTopComponent.parametersPanel.border.title"))); // NOI18N
    parametersPanel.setLayout(new java.awt.GridLayout(2, 2, 5, 5));

    org.openide.awt.Mnemonics.setLocalizedText(sigmaLabel, org.openide.util.NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "ContigAdjacencyPropertiesTopComponent.sigmaLabel.text")); // NOI18N
    parametersPanel.add(sigmaLabel);

    sigmaTextfield.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    sigmaTextfield.setText(org.openide.util.NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "ContigAdjacencyPropertiesTopComponent.sigmaTextfield.text")); // NOI18N
    parametersPanel.add(sigmaTextfield);

    org.openide.awt.Mnemonics.setLocalizedText(muLabel, org.openide.util.NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "ContigAdjacencyPropertiesTopComponent.muLabel.text")); // NOI18N
    parametersPanel.add(muLabel);

    muTextfield.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    muTextfield.setText(org.openide.util.NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "ContigAdjacencyPropertiesTopComponent.muTextfield.text")); // NOI18N
    muTextfield.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        muTextfieldActionPerformed(evt);
      }
    });
    parametersPanel.add(muTextfield);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(run, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
          .addComponent(references, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
          .addComponent(parametersPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(references, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(parametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(run, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runActionPerformed
    OutlineModel tablemodel = outlineView.getOutline().getOutlineModel();

    //todo: do this in a swing worker thread
    Vector<MatchList> contigsToReferencesMatchesList = new Vector<MatchList>();

    for (int i = 0; i < tablemodel.getRowCount(); i++) {
      if (isRowSelected(i)) {
        Node node = getNode(i);
        contigsToReferencesMatchesList.add(node.getLookup().lookup(MatchListNBApiObject.class));
      }
    }

    ContigAdjacencyGraph cag = null;
    LayoutGraph layoutGraph = null;

    if (!contigsToReferencesMatchesList.isEmpty()) {
      cag = new ContigAdjacencyGraph(contigsToReferencesMatchesList);

      //set the tree weights. if these are not given, all distances are set to 1.
//			cag.setTreeWeights(contigsToReferencesTreeDistanceList);

      //fill the weight matrix with the projected contigs, based on the matches
      cag.fillWeightMatrix();

      //this method computes a path, or a Layout Graph
      layoutGraph = cag.findPath(new GreedyTreebasedLayouter());
    }

    projectAndSettings.setLayoutGraph(layoutGraph);
    fireChange();

    //open the graph, if layout graph was computed
    if (layoutGraph != null) {
      TopComponent tc = WindowManager.getDefault().findTopComponent("ContigAdjacencyGraphTopComponent");
      if (tc != null) {
        tc.requestActive();
      }
    }

  }//GEN-LAST:event_runActionPerformed

  private void muTextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muTextfieldActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_muTextfieldActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel muLabel;
  private javax.swing.JTextField muTextfield;
  private javax.swing.JPanel parametersPanel;
  private javax.swing.JScrollPane references;
  private javax.swing.JButton run;
  private javax.swing.JLabel sigmaLabel;
  private javax.swing.JTextField sigmaTextfield;
  // End of variables declaration//GEN-END:variables

  /**
   * Gets default instance. Do not use directly: reserved for *.settings files only,
   * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
   * To obtain the singleton instance, use {@link #findInstance}.
   */
  public static synchronized ContigAdjacencyPropertiesTopComponent getDefault() {
    if (instance == null) {
      instance = new ContigAdjacencyPropertiesTopComponent();
    }
    return instance;
  }

  /**
   * Obtain the ContigAdjacencyPropertiesTopComponent instance. Never call {@link #getDefault} directly!
   */
  public static synchronized ContigAdjacencyPropertiesTopComponent findInstance() {
    TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
    if (win == null) {
      Logger.getLogger(ContigAdjacencyPropertiesTopComponent.class.getName()).warning(
              "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
      return getDefault();
    }
    if (win instanceof ContigAdjacencyPropertiesTopComponent) {
      return (ContigAdjacencyPropertiesTopComponent) win;
    }
    Logger.getLogger(ContigAdjacencyPropertiesTopComponent.class.getName()).warning(
            "There seem to be multiple components with the '" + PREFERRED_ID
            + "' ID. That is a potential source of errors and unexpected behavior.");
    return getDefault();
  }

  @Override
  public int getPersistenceType() {
    return TopComponent.PERSISTENCE_ALWAYS;
  }

  @Override
  public void componentOpened() {
    result = Utilities.actionsGlobalContext().lookupResult(ContigOrderingProject.class);
    result.addLookupListener(this);
  }

  @Override
  public void componentClosed() {
    result.removeLookupListener(this);
    result = null;

    TopComponentGroup group = WindowManager.getDefault().findTopComponentGroup("ContigAdjacencyGraphGroup");
    if (group != null) {
      group.close();
    }

  }

  void writeProperties(java.util.Properties p) {
    // better to version settings since initial version as advocated at
    // http://wiki.apidesign.org/wiki/PropertyFiles
    p.setProperty("version", "1.0");
    // TODO store your settings
  }

  Object readProperties(java.util.Properties p) {
    if (instance == null) {
      instance = this;
    }
    instance.readPropertiesImpl(p);
    return instance;
  }

  private void readPropertiesImpl(java.util.Properties p) {
    String version = p.getProperty("version");
    // TODO read your settings according to their version
  }

  @Override
  protected String preferredID() {
    return PREFERRED_ID;
  }

  @Override
  public void resultChanged(LookupEvent ev) {
//    Lookup.Result r = (Lookup.Result) ev.getSource();

    ArrayList<ContigOrderingProject> projects = new ArrayList<ContigOrderingProject>(result.allInstances());


    if (projects.isEmpty()) {
      //no project in lookup; do nothing
      return;
    }


    ContigOrderingProject prj = null;
    if (projects.size() == 1) {
      prj = projects.get(0);
    } else if (projects.size() > 1) {
      //check if all projects in selection are the same.
      prj = projects.get(0);
      for (int i = 1; i < projects.size(); i++) {
        if (!prj.equals(projects.get(i))) {
          //if not, set no project
          this.setProject(null);
          return;
        }
      } // if all projects are the same, take it
    } // more than one projec tin lookup

    //if no project set, or the new project is distinct from the current one:  set it
    if (projectAndSettings == null) {
      this.setProject(prj);
    } else if (projectAndSettings.getProject() == null || !projectAndSettings.getProject().equals(prj)) {
      this.setProject(prj);
    }
  }

  private void setProject(ContigOrderingProject prj) {
    if (prj == null) {
      //empty the outline view
      explorerManager.setRootContext(Node.EMPTY);
      projectAndSettings = null;
    } else {
      projectAndSettings = projectDependantSettings.get(prj);
      if (projectAndSettings == null) {
        //create new one if not existant
        projectAndSettings = new ProjectDependantCAGInformation(prj);
        projectDependantSettings.put(prj, projectAndSettings);
      }

      //set the root for the outline view
      ContigOrderingProjectLogicalView view = new ContigOrderingProjectLogicalView(projectAndSettings.getProject());
      explorerManager.setRootContext(view.createLogicalView());
    }

    this.fireChange();
  }

  public LayoutGraph getCurrentLayoutGraph() {
    if (projectAndSettings == null) {
      return null;
    } else {
      return projectAndSettings.getLayoutGraph();
    }
  }

  private Boolean isRowSelected(int row) {

    OutlineModel tablemodel = outlineView.getOutline().getOutlineModel();

    Boolean out = null;
    //the selection column is in the model in column 1
    Object tableCell = tablemodel.getValueAt(row, 1);

    if (tableCell != null && tableCell instanceof PropertySupport.Reflection) {
      PropertySupport.Reflection reflection = (PropertySupport.Reflection) tableCell;
      try {
        out = (Boolean) reflection.getValue();
      } catch (IllegalAccessException ex) {
        Exceptions.printStackTrace(ex);
      } catch (IllegalArgumentException ex) {
        Exceptions.printStackTrace(ex);
      } catch (InvocationTargetException ex) {
        Exceptions.printStackTrace(ex);
      }
    }
    return out;
  }

  private Node getNode(int row) {
    OutlineModel tablemodel = outlineView.getOutline().getOutlineModel();

    //the node is in column 0
    Object tableCell = tablemodel.getValueAt(row, 0);
    return Visualizer.findNode(tableCell);
  }
}
