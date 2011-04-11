package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.NumberFormat;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


public class LegendAndInputOptionPanel extends JPanel{
	
	private JRadioButton absoluteSupport;
	private JRadioButton zScoreRadioButton;
	private int numberOfNeighbours;
	private CAGWindow win;
	
	/*
	 * This panel contains the textfield for entering the number of neighbours
	 * and also the options to choose  absolute support or z-scores 
	 * and a legend
	 */
	public LegendAndInputOptionPanel(CAGWindow window) {
		
		this.win = window;  
		GridBagLayout inputOptionLayout = new GridBagLayout();
		this.setLayout(inputOptionLayout);
		this.setPreferredSize(new Dimension(1000, 60));
	}
	

	public LegendAndInputOptionPanel(CagController cagController,
			CagCreator cagModel) {
		// TODO Auto-generated constructor stub
	}

	public void createLegendAndInputOption(int neighboursNumber){
		
		this.numberOfNeighbours = neighboursNumber;
		GridBagConstraints c = new GridBagConstraints();

		JLabel chooseNumberOfNeighbours = new JLabel("number of neighbors ");
		c.gridx = 0;
		c.gridy = 0;
		chooseNumberOfNeighbours.setToolTipText("<html>"
				+ "Here you are able to choose the number of neighbours.<br>"
				+ "Please type a number between 1 and 10 in the textfield<br>"
				+ "and press ENTER."
				+ "</html>");
		this.add(chooseNumberOfNeighbours,c);

		NumberFormat nformat = NumberFormat.getNumberInstance();
		c.gridx = 1;
		c.gridy = 0;
		c.ipadx = 20;
		c.anchor = GridBagConstraints.LINE_START;
		JFormattedTextField inputOptionForNumberOfNeighbours = new JFormattedTextField(nformat);
		inputOptionForNumberOfNeighbours.setPreferredSize(new Dimension(100, 100));
		inputOptionForNumberOfNeighbours.setValue(new Integer(
				numberOfNeighbours));
		inputOptionForNumberOfNeighbours.setColumns(3);
		inputOptionForNumberOfNeighbours.addPropertyChangeListener("value",
				new NumberOfNeighboursListener(win));
		inputOptionForNumberOfNeighbours.setToolTipText("<html>"
				+ "Please type a number <br>" +
						"between 1 and 10<br>"
				+ "and press ENTER/Return."
				+ "</html>");

		this.add(inputOptionForNumberOfNeighbours,c);

		ButtonGroup supportGroup = new ButtonGroup();

		absoluteSupport = new JRadioButton("absolute Support");
		absoluteSupport.setSelected(true);
		absoluteSupport.setToolTipText("<html>If you choose this option<br> you see at each line <br>" +
				"the likelyhood score.</html> ");
		absoluteSupport.setActionCommand("absolute");
		absoluteSupport.addActionListener(new RadioButtonActionListener(win));
		
		c.gridx = 2;
		c.gridy = 0;
		c.ipadx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(absoluteSupport, c);

		zScoreRadioButton = new JRadioButton("z-Score");
		zScoreRadioButton.setToolTipText("<html> If you choose this option <br>you see at each line <br> "
				+" a normalized score.</html>");
		zScoreRadioButton.setActionCommand("zScore");
		zScoreRadioButton.addActionListener(new RadioButtonActionListener(win));
		
		supportGroup.add(absoluteSupport);
		supportGroup.add(zScoreRadioButton);
		
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(zScoreRadioButton, c);
		
		JLabel legendLabel = new JLabel("     legend: ");
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		this.add(legendLabel, c);
		
		ImageIcon nsIcon = new ImageIcon("pictures/nsLine.png");
		c.gridx = 4;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(new JLabel(nsIcon),c);
		
		JLabel notSelectedLabel = new JLabel("not selected adjacency");
		c.gridx = 5;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(notSelectedLabel, c);	
		
		ImageIcon swsIcon = new ImageIcon("pictures/swsLine.png");
		c.gridx = 4;
		c.gridy = 1;
		this.add(new JLabel(swsIcon),c);
		
		JLabel somewhereElseSelectedLabel = new JLabel("somewhere else selected adjacency");
		c.gridx = 5;
		c.gridy = 1;
		this.add(somewhereElseSelectedLabel, c);
		
		ImageIcon sIcon = new ImageIcon("pictures/sLine.png");
		c.gridx = 4;
		c.gridy = 2;
		this.add(new JLabel(sIcon),c);
		
		JLabel selectedLabel = new JLabel("selected adjacency");
		c.gridx = 5;
		c.gridy = 2;
		this.add(selectedLabel, c);		


	}

}
