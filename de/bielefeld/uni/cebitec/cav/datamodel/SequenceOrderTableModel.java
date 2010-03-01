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

package de.bielefeld.uni.cebitec.cav.datamodel;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList.NotifyEvent;

/**
 * This table model provides a wrapper between an alignment positions list and a JTable
 * 
 * @author phuseman
 */
public class SequenceOrderTableModel extends AbstractTableModel {

	// this could be extended to an interface which is suitable for ordering
	// contigs and reference genomes.
	// for the latter the complement column should not be displayed
	private boolean showComplementColumn = false;

	private String[] columnNames = { "Id", "Length" };
	private String[] columnNamesWithComplement = { "Id", "Length", "Complement", "% Repeating" };

	private AlignmentPositionsList apl;

	/**
	 * This is a table backend which displays the contigs of a alignment
	 * positions list.
	 */
	public SequenceOrderTableModel(AlignmentPositionsList apl) {
		this.apl = apl;
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
		if (!showComplementColumn) {
			return columnNames[col].toString();
		} else {
			return columnNamesWithComplement[col].toString();
		}
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
		if (!showComplementColumn) {
			return columnNames.length;
		} else {
			return columnNamesWithComplement.length;
		}
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
		switch (columnIndex) {
		case 0:
			out = dnaSequence().get(rowIndex).getId();
			break;
		case 1:
			out = dnaSequence().get(rowIndex).getSize();
			break;
		case 2:
			out = dnaSequence().get(rowIndex).isReverseComplemented();
			break;
		case 3:
			out = String.format("%.2f", dnaSequence().get(rowIndex).getRepetitivePercent()*100);
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
		if (columnIndex == 2) {
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

	/**
	 * Decides whether or not the display column will be shown
	 * @param showComplementColumn
	 */
	public void setShowComplementColumn(boolean showComplementColumn) {
		this.showComplementColumn = showComplementColumn;
		this.fireTableStructureChanged();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 * <br>
	 * adjust the orientation of a contig
	 */
	public void setValueAt(Object value, int row, int col) {
		if (col == 2) {
			boolean reversed = (Boolean) value;
			apl.setQueryReverseComplemented(row, reversed);
			apl.notifyObservers(NotifyEvent.ORDER_CHANGED_OR_CONTIG_REVERSED);
			fireTableCellUpdated(row, col);
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

}
