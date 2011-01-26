package de.bielefeld.uni.cebitec.contigorderingproject;

import java.awt.Image;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.Action;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
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

    this.project = project;
  }

  @Override
  public Action[] getActions(boolean arg0) {
    Action[] nodeActions = new Action[4];
    nodeActions[0] = new NewReferenceAction();
//    nodeActions[0] = CommonProjectActions.newFileAction();
    //null value results in a separator
    nodeActions[2] = CommonProjectActions.deleteProjectAction();
    nodeActions[3] = CommonProjectActions.closeProjectAction();
    //nodeActions[5] = CommonProjectActions.setAsMainProjectAction();
//    nodeActions[1] = CommonProjectActions.copyProjectAction();
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
