/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.contigorderingproject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author phuseman
 */
@org.openide.util.lookup.ServiceProvider(service = ProjectFactory.class)
public class ContigOrderingProjectFactory implements ProjectFactory {

  public static final String PROJECT_FILE = ".contigproject";
  public static final String DATA_FILE_EXTENSION = "r2c";

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
    if (!projectRoot.isValid()) {
      throw new IOException("Project dir " + projectRoot.getPath()
              + " deleted,"
              + " cannot save project");
    }

    FileObject propertiesFile = projectRoot.getFileObject(PROJECT_FILE);
    if (propertiesFile == null) {
      //Recreate the Properties file if needed
      propertiesFile = projectRoot.createData(PROJECT_FILE);
    }

    Properties properties = project.getLookup().lookup(Properties.class);
    File f = FileUtil.toFile(propertiesFile);
    properties.store(new FileOutputStream(f), "Contig Ordering Project Properties");

  }
}
