/***************************************************************************
 *   Copyright (C) Mar 3, 2011 by Peter Husemann                                  *
 *   phuseman  a t  cebitec.uni-bielefeld.de                               *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
package de.bielefeld.uni.cebitec.dotplotviewer;

import java.awt.Component;
import javax.swing.ProgressMonitor;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.StatusDisplayer;

/**
 * Nasty hack to use the netbeans progress monitor, when a swing progress monitor is expected.
 * @author phuseman
 */
public class NetbeansProgressMonitor extends ProgressMonitor {

  private boolean started = false;
  private String progressHandle = null;
  private ProgressHandle p = null;


  public NetbeansProgressMonitor(String statusbar, String progressHandle) {
    super(null, statusbar, progressHandle, 0, 100);
    StatusDisplayer.getDefault().setStatusText(statusbar);
    p = ProgressHandleFactory.createHandle(progressHandle);
  }
  

  @Override
  public void setProgress(int progress) {

    if (!started) {
      p.start(100);
      started = true;
    }


    p.progress(progress);
    if(progress>=100) {
      p.finish();
    }
    

  }
}
