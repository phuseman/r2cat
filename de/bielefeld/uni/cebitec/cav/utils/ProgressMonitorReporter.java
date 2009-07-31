/***************************************************************************
 *   Copyright (C) 2009 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.cav.utils;

import java.awt.Component;

import javax.swing.ProgressMonitor;

/**
 * Convenience class to pop up an ProgressMonitor that displays only the percentDone part in a new popup window.
 * It implements the {@link AbstractProgressReporter}.
 * 
 * @author phuseman
 *
 */
public class ProgressMonitorReporter extends ProgressMonitor implements AbstractProgressReporter {

	public ProgressMonitorReporter(Component parentComponent, Object message,
			String note) {
		super(parentComponent, message, note, 0, 100);
		this.setMillisToDecideToPopup(10);
		this.setMillisToPopup(500);
	}

	/* (non-Javadoc)
	 * @see de.bielefeld.uni.cebitec.cav.utils.AbstractProgressReporter#reportProgress(double, java.lang.String)
	 */
	@Override
	public void reportProgress(double percentDone, String comment) {
		//the comment is not used here
		if(percentDone>=0 && percentDone <=1) {
			this.setProgress((int) (percentDone*100));
		}

	}

}
