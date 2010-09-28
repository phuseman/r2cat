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

package de.bielefeld.uni.cebitec.cav.controller;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import de.bielefeld.uni.cebitec.cav.R2cat;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsStatistics;
import de.bielefeld.uni.cebitec.cav.datamodel.CSVParser;

/**
 * @author phuseman
 * 
 */
public class DataModelController {

	private AlignmentPositionsList alignmentPositionsList;

	public AlignmentPositionsList parseAlignmentPositionsFromCSV(File csvFile) {
		CSVParser csvParser = new CSVParser(csvFile);
		csvParser.setSwiftMode();
		AlignmentPositionsList alignmentPositionsList = csvParser.parse();
		return alignmentPositionsList;
	}

	public void setAlignmentsPositonsListFromCSV(File csvFile) {
		this.setAlignmentsPositonsList(this
				.parseAlignmentPositionsFromCSV(csvFile));
	}

	public void setAlignmentsPositonsList(AlignmentPositionsList apl) {
		if (apl != null) {
			if (alignmentPositionsList == null) {
				alignmentPositionsList = apl;
			} else {
				// this method keeps the observers
				alignmentPositionsList
						.copyDataFromOtherAlignmentPositionsList(apl);
			}

			this.postSetProcessing();
		} else {
			JOptionPane.showMessageDialog(R2cat.guiController.getMainWindow(), "Sorry, no Matches to display.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void postSetProcessing() {
		// use the preferences: with offsets?
		alignmentPositionsList.generateNewStatistics(); // this sets the
		// center of masses and the reversed match length.
		//this is needed for the next two methods:
		alignmentPositionsList.setInitialQueryOrder();
		alignmentPositionsList.setInitialQueryOrientation();

		//recalculate the offsets
		alignmentPositionsList.sortTargetsByPreviousOffset();

		
		// add offsets for the targets.
		alignmentPositionsList.setInitialTargetOrder();
		
		
		
		if (!R2cat.guiController
				.visualisationInitialized()) {
			R2cat.guiController.initVisualisation();
		}
		alignmentPositionsList
				.notifyObservers(AlignmentPositionsList.NotifyEvent.CHANGE);
	}

	/**
	 * @return the alignmentPositionsList
	 */
	public AlignmentPositionsList getAlignmentPositionsList() {
		return alignmentPositionsList;
	}

	/**
	 * @return the alignmentPositionsStatistics
	 */
	public AlignmentPositionsStatistics getAlignmentPositionsStatistics() {
		return alignmentPositionsList.getStatistics();
	}

	/**
	 * 
	 */
	public DataModelController() {
	}
	
	public void writeAlignmentPositions(File f) throws IOException {
		if(isAlignmentpositionsListReady()) {
		alignmentPositionsList.writeToFile(f);
		}
	}
	
	public void readAlignmentPositions(File f) throws IOException {
		AlignmentPositionsList apl = new AlignmentPositionsList();
		apl.readFromFile(f);
		this.setAlignmentsPositonsList(apl);
	}
	
	public void writeOrderOfContigs(File f) throws IOException {
		if(isAlignmentpositionsListReady()) {
			alignmentPositionsList.writeContigsOrder(f);
		}
	}

	public int writeOrderOfContigsFasta(File f, boolean ignoreMissingFiles) throws IOException {
		//TODO check if all files are existent
		if(isAlignmentpositionsListReady()) {
			return alignmentPositionsList.writeContigsOrderFasta(f, ignoreMissingFiles);
		}
		return 0;
	}

	
	public boolean isAlignmentpositionsListReady() {
		if (alignmentPositionsList != null && !alignmentPositionsList.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	

}
