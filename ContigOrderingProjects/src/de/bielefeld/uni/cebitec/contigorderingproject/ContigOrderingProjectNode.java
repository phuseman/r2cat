package de.bielefeld.uni.cebitec.contigorderingproject;

import java.awt.Image;
import javax.swing.Action;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * This is the node you actually see in the project tab for the project
 */
final class ContigOrderingProjectNode extends FilterNode {

  final ContigOrderingProject project;

  public ContigOrderingProjectNode(Node node, ContigOrderingProject project) throws DataObjectNotFoundException {
    super(node, new FilterNode.Children (node),
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
    return ImageUtilities.loadImage("de/bielefeld/uni/cebitec/contigorderingproject/recources/contigproject.png");
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
