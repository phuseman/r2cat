/***************************************************************************
 *   Copyright (C) 2007 by Peter Husemann                                  *
 *   phuseman@cebitec.uni-bielefeld.de                                     *
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

package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.bielefeld.uni.cebitec.cav.controller.GuiController;

/**
 * @author Peter Husemann
 * 
 */
public class MainWindow extends JFrame implements ChangeListener{

	private static final long serialVersionUID = 3695451270541548008L;

	private boolean centerWindow = true;

	private JPanel controls;
	
	private DataViewPlugin  dataViewPlugin = null;

	protected JScrollPane drawing;

	protected JTextField zoomValue;

	protected JSlider zoomSlider;


	protected MainMenu menuBar;

	private GuiController guiController;

	/**
	 * Calls the super constructor and initializes the window
	 */
	public MainWindow(GuiController guiController) {
		super("Comparative Assembly Viewer");
		this.guiController = guiController;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();

	}

	/**
	 * Initializes all necessary parameters for the main window. (Size,
	 * position, compontents and so on)
	 */
	private void init() {
		Container content = this.getContentPane();

		// determine the windowsize (golden ratio, if possible)
		int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

		Dimension windowsize = new Dimension((int) Math.floor(width / 1.618),
				(int) Math.floor(height / 1.618));
		if (windowsize.width < 640 || windowsize.height < 480) {
			windowsize = new Dimension(640, 480);
		}

		this.setSize(windowsize);
		this.setPreferredSize(windowsize);

		if (centerWindow) {
			Rectangle bounds = this.getGraphicsConfiguration().getBounds();
			this.setLocation((width - windowsize.width) / 2 + bounds.x,
					(height - windowsize.height) / 2 + bounds.y);
		}


		controls = new JPanel();
		controls.setBorder(BorderFactory.createEtchedBorder());

		JPanel zoomPanel = new JPanel();
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));

		zoomSlider = new JSlider();
		zoomSlider.addChangeListener(this);
		zoomSlider.setOrientation(SwingConstants.HORIZONTAL);
		zoomSlider.setMaximum(200);
		zoomSlider.setMinimum(5);

		zoomPanel.add(zoomSlider);

		zoomValue = new JTextField(5);
		zoomValue.setText("1.0");
		zoomPanel.add(zoomValue);

		controls.add(zoomPanel);

		drawing = new JScrollPane();
		drawing.setBorder(BorderFactory.createTitledBorder("Alignments"));

		content.setLayout(new BorderLayout());
		content.add(controls, BorderLayout.SOUTH);
		content.add(drawing, BorderLayout.CENTER);

		menuBar = new MainMenu(guiController);
		this.setJMenuBar(menuBar);

		this.pack();
	}

	/**
	 * Sets the visualisation plugin and registers different listeners to the
	 * plugin.
	 * 
	 * @param dotPlotVisualisation
	 */
	public void setVisualisation(DataViewPlugin dataViewPlugin) {
		this.addComponentListener(dataViewPlugin);
		this.dataViewPlugin=dataViewPlugin;

		drawing.setViewportView( (Component) dataViewPlugin);
		drawing.validate();
		drawing.setVisible(true);
	}

	
	/**
	 * Changes the zoom level when the zoom slider is moved
	 * @param e
	 */
	public void stateChanged(ChangeEvent e) {
		if (zoomSlider != null && zoomValue != null
				&& e.getSource().equals(zoomSlider)) {
			double zoom = 1.;
			zoom = zoomSlider.getValue() / 20.;
			zoomValue.setText(Double.toString(zoom));
			if (dataViewPlugin != null) {
				dataViewPlugin.setZoom(zoom);
			}
		}
	}

}
