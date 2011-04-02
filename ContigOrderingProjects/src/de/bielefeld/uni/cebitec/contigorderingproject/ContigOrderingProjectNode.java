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
package de.bielefeld.uni.cebitec.contigorderingproject;

import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * This is the node you actually see in the project tab for the project
 */
public final class ContigOrderingProjectNode extends FilterNode {

  final ContigOrderingProject project;

  public static class MatchListFilter implements DataFilter {

    @Override
    public boolean acceptDataObject(DataObject obj) {
      if (obj.getPrimaryFile().getExt().equals(ContigOrderingProjectFactory.DATA_FILE_EXTENSION)) {
        return true;
      }
      return false;
    }
  }

  public ContigOrderingProjectNode(DataFolder dataFolder, ContigOrderingProject project) throws DataObjectNotFoundException {
    super(dataFolder.getNodeDelegate(),
            dataFolder.createNodeChildren(new MatchListFilter()),
            //The projects system wants the project in the Node's lookup.
            //NewAction and friends want the original Node's lookup.
            //Make a merge of both
            new ProxyLookup(new Lookup[]{Lookups.singleton(project),
              dataFolder.getNodeDelegate().getLookup()
            }));
    //todo: Is there a way to include the project into the lookup of the children?
    this.project = project;
  }

  @Override
  public Action[] getActions(boolean context) {
    //load all actions that are registered in layer.xml in this path
    List<? extends Action> contigOrderingProjectActions =
            Utilities.actionsForPath("Actions/ContigProject");
    return contigOrderingProjectActions.toArray(new Action[contigOrderingProjectActions.size()]);
  }

  @Override
  public Image getIcon(int type) {
    return ImageUtilities.loadImage("de/bielefeld/uni/cebitec/contigorderingproject/recources/contigproject.png");
  }

  @Override
  public Image getOpenedIcon(int type) {
    return getIcon(type);
  }

  @Override
  public String getDisplayName() {
    return project.getProjectDirectory().getNameExt();
  }
}
