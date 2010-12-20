package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;

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
	private double[] supportleft;

	public GlassPaneWithLines() {
		super();
	}

	/*
	 * relative support noch hinzufuegen
	 * diesen dann nutzen um die dicke der Kante zu veraendern
	 * 
	 * TODO diese Klasse muss noch variabler gestaltet
	 * und umstukturiert werden.
	 */
	public void setLine(JPanel neigbourContainerleft, JPanel neigbourContainer,
			JPanel centralContig, Boolean flag1, boolean flag2, double[] supportOfEachContigleft,double[] supportOfEachContig) {

		neighbour = neigbourContainer;
		neighbourLeft = neigbourContainerleft;
		b2 = centralContig;
		rightflag = flag1;
		leftflag = flag2;
		supportleft = supportOfEachContigleft;
		support = supportOfEachContig;
		repaint();
	}

	@Override
	public void paintComponent(Graphics gr) {
		
		try{
		super.paintComponent(gr);

		if (rightflag && leftflag) {
			Graphics2D g = (Graphics2D) gr;
			
			/*
			 * Damit die Kanten weich gezeichnet werden.
			 */
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);

			centralContig = b2.getLocationOnScreen();
			int laenge2 = (int) b2.getSize().getWidth();
			int[] componentenArray = { 0, 2, 4, 6, 8 }; // TODO s.o.

			/*
			 * For wird hier eingesetzt um die unterschiedlichen Container zu beladen
			 * zuerst wird diese Methode mit den rechten nachbarn und dann mit den linken
			 * nachbarn gefüttert.
			 * 
			 * TODO s.o.
			 */
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
					
					if (left) {
						
						lineStroke =(float)Math.ceil(supportleft[i]*10000)/10;
						
						if((float)Math.log1p(lineStroke)+1 <= 0){
							lineStroke = 1.0f;
						}else if ((float)Math.log1p(lineStroke)+1 > 5 ){
							lineStroke = 5.0f;
						}else{
							lineStroke = (float)Math.log1p(lineStroke)+1;
						}
						
						Point point = neighbourLeft.getComponent(
								componentenArray[i]).getLocationOnScreen();
						Component p = neighbourLeft
								.getComponent(componentenArray[i]);
						int laenge = (int) p.getSize().getWidth();
						int x = (int) point.getX();
						int y = (int) point.getY();

						g.setColor(Color.BLACK);
						g.setStroke(new BasicStroke(lineStroke));
						g.drawLine(x + laenge, y, x2, y2);
//						g.drawLine(x + laenge-5, y, x2-5, y2);

					} else {
						lineStroke =(float)Math.ceil(support[i]*10000)/10;

						if((float)Math.log1p(lineStroke)+1 <= 0){
							lineStroke = 1.0f;
							
						}else{
							lineStroke = (float)Math.log1p(lineStroke)+1;
							
						}
						
						Point point = neighbour.getComponent(
								componentenArray[i]).getLocationOnScreen();
						
						int x = (int) point.getX();
						int y = (int) point.getY();

						g.setColor(Color.BLACK);
						g.setStroke(new BasicStroke(lineStroke));
						g.drawLine(x, y, x2, y2);
//						g.drawLine(x-5, y, x2-5, y2);
					}
				}
			}
		}
		}catch(IllegalComponentStateException ex){
			System.out.println("Jetzt war der thread wieder zu früh");
			
			//Thread.sleep(100);
		}
	}
}