package de.bielefeld.uni.cebitec.r2cat;

import de.bielefeld.uni.cebitec.common.MiscFileUtils;
import de.bielefeld.uni.cebitec.common.ProgressMonitorReporter;
import de.bielefeld.uni.cebitec.common.SimpleProgressReporter;
import de.bielefeld.uni.cebitec.common.Timer;
import de.bielefeld.uni.cebitec.qgram.FastaFileReader;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.qgram.QGramFilter;
import de.bielefeld.uni.cebitec.qgram.QGramIndex;
import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;

import de.bielefeld.uni.cebitec.r2cat.gui.GuiController;

/***************************************************************************
 *   Copyright (C) 2007 by Peter Husemann                                  *
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
/**
 * This is the class which is used to start r2cat.
 * 
 * @author Peter Husemann
 */
public class R2cat {

  public static R2catPrefs preferences;
  public static DataModelController dataModelController;
  public static GuiController guiController;

  /**
   * The usual main method.
   *
   * @param args
   */
  public static void main(String[] args) {
    preferences = new R2catPrefs();
    dataModelController = new DataModelController();
    guiController = new GuiController();

    clearPreferences(args);

    match(args);

    guiController.createMainWindow();
    guiController.showMainWindow();

    if (args.length >= 1 && args[0].endsWith(".r2c")) {
      File initialFile = new File(args[0]);
      try {
        if (!initialFile.exists()) {
          //try the current working directory
          initialFile = new File(System.getProperty("user.dir")
                  + args[0]);
        }

        if (initialFile.canRead()) {
          dataModelController.readMatches(initialFile);
          guiController.setVisualisationNeedsUpdate();
        }
      } catch (IOException e) {
        System.err.println("Cannot open file: " + initialFile.getName());
      }
    } else {

      // testing
      try {
        File last = new File(preferences.getLastFile());
        if (last.exists() && last.canRead()) {
          dataModelController.readMatches(new File(preferences.getLastFile()));
          guiController.setVisualisationNeedsUpdate();
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private static void clearPreferences(String[] args) {
    // option to remove all preferences
    if (args.length >= 1 && args[0].matches("clearprefs")) {
      try {
        preferences.getPreferences().clear();
      } catch (BackingStoreException e) {
        e.printStackTrace();
      }
      System.exit(0);
    }
  }

  /**
   * Allow to match on command line.
   * If the the programm is started with -match contigs.fasta reference.fasta
   * it matches the contigs onto the reference, and saves the result to
   * contigs--reference.r2c
   * While matching, log messages are printed to stdout.
   *
   * @param args
   */
  private static void match(String[] args) {
    SimpleProgressReporter progress = new SimpleProgressReporter();
    if (args.length >= 3 && args[0].matches("-match")) {
      File query = new File(args[1]);
      File target = new File(args[2]);

      MatchList matchList;

      Timer t = Timer.getInstance();
      t.startTimer();


      try { // catches io exceptions

        FastaFileReader targetFasta = new FastaFileReader(target);

        boolean targetIsFasta = targetFasta.isFastaQuickCheck();

        if (targetIsFasta) {

          progress.reportProgress(-1, "Opening target file "
                  + target.getName() + " (" + target.length()
                  + ")");
          t.startTimer();
          targetFasta.scanContents(true);
          progress.reportProgress(-1, " ..." + t.stopTimer() + "\n");
        } else {
          progress.reportProgress(-1, "Error: No valid id line (>idtag ...) found within the first 100 lines");
          System.exit(0);
        }

        FastaFileReader queryFasta = new FastaFileReader(query);
        boolean queryIsFasta = queryFasta.isFastaQuickCheck();
        if (queryIsFasta) {
          progress.reportProgress(-1, "Opening query file " + query.getName()
                  + " (" + query.length() + ")");
          t.startTimer();
          queryFasta.scanContents(true);
          progress.reportProgress(-1, " ..." + t.stopTimer() + "\n");

        } else {
          progress.reportProgress(-1, "Error: No valid id line (>idtag ...) found within the first 100 lines");
          System.exit(0);
        }


        progress.reportProgress(-1, "Generating q-Gram Index\n");
        t.startTimer();
        QGramIndex qi = new QGramIndex();
        qi.register(progress);
        qi.generateIndex(targetFasta);
        progress.reportProgress(-1, " Generating q-Gram Index took: "
                + t.stopTimer() + "\n");


        progress.reportProgress(-1, "Matching:\n");
        t.startTimer();
        QGramFilter qf = new QGramFilter(qi, queryFasta);
        qf.register(progress);
        matchList = qf.match();
        progress.reportProgress(-1, "Matching took: " + t.stopTimer() + "\n");

        progress.reportProgress(1, "Total time: " + t.stopTimer() + "\n");

        if (matchList != null
                && matchList.size() > 0
                && queryFasta.getSequences().size() > matchList.getQueries().size()) {
          //if not all contigs could be matched
          int contigs = queryFasta.getSequences().size();
          int matchedContigs = matchList.getQueries().size();

          progress.reportProgress(-1, "There were " + (contigs - matchedContigs) + " out of " + contigs + " queries that could not be matched.");

          matchList.setInitialQueryOrientation();

          File output = new File(System.getProperty("user.dir") + File.separator
                  + MiscFileUtils.getFileNameWithoutExtension(query) + "--"
                  + MiscFileUtils.getFileNameWithoutExtension(target) + ".r2c");

          matchList.writeToFile(output);

          progress.reportProgress(-1, "Wrote results to file: " + output.getCanonicalPath());

        }

      } catch (IOException e) {
        progress.reportProgress(-1, "\n" + e.getMessage() + "\n");
      }


      System.exit(0);
    }
  }

  public static R2catPrefs getPrefs() {
    return preferences;
  }
}
