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

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author phuseman
 */
public class ContigRootNode extends AbstractNode {

  public ContigRootNode() {
    super(new ContigSetNode());
  }

  @Override
  public Cookie getCookie(Class clazz) {
// http://www.sepix.de/blogs/blogrittner/blog/archive/2008/june/26/beandev_minimaler_einsatz_fuer_drag_and_drop_von_nodes/index.html
//Geertjan schreibt: Dienstag,14-10-08 10:44
//Excellent. But the Cookie references should now be replaced by Lookup instead. As far as possible one should try to avoid using the Cookies. I will work through the examples here and blog about them myself and try to use Lookup instead.
    Children ch = getChildren();

    if (clazz.isInstance(ch)) {
      // hier den Children-Container bekannt machen:
      return (Cookie) ch;
    }
    return super.getCookie(clazz);
  }


}
