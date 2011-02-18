/***************************************************************************
 *   Copyright (C) 2007 by Peter Husemann                                  *
 *   phuseman  a t  cebitec.uni-bielefeld.de                               *
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

package de.bielefeld.uni.cebitec.r2cat.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Observable;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import de.bielefeld.uni.cebitec.common.MiscFileUtils;
import de.bielefeld.uni.cebitec.qgram.DNASequence;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.qgram.MatchList.NotifyEvent;
import de.bielefeld.uni.cebitec.r2cat.R2cat;

/**
 * This should be an interface for different views on the data. at the moment it
 * is a class which makes one particular visualisation. It will maybe be changed
 * later, that only common stuff comes into this, the rest will be via
 * subclasses.
 * 
 * @author Peter Husemann
 * 
 */
public class DotPlotMatchViewer extends MatchViewerPlugin {

	// constants
	// transparent black
	final private Color alignmentColor = new Color(0f, 0f, 0f, 0.5f);

	// transparent red
	final private Color alignmentColorHighlighted = new Color(1f, 0f, 0f, 0.5f);

	//transparent orange for all matches that belong to a contig where at least one match is selected
	final private Color alignmentSameQuery = new Color(1f, 0.5f, 0f, 0.5f);

	// transparent blue
	final private Color alignmentColorReversed = new Color(0f, 0f, 1f, 0.5f);


	final private int border = 20; // pixel

	// class members

	private MatchList matchList;

	private MatchDisplayerList matchDisplayerList;

	private Graphics2D g2d;

	private double zoom = 1.;

	private boolean antialiasing = true;

	// the transformation which is applied before plotting the alignments.
	// usually a translation ( (0,0) is the bottom left point of the plot) and a
	// scaling (zoom).
	// this variable is remembered to calculate a position in the canvas from a
	// given mousepoint
	private AffineTransform MatchTransform = new AffineTransform();

	// th selection rectangle is drawed whichin the drawComponent method.
	private Rectangle lastSelectionRectangle;

	// the width and height of the canvas
	private int drawingWidth = 0;

	private int drawingHeight = 0;

	// a histogram generated from the alignments
	private double[] histogram = {};

	private boolean drawGrid = true;
	
	private JTextField referenceLabel;
	private JTextField contigsLabel;

	/**
	 * Constructor of the main drawing canvas for the alignments.<br>
	 * Turns on buffering, sets the background and some settings.
	 */
	public DotPlotMatchViewer() {
		super(); // double buffering
		this.setDoubleBuffered(true);

		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(400, 400));
		this.setMinimumSize(new Dimension(100, 100));
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		this.setFocusable(true);
//		   Register to display tooltips
		   ToolTipManager.sharedInstance().registerComponent(this);
		   
		   
		   
		   SpringLayout layout = new SpringLayout();

		   //add a textfield for the contigs name and the reference sequence name
		   referenceLabel = new JTextField("Reference");
		   referenceLabel.setEditable(true); //can be changes
		   referenceLabel.setHorizontalAlignment(JTextField.RIGHT);
		   referenceLabel.setOpaque(false);//sets the background transparent. otherwise some parts of the visualisation would be covered.
		   referenceLabel.setBorder(null); // no boder; integrates better in the dotplot.
		   //
		   //put the reference label bottom right.
		   layout.putConstraint(SpringLayout.WEST, referenceLabel,border,SpringLayout.WEST, this);
		   layout.putConstraint(SpringLayout.EAST, referenceLabel,-border,SpringLayout.EAST, this);
     	   layout.putConstraint(SpringLayout.SOUTH, referenceLabel,0,SpringLayout.SOUTH, this);
		   
		   contigsLabel = new JTextField("Contigs");
		   contigsLabel.setEditable(true);
		   contigsLabel.setOpaque(false);
		   contigsLabel.setBorder(null);
		   //put the contigs label top left
     	   layout.putConstraint(SpringLayout.WEST, contigsLabel,border,SpringLayout.WEST, this);
		   layout.putConstraint(SpringLayout.EAST, contigsLabel,-border,SpringLayout.EAST, this);

