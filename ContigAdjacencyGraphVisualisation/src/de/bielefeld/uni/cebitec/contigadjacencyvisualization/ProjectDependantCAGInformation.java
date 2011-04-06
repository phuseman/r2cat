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

package de.bielefeld.uni.cebitec.contigadjacencyvisualization;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigorderingproject.ContigOrderingProject;

/**
 *  Capsulate different information / settings for a contig adjacency graph
 * @author phuseman
 */
public class ProjectDependantCAGInformation {
  private ContigOrderingProject project = null;
  private LayoutGraph layoutGraph = null;

  public ProjectDependantCAGInformation(ContigOrderingProject project) {
    this.project = project;
  }

  public LayoutGraph getLayoutGraph() {
    return layoutGraph;
  }

  public void setLayoutGraph(LayoutGraph layoutGraph) {
    this.layoutGraph = layoutGraph;
  }

  public ContigOrderingProject getProject() {
    return project;
  }

  public void setProject(ContigOrderingProject project) {
    this.project = project;
  }

  

}
