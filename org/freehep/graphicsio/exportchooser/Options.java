// Copyright 2003, FreeHEP
package org.freehep.graphicsio.exportchooser;

import java.util.Properties;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: Options.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface Options {

    /**
     * Sets all the changed options in the properties object.
     * 
     * @return true if any options were set
     */
    public boolean applyChangedOptions(Properties options);
}
