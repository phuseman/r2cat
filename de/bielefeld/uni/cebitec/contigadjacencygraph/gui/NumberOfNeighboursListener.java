package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class NumberOfNeighboursListener implements PropertyChangeListener {
	
	private CagController con;
	private int numberOfNeighbours;
	
	public NumberOfNeighboursListener(CagController controller){
		this.con = controller;
	}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			JFormattedTextField inputOptionForNumberOfNeighbours = 
				((JFormattedTextField) evt.getSource());
			/*
			 * This is going to be activated, if the user set a new number of
			 * neighbours.
			 * But only between 1 and 10
			 */
			int neighboursNumber = ((Number) evt.getNewValue()).intValue();

			JPanel rightContainer = con.getChooseContigPanel().getRightContainer();
			JPanel leftContainer = con.getChooseContigPanel().getLeftContainer();

			if (neighboursNumber <= 10 && neighboursNumber > 0) {

				if (neighboursNumber < numberOfNeighbours) {
					int breite = (int) con.getChooseContigPanel().getSize().getWidth();
					con.getChooseContigPanel().setPreferredSize(new Dimension(breite,
							400));
				}

				if (neighboursNumber > 8) {
					int breite = (int) con.getChooseContigPanel().getSize().getWidth();
					con.getChooseContigPanel().setPreferredSize(new Dimension(breite,
							600));
				}

				numberOfNeighbours = neighboursNumber;
				inputOptionForNumberOfNeighbours.setValue(new Integer(
						numberOfNeighbours));
				con.setNumberOfNeighbours(neighboursNumber);
				con.getChooseContigPanel().setNumberOfNeighbours(numberOfNeighbours);

				
				if (rightContainer.getComponentCount() != 0
						|| leftContainer.getComponentCount() != 0) {
					updateModelAndGui();
				}

			} else if (neighboursNumber == 0) {
				javax.swing.JOptionPane.showMessageDialog(con.getWindow(), "Sorry.\n"
						+ "You can't choose " + neighboursNumber
						+ " Neighbours.\n" + "Please choose between 1 and 10.");

				numberOfNeighbours = 5;
				inputOptionForNumberOfNeighbours .setValue(new Integer(
						numberOfNeighbours));
				con.setNumberOfNeighbours(neighboursNumber);
				con.getChooseContigPanel().setNumberOfNeighbours(numberOfNeighbours);

				if (rightContainer.getComponentCount() != 0
						|| leftContainer.getComponentCount() != 0) {
					updateModelAndGui();
				}
			} else {
				javax.swing.JOptionPane.showMessageDialog(con.getWindow(), "Sorry.\n"
						+ "You can't choose " + neighboursNumber
						+ " Neighbours.\n" + "Please choose between 1 and 10.");

				numberOfNeighbours = 10;
				inputOptionForNumberOfNeighbours.setValue(new Integer(
						numberOfNeighbours));
				int breite = (int) con.getChooseContigPanel().getSize().getWidth();

				con.getChooseContigPanel().setPreferredSize(new Dimension(breite, 600));
				con.setNumberOfNeighbours(neighboursNumber);
				con.getChooseContigPanel().setNumberOfNeighbours(numberOfNeighbours);

				if (rightContainer.getComponentCount() != 0
						|| leftContainer.getComponentCount() != 0) {
					updateModelAndGui();
				}
			}

		}
		
		private void updateModelAndGui(){
			con.sendLeftNeighbours();
			con.sendRightNeighbours();
			con.getChooseContigPanel().repaint();
		}

	
}
