/***************************************************************************
 *   Copyright (C) 2010 by Christian Miele                                 *
 *   cmiele  a t  cebitec.uni-bielefeld.de                                 *
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

package de.bielefeld.uni.cebitec.contigadjacencygraph.visualisation;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.Font;

import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;

/**
 * This class is a modified renderer for a special node-layout.
 * The rendered nodes display the nodes as contigs with a direction.
 *
 * @author cmiele
 *
 */

public class CustomNodeRenderer extends LabelRenderer
{
	@Override
	public void render(Graphics2D g, VisualItem item) 
	{
		Shape outshape = getShape(item);
		int xPoint = (int)outshape.getBounds2D().getX();
		int yPoint = (int)outshape.getBounds2D().getY();
		 
		int rectWidth = (int)outshape.getBounds2D().getWidth(); // -4
		int rectHeight = (int)outshape.getBounds2D().getHeight();
		
		double peakX = xPoint + rectWidth + rectWidth * 0.25;
		double peakY = yPoint + 0.5 * rectHeight;
		
		double xTextCoordinate = xPoint + rectWidth * 0.1;
		double yTextCoordinate = yPoint + rectHeight * 0.8;

		Polygon p = new Polygon();
		p.addPoint(xPoint, yPoint);
		p.addPoint(xPoint + rectWidth, yPoint);
		p.addPoint((int)peakX,(int)peakY);
		p.addPoint(xPoint + rectWidth, yPoint + rectHeight);
		p.addPoint(xPoint, yPoint + rectHeight);
		
		outshape = p;
		item.setBounds(xPoint, yPoint, rectWidth+5, rectHeight);
	
		drawShape(g, item, outshape);	
		g.setFont(new Font("Florida", Font.BOLD, 9));
		g.drawString(getText(item), (int)xTextCoordinate, (int)yTextCoordinate);
	}
}