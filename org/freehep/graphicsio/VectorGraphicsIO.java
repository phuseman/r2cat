// Copyright 2000-2002 FreeHEP
package org.freehep.graphicsio;

import java.awt.Dimension;

import org.freehep.graphics2d.AbstractVectorGraphics;

/**
 * This class provides specifies added methods for VectorGraphicsIO. All added
 * methods are declared abstract.
 * 
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: VectorGraphicsIO.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class VectorGraphicsIO extends AbstractVectorGraphics {

    public VectorGraphicsIO() {
        super();
    }

    public VectorGraphicsIO(VectorGraphicsIO graphics) {
        super(graphics);
    }

    public abstract Dimension getSize();

    public abstract void printComment(String comment);

}
