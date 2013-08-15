/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.List;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Mark Ugarov
 */
public class ExportFrame extends JFrame{
    /**
     * The @param paraPanel contains all elements the user needs to give the
     * parameters for the running - process and the buttons. 
     * It does not contain the outputPanel. 
     */
    private JPanel paraPanel;
    private final long sequenceLength;
    
    private final JPanel maxGapPanel;
    private final JLabel maxGapLabel;
    private JSlider maxGapSlider;
    private JTextField maxGapField;
    private final JPanel gapCheckPanel;
    private final JCheckBox uniqueCheck;
    private final JCheckBox repeatCheck;
    
    private final JPanel minLengthPanel;
    private final JLabel minLengthLabel;
    private JTextField minLengthField;
    private JSlider minLengthSlider;
    private final JPanel formCheckPanel;
    private JCheckBox formCheckQuery;
    private JCheckBox formCheckTarget;
    
    private final JPanel buttonPanel;
    private JButton runButton;
    private JButton cancel;
    private JButton save;
    
    private JPanel outputPanel;
    private JTextPane outputPane;
    private JScrollPane outputScroll;
    
    
    public ExportFrame(long seqLength, int majorTickSpacing){
        super("Export to Unimog");
        this.sequenceLength = seqLength;
        
        this.paraPanel = new JPanel();
        this.paraPanel.setLayout(new BoxLayout(paraPanel, BoxLayout.Y_AXIS));
        
        // components of the maxGapPanel which can be used by the user 
        // to configure the maxGapLength. Two clusters whose distance is 
        // smaller than the maxGapLength will be joined to one cluster by the
        // model after choosing the run - JButton
        
        this.maxGapPanel = new JPanel();
        this.maxGapPanel.setLayout(new BoxLayout(this.maxGapPanel, BoxLayout.X_AXIS));
        this.maxGapLabel = new JLabel(ExportConstants.LABEL_MAXGAP);
        this.maxGapPanel.add(this.maxGapLabel);
        
        this.maxGapSlider = new JSlider();
        this.maxGapSlider.setName(ExportConstants.LABEL_MAXGAP);
        this.maxGapSlider.setMinimum(0);
        this.maxGapSlider.setMaximum((int) this.sequenceLength);
        this.maxGapSlider.setMinorTickSpacing(1);
        this.maxGapSlider.setMajorTickSpacing(majorTickSpacing);
        
        this.maxGapSlider.setPaintTicks(true);
        this.maxGapSlider.setPaintLabels(true);
        this.maxGapSlider.setPaintTrack(true);
        this.maxGapSlider.setValue(0);
        this.maxGapPanel.add(this.maxGapSlider);
        
        this.maxGapField = new JTextField(0+"",(this.sequenceLength+"").length());
        this.maxGapField.setName(ExportConstants.LABEL_MAXGAP);
        this.maxGapPanel.add(this.maxGapField);
        
        this.gapCheckPanel = new JPanel();
        this.gapCheckPanel.setLayout(new BoxLayout(this.gapCheckPanel, BoxLayout.PAGE_AXIS));
        this.uniqueCheck = new JCheckBox(ExportConstants.CHECKBOX_UNIQUE);
        this.uniqueCheck.setMnemonic('u');
        this.gapCheckPanel.add(this.uniqueCheck);
        this.repeatCheck = new JCheckBox(ExportConstants.CHECKBOX_REPEAT);
        this.repeatCheck.setMnemonic('r');
        this.gapCheckPanel.add(this.repeatCheck);
        this.maxGapPanel.add(this.gapCheckPanel);
        
        this.paraPanel.add(this.maxGapPanel);
        
        // components of the minLengthPanel which can be used by the user
        // to configure the minimal length of all clusters;
        // clusters which are smaller will be rejected in the model
        
        this.minLengthPanel = new JPanel();
        this.minLengthLabel = new JLabel(ExportConstants.LABEL_MINLENGTH);
        this.minLengthPanel.add(this.minLengthLabel);
        
        this.minLengthSlider = new JSlider();
        this.minLengthSlider.setName(ExportConstants.LABEL_MINLENGTH);
        this.minLengthSlider.setMinimum(0);
        this.minLengthSlider.setMaximum((int)this.sequenceLength); 
        this.minLengthSlider.setMinorTickSpacing(1);
        this.minLengthSlider.setMajorTickSpacing(majorTickSpacing);

        this.minLengthSlider.setPaintTicks(true);
        this.minLengthSlider.setPaintLabels(true);
        this.minLengthSlider.setPaintTrack(true);
        this.minLengthSlider.setValue(0);
        this.minLengthPanel.add(this.minLengthSlider);
        
        this.minLengthField = new JTextField(0+"",(this.sequenceLength+"").length());
        this.minLengthField.setName(ExportConstants.LABEL_MINLENGTH);
        this.minLengthPanel.add(this.minLengthField);
        
        this.formCheckPanel = new JPanel();
        this.formCheckPanel.setLayout(new BoxLayout(this.formCheckPanel, BoxLayout.PAGE_AXIS));
        this.formCheckQuery = new JCheckBox(ExportConstants.FORMCHECK_QUERY);
        this.formCheckQuery.setMnemonic('q');
        this.formCheckPanel.add(this.formCheckQuery);
        this.formCheckTarget = new JCheckBox(ExportConstants.FORMCHECK_TARGET);
        this.formCheckTarget.setMnemonic('t');
        this.formCheckPanel.add(this.formCheckTarget);
        this.minLengthPanel.add(this.formCheckPanel);
        
        this.paraPanel.add(this.minLengthPanel);
        
        // the buttons for closing the ExportFrame, run the export or 
        // save results
        
        this.buttonPanel = new JPanel();
        this.runButton = new JButton(ExportConstants.BUTTON_RUN);
        this.cancel = new JButton(ExportConstants.BUTTON_CANCEL);
        this.save = new JButton(ExportConstants.BUTTON_SAVE);
        this.buttonPanel.add(this.cancel);
        this.buttonPanel.add(this.runButton);
        this.buttonPanel.add(this.save);
        
        this.paraPanel.add(this.buttonPanel);
        
        this.setLayout(new BorderLayout());
        this.add(this.paraPanel, BorderLayout.SOUTH);
        
        // define the size of the ExportFrame by the size of the used screen (hardware)
        this.pack();
        int width =java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        this.setSize(width,this.getHeight());
        // configurate the length of the JSliders
        this.maxGapSlider.setPreferredSize(new Dimension((int)(width*0.6),this.maxGapSlider.getHeight()));
        this.minLengthSlider.setPreferredSize(new Dimension((int)(width*0.6),this.minLengthSlider.getHeight()));
        
        this.setVisible(true);
        
        this.outputPanel = null;
    }
    
