/***************************************************************************
 *   Copyright (C) 2010 by Christian Miele                                 *
 *   cmiele  a t  cebitec.uni-bielefeld.de                                 *
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

package de.bielefeld.uni.cebitec.contigadjacencygraph.visualisation;

import java.util.Locale;

import prefuse.data.Graph;
import prefuse.data.Table;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;

/**
 * 
 * This class is a wrapper which converts the Contig-Adjacency-Graph 
 * calculated from treecat into the graph-data-format of Prefuse.
 * 
 * @author cmiele
 *
 */

public class ContigGraph extends Graph
{
	private LayoutGraph lg;
	private Table nodes = new Table();
	private Table edges = new Table();
	
	public ContigGraph(LayoutGraph layoutGraph)
	{
		super(false);
		this.lg = layoutGraph;

		this.nodes.addColumn("label", String.class);
		this.nodes.addColumn("key", int.class);
		this.nodes.addColumn("drawableContigLength", int.class);
		this.nodes.addColumn("realContigLength", String.class);
		
		this.nodes.addColumn("description", String.class);
		this.nodes.addColumn("totalAlignmentLength", double.class);
		this.nodes.addColumn("repetetivePercent", double.class);
		this.nodes.addColumn("offset", double.class);
		
		this.edges.addColumn("rightConnector", int.class);
		this.edges.addColumn("leftConnector", int.class);
		this.edges.addColumn("drawableEdgeSupport", double.class);
		//this.edges.addColumn("realEdgeSupport", String.class);
		this.edges.addColumn("asciSupport", String.class);
		
		for (int nodeCounter = 0 ; nodeCounter < this.lg.getNodes().size() ; nodeCounter++)
		{
			int nodeRowCounter = this.nodes.addRow();
			
			this.nodes.set(nodeRowCounter, "label", this.lg.getNodes().get(nodeCounter).getId());
			this.nodes.set(nodeRowCounter, "key", nodeRowCounter);  // TODO, getNodeCopy einfließen lassen!
			this.nodes.set(nodeRowCounter, "drawableContigLength", this.lg.getNodes().get(nodeCounter).getSize());

			
			String arrangedDescription = this.lg.getNodes().get(nodeCounter).getDescription();
			this.nodes.set(nodeRowCounter, "description", arrangedDescription);
			this.nodes.set(nodeRowCounter, "totalAlignmentLength", this.lg.getNodes().get(nodeCounter).getTotalAlignmentLength());
			this.nodes.set(nodeRowCounter, "repetetivePercent", this.lg.getNodes().get(nodeCounter).getRepetitivePercent());
			
			Double doubleOffset = (double)this.lg.getNodes().get(nodeCounter).getOffset();
			this.nodes.set(nodeRowCounter, "offset", doubleOffset);
			
			int realContigLength = (int)this.lg.getNodes().get(nodeCounter).getSize();
			String stringRealContigLength = String.valueOf(realContigLength);
			stringRealContigLength = stringRealContigLength + " base pairs";
			
			this.nodes.set(nodeRowCounter, "realContigLength", stringRealContigLength);	
		}
		
		for (int edgeCounter = 0 ; edgeCounter < this.lg.getEdges().size() ; edgeCounter++)
		{
			int edgeRowCounter = this.edges.addRow();
			
			int leftConnector = this.lg.getEdges().get(edgeCounter).geti();
			int rightConnector = this.lg.getEdges().get(edgeCounter).getj();
			
			LayoutGraph.AdjacencyEdge e = this.lg.getEdges().get(edgeCounter);
			//System.out.println(String.format((Locale)null,"Support: %.2f\n(%.2f%%) %s (%.2f%%)", e.getSupport(), e.getRelativeSupporti()*100, e.showContigConnectionAsciiString(), e.getRelativeSupportj()*100 ));
			String asciSupport = String.format((Locale)null,"Support: %.2f\n(%.2f%%) %s (%.2f%%)", e.getSupport(), e.getRelativeSupporti()*100, e.showContigConnectionAsciiString(), e.getRelativeSupportj()*100 );
			//System.out.println(asciSupport);
			asciSupport = asciSupport.replaceAll("<", " &lt ");
			asciSupport = asciSupport.replaceAll(">", " &gt ");
			asciSupport = asciSupport.replaceAll("\n", "<br>");
			asciSupport = "<html><body><center>"+asciSupport+"</center></body></html>";
			
			if(this.lg.getEdges().get(edgeCounter).isLeftConnectori())
			{
				rightConnector = this.lg.getEdges().get(edgeCounter).geti();
			}

			if(this.lg.getEdges().get(edgeCounter).isRightConnectori())
			{
				leftConnector = this.lg.getEdges().get(edgeCounter).geti();
			}

			if(this.lg.getEdges().get(edgeCounter).isLeftConnectorj())
			{
				rightConnector = this.lg.getEdges().get(edgeCounter).getj();
			}

			if(this.lg.getEdges().get(edgeCounter).isRightConnectorj())
			{
				leftConnector = this.lg.getEdges().get(edgeCounter).getj();
			}	
			
			double drawableEdgesupport = Math.log(this.lg.getEdges().get(edgeCounter).getSupport())/10;
			//double realEdgeSupport = this.lg.getEdges().get(edgeCounter).getSupport();
			//String stringRealEdgeSupport = String.valueOf(realEdgeSupport);
			//stringRealEdgeSupport = stringRealEdgeSupport + " edge support";

			this.edges.set(edgeRowCounter, "drawableEdgeSupport", drawableEdgesupport);
			//this.edges.set(edgeRowCounter, "realEdgeSupport", stringRealEdgeSupport);
			this.edges.set(edgeRowCounter, "asciSupport", asciSupport);
			this.edges.set(edgeRowCounter, "leftConnector", leftConnector);
	        this.edges.set(edgeRowCounter, "rightConnector", rightConnector);
		}
		this.init(this.nodes,this.edges,false,"key","leftConnector","rightConnector");
	}
}