package de.bielefeld.uni.cebitec.contigorderingproject;

import java.awt.Image;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Action;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
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
    public org.openide.nodes.Node createLogicalView() {
     try {
            //Get the DataObject that represents it
            FileObject folder = project.getProjectDirectory();
            DataFolder dataFolder = DataFolder.findFolder(folder);
            Node projectdir = dataFolder.getNodeDelegate();

            //This FilterNode will be our project node
            return new ReferenceGenomeNode(projectdir, project);

        } catch (DataObjectNotFoundException donfe) {
            Exceptions.printStackTrace(donfe);
            //Fallback-the directory couldn't be created -
            //read-only filesystem or something evil happened
            return new AbstractNode(Children.LEAF);
        }
    }

  /** This is the node you actually see in the project tab for the project */
  private static final class ReferenceGenomeNode extends FilterNode {

    final ContigOrderingProject project;

    public ReferenceGenomeNode(Node node, ContigOrderingProject project) throws DataObjectNotFoundException {
      super(node, new FilterNode.Children(node),
              //The projects system wants the project in the Node's lookup.
              //NewAction and friends want the original Node's lookup.
              //Make a merge of both
              new ProxyLookup(new Lookup[]{Lookups.singleton(project),
                node.getLookup()
              }));
      this.project = project;
    }

    @Override
    public Action[] getActions(boolean arg0) {
      Action[] nodeActions = new Action[7];
      nodeActions[0] = CommonProjectActions.newFileAction();
//            nodeActions[1] = CommonProjectActions.copyProjectAction();
      nodeActions[2] = CommonProjectActions.deleteProjectAction();
//            nodeActions[5] = CommonProjectActions.setAsMainProjectAction();
      nodeActions[6] = CommonProjectActions.closeProjectAction();
      return nodeActions;
    }

    @Override
    public Image getIcon(int type) {
      return ImageUtilities.loadImage("org/netbeans/demo/project/icon1.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
      return getIcon(type);
    }

    @Override
    public String getDisplayName() {
      return project.getProjectDirectory().getName();
    }
  }

  @Override
  public Node findPath(Node root, Object target) {
    //leave unimplemented for now
    return null;
  }
}
