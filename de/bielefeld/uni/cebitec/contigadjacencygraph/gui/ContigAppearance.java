package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;

/**
 * Erstellt, das Aussehen eines Contigs. D.h. Länge, Orientierung und
 * Beschriftung
 */
public class ContigAppearance extends JPanel {

	private JLabel contigLabel;
	private AdjacencyEdge edge;
	private int index;
	private int numberOfNeighbours = 5;
	private boolean isReverse;
	private boolean someWhereElseSelected;
	private boolean selected;
	private boolean isRepetitiv;
	private int textSize = 2;
	private int contigPanelHeight = 50;
	private int contigPanelMinWidth = 70;
	private int contigPanelMaxWidth = 215;

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
			boolean isRepetitiv, boolean selected, 
			boolean someWhereElseSelected, double support) {
		
		this.someWhereElseSelected = someWhereElseSelected;
		this.selected = selected;
		
		/*
		 * Create a new Border
		 */
		ContigBorder border = new ContigBorder(
				isRepetitiv, isReverse, selected, someWhereElseSelected, false);
		
		String contigNameAusChar = "";
		char[] dst = new char[numberOfNeighbours + (numberOfNeighbours - 1)];

		/*
		 * If the name of a contig is to big
		 * this is going to handle that.
		 */
		if (contigId.length() > 10) {
			contigId.getChars(0, 3, dst, 0);

			dst[3] = '.';
			dst[4] = ' ';
			dst[5] = '.';

			contigId.getChars(contigId.length() - 3, contigId.length(), dst, 6);

			for (int i = 0; i < dst.length; i++) {
				char c = dst[i];
				contigNameAusChar = contigNameAusChar + c;
			}
			contigId = contigNameAusChar;
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
			contigLabel.setText("<html><font size = -"+textSize+"><u>" + contigId + "</u>"
					+ "<br><b>length:" + "&lt; 1" + " kb</b>" + "</html>");
			this.setBorder(border);
			this.setName(contigId);
		} else {
			contigLabel.setText("<html><font size = -"+textSize+"><u>" + contigId + "</u>"
					+ "<br><b>length: " + size / 1000 + " kb </b> </html>");
			this.setBorder(border);
			this.setName(contigId);
		}
		add(contigLabel);
		setVisible(true);
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
		
		int wSize = (int) ((xInIntervall * contigPanelMaxWidth) + contigPanelMinWidth);

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
