/*
 *  Copyright (C) 2011 Peter Husemann <peter.husemann at cebitec uni bielefeld.de>
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
package de.bielefeld.uni.cebitec.dotplotviewer;

import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.r2cat.gui.DotPlotMatchViewer;
import de.bielefeld.uni.cebitec.r2cat.gui.DotPlotMatchViewerActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;
import org.openide.util.LookupEvent;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.bielefeld.uni.cebitec.dotplotviewer//DotPlotViewer//EN",
autostore = false)
public final class DotPlotViewerTopComponent extends TopComponent implements LookupListener {

  private static DotPlotViewerTopComponent instance;
  /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
  private static final String PREFERRED_ID = "DotPlotViewerTopComponent";
  private DotPlotMatchViewer dotplotMatchViewer;

  public DotPlotViewerTopComponent() {
    initComponents();
    dotplotMatchViewer = new DotPlotMatchViewer();
    		DotPlotMatchViewerActionListener dotPlotVisualisationListener = new DotPlotMatchViewerActionListener(
				null, dotplotMatchViewer);

		dotplotMatchViewer
				.addMouseMotionListener(dotPlotVisualisationListener);
		dotplotMatchViewer.addMouseListener(dotPlotVisualisationListener);
		dotplotMatchViewer
				.addMouseWheelListener(dotPlotVisualisationListener);
		dotplotMatchViewer.addKeyListener(dotPlotVisualisationListener);

		// load the previous state from prefs
		dotplotMatchViewer.drawGrid(true);

    //this way, the visualisation can react to window size changes
    this.addComponentListener(dotplotMatchViewer);



    setName(NbBundle.getMessage(DotPlotViewerTopComponent.class, "CTL_DotPlotViewerTopComponent"));
    setToolTipText(NbBundle.getMessage(DotPlotViewerTopComponent.class, "HINT_DotPlotViewerTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();

    setLayout(new java.awt.BorderLayout());
    add(jScrollPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  // End of variables declaration//GEN-END:variables

  /**
   * Gets default instance. Do not use directly: reserved for *.settings files only,
   * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
   * To obtain the singleton instance, use {@link #findInstance}.
   */
  public static synchronized DotPlotViewerTopComponent getDefault() {
    if (instance == null) {
      instance = new DotPlotViewerTopComponent();
    }
    return instance;
  }

  /**
   * Obtain the DotPlotViewerTopComponent instance. Never call {@link #getDefault} directly!
   */
  public static synchronized DotPlotViewerTopComponent findInstance() {
    TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
    if (win == null) {
      Logger.getLogger(DotPlotViewerTopComponent.class.getName()).warning(
              "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
      return getDefault();
    }
    if (win instanceof DotPlotViewerTopComponent) {
      return (DotPlotViewerTopComponent) win;
    }
    Logger.getLogger(DotPlotViewerTopComponent.class.getName()).warning(
            "There seem to be multiple components with the '" + PREFERRED_ID
            + "' ID. That is a potential source of errors and unexpected behavior.");
    return getDefault();
  }

  @Override
  public int getPersistenceType() {
    return TopComponent.PERSISTENCE_ONLY_OPENED;
  }
  private Lookup.Result result = null;

  @Override
  public void componentOpened() {
    result = Utilities.actionsGlobalContext().lookupResult(MatchList.class);
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
        MatchList matchList = (MatchList) it.next();
        this.setMatchList(matchList);
      }
    } else if (c.size() > 1) {
      this.setMatchList(null);
    }

  }
//		drawing.setViewportView(matchViewerPlugin);
//		drawing.setVisible(true);
//		drawing.validate();

  public void setMatchList(MatchList ml) {
    if (ml != null && !ml.isEmpty()) {
      this.dotplotMatchViewer.setAlignmentsPositionsList(ml);
//TODO: remember and set this properly
      		dotplotMatchViewer.getMatchDisplayerList()
				.showReversedComplements(true);

          //TODO: remember and set this properly
		dotplotMatchViewer.getMatchDisplayerList()
				.setDisplayOffsets(true);

      dotplotMatchViewer.getMatchDisplayerList().setNeedsRegeneration(true);
      this.jScrollPane1.setViewportView(dotplotMatchViewer);
      this.jScrollPane1.setVisible(true);
    } else {
      this.jScrollPane1.getViewport().removeAll();
      this.jScrollPane1.setVisible(false);
    }

    this.invalidate();
    this.repaint();
  }
}