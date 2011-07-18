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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;

import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.util.force.DragForce;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataSizeAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ToolTipControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Tuple;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import prefuse.controls.NeighborHighlightControl;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.data.event.TupleSetListener;
import prefuse.data.query.SearchQueryBinding;
import prefuse.util.ui.JSearchPanel; 
import prefuse.data.Table;
import prefuse.util.FontLib;
import prefuse.controls.FocusControl;

/**
 * 
 * This class anticipates a prefuse-graph and 
 * creates a prefuse-display including a 
 * force-directed-graph-layout.
 * 
 * @author cmiele
 *
 */
public class PrefuseForceDirectedGraph
{
	private Display display = new Display(); 
	private JSearchPanel searchPanel;
	private Visualization vis = new Visualization();
	
	public PrefuseForceDirectedGraph(Graph g, Dimension d)
	{
		// Disable console output "INFO: Parsed Expression" - messages 
		Logger prefuseLogger = Logger.getLogger("prefuse");
		prefuseLogger.setLevel(Level.OFF);
		
		this.vis.add("graph", g);
	     
		// sets interactivity status for all items in given data group
		this.vis.setInteractive("graph.edges", null, true);
	     
		// create the label-renderer and edge-renderer and do some rendering with the nodes and edges
		CustomNodeRenderer nodeRenderer = new CustomNodeRenderer();
		EdgeRenderer edgerenderer = new EdgeRenderer();
		//edgerenderer.setHorizontalAlignment1(Constants.RIGHT);
	    //edgerenderer.setHorizontalAlignment2(Constants.LEFT);
	    edgerenderer.setEdgeType(0);
	    
		// create a default renderer factory and add node- and edge-renderer
		DefaultRendererFactory rendererFactory = new DefaultRendererFactory();
		rendererFactory.setDefaultRenderer(nodeRenderer);
		rendererFactory.setDefaultEdgeRenderer(edgerenderer);
	     
		// put renderer-factory to to visualisation
		this.vis.setRendererFactory(rendererFactory);
	     
		// black Text
		ColorAction nodeText = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR);
		nodeText.setDefaultColor(ColorLib.gray(0));
		// black outlines
		ColorAction nodeStroke = new ColorAction("graph.nodes", VisualItem.STROKECOLOR);
		nodeStroke.setDefaultColor(ColorLib.gray(0));
		// light grey edges
		ColorAction edgeStrokes = new ColorAction("graph.edges", VisualItem.STROKECOLOR);
		edgeStrokes.setDefaultColor(ColorLib.rgb(164,171,134));
		edgeStrokes.add("_fixed", ColorLib.rgb(0,0,0));
		edgeStrokes.add("_highlight", ColorLib.rgb(0,0,0));
	     
		// highlighting Adjacency nodes
		ColorAction fill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(204,204,153)); 
		fill.add(VisualItem.FIXED, ColorLib.rgb(255,204,51)); 
		fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(230,204,102)); 
	 
		// adjusted Forcesimulator
		ForceSimulator fsim = new ForceSimulator();
		float gravConstant = -20f; // the more negative, the more repelling
		float minDistance = 160f; // -1 for always on, the more positive, the more space between nodes
		float theta = 0.3f; // the lower, the more single-node repell calculation
		float drag = 0.01f;
		float springCoeff = 1E-4f; //1E-4
		fsim.addForce(new NBodyForce(gravConstant, minDistance, theta));
		fsim.addForce(new DragForce(drag));
		fsim.addForce(new SpringForce(springCoeff, 5));
	     
		// create action lists
		ActionList layout = new ActionList(7000);
		ActionList color = new ActionList(Activity.INFINITY);
		ActionList filter = new ActionList(Activity.INFINITY);
		
		// create DataSizeAction for EdgeSupport
		DataSizeAction edgeWeight = new DataSizeAction("graph.edges","drawableEdgeSupport");
		edgeWeight.setMaximumSize(4);
		edgeWeight.setMinimumSize(0.1);
		edgeWeight.setIs2DArea(false);
		
		// create DataSizeAction for NodeSupport
		DataSizeAction nodeWeight = new DataSizeAction("graph.nodes","drawableContigLength");
		nodeWeight.setMaximumSize(1.5);
		nodeWeight.setMinimumSize(1.1);
		nodeWeight.setIs2DArea(false);
		
		//add Edge Weight to filter
		filter.add(edgeWeight);
		filter.add(nodeWeight);
		
		// create layout
		ForceDirectedLayout fdl = new ForceDirectedLayout("graph");
		fdl.setForceSimulator(fsim);
		
		// assign colors to color action-list
		color.add(new RepaintAction());
		color.add(edgeStrokes);
		color.add(nodeStroke);
		color.add(nodeText);
		color.add(fill);
		
		// assign TreeLayout to layout-ActionList
	    layout.add(fdl);
  
	    // add the ActionList to the Visualization
	    this.vis.putAction("filter", filter);
	    this.vis.putAction("color", color);
	    this.vis.putAction("layout", layout);
	    
	    // adjust ToolTipLayout
	    Border border = BorderFactory.createLineBorder(Color.BLACK);
