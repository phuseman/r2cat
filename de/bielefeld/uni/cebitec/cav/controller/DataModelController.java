/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
 *   phuseman@cebitec.uni-bielefeld.de                                     *
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

import de.bielefeld.uni.cebitec.cav.ComparativeAssemblyViewer;
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
		AlignmentPositionsList alignmentPositionsList = csvParser.parse();
		return alignmentPositionsList;
	}

	public void setAlignmentsPositonsListFromCSV(File csvFile) {
		this.setAlignmentsPositonsList(this
				.parseAlignmentPositionsFromCSV(csvFile));
	}

	public void setAlignmentsPositonsList(AlignmentPositionsList apl) {
		if (alignmentPositionsList == null) {
			alignmentPositionsList = apl;
		} else {
			// this method keeps the observers
			alignmentPositionsList.copyDataFromOtherAlignmentPositionsList(apl);
		}
		
		// use the preferences: with offsets?
		if (ComparativeAssemblyViewer.preferences.getDisplayOffsets()) {
			alignmentPositionsList.generateStatistics(); // this sets the
			// center of masses
			// for each query
			alignmentPositionsList.addOffsets();
		}

		if(!ComparativeAssemblyViewer.guiController.visualisationInitialized()) {
		ComparativeAssemblyViewer.guiController.initVisualisation();
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
}
