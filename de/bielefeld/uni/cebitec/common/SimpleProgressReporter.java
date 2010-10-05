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

package de.bielefeld.uni.cebitec.common;

/**
 * A simple implementation of an {@link AbstractProgressReporter}.
 * Writes all progress to STDOUT
 *
 * This can be used if progress should be displayed without gui.
 * 
 * @author phuseman
 */
public class SimpleProgressReporter implements AbstractProgressReporter {

	/**
	 * Just print the message (if present) and the percent done (if nonnegative)
	 * 
	 * @param percentDone
	 *            how far is the algorithm: 0.0 is just started, 1.0 is
	 *            finished. any negative value means that only the comment is
	 *            important.
	 * @param comment
	 *            the comment for this status change
	 */
	public void reportProgress(double percentDone, String comment) {
		System.out
				.println((percentDone > 0 ? ((int) (percentDone * 100) + "% ")
						: "")
						+ comment);
	}

}
