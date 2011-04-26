/*
 *  Copyright (C) 2011 phuseman
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

import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.qgram.MatchList.NotifyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;

/**
 *
 * @author phuseman
 */
public class ReferenceNode extends DataNode {

  public ReferenceNode(DataObject obj, Children ch, Lookup lookup) {
    super(obj, ch, lookup);
  }

  @Override
  protected Sheet createSheet() {
    Sheet sheet = Sheet.createDefault();
    Sheet.Set set = Sheet.createPropertiesSet();
    MatchListNBApiObject obj = getLookup().lookup(MatchListNBApiObject.class);

    try {

      Property cagCreation = new PropertySupport.Reflection(obj, Boolean.class, MatchListNBApiObject.PROP_SELECTEDFORCAG);

      cagCreation.setName(MatchListNBApiObject.PROP_SELECTEDFORCAG);
      cagCreation.setDisplayName("Use this reference to build a contig adjacency graph");

      set.put(cagCreation);


      Property treeDist = new PropertySupport.Reflection(obj, Double.class, MatchListNBApiObject.PROP_TREEDISTANCE);

      treeDist.setName(MatchListNBApiObject.PROP_TREEDISTANCE);
      treeDist.setDisplayName("Phylogenetic distance of this reference genome to the contigs genome");

      set.put(treeDist);



    } catch (NoSuchMethodException ex) {
      ErrorManager.getDefault().notify(ex);
    }

    sheet.put(set);
    return sheet;

  }


}