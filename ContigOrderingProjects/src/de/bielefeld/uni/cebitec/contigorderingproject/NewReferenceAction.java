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

import de.bielefeld.uni.cebitec.common.CustomFileFilter;
import de.bielefeld.uni.cebitec.common.MiscFileUtils;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.ProjectUtils;
import org.openide.awt.DynamicMenuContent;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChooserBuilder;
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


      File[] fastaFiles = new FileChooserBuilder("ReferenceFastaFile")
              .addFileFilter(new CustomFileFilter(".fas,.fna,.fasta", "FASTA File"))
              .setFilesOnly(true).setTitle("Select Reference Genome(s)")
              .showMultiOpenDialog();

      if (fastaFiles != null) {
        for (int i = 0; i < fastaFiles.length; i++) {
          File file = fastaFiles[i];

          //show status line
          StatusDisplayer.getDefault().setStatusText("Matching contigs on a reference genome");

          // open output window
          InputOutput io = IOProvider.getDefault().getIO("Matching of" + file.getName(), true);
//          io.select();
          io.getOut().print("Trying to match " + file.getAbsolutePath());

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
  }
}
