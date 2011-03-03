package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
	private double relativeSup;
	private int numberOfNeighbours = 5;
	private boolean isReverse;

	public ContigAppearance() {
		super();
		this.setBackground(Color.WHITE);
		contigLabel = new JLabel();
		this.add(contigLabel);
		// this.setName(contigName);
	}

	/*
	 * Konstruktor, fuer das zentrale Contig, wenn es
	 * zum Beispiel aus der Liste ausgewaehlt wird.
	 */
	public ContigAppearance(DNASequence node, int indexOfCentralContig) {
		this.i = indexOfCentralContig;
		boolean isReverse = false;

		contigLabel = new JLabel();
		this.setBackground(Color.WHITE);
		this.add(contigLabel);
		this.setName(node.getId());
		setContigAppearance(node.getId(), node.getSize(), new ContigBorder(node.isRepetitive(), isReverse, false));
		setSizeOfContig(node.getSize());
	}

	/*
	 * Konstruktor fuer die Nachbarn, hier ist die Kante relevant
	 */
	public ContigAppearance(LayoutGraph graph, AdjacencyEdge includingEdge,
			int indexOfNeighbour, boolean sideIsLeft) {
		super();

		this.lGraph = graph;
		this.i = indexOfNeighbour;
		System.out.println(" ");

		boolean neighbourIsReverse;
		boolean selected = false;
		if (sideIsLeft) {
			System.out.println("linker nachbar ");
			if (includingEdge.geti() == indexOfNeighbour) {
				neighbourIsReverse = includingEdge.isLeftConnectori();
				System.out.println(" i "+includingEdge.getContigi().getId()+" ist zentral j Nachbar "+includingEdge.getContigj().getId()+" " + neighbourIsReverse);
			} else {
				neighbourIsReverse = includingEdge.isLeftConnectorj();
				System.out.println(" j "+includingEdge.getContigj().getId()+"ist zentral i Nachbar " +includingEdge.getContigi().getId()+" " + neighbourIsReverse);

			}
		} else {
			System.out.println("rechter nachbar");
			if (includingEdge.geti() == indexOfNeighbour) {
				neighbourIsReverse = includingEdge.isLeftConnectori();
				System.out.println(" i "+includingEdge.getContigi().getId()+" ist zentral j Nachbar "+includingEdge.getContigj().getId()+" " + neighbourIsReverse);
			} else {
				neighbourIsReverse = includingEdge.isRightConnectorj();
				System.out.println(" j "+includingEdge.getContigj().getId()+"ist zentral i Nachbar " +includingEdge.getContigi().getId()+" " + neighbourIsReverse);

			}
		}
		
		System.out.println(includingEdge + " ist reverse " + neighbourIsReverse);
		System.out.println("  ");
		DNASequence contig = lGraph.getNodes().get(i);

		contigLabel = new JLabel();

		this.setBackground(Color.WHITE);
		this.add(contigLabel);
		this.setName(contig.getId());
		
		if(includingEdge.isSelected()){
			selected = true;
		}

		setContigAppearance(contig.getId(), contig.getSize(), new ContigBorder(contig.isRepetitive(), neighbourIsReverse, selected));
		setSizeOfContig(contig.getSize());
	}

	/*
	 * TODO Koennte ich hier nicht auch die attribute weglassen? Habe da ja fast
	 * nur klassenvariblen und damit sind die auch in dieser Methode zugaenglich
	 */
	private synchronized void setContigAppearance(String contigId, long size,
			ContigBorder border) {
		String contigNameAusChar = "";
		char[] dst = new char[numberOfNeighbours + (numberOfNeighbours - 1)];
		
		contigLabel.setName(contigId);
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
		 * TODO erweitern so viele Informationen wie moeglich in dieses ToolTip
		 * stecken.
		 */
		if (relativeSup <= 0) {
			this.setToolTipText("<html><font size = -2><u>" + contigId + "</u>"
					+ "<br>length:" + size + " b <br>"
					+ "support: is not available for this contig <br>"
					+ "</html>");
		} else {
			this.setToolTipText("<html><font size = -2><u>" + contigId + "</u>"
					+ "<br>length:" + size + " b <br>" + "support: "
					+ Math.ceil(relativeSup * 10000) / 100 + " <br>"
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
	}

	private synchronized void setSizeOfContig(long size) {

		int wSize = (int) ((0.01 * size) / 4);

		// int wSize =(int) Math.log((double)size)*10;
		if (wSize < 85) {
			this.setPreferredSize(new Dimension(85, 50));
			this.setMaximumSize(new Dimension(85, 50));
			this.setMinimumSize(new Dimension(85, 50));
		} else if (wSize > 300) {
			this.setPreferredSize(new Dimension(300, 50));
			this.setMaximumSize(new Dimension(300, 50));
			this.setMinimumSize(new Dimension(300, 50));
		} else {
			this.setPreferredSize(new Dimension(wSize, 50));
			this.setMaximumSize(new Dimension(wSize, 50));
			this.setMinimumSize(new Dimension(wSize, 50));
		}
	}

	
	public synchronized LayoutGraph getlGraph() {
		return lGraph;
	}

	public synchronized AdjacencyEdge getEdge() {
		return edge;
	}

	public synchronized int getI() {
		return i;
	}

	public synchronized String getContigName() {
		return contigName;
	}

	public synchronized boolean isReverse() {
		return isReverse;
	}

}