    public long getMaxGap_slider(){
        int value = this.maxGapSlider.getValue();
        if(value > this.sequenceLength){
            value = (int)this.sequenceLength;
        }
        return value;
    }
    public long getMaxGap_field(){
        String v =this.maxGapField.getText();
        if ("".equals(v)) return 0;
        int value = Integer.parseInt(v);
        if(value > this.sequenceLength){
            value = (int)this.sequenceLength;
        }
        return value;
    }
    
    public void setMaxGap(long value){
        if(value > this.sequenceLength){
            return;
        }
        this.maxGapSlider.setValue((int)value);
        this.maxGapField.setText(""+value);
    }
    
    public long getMinLength_slider(){
        int value = this.minLengthSlider.getValue();
        if(value > this.sequenceLength){
            value = (int)this.sequenceLength;
        }
        return value;
    }
    public long getMinLength_field(){
        String v =this.minLengthField.getText();
        if ("".equals(v)) return 0;
        int value = Integer.parseInt(v);
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
        this.maxGapSlider.addChangeListener(vL);
        this.maxGapField.addKeyListener(vL);
        this.minLengthSlider.addChangeListener(vL);
        this.minLengthField.addKeyListener(vL);
        this.cancel.addActionListener(vL);
        this.runButton.addActionListener(vL);
        this.save.addActionListener(vL);
    }
    
    /**
     * Shows the output in the ExportFrame. 
     * When necessary, the program generates an extra panel.
     * @param text is the output  -String which is shown after 
     * it is calculated by the Model .
     */
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
                                (int)(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height*0.7)
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
    
    public boolean queryIsCircular(){
        return this.formCheckQuery.isSelected();
    }
    public boolean targetIsCircular(){
        return this.formCheckTarget.isSelected();
    }
    public boolean useUnique(){
        return this.uniqueCheck.isSelected();
    }
    public boolean useRepeats(){
        return this.repeatCheck.isSelected();
    }
    
 
}
