package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GlassPaneWithLines extends JPanel {

	private Point centralContig;
	private JPanel neighbour;
	private JPanel b2;
	private boolean left = false;
	private int x2;
	private int y2;
	private JPanel neighbourLeft;
	private JPanel n;
	private Boolean rightflag;
	private boolean leftflag;
	private float lineStroke;
	private double[] support;

	public GlassPaneWithLines() {
		super();
	}

	/*
	 * relative support noch hinzufuegen
	 * diesen dann nutzen um die dicke der Kante zu veraendern
	 */
	public void setLine(JPanel neigbourContainerleft, JPanel neigbourContainer,
			JPanel centralContig, Boolean flag1, boolean flag2, double[] supportOfEachContig) {

		neighbour = neigbourContainer;
		neighbourLeft = neigbourContainerleft;
		b2 = centralContig;
		rightflag = flag1;
		leftflag = flag2;
		support = supportOfEachContig;
		repaint();
	}

	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);

//		System.out.println("paint wird aufgerufen");
		if (rightflag && leftflag) {
			Graphics2D g = (Graphics2D) gr;
			
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);

			centralContig = b2.getLocationOnScreen();
			int laenge2 = (int) b2.getSize().getWidth();
			int[] componentenArray = { 0, 2, 4, 6, 8 };

			for (int s = 0; s < 2; s++) {
				if (s == 1) {
					left = true;
				}else if(s==0){
					left = false;
				}
				if (left) {
					x2 = (int) centralContig.getX();
					y2 = (int) centralContig.getY();
				} else {
					y2 = (int) centralContig.getY();
					x2 = (int) centralContig.getX() + laenge2;
				}

				for (int i = 0; i < componentenArray.length; i++) {
					
					
					lineStroke =(float)Math.ceil(support[i]*10000)/100;
					System.out.println("i "+ i+ " linien Dicke "+lineStroke);
					//	System.out.println("Wie die berechnung von den Cotigs " + (100 * lineStroke)/2);
					
					
					if (left) {
						
						if((float)Math.log(lineStroke +5) <= 0){
							lineStroke = 1.0f;
							System.out.println("LineStrocke ist eins ");
						}else{
							lineStroke = (float)Math.log(lineStroke+5);
							//lineStroke = lineStroke*100;
							System.out.println("log +5/2" +
									" "+lineStroke);
						}
						Point point = neighbourLeft.getComponent(
								componentenArray[i]).getLocationOnScreen();
						Component p = neighbourLeft
								.getComponent(componentenArray[i]);
						int laenge = (int) p.getSize().getWidth();
						int x = (int) point.getX();
						int y = (int) point.getY();

						g.setColor(Color.BLACK);
						g.setStroke(new BasicStroke(0.1f));
						g.drawLine(x + laenge-5, y, x2-5, y2);
					} else {
					/*	if((float)Math.log(lineStroke +1)/(2) <= 0){
							lineStroke = 1.0f;
							System.out.println("LineStrocke ist eins ");
						}else{
							lineStroke = (float)Math.log1p(lineStroke+1)/2;
							//lineStroke = lineStroke*100;
							System.out.println("log +5/2" +
									" "+lineStroke);
						}*/

						
						Point point = neighbour.getComponent(
								componentenArray[i]).getLocationOnScreen();
						int x = (int) point.getX();
						int y = (int) point.getY();

						g.setColor(Color.BLACK);
						g.setStroke(new BasicStroke(lineStroke));
						g.drawLine(x-5, y, x2-5, y2);

					}
				}
			}
		}
	}
}