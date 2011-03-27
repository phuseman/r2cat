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

import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author phuseman
 */
public class ContigOrderingProjectLogicalView implements LogicalViewProvider {

  private final ContigOrderingProject project;

  public ContigOrderingProjectLogicalView(ContigOrderingProject project) {
    this.project = project;
  }

  @Override
  public Node createLogicalView() {
    try {
      //Get the DataObject that represents it
      FileObject folder = project.getProjectDirectory();
      DataFolder dataFolder = DataFolder.findFolder(folder);

      return new ContigOrderingProjectNode(dataFolder, project);

    } catch (DataObjectNotFoundException donfe) {
      Exceptions.printStackTrace(donfe);
      //Fallback-the directory couldn't be created -
      //read-only filesystem or something evil happened
      return new AbstractNode(Children.LEAF);
    }
  }

  @Override
  public Node findPath(Node root, Object target) {
    //TODO: implement find path
    return null;
  }
}
