/***************************************************************************
 *   Copyright (C) 2007 by Peter Husemann                                  *
 *   phuseman@cebitec.uni-bielefeld.de                                     *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package de.bielefeld.uni.cebitec.cav.datamodel;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.ComparativeAssemblyViewer;

/**
 * This class is a list of alignment positions.
 * 
 * @author Peter Husemann
 * 
 */
public class AlignmentPositionsList extends Observable implements Serializable,
		 Iterable<AlignmentPosition> /* TODO: maybe implement Externalizable to write out the alignments */ {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6784900397716077729L;

	private Vector<AlignmentPosition> alignmentPositions;

	private HashMap<String, DNASequence> targets;

	private HashMap<String, DNASequence> queries;

	AlignmentPositionsStatistics statistics;

	private boolean queriesWithOffsets = false;

	public static enum NotifyEvent {
		/**
		 * This event is fired if alignments are marked in the gui
		 */
		MARK, 
		/**
		 * This event is fired if elements are going to hide. (not implemented yet) 
		 */
		HIDE, 
		/**
		 * This event is fired 
		 */
		CHANGE
	};

	/**
	 * 
	 */
	public AlignmentPositionsList() {
		alignmentPositions = new Vector<AlignmentPosition>();
		targets = new HashMap<String, DNASequence>();
		queries = new HashMap<String, DNASequence>();
		
		// Collections.sort(targets);
		// Collections.sort(queries);
	}

	public void addAlignmentPosition(AlignmentPosition a) {
		alignmentPositions.add(a);
		AlignmentPosition.register(this);

		if (!targets.containsKey(a.getTarget().getId())) {
			targets.put(a.getTarget().getId(), a.getTarget());
		}
		if (!queries.containsKey(a.getQuery().getId())) {
			queries.put(a.getQuery().getId(), a.getQuery());
		}

		this.setChanged();
	}

	public Iterator<AlignmentPosition> iterator() {
		return alignmentPositions.iterator();
	}

	public boolean isEmpty() {
		return alignmentPositions.isEmpty();
	}


	public int size() {
		return alignmentPositions.size();
	}

	public AlignmentPosition getAlignmentPositionAt(int index) {
		return alignmentPositions.elementAt(index);
	}

	protected void alignmentChanged() {
		this.setChanged();
	}

	/**
	 * Orders the query sequences. Each one gets as offset the sum of the
	 * lengths of the previous query sequences. This information can then be
	 * used for drawing some kind of a synteny plot.
	 */
	public void addOffsets() {
		Vector<DNASequence> queriesList = new Vector<DNASequence>();
		queriesList.addAll(queries.values());
		Collections.sort(queriesList);

		long offset = 0;
		for (DNASequence sequence : queriesList) {
			sequence.setOffset(offset);
			offset += sequence.getSize();
			// System.out.println(sequence.getId() + " "+ sequence.getSize()+" "
			// + sequence.getOffset());
		}
		queriesWithOffsets = true;
		ComparativeAssemblyViewer.preferences.setDisplayOffsets(true);
	}

	/**
	 * Removes the offsets which could be drawn.
	 */
	public void resetOffsets() {
		for (DNASequence sequence : queries.values()) {
			sequence.setOffset(0);
		}
		queriesWithOffsets = false;
		ComparativeAssemblyViewer.preferences.setDisplayOffsets(false);
		}

	public void toggleOffsets() {
		if (queriesWithOffsets == false) {
			this.addOffsets();
		} else {
			this.resetOffsets();
		}
	}

	protected Vector<AlignmentPosition> getAlignmentPositions() {
		return alignmentPositions;
	}

	protected HashMap<String, DNASequence> getQueries() {
		return queries;
	}

	/**
	 * Gets the query {@link DNASequence} object for a specified id and size, if
	 * this is already present.
	 * 
	 * @param id
	 *            Identifier
	 * @param size
	 *            given size
	 * @return the existing object, or null if not found
	 * @throws Exception
	 *             throws if the given size differs while having the same id
	 */
	public DNASequence getQuery(String id, long size) throws Exception {
		if (queries.containsKey(id)) {
			DNASequence out = queries.get(id);
			if (out.getSize() != size) {
				throw new Exception("Found Query Id with wrong size!");
			}
			return out;

		} else {
			return null;
		}
	}

	protected HashMap<String, DNASequence> getTargets() {
		return targets;
	}

	/**
	 * Gets the target {@link DNASequence} object for a specified id and size,
	 * if this is already present.
	 * 
	 * @param id
	 *            Identifier
	 * @param size
	 *            given size
	 * @return the existing object, or null if not found
	 * @throws Exception
	 *             throws if the given size differs while having the same id
	 */
	public DNASequence getTarget(String id, long size) throws Exception {
		if (targets.containsKey(id)) {
			DNASequence out = targets.get(id);
			if (out.getSize() != size) {
				throw new Exception("Found Query Id with wrong size!");
			}
			return out;

		} else {
			return null;
		}
	}

	public AlignmentPositionsStatistics getStatistics() {
		if (statistics == null) {
			statistics = new AlignmentPositionsStatistics(this);
		}
		return statistics;
	}
	
	
	/**
	 * Generates a Statistics Object which does some counting.
	 * The Statistics Object sets the center of mass for each contig/query
	 */
	public void generateStatistics() {
		this.getStatistics();
	}

	public void markQueriesWithSelectedAps() {
		unmarkAllQueries();

		// mark all queries which have at least one selected alignment
		for (AlignmentPosition ap : alignmentPositions) {
			if (ap.isSelected() && !ap.getQuery().isMarked()) {
				ap.getQuery().setMarked(true);
			}
		}
	}

	/**
	 * remove all markings 
	 */
	public void unmarkAllQueries() {
		for (DNASequence query : queries.values()) {
			query.setMarked(false);
		}
	}

	@Override
	public void notifyObservers(Object arg) {
		this.markQueriesWithSelectedAps();
		super.notifyObservers(arg);
	}
}
