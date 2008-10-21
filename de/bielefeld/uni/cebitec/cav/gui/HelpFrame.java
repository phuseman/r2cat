/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
 *   phuseman  a t  cebitec.uni-bielefeld.de                               *
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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

/**
 * @author phuseman
 * 
 */
public class HelpFrame extends JFrame implements HyperlinkListener, ActionListener {

	private JEditorPane editorpane;

	public HelpFrame() {
		super("Help");

		editorpane = new JEditorPane();
		editorpane.setEditable(false);
		editorpane.addHyperlinkListener(this);

		URL url = Thread.currentThread().getContextClassLoader().getResource(
				"extra/howto.html");

		this.setPage(url);
		this.getContentPane().add(new JScrollPane(editorpane));
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		// TODO Auto-generated method stub
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			this.setPage(e.getURL());
		}
	}

	public void setPage(URL url) {
		try {
			if (url.getProtocol().startsWith("http")) {
				if (java.awt.Desktop.isDesktopSupported()) {
					try {
						java.awt.Desktop.getDesktop().browse(url.toURI());
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
				JOptionPane.showMessageDialog(this, url.toExternalForm(),
						"Please open this url:",
						JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				editorpane.setPage(url);
				if (editorpane.getDocument().getClass() == HTMLDocument.class) {
					URL base = new URL(url.toExternalForm());
					((HTMLDocument) editorpane.getDocument()).setBase(base);
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
