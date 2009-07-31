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


/**
 * This interface should give uniform access in order to show progress reports.
 * The implementing class assures to show the results in an appropriate way.
 * 
 * For example this can be used with a SwingWorker task by calling the publish and setProgress methods.
 * 
 * 
 * @author phuseman
 *
 */
public interface AbstractProgressReporter {

	/**
	 * This method updates the progress of the object which implements this in an appropriate way.
	 * The progress is given as double value between 0 and 1 indicating how far the computation is,
	 * and a comment string describing the progress
	 * @param percentDone how far is the algorithm: 0.0 is just started, 1.0 is finished.
	 * 			any negative value means that only the comment is important.
	 * @param comment the comment for this status change. Can be null or empty if not needed.
	 */
	abstract void reportProgress(double percentDone, String comment);
	
	//here is one example to use with a JTextArea progress and a JProgressbar progressBar
	//
//	@Override
//	public void reportProgress(double percentDone, String comment) {
//		if(percentDone>=0 && percentDone<=1) {
//		progressBar.setValue((int) (percentDone*100.));
//		}
//		if (comment != null && !comment.isEmpty()) {
//			progress.append(comment+"\n");
//			progress.setCaretPosition(progress.getDocument().getLength());
//		}
//	}
	
	
//	Another example for a SwingWorker<?, String>
//	@Override
//	public void reportProgress(double percentDone, String comment) {
//		if(percentDone>=0 && percentDone<=1) {
//			setProgress((int) (percentDone*100.));
//		}
//		if (comment != null && !comment.isEmpty()) {
//			publish(comment);
//		}
//	}
//
// the events are then delivered via the process method and with the help of a property change listener
	

}

