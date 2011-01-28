/*
 *  Copyright (C) 2011 Peter Husemann <peter.husemann at cebitec uni bielefeld.de>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.bielefeld.uni.cebitec.dnasequences;

import java.util.Vector;
import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 *
 * @author phuseman
 */
class ContigSetNode extends Index.ArrayChildren {

  public ContigSetNode() {
    super();
  }

  public ContigSetNode(Vector<Contig> contigs) {
    super();
    for (Contig contig : contigs) {
      add(createNodes(contig));
    }
  }



  protected Node[] createNodes(Object o) {
    Contig contig = (Contig) o;
    ContigNode result = new ContigNode (contig);
    return new Node[] { result };
}

}

