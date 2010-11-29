package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

public enum EventType {

	/*
	 * Event which will be fired, if the user select a contig of the List
	 */
	EVENT_CHOOSED_CONTIG,
	

	/*
	 * Event which will be fired, if the user select a neighbor 
	 */
	EVENT_CHOOSED_NEIGHBOUR,
	

	EVENT_SEND_RIGHT_NEIGHBOURS,
	EVENT_SEND_LEFT_NEIGHBOURS,
	/*
	 * Event which will be fired, if the user cancel a contig from the order
	 */
	EVENT_CANCEL_CONTIG,
	
	/*
	 * Event which will be fired, if the user quit the programm 
	 */
	EVENT_QUIT_PROGRAMM

}