/***************************************************************************
 *   Copyright (C) Mar 3, 2011 by Peter Husemann                                  *
 *   phuseman  a t  cebitec.uni-bielefeld.de                               *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
package de.bielefeld.uni.cebitec.matchingtask;

import de.bielefeld.uni.cebitec.common.AbstractProgressReporter;
import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.ProgressMonitor;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author phuseman
 */
public class CombinedNetbeansProgressReporter implements AbstractProgressReporter {

  private boolean started = false;

  	private StringBuilder log = null;


private ProgressMonitor additionalProgressMonitor = null;

  public void useAdditionalProgressMonitor(Component c) {
		additionalProgressMonitor = new ProgressMonitor(c, "Matching", progressHandle, 0, 100);
		additionalProgressMonitor.setMillisToDecideToPopup(10);
		additionalProgressMonitor.setMillisToPopup(500);

  }


  private String progressHandle = null;

  private InputOutput io = null;
  private ProgressHandle p = null;

  public CombinedNetbeansProgressReporter(String statusbar, String progressHandle, String outputWindowTitle) {
    StatusDisplayer.getDefault().setStatusText(statusbar);
    p = ProgressHandleFactory.createHandle(progressHandle);
    io = IOProvider.getDefault().getIO(outputWindowTitle, true);
 		log = new StringBuilder();

  }

  @Override
  public void reportProgress(double percentDone, String comment) {
    
    if(!started) {
      p.start(100);
      io.select();
      started=true;
    }
    
    if (percentDone >= 0 && percentDone <= 1) {
      p.progress(comment, (int) (percentDone * 100.));
      if(additionalProgressMonitor!=null) {
        additionalProgressMonitor.setProgress((int) (percentDone*100));
      }
    }
    if (comment != null && !comment.isEmpty()) {
      io.getOut().print(comment+"\n");
 			log.append(comment+"\n");
    }
    
    if (percentDone == 1) {
      p.finish();
      io.getOut().flush();
      io.getOut().close();
    }

  }

  	/**
	 * Writes all cached progress comments to a file.
	 * @param output
	 * @throws IOException
	 */
	public void writeCommentsToFile(File output) throws IOException {
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(output));
		fileWriter.write(log.toString());
		fileWriter.close();
	}


}
