/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.contigorderingproject;

import javax.swing.JPanel;

public final class ContigOrderingProjectVisualPanel extends JPanel {

  /** Creates new form ContigOrderingProjectVisualPanel1 */
  public ContigOrderingProjectVisualPanel() {
    initComponents();
  }

  @Override
  public String getName() {
    return "Step #1";
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    contigs = new javax.swing.JTextField();

    contigs.setText(org.openide.util.NbBundle.getMessage(ContigOrderingProjectVisualPanel.class, "ContigOrderingProjectVisualPanel.contigs.text")); // NOI18N
    contigs.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        contigsActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(contigs, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(196, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(contigs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(269, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

  private void contigsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contigsActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_contigsActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTextField contigs;
  // End of variables declaration//GEN-END:variables
}