		   this.setLayout(layout);
		   this.add(referenceLabel);
		   this.add(contigsLabel);
	}

	/**
	 * Constructor for with a given list of alignment positions.
	 * 
	 * @param alignmentsPositionsList
	 */
	public DotPlotMatchViewer(MatchList ap) {
		this();
		setAlignmentsPositionsList(ap);
		this.setLables();
}

	/**
	 * Set the lables for contigs and reference to the filenames without extension.
	 * If these are not available, set fixed names.
	 */
	private void setLables() {

		String refLabel;
		DNASequence ref = null;
		if (this.matchList.getTargets().size()>0) {
			ref = this.matchList.getTargets().get(0);
		}
		if (ref != null && ref.getFile() != null) {
			refLabel = MiscFileUtils.getFileNameWithoutExtension(ref.getFile());
		} else {
			if (matchList.getStatistics().getNumberOfTargets() <= 2) {
				refLabel = "Reference Sequences";
			} else {
				refLabel = "Reference Genome";
			}
		}
		referenceLabel.setText(refLabel);
		

		
		String contigLabel;
		DNASequence cont = null;
		if (this.matchList.getQueries().size()>0) {
			cont = this.matchList.getQueries().get(0);
		}
		if(cont != null && cont.getFile() != null) {
			contigLabel = MiscFileUtils.getFileNameWithoutExtension(cont.getFile());
		} else {
			contigLabel = "Contigs";
		}
		contigsLabel.setText(contigLabel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin#setAlignmentspositionsList(de.bielefeld.uni.cebitec.cav.datamodel.MatchList)
	 */
	public void setAlignmentsPositionsList(MatchList ap) {
		ap.addObserver(this);
		this.matchList = ap;
		this.matchDisplayerList = new MatchDisplayerList(
				ap);
    this.setLables();
	}

	/**
	 * This method draws the alignment positions. A list of alignments is stored
	 * in the {@link MatchDisplayerList}. An
	 * {@link MatchDisplayer} extends {@link Line2D.Double} which is
	 * a shape. The alignments can so easily be drawn.
	 * 
	 * If the list was not generated yet, this method generates the list from
	 * the {@link MatchList}.
	 * 
	 * To avoid a slow gui outsource longer lasting tasks to a thread: Runnable
	 * doSomething = new Runnable(){ public void run(){
	 * System.out.println("Hello World on " + Thread.currentThread()); } }
	 * SwingUtilities.invokeLater(doSomething);
	 * 
	 * @param g2d
	 *            the graphics object to draw on
	 */
	private void drawMatches(Graphics2D g2d) {

		Color lastColor = g2d.getColor();

		Stroke normal = new BasicStroke(1);
		Stroke marked = new BasicStroke(2);

		Stroke lastStroke = g2d.getStroke();

		g2d.setStroke(normal);

		// draw the alignments. the colors have a alpha channel, so it can be
		// seen, when alignments are overlapping
		for (MatchDisplayer matchDisplayer : matchDisplayerList) {
			g2d.setStroke(normal);
			g2d.setColor(alignmentColor);

			if (!matchDisplayer.isInvisible()) { // draw only visible marked ones
				if (matchDisplayer.isSelected()) { // draw the marked in a different color
					g2d.setStroke(marked);
					g2d.setColor(alignmentColorHighlighted);
				} else {
					g2d.setColor(alignmentColor);
					// draw all non marked
					if (matchDisplayer.isReversed()) {
						// special color for reversed
						g2d.setColor(alignmentColorReversed);
					}
					if (matchDisplayer.getMatch().getQuery().isMarked()) {
						// long size =
						// matchDisplayer.getMatch().getQuery().getSize();
						// long sum =
						// Match.getMatchList().getStatistics().getMaximumQuerySize();
						// g2d.setColor(Color.getHSBColor(((float)size/sum),
						// 1f, 1f));
						g2d.setColor(alignmentSameQuery);
						g2d.setStroke(marked);
					}
				}

			}

			g2d.draw(matchDisplayer);
		} // for matchDisplayer

		// restore g2d object
		g2d.setStroke(lastStroke);
		g2d.setColor(lastColor);

	}

	/**
	 * Draws horizontal and vertical bars for the beginning of each sequence.
	 * E.g. every contig in the y axis is separated by a light gray separation
	 * mark.
	 * 
	 * @param g2d
	 */
	private void drawGrid(Graphics2D g2d) {

		if (drawGrid && matchDisplayerList.getDisplayOffsets()) {

			Color last = g2d.getColor();
			g2d.setColor(Color.LIGHT_GRAY);

			Line2D.Double separator;
			// horizontal
			double horizontalOffset = 0;

			if (drawingWidth <= 0) {
				drawingWidth = this.getParent().getWidth() - 2 * border;
			}

			for (DNASequence q : this.matchList.getQueries()) {
				horizontalOffset = (q.getOffset() * MatchDisplayer
						.getNormalisationFactorY());
				separator = new Line2D.Double(0, -horizontalOffset,
						drawingWidth, -horizontalOffset);
				g2d.draw(separator);
			}

			double verticalOffset = 0;
			// vertical
			for (DNASequence t : this.matchList.getTargets()) {
				verticalOffset = (t.getOffset() * MatchDisplayer
						.getNormalisationFactorX());
				separator = new Line2D.Double(verticalOffset, 0,
						verticalOffset, -this.getHeight() + 2 * border);
				g2d.draw(separator);
			}

			g2d.setColor(last);
		}
	}


	/**
	 * Draws a coordinate system.
	 * 
	 * @param g2d
	 *            the graphics object to draw on
	 */
	private void drawCoordinateSystem(Graphics2D g2d) {

		// TODO extend this, so it draws axis labels 

		Color last = g2d.getColor();
		g2d.setColor(Color.BLACK);

		// y axis
		g2d.drawLine(0, 0, 0, -this.getHeight() + 2 * border);
		// xaxis
		g2d.drawLine(0, 0, drawingWidth, 0);
		g2d.setColor(last);
	}

	/**
	 * Draws a histogram of the alignments below the x-axis.<br>
	 * The darker the color the more overlapping are the alignments.<br>
	 * Special case: When there is no overlap the color will be red.
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
			histogram = matchDisplayerList
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
		MatchTransform.setToIdentity();
		// translate (0,0) origin to the left bottom corner, without border
		MatchTransform.translate(border, this.getHeight() - border);
		// and zoom the canvas to the desired zoom level
		MatchTransform.scale(zoom, zoom);

		// apply to graphics context
		g2d.transform(MatchTransform);

		// create displayer list if necessary
		// must be done before drawGrid() because this needs the normalisation
		// factors
		createMatchDisplayerList();

		drawGrid(g2d);

		if (antialiasing) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		}

		drawMatches(g2d);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		drawHistogram(g2d);

		drawCoordinateSystem(g2d);

		g2d.dispose();
	}

	/**
	 * Creates the list of MatchDisplayers if necessary.
	 */
	private void createMatchDisplayerList() {
		if (!matchDisplayerList.isGenerated()) {
			if (drawingWidth <= 0 || drawingHeight <= 0) {
				drawingWidth = this.getParent().getWidth() - 2 * border;
				drawingHeight = this.getParent().getHeight() - 2 * border;

			}
			matchDisplayerList
					.generateMatchDisplayerList(drawingWidth,
							drawingHeight);
		}

		if (matchDisplayerList.needsRegeneration()) {
			matchDisplayerList.regenerate();
		}
	}

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
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin#update(java.util.Observable,
	 *      java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (arg == null) {
			this.repaint();
		} else {
			MatchList.NotifyEvent action = (MatchList.NotifyEvent) arg;

			if (action == NotifyEvent.MARK) {
				this.repaint();
			} else if (action == NotifyEvent.HIDE) {
				; // todo: not implemented yet
			} else if (action == NotifyEvent.CHANGE) {
				// if new data are loaded the MatchList will not
				// be a new object to keep the observers (table and
				// visualisation)
				// ut one has to
				// empty diagonal line segments, so that they will be generated
				// on the next draw call
				matchDisplayerList.clear();
				// reset histogramm so that it will be recomputed with repaint()
				histogram = new double[0];
				
				//set the lables for contigs and references
				this.setLables();
				this.repaint();
			} else if (action == NotifyEvent.ORDER_CHANGED_OR_CONTIG_REVERSED) {
				matchDisplayerList.regenerate();
				this.repaint();
			}

			// for all cases
			this.revalidate();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin#setZoom(double)
	 */
	public void setZoom(double zoom) {
		if (zoom != this.zoom) {
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
					Dimension d = new Dimension(
							(int) ((innerArea.width) * zoom),
							(int) ((innerArea.height) * zoom));

					this.setSize(d);
					this.revalidate();
				}

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
	 * @return the MatchTransform
	 */
	public AffineTransform getMatchTransform() {
		return MatchTransform;
	}

	/**
	 * Gives the view of the list of matches.
	 * 
	 * @return
	 */
	public MatchDisplayerList getMatchDisplayerList() {
		return matchDisplayerList;
	}

  	/**
	 * Gives the list matches.
	 *
	 * @return
	 */
	public MatchList getMatchList() {
		return matchList;
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
    if (this.getParent() != null && this.getParent().isVisible()) {
      int width = this.getParent().getWidth() - 2 * border;
      int heigth = this.getParent().getHeight() - 2 * border;

      if (width != drawingWidth || heigth != drawingHeight) {
        drawingHeight = heigth;
        drawingWidth = width;

        if (matchDisplayerList.isEmpty()) {
          matchDisplayerList.generateMatchDisplayerList(drawingWidth,
                  drawingHeight);
        } else {
          matchDisplayerList.rescaleMatchDisplayerList(drawingWidth,
                  drawingHeight);
        }

        histogram = matchDisplayerList.getTargetHistogram(drawingWidth);

        this.repaint();
      }
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

	/**
	 * @param drawGrid
	 *            the drawGrid to set
	 */
	public void drawGrid(boolean drawGrid) {
    if(R2cat.preferences != null){
      		R2cat.preferences.setDisplayGrid(drawGrid);
    }
		this.drawGrid = drawGrid;
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
	 * 
	 * Write the contig name and the target name as tooltip
	 */
	@Override
	public String getToolTipText(MouseEvent e) {
		String tooltip = null;
		if (this.getMatchDisplayerList().getDisplayOffsets()) {
			// get the mouse coordinates on the canvas
			Point2D.Double clicked = convertMouseEventToCanvasPoint(e);
			double x = clicked.x;
			double y = -clicked.y;

			double lower = 0;
			double upper = 0;

			// check which contig we are pointing at
			String contig = "";

			for (DNASequence q : this.matchList.getQueries()) {

				lower = (q.getOffset() * MatchDisplayer
						.getNormalisationFactorY());
				upper = ((q.getOffset() + q.getSize()) * MatchDisplayer
						.getNormalisationFactorY());

				if (y >= lower && y < upper) {
					contig = "Contig: " + q.getId() + (q.isReverseComplemented()?" (reverse complemented)":"");
					break;
				}
			}

			// check on which reference sequence we are
			String reference = "";
			for (DNASequence t : this.matchList.getTargets()) {

				lower = (t.getOffset() * MatchDisplayer
						.getNormalisationFactorX());
				upper = ((t.getOffset() + t.getSize()) * MatchDisplayer
						.getNormalisationFactorX());

				if (x >= lower && x < upper) {
					reference = "Reference: " + t.getId();
					break;
				}
			}

			// if both (contig and reference) are given separate them with a
			// newline
			String separator = "";
			if (!contig.isEmpty() && !reference.isEmpty()) {
				separator = "<br>";
			}

			
			//sanity check; truncate if the sequence names are too big
			if (contig.length()>100){
				contig = contig.substring(0, 97)+"...";
			}
			if (reference.length()>100){
				reference = contig.substring(0, 97)+"...";
			}

			//if contig or reference is given, set tooltip. else it is null
			if (!contig.isEmpty() || !reference.isEmpty()) {
			tooltip = "<html>" + contig + separator + reference;
			}
			
		}
		return tooltip;
	}

  /*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#createToolTip() 
	 * modify tool tips back and fg color
	 */
	public JToolTip createToolTip() {
		JToolTip tip = super.createToolTip();
		tip.setBackground(Color.WHITE);
		tip.setForeground(Color.BLACK);
		return tip;
	}

	/**
	 * Maps the mouse coordinates of a given event to the coordinates inside the
	 * DotPlotVisualisation.<br>
	 * This way alignments near to this point can be found.
	 * 
	 * @param e
	 *            Mouse Event to map.
	 * @return point coordinates inside the canvas.
	 */
	public Point2D.Double convertMouseEventToCanvasPoint(MouseEvent e) {
		SwingUtilities.convertMouseEvent((JComponent) e.getSource(), e,
				this);
	
		return convertPointToCanvasPoint(e.getPoint());
	}

	/**
	 * The DotPlotVisualisation is usually transformed by a translation and
	 * possibly a scaling.<br>
	 * This method calculates the inversly transformed point.
	 * 
	 * @param p
	 *            the Point given by a mouse event.
	 * @return the transformed point in the canvas.
	 */
	public Point2D.Double convertPointToCanvasPoint(Point p) {
		Double transformedPoint = new Double();
		try {
			getMatchTransform()
					.inverseTransform(p, transformedPoint);
	
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		return transformedPoint;
	}
    
    
    
}
