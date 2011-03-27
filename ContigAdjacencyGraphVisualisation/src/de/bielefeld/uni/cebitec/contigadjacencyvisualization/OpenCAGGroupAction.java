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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.netbeans.api.project.Project;
import org.openide.windows.TopComponentGroup;
import org.openide.windows.WindowManager;

public final class OpenCAGGroupAction implements ActionListener {

  private final Project context;

  public OpenCAGGroupAction(Project context) {
    this.context = context;
  }

  //see http://blogs.sun.com/geertjan/entry/creating_a_window_group
  public void actionPerformed(ActionEvent ev) {
    TopComponentGroup group = WindowManager.getDefault().findTopComponentGroup("ContigAdjacencyGraphGroup");
    if (group != null) {
      group.open();
    }
  }
}
