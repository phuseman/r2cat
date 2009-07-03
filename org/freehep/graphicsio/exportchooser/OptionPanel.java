// Copyright 2003, FreeHEP.
package org.freehep.graphicsio.exportchooser;

import java.awt.Component;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.freehep.swing.layout.TableLayout;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: OptionPanel.java 8584 2006-08-10 23:06:37Z duns $
 */
public class OptionPanel extends JPanel implements Options {
    public OptionPanel() {
        this(null);
    }

    public OptionPanel(String title) {
        super(new TableLayout());
        if (title != null)
            setBorder(BorderFactory.createTitledBorder(BorderFactory
                    .createEtchedBorder(), title));
    }

    public void setEnabled(boolean enable) {
        for (int i = 0; i < getComponentCount(); i++) {
            Component c = getComponent(i);
            c.setEnabled(enable);
        }
    }

    public boolean applyChangedOptions(Properties options) {
        boolean changed = false;
        for (int i = 0; i < getComponentCount(); i++) {
            Component c = getComponent(i);
            if (c instanceof Options) {
                boolean changedThis = ((Options) c)
                        .applyChangedOptions(options);
                changed = changed || changedThis;
            }
        }
        return changed;
    }
}
