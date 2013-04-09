/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport;

import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.r2cat.DataModelController;

/**
 *
 * @author Mark Ugarov
 */
public class ExportController {
    
    final private static int NAMELENGTH=8;
    
    /**
     * The frame makes it easy for the user to modify parameters like the 
     * minimal length of matches.
     */
    private ExportFrame frame;
    /**
     * The dataControl is the DataModelController of the whole program and
     * is used to get informations about the actual input - sequences.
     */
    private DataModelController dataControl;
    /**
     * The MatchList of the dataControl.
     */
    private MatchList matches;
    /**
     * The FrameListener, which gets his signals from the elements of the frame.
     */
    private FrameListener valueListener;
    /**
     * The sequenceLength, which is necessary for the frame to calculate the 
     * maximum values of his JSlider.
     */
    private long sequenceLength;
    /**
     * The name of the Sequence.
     */
    private String sequenceName;
    /**
     * The name of the used pattern. 
     */
    private String patternName;
    
    
    /**
     * The Constructor needs the DataModelController
     * @param datCon 
     */
    public ExportController(DataModelController datCon){
        // the following lines have to be corrected: 
        //the maximum has to be the length of the longer sequence 
        //the sequenceName and the patternName have to be the names of the input-sequence
        this.dataControl = datCon;
        if(!this.dataControl.getMatchesList().isEmpty()){
            this.init();
        }    
    }
    
    private void init(){
        this.matches = this.dataControl.getMatchesList();
        this.sequenceName = matches.getQueries().elementAt(0).getDescription();
        // cutting the sequenceName if it's longer than the given NAMELENGTH
        this.sequenceName = this.sequenceName.substring(0, Math.min(this.sequenceName.length(), NAMELENGTH));
        
        this.patternName = matches.getTargets().elementAt(0).getDescription();
        this.patternName = this.patternName.substring(0, Math.min(this.patternName.length(), NAMELENGTH));
        
        this.sequenceLength = matches.getQueries().elementAt(0).getSize();
        
        
        frame = new ExportFrame(sequenceLength, calculateMajorTickSpacing((int)sequenceLength), sequenceName, patternName);
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
    
    /**
     * The following Method calculates the ouput, wich can be used as input for
     * Unimog. 
     * @param matches are the given matches from the DataModelController
     * @param frame.getGapValue_field is the minimal length of the q-gram-cluster
     * @param linear is the chosen by the user and discribes wether the input
     * sequence comes from a linear chromosom or not
     * @return the output
     */
    public String calculateOutput(boolean linear){
        Model model = new Model(this.matches, this.frame.getMinLength_field(), linear);
        return model.getOuput();
    }
}
