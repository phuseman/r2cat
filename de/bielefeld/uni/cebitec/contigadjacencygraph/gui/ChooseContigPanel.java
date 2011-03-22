package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import java.awt.Toolkit;

public class ChooseContigPanel extends JPanel{

	private BoxLayout layout;
	private JPanel leftContainer;
	private JPanel leftRadioButtonContainer;
	private JPanel centerContainer;
	private JPanel rightRadioButtonContainer;
	private JPanel rightContainer;
	private int numberOfNeighbours;
	private Point[] leftComponentPositions;
	private Point[] rightComponentPositions;
	private boolean flag = false;
	private ContigAppearance centralContig;
	private Point[] centralPosition;
	private boolean isZScore;
	private double[] leftSupport;
	private double minSupport;
	private double maxSupport;
	private double[] rightSupport;

	public ChooseContigPanel(int neighboursNumber,
			 boolean zScore, double max, double min){
		
		this.numberOfNeighbours = neighboursNumber;
		this.isZScore = zScore;
		this.maxSupport = max;
		this.minSupport = min;
		
		/*
		 * Dieses Panel enhaelt das Contig das Ausgewaehlt wurde und deren
		 * moegliche Nachbarn
		 */

		this.setName("chooseContigPanel");
		this.setBackground(Color.WHITE);

		layout = new BoxLayout(this, BoxLayout.LINE_AXIS);
		this.setLayout(layout);

		leftContainer = new JPanel();
		leftContainer.setName("leftContainer");
		leftRadioButtonContainer = new JPanel();
		centerContainer = new JPanel();
		rightRadioButtonContainer = new JPanel();
		rightContainer = new JPanel();
		rightContainer.setName("rightContainer");

		BoxLayout leftBoxLayout = new BoxLayout(leftContainer,
				BoxLayout.PAGE_AXIS);
		BoxLayout leftRadioBoxLayout = new BoxLayout(leftRadioButtonContainer,
				BoxLayout.PAGE_AXIS);
		BoxLayout centerBoxLayout = new BoxLayout(centerContainer,
				BoxLayout.PAGE_AXIS);
		BoxLayout rightRadioBoxLayout = new BoxLayout(
				rightRadioButtonContainer, BoxLayout.PAGE_AXIS);
		BoxLayout rightBoxLayout = new BoxLayout(rightContainer,
				BoxLayout.PAGE_AXIS);

		/*
		 * Container for all left neighbors
		 */
		leftContainer.setLayout(leftBoxLayout);
		leftContainer.setOpaque(false);
		leftContainer.setPreferredSize(new Dimension(310, 400));
		leftContainer.setMinimumSize(new Dimension(310, 400));
//		leftContainer.setMaximumSize(new Dimension(310, 400));

		leftRadioButtonContainer.setLayout(leftRadioBoxLayout);
		leftRadioButtonContainer.setOpaque(false);
		leftRadioButtonContainer.setPreferredSize(new Dimension(20, 400));
		leftRadioButtonContainer.setMinimumSize(new Dimension(20, 400));
//		leftRadioButtonContainer.setMaximumSize(new Dimension(20, 400));

		/*
		 * Container for central contig
		 */
		centerContainer.setLayout(centerBoxLayout);
		centerContainer.setOpaque(false);
		centerContainer.setMinimumSize(new Dimension(310, 400));
		centerContainer.setPreferredSize(new Dimension(310, 400));
		centerContainer.setMaximumSize(new Dimension(310, 400));

		/*
		 * Container for all right neigbors
		 */
		rightContainer.setLayout(rightBoxLayout);
		rightContainer.setOpaque(false);
		rightContainer.setPreferredSize(new Dimension(310, 400));
		rightContainer.setMinimumSize(new Dimension(310, 400));
//		rightContainer.setMaximumSize(new Dimension(310, 400));

		rightRadioButtonContainer.setLayout(rightRadioBoxLayout);
		rightRadioButtonContainer.setOpaque(false);
		rightRadioButtonContainer.setPreferredSize(new Dimension(20, 400));
		rightRadioButtonContainer.setMinimumSize(new Dimension(20, 400));
//		rightRadioButtonContainer.setMaximumSize(new Dimension(20, 400));

		/*
		 * Parent Panel for all other the container of all neighbours and
		 * central contig. Used this because so I'm able to change the content
		 * of each container independently.
		 */
		this.add(leftContainer);
		this.add(leftRadioButtonContainer);
		this.add(Box.createHorizontalGlue());
		this.add(centerContainer);
		this.add(Box.createHorizontalGlue());
		this.add(rightRadioButtonContainer);
		this.add(rightContainer);
		updateUI();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		
		Graphics2D g2 = (Graphics2D) g;

		/*
		 * Sicherung der Positionen der Punkte, damit es keine Probleme beim
		 * Zeichnen der Linien gibt.
		 */
		leftComponentPositions = new Point[numberOfNeighbours];
		rightComponentPositions = new Point[numberOfNeighbours];

		/*
		 * Damit die Kanten "weich" gezeichnet werden.
		 */
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		/*
		 * Dieser Teil wird nur dann nicht ausgeführt, wenn die neuen Nachbarn
		 * berechnet werden.
		 */
		if (flag) {
			int laenge2 = (int) centralContig.getSize().getWidth();
			int höhe2 = (int) centralContig.getHeight();

			/*
			 * For wird hier eingesetzt um die beiden Container nacheinander zu
			 * beladen; zuerst wird diese Methode mit den rechten nachbarn und
			 * dann mit den linken nachbarn gefüttert.
			 */
			boolean left = false;
			for (int s = 0; s < 2; s++) {
				if (s == 1) {
					left = true;
				} else if (s == 0) {
					left = false;
				}

				int x2;
				int y2;
				centralPosition = new Point[2];
				if (left) {
					x2 = (int) centralContig.getX()
							+ (int) centralContig.getParent().getX();
					y2 = (int) centralContig.getY()+ höhe2 / 2;// + höhe2 + höhe2 / 2;
					centralPosition[0] = new Point(x2, y2);
				} else {
					y2 = (int) centralContig.getY() + höhe2 / 2;
					x2 = (int) centralContig.getX() + laenge2
							+ (int) centralContig.getParent().getX();
					centralPosition[1] = new Point(x2, y2);
				}

				/*
				 * Berechnen der Linien zwischen den linken Nachbarn und dem
				 * zentralem Contig
				 */
				if (left) {

					int z = 1;
					int zaehler = 0;

					for (Component co : leftContainer.getComponents()) {
						/*
						 * Berechnen der Liniendicke, abhängig davon ob der
						 * Nutzer den relativen oder absoluten Support wählt.
						 */
						float lineStrokeLeft;
						if (isZScore) {
							if (leftSupport[zaehler] > 0) {
								lineStrokeLeft = (float) (leftSupport[zaehler]);// /10.0);
							} else {
								lineStrokeLeft = (float) 0.01;
							}
						} else {
							float nenner = (float) (leftSupport[zaehler] - minSupport);
							float counter = (float) (maxSupport - minSupport);
							float xInIntervall = nenner / counter;

							lineStrokeLeft = (float) ((xInIntervall * 3) + 1);
						}
						z++;
						if (z % 2 == 0) {

							Point point = co.getLocation();
							ContigAppearance test2 = (ContigAppearance)co;
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
									+ (int) (co.getHeight() * 0.5);
							Point currentPoint = new Point(x, y);

							leftComponentPositions[zaehler] = currentPoint;

							if(test2.isAnderweitigAusgewaehlt()){
								float[] dash2 = {30,10};
								g2.setColor(Color.DARK_GRAY);
								g2.setStroke(new BasicStroke(lineStrokeLeft, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash2 , 0));
							}else if(test2.isSelected()){
								g2.setColor(Color.BLACK);
								g2.setStroke(new BasicStroke(lineStrokeLeft));
							}else{
								float[] dash = {2,2};
								g2.setColor(Color.GRAY);
								g2.setStroke(new BasicStroke(lineStrokeLeft, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1 , dash, 0));
							}
							g2.drawLine(x + laenge, y, x2, y2);

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

					for (Component co : rightContainer.getComponents()) {

						float lineStroke;
						if (isZScore) {
							if (rightSupport[c] > 0) {
								lineStroke = (float) (rightSupport[c]);// /10.0);
							} else {
								lineStroke = (float) 0.01;
							}
						} else {
							float nenner = (float) (rightSupport[c] - minSupport);
							float counter = (float) (maxSupport - minSupport);
							float xInIntervall = nenner / counter;
							lineStroke = (float) ((xInIntervall * 3) + 1);
						}
						z++;
						if (z % 2 == 0) {

							Point point = co.getLocation();
							ContigAppearance test = (ContigAppearance)co;
							
							/*
							 * Arbeite hier mit verschiedenen Panel. Es ist
							 * nötig sich die richtigen Positionen der Contig
							 * Panel zu berechnen, da sonst die koordinaten in
							 * einer subkomponente auf das ursprungspanel
							 * projiziert wird.
							 */
							int x = (int) co.getParent().getX();
							int y = (int) point.getY()
									+ (int) (0.5 * co.getHeight());
							Point currentPoint = new Point(x, y);

							rightComponentPositions[c] = currentPoint;

							if(test.isAnderweitigAusgewaehlt()){
								float[] dash2 = {30,10};
								g2.setColor(Color.DARK_GRAY);
								g2.setStroke(new BasicStroke(lineStroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash2 , 0));
							}else if(test.isSelected()){
								g2.setColor(Color.BLACK);
								g2.setStroke(new BasicStroke(lineStroke));
							}else{
								float[] dash = {2,2};
								g2.setColor(Color.GRAY);
								g2.setStroke(new BasicStroke(lineStroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1 , dash, 0));
							}
							g2.drawLine(x, y, x2, y2);
							c++;
						}
					}
				}
			}
		}
	}

	public int getNumberOfNeighbours() {
		return numberOfNeighbours;
	}

	public void setNumberOfNeighbours(int numberOfNeighbours) {
		this.numberOfNeighbours = numberOfNeighbours;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean isZScore() {
		return isZScore;
	}

	public void setZScore(boolean isZScore) {
		this.isZScore = isZScore;
	}

	public double[] getLeftSupport() {
		return leftSupport;
	}

	public void setLeftSupport(double[] leftSupport) {
		this.leftSupport = leftSupport;
	}

	public double getMinSupport() {
		return minSupport;
	}

	public void setMinSupport(double minSupport) {
		this.minSupport = minSupport;
	}

	public double getMaxSupport() {
		return maxSupport;
	}

	public void setMaxSupport(double maxSupport) {
		this.maxSupport = maxSupport;
	}

	public JPanel getLeftContainer() {
		return leftContainer;
	}

	public JPanel getLeftRadioButtonContainer() {
		return leftRadioButtonContainer;
	}

	public JPanel getCenterContainer() {
		return centerContainer;
	}

	public JPanel getRightRadioButtonContainer() {
		return rightRadioButtonContainer;
	}

	public JPanel getRightContainer() {
		return rightContainer;
	}

	public Point[] getLeftComponentPositions() {
		return leftComponentPositions;
	}

	public Point[] getRightComponentPositions() {
		return rightComponentPositions;
	}

	public Point[] getCentralPosition() {
		return centralPosition;
	}

	public double[] getRightSupport() {
		return rightSupport;
	}

	public void setRightSupport(double[] rightSupport) {
		this.rightSupport = rightSupport;
	}
	public ContigAppearance getCentralContig() {
		return centralContig;
	}

	public void setCentralContig(ContigAppearance centralContig) {
		this.centralContig = centralContig;
	}

}
