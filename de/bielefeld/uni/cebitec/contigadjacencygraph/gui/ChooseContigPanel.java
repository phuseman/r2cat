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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class ChooseContigPanel extends JPanel implements MouseListener,
		Observer {

	private final CagCreator model;

	private BoxLayout layout;
	private JPanel leftContainer;
	private JPanel leftRadioButtonContainer;
	private JPanel centerContainer;
	private JPanel rightRadioButtonContainer;
	private JPanel rightContainer;
	private int numberOfNeighbours;
	private Point[] leftComponentPositions;
	private Point[] rightComponentPositions;

	private Point[] centralPosition;
	private boolean isZScore;
	private double[] leftSupport;
	private double[] rightSupport;
	private boolean leftNeigboursReady;
	private boolean centralContigThere;
	private boolean rightNeighboursReady;

	private int centralContigIndex;

	ChooseContigPanel(CagCreator cagModel) {
		this.model = cagModel;
		model.addObserver(this);
		this.addMouseListener(this);
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

		if (leftNeigboursReady && rightNeighboursReady && centralContigThere) {

			ContigAppearance centralContig = null;

			if (centerContainer.getComponent(0) instanceof ContigAppearance) {
				centralContig = (ContigAppearance) centerContainer
						.getComponent(0);
			}
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

 						if (co instanceof ContigAppearance) {
 							ContigAppearance test2 = (ContigAppearance) co;

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
								
								if (Math.log(leftSupport[zaehlerFuerSupport]) > 0
										&& Math
												.log(leftSupport[zaehlerFuerSupport]) < 5) {

									lineStrokeLeft = (float) Math
											.log(leftSupport[zaehlerFuerSupport]);
								} else if (Math
										.log(leftSupport[zaehlerFuerSupport]) < 0.01) {
									lineStrokeLeft = 0.01f;
								} else {
									lineStrokeLeft = 5.0f;
								}
							}

							Point point = co.getLocation();
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
					int c = 0;
					int zaehlerFuerSupport = 0;

					for (Component co : rightContainer.getComponents()) {

						if (co instanceof ContigAppearance) {
 							ContigAppearance test = (ContigAppearance) co;


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
							
								if (Math.log(rightSupport[zaehlerFuerSupport]) > 0
										&& Math
												.log(rightSupport[zaehlerFuerSupport]) < 5) {

									lineStroke = (float) Math
											.log(rightSupport[zaehlerFuerSupport]);
								} else if (Math
										.log(rightSupport[zaehlerFuerSupport]) < 0.01) {
									lineStroke = 0.01f;
								} else {
									lineStroke = 5.0f;
								}
							}

							Point point = co.getLocation();

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

	@Override
	public void update(Observable o, Object arg) {

		if (isZScore != model.isZScore()) {
			this.isZScore = model.isZScore();
		}

		if (numberOfNeighbours != model.getNumberOfNeighbours()
				|| centralContigIndex != model.getCurrentContigIndex()) {
			this.numberOfNeighbours = model.getNumberOfNeighbours();
			updateCentralContig(model.getCurrentContigIndex());
			updateLeftNeighbours();
			updateRightNeighbours();
		}
		
		this.repaint();
	}

	

	private void updateCentralContig(int index) {

		centralContigThere = false;

		JPanel centerContainer = this.centerContainer;

		if (centerContainer.getComponentCount() > 0) {
			centerContainer.removeAll();
		}

		int centralContigIndex = index;
		DNASequence currentContig = model.getGraph().getNodes().get(
				centralContigIndex);
		boolean isReverse = model.isCurrentContigIsReverse();
		boolean isSelected = false;

		if (!model.getSelectedLeftEdges().elementAt(centralContigIndex)
				.isEmpty()
				|| !model.getSelectedRightEdges().elementAt(centralContigIndex)
						.isEmpty()) {
			isSelected = true;
		}

		ContigAppearance centralContig = new ContigAppearance(currentContig,
				centralContigIndex, isSelected, isReverse, model
						.getMaxSizeOfContigs(), model.getMinSizeOfContigs());

		centerContainer.add(centralContig);
		centerContainer.updateUI();
		centralContigThere = true;

	}
	
	private void updateLeftNeighbours() {

		leftNeigboursReady = false;

		Vector<AdjacencyEdge> leftNeighbourEdges = model
				.getCurrentLeftNeighbours();
		ContigAppearance contigPanel = null;

		ContigRadioButton radioButton;
		double[] leftSupport = new double[model.getNumberOfNeighbours()];
		int t = 0;

		JPanel leftContainer = this.leftContainer;
		JPanel leftRadioButtonContainer = this.leftRadioButtonContainer;
		ButtonGroup leftGroup = new ButtonGroup();

		clearComponets(leftContainer, leftRadioButtonContainer);

		/*
		 * The terminator finish the creation of the layout. it has either to be
		 * the number of neighbours or if the number of neighbours, which is
		 * choosed from user is bigger than there are neighbours, it should
		 * finish earlier.
		 */
		int terminator = setTerminator(leftNeighbourEdges);

		boolean isALeftNeighourSelected = false;
		AdjacencyEdge whichNeighbourIsSelected = null;

		/*
		 * Figure out, if there is a neighbour already selected
		 */
		for (AdjacencyEdge e : leftNeighbourEdges) {
			if (e.isSelected()) {
				isALeftNeighourSelected = true;
				whichNeighbourIsSelected = e;
			}
		}
		/*
		 * This is necessary to set the layout of the choosed ContigPanel or
		 * rather for the leftcontainer
		 */
		for (AdjacencyEdge edge : leftNeighbourEdges) {

			if (t < terminator) {

				int indexOfContig = indexOfNeighbourContig(edge);

				/*
				 * Save the support or z-scores here in an array to commit them
				 * to the choose contig panel for setting the linestroke
				 */
				if (model.isZScore()) {
					leftSupport[t] = calculateZScore(edge, model
							.getCurrentContigIndex(), model
							.getMeanForLeftNeigbours(), model
							.getsDeviationsForLeftNeigbours());
				} else {
					leftSupport[t] = edge.getSupport();
				}

				boolean anderweitigAusgewaehlt = ulteriorSelected(true,
						indexOfContig, edge);

				/*
				 * Set the appearance for each contig
				 */
				contigPanel = new ContigAppearance(model.getGraph(), edge,
						indexOfContig, true, model.getMaxSizeOfContigs(), model
								.getMinSizeOfContigs(), anderweitigAusgewaehlt);
				contigPanel.addMouseListener(this);

				/*
				 * The radio Button get commands to differentiate between
				 * adjacencies which are already selected, or selected somewhere
				 * else or not selected
				 */
				radioButton = new ContigRadioButton(edge, contigPanel);

				if (isALeftNeighourSelected) {
					radioButton.setActionCommand("nachbarAusgewaehlt");
					radioButton
							.setSelectedNeighbourOfButtonGroup(whichNeighbourIsSelected);
				} else if (!isALeftNeighourSelected) {
					radioButton
							.setActionCommand("noch kein nachbar ausgewaehlt");
				}

				if (anderweitigAusgewaehlt) {
					radioButton.setActionCommand("anderweitigAusgewaehlt");
					AdjacencyEdge otherEdgeForThisNeighbour = model
							.getSelectedLeftEdges().get(indexOfContig)
							.firstElement();
					radioButton
							.setNeighboursForTheThisNeighbour(otherEdgeForThisNeighbour);
				}
				if (edge.isSelected()) {
					radioButton.setSelected(true);
				}

				radioButton.setLeft(true);
				radioButton.setOpaque(false);
				radioButton.addActionListener(new RadioButtonActionListener(
						model));

				// add here Contigs and RadioButton with dynamic space
				leftContainer.add(contigPanel);

				leftGroup.add(radioButton);
				leftRadioButtonContainer.add(radioButton);

				/*
				 * There will be added some dynamic space
				 */
				if (t < (model.getNumberOfNeighbours() - 1)) {
					leftContainer.add(Box.createVerticalGlue());
					leftRadioButtonContainer.add(Box.createVerticalGlue());
				}
				leftContainer.updateUI();
				leftRadioButtonContainer.updateUI();
				t++;
			}
			if (t == terminator) {
				break;
			}
		}
		leftContainer.add(Box.createVerticalGlue());
		leftRadioButtonContainer.add(Box.createVerticalGlue());
		this.leftSupport = leftSupport;
		leftNeigboursReady = true;

	}


	/*
	 * similar to updateRightNeigbours
	 */
	private void updateRightNeighbours() {

		rightNeighboursReady = false;
		int s = 0;

		Vector<AdjacencyEdge> rightNeighbourEdges = model
				.getCurrentRightNeighbours();
		ContigAppearance contigPanel = null;

		int terminator = setTerminator(rightNeighbourEdges);

		ContigRadioButton radioButton;
		double[] rightSupport = new double[model.getNumberOfNeighbours()];

		
		JPanel rightContainer = this.rightContainer;
		JPanel rightRadioButtonContainer = this.rightRadioButtonContainer;
		ButtonGroup rightGroup = new ButtonGroup();
		clearComponets(rightContainer, rightRadioButtonContainer);

		
		boolean isARightNeighourSelected = false;
		AdjacencyEdge neighbourForThisGroup = null;
	
		for (AdjacencyEdge e : rightNeighbourEdges) {
			if (e.isSelected()) {
				isARightNeighourSelected = true;
				neighbourForThisGroup = e;
			}
		}

		/*
		 * For each adjacency edge here is going to be a contig Panel
		 */
		for (AdjacencyEdge edge : rightNeighbourEdges) {
			if (s < terminator) {

				int indexOfContig = indexOfNeighbourContig(edge);

			
				if (model.isZScore()) {
					rightSupport[s] = calculateZScore(edge, model
							.getCurrentContigIndex(), model
							.getMeanForRightNeigbours(), model
							.getsDeviationsForRightNeigbours());
				} else {
					rightSupport[s] = edge.getSupport();
				}
				
				boolean anderweitigAusgewaehlt = ulteriorSelected(false,
						indexOfContig, edge);

				contigPanel = new ContigAppearance(model.getGraph(), edge,
						indexOfContig, false, model.getMaxSizeOfContigs(),
						model.getMinSizeOfContigs(), anderweitigAusgewaehlt);
				contigPanel.addMouseListener(this);

				radioButton = new ContigRadioButton(edge, contigPanel);

				if (edge.isSelected()) {
					radioButton.setSelected(true);
				}

				if (isARightNeighourSelected) {
					radioButton.setActionCommand("nachbarAusgewaehlt");
					radioButton
							.setSelectedNeighbourOfButtonGroup(neighbourForThisGroup);

				} else if (!isARightNeighourSelected) {
					radioButton
							.setActionCommand("noch kein nachbar ausgewaehlt");
				}
				if (anderweitigAusgewaehlt) {
					radioButton.setActionCommand("anderweitigAusgewaehlt");
					AdjacencyEdge otherEdge = model.getSelectedLeftEdges().get(
							indexOfContig).firstElement();
					radioButton.setNeighboursForTheThisNeighbour(otherEdge);

				}

				radioButton.setNeighbourIndex(indexOfContig);
				radioButton.setCentralIndex(model.getCurrentContigIndex());
				radioButton.setLeft(false);
				radioButton.setOpaque(false);
				radioButton.addActionListener(new RadioButtonActionListener(
						model));

				rightGroup.add(radioButton);
				rightContainer.add(contigPanel);
				rightRadioButtonContainer.add(radioButton);

				if (s < (model.getNumberOfNeighbours() - 1)) {
					rightContainer.add(Box.createVerticalGlue());
					rightRadioButtonContainer.add(Box.createVerticalGlue());
				}
				rightContainer.updateUI();
				rightRadioButtonContainer.updateUI();
				s++;
			}
			if (s == terminator) {
				break;
			}
		}
		rightContainer.add(Box.createVerticalGlue());
		rightRadioButtonContainer.add(Box.createVerticalGlue());
		this.rightSupport = rightSupport;
		rightNeighboursReady = true;

	}

		private int setTerminator(Vector<AdjacencyEdge> neighbourVector) {

		int value = neighbourVector.size();
		if (model.getNumberOfNeighbours() < neighbourVector.size()) {
			value = model.getNumberOfNeighbours();
		} else if (model.getNumberOfNeighbours() > neighbourVector.size()) {
			value = neighbourVector.size();
		}

		return value;
	}

	private double calculateZScore(AdjacencyEdge edge, int centralContigIndex,
			double[] meanForNeighbours, double[] sDeviationForNeighbours) {

		double zScore = 0;

		zScore = (edge.getSupport() - meanForNeighbours[centralContigIndex])
				/ sDeviationForNeighbours[centralContigIndex];

		return zScore;

	}

	private int indexOfNeighbourContig(AdjacencyEdge edge) {

		int index;

		if (edge.geti() == model.getCurrentContigIndex()) {
			index = edge.getj();
		} else {
			index = edge.geti();
		}

		return index;
	}

	private int indexOfCentralContig(AdjacencyEdge edge) {

		int index;

		if (edge.geti() == model.getCurrentContigIndex()) {
			index = edge.geti();
		} else {
			index = edge.getj();
		}

		return index;
	}

	/*
	 * If a not repetitiv contig is used in an another adjacency, the flag will
	 * be set on true.
	 */
	private boolean ulteriorSelected(boolean isLeft, int indexOfNeighbour,
			AdjacencyEdge edge) {
		
		boolean isSelected = false;

		if (isLeft) {
			if (!model.getSelectedLeftEdges().get(indexOfNeighbour).isEmpty()
					&& !model.getGraph().getNodes().get(indexOfNeighbour)
							.isRepetitive()) {

				AdjacencyEdge other = model.getSelectedLeftEdges().get(
						indexOfNeighbour).firstElement();

				int i = indexOfCentralContig(other);
				isSelected = true;
				if (i == model.getCurrentContigIndex()) {
					isSelected = false;
				}

			}
		} else {
			if (!model.getSelectedRightEdges().get(indexOfNeighbour).isEmpty()
					&& !model.getGraph().getNodes().get(indexOfNeighbour)
							.isRepetitive()) {

				AdjacencyEdge other = model.getSelectedRightEdges().get(
						indexOfNeighbour).firstElement();

				int i = indexOfCentralContig(other);
				isSelected = true;

				if (i == model.getCurrentContigIndex()) {
					isSelected = false;
				}
			}
		}

		return isSelected;
	}

	private void clearComponets(JPanel contigContainer,
			JPanel radioButtonContainer) {

		if (contigContainer.getComponentCount() > 0
				|| radioButtonContainer.getComponentCount() > 0) {
			contigContainer.removeAll();
			radioButtonContainer.removeAll();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		/*
		 * If this will be activated the choosed contig will be displayed as
		 * central contig with its neighbours.
		 */
		if (e.getSource() instanceof ContigAppearance) {
			ContigAppearance contigPanel = (ContigAppearance) e.getSource();

			int index = contigPanel.getI();
			boolean currentContigIsReverse = contigPanel.isReverse();

			model.changeContigs(index, currentContigIsReverse);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() instanceof ContigAppearance) {
			ContigAppearance contigPanel = (ContigAppearance) e.getSource();
			contigPanel.highlightOfContigPanel(true);
		}

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (e.getSource() instanceof ContigAppearance) {
			ContigAppearance contigPanel = (ContigAppearance) e.getSource();
			contigPanel.highlightOfContigPanel(false);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Auto-generated method stub
	}

}
