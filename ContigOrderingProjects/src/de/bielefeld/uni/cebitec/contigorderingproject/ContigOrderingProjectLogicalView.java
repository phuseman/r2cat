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
      Node projectdir = dataFolder.getNodeDelegate();

      //This FilterNode will be our project node
      return new ContigOrderingProjectNode(projectdir, project);

    } catch (DataObjectNotFoundException donfe) {
      Exceptions.printStackTrace(donfe);
      //Fallback-the directory couldn't be created -
      //read-only filesystem or something evil happened
      return new AbstractNode(Children.LEAF);
    }
  }

  @Override
  public Node findPath(Node root, Object target) {
    //leave unimplemented for now
    return null;
  }
}
