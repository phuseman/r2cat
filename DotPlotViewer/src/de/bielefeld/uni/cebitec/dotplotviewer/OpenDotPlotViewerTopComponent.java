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

import de.bielefeld.uni.cebitec.referencematches.ReferenceMatchesDataObject;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.openide.loaders.DataObject;
import org.openide.windows.WindowManager;

public final class OpenDotPlotViewerTopComponent implements ActionListener {

  private final DataObject context;

  public OpenDotPlotViewerTopComponent(DataObject context) {
    this.context = context;
  }

  @Override
  public void actionPerformed(ActionEvent ev) {
    DotPlotViewerTopComponent viewer = (DotPlotViewerTopComponent) WindowManager.getDefault().findTopComponent("DotPlotViewerTopComponent");
    if (viewer != null) {
      ReferenceMatchesDataObject dataObject = context.getNodeDelegate().getLookup().lookup(ReferenceMatchesDataObject.class);
      if (dataObject != null) {
        viewer.setMatchList(dataObject.getReferenceMatches());
      }
      viewer.open();
      viewer.requestActive();

      //also open the contigs table
      ContigTableTopComponent contigs = (ContigTableTopComponent) WindowManager.getDefault().findTopComponent("ContigTableTopComponent");
      if (contigs != null) {
        contigs.setMatchList(dataObject.getReferenceMatches());
        contigs.open();
      }

    }
  }
}
