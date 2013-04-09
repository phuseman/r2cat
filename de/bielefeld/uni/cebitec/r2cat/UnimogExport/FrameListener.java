/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Mark Ugarov
 */
public class FrameListener implements ChangeListener,KeyListener,ActionListener {
    private ExportFrame frame;
    private ExportController eC;
    
    public FrameListener(ExportFrame frame, ExportController expCon){
        this.frame = frame;
        this.eC = expCon;
    }

    public void stateChanged(ChangeEvent e) {
        this.frame.setMinLength(this.frame.getMinLength_slider());
    }

    public void keyTyped(KeyEvent e) {
        // not used
    }

    public void keyPressed(KeyEvent e) {
        // not used
    }

    public void keyReleased(KeyEvent e) {
        this.frame.setMinLength(this.frame.getMinLength_field());
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ExportConstants.BUTTON_CANCEL)){
            frame.dispose();
        }    
        else if(e.getActionCommand().equals(ExportConstants.BUTTON_RUN)){
            this.frame.setOutput(this.eC.calculateOutput(this.frame.linearIsChoosen()));
        }
    }
    

    
}
