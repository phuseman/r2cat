/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.r2cat;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.qgram.MatchStatistics;


/**
 * @author phuseman
 * 
 */
public class DataModelController {

	private MatchList matchList;

	public MatchList parseMatchesFromCSVFile(File csvFile) {
		CSVParser csvParser = new CSVParser(csvFile);
		csvParser.setSwiftMode();
		MatchList matchList = csvParser.parse();
		return matchList;
	}

	public void setMatchesFromCSVFile(File csvFile) {
		this.setAlignmentsPositonsList(this
				.parseMatchesFromCSVFile(csvFile));
	}

	public void setAlignmentsPositonsList(MatchList matches) {
		if (matches != null) {
			if (matchList == null) {
				matchList = matches;
			} else {
				// this method keeps the observers
				matchList
						.copyDataFromOtherMatchList(matches);
			}

			this.postSetProcessing();
		} else {
			JOptionPane.showMessageDialog(R2cat.guiController.getMainWindow(), "Sorry, no Matches to display.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void postSetProcessing() {
		// use the preferences: with offsets?
		matchList.generateNewStatistics(); // this sets the
		// center of masses and the reversed match length.
		//this is needed for the next two methods:
		matchList.setInitialQueryOrder();
		matchList.setInitialQueryOrientation();

		//recalculate the offsets
		matchList.sortTargetsByPreviousOffset();

		
		// add offsets for the targets.
		matchList.setInitialTargetOrder();
		
		
		
		if (!R2cat.guiController
				.visualisationInitialized()) {
			R2cat.guiController.initVisualisation();
		}
		matchList
				.notifyObservers(MatchList.NotifyEvent.CHANGE);
	}

	/**
	 * @return the MatchList
	 */
	public MatchList getMatchesList() {
		return matchList;
	}

	/**
	 * @return the MatchStatistics
	 */
	public MatchStatistics getMatchStatistics() {
		return matchList.getStatistics();
	}

	/**
	 * 
	 */
	public DataModelController() {
	}
	
	public void writeMatches(File f) throws IOException {
		if(isMatchesListReady()) {
		matchList.writeToFile(f);
		}
	}
	
	public void readMatches(File f) throws IOException {
		MatchList matches = new MatchList();
		matches.readFromFile(f);
		this.setAlignmentsPositonsList(matches);
	}
	
	public void writeOrderOfContigs(File f) throws IOException {
		if(isMatchesListReady()) {
			matchList.writeContigsOrder(f);
		}
	}

	public int writeOrderOfContigsFasta(File f, boolean ignoreMissingFiles) throws IOException {
		//TODO check if all files are existent
		if(isMatchesListReady()) {
			return matchList.writeContigsOrderFasta(f, ignoreMissingFiles);
		}
		return 0;
	}

	
	public boolean isMatchesListReady() {
		if (matchList != null && !matchList.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	

}
