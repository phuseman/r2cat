package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class ChooseContigPanel extends JPanel {

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

	public ChooseContigPanel(int neighboursNumber, boolean zScore, double max,
			double min) {

		this.numberOfNeighbours = neighboursNumber;
		this.isZScore = zScore;
		this.maxSupport = max;
		this.minSupport = min;

	}

	/*
	 * this panel contains the central contig and its neighbours
	 */
	public void createPanel() {

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
		 * Container for all left neighbours
		 */
		leftContainer.setLayout(leftBoxLayout);
		leftContainer.setOpaque(false);
		leftContainer.setPreferredSize(new Dimension(310, 400));
		leftContainer.setMinimumSize(new Dimension(310, 400));

		leftRadioButtonContainer.setLayout(leftRadioBoxLayout);
		leftRadioButtonContainer.setOpaque(false);
		leftRadioButtonContainer.setPreferredSize(new Dimension(20, 400));
		leftRadioButtonContainer.setMinimumSize(new Dimension(20, 400));


		/*
		 * Container for central contig
		 */
		centerContainer.setLayout(centerBoxLayout);
		centerContainer.setOpaque(false);
		centerContainer.setMinimumSize(new Dimension(310, 400));
		centerContainer.setPreferredSize(new Dimension(310, 400));
		centerContainer.setMaximumSize(new Dimension(310, 900));

		/*
		 * Container for all right neigbours
		 */
		rightContainer.setLayout(rightBoxLayout);
		rightContainer.setOpaque(false);
		rightContainer.setPreferredSize(new Dimension(310, 400));
		rightContainer.setMinimumSize(new Dimension(310, 400));

		rightRadioButtonContainer.setLayout(rightRadioBoxLayout);
		rightRadioButtonContainer.setOpaque(false);
		rightRadioButtonContainer.setPreferredSize(new Dimension(20, 400));
		rightRadioButtonContainer.setMinimumSize(new Dimension(20, 400));


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
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Stroke originalStroke = g2.getStroke();
		Color originalColor = g2.getColor();

		/*
		 * save point of positions, so that there are no problems to draw the
		 * lines
		 */
		leftComponentPositions = new Point[numberOfNeighbours];
		rightComponentPositions = new Point[numberOfNeighbours];

		/*
		 * For drawing the edging soft
		 */
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		/*
		 * This part will to run, during the time we get new central and
		 * neigbour contigs
		 */

		if (flag) {

			int laenge2 = (int) centralContig.getSize().getWidth();
			int höhe2 = (int) centralContig.getHeight();

			/*
			 * Using for, so that both container (left and right) will be loaded
			 */
			boolean left = false;
			for (int s = 0; s < 2; s++) {
				if (s == 1) {
					left = true;
				} else if (s == 0) {
					left = false;
				}

				/*
				 * calculate position of central contig
				 */
				int x2;
				int y2;
				centralPosition = new Point[2];
				if (left) {
					x2 = (int) centralContig.getX()
							+ (int) centralContig.getParent().getX();
					y2 = (int) centralContig.getY() + höhe2 / 2;
					centralPosition[0] = new Point(x2, y2);
				} else {
					y2 = (int) centralContig.getY() + höhe2 / 2;
					x2 = (int) centralContig.getX() + laenge2
							+ (int) centralContig.getParent().getX();
					centralPosition[1] = new Point(x2, y2);
				}

				/*
				 * calculate lines between left neigbours and central contig
				 */
				if (left) {

					int z = 1;
					int zaehler = 0;
					int zaehlerFuerSupport = 0;

					/*
					 * the components are our contigs
					 */
					for (Component co : leftContainer.getComponents()) {

						/*
						 * calculation of the line stroke is depend on the user
						 * if the user use z-scores or absolute support
						 */

						z++;
						if (z % 2 == 0) {

							float lineStrokeLeft = 0.01f;
							if (isZScore) {
								if (leftSupport[zaehlerFuerSupport] > 0
										&& leftSupport[zaehlerFuerSupport] < 5) {
									lineStrokeLeft = (float) (leftSupport[zaehlerFuerSupport]);
								} else if (leftSupport[zaehlerFuerSupport] > 5) {
									lineStrokeLeft = 5.0f;
								} else {
									lineStrokeLeft = 0.01f;
								}
							} else {
								/*
								 * normalized absolute support
								 */
								// float nenner = (float)
								// (leftSupport[zaehlerFuerSupport] -
								// minSupport);
								// float counter = (float) (maxSupport -
								// minSupport);
								// float xInIntervall = nenner / counter;
								//
								// lineStrokeLeft = (float) ((xInIntervall * 3)
								// + 1);
								// System.out.println(leftSupport[zaehlerFuerSupport
								// / 1000]);
							/*	if ((leftSupport[zaehlerFuerSupport] / 1000) > 0
										&& (leftSupport[zaehlerFuerSupport] / 1000) < 5) {

									lineStrokeLeft = (float) leftSupport[zaehlerFuerSupport] / 1000;
								} else if ((leftSupport[zaehlerFuerSupport] / 100) < 0.01) {
									lineStrokeLeft = 0.01f;
								} else {
									lineStrokeLeft = 5.0f;
								}*/
								if (Math.log(leftSupport[zaehlerFuerSupport]) > 0
										&& Math.log(leftSupport[zaehlerFuerSupport]) < 5) {

									lineStrokeLeft = (float) Math.log(leftSupport[zaehlerFuerSupport]);
								} else if (Math.log(leftSupport[zaehlerFuerSupport]) < 0.01) {
									lineStrokeLeft = 0.01f;
								} else {
									lineStrokeLeft = 5.0f;
								}
							}

							Point point = co.getLocation();
							ContigAppearance test2 = (ContigAppearance) co;
							/*
							 * working with different panels it is necessary to
							 * calculate the right positions of the contigs on
							 * screen if i only would use the getPoint from the
							 * contig i would get the positions just from a
							 * subpanel but not the right positions on the
							 * chooseContigPanel
							 */
							int laenge = (int) co.getSize().getWidth();
							int x = (int) point.getX();
							int y = (int) point.getY()
									+ (int) (co.getHeight() * 0.5);
							Point currentPoint = new Point(x, y);

							leftComponentPositions[zaehlerFuerSupport] = currentPoint;

							/*
							 * if the contig is "some where else" selected the
							 * line will be dashed with long lines
							 */
							if (test2.isAnderweitigAusgewaehlt()) {
								float[] dash2 = { 30, 10 };
								g2.setColor(Color.DARK_GRAY);
								g2.setStroke(new BasicStroke(lineStrokeLeft,
										BasicStroke.CAP_BUTT,
										BasicStroke.JOIN_MITER, 1, dash2, 0));
								/*
								 * if the contig is selected the line is
								 * continuous
								 */
							} else if (test2.isSelected()) {
								g2.setColor(Color.BLACK);
								g2.setStroke(new BasicStroke(lineStrokeLeft));
								/*
								 * if the contig is not selected jet the line
								 * will be dashed with short lines
								 */
							} else {
								float[] dash = { 2, 2 };
								g2.setColor(Color.GRAY);
								g2.setStroke(new BasicStroke(lineStrokeLeft,
										BasicStroke.CAP_BUTT,
										BasicStroke.JOIN_MITER, 1, dash, 0));
							}
							g2.drawLine(x + laenge, y, x2, y2);
							zaehlerFuerSupport++;
						}
					}
					zaehler++;
					// }
				} else {
					/*
					 * equivalent to top, but for right neighbours
					 */
					int z = 1;
					int c = 0;
					int zaehlerFuerSupport = 0;

					for (Component co : rightContainer.getComponents()) {

						z++;
						if (z % 2 == 0) {

							float lineStroke = 0.01f;
							if (isZScore) {
								if (rightSupport[zaehlerFuerSupport] > 0
										&& rightSupport[zaehlerFuerSupport] < 5) {
									lineStroke = (float) (rightSupport[zaehlerFuerSupport]);
								} else if (rightSupport[zaehlerFuerSupport] > 5) {
									lineStroke = 5.0f;
								} else {
									lineStroke = 0.01f;
								}
							} else {
								// float nenner = (float)
								// (rightSupport[zaehlerFuerSupport] -
								// minSupport);
								// float counter = (float) (maxSupport -
								// minSupport);
								// float xInIntervall = nenner / counter;
								// lineStroke = (float) ((xInIntervall * 3) +
								// 1);
								// System.out.println(rightSupport[zaehlerFuerSupport]/1000);
								/*if ((rightSupport[zaehlerFuerSupport] / 1000) > 0
										&& (rightSupport[zaehlerFuerSupport] / 1000) < 5) {
									lineStroke = (float) rightSupport[zaehlerFuerSupport] / 1000;
								} else if ((rightSupport[zaehlerFuerSupport] / 1000) < 0.01) {
									lineStroke = 0.01f;
								} else {
									lineStroke = 5.0f;
								}*/
								if (Math.log(rightSupport[zaehlerFuerSupport]) > 0
										&& Math.log(rightSupport[zaehlerFuerSupport]) < 5) {

									lineStroke = (float) Math.log(rightSupport[zaehlerFuerSupport]);
								} else if (Math.log(rightSupport[zaehlerFuerSupport]) < 0.01) {
									lineStroke = 0.01f;
								} else {
									lineStroke = 5.0f;
								}
							}

							Point point = co.getLocation();
							ContigAppearance test = (ContigAppearance) co;

							int x = (int) co.getParent().getX();
							int y = (int) point.getY()
									+ (int) (0.5 * co.getHeight());
							Point currentPoint = new Point(x, y);

							rightComponentPositions[zaehlerFuerSupport] = currentPoint;

							if (test.isAnderweitigAusgewaehlt()) {
								float[] dash2 = { 30, 10 };
								g2.setColor(Color.DARK_GRAY);
								g2.setStroke(new BasicStroke(lineStroke,
										BasicStroke.CAP_BUTT,
										BasicStroke.JOIN_MITER, 1, dash2, 0));
							} else if (test.isSelected()) {
								g2.setColor(Color.BLACK);
								g2.setStroke(new BasicStroke(lineStroke));
							} else {
								float[] dash = { 2, 2 };
								g2.setColor(Color.GRAY);
								g2.setStroke(new BasicStroke(lineStroke,
										BasicStroke.CAP_BUTT,
										BasicStroke.JOIN_MITER, 1, dash, 0));
							}
							g2.drawLine(x, y, x2, y2);
							zaehlerFuerSupport++;
						}
					}
					c++;
				}
			}
		}
		g2.setStroke(originalStroke);
		g2.setColor(originalColor);
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
