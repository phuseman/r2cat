/***************************************************************************
 *   Copyright (C) 20.09.2007 by Peter Husemann                                  *
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

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import de.bielefeld.uni.cebitec.qgram.Match;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.qgram.MatchList.NotifyEvent;

/**
 * @author Peter Husemann
 * 
 */
public class MatchesTable extends JTable implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7954505821098415030L;

	private MatchList matches;

	private boolean selectionByUpdate = false;
	private boolean ignoreUpdate = false;
	
	

	public MatchesTable(MatchList matches) {
		super(new MatchesTableModel(matches));

		this.matches = matches;
		this.matches.addObserver(this);

		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setColumnSelectionAllowed(false);
		this.setRowSelectionAllowed(true);

		// these are only available in java 6
		this.setAutoCreateRowSorter(true);
		this.setFillsViewportHeight(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Update the table if the datamodel had changed.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (ignoreUpdate) {
			return;
		}

		if (arg == null) {
			this.revalidate();
		} else {
			MatchList.NotifyEvent action = (MatchList.NotifyEvent) arg;

			if (action == NotifyEvent.MARK) {
				// Selection changed; adjust table

				// avoid interference with the valueChanged method.
				// mutex by this variable:
				selectionByUpdate = true;

				this.clearSelection();

				boolean scrolledToFirstSelectedRow = false;
				int viewIndex;
				for (int i = 0; i < matches.size(); i++) {
					if (matches.getMatchAt(i).isSelected()) {
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
				// TODO hiding is not implemented yet
				System.out.println("Elements hid; adjust table");
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
	 * 
	 * This method calls the superclass method and additionally marks all
	 * alignment positions which are selected in the table. The marked positions
	 * can then be drawn in the viewer.
	 * 
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);

		// avoid interference with the update method.
		if (!selectionByUpdate) {
			// wait until the selection has settled
			if (!e.getValueIsAdjusting()) {
				DefaultListSelectionModel lsm = (DefaultListSelectionModel) e
						.getSource();

				int modelIndex;
				for (int i = 0; i < matches.size(); i++) {
					modelIndex = this.convertRowIndexToModel(i);
					matches.getMatchAt(modelIndex).setSelected(
							lsm.isSelectedIndex(i));
				}
				matches.markQueriesWithSelectedAps();
				this.ignoreUpdate = true;
				matches.notifyObservers(NotifyEvent.MARK);
				this.ignoreUpdate = false;
			}

		}
	}
	
    /* (non-Javadoc)
     * @see javax.swing.JTable#getToolTipText(java.awt.event.MouseEvent)
     * Borrowed from http://java.sun.com/docs/books/tutorial/uiswing/components/table.html
     */
    public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        int realColumnIndex = convertColumnIndexToModel(colIndex);

        int modelIndex = this.convertRowIndexToModel(rowIndex);
        Match ap = matches.getMatchAt(modelIndex);
		
        if (realColumnIndex == 6) { //qhits
        	if(ap.getNumberOfQHits()>1) {
        		tip="<html>There were " + ap.getNumberOfQHits()+ " exactly matching 11-grams."+
        		String.format((Locale)null,"<br>That is an average of %.3f q-grams per base.", (((float)ap.getNumberOfQHits())/ap.size()));
        	} else {
        		tip=null;
        	}
        } else if (realColumnIndex == 7) { //repeat count
        	if (ap.getRepeatCount()>0) {
        	tip="<html>The query sequence of this match seems to be<br>"
        		+ ap.getRepeatCount()+ " times repeating";
        	} else {
        		tip=null;
        	}
        } else { //another column
        	tip = "<html>Match size: "+ ap.size()+ " bases. That is<br>" +
        	String.format((Locale)null,"%.2f%% of the queries size (%d)<br>%.2f%% of the targets size (%d)",
        			((100.*ap.size())/ap.getQuery().getSize()),ap.getQuery().getSize(),
        			((100.*ap.size())/ap.getTarget().getSize()),ap.getTarget().getSize()) ;
        	
        	
        }
        return tip;
    }


}
