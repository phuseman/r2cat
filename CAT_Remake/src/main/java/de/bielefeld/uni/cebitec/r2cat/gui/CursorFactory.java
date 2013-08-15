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
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;

/**
 * @author Peter Husemann
 * 
 */
public class CursorFactory {

	/**
	 * Typ of the cursor to generate.<br>
	 * <ul>
	 * <li>normal is a normal selection cursor
	 * <li>add has an additional + sign
	 * <li>remove: - sign
	 * <li>toggle has a pictogamm which should look like a toggle action (two
	 * arrows)
	 * </ul>
	 * 
	 * @author Peter Husemann
	 */
	public static enum CursorType {
		normal, add, remove, toggle
	};

	/**
	 * Constructor. Does nothing.
	 */
	public CursorFactory() {
		;
	}

	/**
	 * Factory method. Returns the cursor given by the cursor argument. The
	 * cursor can then be used by <code>Component.setCursor(cursor)</code>
	 * 
	 * @param cursor
	 *            type of the cursor which should be created
	 * @return a Cursor.
	 */
	public static Cursor createCursor(CursorType cursor) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		// Load the desired image for the cursor
		Image cursorImage = null;
		if (cursor == CursorType.normal) {
			cursorImage = toolkit.getImage(getUrl("de/bielefeld/uni/cebitec/r2cat/gui/cursorimages/selectioncursor.png"));
		} else if (cursor == CursorType.add) {
			cursorImage = toolkit.getImage(getUrl("de/bielefeld/uni/cebitec/r2cat/gui/cursorimages/selectioncursor_add.png"));
		} else if (cursor == CursorType.remove) {
			cursorImage = toolkit.getImage(getUrl("de/bielefeld/uni/cebitec/r2cat/gui/cursorimages/selectioncursor_remove.png"));
		} else if (cursor == CursorType.toggle) { 
			cursorImage = toolkit.getImage(getUrl("de/bielefeld/uni/cebitec/r2cat/gui/cursorimages/selectioncursor_toggle.png"));
		}

		// Create the desired hotspot for the cursor
		Point cursorHotSpot = new Point(0, 0);

		// Create a custom cursor

		Cursor customCursor;
		if (cursorImage != null) {
			customCursor = toolkit.createCustomCursor(cursorImage,
					cursorHotSpot, "Selection Cursor");

			// use with Component.setCursor(cursor)
			return customCursor;
		} else {
			return new Cursor(Cursor.DEFAULT_CURSOR);
		}
	}
	
	private static URL getUrl(String resource) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
		return url;
	}

}
