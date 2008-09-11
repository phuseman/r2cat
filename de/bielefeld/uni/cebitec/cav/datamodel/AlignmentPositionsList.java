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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
public class AlignmentPositionsList extends Observable implements 
		Iterable<AlignmentPosition> { 
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
		 * This event is fired if elements are going to hide. (not implemented
		 * yet)
		 */
		HIDE,
		/**
		 * This event is fired if there is a change on the data
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

	
	
	public static void main(String args[]) {
//		CSVParser csvParser = new CSVParser(new File("/homes/phuseman/compassemb/query.csv"));
		AlignmentPositionsList test = new AlignmentPositionsList() ;//= csvParser.parse();
//
		File file = new File("test.r2c");
		
		try {
			test.readFromFile(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		System.exit(0);
	}
	
	/**
	 * This method keeps the already registered observers and copies the new
	 * data
	 * 
	 * @param other
	 */
	public void copyDataFromOtherAlignmentPositionsList(
			AlignmentPositionsList other) {
		this.alignmentPositions = other.getAlignmentPositions();
		this.targets = other.getTargets();
		this.queries = other.getQueries();
		statistics = null; // will be recomputed
		AlignmentPosition.setParentList(this);
		unmarkAllAlignments();

		this.setChanged();
	}

	public void addAlignmentPosition(AlignmentPosition a) {
		alignmentPositions.add(a);
		AlignmentPosition.setParentList(this);

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

	public HashMap<String, DNASequence> getQueries() {
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

	public HashMap<String, DNASequence> getTargets() {
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
			this.generateStatistics();
		}
		return statistics;
	}

	/**
	 * Generates a Statistics Object which does some counting. The Statistics
	 * Object sets the center of mass for each contig/query
	 */
	public void generateStatistics() {
		statistics = new AlignmentPositionsStatistics(this);
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

	public void unmarkAllAlignments() {
		for (AlignmentPosition elem : alignmentPositions) {
			elem.setSelected(false);
		}
		if (this.hasChanged()) {
			this.notifyObservers(AlignmentPositionsList.NotifyEvent.MARK);
		}
	}

	@Override
	public void notifyObservers(Object arg) {
		this.markQueriesWithSelectedAps();
		super.notifyObservers(arg);
	}

	/**
	 * Add offsets if there are multiple targets. 
	 * TODO: sort the targets by length when adding the offsets
	 */
	public void addOffsetsToTargets() {
		long offset = 0;
		for (DNASequence target : targets.values()) {
			target.setOffset(offset);
			offset += target.getSize();
		}
	}


	/**
	 * Writes this list of alignment positions to a file.<br>
	 * For each query and target object there is a section (BEGIN_QUERY ... END_QUERY and so on) writing additional information like
	 * size, description, offset in the graph and so on.
	 * After that there is a section where each matching region is listed on a line. The important informations
	 * are separated by a tab character.
	 * 
	 * @param f
	 * @throws IOException
	 */
	public void writeToFile(File f) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(f));
		
		// write a section for each target
		for (DNASequence target : targets.values()) {
			out.write("BEGIN_TARGET "+ target.getId()+"\n");
			if (target.getDescription()!=null && !target.getDescription().isEmpty()) {
				out.write(" description="+ target.getDescription() +"\n");
			}
			out.write(" size="+ target.getSize()+"\n");
			if (target.getOffset()>0) {
			out.write(" offset="+ target.getOffset()+"\n");
			}
			if (target.getFile()!=null ) {
				out.write(" file="+ target.getFile().getAbsolutePath() + "\n");
			}
			out.write("END_TARGET\n\n");
		}

//		write a section for all the queries
		for (DNASequence query : queries.values()) {
			out.write("BEGIN_QUERY "+ query.getId()+"\n");
			if (query.getDescription()!=null && !query.getDescription().isEmpty()) {
				out.write(" description="+ query.getDescription() +"\n");
			}
			out.write(" size="+ query.getSize()+"\n");
			if (query.getOffset()>0) {
			out.write(" offset="+ query.getOffset()+"\n");
			}
			if (query.getFile()!=null) {
				out.write(" file="+ query.getFile().getAbsolutePath() + "\n");
			}
			out.write("END_QUERY\n\n");
		}

		
		//write the hit section
		//each hit is represented by a line with tab seperated values.
		// the order is written into the file too
		out.write("BEGIN_HITS\n");
		out.write("#query_id\tquery_start\tquery_end\ttarget_id\ttarget_start\ttarget_end\thit_variance\tq_hits\n");
		for (AlignmentPosition ap : alignmentPositions) {
			// if the number of qhits is -1 (default) then variance an qhits are not set. leave them out.
			if (ap.getNumberOfQHits() < 1 ) {
				//in this case the input could be imported from swift. then the last two pieces of information are not available
			out.write(String.format(
						"%s\t%d\t%d\t%s\t%d\t%d\n", 
						ap.getQuery().getId(),
						ap.getQueryStart(),
						ap.getQueryEnd(),
						ap.getTarget().getId(),
						ap.getTargetStart(),
						ap.getTargetEnd()));
			} else {
				//write the imported information as tab separated value line
			out.write(String.format(
					"%s\t%d\t%d\t%s\t%d\t%d\t%f\t%d\n", 
					ap.getQuery().getId(),
					ap.getQueryStart(),
					ap.getQueryEnd(),
					ap.getTarget().getId(),
					ap.getTargetStart(),
					ap.getTargetEnd(),
					ap.getVariance(),
					ap.getNumberOfQHits()));
			}

		}
		out.write("END_HITS\n");
		
		out.close();
	}

	/**
	 * This method reads the data which are saved by the writeToFile() method.
	 * Every line starting with # will be ignored. every line wich is not between BEGIN_something and
	 * END_something will be ignored too.
	 *<br>
	 * The odrder of the sections should play no role. e.g. BEGIN_HITS first or BEGIN_TARGET first. 
	 * @param f file to read from
	 * @throws IOException
	 */
	public void readFromFile(File f) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(f));

		this.alignmentPositions.clear();
		this.targets.clear();
		this.queries.clear();
		statistics = null; // will be recomputed

		String line;
		String[] propertyValue;
		String[] values;
		DNASequence target;
		DNASequence query;

		while (in.ready()) {
			line = in.readLine();
			// each section begins with BEGIN_... ; jump to the appropriate code
			// part to parse this.
			//
			// process the hits section
			if (line.matches("BEGIN_HITS")) {
				// read every line until the end of this section
				while (in.ready() && !line.matches("END_HITS")) {
					line = in.readLine();

					// ignore comments
					if (line.startsWith("#") || line.startsWith("\"#")) {
						continue;
					}

					// split the tab seperated values...
					values = line.split("\t");

					if (values.length < 6) {
						continue;
					}

					try {
						// ...and assemble them to a alignmentPosition object
						target = targets.get(values[3]);
						query = queries.get(values[0]);
						if (query == null) {
							query = new DNASequence(values[0]);
						}
						if (target == null) {
							target = new DNASequence(values[3]);
						}

						long queryStart = Long.parseLong(values[1]);
						long queryEnd = Long.parseLong(values[2]);

						long targetStart = Long.parseLong(values[4]);
						long targetEnd = Long.parseLong(values[5]);

						// set the size of a query/target at least to the max
						// ending value.
						// this is needed for drawing if the size information is
						// missing
						if (query.getSize() < queryEnd) {
							query.setSize(queryEnd + 1);
						}
						if (target.getSize() < targetEnd) {
							target.setSize(targetEnd + 1);
						}

						AlignmentPosition ap = new AlignmentPosition(target,
								targetStart, targetEnd, query, queryStart,
								queryEnd);

						this.addAlignmentPosition(ap);

						// set additional properties
						if (values.length >= 8) {
							ap.setVariance(Float.parseFloat(values[6]));
							ap.setNumberOfQHits(Integer.parseInt(values[7]));
						}
					} catch (NumberFormatException e) {
						System.err.println(e.toString());
					}

				}
				// process a target section
			} else if (line.matches("BEGIN_TARGET .+")) {
				String target_id = line.split(" ")[1];
				// if the target was added in the hits section update the old
				// instance
				if (targets.containsKey(target_id)) {
					target = targets.get(target_id);
				} else {
					// if not create a new one
					target = new DNASequence(target_id);
					targets.put(target_id, target);
				}
				while (in.ready() && !line.matches("END_TARGET")) {
					line = in.readLine();
					if (line.startsWith("#") || line.startsWith("\"#")
							|| line.matches("END_TARGET")) {
						continue;
					}
					// read each line and process the
					// property=value
					// lines
					propertyValue = line.split("=");
					if (propertyValue.length != 2) {
						System.err
								.println("Line ignored, to much or to less values for property:\n"
										+ line);
						continue;
					}

					if (propertyValue[0].matches(".+description")) {
						target.setDescription(propertyValue[1]);
					} else if (propertyValue[0].matches(".+size")) {
						target.setSize(Long.parseLong(propertyValue[1]));
					} else if (propertyValue[0].matches(".+offset")) {
						target.setOffset(Long.parseLong(propertyValue[1]));
					} else if (propertyValue[0].matches(".+file")) {
						target.setFile(new File(propertyValue[1]));
					}

				}

				// process a query section
			} else if (line.matches("BEGIN_QUERY .+")) {
				String query_id = line.split(" ")[1];

				// if this query was added in the hits section, update the
				// values of old instance
				if (targets.containsKey(query_id)) {
					query = targets.get(query_id);
				} else {
					// if not create a new one
					query = new DNASequence(query_id);
					queries.put(query_id, query);
				}
				while (in.ready() && !line.matches("END_QUERY")) {
					line = in.readLine();
					if (line.startsWith("#") || line.startsWith("\"#")
							|| line.matches("END_QUERY")) {
						continue;
					}
					propertyValue = line.split("=");
					// read each line and process the
					// property=value
					// lines
					if (propertyValue.length != 2) {
						System.err
								.println("Line ignored, to much or to less values for property:\n"
										+ line);
						continue;
					}

					if (propertyValue[0].matches(".+description")) {
						query.setDescription(propertyValue[1]);
					} else if (propertyValue[0].matches(".+size")) {
						query.setSize(Long.parseLong(propertyValue[1]));
					} else if (propertyValue[0].matches(".+offset")) {
						query.setOffset(Long.parseLong(propertyValue[1]));
					} else if (propertyValue[0].matches(".+file")) {
						query.setFile(new File(propertyValue[1]));
					}
				}

			} else {
				// if this line does not match one of the sections, read the
				// next one
				continue;
			}
		}

		// postprocessing (don't know if thi is needed here...)
		AlignmentPosition.setParentList(this);
		unmarkAllAlignments();
		this.setChanged();
	}

	
}
