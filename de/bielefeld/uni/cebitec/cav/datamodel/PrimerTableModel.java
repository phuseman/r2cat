/***************************************************************************
 *   Copyright (C) 2010 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.cav.datamodel;

import java.util.Locale;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList.NotifyEvent;

/**
 * This table model provides a wrapper between an alignment positions list and a JTable
 * 
 * @author phuseman
 */
public class PrimerTableModel extends AbstractTableModel {

	private String[] columnNames = { "Contig 1", "Contig 2", "Generate Primer?" };
	private boolean[] createPrimer;
	private AlignmentPositionsList apl;

	/**
	 * This is a table backend which displays the contigs of a alignment
	 * positions list.
	 */
	public PrimerTableModel(AlignmentPositionsList apl) {
		this.apl = apl;
		createPrimer = new boolean[this.getRowCount()];
	}

	/**
	 * Get the vector of dnasequences which the table shows
	 * 
	 * @return the vector of DNASequences
	 */
	private Vector<DNASequence> dnaSequence() {
		return apl.getQueries();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int col) {
			return columnNames[col].toString();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return dnaSequence().size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
			return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int) 
	 * 
	 * Maps the Vector of
	 *      DNASequences to cell entries of the table
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object out = null;
		
		int nextIndex=(rowIndex+1)%dnaSequence().size();
		
		switch (columnIndex) {
		case 0:
			out = dnaSequence().get(rowIndex);
			break;
		case 1:
			out = dnaSequence().get(nextIndex);
			break;
		case 2:
			out = createPrimer[rowIndex];
			break;

		default:
			break;
		}

		return out;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 * 
	 * Only the complement column should be editable
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 2 ) {
			return true;
		} else {
			return false;
		}

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return this.getValueAt(0, columnIndex).getClass();
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 * <br>
	 * adjust the orientation of a contig
	 */
	public void setValueAt(Object value, int row, int col) {
		if (col == 2) {
			boolean markedForPrimerdesign = (Boolean) value;
			createPrimer[row]=markedForPrimerdesign;
		}
	}

	/**
	 * Moves a contig (row) from the from index to the to index
	 * 
	 * @param from origin of move
	 * @param to target of move
	 */
	public void moveRow(int from, int to) {
		apl.moveQuery(from, to);
	}

	/**
	 * After a reordering (contigs moved or orientation changed) has happened
	 * this method adjusts the offsets of the contigs and notifies the observers
	 * of the rearrangement.
	 */
	public void finishReordering() {
		apl.setQueryOffsets();
		apl.notifyObservers(NotifyEvent.ORDER_CHANGED_OR_CONTIG_REVERSED);
	}

	/**
	 * Selects all pairs of contigs.
	 */
	public void selectAll() {
		for (int i = 0; i < createPrimer.length; i++) {
			createPrimer[i]=true;
		}
	}

	/**
	 * Unselects all contigs.
	 */
	public void selectNone(){
		for (int i = 0; i < createPrimer.length; i++) {
			createPrimer[i]=false;
		}
	}
	
	/**
	 * Method to get all primer pairs that are selected.
	 * (The return type string is only an example. Feel free to adopt it as necessary)
	 * @return
	 */
	public String getSelectedPairs() {
		StringBuilder out = new StringBuilder();
		int secondIndex ;
		Vector<String[]> contigPairVector = new Vector<String[]>();
		int j = 0;
		for (int i = 0; i < createPrimer.length; i++) {
			String[] contigPair = new String[6];
			if (createPrimer[i]) {
				//we look at all adjacent contigs, so the next contig has index+1, except for the last one, here it is the zero index.
				secondIndex=(i+1)%dnaSequence().size();
				
				// the key information are the contig names and their direction
				out.append( String.format((Locale) null,
						"%s, %b,%s, %s, %b,%s\n",
						dnaSequence().get(i).getId(),
						dnaSequence().get(i).isReverseComplemented(),
						"forward",
						dnaSequence().get(secondIndex).getId(),
						dnaSequence().get(secondIndex).isReverseComplemented(),
						"reverse"
						));
			}
		}
		return out.toString();
	}
	
//other setup for getSelectedParis... works with my setup of actionperformed in primertable
/*	public Vector<String[]> getSelectedPairs() {
		int secondIndex ;
		Vector<String[]> contigPairVector = new Vector<String[]>();
		int j = 0;
		for (int i = 0; i < createPrimer.length; i++) {
			String[] contigPair = new String[6];
			if (createPrimer[i]) {
				//we look at all adjacent contigs, so the next contig has index+1, except for the last one, here it is the zero index.
				secondIndex=(i+1)%dnaSequence().size();
				
				contigPair[0] = dnaSequence().get(i).getId();
				contigPair[1] = new Boolean(dnaSequence().get(i).isReverseComplemented()).toString();
				contigPair[2] = "forward";
				contigPair[3] = dnaSequence().get(secondIndex).getId();
				contigPair[4] = new Boolean(dnaSequence().get(secondIndex).isReverseComplemented()).toString();
				contigPair[5] = "reverse";
				contigPairVector.add(j, contigPair);

				j++;
				
			}
		}
		return contigPairVector;
	}*/

}
