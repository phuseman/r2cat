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

package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.datamodel.PrimerTableModel;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList.NotifyEvent;

/**
 * This table shows the order of queries of an AlignmentPositionsList Elements
 * can be moved via drag and drop or the moveSelectionUpDown method.
 * 
 * @author phuseman
 * 
 */
public class PrimerTable extends JTable implements Observer, ActionListener {

	static class ContigTableCellRenderer extends DefaultTableCellRenderer {
		public ContigTableCellRenderer() {
			super();
		}

		public void setValue(Object value) {
			DNASequence s = (DNASequence) value;
			setText((s.isReverseComplemented()?"<":"|") + s.getId() + (s.isReverseComplemented()?"|":">"));
			if(s.isRepetitive()) {
				setBackground(Color.lightGray);
			} else {
				setBackground(Color.white);
			}
		}
	}

	private AlignmentPositionsList apl;
	// mutex to avoid interference
	private boolean selectionByUpdate = false;
	// mutex to ignore an update
	private boolean ignoreUpdate = false;

	public PrimerTable(AlignmentPositionsList apl) {
		super(new PrimerTableModel(apl));

		this.apl = apl;
		apl.addObserver(this);

		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setColumnSelectionAllowed(false);
		this.setAutoscrolls(true);
		this.setRowSelectionAllowed(true);
		this.setDragEnabled(false);
		// this.setDropMode(DropMode.INSERT_ROWS);
		// this.setTransferHandler(new SequenceOrderTableTransferHandler());

		this.setDefaultRenderer(DNASequence.class,
				new ContigTableCellRenderer());
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
				this.invalidate();
				this.repaint();
			} else if (action == NotifyEvent.CHANGE) {
				this.invalidate();
				this.repaint();
			} else if (action == NotifyEvent.ORDER_CHANGED_OR_CONTIG_REVERSED) {
				this.invalidate();
				this.repaint();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.JTable#valueChanged(javax.swing.event.ListSelectionEvent)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("generate_primer")) {
			System.out.println("Generate primer!!");
		} else if (e.getActionCommand().equals("select_all")) {
			((PrimerTableModel) this.getModel()).selectAll();
		} else if (e.getActionCommand().equals("select_none")) {
			((PrimerTableModel) this.getModel()).selectNone();
		}
		this.invalidate();
		this.repaint();

	}

}
