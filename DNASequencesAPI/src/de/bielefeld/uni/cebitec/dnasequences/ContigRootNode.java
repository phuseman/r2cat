/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
