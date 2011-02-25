package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

/*
 * TODO Kann gelöscht werden!
 */
public class ChooseContigPanel extends JPanel {

	private ContigBorder border;
	private ContigBorder reverseBorder;
	private ContigBorder repeatBorder;
	private ContigBorder reverseRepeatBorder;
	
	public ChooseContigPanel() {
		
		GroupLayout layout = new GroupLayout(this);
		
		setLayout(layout);
		
		border = new ContigBorder(false , false);
		reverseBorder = new ContigBorder(false, true);
		repeatBorder = new ContigBorder(true, false);
		reverseRepeatBorder = new ContigBorder(true, true);
		


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
		
		leftContig1.setText("Contig 1");
		leftContig1.setContentAreaFilled(false);
		leftContig1.setBorder(border);
		leftContig2.setText("Contig 2");
		leftContig2.setContentAreaFilled(false);
		leftContig2.setBorder(repeatBorder);
		leftContig3.setText("Contig 3");
		leftContig3.setBorder(border);
		leftContig3.setContentAreaFilled(false);
		leftContig4.setText("Contig 4");
		leftContig4.setBorder(border);
		leftContig4.setContentAreaFilled(false);
		leftContig5.setText("Contig 5");
		leftContig5.setBorder(reverseBorder);
		leftContig5.setContentAreaFilled(false);
		
		centralContig.setText("aktuelles Contig");
		centralContig.setBorder(reverseRepeatBorder);
		centralContig.setContentAreaFilled(false);
		
		rightContig1.setText("Contig 1");
		rightContig1.setContentAreaFilled(false);
		rightContig1.setBorder(reverseBorder);
		rightContig2.setText("Contig 2");
		rightContig2.setContentAreaFilled(false);
		rightContig2.setBorder(border);
		rightContig3.setText("Contig 3");
		rightContig3.setContentAreaFilled(false);
		rightContig3.setBorder(border);
		rightContig4.setText("Contig 4");
		rightContig4.setContentAreaFilled(false);
		rightContig4.setBorder(repeatBorder);
		rightContig5.setText("Contig 5");
		rightContig5.setContentAreaFilled(false);
		rightContig5.setBorder(border);
		/*
		 * automatic gaps that correspond to preferred distances between
		 * neighboring components (or between a component and container border)
		 */
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(leftContig1).addComponent(leftContig2)
						.addComponent(leftContig3).addComponent(leftContig4)
						.addComponent(leftContig5))
				.addComponent(centralContig)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(rightContig1).addComponent(	rightContig2)
						.addComponent(rightContig3).addComponent(rightContig4)
						.addComponent(rightContig5))
				);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(leftContig1)
								.addComponent(rightContig1))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(
								leftContig2).addComponent(rightContig2))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(
								leftContig3).addComponent(centralContig)
								.addComponent(rightContig3))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(
								leftContig4).addComponent(rightContig4))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(
								leftContig5).addComponent(rightContig5)));
	}
	
}