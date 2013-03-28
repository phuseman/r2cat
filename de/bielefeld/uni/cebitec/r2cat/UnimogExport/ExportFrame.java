/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport;

import de.bielefeld.uni.cebitec.r2cat.UnimogExport.ExportConstants;
import de.bielefeld.uni.cebitec.r2cat.UnimogExport.FrameListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.List;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Mark Ugarov
 */
public class ExportFrame extends JFrame{
    
    private final JPanel buttonPanel;
    private JButton run;
    private JButton cancel;
    
    private final JPanel maxGapPanel;
    private final int sequenceLength;
    private JTextField maxGapField;
    private JSlider maxGapSlider;
    private List formList;
    
    private JPanel outputPanel;
    private JTextPane outputPane;
    private JScrollPane outputScroll;
    
    
    public ExportFrame(int seqLength, int majorTickSpacing, String seqName, String patName){
        super("Export to Unimog: " + seqName +" / " + patName);
        this.sequenceLength = seqLength;
        
        this.buttonPanel = new JPanel();
        this.run = new JButton(ExportConstants.BUTTON_RUN);
        this.cancel = new JButton(ExportConstants.BUTTON_CANCEL);
        this.buttonPanel.add(this.cancel);
        this.buttonPanel.add(this.run);

        
        this.maxGapPanel = new JPanel();
        
        this.maxGapSlider = new JSlider();
        this.maxGapSlider.setMinimum(0);
        this.maxGapSlider.setMaximum(this.sequenceLength);
        this.maxGapSlider.setMinorTickSpacing(1);
        this.maxGapSlider.setMajorTickSpacing(majorTickSpacing);

        this.maxGapSlider.setPaintTicks(true);
        this.maxGapSlider.setPaintLabels(true);
        this.maxGapSlider.setPaintTrack(true);
        this.maxGapPanel.add(this.maxGapSlider);
        
        this.maxGapField = new JTextField(this.sequenceLength+"",(this.sequenceLength+"").length());
        this.maxGapPanel.add(this.maxGapField);
        
        this.formList = new List(2);
        this.formList.add(ExportConstants.LISTENTRY_LIN);
        this.formList.add(ExportConstants.LISTENTRY_CIRC);
        this.formList.select(0);
        this.maxGapPanel.add(this.formList);
        
          
        this.setLayout(new BorderLayout());
        this.add(this.maxGapPanel, BorderLayout.CENTER);
        this.add(this.buttonPanel, BorderLayout.SOUTH);
        this.pack();
        int width =java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        this.setSize(width,this.getHeight());
        this.maxGapSlider.setSize(maxGapSlider.getHeight(), (int)(width*0.8));
        this.setVisible(true);
        
        this.outputPanel = null;
    }
    
    public int getGapValue_slider(){
        int value = this.maxGapSlider.getValue();
        if(value > this.sequenceLength){
            value = this.sequenceLength;
        }
        return value;
    }
    public int getGapValue_field(){
        int value = Integer.parseInt(this.maxGapField.getText());
        if(value > this.sequenceLength){
            value = this.sequenceLength;
        }
        return value;
    }
    
    public void setGapValue(int value){
        if(value > this.sequenceLength){
            return;
        }
        this.maxGapSlider.setValue(value);
        this.maxGapField.setText(""+value);
    }
    
    public void setListener(FrameListener vL){
        this.maxGapSlider.addChangeListener(vL);
        this.maxGapField.addKeyListener(vL);
        this.cancel.addActionListener(vL);
        this.run.addActionListener(vL);
    }
    
    public void setOutput(String text){
        if (this.outputPanel == null){
            System.out.println("create OutputPanel");
            this.outputPanel = new JPanel();
            this.outputPane = new JTextPane();
            this.outputPane.setMaximumSize(
                    new Dimension(
                                (int)(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width*0.8),
                                (int)(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height*0.8)
                                )
                 );
            this.outputScroll = new JScrollPane(outputPane, 
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.outputPanel.add(this.outputScroll);
            this.add(this.outputPanel, BorderLayout.NORTH);
        }
        this.outputPane.setText(text);
        this.pack();
        this.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,this.getHeight());   
    }
    
    public boolean linearIsChoosen(){
        if (this.formList.getSelectedItem().equals(ExportConstants.LISTENTRY_CIRC)){
            return false;
        }
        else{
            return true;
        }
    }
    
 
}
