/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.referencematches;

import de.bielefeld.uni.cebitec.dotplotviewer.DotPlotViewerTopComponent;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.openide.loaders.DataObject;
import org.openide.windows.WindowManager;

public final class OpenDotPlotViewerTopComponent implements ActionListener {

  private final DataObject context;

  public OpenDotPlotViewerTopComponent(DataObject context) {
    this.context = context;
  }

  public void actionPerformed(ActionEvent ev) {
    DotPlotViewerTopComponent viewer = (DotPlotViewerTopComponent) WindowManager.getDefault().findTopComponent("DotPlotViewerTopComponent");
    if (viewer != null) {
      MatchList ml = context.getNodeDelegate().getLookup().lookup(MatchList.class);
      if(ml!=null) {
        viewer.setMatchList(ml);
      }
      viewer.open();
      viewer.requestActive();
    }
  }
}
