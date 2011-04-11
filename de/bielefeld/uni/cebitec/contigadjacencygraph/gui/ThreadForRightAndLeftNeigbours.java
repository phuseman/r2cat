package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Component;

import javax.swing.SwingWorker;

public class ThreadForRightAndLeftNeigbours  extends SwingWorker<String, String>{

	private CagController con;
	private boolean isLeftNeighbour;

	public ThreadForRightAndLeftNeigbours(CagController controller, boolean isLeft){
		this.con = controller;
		this.isLeftNeighbour = isLeft;
	}

	@Override
	protected String doInBackground() {
		if (isLeftNeighbour){
			con.sendLeftNeighbours();			
		}else{
			con.sendRightNeighbours();
		}
		return null;
	}

	@Override
	protected void done() {
		super.done();
		if(isLeftNeighbour){		
			/*
			 * TODO 
			 * denk daran sie auch wieder auf false zu setzten
			 */
			con.setLeftNeigboursReady(true);
			if(con.isRightNeighboursReady()){
				con.getChooseContigPanel().setFlag(true);
			}
		}else{
			con.setRightNeighboursReady(true);
			if(con.isLeftNeigboursReady()){
				con.getChooseContigPanel().setFlag(true);
			}
		}
		con.getWindow().repaint();
	}

}
