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

package de.bielefeld.uni.cebitec.r2cat.gui;

import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import de.bielefeld.uni.cebitec.qgram.MatchList;

/**
 * This is a Table model for displaying an {@link MatchList} as a
 * table.
 * 
 * @author Peter Husemann
 * 
 */
public class MatchesTableModel extends AbstractTableModel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5076289599342214580L;

	private String[] columnNames = { "Query", "Start", "Stop", "Target",
			"Start", "Stop",  "q-hits", "repeat Count" };

	private MatchList alPosList;

	/**
	 * Constructs a table model from an {@link MatchList}.
	 * 
	 * @param alPosList
	 */
	public MatchesTableModel(MatchList matches) {
		this.setMathesList(matches);
	}

	public void setMathesList(MatchList matches) {
		this.alPosList = matches;
		this.alPosList.addObserver(this);
		this.fireTableDataChanged();
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col].toString();
	}

	public int getRowCount() {
		return alPosList.size();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Object out = null;
		switch (columnIndex) {
		case 0:
			out = alPosList.getMatchAt(rowIndex).getQuery().getId();
			break;
		case 1:
			out = alPosList.getMatchAt(rowIndex).getQueryStart();
			break;
		case 2:
			out = alPosList.getMatchAt(rowIndex).getQueryEnd();
			break;
		case 3:
			out = alPosList.getMatchAt(rowIndex).getTarget()
					.getId();
			break;
		case 4:
			out = alPosList.getMatchAt(rowIndex).getTargetStart();
			break;
		case 5:
			out = alPosList.getMatchAt(rowIndex).getTargetEnd();
			break;
		case 6:
			if(alPosList.getMatchAt(rowIndex).getNumberOfQHits() > 0) {
				out = alPosList.getMatchAt(rowIndex).getNumberOfQHits();
			} else {
				out = "N/A";
			}
			break;
		case 7:
			out = alPosList.getMatchAt(rowIndex).getRepeatCount();
			break;
			
		default:
			break;
		}

		return out;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return this.getValueAt(0, columnIndex).getClass();
	}

	public void update(Observable o, Object arg) {
		// TODO maybe only update modified cells
	}


}
