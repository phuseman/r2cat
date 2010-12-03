package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.util.Map.Entry;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class CagEvent {

	private EventType event_type;
	private String data;
	private long size;
	private boolean repetitive;
	private boolean reverse;
	private Contig contig;
	private  DNASequence[]  contigData;
	/*
	 * TODO
	 * Evtl noch eine Varibel mit der ich Daten weiterleiten kann
	 */
	public CagEvent(EventType event, String data) {
		this.event_type = event;
		this.data = data;
	}
	public CagEvent(EventType event, String data, long size, boolean repetitive, boolean reverse) {
		this.event_type = event;
		this.data = data;
		this.size = size;
		this.repetitive = repetitive;
		this.reverse = reverse;
	}
	
	public CagEvent(EventType event, Contig currentContig){
		this.event_type = event;
		
	}
	
	public CagEvent(EventType event, DNASequence[]  neighbours){
		this.event_type = event;
		this.contigData = neighbours;
	}

	public long getSize() {
		return size;
	}
	public boolean isRepetitive() {
		return repetitive;
	}
	public boolean isReverse() {
		return reverse;
	}
	
	public EventType getEvent_type() {
		return event_type;
	}
	
	public String getData(){
		return data;
	}
	
	public DNASequence[] getContigData() {
		return contigData;
	}
	public Contig getContig() {
		return contig;
	}


}