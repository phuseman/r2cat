/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.contigorderingproject;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.ProjectManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

public final class ContigOrderingProjectWizardIterator implements WizardDescriptor.InstantiatingIterator {

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
                new ContigOrderingProjectWizardPanel()
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

  // If something changes dynamically (besides moving between panels), e.g.
  // the number of panels changes in response to user input, then uncomment
  // the following and call when needed: fireChangeEvent();
    /*
  private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
  public final void addChangeListener(ChangeListener l) {
  synchronized (listeners) {
  listeners.add(l);
  }
  }
  public final void removeChangeListener(ChangeListener l) {
  synchronized (listeners) {
  listeners.remove(l);
  }
  }
  protected final void fireChangeEvent() {
  Iterator<ChangeListener> it;
  synchronized (listeners) {
  it = new HashSet<ChangeListener>(listeners).iterator();
  }
  ChangeEvent ev = new ChangeEvent(this);
  while (it.hasNext()) {
  it.next().stateChanged(ev);
  }
  }
   */
  private String[] createSteps() {
    String[] steps = new String[panels.length];
    for (int i = 0; i < steps.length; i++) {
      steps[i]=panels[i].getComponent().getName();;
    }
    return steps;
  }

  @Override
  public Set instantiate() throws IOException {
    Set<FileObject> resultSet = new LinkedHashSet<FileObject>();

    boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;
    if (!cancelled) {
      String path = (String) wizard.getProperty(ContigOrderingProjectVisualPanel.PROP_PROJECT_PATH);
      File pathToNewProject = FileUtil.normalizeFile(new File(path));
      FileObject newProject = FileUtil.createFolder(pathToNewProject);

      //create the marker file that recognizes this as a project
      newProject.createData(ContigOrderingProjectFactory.PROJECT_FILE);

      Properties projectProperties = ProjectManager.getDefault().findProject(newProject).getLookup().lookup(Properties.class);
      projectProperties.put("contigs", (String) wizard.getProperty(ContigOrderingProjectVisualPanel.PROP_CONTIGS_FILE));


      //provide the created project folder
      resultSet.add(newProject);
    } else {
      return Collections.EMPTY_SET;
    }
    return resultSet;

  }
}
