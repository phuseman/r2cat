// Copyright 2003, FreeHEP.
package org.freehep.graphicsio.exportchooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: OptionButton.java 8584 2006-08-10 23:06:37Z duns $
 */
public class OptionButton extends JButton implements Options {

    protected String key;

    public OptionButton(Properties options, String key, String text,
            final JDialog dialog) {
        super(text);
        this.key = key;
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dialog.setVisible(true);
                dialog.dispose();
            }
        });
    }

    public boolean applyChangedOptions(Properties options) {
        return false;
    }

}
