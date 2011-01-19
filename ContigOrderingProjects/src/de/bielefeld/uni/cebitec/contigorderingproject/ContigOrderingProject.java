package de.bielefeld.uni.cebitec.contigorderingproject;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.Icon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author phuseman
 */
public class ContigOrderingProject implements Project {

  private FileObject projectDir = null;
  private ProjectState state = null;
  private Lookup lkp = null;

  public ContigOrderingProject(FileObject projectDir, ProjectState state) {
    this.projectDir = projectDir;
    this.state = state;
  }

  @Override
  public FileObject getProjectDirectory() {
    return projectDir;
  }

  @Override
  public Lookup getLookup() {
    if (lkp == null) {
      lkp = Lookups.fixed(new Object[]{
                this, //project spec requires a project be in its own lookup
                state, //allow outside code to mark the project as needing saving
                new ActionProviderImpl(), //Provides standard actions like Build and Clean
                loadProperties(), //The project properties
                new ContigOrderingProjectDeleteOperation(),
                new ContigOrderingProjectInfo(), //Project information implementation
                new ContigOrderingProjectLogicalView(this), //Logical view of project implementation
              });
    }
    return lkp;
  }

  private Properties loadProperties() {
    FileObject fob = projectDir.getFileObject(ContigOrderingProjectFactory.PROJECT_FILE);
    Properties properties = new NotifyProperties(state);
    if (fob != null) {
        try {
            properties.load(fob.getInputStream());
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }
    return properties;
}

private static class NotifyProperties extends Properties {
    private final ProjectState state;
    NotifyProperties (ProjectState state) {
        this.state = state;
    }

    @Override
    public Object put(Object key, Object val) {
        Object result = super.put (key, val);
        if (((result == null) != (val == null)) || (result != null &&
            val != null && !val.equals(result))) {
            state.markModified();
        }
        return result;
    }
}

  private final class ActionProviderImpl implements ActionProvider {

    private String[] supported = new String[]{
      ActionProvider.COMMAND_DELETE,
      ActionProvider.COMMAND_COPY,};

    @Override
    public String[] getSupportedActions() {
      return supported;
    }

    @Override
    public void invokeAction(String string, Lookup lookup) throws IllegalArgumentException {
      if (string.equalsIgnoreCase(ActionProvider.COMMAND_DELETE)) {
        DefaultProjectOperations.performDefaultDeleteOperation(ContigOrderingProject.this);
      }
      if (string.equalsIgnoreCase(ActionProvider.COMMAND_COPY)) {
        DefaultProjectOperations.performDefaultCopyOperation(ContigOrderingProject.this);
      }
    }

    @Override
    public boolean isActionEnabled(String command, Lookup lookup) throws IllegalArgumentException {
      if ((command.equals(ActionProvider.COMMAND_DELETE))) {
        return true;
      } else if ((command.equals(ActionProvider.COMMAND_COPY))) {
        return true;
      } else {
        throw new IllegalArgumentException(command);
      }
    }
  }

  private final class ContigOrderingProjectDeleteOperation implements DeleteOperationImplementation {

    public void notifyDeleting() throws IOException {
    }

    public void notifyDeleted() throws IOException {
    }

    public List<FileObject> getMetadataFiles() {
      List<FileObject> dataFiles = new ArrayList<FileObject>();
      return dataFiles;
    }

    public List<FileObject> getDataFiles() {
      List<FileObject> dataFiles = new ArrayList<FileObject>();
      return dataFiles;
    }
  }

  private final class ContigOrderingProjectInfo implements ProjectInformation {

    @Override
    public Icon getIcon() {
      return null;
//      return new ImageIcon(ImageUtilities.loadImage(
//              "org/netbeans/demo/project/icon2.png"));
    }

    @Override
    public String getName() {
      return getProjectDirectory().getName();
    }

    @Override
    public String getDisplayName() {
      return getName();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
      //do nothing, won't change
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
      //do nothing, won't change
    }

    @Override
    public Project getProject() {
      return ContigOrderingProject.this;
    }
  }
}
