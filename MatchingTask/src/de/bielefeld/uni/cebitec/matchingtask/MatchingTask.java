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
import de.bielefeld.uni.cebitec.common.MiscFileUtils;
import de.bielefeld.uni.cebitec.common.Timer;
import de.bielefeld.uni.cebitec.qgram.DNASequence;
import de.bielefeld.uni.cebitec.qgram.FastaFileReader;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.qgram.QGramFilter;
import de.bielefeld.uni.cebitec.qgram.QGramIndex;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author phuseman
 */
public class MatchingTask extends SwingWorker<MatchList, String> implements AbstractProgressReporter {

  private AbstractProgressReporter progress = null;
  final private File reference;
  final private File contigs;
  private MatchList result = null;

  public MatchingTask(String contigs, String reference) {
    this.contigs = new File(contigs);
    this.reference = new File(reference);
    progress = this;
  }

  public MatchingTask(File contigs, File reference) {
    this.contigs = contigs;
    this.reference = reference;
    progress = this;
  }

  public void setProgressReporter(AbstractProgressReporter progress) {
    this.progress = progress;
  }

  public AbstractProgressReporter getProgressReporter() {
    return progress;
  }

  @Override
  public void reportProgress(double percentDone, String comment) {
    if (percentDone >= 0 && percentDone <= 1) {
      setProgress((int) (percentDone * 100.));
    }
    if (comment != null && !comment.isEmpty()) {
      publish(comment);
    }
  }

  public File getContigs() {
    return contigs;
  }

  public File getReference() {
    return reference;
  }

  public String getReferenceFilenameWithoutExtension() {
    return MiscFileUtils.getFileNameWithoutExtension(reference);
  }

  @Override
  protected MatchList doInBackground() {
    try { // catches OutOfMemoryError s

      Timer t = Timer.getInstance();
      t.startTimer();

      try { // catches io exceptions

        FastaFileReader targetFasta = new FastaFileReader(reference);

        boolean targetIsFasta = targetFasta.isFastaQuickCheck();

        if (targetIsFasta) {
          progress.reportProgress(-1, "Opening target file "
                  + reference.getName() + " (" + reference.length()
                  + ")");
          t.startTimer();
          targetFasta.scanContents(true);
          progress.reportProgress(-1, " ..." + t.stopTimer() + "\n");
        } else {
          progress.reportProgress(-1, "Error: No valid id line (>idtag ...) found within the first 100 lines");
          return null;
        }

        FastaFileReader queryFasta = new FastaFileReader(contigs);
        boolean queryIsFasta = queryFasta.isFastaQuickCheck();
        if (queryIsFasta) {
          progress.reportProgress(-1, "Opening query file" + contigs.getName()
                  + " (" + contigs.length() + ")");
          t.startTimer();
          queryFasta.scanContents(true);
          progress.reportProgress(-1, " ..." + t.stopTimer() + "\n");

        } else {
          progress.reportProgress(-1, "Error: No valid id line (>idtag ...) found within the first 100 lines");
          return null;
        }

        // check if targets and queries might be switched
        double averageTargetSize = 0;
        for (DNASequence seq : targetFasta.getSequences()) {
          averageTargetSize += seq.getSize();
        }
        averageTargetSize /= targetFasta.getSequences().size();

        double averageQuerySize = 0;
        for (DNASequence seq : queryFasta.getSequences()) {
          averageQuerySize += seq.getSize();
        }
        averageQuerySize /= queryFasta.getSequences().size();

        String warningMsg = "";
        if (averageQuerySize > averageTargetSize) {
          warningMsg = "The queries are bigger than the target sequences on average.";
        } else if (targetFasta.getSequences().size() > queryFasta.getSequences().size()) {
          warningMsg = "There are more targets than queries.";
        }

        // if there is an error, display dialog
        if (!warningMsg.equals("")) {
          int warningAnswer = -1;
          warningMsg += "\nThis can lead to problems during the matching phase\n"
                  + "and in the visualization.";
          Object[] options = {"I know, continue!",
            "Switch sequences"};
          warningAnswer = JOptionPane.showOptionDialog(
                  null, warningMsg,
                  "Target and queries switched?",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.WARNING_MESSAGE, null, options,
                  options[1]);
          if (warningAnswer == 1) {
            // switch query <-> target
            FastaFileReader tmp;
            tmp = queryFasta;
            queryFasta = targetFasta;
            targetFasta = tmp;
          }// else just continue

        }

        progress.reportProgress(-1, "Generating q-Gram Index\n");
        t.startTimer();
        QGramIndex qi = new QGramIndex();
        //register progress reporter
        qi.register(progress);
        qi.generateIndex(targetFasta);
        progress.reportProgress(-1, " Generating q-Gram Index took: "
                + t.stopTimer() + "\n");

        System.gc();

        progress.reportProgress(-1, "Matching:\n");
        t.startTimer();
        QGramFilter qf = new QGramFilter(qi, queryFasta);
        qf.register(progress);
        result = qf.match();
        progress.reportProgress(-1, "Matching took: " + t.stopTimer() + "\n");

        progress.reportProgress(-1, "Total time: " + t.stopTimer() + "\n");

        if (result != null
                && result.size() > 0
                && queryFasta.getSequences().size() > result.getQueries().size()) {
          //if not all contigs could be matched
          int contigs = queryFasta.getSequences().size();
          int matchedContigs = result.getQueries().size();

          progress.reportProgress(-1, "There were " + (contigs - matchedContigs) + " out of " + contigs + " queries that could not be matched.");

        }

        progress.reportProgress(1, "Done!\n");

      } catch (IOException e) {
        progress.reportProgress(-1, "\n" + e.getMessage() + "\n");
      }


      if (!result.isEmpty()) {
        result.setInitialQueryOrientation();
      }

      return result;

      // Catch if the memory is exhausted. If so display a message and
      // return
    } catch (OutOfMemoryError e) {
      int heapmem = (int) Math.ceil((Runtime.getRuntime().maxMemory() / (1024. * 1024.)));

      progress.reportProgress(-1, "Sorry, the maximal heap memory (currently ~" + heapmem + "MB) was\n"
              + "exhausted. Probably the reference genome was too big.\n"
              + "Try to increase the heap size, e.g. to " + (2 * heapmem) + "MB, "
              + "by using the Java option '-Xmx" + (2 * heapmem) + "m'.");

      progress.reportProgress(-1, e.toString());

      return null;
    }
  }


  /* (non-Javadoc)
   * @see javax.swing.SwingWorker#done()
   */
  public void done() {
    //if the thread was not cancelled get and check the result.
    if (!isCancelled()) {
      try {
        result = this.get();
        if (result != null) {
          if (result.size() == 0) {
            progress.reportProgress(-1, "Sorry, no matches were found.\n"
                    + "One reason could be that the sequences are too small (<500 bases)\n"
                    + "or maybe they are not similar enough.");
          }
        } else {
          progress.reportProgress(-1, "An error happened, no results have been created.");
        }
      } catch (InterruptedException e) {
        //ignore
        ;
      } catch (ExecutionException e) {
        //ignore
        ;
      }
    }
  }
}
