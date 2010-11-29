package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.util.Map.Entry;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;

public class CagEvent {

	private EventType event_type;
	private String data;
	private long size;
	private boolean repetitiv;
	private Contig contig;
	private  Contig[]  contigData;
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
	
	public CagEvent(EventType event, Contig currentContig){
		this.event_type = event;
		
	}
	
	public CagEvent(EventType event, Contig[]  neighbours){
		this.event_type = event;
		this.contigData = neighbours;
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
	
	public Contig[] getContigData() {
		return contigData;
	}
	public Contig getContig() {
		return contig;
	}


}