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

package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import de.bielefeld.uni.cebitec.cav.datamodel.SequenceOrderTableModel;

/**
 * This is a transfer handler which allows drag and drop in the
 * SequenceOrderTable.
 * 
 * @author phuseman
 * 
 */
public class SequenceOrderTableTransferHandler extends TransferHandler {

	private int[] indices = null;
	private SequenceOrderTable table;
	private SequenceOrderTableModel model;
	private JTable.DropLocation dropLocation;
	private int lastDropRow = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 * 
	 * creates a string representation of the selected rows. This it not
	 * necessary for the drag and drop operation. The latter is realized through
	 * the indices[] array which is set in this method and used in iportData.
	 */
	protected Transferable createTransferable(JComponent c) {
		SequenceOrderTable table = (SequenceOrderTable) c;
		
		//this is important for the DND!
		indices = table.getSelectedRows();

		
		SequenceOrderTableModel tm = (SequenceOrderTableModel) table.getModel();

		//this is only a string representation for other applications
		StringBuffer buff = new StringBuffer();
		if (indices.length > 0) {
			buff.append("#");

			for (int i = 0; i < tm.getColumnCount(); i++) {
				buff.append(tm.getColumnName(i));
				if (i != tm.getColumnCount() - 1) {
					buff.append("\t");
				}

			}
			buff.append("\n");

		}
		for (int i = 0; i < indices.length; i++) {
			for (int j = 0; j < tm.getColumnCount(); j++) {
				buff.append(tm.getValueAt(i, j));
				if (j != tm.getColumnCount() - 1) {
					buff.append("\t");
				}
			}
			buff.append("\n");
		}

		return new StringSelection(buff.toString());
	}

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
	 */
	protected void exportDone(JComponent c, Transferable data, int action) {
		if (action == TransferHandler.MOVE) {
			//reset the array which was used for message passing
			indices = null;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	 */
	public boolean canImport(TransferHandler.TransferSupport info) {
		
		//only import if the dnd event comes from the same class
		//TOTO assure that it is the same instance, not only the same object
		if (info.getComponent().getClass() != SequenceOrderTable.class) {
			return false;
		}

		
		// highlight the dragpoint in the visualization
		table = (SequenceOrderTable) info.getComponent();
		model = (SequenceOrderTableModel) table.getModel();
		dropLocation = (JTable.DropLocation) info.getDropLocation();
		if (lastDropRow != dropLocation.getRow()) {
			lastDropRow = dropLocation.getRow();
			table.highlightDropLocation(lastDropRow);
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
	 * 
	 * 
	 * Perform the actual import. This method only supports drag and drop.
	 */
	public boolean importData(TransferHandler.TransferSupport info) {
		if (!info.isDrop()) {
			return false;
		}

		table = (SequenceOrderTable) info.getComponent();
		model = (SequenceOrderTableModel) table.getModel();

		dropLocation = (JTable.DropLocation) info.getDropLocation();
		int drop_index = dropLocation.getRow();

		//if there are rows selected and the index is valid
		if (indices != null && drop_index >= 0) {
			// System.out.println("move ");
			// for (int i = 0; i < indices.length; i++) {
			// System.out.print(indices[i]+", ");
			// }
			// System.out.println("to "+drop_index);

			for (int i = 0; i < indices.length; i++) {
				if (indices[i] != drop_index) {
					//move the row
					model.moveRow(indices[i], drop_index);
					// System.out.println("moved "+indices[i]+" to " +
					// drop_index);
				}

				/*
				 * All elements between drop index and the element to be moved
				 * are shifted by one up
				 */
				if (drop_index < indices[i]) {
					for (int j = i + 1; j < indices.length; j++) {
						if (indices[j] >= drop_index && indices[j] < indices[i]) {
							// System.out.println("adjusted["+j+"]
							// "+indices[j]+" to " + (indices[j]+1));

							indices[j]++;
						}
					}
					//and the drop index has to be adjusted
					drop_index++;
				} else {
					/*
					 * All elements between the element to be moved and the drop
					 * are shifted by one down
					 */
					for (int j = i + 1; j < indices.length; j++) {
						if (indices[j] < drop_index && indices[j] >= indices[i]) {
							// System.out.println("adjusted["+j+"]
							// "+indices[j]+" to " + (indices[j]-1));
							indices[j]--;
						}
					}
				}

			}
			// System.out.println("-----");

		}

		model.finishReordering();
		model.fireTableDataChanged();

		return true;
	}

}
