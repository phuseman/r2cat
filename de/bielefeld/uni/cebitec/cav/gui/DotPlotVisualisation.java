/***************************************************************************
 *   Copyright (C) 2007 by Peter Husemann                                  *
 *   phuseman@cebitec.uni-bielefeld.de                                     *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sun.java2d.loops.DrawLine;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.DNASequence;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList.NotifyEvent;

/**
 * This should be an interface for different views on the data. at the moment it
 * is a class which makes one particular visualisation. It will maybe be changed
 * later, that only common stuff comes into this, the rest will be via
 * subclasses.
 * 
 * @author Peter Husemann
 * 
 */
public class DotPlotVisualisation extends JPanel implements Observer,
		ComponentListener, DataViewPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7711739793055363113L;

	// constants
	// transparent black
	final private Color alignmentColor = new Color(0f, 0f, 0f, 0.5f);

	// transparent red
	final private Color alignmentColorHighlighted = new Color(1f, 0f, 0f, 0.5f);

	// transparent blue
	final private Color alignmentColorReversed = new Color(0f, 0f, 1f, 0.5f);

	final private Color alignmentSameQuery = new Color(0f, 1f, 0f, 0.5f);

	final private int border = 20; // pixel

	// class members
	private AlignmentPositionsList alignmentsPositionsList;

	private AlignmentPositionDisplayerList alignmentPositionDisplayerList;

	private Graphics2D g2d;

	private double zoom = 1.;

	private boolean antialiasing = true;

	// the transformation which is applied before plotting the alignments.
	// usually a translation ( (0,0) is the bottom left point of the plot) and a
	// scaling (zoom).
	// this variable is remembered to calculate a position in the canvas from a
	// given mousepoint
	private AffineTransform alignmentPositionTransform = new AffineTransform();

	// th selection rectangle is drawed whichin the drawComponent method.
	private Rectangle lastSelectionRectangle;

	// the width and height of the canvas
	private int drawingWidth = 0;

	private int drawingHeight = 0;

	// a histogram generated from the alignments
	private double[] histogram = {};

	/**
	 * Constructor of the main drawing canvas for the alignments.<br>
	 * Turns on buffering, sets the background and some settings.
	 */
	public DotPlotVisualisation() {
		super(true); // double buffering
		this.setDoubleBuffered(true);

		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(400, 400));
		this.setMinimumSize(new Dimension(100, 100));
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		this.setFocusable(true);

	}

	/**
	 * Constructor for with a given list of alignment positions.
	 * 
	 * @param alignmentsPositionsList
	 */
	public DotPlotVisualisation(AlignmentPositionsList ap) {
		this();
		setAlignmentspositionsList(ap);
	}

	/* (non-Javadoc)
	 * @see de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin#setAlignmentspositionsList(de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList)
	 */
	public void setAlignmentspositionsList(AlignmentPositionsList ap) {
		this.alignmentsPositionsList = ap;
		this.alignmentsPositionsList.addObserver(this);
		this.alignmentPositionDisplayerList = new AlignmentPositionDisplayerList(
				ap);
	}

	/**
	 * This method draws the alignment positions. A list of alignments is stored
	 * in the {@link AlignmentPositionDisplayerList}. An
	 * {@link AlignmentPositionDisplayer} extends {@link Line2D.Double} which is
	 * a shape. The alignments can so easily be drawn.
	 * 
	 * If the list was not generated yet, this method generates the list from
	 * the {@link AlignmentPositionsList}.
	 * 
	 * To avoid a slow gui outsource longer lasting tasks to a thread: Runnable
	 * doSomething = new Runnable(){ public void run(){
	 * System.out.println("Hello World on " + Thread.currentThread()); } }
	 * SwingUtilities.invokeLater(doSomething);
	 * 
	 * @param g2d
	 *            the graphics object to draw on
	 */
	private void drawAlignmentPositions(Graphics2D g2d) {

		Color lastColor = g2d.getColor();

		Stroke normal = new BasicStroke(1);
		Stroke marked = new BasicStroke(2);

		Stroke lastStroke = g2d.getStroke();

		g2d.setStroke(normal);

		// create displayer list if necessary
		if (!alignmentPositionDisplayerList.isGenerated()) {
			if (drawingWidth <= 0 || drawingHeight <= 0) {
				drawingWidth = this.getParent().getWidth() - 2 * border;
				drawingHeight = this.getParent().getHeight() - 2 * border;

			}
			alignmentPositionDisplayerList
					.generateAlignmentPositionDisplayerList(drawingWidth,
							drawingHeight);
		}

		// draw the alignments. the colors have a alpha channel, so it can be
		// seen, when alignments are overlapping
		for (AlignmentPositionDisplayer apd : alignmentPositionDisplayerList) {
			g2d.setStroke(normal);
			g2d.setColor(alignmentColor);

			if (!apd.isInvisible()) { // draw only visible marked ones
				if (apd.isSelected()) { // draw the marked in a different color
					g2d.setStroke(marked);
					g2d.setColor(alignmentColorHighlighted);
				} else {
					g2d.setColor(alignmentColor);
					// draw all non marked
					if (apd.isReversed()) {
						// special color for reversed
						g2d.setColor(alignmentColorReversed);
					}
					if (apd.getAlignmentPosition().getQuery().isMarked()) {
						// long size =
						// apd.getAlignmentPosition().getQuery().getSize();
						// long sum =
						// AlignmentPosition.getAlignmentPositionsList().getStatistics().getMaximumQuerySize();
						// g2d.setColor(Color.getHSBColor(((float)size/sum),
						// 1f, 1f));
						g2d.setColor(alignmentSameQuery);
						g2d.setStroke(marked);
					}
				}

			}

			g2d.draw(apd);
		} // for apd

		// restore g2d object
		g2d.setStroke(lastStroke);
		g2d.setColor(lastColor);

	}

	/**
	 * Draws a coordinate system.
	 * 
	 * @param g2d
	 *            the graphics object to draw on
	 */
	private void drawCoordinateSystem(Graphics2D g2d) {

		// TODO extend this, so it draws axis lables

		Color last = g2d.getColor();
		g2d.setColor(Color.BLACK);

		// y axis
		g2d.drawLine(0, 0, 0, -this.getHeight() + 2 * border);
		// xaxis
		g2d.drawLine(0, 0, drawingWidth, 0);

		g2d
				.drawString("Contigs", -(border / 2), -this.getHeight() + 2
						* border);

		String xLabel = "Reference Genome";
		int xLabelSize = SwingUtilities.computeStringWidth(this
				.getFontMetrics(this.getFont()), xLabel);

		g2d.drawString(xLabel, drawingWidth - xLabelSize, border / 2 + 5);

		g2d.setColor(last);
	}

	/**
	 * Draws a histogram of the alignments below the x-axis.<br>
	 * The darker the color the more overlapping are the alignments.<br>
	 * Special case: When tere is no overlap the color will be red.
	 * 
	 * @param g2d
	 *            graphics object to draw on
	 */
	private void drawHistogram(Graphics2D g2d) {

		Color last = g2d.getColor();
		g2d.setColor(Color.BLACK);

		if (drawingWidth <= 0) {
			drawingWidth = this.getParent().getWidth() - 2 * border;
		}
		// create histogramm if not existent
		if (histogram.length <= 2) {
			histogram = alignmentPositionDisplayerList
					.getTargetHistogram(drawingWidth);
		}

		// compute the length one bin should have when drawing
		int binLength = (int) ((double) drawingWidth / histogram.length);

		for (int i = 0; i < histogram.length; i++) {

			if (histogram[i] == 0) {
				g2d.setColor(Color.RED);
			} else {
				// set the color to [white..black] white means no hit, black is
				// the
				// max. number
				g2d.setColor(new Color(Color.HSBtoRGB(0.0f, 0.0f,
						(float) (1. - histogram[i]))));
			}

			g2d.fillRect(i * binLength, 0, binLength, 5);
		}

		// restore the g2d object
		g2d.setColor(last);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		if (isOpaque()) { // paint background
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		// super.paintComponent(g);

		g2d = (Graphics2D) g.create();

		drawSelection(g2d);

		// reset the transformation and
		// save it for later inverse mapping into the alignment positions
		alignmentPositionTransform.setToIdentity();
		// translate (0,0) origin to the left bottom corner, without border
		alignmentPositionTransform.translate(border, this.getHeight() - border);
		// and zoom the canvas to the desired zoom level
		alignmentPositionTransform.scale(zoom, zoom);

		// apply to grapics context
		g2d.transform(alignmentPositionTransform);

		if (antialiasing) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		}

		drawAlignmentPositions(g2d);
		
// TODO: draw for the beginning of each contig a new line
//		drawContigBorders(g2d);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		drawHistogram(g2d);

		drawCoordinateSystem(g2d);

		g2d.dispose();
	}

