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

import de.bielefeld.uni.cebitec.common.MiscFileUtils;
import de.bielefeld.uni.cebitec.matchingtask.CombinedNetbeansProgressReporter;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.ProjectManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import de.bielefeld.uni.cebitec.matchingtask.MatchingTask;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import org.openide.util.Exceptions;

public final class ContigOrderingProjectWizardIterator implements WizardDescriptor.ProgressInstantiatingIterator {

  private int index;
  private WizardDescriptor wizard;
  private WizardDescriptor.Panel[] panels;

  /**
   * Initialize panels representing individual wizard's steps and sets
   * various properties for them influencing wizard appearance.
   */
  private WizardDescriptor.Panel[] getPanels() {

    if (panels == null) {
      panels = new WizardDescriptor.Panel[]{
                new ContigOrderingProjectWizardPanel(),
                new ContigOrderingProjectWizardPanel2()
              };
      String[] steps = createSteps();
      for (int i = 0; i < panels.length; i++) {
        Component c = panels[i].getComponent();
        if (steps[i] == null) {
          // Default step name to component name of panel. Mainly
          // useful for getting the name of the target chooser to
          // appear in the list of steps.
          steps[i] = c.getName();
        }
        if (c instanceof JComponent) { // assume Swing components
          JComponent jc = (JComponent) c;
          // Sets step number of a component
          // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
          jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
          // Sets steps names for a panel
          jc.putClientProperty("WizardPanel_contentData", steps);
          // Turn on subtitle creation on each step
          jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
          // Show steps on the left side with the image on the background
          jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
          // Turn on numbering of all steps
          jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
        }
      }
    }
    return panels;
  }

  @Override
  public void initialize(WizardDescriptor wizard) {
    this.wizard = wizard;
  }

  @Override
  public void uninitialize(WizardDescriptor wizard) {
    panels = null;
  }

  @Override
  public WizardDescriptor.Panel current() {
    return getPanels()[index];
  }

  @Override
  public String name() {
    return index + 1 + ". from " + getPanels().length;
  }

  @Override
  public boolean hasNext() {
    return index < getPanels().length - 1;
  }

  @Override
  public boolean hasPrevious() {
    return index > 0;
  }

  @Override
  public void nextPanel() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    index++;
  }

  @Override
  public void previousPanel() {
    if (!hasPrevious()) {
      throw new NoSuchElementException();
    }
    index--;
  }

  // If nothing unusual changes in the middle of the wizard, simply:
  public void addChangeListener(ChangeListener l) {
  }

  public void removeChangeListener(ChangeListener l) {
  }

  private String[] createSteps() {
    String[] steps = new String[panels.length];
    for (int i = 0; i < steps.length; i++) {
      steps[i] = panels[i].getComponent().getName();
      ;
    }
    return steps;
  }

  @Override
  public Set instantiate(ProgressHandle handle) throws IOException {
    return createProjectAndMatchContigs(handle);
  }

  @Override
  public Set instantiate() throws IOException {
    return createProjectAndMatchContigs(null);
  }

  public Set createProjectAndMatchContigs(ProgressHandle handle) throws IOException {
    Set<FileObject> resultSet = new LinkedHashSet<FileObject>();

    boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;
    if (!cancelled) {
      String path = (String) wizard.getProperty(ContigOrderingProjectVisualPanel.PROP_PROJECT_PATH);
      File pathToNewProject = FileUtil.normalizeFile(new File(path));
      FileObject newProject = FileUtil.createFolder(pathToNewProject);

      //create the marker file that recognizes this as a project
      newProject.createData(ContigOrderingProjectFactory.PROJECT_FILE);

      Properties projectProperties = ProjectManager.getDefault().findProject(newProject).getLookup().lookup(Properties.class);

      File contigs = new File((String) wizard.getProperty(ContigOrderingProjectVisualPanel2.PROP_CONTIGS_FILE));
      projectProperties.put("contigs", FileUtil.normalizeFile(contigs).getPath());


      //provide the created project folder
      resultSet.add(newProject);


      String[] references = (String[]) wizard.getProperty(ContigOrderingProjectVisualPanel2.PROP_REFERENCES);


      if (references != null) {
        handle.start(references.length);

        for (int i = 0; i < references.length; i++) {
          File reference = new File(references[i]);
          String referenceString = MiscFileUtils.getFileNameWithoutExtension(reference);

          MatchingTask matcher = new MatchingTask(contigs, reference);

          CombinedNetbeansProgressReporter progress = new CombinedNetbeansProgressReporter(
                  "Matching Contigs:" + MiscFileUtils.getFileNameWithoutExtension(contigs),
                  "Matching on " + referenceString,
                  referenceString);
          progress.useAdditionalProgressMonitor(current().getComponent());

          matcher.setProgressReporter(progress);
          matcher.execute();
          try {
            handle.progress("Matching " + referenceString, i);
            MatchList matches = matcher.get();

            //write matches to file
            if (matches != null && !matches.isEmpty()) {
              FileObject matchFile = newProject.createData(referenceString, "r2c");
              matches.writeToFile(FileUtil.toFile(matchFile));
            }

            //write log to file
            FileObject logfile = newProject.createData(referenceString, "log");
            progress.writeCommentsToFile(FileUtil.toFile(logfile));

          } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
          } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
          }

        }
        handle.finish();
      }



    } else {
      return Collections.EMPTY_SET;
    }
    return resultSet;
  }
}
