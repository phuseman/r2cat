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

import java.io.IOException;

import javax.swing.JOptionPane;

import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.gui.CustomFileFilter;
import de.bielefeld.uni.cebitec.cav.utils.MiscFileUtils;

/**
 * This exception is used if a fasta file should be written but the source file
 * for a DNASequence object is not known or does not contain the sequence with
 * the given ID
 * 
 * @author phuseman
 * 
 */
public class SequenceNotFoundException extends IOException {
	private DNASequence sequence = null;

	public SequenceNotFoundException(DNASequence s) {
		super();
		sequence = s;
	}

	public SequenceNotFoundException(String message, Throwable cause,
			DNASequence s) {
		super(message, cause);
		sequence = s;
	}

	public SequenceNotFoundException(String message, DNASequence s) {
		super(message);
		sequence = s;
	}

	public SequenceNotFoundException(Throwable cause, DNASequence s) {
		super(cause);
		sequence = s;
	}

	public DNASequence getDNASequence() {
		return sequence;
	}

	public void setDNASequence(DNASequence sequence) {
		this.sequence = sequence;
	}

	/**
	 * This method handles the case, when the file for a dna sequence is not given, or not readable.
	 * The user is asked, if the missing file should be selected. If so a dialoge is popped up to select a file.
	 * 
	 * @param SequenceNotFoundException contains the {@link DNASequence} object for that the file is missing.
	 * @return Integer either JOptionPane.YES_OPTION, JOptionPane.NO_OPTION, or JOptionPane.CANCEL_OPTION
	 */
	public static int handleSequenceNotFoundException(
			SequenceNotFoundException e) {
		Object[] options = { "Yes", "No, leave out missing sequences", "Abort" };
		int jOptionPaneAnswer = JOptionPane.showOptionDialog(null, e
				.getMessage()
				+ "\nDo you want to select a file?", "Sequence not found",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
				null, options, options[0]);

		if(jOptionPaneAnswer == JOptionPane.YES_OPTION) {
			e.getDNASequence().setFile(
					MiscFileUtils.chooseFile(null,"Choose a new file", null, true,
							new CustomFileFilter(".fas,.fna,.fasta",
									"Fasta file")));
		}

		return jOptionPaneAnswer;
	}
}
