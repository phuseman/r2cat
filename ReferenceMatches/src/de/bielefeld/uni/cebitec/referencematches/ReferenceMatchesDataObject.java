/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.referencematches;

import de.bielefeld.uni.cebitec.qgram.MatchList;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

public class ReferenceMatchesDataObject extends MultiDataObject {

  private MatchList referenceMatches = null;

  public ReferenceMatchesDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
    super(pf, loader);
    CookieSet cookies = getCookieSet();
    cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
  }

  @Override
  protected Node createNodeDelegate() {
    return new DataNode(this,
            Children.create(new ReferenceNodeChildFactory(this), true),
            getLookup());
  }

  @Override
  public Lookup getLookup() {
              return  new ProxyLookup(new Lookup[]{Lookups.singleton(getReferenceMatches()),
              getCookieSet().getLookup()
            });
  }

  @Override
  public boolean isCopyAllowed() {
    return false;
  }

  @Override
  public boolean isMoveAllowed() {
    return false;
  }

  public MatchList getReferenceMatches() {
    if (referenceMatches == null) {

      referenceMatches = new MatchList();
      try {
        referenceMatches.readFromFile(FileUtil.toFile(this.getPrimaryFile()));
      } catch (IOException ex) {
        Exceptions.printStackTrace(ex);
      }
    }
    return referenceMatches;
  }
}
