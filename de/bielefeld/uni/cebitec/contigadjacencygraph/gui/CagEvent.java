package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

public class CagEvent {

	private EventType event_type;
	private String data;
	/*
	 * TODO
	 * Evtl noch eine Varibel mit der ich Daten weiterleiten kann
	 */

	public CagEvent(EventType event, String data) {
		this.event_type = event;
		this.data = data;
	}

	public EventType getEvent_type() {
		return event_type;
	}
	
	public String getData(){
		return data;
	}

}