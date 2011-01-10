/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.dnasequences;

import java.io.File;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author phuseman
 */
public class ContigNode extends AbstractNode {

  private InstanceContent content;

  public ContigNode(Contig data) {
    this(data, new InstanceContent());
//        super (new ContigsList(), Lookups.singleton(c));
    setDisplayName(data.getId());
  }

  public ContigNode(Contig data, InstanceContent content) {
    super(Children.LEAF, new AbstractLookup(content));
    this.content = content;
    content.add(data);
  }

  public Contig getData() {
    return getLookup().lookup(Contig.class);
  }


  @Override
  public boolean canCut() {
    return true;
  }

  @Override
  public String getName() {
    return getData().getId();
  }

  protected Sheet createSheet() {

    Sheet sheet = Sheet.createDefault();
    Sheet.Set set = Sheet.createPropertiesSet();
    Contig contig = getLookup().lookup(Contig.class);

    try {
        Property iDProp   = new PropertySupport.Reflection(contig, String.class, "getId", null);
        Property descProp = new PropertySupport.Reflection(contig, String.class, "getDescription", "setDescription");
        Property sizeProp = new PropertySupport.Reflection(contig, Integer.class, "getSize", null);
        Property fileProp = new PropertySupport.Reflection(contig, File.class, "fastaFile");

        iDProp.setName("ID");
        descProp.setName("Description");
        sizeProp.setName("Size");
        fileProp.setName("Fasta File");

        set.put(iDProp);
        set.put(descProp);
        set.put(sizeProp);
        set.put(fileProp);

    } catch (NoSuchMethodException ex) {
        ErrorManager.getDefault();
    }

    sheet.put(set);
    return sheet;

}
}
