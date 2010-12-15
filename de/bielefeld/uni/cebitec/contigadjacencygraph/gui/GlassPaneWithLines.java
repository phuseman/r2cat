package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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

	public GlassPaneWithLines() {
		super();
	}

	/*
	 * relative support noch hinzufuegen
	 * diesen dann nutzen um die dicke der Kante zu veraendern
	 */
	public void setLine(JPanel neigbourContainerleft, JPanel neigbourContainer,
			JPanel centralContig, Boolean flag1, boolean flag2) {

		neighbour = neigbourContainer;
		neighbourLeft = neigbourContainerleft;
		b2 = centralContig;
		rightflag = flag1;
		leftflag = flag2;
		repaint();
	}

	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);

//		System.out.println("paint wird aufgerufen");
		if (rightflag && leftflag) {
			Graphics2D g = (Graphics2D) gr;

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
					if (left) {
						Point point = neighbourLeft.getComponent(
								componentenArray[i]).getLocationOnScreen();
						Component p = neighbourLeft
								.getComponent(componentenArray[i]);
						int laenge = (int) p.getSize().getWidth();
						int x = (int) point.getX();
						int y = (int) point.getY();

						g.setColor(Color.BLACK);
						g.setStroke(new BasicStroke(1.0f));
						g.drawLine(x + laenge-5, y, x2-5, y2);
					} else {

						
						Point point = neighbour.getComponent(
								componentenArray[i]).getLocationOnScreen();
						int x = (int) point.getX();
						int y = (int) point.getY();

						g.setColor(Color.BLACK);
						g.setStroke(new BasicStroke(1.0f));
						g.drawLine(x-5, y, x2-5, y2);

					}
				}
			}

		}

	}
}