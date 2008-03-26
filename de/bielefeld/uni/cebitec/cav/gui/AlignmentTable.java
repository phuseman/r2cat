/***************************************************************************
 *   Copyright (C) 20.09.2007 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.cav.gui;

import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentTableModel;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList.NotifyEvent;

/**
 * @author Peter Husemann
 * 
 */
public class AlignmentTable extends JTable implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7954505821098415030L;

	private AlignmentPositionsList apl;

	private boolean selectionByUpdate = false;

	public AlignmentTable(AlignmentPositionsList apl) {
		super(new AlignmentTableModel(apl));

		this.apl = apl;
		this.apl.addObserver(this);

		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setColumnSelectionAllowed(false);
		this.setRowSelectionAllowed(true);

		// these are only available in java 6
		// this.setAutoCreateRowSorter(true);
		// this.setFillsViewportHeight(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Update the table if the datamodel had changed.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {

		if (arg == null) {
			this.revalidate();
		} else {

			AlignmentPositionsList.NotifyEvent action = (AlignmentPositionsList.NotifyEvent) arg;

			if (action == NotifyEvent.MARK) {
				// Selection changed; adjust table

				// avoid interference with the valueChanged method.
				// mutex by this variable:
				selectionByUpdate = true;

				this.clearSelection();

				for (int i = 0; i < apl.size(); i++) {
					if (apl.getAlignmentPositionAt(i).isSelected()) {
						this.addRowSelectionInterval(i, i);
					}
				}
				selectionByUpdate = false;
				this.revalidate();
			} else if (action == NotifyEvent.HIDE) {
				System.out.println("Elements hid; adjust table");
				// TODO: not yet implemented
			} else if (action == NotifyEvent.CHANGE) {
				System.out.println("Elements changed; adjust table");
				this.revalidate();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTable#valueChanged(javax.swing.event.ListSelectionEvent)
	 * 
	 * This method calls the superclass method and additionally marks all
	 * alignment positions which are selected in the table. The marked positions
	 * can then be drawn in the viewer.
	 * 
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);

		if (!selectionByUpdate) {

			if (!e.getValueIsAdjusting()) {
				DefaultListSelectionModel lsm = (DefaultListSelectionModel) e
						.getSource();

				for (int i = 0; i < apl.size(); i++) {
					apl.getAlignmentPositionAt(i).setSelected(
							lsm.isSelectedIndex(i));
				}
				apl.notifyObservers(null);
			}

		}
	}

}