//	private void drawContigBorders(Graphics2D g2d) {
//				Color last = g2d.getColor();
//				g2d.setColor(Color.BLACK);
//		
//		// TODO values is too slow
//				for (DNASequence contig : alignmentsPositionsList.getQueries().values().iterator()) {
//			if (contig.getOffset() > 0) {
//				int offs = (int) (contig.getOffset()*AlignmentPositionDisplayer.getNormalisationFactorY());
//				System.out.println(offs);
//				g2d.drawLine(0, -10, drawingWidth, -10);
//				g2d.fillOval(100, 10, 10, 10);
//			}
//		}
//
//		g2d.setColor(last);
//
//	}

	/**
	 * Draws a frame, if lastSelectionRectangle is set. This is used when
	 * selecting alignments. The frame will be drawn before the transformation
	 * is applied, so the coordinates are the same as in the window.<br>
	 * 
	 * The frame will be in gray with a black border.<br>
	 * 
	 * The frame can be set with the setSelectiontRectangle method. It will then
	 * be drawn until the clearSelectionRectangle method is called.
	 * 
	 * @param g2d
	 *            the graphics object to draw on
	 */
	private void drawSelection(Graphics2D g2d) {
		if (lastSelectionRectangle != null) {
			Color lastColor = g2d.getColor();
			Composite lastComposite = g2d.getComposite();
			AlphaComposite myAlpha = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.5f);
			g2d.setComposite(myAlpha);

			g2d.setColor(new Color(Color.HSBtoRGB(0.0f, 0.0f, 0.95f)));
			g2d
					.fillRect(lastSelectionRectangle.x,
							lastSelectionRectangle.y,
							lastSelectionRectangle.width,
							lastSelectionRectangle.height);

			g2d.setColor(Color.BLACK);
			g2d.drawRect(lastSelectionRectangle.x, lastSelectionRectangle.y,
					lastSelectionRectangle.width - 1,
					lastSelectionRectangle.height - 1);

			g2d.setColor(lastColor);
			g2d.setComposite(lastComposite);
		}
	}

	/**
	 * Sets the rectangle which should be drawn for the selection process.
	 * 
	 * @param r
	 *            Rectangle to be drawn
	 */
	public void setSelectionRectangle(Rectangle r) {
		Rectangle dirtyArea;
		if (lastSelectionRectangle == null) {
			dirtyArea = r;
		} else {
			dirtyArea = r.union(lastSelectionRectangle);

		}
		this.lastSelectionRectangle = r;
		this.repaint(dirtyArea);

	}

	/**
	 * Clears the rectangle. If called no selection rectangle will be drawn in
	 * the paintComponent method.
	 */
	public void clearSelectionRectangle() {
		if (lastSelectionRectangle != null) {
			this.repaint(lastSelectionRectangle);
		}
		this.lastSelectionRectangle = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 * 
	 * the observed object is the list of alignments
	 */
	/* (non-Javadoc)
	 * @see de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (arg == null) {
			this.repaint();
		} else {

			AlignmentPositionsList.NotifyEvent action = (AlignmentPositionsList.NotifyEvent) arg;

			if (action == NotifyEvent.MARK) {
				;
			} else if (action == NotifyEvent.HIDE) {
				;
			} else if (action == NotifyEvent.CHANGE) {
				alignmentPositionDisplayerList
						.generateAlignmentPositionDisplayerList(drawingWidth,
								drawingHeight);
			}

			// for all cases
			this.revalidate();
		}
	}

	/* (non-Javadoc)
	 * @see de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin#setZoom(double)
	 */
	public void setZoom(double zoom) {
		if (zoom <= 0) {
			this.repaint();
			return;
		} else {
			this.zoom = zoom;

			if (this.getParent() != null && this.getParent().isVisible()) {
				// get the dimension of the parent container
				Rectangle innerArea = new Rectangle();
				SwingUtilities.calculateInnerArea(((JComponent) this
						.getParent()), innerArea);

				// calculate the new dimension of this component
				Dimension d = new Dimension((int) ((innerArea.width) * zoom),
						(int) ((innerArea.height) * zoom));

				this.setSize(d);
				this.revalidate();
			}

		}
	}

	/**
	 * 
	 * @return current zoom factor.
	 */
	public double getZoom() {
		return zoom;
	}

	/**
	 * Returns if anialiasing should be used for the alignents.
	 * 
	 * @return bool antialiasing
	 */
	public boolean isAntialiased() {
		return antialiasing;
	}

	/**
	 * Switches on/off antialiasing for the drawing of the alignments.
	 * 
	 * @param antialiasing
	 *            the antialiasing to set
	 */
	public void setAntialiasing(boolean antialiasing) {
		this.antialiasing = antialiasing;
	}

	/**
	 * The actual transform which is applied before drawing the alignments.
	 * 
	 * @return the alignmentPositionTransform
	 */
	public AffineTransform getAlignmentPositionTransform() {
		return alignmentPositionTransform;
	}

	/**
	 * Gives the list of the AlignmentDisplayer objects.
	 * 
	 * @return
	 */
	public AlignmentPositionDisplayerList getAlignmentPositionDisplayerList() {
		return alignmentPositionDisplayerList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {

		Rectangle innerArea = new Rectangle();
		SwingUtilities.calculateInnerArea(((JComponent) this.getParent()),
				innerArea);

		Dimension d = new Dimension((int) ((innerArea.width) * zoom),
				(int) ((innerArea.height) * zoom));

		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 *      Calculate new alignment drawing positions if the window was resized.
	 */
	public void componentResized(ComponentEvent e) {
		int width = this.getParent().getWidth() - 2 * border;
		int heigth = this.getParent().getHeight() - 2 * border;

		if (width != drawingWidth || heigth != drawingHeight) {
			drawingHeight = heigth;
			drawingWidth = width;

			if (alignmentPositionDisplayerList.isEmpty()) {
				alignmentPositionDisplayerList
						.generateAlignmentPositionDisplayerList(drawingWidth,
								drawingHeight);
			} else {
				alignmentPositionDisplayerList
						.rescaleAlignmentPositionDisplayerList(drawingWidth,
								drawingHeight);
			}

			histogram = alignmentPositionDisplayerList
					.getTargetHistogram(drawingWidth);

			this.repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent e) {
	}

}
