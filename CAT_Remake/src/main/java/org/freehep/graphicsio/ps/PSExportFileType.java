// Copyright 2003-2007, FreeHEP.
package org.freehep.graphicsio.ps;


/**
 * @author Mark Donszelmann
 * @author Charles Loomis, Simon Fischer
 * @version $Id: PSExportFileType.java 12753 2007-06-12 22:32:31Z duns $
 */
public class PSExportFileType extends AbstractPSExportFileType {

    public String getDescription() {
        return "PostScript";
    }

    public String[] getExtensions() {
        return new String[] { "ps" };
    }

    public boolean isMultipageCapable() {
        return true;
    }

}
