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
