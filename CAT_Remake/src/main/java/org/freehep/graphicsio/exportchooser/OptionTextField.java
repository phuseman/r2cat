// Copyright 2003, FreeHEP.
package org.freehep.graphicsio.exportchooser;

import java.util.Properties;

import javax.swing.JTextField;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: OptionTextField.java 8584 2006-08-10 23:06:37Z duns $
 */
public class OptionTextField extends JTextField implements Options {
    protected String initialText;

    protected String key;

    public OptionTextField(Properties options, String key, int columns) {
        super(options.getProperty(key, ""), columns);
        this.key = key;
        initialText = getText();
    }

    public boolean applyChangedOptions(Properties options) {
        if (!getText().equals(initialText)) {
            options.setProperty(key, getText());
            return true;
        }
        return false;
    }
}
