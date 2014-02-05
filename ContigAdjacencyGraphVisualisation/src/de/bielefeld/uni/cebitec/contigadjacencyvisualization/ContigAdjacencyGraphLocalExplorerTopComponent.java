/*
 *  Copyright (C) 2011 phuseman
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.bielefeld.uni.cebitec.contigadjacencyvisualization;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencyvisualization.local.CAGWindow;
import de.bielefeld.uni.cebitec.contigadjacencyvisualization.local.CagController;
import de.bielefeld.uni.cebitec.contigadjacencyvisualization.local.CagCreator;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.bielefeld.uni.cebitec.contigadjacencyvisualization//ContigAdjacencyGraphLocalExplorer//EN",
autostore = false)
public final class ContigAdjacencyGraphLocalExplorerTopComponent extends TopComponent implements ChangeListener {

    private static ContigAdjacencyGraphLocalExplorerTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "ContigAdjacencyGraphLocalExplorerTopComponent";

        private CagController viewController;

    public ContigAdjacencyGraphLocalExplorerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(ContigAdjacencyGraphLocalExplorerTopComponent.class, "CTL_ContigAdjacencyGraphLocalExplorerTopComponent"));
        setToolTipText(NbBundle.getMessage(ContigAdjacencyGraphLocalExplorerTopComponent.class, "HINT_ContigAdjacencyGraphLocalExplorerTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));


        ContigAdjacencyPropertiesTopComponent captc = (ContigAdjacencyPropertiesTopComponent) WindowManager.getDefault().findTopComponent("ContigAdjacencyPropertiesTopComponent");
        if (captc != null) {
            captc.addChangeListener(this);
        } else {
            Logger.getLogger(ContigAdjacencyGraphTopComponent.class.getName()).warning(
                    "Could not register a change listener.");

        }

        viewController = new CagController();

        jScrollPane1.getViewport().add(viewController.getContigView());
        jScrollPane1.setVisible(true);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
    );
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized ContigAdjacencyGraphLocalExplorerTopComponent getDefault() {
        if (instance == null) {
            instance = new ContigAdjacencyGraphLocalExplorerTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the ContigAdjacencyGraphLocalExplorerTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ContigAdjacencyGraphLocalExplorerTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(ContigAdjacencyGraphLocalExplorerTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ContigAdjacencyGraphLocalExplorerTopComponent) {
            return (ContigAdjacencyGraphLocalExplorerTopComponent) win;
        }
        Logger.getLogger(ContigAdjacencyGraphLocalExplorerTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        ContigAdjacencyPropertiesTopComponent captc = (ContigAdjacencyPropertiesTopComponent) WindowManager.getDefault().findTopComponent("ContigAdjacencyPropertiesTopComponent");
        if (captc != null) {
            captc.open();
        }
    }

    @Override
    public void componentClosed() {
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof ContigAdjacencyPropertiesTopComponent) {
            ContigAdjacencyPropertiesTopComponent captc = (ContigAdjacencyPropertiesTopComponent) e.getSource();
            this.setLayoutGraph(captc.getCurrentLayoutGraph());
        }
    }

  private void setLayoutGraph(LayoutGraph layoutGraph) {
    viewController.setLayoutGraph(layoutGraph);
    this.invalidate();
    this.revalidate();
  }

}