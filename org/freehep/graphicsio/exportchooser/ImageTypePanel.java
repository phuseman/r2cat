// Copyright 2003, FreeHEP.
package org.freehep.graphicsio.exportchooser;

import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.freehep.graphicsio.ImageConstants;
import org.freehep.swing.layout.TableLayout;
import org.freehep.util.UserProperties;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: ImageTypePanel.java 8584 2006-08-10 23:06:37Z duns $
 */
public class ImageTypePanel extends OptionPanel {

    private String key;

    private String initialType;

    private JComboBox imageTypeCombo;

    public ImageTypePanel(Properties user, String rootKey, String[] types) {
        super("Image Type");
        key = rootKey + "." + ImageConstants.WRITE_IMAGES_AS;

        UserProperties options = new UserProperties(user);
        initialType = options.getProperty(key);

        imageTypeCombo = new OptionComboBox(options, key, types);
        // FREEHEP-575
        imageTypeCombo.setSelectedItem(initialType);
        add(TableLayout.LEFT, new JLabel("Include Images as "));
        add(TableLayout.RIGHT, imageTypeCombo);
    }
}