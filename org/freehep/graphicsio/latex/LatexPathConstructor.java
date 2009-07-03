// Copyright 2004, FreeHEP
package org.freehep.graphicsio.latex;

import java.io.IOException;
import java.io.PrintStream;

import org.freehep.graphicsio.QuadToCubicPathConstructor;

/**
 * @author Andre Bach
 * @author Mark Donszelmann
 * @version $Id: LatexPathConstructor.java 8584 2006-08-10 23:06:37Z duns $
 */
public class LatexPathConstructor extends QuadToCubicPathConstructor {

    private PrintStream ps;

    public LatexPathConstructor(PrintStream ps) {
        super();
        this.ps = ps;
    }

    public void move(double x, double y) throws IOException {
        ps.println("\\moveto(" + fixedPrecision(x) + "," + fixedPrecision(y)
                + ")");
    }

    public void line(double x, double y) throws IOException {
        ps.println("\\lineto(" + fixedPrecision(x) + "," + fixedPrecision(y)
                + ")");
        // psline doesn't give proper corners at sharp angles
    }

    public void cubic(double x1, double y1, double x2, double y2, double x3,
            double y3) throws IOException {
        super.cubic(x1, y1, x2, y2, x3, y3);
        ps.println("\\curveto(" + fixedPrecision(x1) + "," + fixedPrecision(y1)
                + ")(" + fixedPrecision(x2) + "," + fixedPrecision(y2) + ")("
                + fixedPrecision(x3) + "," + fixedPrecision(y3) + ")");
    }

    public void closePath(double x0, double y0) throws IOException {
        ps.println("\\closepath");
    }

    protected String fixedPrecision(double d) {
        return LatexGraphics2D.fixedPrecision(d);
    }
}