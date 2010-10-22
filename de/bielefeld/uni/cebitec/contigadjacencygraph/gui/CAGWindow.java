package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

public class CAGWindow extends JFrame{

	public CAGWindow(String title){
			initComponents();
	}
	
	private void initComponents(){
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		
		JPanel panel = new JPanel();
		JButton leftContig1 = new JButton();
		JButton leftContig2 = new JButton();
		JButton leftContig3 = new JButton();
		JButton leftContig4 = new JButton();
		JButton leftContig5 = new JButton();
		
		JButton centralContig = new JButton();
		
		JButton rightContig1 = new JButton();
		JButton rightContig2 = new JButton();
		JButton rightContig3 = new JButton();
		JButton rightContig4 = new JButton();
		JButton rightContig5 = new JButton();
		
		JList contigList = new JList();
		
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		/*
		 * automatic gaps that correspond to  preferred distances between neighboring components 
		 * (or between a component and container border)
		 */
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
				layout.createSequentialGroup()
			      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
			    		  .addComponent(leftContig1)
			    		  .addComponent(leftContig2)
			    		  .addComponent(leftContig3)
			    		  .addComponent(leftContig4)
			    		  .addComponent(leftContig5))
			      .addComponent(centralContig)
			      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
			           .addComponent(rightContig1)
			           .addComponent(rightContig2)
			           .addComponent(rightContig3)
			           .addComponent(rightContig4)
			           .addComponent(rightContig5))
			      .addComponent(contigList)
		);
		
	}
	
}
