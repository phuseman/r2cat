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
package de.bielefeld.uni.cebitec.dotplotviewer;

import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.r2cat.ContigSorter;
import de.bielefeld.uni.cebitec.r2cat.gui.SequenceOrderTable;
import de.bielefeld.uni.cebitec.r2cat.gui.SequenceOrderTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ProgressMonitor;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.bielefeld.uni.cebitec.dotplotviewer//ContigTable//EN",
autostore = false)
public final class ContigTableTopComponent extends TopComponent implements PropertyChangeListener {

  private SequenceOrderTable contigTable = null;
  private MatchList matchList = null;
  private static ContigTableTopComponent instance;
  /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
  private static final String PREFERRED_ID = "ContigTableTopComponent";

  public ContigTableTopComponent() {
    initComponents();
    setName(NbBundle.getMessage(ContigTableTopComponent.class, "CTL_ContigTableTopComponent"));
    setToolTipText(NbBundle.getMessage(ContigTableTopComponent.class, "HINT_ContigTableTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
  }

  public void setMatchList(MatchList ml) {

    contigTableScrollPane.setViewportView(null);

    if (ml != null) {
      moveStartButton.removeActionListener(contigTable);
      moveEndButton.removeActionListener(contigTable);

      matchList = ml;
      contigTable = new SequenceOrderTable(matchList);
      SequenceOrderTableModel model = (SequenceOrderTableModel) contigTable.getModel();
      model.setShowComplementColumn(true);
      Dimension d = contigTableScrollPane.getSize();

      moveStartButton.addActionListener(contigTable);
      moveEndButton.addActionListener(contigTable);

      contigTableScrollPane.setViewportView(contigTable);
    }

    if (matchList == null) {
      enableButtons(false);
    } else {
      enableButtons(true);
    }

    this.revalidate();
  }

  private void enableButtons(boolean enable) {
    moveStartButton.setEnabled(enable);
    moveEndButton.setEnabled(enable);
    sortButton.setEnabled(enable);
    exportTextButton.setEnabled(enable);
    exportFastaButton.setEnabled(enable);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    controlPanel = new javax.swing.JPanel();
    moveStartButton = new javax.swing.JButton();
    moveEndButton = new javax.swing.JButton();
    sortButton = new javax.swing.JButton();
    MoveLabel = new javax.swing.JLabel();
    exportTextButton = new javax.swing.JButton();
    exportFastaButton = new javax.swing.JButton();
    exportLabel = new javax.swing.JLabel();
    contigTableScrollPane = new javax.swing.JScrollPane();

    setLayout(new java.awt.BorderLayout());

    controlPanel.setLayout(new java.awt.GridBagLayout());

    org.openide.awt.Mnemonics.setLocalizedText(moveStartButton, org.openide.util.NbBundle.getMessage(ContigTableTopComponent.class, "ContigTableTopComponent.moveStartButton.text")); // NOI18N
    moveStartButton.setActionCommand(org.openide.util.NbBundle.getMessage(ContigTableTopComponent.class, "ContigTableTopComponent.moveStartButton.actionCommand")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    controlPanel.add(moveStartButton, gridBagConstraints);

    org.openide.awt.Mnemonics.setLocalizedText(moveEndButton, org.openide.util.NbBundle.getMessage(ContigTableTopComponent.class, "ContigTableTopComponent.moveEndButton.text")); // NOI18N
    moveEndButton.setActionCommand(org.openide.util.NbBundle.getMessage(ContigTableTopComponent.class, "ContigTableTopComponent.moveEndButton.actionCommand")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    controlPanel.add(moveEndButton, gridBagConstraints);

    org.openide.awt.Mnemonics.setLocalizedText(sortButton, org.openide.util.NbBundle.getMessage(ContigTableTopComponent.class, "ContigTableTopComponent.sortButton.text")); // NOI18N
    sortButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        sortButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    controlPanel.add(sortButton, gridBagConstraints);

    org.openide.awt.Mnemonics.setLocalizedText(MoveLabel, org.openide.util.NbBundle.getMessage(ContigTableTopComponent.class, "ContigTableTopComponent.MoveLabel.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
    controlPanel.add(MoveLabel, gridBagConstraints);

    org.openide.awt.Mnemonics.setLocalizedText(exportTextButton, org.openide.util.NbBundle.getMessage(ContigTableTopComponent.class, "ContigTableTopComponent.exportTextButton.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
    controlPanel.add(exportTextButton, gridBagConstraints);

    org.openide.awt.Mnemonics.setLocalizedText(exportFastaButton, org.openide.util.NbBundle.getMessage(ContigTableTopComponent.class, "ContigTableTopComponent.exportFastaButton.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    controlPanel.add(exportFastaButton, gridBagConstraints);

    org.openide.awt.Mnemonics.setLocalizedText(exportLabel, org.openide.util.NbBundle.getMessage(ContigTableTopComponent.class, "ContigTableTopComponent.exportLabel.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    controlPanel.add(exportLabel, gridBagConstraints);

    add(controlPanel, java.awt.BorderLayout.PAGE_END);
    add(contigTableScrollPane, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void sortButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortButtonActionPerformed

    ContigSorter sorter = new ContigSorter(matchList);

    		ProgressMonitor progress = new NetbeansProgressMonitor("Sorting contigs","Sorting contigs");
	
		if (progress!=null) {
		sorter.register(progress);
		}


    //if the sorting is not done in a thread, the gui blocks
    Thread t = new Thread(sorter);
    t.start();


  }//GEN-LAST:event_sortButtonActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel MoveLabel;
  private javax.swing.JScrollPane contigTableScrollPane;
  private javax.swing.JPanel controlPanel;
  private javax.swing.JButton exportFastaButton;
  private javax.swing.JLabel exportLabel;
  private javax.swing.JButton exportTextButton;
  private javax.swing.JButton moveEndButton;
  private javax.swing.JButton moveStartButton;
  private javax.swing.JButton sortButton;
  // End of variables declaration//GEN-END:variables

  /**
   * Gets default instance. Do not use directly: reserved for *.settings files only,
   * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
   * To obtain the singleton instance, use {@link #findInstance}.
   */
  public static synchronized ContigTableTopComponent getDefault() {
    if (instance == null) {
      instance = new ContigTableTopComponent();
    }
    return instance;
  }

  /**
   * Obtain the ContigTableTopComponent instance. Never call {@link #getDefault} directly!
   */
  public static synchronized ContigTableTopComponent findInstance() {
    TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
    if (win == null) {
      Logger.getLogger(ContigTableTopComponent.class.getName()).warning(
              "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
      return getDefault();
    }
    if (win instanceof ContigTableTopComponent) {
      return (ContigTableTopComponent) win;
    }
    Logger.getLogger(ContigTableTopComponent.class.getName()).warning(
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
    DotPlotViewerTopComponent.findInstance().addPropertyChangeListener(this);
    this.setMatchList(DotPlotViewerTopComponent.findInstance().getMatchList());
  }

  @Override
  public void componentClosed() {
    DotPlotViewerTopComponent.findInstance().removePropertyChangeListener(this);
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
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getNewValue() == null) {
      this.close();
    } else {
      if (evt.getNewValue() instanceof MatchList) {
        this.setMatchList((MatchList) evt.getNewValue());
      }
    }
  }
}
