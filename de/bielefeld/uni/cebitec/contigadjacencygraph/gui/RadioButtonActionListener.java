package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class RadioButtonActionListener implements ActionListener {
	
	private CagController con;
	private CagCreator cagModel;

	private boolean isZScore = false;
	private DNASequence centralContig;
	private int centralContigIndex;
	private JRadioButton absoluteSupport;
	private JRadioButton zScore;
	private Vector<Vector<AdjacencyEdge>> selectedLeftEdges;
	private Vector<Vector<AdjacencyEdge>> selectedRightEdges;
	

	
	public RadioButtonActionListener (CagController controller, CagCreator model){

		this.con = controller;
		this.cagModel = model;
		this.isZScore = cagModel.isZScore();
		
		this.centralContig = cagModel.getCurrentContigObject();
		this.centralContigIndex = cagModel.getCurrentContigIndex();
	
		
		selectedLeftEdges = cagModel.getSelectedLeftEdges();
		selectedRightEdges = cagModel.getSelectedRightEdges();
		
	}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			/*
			 * Here are the options to react on a selection of a radion Button
			 * next to the contigs
			 * 
			 *  If there is not a neighbour selected 
			 */
		 if (e.getActionCommand().equals(
					"noch kein nachbar ausgewaehlt")) {
			
				ContigRadioButton radioButton = (ContigRadioButton) e
						.getSource();
				AdjacencyEdge selectedEdge = radioButton.getEdge();

				int[] indices = leftAndRightIndex(radioButton, selectedEdge);
				/*
				 * the adjacency will be set as selected
				 */
				selectEdge(selectedEdge, indices);

				/*
				 * if there is a neighbour already selected
				 */
			} else if (e.getActionCommand().equals("nachbarAusgewaehlt")) {

				ContigRadioButton radioButton = (ContigRadioButton) e.getSource();
				AdjacencyEdge selectedEdge = radioButton.getEdge();

				int[] indices = leftAndRightIndex(radioButton, selectedEdge);
				AdjacencyEdge oldEdge = radioButton
						.getSelectedNeighbourOfButtonGroup();
				int[] oldIndices = leftAndRightIndex(radioButton, oldEdge);
				
				/*
				 * the old selected adjacency will be deleted
				 * and the new set as selected
				 */
				deleteEdge(oldEdge, oldIndices);
				if(!oldEdge.equals(selectedEdge)){
					selectEdge(selectedEdge, indices);
				}
				
				/*
				 * It is also possible that there will be contig which is already selected
				 *  in an another adjacency. 
				 *  This will treat that possibility.
				 */
			} else if (e.getActionCommand().equals("anderweitigAusgewaehlt")) {

				ContigRadioButton radioButton = (ContigRadioButton) e
						.getSource();
				AdjacencyEdge selectedEdge = radioButton.getEdge();

				int[] indices = leftAndRightIndex(radioButton, selectedEdge);
				AdjacencyEdge otherEdge = radioButton
						.getNeighboursForTheThisNeighbour();
				int[] oldIndices = leftAndRightIndex(radioButton, otherEdge);
				
				AdjacencyEdge neighbourEdge = radioButton.getSelectedNeighbourOfButtonGroup();
				int[] neighbourIndices = null;
				if (neighbourEdge!= null){
					neighbourIndices = leftAndRightIndex(radioButton, neighbourEdge);
				}
				
				/*
				 * The user will be asked, if he would like to deleted the old adjacency 
				 */
					Object[] options = { "Yes", "No" };

					int n = javax.swing.JOptionPane.showOptionDialog(con.getWindow(),
							"You already selected this neighbour for an another selection.\n"
									+ " Do you want to delete that selection and want to select this selection?",
							"", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);

					if (n == JOptionPane.YES_OPTION) {
						deleteEdge(otherEdge, oldIndices);

						if(neighbourEdge != null){
							deleteEdge(neighbourEdge, neighbourIndices);
						}
						selectEdge(selectedEdge, indices);
						updateModelAndGui();
					}
				
			}

		}

		/*
		 * This method identifies the indices of the neighbours
		 */
		private int[] leftAndRightIndex(ContigRadioButton radioButton,
				AdjacencyEdge selectedEdge) {

			/*
			 * at pos 0 left index at pos 1 right index
			 */
			int[] indices = new int[2];

			int indexLeft = -1;
			int indexRight = -1;

			if ((radioButton.isLeft() && !centralContig.isReverse())
					|| (!radioButton.isLeft() && centralContig.isReverse())) {

				if (centralContigIndex == selectedEdge.geti()) {
					indexLeft = selectedEdge.getj();
					indexRight = selectedEdge.geti();
				} else {
					indexLeft = selectedEdge.geti();
					indexRight = selectedEdge.getj();
				}

			} else if (!radioButton.isLeft() && !centralContig.isReverse()
					|| (radioButton.isLeft() && centralContig.isReverse())) {

				if (centralContigIndex == selectedEdge.geti()) {
					indexLeft = selectedEdge.geti();
					indexRight = selectedEdge.getj();
				} else {
					indexLeft = selectedEdge.getj();
					indexRight = selectedEdge.geti();
				}
			}

			indices[0] = indexLeft;
			indices[1] = indexRight;

			return indices;

		}

		/*
		 * This method delete an edge from the neighbour vectors
		 * and set the edge as deselected
		 */
		private void deleteEdge(AdjacencyEdge oldEdge, int[] indices) {

			Vector<AdjacencyEdge> neighbourl;
			Vector<AdjacencyEdge> neighbourR;

			int leftIndex = indices[0];
			int rightIndex = indices[1];
			
			/*
			 * set edge as not selected
			 */
			oldEdge.deselect();

			neighbourl = selectedLeftEdges.get(leftIndex);
			neighbourl.remove(oldEdge);
			
			neighbourR = selectedRightEdges.get(rightIndex);
			neighbourR.remove(oldEdge);
			
			/*
			 * Sometimes it is possible that I do not calculate the indices right
			 * (when the an ulterior selected edge an neighbour of the right side
			 * but currently the radio button is not right) 
			 */
			 if (!neighbourl.remove(oldEdge)&& !neighbourR.remove(oldEdge)){
				 neighbourl = selectedLeftEdges.get(rightIndex);
				 neighbourl.remove(oldEdge);
				 neighbourR = selectedRightEdges.get(leftIndex);
				 neighbourR.remove(oldEdge);
			 }

			updateModelAndGui();

		}

		/*
		 * This method add the selected edge in the both neighbour vectors
		 * at the right indices and set the edge as selected
		 */
		private void selectEdge(AdjacencyEdge selectedEdge, int[] indices) {

			Vector<AdjacencyEdge> contigCollectionL;
			Vector<AdjacencyEdge> contigCollectionR;
			
			int leftIndex = indices[0];
			int rightIndex = indices[1];
			
			/*
			 * set Edge as selected and save edge as left and right neighbour
			 */
			selectedEdge.select();
			
			contigCollectionL = selectedLeftEdges.get(leftIndex);
			contigCollectionL.add(selectedEdge);
			
			contigCollectionR = selectedRightEdges.get(rightIndex);		
			contigCollectionR.add(selectedEdge);

			updateModelAndGui();

		}

		private void updateModelAndGui() {
			
			JPanel rightContainer = con.getChooseContigPanel().getRightContainer();
			JPanel leftContainer = con.getChooseContigPanel().getLeftContainer();
			if (rightContainer.getComponentCount() != 0
					|| leftContainer.getComponentCount() != 0) {
				cagModel.sendCurrentContig();
				cagModel.sendLeftNeighbours();
				cagModel.sendRightNeighbours();
				con.getWindow().repaint();
			}
		}

}