//	    UIManager.put("ToolTip.background", new ColorUIResource(204,204,153));
//	    UIManager.put("ToolTip.foreground",Color.BLACK);
//	    UIManager.put("ToolTip.bordercolor",Color.BLACK);
//	    UIManager.put("ToolTip.border", border);
	    
	    // put visualized data on display
	    this.display.setVisualization(vis);
	    this.display.addControlListener(new ToolTipControl("asciSupport"));
	    this.display.addControlListener(new ToolTipControl("realContigLength"));	    
	    this.display.addControlListener(new DragControl());
	    this.display.addControlListener(new PanControl());
	    this.display.addControlListener(new ZoomControl());
	    this.display.addControlListener(new FocusControl());
	    this.display.addControlListener(new NeighborHighlightControl());
	    this.display.setHighQuality(true);
	    this.display.setSize(d);

	    // run ActionLists
	    this.vis.run("filter");
	    this.vis.run("color");
	    this.vis.run("layout");
	    
	    SearchTupleSet search = new PrefixSearchTupleSet();
		this.vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
		
		fill.add("ingroup('_search_')", ColorLib.rgb(255,120,120));
		
		SearchQueryBinding sq = new SearchQueryBinding((Table) vis.getGroup("graph.nodes"), "label", (SearchTupleSet) vis.getGroup(Visualization.SEARCH_ITEMS));
		this.searchPanel = sq.createSearchPanel(); 
		
		this.searchPanel.setShowResultCount(true);
		this.searchPanel.setMaximumSize(new Dimension(400, 20));
//		this.searchPanel.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
		this.searchPanel.setVisible(true);
		//this.searchPanel.setBackground(new Color(204,204,153));
		this.searchPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		this.searchPanel.setShowCancel(false);
		this.searchPanel.setShowResultCount(false);
		
		// make nodes selectable via click
		display.addControlListener(new ControlAdapter() { 
			public void itemClicked(VisualItem item, MouseEvent e)
			{
				System.out.println(item.getString("label"));
			}
		});
		
		// fix selected focus nodes
        TupleSet focusGroup = vis.getGroup(Visualization.FOCUS_ITEMS); 
        focusGroup.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
            {
                for ( int i=0; i<rem.length; ++i )
                    ((VisualItem)rem[i]).setFixed(false);
                for ( int i=0; i<add.length; ++i ) {
                    ((VisualItem)add[i]).setFixed(false);
                    ((VisualItem)add[i]).setFixed(true);
                }
                if ( ts.getTupleCount() == 0 ) {
                    ts.addTuple(rem[0]);
                    ((VisualItem)rem[0]).setFixed(false);
                }
                vis.run("color");
            }
        });
	}  
	
	// returns the prefuse-display including a force directed prefuse graph
	public Display getOutputDisplay()
	{
		return this.display;
	}
	
	// returns the display depending Search-Panel
	public JSearchPanel getPanel()
	{
		return this.searchPanel;
	}
}