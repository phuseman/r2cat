package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Component;

import javax.swing.SwingWorker;

public class ThreadForRightAndLeftNeigbours  extends SwingWorker<String, String>{

	private CAGWindow window;
	private boolean isLeftNeighbour;

	public ThreadForRightAndLeftNeigbours(CAGWindow cagw, boolean isLeft){
		this.window = cagw;
		this.isLeftNeighbour = isLeft;
	}

	@Override
	protected String doInBackground() {
		if (isLeftNeighbour){
			window.model.sendLeftNeighbours();			
		}else{
			window.model.sendRightNeighbours();
		}
		return null;
	}

	@Override
	protected void done() {
		super.done();
		if(isLeftNeighbour){			
			window.leftNeigboursReady = true;
			if(window.rightNeighboursReady){
				window.chooseContigPanel.setFlag(true);
			}
		}else{
			window.rightNeighboursReady = true;
			if(window.leftNeigboursReady){
				window.chooseContigPanel.setFlag(true);
			}
		}
		window.repaint();
	}

}
