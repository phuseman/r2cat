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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author phuseman
 */
public class MatchListNBApiObject extends MatchList {

  private List listeners = Collections.synchronizedList(new LinkedList());
  boolean selectedForContigAdjacencyGraphCreation = false;

  public Boolean isSelectedForContigAdjacencyGraphCreation() {
    return selectedForContigAdjacencyGraphCreation;
  }

  public void setSelectedForContigAdjacencyGraphCreation(Boolean isSelectedForContigAdjacencyGraphCreation) {
    boolean old = this.selectedForContigAdjacencyGraphCreation;
    this.selectedForContigAdjacencyGraphCreation = isSelectedForContigAdjacencyGraphCreation;
    fire("isSelectedForContigAdjacencyGraphCreation", old, isSelectedForContigAdjacencyGraphCreation);
  }


  public void addPropertyChangeListener(PropertyChangeListener pcl) {
    listeners.add(pcl);
  }

  public void removePropertyChangeListener(PropertyChangeListener pcl) {
    listeners.remove(pcl);
  }

  private void fire(String propertyName, Object old, Object nue) {
    //Passing 0 below on purpose, so you only synchronize for one atomic call:
    PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
    for (int i = 0; i < pcls.length; i++) {
      pcls[i].propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
    }
  }
}
