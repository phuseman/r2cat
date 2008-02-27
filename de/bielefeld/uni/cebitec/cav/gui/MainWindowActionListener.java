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

import java.awt.Cursor;
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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPosition;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList.NotifyEvent;

/**
 * @author Peter Husemann
 * 
 */
public class MainWindowActionListener implements ActionListener, MouseListener,
		MouseMotionListener, KeyListener, ChangeListener, MouseWheelListener {

	private MainWindow mainWindow;

	private Point pressedCoordinates = new Point();

	private Rectangle vplugVisualRectangle = new Rectangle();

	private Point selectionStart = new Point();

	private Cursor lastDefaultCursor;

	/**
	 * 
	 */
	public MainWindowActionListener(MainWindow window) {
		this.mainWindow = window;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().matches("exit")) {
			System.exit(0);
		}
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
	private Point2D.Double convertMouseEventToCanvasPoint(MouseEvent e) {
		SwingUtilities.convertMouseEvent((JComponent) e.getSource(), e,
				mainWindow.dotPlotVisualisation);

		return convertPointToCanvasPoint(e.getPoint());
	}

	/**
	 * The DotPlotVisualisation is usually transformed by a translation and possibly a
	 * scaling.<br>
	 * This method calculates the inversly transformed point.
	 * 
	 * @param p
	 *            the Point given by a mouse event.
	 * @return the transformed poind in the canvas.
	 */
	private Point2D.Double convertPointToCanvasPoint(Point p) {
		Point2D.Double transformedPoint = new Point2D.Double();
		try {
			mainWindow.dotPlotVisualisation.getAlignmentPositionTransform().inverseTransform(
					p, transformedPoint);

		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		return transformedPoint;
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
		Point2D.Double clickedPoint = convertMouseEventToCanvasPoint(e);

		AlignmentPositionDisplayer smallestap = mainWindow.dotPlotVisualisation
				.getAlignmentPositionDisplayerList()
				.getClosestHit(clickedPoint);

		// mark only if distance is not to far
		if (smallestap.ptLineDist(clickedPoint) < 100) {
			if (!e.isShiftDown() && !e.isControlDown()) {
				// on single click only mark the nearest alignment
				mainWindow.dotPlotVisualisation.getAlignmentPositionDisplayerList().unmakAll();
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
		}
		AlignmentPosition.getAlignmentPositionsList().notifyObservers(
				NotifyEvent.MARK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		mainWindow.dotPlotVisualisation.requestFocusInWindow();
		// System.out.println(e.getComponent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// remember coordinates for scrolling
		pressedCoordinates = e.getPoint();
		vplugVisualRectangle = mainWindow.dotPlotVisualisation.getVisibleRect();
		lastDefaultCursor = mainWindow.dotPlotVisualisation.getCursor();

		if (SwingUtilities.isLeftMouseButton(e)) {
			// remember position for selection
			selectionStart = e.getPoint();
		} else if (SwingUtilities.isRightMouseButton(e)) {
			// change to hand cursor for moving the viewport
			mainWindow.dotPlotVisualisation.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		}
	}

	public void mouseReleased(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) {
			markSelection(e);
		}

		if (lastDefaultCursor != null) {
			mainWindow.dotPlotVisualisation.setCursor(lastDefaultCursor);
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
			mainWindow.dotPlotVisualisation.setCursor(lastDefaultCursor);
			lastDefaultCursor = null;
		}

		// allow selection to top left as well as bottom right direction
		int topX = Math.min(selectionStart.x, e.getPoint().x);
		int topY = Math.min(selectionStart.y, e.getPoint().y);
		int bottomX = Math.max(selectionStart.x, e.getPoint().x);
		int bottomY = Math.max(selectionStart.y, e.getPoint().y);

		// translate the coordinates
		Point2D.Double topLeft = convertPointToCanvasPoint(new Point(topX, topY));
		Point2D.Double bottomRight = convertPointToCanvasPoint(new Point(
				bottomX, bottomY));

		// handle different cases:
		if (!e.isShiftDown() && !e.isControlDown()) {
			// new selection
			mainWindow.dotPlotVisualisation.getAlignmentPositionDisplayerList().markArea(
					topLeft, bottomRight,
					AlignmentPositionDisplayerList.SelectionType.ONLY);
		} else if (e.isShiftDown() && !e.isControlDown()) {
			// add alignments in selection
			mainWindow.dotPlotVisualisation.getAlignmentPositionDisplayerList().markArea(
					topLeft, bottomRight,
					AlignmentPositionDisplayerList.SelectionType.ADD);
		} else if (!e.isShiftDown() && e.isControlDown()) {
			// toggle alignments in selection
			mainWindow.dotPlotVisualisation.getAlignmentPositionDisplayerList().markArea(
					topLeft, bottomRight,
					AlignmentPositionDisplayerList.SelectionType.TOGGLE);
		} else if (e.isShiftDown() && e.isControlDown()) {
			// remove alignments in selected area
			mainWindow.dotPlotVisualisation.getAlignmentPositionDisplayerList().markArea(
					topLeft, bottomRight,
					AlignmentPositionDisplayerList.SelectionType.REMOVE);
		}

		// do not draw the selection rectangle any more
		mainWindow.dotPlotVisualisation.clearSelectionRectangle();
		mainWindow.dotPlotVisualisation.repaint();
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

		mainWindow.dotPlotVisualisation.setSelectionRectangle(new Rectangle(topX, topY,
				bottomX, bottomY));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		// SwingUtilities.convertMouseEvent((JComponent) e.getSource(), e,
		// mainWindow.vplug);
		//        
		// Point2D.Double point = new Point2D.Double();
		// try {
		// mainWindow.vplug.getAlignmentPositionTransform().inverseTransform(
		// e.getPoint(), point);
		// //
		// //
		// System.out.println(mainWindow.vplug.getAlignmentPositionTransform().createInverse());
		//        
		// } catch (NoninvertibleTransformException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		
		//DEBUG
//		mainWindow.vplug.setToolTipText(convertMouseEventToCanvasPoint(e)
//				.toString());
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
		// TODO: Zooming has to be fixed!
		double zoomStep = 0.25;

		SwingUtilities.convertMouseEvent((JComponent) e.getSource(), e,
				mainWindow.dotPlotVisualisation);

		vplugVisualRectangle = mainWindow.dotPlotVisualisation.getVisibleRect();

		double newZoomValue = mainWindow.dotPlotVisualisation.getZoom()
				+ (zoomStep * -e.getWheelRotation());

		// change the slider and the textfield accordingly
		mainWindow.zoomSlider.setValue((int) (newZoomValue * 20));
		mainWindow.zoomValue.setText(Double
				.toString(((int) (newZoomValue * 20) / 20.)));

		// Rectangle bounds = new Rectangle();
		// SwingUtilities.calculateInnerArea(mainWindow.vplug, bounds);
		// System.out.println(bounds);

		// set zoom
		mainWindow.dotPlotVisualisation.setZoom(newZoomValue);

		// SwingUtilities.calculateInnerArea(mainWindow.vplug, bounds);
		// System.out.println(bounds);

		// TODO fix the scroll issue
		int dx = 0;
		int dy = 0;
		if (e.getWheelRotation() < 0) {
			dx = (int) ((e.getX() - vplugVisualRectangle.getCenterX()) * zoomStep);
			dy = (int) ((e.getY() - vplugVisualRectangle.getCenterY()) * zoomStep);

			dx += (int) ((vplugVisualRectangle.getCenterX() * zoomStep) / 2);
			dy += (int) ((vplugVisualRectangle.getCenterY() * zoomStep) / 2);

		} else {
			// scroll out
			dx -= (int) ((vplugVisualRectangle.getCenterX() * zoomStep) / 2);
			dy -= (int) ((vplugVisualRectangle.getCenterY() * zoomStep) / 2);

		}

		// dx += (int)((vplugVisualRectangle.x * zoomStep) / 2);
		// dy += (int)((vplugVisualRectangle.y * zoomStep) / 2);
		// vplugVisualRectangle.width = (int) (vplugVisualRectangle.width *
		// (1.-zoomStep));
		// vplugVisualRectangle.height = (int) (vplugVisualRectangle.height *
		// (1.-zoomStep));

		// vplugVisualRectangle.width -= 1;
		// vplugVisualRectangle.height-= 1;

//		System.out.println(dx + " " + dy + " " + vplugVisualRectangle);
		vplugVisualRectangle.translate(dx, dy);

		//
		// vplugVisualRectangle.setFrameFromCenter(vplugVisualRectangle.getCenterX()+dx,
		// vplugVisualRectangle.getCenterY()+dy,
		// 1,//vplugVisualRectangle.x ,
		// 1//vplugVisualRectangle.y
		// );
		//	
		
		

		//Debugging
		//		System.out.println(dx + " " + dy + " " + vplugVisualRectangle);
//
//		((JPanel) e.getSource()).scrollRectToVisible(vplugVisualRectangle);
//
//		mainWindow.vplug.getGraphics().drawLine(
//				(int) vplugVisualRectangle.getCenterX(),
//				(int) vplugVisualRectangle.getCenterY(), e.getX(), e.getY());
//		mainWindow.vplug.getGraphics().drawRect(vplugVisualRectangle.x,
//				vplugVisualRectangle.y, vplugVisualRectangle.width,
//				vplugVisualRectangle.height);
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
				mainWindow.dotPlotVisualisation.setCursor(CursorFactory
						.createCursor(CursorFactory.CursorType.normal));
			} else if (e.isShiftDown() && !e.isControlDown()) {
				// add alignments
				mainWindow.dotPlotVisualisation.setCursor(CursorFactory
						.createCursor(CursorFactory.CursorType.add));
			} else if (!e.isShiftDown() && e.isControlDown()) {
				// toggle alignments
				mainWindow.dotPlotVisualisation.setCursor(CursorFactory
						.createCursor(CursorFactory.CursorType.toggle));
			} else if (e.isShiftDown() && e.isControlDown()) {
				// remove alignments
				mainWindow.dotPlotVisualisation.setCursor(CursorFactory
						.createCursor(CursorFactory.CursorType.remove));
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		changeSelectionCursor(e);
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	public void stateChanged(ChangeEvent e) {

		// vplugVisualRectangle = mainWindow.vplug.getVisibleRect();

		if (mainWindow.zoomSlider != null && mainWindow.zoomValue != null
				&& e.getSource().equals(mainWindow.zoomSlider)) {
			double zoom = 1.;
			zoom = mainWindow.zoomSlider.getValue() / 20.;
			mainWindow.zoomValue.setText(Double.toString(zoom));
			if (mainWindow.dotPlotVisualisation != null) {
				mainWindow.dotPlotVisualisation.setZoom(zoom);
			}

			// vplugVisualRectangle.setFrameFromCenter(vplugVisualRectangle
			// .getCenterX(), vplugVisualRectangle.getCenterY(), 1, 1);
			// ((JPanel)
			// e.getSource()).scrollRectToVisible(vplugVisualRectangle);

		}

	}

}
