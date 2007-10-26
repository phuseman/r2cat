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

package de.bielefeld.uni.cebitec.cav.tests;

import java.io.File;

import junit.framework.TestCase;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.CSVParser;

/**
 * @author Peter Husemann
 * 
 * All test functions have the prefix "test" Check the values with:
 * 
 * assertEquals(); assertTrue();
 */
public class ParserTest extends TestCase {
	public void testParsefile() {
		CSVParser csvParser = new CSVParser(new File("tests/query.csv"));
		AlignmentPositionsList apl = csvParser.parse();
		assertTrue(apl != null);
		assertEquals(435, apl.size());
	}

}
