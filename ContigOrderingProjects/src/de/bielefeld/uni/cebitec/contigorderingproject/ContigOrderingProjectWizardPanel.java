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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.ProjectManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;

public class ContigOrderingProjectWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

  private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
  /**
   * The visual component that displays this panel. If you need to access the
   * component from this class, just use getComponent().
   */
  //this class follows the model view controller pattern. the class itself is the controller.
  private ContigOrderingProjectVisualPanel view;
  private WizardDescriptor model;
  private boolean isValid = false;

  // Get the visual component for the panel. In this template, the component
  // is kept separate. This can be more efficient: if the wizard is created
  // but never displayed, or not all panels are displayed, it is better to
  // create only those which really need to be visible.
  public ContigOrderingProjectVisualPanel getComponent() {
    if (view == null) {
      view = new ContigOrderingProjectVisualPanel();
    }
    return view;
  }

  @Override
  public HelpCtx getHelp() {
    // Show no Help button for this panel:
    return HelpCtx.DEFAULT_HELP;
    // If you have context help:
    // return new HelpCtx(SampleWizardPanel1.class);
  }

  @Override
  public boolean isValid() {
    return isValid;
  }

  @Override
  public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
      listeners.add(l);
    }
  }

  @Override
  public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
      listeners.remove(l);
    }
  }

  protected final void fireChangeEvent(Object source, boolean oldState, boolean newState) {

    if (oldState != newState) {
      Iterator<ChangeListener> it;
      synchronized (listeners) {
        it = new HashSet<ChangeListener>(listeners).iterator();
      }
      ChangeEvent ev = new ChangeEvent(source);
      while (it.hasNext()) {
        it.next().stateChanged(ev);
      }
    }
  }

  // You can use a settings object to keep track of state. Normally the
  // settings object will be the WizardDescriptor, so you can use
  // WizardDescriptor.getProperty & putProperty to store information entered
  // by the user.
  @Override
  public void readSettings(WizardDescriptor settings) {
    //this method is called when this panel is opened
    this.model = settings;
    getComponent().addPropertyChangeListener(this);
  }

  @Override
  public void storeSettings(WizardDescriptor settings) {
    model.putProperty(ContigOrderingProjectVisualPanel.PROP_PROJECT_PATH, getComponent().getProjectPath());
//    model.putProperty(ContigOrderingProjectVisualPanel.PROP_CONTIGS_FILE, getComponent().getContigsFile());
//    model.putProperty(ContigOrderingProjectVisualPanel.PROP_REFERENCES, getComponent().getReferences());
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    boolean oldState = isValid;
    isValid = checkValidity();
    fireChangeEvent(this, oldState, isValid);
  }

  private boolean checkValidity() {
    String projectName = getComponent().getProjectName();
    setMessage(null);

    if (projectName.isEmpty()) {
      setMessage("Please provide a projects name");
      return false;
    }

    if (projectName.contains(" ")) {
      setMessage("Please avoid space characters in the projects name");
    }

    File projectParentPath = new File(getComponent().getProjectParentPath());
    if (!projectParentPath.exists()) {
      setMessage("Project location does not exist");
      return false;
    }

    if (!projectParentPath.canWrite()) {
      setMessage("Project location is not writable");
      return false;
    } else if (!projectParentPath.canRead()) {
      setMessage("Project location is writable but not readable");
    }


    File project = new File(getComponent().getProjectPath());
    if (project.exists()) {
      setMessage("Thr project directory exists already.");

      if (ProjectManager.getDefault().isProject(FileUtil.toFileObject(project))) {
        setMessage("The directory already contains a project");
        return false;
      }
    }



    return true;

  }

  private void setMessage(String message) {
    model.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, message);
  }

}
