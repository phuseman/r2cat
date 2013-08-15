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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.bielefeld.uni.cebitec.qgram.MatchList.NotifyEvent;
import de.bielefeld.uni.cebitec.r2cat.R2cat;

/**
 * @author Peter Husemann
 * 
 */
public class DotPlotMatchViewerActionListener implements ActionListener,
		MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

	private Point pressedCoordinates = new Point();

	private Rectangle vplugVisualRectangle = new Rectangle();

	private Point selectionStart = new Point();

	private Cursor lastDefaultCursor;

	DotPlotMatchViewer dotPlotMatchViewer;

	private GuiController guiController;

	/**
	 * @param guiController2
	 * @param dotPlotMatchViewer
	 * 
	 */
	public DotPlotMatchViewerActionListener(GuiController guiController,
			DotPlotMatchViewer dotPlotMatchViewer) {
		this.guiController = guiController;
		this.dotPlotMatchViewer = dotPlotMatchViewer;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().matches("exit")) {
			System.exit(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			markNearestPoint(e);
		}
	}

	/**
	 * Computes the nearest Point and marks or unmarks it accordingly to the key
	 * modifier
	 * 
	 * @param e
	 *            Mouse Event which triggered this action
	 */
	private void markNearestPoint(MouseEvent e) {
		Point2D.Double clickedPoint = dotPlotMatchViewer.convertMouseEventToCanvasPoint(e);

		MatchDisplayer smallestap = dotPlotMatchViewer
				.getMatchDisplayerList()
				.getClosestHit(clickedPoint);

		// mark only if distance is not to far
		if (smallestap.ptLineDist(clickedPoint) < 100) {
			if (!e.isShiftDown() && !e.isControlDown()) {
				// on single click only mark the nearest alignment
				dotPlotMatchViewer.getMatchDisplayerList()
						.unmakAll();
				smallestap.setSelected(true);
			} else if (e.isShiftDown() && !e.isControlDown()) {
				// with shift add the nearest alignment to the marked ones
				smallestap.setSelected(true);
			} else if (!e.isShiftDown() && e.isControlDown()) {
				// with control toggle the nearest alignment
				smallestap.switchSelected();
			} else if (e.isShiftDown() && e.isControlDown()) {
				// remove nearest alignment
				smallestap.setSelected(false);
			}
		} else {
			// remove selection if too far away
			dotPlotMatchViewer.getMatchDisplayerList().unmakAll();
		}
		R2cat.dataModelController
				.getMatchesList().markQueriesWithSelectedAps();
		R2cat.dataModelController
				.getMatchesList().notifyObservers(NotifyEvent.MARK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		dotPlotMatchViewer.requestFocusInWindow();
		// System.out.println(e.getComponent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		; // do nothing at the moment
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// remember coordinates for scrolling
		pressedCoordinates = e.getPoint();
		vplugVisualRectangle = dotPlotMatchViewer.getVisibleRect();
		lastDefaultCursor = dotPlotMatchViewer.getCursor();

		if (SwingUtilities.isLeftMouseButton(e)) {
			// remember position for selection
			selectionStart = e.getPoint();
		} else if (SwingUtilities.isRightMouseButton(e)) {
			// change to hand cursor for moving the viewport
			dotPlotMatchViewer.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		}
	}

	public void mouseReleased(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) {
			markSelection(e);
		}

		if (lastDefaultCursor != null) {
			dotPlotMatchViewer.setCursor(lastDefaultCursor);
			lastDefaultCursor = null;
		}

	}

	/**
	 * Marks the selection given by the coordinates of this mouse event and the
	 * coordinates stored in selectionStart.<br>
	 * 
	 * @param e
	 *            Mouse event with the needed coordinates.
	 */
	private void markSelection(MouseEvent e) {
		if (lastDefaultCursor != null) {
			dotPlotMatchViewer.setCursor(lastDefaultCursor);
			lastDefaultCursor = null;
		}

		// allow selection to top left as well as bottom right direction
		int topX = Math.min(selectionStart.x, e.getPoint().x);
		int topY = Math.min(selectionStart.y, e.getPoint().y);
		int bottomX = Math.max(selectionStart.x, e.getPoint().x);
		int bottomY = Math.max(selectionStart.y, e.getPoint().y);

		// translate the coordinates
		Point2D.Double topLeft = dotPlotMatchViewer.convertPointToCanvasPoint(new Point(topX, topY));
		Point2D.Double bottomRight = dotPlotMatchViewer.convertPointToCanvasPoint(new Point(
				bottomX, bottomY));

		// handle different cases:
		if (!e.isShiftDown() && !e.isControlDown()) {
			// new selection
			dotPlotMatchViewer.getMatchDisplayerList().markArea(
					topLeft, bottomRight,
					MatchDisplayerList.SelectionType.ONLY);
		} else if (e.isShiftDown() && !e.isControlDown()) {
			// add alignments in selection
			dotPlotMatchViewer.getMatchDisplayerList().markArea(
					topLeft, bottomRight,
					MatchDisplayerList.SelectionType.ADD);
		} else if (!e.isShiftDown() && e.isControlDown()) {
			// toggle alignments in selection
			dotPlotMatchViewer.getMatchDisplayerList().markArea(
					topLeft, bottomRight,
					MatchDisplayerList.SelectionType.TOGGLE);
		} else if (e.isShiftDown() && e.isControlDown()) {
			// remove alignments in selected area
			dotPlotMatchViewer.getMatchDisplayerList().markArea(
					topLeft, bottomRight,
					MatchDisplayerList.SelectionType.REMOVE);
		}

		// do not draw the selection rectangle any more
		dotPlotMatchViewer.clearSelectionRectangle();
		dotPlotMatchViewer.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {

		// left button: select alignments
		if (SwingUtilities.isLeftMouseButton(e)) {
			drawAlignmentSelectionRectangle(e);
		} else if (SwingUtilities.isRightMouseButton(e)) {
			// right button: move the viewport
			vplugVisualRectangle.translate(pressedCoordinates.x - e.getX(),
					pressedCoordinates.y - e.getY());
			// System.out.println(vplugVisualRectangle);
			((JPanel) e.getSource()).scrollRectToVisible(vplugVisualRectangle);
		}
	}

	/**
	 * Sets a rectangle to be drawn in the DotPlotVisualisation.<br>
	 * The rectangle should aid the selection process.
	 * 
	 * @param e
	 */
	private void drawAlignmentSelectionRectangle(MouseEvent e) {
		changeSelectionCursor(e);

		// get right coordinates regardless where the selection had started
		int topX = Math.min(selectionStart.x, e.getPoint().x);
		int topY = Math.min(selectionStart.y, e.getPoint().y);
		int bottomX = Math.max(selectionStart.x - e.getPoint().x,
				e.getPoint().x - selectionStart.x);
		int bottomY = Math.max(selectionStart.y - e.getPoint().y,
				e.getPoint().y - selectionStart.y);

		dotPlotMatchViewer.setSelectionRectangle(new Rectangle(topX, topY,
				bottomX, bottomY));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
//		 SwingUtilities.convertMouseEvent((JComponent) e.getSource(), e,
//		 dotPlotVisualisation);

		 
//		 Point2D.Double point = new Point2D.Double();
//		 try {
//			 dotPlotVisualisation.getMatchTransform().inverseTransform(
//		 e.getPoint(), point);
//		 //
//		 //
////		 System.out.println(dotPlotVisualisation.getMatchTransform().createInverse());
//		        
//		 } catch (NoninvertibleTransformException e1) {
//		 ; // ignore
//		 }

//		 DEBUG
//		 dotPlotVisualisation.setToolTipText(convertMouseEventToCanvasPoint(e)
//		 .toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		changeDataViewPluginZoom(e);
	}

	/**
	 * Zoom in or out using the mouse wheel.
	 * 
	 * @param e
	 *            mouse wheel event
	 */
	private void changeDataViewPluginZoom(MouseWheelEvent e) {
		double zoomStep = 0.25;

		Rectangle oldViewport = dotPlotMatchViewer.getVisibleRect();
		Dimension oldDimension = dotPlotMatchViewer.getSize();

		double newZoomValue = dotPlotMatchViewer.getZoom()
				+ (zoomStep * -e.getWheelRotation());
		
		//don't allow a shrinking of the view area. normally the view is adjusted to screen witdth 
		if(newZoomValue<1) {
			newZoomValue = 1.;
		}

		// set zoom
		dotPlotMatchViewer.setZoom(newZoomValue);

		
		Rectangle newViewport = dotPlotMatchViewer.getVisibleRect();
		Dimension newDimension = dotPlotMatchViewer.getSize();

		double factorWidth = newDimension.getWidth()
				/ (double) oldDimension.getWidth();
		double factorHeight = newDimension.getHeight()
				/ (double) oldDimension.getHeight();

		Rectangle oldViewportScaled = new Rectangle(
				(int) (oldViewport.x * factorWidth),
				(int) (oldViewport.y * factorHeight),
				(int) (oldViewport.width * factorWidth),
				(int) (oldViewport.height * factorHeight));

		double dx = 0;
		double dy = 0;
		if (e.getWheelRotation() < 0) {
			// zoom in: try to keep the point of the mouse pointer where it is.

			// translate the coordinates to the right compontent
			SwingUtilities.convertMouseEvent((JComponent) e.getSource(), e,
					dotPlotMatchViewer);
			// set the origin of the viewport to the scaled mouse point
			newViewport.setLocation((int) (e.getX() * factorWidth), (int) (e
					.getY() * factorHeight));
			// ...and move back the distance from the mouse point to the old
			dx -= e.getX() - oldViewport.getMinX();
			dy -= e.getY() - oldViewport.getMinY();
		} else {
			// zoom out: keep the center of the new rectangle in the middle

			// base movement of the origin of the new rectangle
			dx = oldViewportScaled.getMinX() - oldViewport.getMinX();
			dy = oldViewportScaled.getMinY() - oldViewport.getMinY();

			// and adjustment to the center of the scaled rectangle
			dx += (oldViewportScaled.getWidth() - oldViewport.getWidth()) / 2.;
			dy += (oldViewportScaled.getHeight() - oldViewport.getHeight()) / 2.;
		}

		// translate the rectangle
		newViewport.translate((int) dx, (int) dy);
		// and move the viewport accordingly
		((JPanel) e.getSource()).scrollRectToVisible(newViewport);
		Object test = e.getSource();

		// change the slider and the textfield
		guiController.getMainWindow().zoomSlider
				.setValue((int) (newZoomValue * 20));
		guiController.getMainWindow().zoomValue.setText(Double
				.toString(((int) (newZoomValue * 20) / 20.)));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		// change the cursor (in selection mode) already when a key is pressed,
		// instead of when a mouse draged event occures
		changeSelectionCursor(e);
	}

	/**
	 * If in selection mode (lastDefaultCursor is not null) this method changes
	 * the cursor. possible cursors are normal, add, toggle and remove
	 * selection.
	 * 
	 * @param e
	 *            the input event upon which the cursor should be changed<br>
	 *            the key medifiers are used to determine the action
	 */
	private void changeSelectionCursor(InputEvent e) {

		// lastDefaultCursur is null, if it is active
		if (lastDefaultCursor != null) {
			// change to a cursor that indicates the action
			if (!e.isShiftDown() && !e.isControlDown()) {
				// new selection
				dotPlotMatchViewer.setCursor(CursorFactory
						.createCursor(CursorFactory.CursorType.normal));
			} else if (e.isShiftDown() && !e.isControlDown()) {
				// add alignments
				dotPlotMatchViewer.setCursor(CursorFactory
						.createCursor(CursorFactory.CursorType.add));
			} else if (!e.isShiftDown() && e.isControlDown()) {
				// toggle alignments
				dotPlotMatchViewer.setCursor(CursorFactory
						.createCursor(CursorFactory.CursorType.toggle));
			} else if (e.isShiftDown() && e.isControlDown()) {
				// remove alignments
				dotPlotMatchViewer.setCursor(CursorFactory
						.createCursor(CursorFactory.CursorType.remove));
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		//remove selection when escape is pressed
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			dotPlotMatchViewer.getMatchDisplayerList()
			.unmakAll();
			R2cat.dataModelController
			.getMatchesList().markQueriesWithSelectedAps();
	R2cat.dataModelController
			.getMatchesList().notifyObservers(NotifyEvent.MARK);
		}

		changeSelectionCursor(e);
	}

	public void keyTyped(KeyEvent e) {
		; // do nothing at the moment
	}

}
