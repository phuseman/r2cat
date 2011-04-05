package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

/**
 * Erstellt, das Aussehen eines Contigs. D.h. Länge, Orientierung und
 * Beschriftung
 */
public class ContigAppearance extends JPanel {

	private LayoutGraph lGraph;
	private JLabel contigLabel;
	private AdjacencyEdge edge;
	private int i;
	private String contigName;
	private double support;
	private int numberOfNeighbours = 5;
	private boolean isReverse;
	private boolean anderweitigAusgewaehlt;
	private boolean selected;
	private boolean isRepetitiv;
	private ContigBorder border;

	public ContigAppearance() {
		super();
		this.setBackground(Color.WHITE);
		contigLabel = new JLabel();
		this.add(contigLabel);
	}

	/*
	 * This should be used for the central contig 
	 * where we don't need the edge
	 */
	public ContigAppearance(DNASequence node, int indexOfCentralContig,boolean isCurrentContigSelected,
			boolean isCurrentContigReverse, long maxSize, long minSize) {
		this.i = indexOfCentralContig;
		isReverse = isCurrentContigReverse;
		isRepetitiv= node.isRepetitive();
		contigLabel = new JLabel();
		this.setBackground(Color.WHITE);
		this.add(contigLabel);
		this.setName(node.getId());
		setContigAppearance(node.getId(), node.getSize(), new ContigBorder(isRepetitiv,
				isReverse, isCurrentContigSelected, false));
		setSizeOfContig(node.getSize(), maxSize, minSize);
	}


	/*
	 * This is should be used for the neighbours.
	 * The edge is needed.
	 */
	public ContigAppearance(LayoutGraph graph, AdjacencyEdge includingEdge,
			int indexOfNeighbour, boolean sideIsLeft, long maxSize, long minSize, boolean woandersAusgewaehlt) {
		super();

		this.lGraph = graph;
		this.i = indexOfNeighbour;
		this.isReverse = isContigReverse(sideIsLeft, includingEdge, indexOfNeighbour);

		DNASequence contig = lGraph.getNodes().get(i);
		contigLabel = new JLabel();

		this.setBackground(Color.WHITE);
		this.add(contigLabel);
		this.setName(contig.getId());

		selected = false;
		if (includingEdge.isSelected()) {
			selected = true;
		}
		anderweitigAusgewaehlt = false;
		if(!selected && woandersAusgewaehlt){
			anderweitigAusgewaehlt = true;
		}
		
		support = includingEdge.getSupport();

		isRepetitiv = contig.isRepetitive();
		border = new ContigBorder(
				isRepetitiv, isReverse, selected, anderweitigAusgewaehlt);
		setContigAppearance(contig.getId(), contig.getSize(), border);
		setSizeOfContig(contig.getSize(), maxSize, minSize);
	}
	public ContigBorder getBorder() {
		return border;
	}

	public void setBorder(ContigBorder border) {
		this.border = border;
	}

	/*
	 * Figure out, if the contig have to be displayed as reverse or not
	 */
	private boolean isContigReverse( boolean isLeftContig, AdjacencyEdge edge, int indexOfContig ){
		
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
		
		return isContigReverse;
	}


	private synchronized void setContigAppearance(String contigId, long size,
			ContigBorder border) {
		String contigNameAusChar = "";
		char[] dst = new char[numberOfNeighbours + (numberOfNeighbours - 1)];

		contigLabel.setName("contigLabel "+contigId);
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
					+ "<br>length:" + size + " b <br>" + "support: "
					+ Math.ceil(support) + " <br>"
					+ "</html>");
		}

		if (size < 1000) {
			contigLabel.setText("<html><font size = -2><u>" + contigId + "</u>"
					+ "<br>length:" + "&lt; 1" + " kb<br>" + "</html>");
			this.setBorder(border);
			this.setName(contigId);
		} else {
			contigLabel.setText("<html><font size = -2><u>" + contigId + "</u>"
					+ "<br>length: " + size / 1000 + " kb </html>");
			this.setBorder(border);
			this.setName(contigId);
		}
		setVisible(true);
	}

	private synchronized void setSizeOfContig(long size, long maxSize,
			long minSize) {

		float nenner = (float) (Math.log(size) - Math.log(minSize));
		float zaehler = (float) (Math.log(maxSize) - Math.log(minSize));

		float xInIntervall = nenner / zaehler;

		int wSize = (int) ((xInIntervall * 215) + 85);

		this.setPreferredSize(new Dimension(wSize, 50));
		this.setMaximumSize(new Dimension(wSize, 50));
		this.setMinimumSize(new Dimension(wSize, 50));

	}

	public LayoutGraph getlGraph() {
		return lGraph;
	}

	public AdjacencyEdge getEdge() {
		return edge;
	}

	public int getI() {
		return i;
	}

	public String getContigName() {
		return contigName;
	}

	public boolean isReverse() {
		return isReverse;
	}
	
	public boolean isAnderweitigAusgewaehlt() {
		return anderweitigAusgewaehlt;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public boolean isRepetitiv() {
		return isRepetitiv;
	}

}
