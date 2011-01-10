/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

