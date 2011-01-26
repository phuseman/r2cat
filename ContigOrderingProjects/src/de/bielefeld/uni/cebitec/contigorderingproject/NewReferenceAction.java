/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.contigorderingproject;

import de.bielefeld.uni.cebitec.common.CustomFileFilter;
import de.bielefeld.uni.cebitec.common.MiscFileUtils;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.DynamicMenuContent;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.WindowManager;

public final class NewReferenceAction extends AbstractAction implements ContextAwareAction {

  public
  @Override
  void actionPerformed(ActionEvent e) {
    assert false;
  }

  public
  @Override
  Action createContextAwareInstance(Lookup context) {
    return new ContextAction(context);
  }

  private static final class ContextAction extends AbstractAction {

    private final ContigOrderingProject p;

    public ContextAction(Lookup context) {
      p = context.lookup(ContigOrderingProject.class);

      setEnabled(p != null);
      putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);

      if (p != null) {
        String name = ProjectUtils.getInformation(p).getDisplayName();
        putValue(NAME, "&Add new reference genome for " + name);
      }
    }

    public
    @Override
    void actionPerformed(ActionEvent e) {
      File fastafile = MiscFileUtils.chooseFile(WindowManager.getDefault().getMainWindow(), "Open fasta", FileUtil.toFile(p.getProjectDirectory()), true, new CustomFileFilter(".fas,.fna,.fasta", "FASTA File"));

      // open output window
      InputOutput io = IOProvider.getDefault().getIO("TestIO", true);
      io.select();
      io.getOut().print(fastafile.getAbsoluteFile());



      //show status line
      StatusDisplayer.getDefault().setStatusText("Matching contigs on a reference genome");


      //show progress bar
      Runnable run = new Runnable() {

        @Override
        public void run() {
          ProgressHandle p = ProgressHandleFactory.createHandle("Matcher Task");
          p.start(100);
            try {
          for (int i = 0; i < 10; i++) {
              Thread.sleep(500); //do work
              p.progress("Step" + i, (i + 1) * 10);
          }
          p.finish();
            } catch (InterruptedException ex) {
              Exceptions.printStackTrace(ex);
            }
        }
      };

      Thread t = new Thread(run);
      t.start();
    }
  }
}
