/***************************************************************************
 *   Copyright (C) 2010 by Christian Miele                                 *
 *   cmiele  a t  cebitec.uni-bielefeld.de                               *
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

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;

import org.freehep.util.export.ExportDialog;

import prefuse.Display;
import prefuse.data.Graph;
import prefuse.util.ui.JSearchPanel;

/**
 * 
 * This class aggregates a controlpanel and the visualisation of the different 
 * prefuse-displays, which include the selected graph-layouts.
 *
 * @author cmiele
 *
 */

public class TreecatGraphFrame implements ActionListener
{
	private JFrame window = new JFrame(" - t r e e c a t | o u t p u t - ");  
	
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private Graph graph;
	private Display disp;
	
	private JPanel displayPanel = new JPanel();
	private JPanel controlPanel = new JPanel();

	private JSearchPanel searchPanel;
	
	private Dimension d;

	public TreecatGraphFrame(Graph g)
	{ 
	    JButton fdlbtn = new JButton("Force - Directed - Layout");
	    //fdlbtn.setBackground(new Color(60,60,60));
	    //fdlbtn.setForeground(new Color(204,204,153));
	    fdlbtn.setBorder(BorderFactory.createLineBorder(Color.black));
	    
	    JButton rlbtn = new JButton("Radial - Layout");
	    //rlbtn.setBackground(new Color(60,60,60));
	    //rlbtn.setForeground(new Color(204,204,153));
	    rlbtn.setBorder(BorderFactory.createLineBorder(Color.black));
	    
	    JButton frbtn = new JButton("Fruchterman - Reingold - Layout");
	    //frbtn.setBackground(new Color(60,60,60));
	    //frbtn.setForeground(new Color(204,204,153));
	    frbtn.setBorder(BorderFactory.createLineBorder(Color.black));
	    
	    JButton exportbtn = new JButton("Export Graph");
	    //exportbtn.setBackground(new Color(60,60,60));
	    //exportbtn.setForeground(new Color(204,204,153));
	    exportbtn.setBorder(BorderFactory.createLineBorder(Color.black));
	    
	    fdlbtn.setActionCommand("fdl");
	    rlbtn.setActionCommand("rl");
	    frbtn.setActionCommand("frl");
	    exportbtn.setActionCommand("exppng");
	    
	    fdlbtn.addActionListener(this);
	    rlbtn.addActionListener(this);
	    frbtn.addActionListener(this);
	    exportbtn.addActionListener(this);

	    this.controlPanel.add(fdlbtn);
	    this.controlPanel.add(rlbtn);
	    this.controlPanel.add(frbtn);
	    this.controlPanel.add(exportbtn);
	    
	    this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.window.setSize(Toolkit.getDefaultToolkit().getScreenSize());
	
	    this.controlPanel.setBackground(new Color(255,255,255));
	    this.controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	    this.window.add(controlPanel, BorderLayout.NORTH);
	    
	    this.displayPanel.setBackground(new Color(255,255,255));
	
	    this.window.add(displayPanel);
	    this.window.setVisible(true);
	    
	    this.d = this.displayPanel.getSize();
	    double dispHeight = this.d.getHeight() - 40;
	    double dispWidth = this.d.getWidth();
	    d.setSize((int)dispWidth,(int)dispHeight);
	    
		this.setactualGraph(g);
		this.init("frl", this.getactualGraph());
	}  

	private void init(String str, Graph g)
	{
		Graph graph = g;
		String string = str;

	    if ( string.equals("fdl") )
	    {
	    	PrefuseForceDirectedGraph pfdg = new PrefuseForceDirectedGraph(graph,d);
	    	this.disp = pfdg.getOutputDisplay();
	    	this.disp.pan(this.getHalfScreenWidth(this.screenSize)-30, this.getHalfScreenHeight(this.screenSize)-80);
	    	this.displayPanel.add(this.disp);
	    	this.searchPanel = pfdg.getPanel();
	    	this.controlPanel.add(this.searchPanel);
	    	this.displayPanel.updateUI();
	    }
	    if ( string.equals("rl") )
	    {
	    	 PrefuseRadialGraph prg = new PrefuseRadialGraph(graph,d);
	    	 this.disp = prg.getOutputDisplay();
	    	 this.displayPanel.add(this.disp);
	    	 this.searchPanel = prg.getPanel();
	    	 this.controlPanel.add(this.searchPanel);
	    	 this.displayPanel.updateUI();
	    }
	    if ( string.equals("frl") )
	    {
	    	 PrefuseFruchtermanReingoldGraph pfrg = new PrefuseFruchtermanReingoldGraph(graph,d);
	    	 this.disp = pfrg.getOutputDisplay();
	    	 this.displayPanel.add(this.disp);
	    	 this.searchPanel = pfrg.getPanel();	
	    	 this.controlPanel.add(this.searchPanel);
	    	 this.displayPanel.updateUI();
	    }    
	}

	private double getHalfScreenWidth(Dimension d)
	{
		double centerPositionX = this.screenSize.getWidth()/2;
		return centerPositionX;
	}
	
	private double getHalfScreenHeight(Dimension d)
	{
		double centerPositionY = this.screenSize.getHeight()/2;
		return centerPositionY;
	}
	
	public JFrame getOutputFrame()
	{
		return this.window;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if ("fdl".equals(e.getActionCommand()))
		{
			this.controlPanel.remove(this.searchPanel);
			this.displayPanel.updateUI();
			this.displayPanel.removeAll();
			this.setactualGraph(this.getactualGraph());
			this.init("fdl", this.getactualGraph());
		}
		if ("rl".equals(e.getActionCommand()))
		{
			this.controlPanel.remove(this.searchPanel);
			this.displayPanel.updateUI();
			this.displayPanel.removeAll();
			this.setactualGraph(this.getactualGraph());
			this.init("rl", this.getactualGraph());	
		}
		if ("frl".equals(e.getActionCommand()))
		{
			this.controlPanel.remove(this.searchPanel);
			this.displayPanel.updateUI();
			this.displayPanel.removeAll();
			this.setactualGraph(this.getactualGraph());
			this.init("frl", this.getactualGraph());	
		}
		if ("exppng".equals(e.getActionCommand()))
		{	
			this.exportAsPNG();
		}
    }

	private Graph getactualGraph()
	{
		return this.graph;
	}
	
	private void setactualGraph(Graph g)
	{
		this.graph = g;
	}
	
	private void exportAsPNG()
	{	
		try
		{
			OutputStream fout = new FileOutputStream("/home/chrm/ActualGraph.png");
			this.disp.saveImage(fout, "PNG", 2.0);
		}
		
		catch(IOException e)
		{
			System.out.println(e + "file not found");
		}
	}
}