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

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;

import de.bielefeld.uni.cebitec.cav.PrimerDesign.PrimerGenerator;
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
public class PrimerTable extends JTable implements Observer, ActionListener, ItemListener {

	static class ContigTableCellRenderer extends DefaultTableCellRenderer {
		private boolean reverse = false;
		private boolean repetitive = false;
		private int size = 0;


		public ContigTableCellRenderer() {
			super();
			this.setHorizontalAlignment(CENTER);
			this.setPreferredSize(getPreferredSize());
		}

		public void setValue(Object value) {
			DNASequence s = (DNASequence) value;
			reverse = s.isReverseComplemented();
			repetitive = s.isRepetitive();
			size = (int) s.getSize();
			setText(s.getId());
		}

		public void paintComponent(Graphics g) {
			//if a table row is selected, then fore and background are overpainted by super.paintComponent(g)
			// so, we first paint a solid contig representation...
			paintContig(g,true);
			// this will possibly be overpainted by the following method
			super.paintComponent(g);
			// and then we again draw the conture of the contig, again overwriting the paint component
			paintContig(g,false);
		}

		/**
		 * Paints some kind of an arrow to depict a contig.
		 * 
		 * @param g
		 */
		private void paintContig(Graphics g, boolean filled) {
			int arrowsize = 10;
			int border = 1;
			
			//TODO include size differences for the contigs

			Color oldColor = g.getColor();
			Color drawColor;
			// select different colors for repetitive and non repetitive contigs
			if (repetitive) {
				drawColor = new Color(230, 230, 230);
			} else {
				drawColor = new Color(220, 220, 220);
			}

			g.setColor(drawColor);
			int halfheight = (this.getHeight() - 2 * border) / 2;
			if (!reverse) {
				// this draws an arrow pointing to the right |===>
				int[] xcoords = { border, this.getWidth() - arrowsize,
						this.getWidth(), this.getWidth() - arrowsize, border,
						border };
				int[] ycoords = { border, border, halfheight,
						this.getHeight() - border, this.getHeight() - border, 0 };
				if (filled) {
					g.fillPolygon(xcoords, ycoords, xcoords.length);
				} else {
					g.drawPolygon(xcoords, ycoords, xcoords.length);
				}
			} else {
				// if the contig was reverse complemented, this draws the arrow
				// to the other side <===|
				int[] xcoords = { 0, arrowsize, this.getWidth() - border,
						this.getWidth() - border, arrowsize, 0 };
				int[] ycoords = { halfheight, border, border,
						this.getHeight() - border, this.getHeight() - border,
						halfheight };
				if (filled) {
					g.fillPolygon(xcoords, ycoords, xcoords.length);
				} else {
					g.drawPolygon(xcoords, ycoords, xcoords.length);
				}
			}
			g.setColor(oldColor);
		}
	}

	private AlignmentPositionsList apl;
	// mutex to avoid interference
	private boolean selectionByUpdate = false;
	// mutex to ignore an update
	private boolean ignoreUpdate = false;

	private int biggestContig;

	public PrimerTable(AlignmentPositionsList apl) {
		super(new PrimerTableModel(apl));

		this.apl = apl;
		apl.addObserver(this);

		biggestContig = (int) apl.getStatistics().getMaximumQuerySize();

		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setColumnSelectionAllowed(false);
		this.setAutoscrolls(true);
		this.setRowSelectionAllowed(true);
		// this.setDragEnabled(false);
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
				// if the order has changed, the selected contig pairs might
				// have changed.
				// -> deselect all
				((PrimerTableModel) this.getModel()).selectNone();
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
			//TODO: Add the code to generate primer pairs
			// For somewhat lengthy calculations we should use here a SwingWorker that does not block the GUI.
			// Additionally a ProgressMonitor helps to estimate the waiting time.
			System.out.println(((PrimerTableModel) this.getModel()).getSelectedPairs());
			

		} else if (e.getActionCommand().equals("select_all")) {
			((PrimerTableModel) this.getModel()).selectAll();
		} else if (e.getActionCommand().equals("select_none")) {
			((PrimerTableModel) this.getModel()).selectNone();
		}
		this.invalidate();
		this.repaint();

	}

/*	//other set up for actionPerformed method
	private PrimerGenerator pg = null;
	private Vector<String[]> contigPairs = null;
	private boolean repeatMasking = false;
	
	class PrimerGeneratorTask extends SwingWorker<Void,Void>{
		@Override
		protected Void doInBackground() throws Exception {
		File configFile = new File("C:/Users/Yvisunshine/Uni/primer_search_default_config_original.xml");
		File fastaFile = new File("C:/Users/Yvisunshine/Uni/contigs.fas");
		File outputDir = new File(System.getProperty("user.home"));
		boolean rm = false;
		if(repeatMasking){
			rm = repeatMasking;
		pg = new PrimerGenerator(fastaFile,configFile,rm,outputDir);
		} else{
			rm = repeatMasking;
			pg = new PrimerGenerator(fastaFile,configFile,rm,outputDir);
		}
			pg.generatePrimers(contigPairs);
			return null;
		}
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("generate_primer")) {
			System.out.println("Generate primer!!");
			//TODO: Add the code to generate primer pairs
			// For somewhat lengthy calculations we should use here a SwingWorker that does not block the GUI.
			// Additionally a ProgressMonitor helps to estimate the waiting time.
			PrimerGeneratorTask pgT = new PrimerGeneratorTask();
			contigPairs = (Vector<String[]>)((PrimerTableModel)this.getModel()).getSelectedPairs();
			pgT.execute();

		} else if (e.getActionCommand().equals("select_all")) {
			((PrimerTableModel) this.getModel()).selectAll();
		} else if (e.getActionCommand().equals("select_none")) {
			((PrimerTableModel) this.getModel()).selectNone();
		}
		this.invalidate();
		this.repaint();

	}*/

	@Override
	public void itemStateChanged(ItemEvent arg0) {
/*		if(arg0.getItem()=="Repeat Masking"){
		if(arg0.getStateChange()==1){
			repeatMasking = true;
		} else{
			repeatMasking = false;
		}
		}*/
		
	}
}

