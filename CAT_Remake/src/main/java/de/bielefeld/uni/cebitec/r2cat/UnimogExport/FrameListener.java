/** 
 * This Class takes all signals from the frame except from the Checkboxes. 
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JDialog;
import javax.swing.JSlider;
import javax.swing.JTextField;
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
        String source = (((JSlider) e.getSource()).getName());
        if(ExportConstants.LABEL_MAXGAP.equals(source)){
            this.frame.setMaxGap(this.frame.getMaxGap_slider());
        }
        else if(ExportConstants.LABEL_MINLENGTH.equals(source)){
            this.frame.setMinLength(this.frame.getMinLength_slider());
        }
        
    }

    public void keyTyped(KeyEvent e) {
        // not used
        
    }

    public void keyPressed(KeyEvent e) {
        // not used
        String source = ((JTextField) e.getSource()).getName();
        if(ExportConstants.LABEL_MAXGAP.equals(source)){
            this.frame.setMaxGap(this.frame.getMaxGap_field());
        }
        else if(ExportConstants.LABEL_MINLENGTH.equals(source)){
            this.frame.setMinLength(this.frame.getMinLength_field());
        }
    }

    public void keyReleased(KeyEvent e) {
        
        // not used
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ExportConstants.BUTTON_CANCEL)){
            this.eC.forceStop();
            this.frame.dispose();
        }    
        else if(e.getActionCommand().equals(ExportConstants.BUTTON_RUN)){
            String output = this.eC.calculateOutput();
            if(output != null){
                this.frame.setOutput(output);
            } 
        }
        else if(e.getActionCommand().equals(ExportConstants.BUTTON_SAVE)){
            new ExportFileChooser(this.frame.getOutput());
        }
    }
    
    
    

    
}
