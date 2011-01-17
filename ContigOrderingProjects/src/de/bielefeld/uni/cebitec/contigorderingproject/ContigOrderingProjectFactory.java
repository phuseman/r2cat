/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.contigorderingproject;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 *
 * @author phuseman
 */
@org.openide.util.lookup.ServiceProvider(service = ProjectFactory.class)
public class ContigOrderingProjectFactory implements ProjectFactory {

  public static final String PROJECT_FILE = ".contigproject";

  @Override
  public boolean isProject(FileObject projectDirectory) {
    return projectDirectory.getFileObject(PROJECT_FILE) != null;
  }

  @Override
  public Project loadProject(FileObject projectDirectory, ProjectState state) throws IOException {
    return isProject(projectDirectory) ? new ContigOrderingProject(projectDirectory, state) : null;
  }

  @Override
  public void saveProject(Project project) throws IOException, ClassCastException {
    FileObject projectRoot = project.getProjectDirectory();
    if (projectRoot.getFileObject(PROJECT_FILE) == null) {
      throw new IOException("Project dir " + projectRoot.getPath()
              + " deleted,"
              + " cannot save project");
    }
  }

}
