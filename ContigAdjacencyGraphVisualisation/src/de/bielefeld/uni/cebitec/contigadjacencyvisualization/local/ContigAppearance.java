/***************************************************************************
 *   Copyright (C) 2010/11 by Annica Seidel                                *
 *   aseidel  a t  cebitec.uni-bielefeld.de                                *
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
package de.bielefeld.uni.cebitec.contigadjacencyvisualization.local;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;

/**
 * Create a Contig illustration 
 * size, orientation, (not) selected or ulterior selected 
 */
public class ContigAppearance extends JPanel {

	private JLabel contigLabel;
	private AdjacencyEdge edge;
	private int index;
	private boolean isReverse;
	private boolean someWhereElseSelected;
	private boolean selected;
	private boolean isRepetitiv;
	private int textSize = 2;
	private int contigPanelHeight = 50;
	private int contigPanelMinWidth = 70;
	private int contigPanelMaxWidth = 215;
	private int wSize;

	public ContigAppearance() {
		super();
		setBackground(Color.WHITE);
		contigLabel = new JLabel();
	}
	

	/*
	 * Figure out, if the contig have to be displayed as reverse or not
	 * this should only used for neighbours!
	 */
	public void isContigReverse( boolean isLeftContig, AdjacencyEdge edge, int indexOfContig ){
		
		boolean isContigReverse = false;
		
		if (isLeftContig) {

			if (edge.geti() == indexOfContig) {
				isContigReverse = edge.isLeftConnectori();
			} else {
				isContigReverse = edge.isLeftConnectorj();
			}
		} else {
			if (edge.geti() == indexOfContig) {
				isContigReverse = edge.isRightConnectori();
			} else {
				isContigReverse = edge.isRightConnectorj();
			}
		}
		
		isReverse =  isContigReverse;
	}

	/*
	 * Set the individual appearance for this contig
	 */
	public void setContigAppearance(String contigId, long size,
			
			boolean selected, 
			boolean someWhereElseSelected, double support) {
		
		this.someWhereElseSelected = someWhereElseSelected;
		this.selected = selected;
		
		/*
		 * Create a new Border
		 */
		ContigBorder border = new ContigBorder(
				isRepetitiv, isReverse, selected, someWhereElseSelected, false);
	
		String contigNameAusChar = contigId; // is going to be shorter, if it is to long
		char[] dst = new char[11];

		/*
		 * If the name of a contig is to big 
		 * for the size of the contig
		 * this is going to handle that.
		 */

		if (contigId.length() > 10 && !(wSize > 140)) {

			contigNameAusChar = "";

			dst[0] = '.';
			dst[1] = ' ';
			dst[2] = '.';
			dst[3] = ' ';
			dst[4] = '.';
			dst[5] = ' ';
			contigId.getChars(contigId.length() - 5, contigId.length(), dst, 6);

			for (int i = 0; i < dst.length; i++) {
				char c = dst[i];
				contigNameAusChar = contigNameAusChar + c;
			}
		}

		/*
		 * Tooltips for displaying some further informations
		 */
		if (support <= 0) {
			this.setToolTipText("<html><font size = -2><u>" + contigId + "</u>"
					+ "<br>length:" + size + " b <br>"
					+ "support: is not available for this contig <br>"
					+ "</html>");
		} else {
			this.setToolTipText("<html><font size = -2><u>" + contigId + "</u>"
					+ "<br>l:" + size + " b <br>" + "support: "
					+ Math.ceil(support) + " <br>"
					+ "</html>");
		}

		/*
		 * Create the text at the panel
		 */
		if (size < 1000) {
			contigLabel.setText("<html><font size = -"+textSize+"><u>" + contigNameAusChar + "</u>"
					+ "<br><b>length:" + "&lt; 1" + " kb</b>" + "</html>");
			this.setBorder(border);
			this.setName(contigId);
		} else {
			contigLabel.setText("<html><font size = -"+textSize+"><u>" + contigNameAusChar + "</u>"
					+ "<br><b>length: " + size / 1000 + " kb </b> </html>");
			this.setBorder(border);
			this.setName(contigId);
		}
		add(contigLabel);
		setVisible(true);
	}
	
	public void setRepetitiv(boolean isRepetitiv) {
		this.isRepetitiv = isRepetitiv;
	}


	/*
	 * Necessary if the contigs have to be very small
	 * if the number is high the size will be small.
	 */
	public  void setSizeOfContig(long size, long maxSize,
			long minSize) {
	
		float nenner = (float) (Math.log(size) - Math.log(minSize));
		float zaehler = (float) (Math.log(maxSize) - Math.log(minSize));

		float xInIntervall = nenner / zaehler;
		
		wSize = (int) ((xInIntervall * contigPanelMaxWidth) + contigPanelMinWidth);

		this.setPreferredSize(new Dimension(wSize, contigPanelHeight));
		this.setMaximumSize(new Dimension(wSize, contigPanelHeight));
		this.setMinimumSize(new Dimension(wSize, contigPanelHeight));

	}
	
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}


	public void setContigPanelHeight(int contigPanelHeight) {
		if(contigPanelHeight > 40){
			this.contigPanelHeight = contigPanelHeight;
		}else{
			this.contigPanelHeight = 40;
		}
	}


	public void setContigPanelMinWidth(int contigPanelMinWidth) {
		if(contigPanelMinWidth > 60){
			this.contigPanelMinWidth = contigPanelMinWidth;
		}else{
			this.contigPanelMinWidth = 60;
		}
	}


	public void setContigPanelMaxWidth(int contigPanelMaxWidth) {
		if(contigPanelMaxWidth< 180){
			this.contigPanelMaxWidth = 180;
		}else{
			this.contigPanelMaxWidth = contigPanelMaxWidth;
		}
	}


	/*
	 * this do the background of the panels grey
	 */
	public void highlightOfContigPanel ( boolean highlight){

		ContigBorder border = new ContigBorder(isRepetitiv, isReverse, selected	, someWhereElseSelected	, highlight);
		this.setBorder(border);	
	}

	public AdjacencyEdge getEdge() {
		return edge;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isReverse() {
		return isReverse;
	}
	
	public void setReverse(boolean isReverse) {
		this.isReverse = isReverse;
	}

	public boolean isAnderweitigAusgewaehlt() {
		return someWhereElseSelected;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public boolean isRepetitiv() {
		return isRepetitiv;
	}

}