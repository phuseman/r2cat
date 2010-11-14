package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

public class CagEvent {

	private EventType event_type;
	private String data;
	private long size;
	private boolean repetitiv;
	/*
	 * TODO
	 * Evtl noch eine Varibel mit der ich Daten weiterleiten kann
	 */
	public CagEvent(EventType event, String data) {
		this.event_type = event;
		this.data = data;
	}
	public CagEvent(EventType event, String data, long size, boolean repetitiv) {
		this.event_type = event;
		this.data = data;
		this.size = size;
		this.repetitiv = repetitiv;
	}

	public long getSize() {
		return size;
	}
	public boolean isRepetitiv() {
		return repetitiv;
	}
	public EventType getEvent_type() {
		return event_type;
	}
	
	public String getData(){
		return data;
	}

}