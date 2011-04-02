/***************************************************************************
 *   Copyright (C) Jan 25, 2011 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.referencematches;

import de.bielefeld.uni.cebitec.qgram.DNASequence;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author phuseman
 */
public class ReferenceNodeChildFactory extends ChildFactory<String> {
private final ReferenceMatchesDataObject referenceMatchesDataObject;

  public ReferenceNodeChildFactory(ReferenceMatchesDataObject referenceMatchesDataObject) {
    this.referenceMatchesDataObject = referenceMatchesDataObject;
  }


     @Override
    protected boolean createKeys(List<String> toPopulate) {
       Vector<DNASequence> references =  referenceMatchesDataObject.getReferenceMatches().getTargets();
 
       for (Iterator<DNASequence> it = references.iterator(); it.hasNext();) {
         DNASequence dNASequence = it.next();
         toPopulate.add(dNASequence.getId());
       }

        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        Node childNode = new AbstractNode(Children.LEAF,referenceMatchesDataObject.getLookup());
        childNode.setDisplayName(key);
        return childNode;
    }

}
