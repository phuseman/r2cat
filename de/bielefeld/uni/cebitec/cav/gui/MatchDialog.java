/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
 *   phuseman ät cebitec.uni-bielefeld.de                                     *
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import com.sun.java.help.impl.SwingWorker;
import com.sun.jmx.snmp.tasks.Task;

import de.bielefeld.uni.cebitec.cav.utils.CAVPrefs;

/**
 * @author phuseman
 *
 */
public class MatchDialog extends JFrame implements ActionListener, 
PropertyChangeListener {
	
	private Preferences prefs;

    private JProgressBar progressBar;
    private JButton startButton;
    private JTextArea taskOutput;
    private Task task;

    class qGramMatcherTask extends SwingWorker{

		@Override
		public Object construct() {
			// TODO Auto-generated method stub
			return null;
		}
  
    }

	
	public MatchDialog() {
		super();
		prefs = CAVPrefs.getPreferences();
		init();

		this.setSize(this.getPreferredSize());
		this.pack();
		this.setLocationByPlatform(true);
		this.setVisible(true);
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
	
	private void init() {
		;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}


}
