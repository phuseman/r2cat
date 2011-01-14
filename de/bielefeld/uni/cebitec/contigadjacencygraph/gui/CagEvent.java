package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.util.Vector;
import java.util.Map.Entry;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class CagEvent {

	private EventType event_type;
	private LayoutGraph graph;
	private int index;
	private AdjacencyEdge edge;
	private String data;
	private long size;
	private boolean repetitive;
	private boolean reverse;
	private  Vector<DNASequence>  contigData;
	private  DNASequence  contigNode;
	
	public CagEvent(EventType event, String data) {
		this.event_type = event;
		this.data = data;
	}
	public CagEvent(EventType event, DNASequence contig) {
		this.event_type = event;
		this.contigNode = contig;
	}
	
	public CagEvent(EventType event, LayoutGraph layoutGraph, int i){
		this.event_type = event;
		this.graph = layoutGraph;
		this.index = i;
	}
	
	public CagEvent(EventType event,  LayoutGraph layoutGraph, AdjacencyEdge includingEdge, int i) {
		this.event_type = event;
		this.graph = layoutGraph;
		this.edge = includingEdge;
		this.index = i;
	}
	
	
	public CagEvent(EventType event, Vector<DNASequence>  neighbours){
		this.event_type = event;
		this.contigData = neighbours;
	}

	public  DNASequence getContigNode() {
		return contigNode;
	}
	public  long getSize() {
		return size;
	}
	public boolean isRepetitive() {
		return repetitive;
	}
	public boolean isReverse() {
		return reverse;
	}
	
	public  EventType getEvent_type() {
		return event_type;
	}
	public String getData(){
		return data;
	}
	public Vector<DNASequence> getContigData() {
		return contigData;
	}
	public LayoutGraph getGraph() {
		return graph;
	}
	public int getIndex() {
		return index;
	}
	public AdjacencyEdge getEdge() {
		return edge;
	}
}