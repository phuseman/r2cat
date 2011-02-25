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
	private JPanel b2;
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

	private boolean flag;
	private float[] leftSupport;
	private float[] rightSupport;
	
	private boolean isRelativeSupport = false;

	public GlassPaneWithLines() {
		super();
	}

	/*
	 * TODO diese Klasse muss noch variabler gestaltet und umstukturiert werden.
	 */
	public void setLine(JPanel neigbourContainerleft, JPanel neigbourContainer,
			JPanel centralContig, double[] supportOfEachContigleft,
			double[] supportOfEachContig) {

		neighbour = neigbourContainer;
		neighbourLeft = neigbourContainerleft;
		b2 = centralContig;

		supportleft = supportOfEachContigleft;
		support = supportOfEachContig;
		
		flag = true;
		repaint();
	}

	public void setFlag(boolean b) {
		flag = b;
	}

	@Override
	public void paintComponent(Graphics gr) {

		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		/*
		 * Sicherung der Positionen der Punkte, damit es keine Probleme 
		 * beim Zeichnen der Linien gibt.
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
		 * Dieser Teil wird nur dann nicht ausgeführt, wenn
		 * die neuen Nachbarn berechnet werden.
		 */
		if (flag) {
			int laenge2 = (int) b2.getSize().getWidth();
			int höhe2 = (int) b2.getHeight();

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
					x2 = (int) b2.getParent().getX() + (int) b2.getX();
					y2 = (int) b2.getY() + höhe2;
					centralPosition[0] = new Point(x2, y2);
				} else {
					y2 = (int) b2.getY() + höhe2;
					x2 = (int) b2.getParent().getX() + (int) b2.getX()
							+ laenge2;
					centralPosition[1] = new Point(x2, y2);
				}
				
				/*
				 * Berechnen der Linien zwischen den linken Nachbarn und
				 * dem zentralem Contig
				 */
				if (left) {
					
					int z = 1;
					int zaehler = 0;

					for (Component co : neighbourLeft.getComponents()) {
						
						/*
						 * Berechnen der Liniendicke, abhängig davon ob der Nutzer den 
						 * relativen oder absoluten Support wählt.
						 */
						if(isRelativeSupport){
							lineStrokeLeft = (float) Math.ceil(supportleft[zaehler] * 10000) / 10;
						}else{
							lineStrokeLeft = (float) Math.log1p(supportleft[zaehler]/1000) +1;
						}
						
						z++;
						if (z % 2 == 0) {
							/*
							 * Begrenze die Liniendicke
							 */
							if (lineStrokeLeft <= 0) {
								lineStrokeLeft = 0.1f;
							} else if (lineStrokeLeft > 5) {
								lineStrokeLeft = 5.0f;
							}
							
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
									+ (int) co.getHeight() / 2;
							Point currentPoint = new Point(x, y);

							leftComponentPositions[zaehler] = currentPoint;
							leftSupport[zaehler] = lineStroke;

							g.setColor(Color.BLACK);
							g.setStroke(new BasicStroke(lineStrokeLeft));
							g.drawLine(x + laenge, y, x2, y2);

							zaehler++;
						}
					}

				} else {
					/*
					 * äquivalent zu oben, 
					 * nur das dies hier für die rechten Nachbarn ist.
					 */
					int z = 1;
					int c = 0;

					for (Component co : neighbour.getComponents()) {
						if(isRelativeSupport){
							lineStroke = (float) Math.ceil(support[c] * 10000) / 10;
						}else{
							lineStroke = (float) Math.log1p(support[c]/1000)+1;
						}
						
						z++;
						if (z % 2 == 0) {
							if (lineStroke <= 0) {
								lineStroke = 1.0f;
							} else if (lineStroke > 5) {
								lineStroke = 5.0f;
							}
							
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
									+ (int) co.getHeight() / 2;

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
	
	
	public void setRelativeSupport(boolean isRelativeSupport) {
		this.isRelativeSupport = isRelativeSupport;
	}

	public void setNumberOfNeighbours(int numberOfNeighbours) {
		this.numberOfNeighbours = numberOfNeighbours;
	}
}