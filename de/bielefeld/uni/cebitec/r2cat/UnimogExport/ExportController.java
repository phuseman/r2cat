/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport;

/**
 *
 * @author Mark Ugarov
 */
public class ExportController {
    private ExportFrame frame;
    private FrameListener valueListener;
    private int sequenceLength;
    private String sequenceName;
    private String patternName;
    
    public ExportController(){
        // the following lines have to be corrected: 
        //the maximum has to be the length of the longer sequence 
        //the sequenceName and the patternName have to be the names of the input-sequence
        sequenceLength=150;
        sequenceName = "Sequence";
        patternName = "Pattern";
        frame = new ExportFrame(sequenceLength, calculateMajorTickSpacing(sequenceLength), sequenceName, patternName);
        valueListener = new FrameListener(frame, this);
        frame.setListener(valueListener);
    }
    
    private int calculateMajorTickSpacing(int sL){
        int retValue = 1;
        while(sL>100){
            retValue *= 10;
            sL /= 10;
        }
        while(this.sequenceLength/retValue>=10){
            retValue *= 2;
        }
        return retValue;
    }
    
    public String calculate(boolean linear){
        Model model = new Model(linear);
        return model.getOuput();
    }
}
