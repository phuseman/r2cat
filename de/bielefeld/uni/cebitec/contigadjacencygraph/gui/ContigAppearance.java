package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class ContigAppearance extends JPanel {
	
	private DNASequence contig;
	private String contigName;
	private String isReverseToString;
	private JLabel contigLabel;
	private long length;
	private boolean isRepeat;
	private boolean isReverse; 	
	private ContigBorder border;
	private double relativeSup;
	
	public ContigAppearance(){
		this.setBackground(Color.WHITE);
		contigLabel = new JLabel();
		this.add(contigLabel);		
		this.setName(contigName);
	}

	public ContigAppearance(DNASequence contigNode){
		getDetailsOfContig(contigNode);
		this.setBackground(Color.WHITE);
		contigLabel = new JLabel();
		this.add(contigLabel);
		this.setName(contigName);
		this.contig = contigNode;
		setContigAppearance(contigName, isReverseToString, length, border);
		setSizeOfContig(length);
	}
	
	private  void getDetailsOfContig(DNASequence contigNode){
		
		contigName = contigNode.getId();
		length = contigNode.getSize();
		isRepeat = contigNode.isRepetitive();
		isReverse = contigNode.isReverse();
		isReverseToString = new Boolean(isReverse).toString();
		border = new ContigBorder(isRepeat, isReverse);
	}
		
	private  void setContigAppearance(String contigId, String isReverseToString,
			long size, ContigBorder border) {		
		
		contigLabel.setName(contigId);
		if(size <1000){			
			contigLabel.setText("<html><font size = -2><u>"
					+ contigId + "</u>" + "<br>length:"
					+ "&lt; 1" + " kb</html>");
			this.setBorder(border);
			this.setName(isReverseToString);
		}else{
			contigLabel.setText("<html><font size = -2><u>"
					+ contigId + "</u>" + "<br>length: "
					+ size / 1000 + " kb </html>");
			this.setBorder(border);
			this.setName(isReverseToString);
		}
	}

	private  void setSizeOfContig(long size) {

		int wSize = (int) ((0.01 * size) / 4);
		
		// int wSize =(int) Math.log((double)size)*10;
		if (wSize < 85) {
			this.setPreferredSize(new Dimension(85, 50));
			this.setMaximumSize(new Dimension(85, 50));
			this.setMinimumSize(new Dimension(85, 50));
		} else if (wSize > 300){
			this.setPreferredSize(new Dimension(300, 50));
			this.setMaximumSize(new Dimension(300, 50));
			this.setMinimumSize(new Dimension(300, 50));
		}else {
			this.setPreferredSize(new Dimension(wSize, 50));
			this.setMaximumSize(new Dimension(wSize, 50));
			this.setMinimumSize(new Dimension(wSize, 50));
		}
	}
	
	
	
}
