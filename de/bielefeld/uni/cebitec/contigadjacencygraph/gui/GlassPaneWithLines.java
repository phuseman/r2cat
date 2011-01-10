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
	private Point[] leftComponentPositions = new Point[numberOfNeighbours];
	private Point[] rightComponentPositions = new Point[numberOfNeighbours];
	private Point[] centralPosition= new Point[2]; 
	
	private Point[] leftPositions = new Point[numberOfNeighbours];
	private Point[] rightPositions = new Point[numberOfNeighbours];
	private Point[] cPosition= new Point[2]; 
	private boolean flag;
	private float[] leftSupport = new float[numberOfNeighbours];
	private float[] rightSupport = new float[numberOfNeighbours];
	private float[] rsupport = new float[numberOfNeighbours];
	private float[] lsupport = new float[numberOfNeighbours];


	public GlassPaneWithLines() {
		super();
	}

	/*
	 * relative support noch hinzufuegen diesen dann nutzen um die dicke der
	 * Kante zu veraendern
	 * 
	 * TODO diese Klasse muss noch variabler gestaltet und umstukturiert werden.
	 */
	public void setLine(JPanel neigbourContainerleft, JPanel neigbourContainer,
			JPanel centralContig, double[] supportOfEachContigleft, double[] supportOfEachContig) {

		neighbour = neigbourContainer;
		neighbourLeft = neigbourContainerleft;
		b2 = centralContig;

		supportleft = supportOfEachContigleft;
		support = supportOfEachContig;
		flag = true;
		repaint();
	}
	

	@Override
	public void paintComponent(Graphics gr) {

		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		/*
		 * Damit die Kanten weich gezeichnet werden.
		 */
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		if (flag){
		try {
			/*
			 * Serialisieren ist keine Lösung!
			 */
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("Positions.ser"));

			int laenge2 = (int) b2.getSize().getWidth();
			int höhe2 = (int) b2.getHeight();

			/*
			 * For wird hier eingesetzt um die unterschiedlichen Container zu
			 * beladen zuerst wird diese Methode mit den rechten nachbarn und
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
					centralPosition[0]=new Point(x2, y2);
				} else {
					y2 = (int) b2.getY() + höhe2;
					x2 = (int) b2.getParent().getX() + (int) b2.getX()
							+ laenge2;
					centralPosition[1]=new Point(x2,y2);
				}

				if (left) {

					int z = 1;
					int zaehler = 0;

					lineStrokeLeft = (float) Math.ceil(supportleft[zaehler] * 10000) / 10;
					
					for (Component co : neighbourLeft.getComponents()) {
						
						z++;
						if (z % 2 == 0) {
							if ((float) Math.log1p(lineStrokeLeft) + 1 <= 0) {
								lineStrokeLeft = 1.0f;
							} else if ((float) Math.log1p(lineStrokeLeft) + 1 > 5) {
								lineStrokeLeft = 5.0f;
							} else {
								lineStrokeLeft = (float) Math
										.log1p(lineStrokeLeft) + 1;
							}
							Point point = co.getLocation();
							/*
							 * Arbeite hier mit verschiedenen Panel.
							 * Es ist nötig sich die richtigen Positionen der Contig Panel
							 * zu berechnen, da sonst die koordinaten in einer subkomponente
							 * auf das ursprungspanel projiziert wird.
							 */
							int laenge = (int) co.getSize().getWidth();
							int x = (int) point.getX();
							int y = (int) point.getY()
									+ (int) co.getParent().getParent().getY()
									+ (int) co.getHeight() / 2;
							Point currentPoint = new Point(x, y);

							leftComponentPositions[zaehler] = currentPoint;
							leftSupport[zaehler]= lineStroke;

							g.setColor(Color.BLACK);
							g.setStroke(new BasicStroke(lineStrokeLeft));
							g.drawLine(x + laenge, y, x2, y2);
							
							zaehler++;
						}
					}

						try {
							os.writeObject(leftComponentPositions);
							os.writeObject(leftSupport);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					
				} else {

					int z = 1;
					int c = 0;

					lineStroke = (float) Math.ceil(support[c] * 10000) / 10;
					for (Component co : neighbour.getComponents()) {
						z++;
						if (z % 2 == 0) {
							if ((float) Math.log1p(lineStroke) + 1 <= 0) {
								lineStroke = 1.0f;
							} else if ((float) Math.log1p(lineStroke) + 1 > 5) {
								lineStroke = 5.0f;
							} else {
								lineStroke = (float) Math.log1p(lineStroke) + 1;

							}
							Point point = co.getLocation();
							/*
							 * Arbeite hier mit verschiedenen Panel.
							 * Es ist nötig sich die richtigen Positionen der Contig Panel
							 * zu berechnen, da sonst die koordinaten in einer subkomponente
							 * auf das ursprungspanel projiziert wird.
							 */
							int x = (int) co.getParent().getX();
							int y = (int) point.getY()
									+ (int) co.getParent().getParent().getY()
									+ (int) co.getHeight() / 2;
							
							Point currentPoint = new Point(x, y);

								rightComponentPositions[c] = currentPoint;
								rightSupport[c]= lineStroke;

							g.setColor(Color.BLACK);
							g.setStroke(new BasicStroke(lineStroke));
							g.drawLine(x, y, x2, y2);
							c++;
						}
					}
					try {
						os.writeObject(rightComponentPositions);
						os.writeObject(rightSupport);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					
				}
			}
			
			try {
				os.writeObject(centralPosition);
				os.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			flag = false;
		} catch (IllegalComponentStateException ex) {
			System.out.println("Jetzt war der thread wieder zu früh");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}else{
			
			try {
				ObjectInputStream os = new ObjectInputStream(new FileInputStream("Positions.ser"));
				rightPositions = (Point[]) os.readObject();
				rsupport = (float[])os.readObject();
				leftPositions = (Point[]) os.readObject();
				lsupport = (float[])os.readObject();
				cPosition = (Point[]) os.readObject();
				os.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int c = 0;
			int xc1 = (int) cPosition[0].getX();
			int yc1 = (int) cPosition[0].getY();
			
			int xc2 = (int) centralPosition[1].getX();
			int yc2 = (int) centralPosition[1].getY();
			
			for (Point point : leftPositions) {
				int x = (int) point.getX();
				int y = (int) point.getY();
				float stroke = lsupport[c];
				System.out.println(stroke);
				g.setColor(Color.BLACK);
				g.setStroke(new BasicStroke(stroke));
				g.drawLine(x, y, xc1, yc1);
				c++;
			}
			c = 0;
			for (Point point : rightPositions) {
				int x = (int) point.getX();
				int y = (int) point.getY();
				float stroke = rsupport[c];
				System.out.println(" 2 "+stroke);
				g.setColor(Color.BLACK);
				g.setStroke(new BasicStroke(stroke));
				g.drawLine(x, y, xc2, yc2);
				c++;
			}
		}
	}
}