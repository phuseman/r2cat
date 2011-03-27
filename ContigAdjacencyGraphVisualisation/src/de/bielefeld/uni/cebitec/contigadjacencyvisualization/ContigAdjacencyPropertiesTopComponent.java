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

import de.bielefeld.uni.cebitec.contigorderingproject.ContigOrderingProject;
import de.bielefeld.uni.cebitec.contigorderingproject.ContigOrderingProjectLogicalView;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;
import org.openide.util.LookupEvent;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.OutlineView;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import javax.swing.table.TableColumn;

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
  private Lookup.Result result = null;
  private final ExplorerManager explorerManager = new ExplorerManager();
  OutlineView outlineView = new OutlineView();

  @Override
  public ExplorerManager getExplorerManager() {
    return explorerManager;
  }

  public ContigAdjacencyPropertiesTopComponent() {
    initComponents();
    setName(NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "CTL_ContigAdjacencyPropertiesTopComponent"));
    setToolTipText(NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "HINT_ContigAdjacencyPropertiesTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));



    outlineView.getOutline().setRootVisible(false);

    outlineView.setPropertyColumns(
            "cagCreation", "Use");


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

    matchLists.getViewport().add(outlineView);

  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    run = new javax.swing.JButton();
    matchLists = new javax.swing.JScrollPane();

    org.openide.awt.Mnemonics.setLocalizedText(run, org.openide.util.NbBundle.getMessage(ContigAdjacencyPropertiesTopComponent.class, "ContigAdjacencyPropertiesTopComponent.run.text")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(run, javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(matchLists, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(matchLists, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
        .addComponent(run)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane matchLists;
  private javax.swing.JButton run;
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
    return TopComponent.PERSISTENCE_ONLY_OPENED;
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
    Lookup.Result r = (Lookup.Result) ev.getSource();
    //jTextArea1.setText("");
    Collection c = r.allInstances();


    if (c.size() < 1) {
      //no match object in lookup; do nothing
      return;
    }
    if (c.size() == 1) {
      for (Iterator it = c.iterator(); it.hasNext();) {
        ContigOrderingProject prj = (ContigOrderingProject) it.next();
        this.setProject(prj);
      }
    } else if (c.size() > 1) {
      this.setProject(null);
    }

  }

  private void setProject(ContigOrderingProject prj) {
    if (prj != null) {
      ContigOrderingProjectLogicalView view = new ContigOrderingProjectLogicalView(prj);
      explorerManager.setRootContext(view.createLogicalView());
    }
  }
}
