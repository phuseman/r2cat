/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
 *   phuseman ät cebitec.uni-bielefeld.de                                  *
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

package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListSelectionModel;
import javax.swing.DropMode;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.SequenceOrderTableModel;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList.NotifyEvent;

/**
 * This table shows the order of queries of an AlignmentPositionsList Elements
 * can be moved via drag and drop or the moveSelectionUpDown method.
 * 
 * @author phuseman
 * 
 */
public class SequenceOrderTable extends JTable implements Observer,
		ActionListener {

	private AlignmentPositionsList apl;
	// mutex to avoid interference
	private boolean selectionByUpdate = false;
	// mutex to ignore an update
	private boolean ignoreUpdate = false;

	public SequenceOrderTable(AlignmentPositionsList apl) {
		super(new SequenceOrderTableModel(apl));

		this.apl = apl;
		apl.addObserver(this);

		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setColumnSelectionAllowed(false);
		this.setAutoscrolls(true);
		this.setRowSelectionAllowed(true);
		this.setDragEnabled(true);
		this.setDropMode(DropMode.INSERT_ROWS);
		this.setTransferHandler(new SequenceOrderTableTransferHandler());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 * 
	 * If the apl was changed this method will be called. ignoreUpdate is used
	 * if this object caused the changes.
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (ignoreUpdate) {
			return;
		}
		// 
		if (arg == null) {
			this.revalidate();
		} else {
			AlignmentPositionsList.NotifyEvent action = (AlignmentPositionsList.NotifyEvent) arg;

			if (action == NotifyEvent.MARK) {

				// avoid interference with the valueChanged method.
				// mutex by this variable:
				selectionByUpdate = true;

				this.clearSelection();

				boolean scrolledToFirstSelectedRow = false;
				int viewIndex;
				for (int i = 0; i < apl.getQueries().size(); i++) {
					if (apl.getQueries().get(i).isMarked()) {
						viewIndex = this.convertRowIndexToView(i);
						this.addRowSelectionInterval(viewIndex, viewIndex);
						if (!scrolledToFirstSelectedRow) {
							Rectangle rect = this.getCellRect(viewIndex, 0,
									false);
							if (rect != null) {
								this.scrollRectToVisible(rect);
							}

							scrolledToFirstSelectedRow = true;
						}
					}
				}
				selectionByUpdate = false;
				this.revalidate();
			} else if (action == NotifyEvent.HIDE) {
				this.revalidate();
			} else if (action == NotifyEvent.CHANGE) {
				// System.out.println("Elements changed; adjust table");
				this.revalidate();
			} else if (action == NotifyEvent.ORDER_CHANGED_OR_CONTIG_REVERSED) {
				this.revalidate();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTable#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);

		// avoid interference with the update method.
		if (!selectionByUpdate) {
			// wait until the selection has settled
			if (!e.getValueIsAdjusting()) {
				apl.unmarkAllAlignments();
				apl.unmarkAllQueries();

				DefaultListSelectionModel lsm = (DefaultListSelectionModel) e
						.getSource();

				int modelIndex = 0;
				for (int i = 0; i < apl.getQueries().size(); i++) {
					modelIndex = this.convertRowIndexToModel(i);
					apl.markQuery(modelIndex, lsm.isSelectedIndex(i));
				}
				this.ignoreUpdate = true;
				apl.notifyObservers(NotifyEvent.MARK);
				this.ignoreUpdate = false;
			}

		}
	}

	/**
	 * Marks the two contigs above and below the drop location which is given by
	 * the row index of the table.
	 * 
	 * @param rowIndex
	 */
	public void highlightDropLocation(int rowIndex) {
		apl.unmarkAllQueries();
		apl.unmarkAllAlignments();
		if (rowIndex > 0) {
			apl.markQuery(rowIndex - 1, true);
		}
		if (rowIndex < apl.getQueries().size()) {
			apl.markQuery(rowIndex, true);
		}
		this.ignoreUpdate = true;
		apl.notifyObservers(NotifyEvent.MARK);
		this.ignoreUpdate = false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("up")) {
			this.moveSelectionUpDown(true);
		} else if (e.getActionCommand().equals("down")) {
			this.moveSelectionUpDown(false);
		}

	}

	/**
	 * This method moves the selected entries (and all between them) one
	 * position up or down. If up is true the selection will get lower indices
	 * in the table.
	 * 
	 * @param up
	 *            move the entries in the table up (true) or down (false)
	 */
	public void moveSelectionUpDown(boolean up) {

		SequenceOrderTableModel model = (SequenceOrderTableModel) this
				.getModel();

		int[] rows = this.getSelectedRows();
		if (rows.length < 1) {
			return;
		}

		int min = Integer.MAX_VALUE;
		int max = 0;

		for (int i = 0; i < rows.length; i++) {
			if (min > rows[i]) {
				min = rows[i];
			}
			if (max < rows[i]) {
				max = rows[i];
			}
		}

		if (up && (min - 1) < 0) {
			return;
		}
		if (!up && (max + 1) >= model.getRowCount()) {
			return;
		}

		boolean change = false;
		if (up && min >= 1) {
			model.moveRow(min - 1, max + 1);
			// System.out.println("move up: " + (min-1) +" to "+ (max+1));
			model.fireTableRowsUpdated(min - 1, max + 1);
			change = true;
		} else if (max < model.getRowCount()) {
			model.moveRow(max + 1, min);
			// System.out.println("move down: " + (max+1) +" to "+ (min));
			model.fireTableRowsUpdated(min, max + 1);
			change = true;
		}

		// if really something was changed:
		if (change) {
			// this has to occur here, because later methods call the notify
			// observer method
			// of the apl and set the status to not changed otherwise
			model.finishReordering();
			
			boolean scrolledToFirstSelectedRow=false;

			// restore old selection
			this.clearSelection();
			for (int i = 0; i < rows.length; i++) {
				int row = rows[i];
				if (up) {
					row--;
				} else {
					row++;
				}
				this.addRowSelectionInterval(row, row);
				if (!scrolledToFirstSelectedRow) {
					Rectangle rect = this.getCellRect(row, 0,
							false);
					if (rect != null) {
						this.scrollRectToVisible(rect);
					}

					scrolledToFirstSelectedRow = true;
				}
			}
			


		}

	}

}
