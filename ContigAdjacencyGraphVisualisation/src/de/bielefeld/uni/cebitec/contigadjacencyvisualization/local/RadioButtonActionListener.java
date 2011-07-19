/***************************************************************************
 *   Copyright (C) 2010/11 by Annica Seidel                                *
 *   aseidel  a t  cebitec.uni-bielefeld.de                                *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
package de.bielefeld.uni.cebitec.contigadjacencyvisualization.local;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JOptionPane;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;

public class RadioButtonActionListener implements ActionListener {

	private CagCreator cagModel;
	private int centralContigIndex;

	
	public RadioButtonActionListener (CagCreator model){

		this.cagModel = model;
		this.centralContigIndex = cagModel.getCurrentContigIndex();

	}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() instanceof ContigRadioButton){
				
				ContigRadioButton radioButton = 
					(ContigRadioButton) e.getSource();
				
				AdjacencyEdge selectedEdge = radioButton.getEdge();
			/*
			 * Here are the options to react on a selection of a radion Button
			 * next to the contigs
			 * 
			 *  If there is not a neighbour selected 
			 */
		 if (e.getActionCommand().equals(
					"noch kein nachbar ausgewaehlt")) {

				int[] indices = leftAndRightIndex(radioButton, selectedEdge);
				/*
				 * the adjacency will be set as selected
				 */
				selectEdge(selectedEdge, indices);

				/*
				 * if there is a neighbour already selected
				 */
			} else if (e.getActionCommand().equals("nachbarAusgewaehlt")) {

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

					int n = javax.swing.JOptionPane.showOptionDialog(radioButton.getParent(),
							"You already used this neighbour for an another adjacency.\n"
									+ " Do you want to delete that choice and want to choose this selection?",
							"", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);

					if (n == JOptionPane.YES_OPTION) {
						deleteEdge(otherEdge, oldIndices);

						if(neighbourEdge != null){
							deleteEdge(neighbourEdge, neighbourIndices);
						}
						selectEdge(selectedEdge, indices);
						updateModel();
					}
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

			if ((radioButton.isLeft() && !false)
					|| (!radioButton.isLeft() && false)){

				if (centralContigIndex == selectedEdge.geti()) {
					indexLeft = selectedEdge.getj();
					indexRight = selectedEdge.geti();
				} else {
					indexLeft = selectedEdge.geti();
					indexRight = selectedEdge.getj();
				}

			} else if (!radioButton.isLeft() && !false
					|| (radioButton.isLeft() && false)){

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

			neighbourl = cagModel.getSelectedLeftEdges().get(leftIndex);
			neighbourl.remove(oldEdge);
			
			neighbourR = cagModel.getSelectedRightEdges().get(rightIndex);
			neighbourR.remove(oldEdge);
			
			/*
			 * Sometimes it is possible that I do not calculate the indices right
			 * (when the an ulterior selected edge an neighbour of the right side
			 * but currently the radio button is not right) 
			 */
			 if (!neighbourl.remove(oldEdge)&& !neighbourR.remove(oldEdge)){
				 neighbourl = cagModel.getSelectedLeftEdges().get(rightIndex);
				 neighbourl.remove(oldEdge);
				 neighbourR = cagModel.getSelectedRightEdges().get(leftIndex);
				 neighbourR.remove(oldEdge);
			 }

			updateModel();

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
			
			contigCollectionL = cagModel.getSelectedLeftEdges().get(leftIndex);
			contigCollectionL.add(selectedEdge);
			
			contigCollectionR = cagModel.getSelectedRightEdges().get(rightIndex);		
			contigCollectionR.add(selectedEdge);
			System.out.println("left "+cagModel.getSelectedLeftEdges().get(leftIndex));
			System.out.println("right "+cagModel.getSelectedRightEdges().get(rightIndex));
			updateModel();

		}

		/*
		 * if the user select a contig by this radio button 
		 * the model have to be changed.
		 * which adjacency is selected, which have to be deselected
		 * will be set above
		 * but the contigs have to be repainted this will be done here
		 */
		private void updateModel() {
			
			cagModel.changeContigs(centralContigIndex, cagModel.isCurrentContigIsReverse());
			
		}

}
