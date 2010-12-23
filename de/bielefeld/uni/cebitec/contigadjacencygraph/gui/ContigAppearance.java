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
	private boolean isRepeat= false;
	private boolean isReverse= false; 	
	private boolean isSelected = false;
	private ContigBorder border;
	private double relativeSup;
	
	public ContigAppearance(){
		super();
		this.setBackground(Color.WHITE);
		contigLabel = new JLabel();
		this.add(contigLabel);		
		this.setName(contigName);
	}

	public ContigAppearance(DNASequence contigNode){
		super();
		this.contig=contigNode;
		getDetailsOfContig(contigNode);
		this.setBackground(Color.WHITE);
		contigLabel = new JLabel();
		this.add(contigLabel);
		this.setName(contigName);
		setContigAppearance(contigName, isReverseToString, length, border);
		setSizeOfContig(length);
	}
	
	private  void getDetailsOfContig(DNASequence contigNode){
		
		contigName = contigNode.getId();
		length = contigNode.getSize();
		isRepeat = contigNode.isRepetitive();
		isReverse = contigNode.isReverse();
		isReverseToString = new Boolean(isReverse).toString();
		isSelected = contigNode.isContigIsSelected();
		border = new ContigBorder(isRepeat, isReverse, isSelected);
		relativeSup = contigNode.getSupportComparativeToCentralContig();
			/*TODO hier sollte ich eine Methode aufrufen koennen dir mir den
		 							relativen support des Contigs nennt. 
									Muss ich noch in dem Model einbauen, dass es gespeichert wird.*/
	}
		
	private  void setContigAppearance(String contigId, String isReverseToString,
			long size, ContigBorder border) {
		String contigNameAusChar = "";
		char[] dst = new char[9];
		contigLabel.setName(contigId);
		if (contigId.length() > 10){
			contigId.getChars(0, 3, dst, 0);
			
			dst[3] = '.';
			dst[4] = ' ';
			dst[5] = '.';
			
			contigId.getChars(contigId.length() - 3,	 contigId.length(), dst, 6);
			
			for (int i = 0; i < dst.length; i++) {
				char c = dst[i];				
				contigNameAusChar = contigNameAusChar + c;
			}
			contigId = contigNameAusChar;
		}
		
		/*
		 * TODO erweitern
		 * so viele Informationen wie moeglich in dieses ToolTip stecken.
		 */
		if(relativeSup <= 0){
			this.setToolTipText("<html><font size = -2><u>"
					+ contigId + "</u>" + "<br>length:"
					+ size + " b <br>"
					+ "support: is not available for this contig <br>"
					+ "</html>");
		}else{
				this.setToolTipText("<html><font size = -2><u>"
							+ contigId + "</u>" + "<br>length:"
							+ size + " b <br>"
							+ "support: " + Math.ceil(relativeSup*10000)/100+" <br>"
							+ "</html>");
		}
		
		if(size <1000){			
			contigLabel.setText("<html><font size = -2><u>"
					+ contigId+ "</u>" + "<br>length:"
					+ "&lt; 1" + " kb<br>"
					+	"</html>");
			this.setBorder(border);
			this.setName(contigId);
		}else{
			contigLabel.setText("<html><font size = -2><u>"
					+ contigId + "</u>" + "<br>length: "
					+ size / 1000 + " kb </html>");
			this.setBorder(border);
			this.setName(contigId);
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
