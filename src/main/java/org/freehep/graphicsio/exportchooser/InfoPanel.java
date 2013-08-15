// Copyright 2003, FreeHEP.
package org.freehep.graphicsio.exportchooser;

import java.util.Properties;

import javax.swing.JLabel;

import org.freehep.swing.layout.TableLayout;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: InfoPanel.java 8584 2006-08-10 23:06:37Z duns $
 */
public class InfoPanel extends OptionPanel {
    public InfoPanel(Properties options, String rootKey, String[] keys) {
        super("Info");

        for (int i = 0; i < keys.length; i++) {
            add(TableLayout.LEFT, new JLabel(keys[i]));
            add(TableLayout.RIGHT, new OptionTextField(options, rootKey + "."
                    + keys[i], 40));
        }
    }
}
