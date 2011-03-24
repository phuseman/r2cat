package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GlassPaneWithLines extends JPanel {

	private JPanel neighbour;
	private JPanel cContig;
	private boolean left = false;
	private int x2;
	private int y2;
	private JPanel neighbourLeft;
	private float lineStrokeLeft;
	private float lineStroke;
	private double[] support;
	private double[] supportleft;
	private int numberOfNeighbours = 5;

	private Point[] leftComponentPositions;
	private Point[] rightComponentPositions;
	private Point[] centralPosition = new Point[2];

	private boolean flag = false;
	private float[] leftSupport;
	private float[] rightSupport;

	private double maxSupportOfAllEdges;
	private double minSupportOfAllEdges;
	
	private boolean isZScores;

	public GlassPaneWithLines() {
		super();
	}

	/*
	 * TODO diese Klasse muss noch variabler gestaltet und umstukturiert werden.
	 */
//	public void setLine(JPanel neigbourContainerleft, JPanel neigbourContainer,
//			JPanel centralContig, double[] supportOfEachContigleft,
//			double[] supportOfEachContig, double maxSupport, double minSupport, boolean isZScore) {
//
//		neighbour = neigbourContainer;
//		neighbourLeft = neigbourContainerleft;
//		cContig = centralContig;
//
//		supportleft = supportOfEachContigleft;
//		support = supportOfEachContig;
//		
//		maxSupportOfAllEdges = maxSupport;
//		minSupportOfAllEdges = minSupport;
//		
//		isZScores = isZScore;
//		
//		System.out.println("min support in glasspanel "+ minSupport + " maxsupport "+maxSupport);
//		
//		/*double maxLeftSupport = calculateMax(supportOfEachContigleft);
//		double maxRightSupport = calculateMax(supportOfEachContig);
//		if (maxLeftSupport > maxRightSupport) {
//			maxSupportOfAllEdges = maxLeftSupport;
//		} else {
//			maxSupportOfAllEdges = maxRightSupport;
//		}
//
//		double minLeftSupport = calculateMin(supportOfEachContigleft);
//		double minRightSupport = calculateMin(supportOfEachContig);
//		if (minLeftSupport < minRightSupport) {
//			minSupportOfAllEdges = minLeftSupport;
//		} else {
//			minSupportOfAllEdges = minRightSupport;
//		}*/
//
//		flag = true;
//		repaint();
//	}


	public void setFlag(boolean b) {
		flag = b;
	}

	@Override
	public void paintComponent(Graphics gr) {

		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		/*
		 * Sicherung der Positionen der Punkte, damit es keine Probleme beim
		 * Zeichnen der Linien gibt.
		 */
		leftComponentPositions = new Point[numberOfNeighbours];
		rightComponentPositions = new Point[numberOfNeighbours];
		leftSupport = new float[numberOfNeighbours];
		rightSupport = new float[numberOfNeighbours];

		/*
		 * Damit die Kanten "weich" gezeichnet werden.
		 */
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		/*
		 * Dieser Teil wird nur dann nicht ausgeführt, wenn die neuen Nachbarn
		 * berechnet werden.
		 */
		if (flag) {
			int laenge2 = (int) cContig.getSize().getWidth();
			int höhe2 = (int) cContig.getHeight();

			/*
			 * For wird hier eingesetzt um die beiden Container nacheinander zu
			 * beladen; zuerst wird diese Methode mit den rechten nachbarn und
			 * dann mit den linken nachbarn gefüttert.
			 * 
			 * TODO s.o.
			 */
			for (int s = 0; s < 2; s++) {
				if (s == 1) {
					left = true;
				} else if (s == 0) {
					left = false;
				}
				if (left) {
					x2 = (int) cContig.getParent().getX() + (int) cContig.getX();
					y2 = (int) cContig.getY() + höhe2;
					centralPosition[0] = new Point(x2, y2);
				} else {
					y2 = (int) cContig.getY() + höhe2;
					x2 = (int) cContig.getParent().getX() + (int) cContig.getX()
							+ laenge2;
					centralPosition[1] = new Point(x2, y2);
				}

				/*
				 * Berechnen der Linien zwischen den linken Nachbarn und dem
				 * zentralem Contig
				 */
				if (left) {

					int z = 1;
					int zaehler = 0;

					for (Component co : neighbourLeft.getComponents()) {
						/*
						 * Berechnen der Liniendicke, abhängig davon ob der
						 * Nutzer den relativen oder absoluten Support wählt.
						 */
//						System.out.println("support "+supportleft[zaehler]+" zaehler "+ zaehler);
						
						if(isZScores){
							if(supportleft[zaehler] > 0){
									lineStrokeLeft = (float) (supportleft[zaehler]);///10.0);
							}else{
								lineStroke = (float) 0.01;
							}
						}else{
							float nenner = (float) (supportleft[zaehler] - minSupportOfAllEdges);
							float counter = (float) (maxSupportOfAllEdges - minSupportOfAllEdges);
							float xInIntervall = nenner / counter;
	//						System.out.println("x in Intervall "+ xInIntervall);
							lineStrokeLeft = (float) ((xInIntervall * 3) + 1);
						}
//						System.out.println("lininen dicke "+lineStrokeLeft);
						
						/*float nenner = (float) (Math.log(supportleft[zaehler]) - Math
								.log(minSupportOfAllEdges));
						float counter = (float) (Math.log(maxSupportOfAllEdges) - Math
								.log(minSupportOfAllEdges));
						float xInIntervall = nenner / counter;
						System.out.println("x in Intervall "+ xInIntervall);
						lineStrokeLeft = (float) ((xInIntervall * 3.9) + 0.1);
						System.out.println("lininen dicke "+lineStrokeLeft);*/
						z++;
						if (z % 2 == 0) {

							Point point = co.getLocation();
							/*
							 * Arbeite hier mit verschiedenen Panel. Es ist
							 * nötig sich die richtigen Positionen der Contig
							 * Panel zu berechnen, da sonst die koordinaten in
							 * einer subkomponente auf das ursprungspanel
							 * projiziert wird.
							 */
							int laenge = (int) co.getSize().getWidth();
							int x = (int) point.getX();
							int y = (int) point.getY()
									+ (int) co.getParent().getParent().getY()
									+ (int) co.getHeight() ;
							Point currentPoint = new Point(x, y);

							leftComponentPositions[zaehler] = currentPoint;
							leftSupport[zaehler] = lineStrokeLeft;

							g.setColor(Color.BLACK);
							g.setStroke(new BasicStroke(lineStrokeLeft));
							g.drawLine(x + laenge, y, x2, y2);

							zaehler++;
						}
					}

				} else {
					/*
					 * äquivalent zu oben, nur das dies hier für die rechten
					 * Nachbarn ist.
					 */
					int z = 1;
					int c = 0;

					for (Component co : neighbour.getComponents()) {

						if(isZScores){
							if(support[c] > 0){
									lineStroke = (float) (support[c]);///10.0);
							}else{
								lineStroke = (float) 0.01;
							}
						}else{
							float nenner = (float) (support[c] - minSupportOfAllEdges);
							float counter = (float) (maxSupportOfAllEdges - minSupportOfAllEdges);
							float xInIntervall = nenner / counter;
							lineStroke = (float) ((xInIntervall * 3) + 1);
						}
						z++;
						if (z % 2 == 0) {

							Point point = co.getLocation();
							/*
							 * Arbeite hier mit verschiedenen Panel. Es ist
							 * nötig sich die richtigen Positionen der Contig
							 * Panel zu berechnen, da sonst die koordinaten in
							 * einer subkomponente auf das ursprungspanel
							 * projiziert wird.
							 */
							int x = (int) co.getParent().getX();
							int y = (int) point.getY()
									+ (int) co.getParent().getParent().getY()
									+ (int) co.getHeight() ;

							Point currentPoint = new Point(x, y);

							rightComponentPositions[c] = currentPoint;
							rightSupport[c] = lineStroke;

							g.setColor(Color.BLACK);
							g.setStroke(new BasicStroke(lineStroke));
							g.drawLine(x, y, x2, y2);
							c++;
						}
					}
				}
			}
		}

	}

	public void setNumberOfNeighbours(int numberOfNeighbours) {
		this.numberOfNeighbours = numberOfNeighbours;
	}

	public double getMaxSupportOfAllEdges() {
		return maxSupportOfAllEdges;
	}

	public double getMinSupportOfAllEdges() {
		return minSupportOfAllEdges;
	}

	
//	private double calculateMax(double[] support) {
//		double max = 0;
//
//		for (double d : support) {
//			if (d > max) {
//				max = d;
//			}
//		}
//		return max;
//	}
//
//	private double calculateMin(double[] support) {
//		double min = support[0];
//
//		for (double d : support) {
//			if (d < min) {
//				min = d;
//			}
//		}
//		return min;
//	}

}