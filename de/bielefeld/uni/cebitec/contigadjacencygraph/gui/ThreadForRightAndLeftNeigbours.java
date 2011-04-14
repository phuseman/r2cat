package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.Component;

import javax.swing.SwingWorker;

public class ThreadForRightAndLeftNeigbours  extends SwingWorker<String, String>{

	private CagCreator myModel;
	private CagController con;
	private boolean isLeftNeighbour;

	public ThreadForRightAndLeftNeigbours(CagController controller, CagCreator model, boolean isLeft){
		this.myModel = model;
		this.con = controller;
		this.isLeftNeighbour = isLeft;
	}

	@Override
	protected String doInBackground() {
		if (isLeftNeighbour){
			myModel.sendLeftNeighbours();			
		}else{
			myModel.sendRightNeighbours();
		}
		return null;
	}

	@Override
	protected void done() {
		super.done();
		/*if(isLeftNeighbour){		
			
			 * TODO 
			 * denk daran sie auch wieder auf false zu setzten
			 
//			con.setLeftNeigboursReady(true);
			if(con.isRightNeighboursReady()){
				con.getChooseContigPanel().setFlag(true);
				con.getWindow().repaint();
				System.out.println("l rufe repaint auf "+con.isLeftNeigboursReady()+" "+con.isRightNeighboursReady());
			}
		}else{
//			con.setRightNeighboursReady(true);
			if(con.isLeftNeigboursReady()){
				con.getChooseContigPanel().setFlag(true);
				con.getWindow().repaint();
				System.out.println("r rufe repaint auf "+con.isLeftNeigboursReady()+" "+con.isRightNeighboursReady());
			}
		}*/
	}

}
