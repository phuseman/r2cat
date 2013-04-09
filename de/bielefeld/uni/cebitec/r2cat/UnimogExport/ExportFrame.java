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
import javax.swing.JLabel;
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
    private JButton runButton;
    private JButton cancel;
    private JButton save;
    
    private final JPanel minLengthPanel;
    private final long sequenceLength;
    private final JLabel minLengthLabel;
    private JTextField minLengthField;
    private JSlider minLengthSlider;
    private List formList;
    
    private JPanel outputPanel;
    private JTextPane outputPane;
    private JScrollPane outputScroll;
    
    
    public ExportFrame(long seqLength, int majorTickSpacing){
        super("Export to Unimog");
        this.sequenceLength = seqLength;
        
        this.buttonPanel = new JPanel();
        this.runButton = new JButton(ExportConstants.BUTTON_RUN);
        this.cancel = new JButton(ExportConstants.BUTTON_CANCEL);
        this.save = new JButton(ExportConstants.BUTTON_SAVE);
        this.buttonPanel.add(this.cancel);
        this.buttonPanel.add(this.runButton);
        this.buttonPanel.add(this.save);
        
        this.minLengthPanel = new JPanel();
        this.minLengthLabel = new JLabel(ExportConstants.LABEL_LENGTH);
        this.minLengthPanel.add(this.minLengthLabel);
        
        this.minLengthSlider = new JSlider();
        this.minLengthSlider.setMinimum(0);
        this.minLengthSlider.setMaximum((int)this.sequenceLength); 
        this.minLengthSlider.setMinorTickSpacing(1);
        this.minLengthSlider.setMajorTickSpacing(majorTickSpacing);

        this.minLengthSlider.setPaintTicks(true);
        this.minLengthSlider.setPaintLabels(true);
        this.minLengthSlider.setPaintTrack(true);
        this.minLengthSlider.setValue(0);
        this.minLengthPanel.add(this.minLengthSlider);
        
        this.minLengthField = new JTextField(this.sequenceLength+"",(this.sequenceLength+"").length());
        this.minLengthField.setText("0");
        this.minLengthPanel.add(this.minLengthField);
        
        this.formList = new List(2);
        this.formList.add(ExportConstants.LISTENTRY_LIN);
        this.formList.add(ExportConstants.LISTENTRY_CIRC);
        this.formList.select(0);
        this.minLengthPanel.add(this.formList);
        
          
        this.setLayout(new BorderLayout());
        this.add(this.minLengthPanel, BorderLayout.CENTER);
        this.add(this.buttonPanel, BorderLayout.SOUTH);
        // define the size of the ExportFrame by the size of the used screen (hardware)
        this.pack();
        int width =java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        this.setSize(width,this.getHeight());
        // configurate the length of the JSlider
        this.minLengthSlider.setPreferredSize(new Dimension((int)(width*0.6),minLengthSlider.getHeight()));
        this.setVisible(true);
        
        this.outputPanel = null;
    }
    
    public long getMinLength_slider(){
        int value = this.minLengthSlider.getValue();
        if(value > this.sequenceLength){
            value = (int)this.sequenceLength;
        }
        return value;
    }
    public long getMinLength_field(){
        int value = Integer.parseInt(this.minLengthField.getText());
        if(value > this.sequenceLength){
            value = (int)this.sequenceLength;
        }
        return value;
    }
    
    public void setMinLength(long value){
        if(value > this.sequenceLength){
            return;
        }
        this.minLengthSlider.setValue((int)value);
        this.minLengthField.setText(""+value);
    }
    
    public void setListener(FrameListener vL){
        this.minLengthSlider.addChangeListener(vL);
        this.minLengthField.addKeyListener(vL);
        this.cancel.addActionListener(vL);
        this.runButton.addActionListener(vL);
        this.save.addActionListener(vL);
    }
    
    public void setOutput(String text){
        if (this.outputPanel == null){
            this.outputPanel = new JPanel();
            this.outputPane = new JTextPane();
            
            this.outputScroll = new JScrollPane(outputPane, 
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.outputScroll.setPreferredSize(
                    new Dimension(
                                (int)(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width*0.9),
                                (int)(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height*0.8)
                                )
                 );
            this.outputPanel.add(this.outputScroll);
            this.add(this.outputPanel, BorderLayout.NORTH);
        }
        this.outputPane.setText(text);
        this.pack();
        this.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,this.getHeight());   
    }
    
    public String getOutput(){
        if(this.outputPanel == null){
            return "";
        }
        return this.outputPane.getText();
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
