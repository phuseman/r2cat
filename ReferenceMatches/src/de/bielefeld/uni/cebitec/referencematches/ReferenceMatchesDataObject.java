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
package de.bielefeld.uni.cebitec.referencematches;

import java.io.IOException;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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

  private MatchListNBApiObject referenceMatches = null;

  public ReferenceMatchesDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
    super(pf, loader);
    CookieSet cookies = getCookieSet();
    cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
  }

  @Override
  protected Node createNodeDelegate() {
    return new ReferenceNode(this,
            Children.create(new ReferenceNodeChildFactory(this), true),
            getLookup());
  }

  @Override
  public Lookup getLookup() {
            Project p = FileOwnerQuery.getOwner(this.getPrimaryFile());
            if(p!=null){
              return  new ProxyLookup(
                      new Lookup[]{
                Lookups.singleton(getReferenceMatches()),
              getCookieSet().getLookup(),
                //include the project into the lookup. I found no better way..
              Lookups.singleton(p)
            });
            } else {
              return  new ProxyLookup(new Lookup[]{Lookups.singleton(getReferenceMatches()),
              getCookieSet().getLookup()
            });
    }
  }

  @Override
  public boolean isCopyAllowed() {
    return false;
  }

  @Override
  public boolean isMoveAllowed() {
    return false;
  }

  public MatchListNBApiObject getReferenceMatches() {
    if (referenceMatches == null) {

      referenceMatches = new MatchListNBApiObject();
      try {
        referenceMatches.readFromFile(FileUtil.toFile(this.getPrimaryFile()));
      } catch (IOException ex) {
        Exceptions.printStackTrace(ex);
      }
    }
    return referenceMatches;
  }
}
