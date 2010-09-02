/***************************************************************************
 *   Copyright (C) 2010 by Yvonne Herrmann, Peter Husemann                  *
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

package de.bielefeld.uni.cebitec.cav.primerdesign;

import java.util.Hashtable;

/**
 * Interface to handle the tags while going through a xml-file.
 * It is based on the DocumentHandler from SAX (Simple API for XML).
 * 
 * 
 ****************************************************************************
 * SAX 1.0																	*
 * Version 1.0 of the Simple API for XML (SAX), created collectively 		*
 * by the membership of the XML-DEV mailing list, is hereby released into 	*
 * the public domain.														*
 * 																			*
 * No one owns SAX: you may use it freely in both commercial and 			*
 * non-commercial applications, bundle it with your software distribution, 	*
 * include it on a CD-ROM, list the source code in a book, mirror the 		*
 * documentation at your own web site, or use it in any 					*
 * other way you see fit.													*
 * 																		   	*
 * David Megginson, Megginson Technologies Ltd. 1998-05-11                 	*
 *                                                                         	*
 ***************************************************************************/

public interface DocumentHandler {
	
	/**
	 * Method is called when a element of XML starts
	 *
	 * @param tag represents the xml-tag
	 * @param attrs includes the expression infront of '=' as a key and 
	 * the following expression as attributes
	 * @throws Exception
	 */
	
  public void startElement(String tag,Hashtable attributes) throws Exception;
  
  /**
   * Method is called when a element of XML ends
   * @param tag
   * @throws Exception
   */
  
  public void endElement(String tag) throws Exception;
  
  /**
   * Method is called when XML-file starts.
   * @throws Exception
   */
  
  public void startDocument() throws Exception;
  
  /**
   * Method is called when XML-file ends.
   * @throws Exception
   */
  
  public void endDocument() throws Exception;
  
  /**
   * Method is called if there is a value between the XML-tags
   * @param str
   * @throws Exception
   */
  
  public void value(String str) throws Exception;
}
