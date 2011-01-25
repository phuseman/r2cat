/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.dotplotviewer;

import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.r2cat.gui.DotPlotMatchViewer;
import de.bielefeld.uni.cebitec.r2cat.gui.MatchViewerPlugin;
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

  private DotPlotMatchViewer dpview;

  private static DotPlotViewerTopComponent instance;
  /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
  private static final String PREFERRED_ID = "DotPlotViewerTopComponent";
  private DotPlotMatchViewer dotplotMatchViewer;

  public DotPlotViewerTopComponent() {
    initComponents();
    dotplotMatchViewer=new DotPlotMatchViewer();
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

    setLayout(new java.awt.BorderLayout());
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
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
    return TopComponent.PERSISTENCE_NEVER;
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


    if (c.size() == 1) {
    for (Iterator it = c.iterator(); it.hasNext();) {
      MatchList matchList = (MatchList) it.next();
      this.dotplotMatchViewer.setAlignmentsPositionsList(matchList);
    }

    }

      if (dotplotMatchViewer.getMatchDisplayerList() != null) {
          this.add(dotplotMatchViewer);
      } else {
        this.remove(dotplotMatchViewer);
      }

  }
}
