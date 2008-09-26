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
import java.util.Locale;
import java.util.Observable;
import java.util.Vector;

import de.bielefeld.uni.cebitec.cav.qgram.FastaFileReader;

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
	private Vector<DNASequence> targetOrder;
	private boolean targetOrderDefined = false;

	private HashMap<String, DNASequence> queries;
	private Vector<DNASequence> queryOrder;
	private boolean queryOrderDefined = false;
	private boolean queryOrientationDefined = false;

	AlignmentPositionsStatistics statistics;

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
		targetOrder = new Vector<DNASequence>();
		queryOrder = new Vector<DNASequence>();

		// Collections.sort(targets);
		// Collections.sort(queries);
	}

	/**
	 * This method keeps the already registered observers and copies the new
	 * data
	 * 
	 * @param other
	 */
	public void copyDataFromOtherAlignmentPositionsList(
			AlignmentPositionsList other) {
		this.alignmentPositions = other.alignmentPositions;
		this.targets = other.targets;
		this.targetOrder = other.targetOrder;
		this.queries = other.queries;
		this.queryOrder = other.queryOrder;
		
		this.queryOrderDefined=other.queryOrderDefined;
		this.queryOrientationDefined=other.queryOrientationDefined;
		this.targetOrderDefined=other.targetOrderDefined;

		
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
			targetOrder.add(a.getTarget());
		}
		if (!queries.containsKey(a.getQuery().getId())) {
			queries.put(a.getQuery().getId(), a.getQuery());
			queryOrder.add(a.getQuery());
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
	 * Add offsets if there are multiple targets. TODO: sort the targets by
	 * length when adding the offsets
	 */
	public void setInitialTargetsOffsets() {
		checkStatistics();
		if (!targetOrderDefined) {
			long offset = 0;
			for (DNASequence target : targetOrder) {
				target.setOffset(offset);
				offset += target.getSize();
			}
			targetOrderDefined = true;
		}
	}

	/**
	 * Orders the query sequences by their center of mass.
	 */
	public void setInitialQueryOrder() {
		checkStatistics();
		if (!queryOrderDefined) {
			Collections.sort(queryOrder);
			queryOrderDefined = true;
		}
	}


	/**
	 * Marks a contig as reverse complement if more than 50% of the matches (size not number) are on the reverse strand
	 */
	public void setInitialQueryOrientation() {
		if(!queryOrientationDefined) {
		this.checkStatistics();
		for (DNASequence sequence : queryOrder) {
			if (sequence.totalAlignmentLength == 0) {
				continue;
			}
			if (sequence.reverseAlignmentLength / sequence.totalAlignmentLength > 0.5) {
				sequence.setReverseComplemented(true);
			} else {
				sequence.setReverseComplemented(false);
			}
		}
		queryOrientationDefined=true;
		}
	}

	
	/**
	 * Each query gets as offset the sum of the lengths of the previous query
	 * sequences. This information can then be used for drawing some kind of a
	 * synteny plot. The order is derived from the queryOrder Vector
	 */
	public void setQueryOffsets() {
		if (!queryOrderDefined) {
			setInitialQueryOrder();
		}
		long offset = 0;
		for (DNASequence sequence : queryOrder) {
			sequence.setOffset(offset);
			offset += sequence.getSize();
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
		checkStatistics();
		return statistics;
	}

	/**
	 * Checks if there is a statistics object. If not it creates one. Thus after
	 * this call there will be a statistics object!
	 * 
	 * @return
	 */
	private boolean checkStatistics() {
		if (statistics == null) {
			this.generateNewStatistics();
			return false;
		}
		return true;
	}

	/**
	 * Generates a Statistics Object which does some counting. The Statistics
	 * Object sets the center of mass for each contig/query and the total
	 * alignment length as well as reversed alignment length
	 */
	public void generateNewStatistics() {
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
	 * Writes this list of alignment positions to a file.<br>
	 * For each query and target object there is a section (BEGIN_QUERY ...
	 * END_QUERY and so on) writing additional information like size,
	 * description, offset in the graph and so on. After that there is a section
	 * where each matching region is listed on a line. The important
	 * informations are separated by a tab character.
	 * 
	 * @param f
	 * @throws IOException
	 */
	public void writeToFile(File f) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(f));

		out
				.write("# r2cat output\n# Warning: Comments will be overwritten\n\n");
		// write a section for each target
		for (DNASequence target : targetOrder) {
			out.write("BEGIN_TARGET " + target.getId() + "\n");
			if (target.getDescription() != null
					&& !target.getDescription().isEmpty()) {
				out.write(" description=" + target.getDescription() + "\n");
			}
			out.write(" size=" + target.getSize() + "\n");
			if (target.getOffset() > 0) {
				out.write(" offset=" + target.getOffset() + "\n");
			}
			if (target.getFile() != null) {
				out.write(" file=" + target.getFile().getAbsolutePath() + "\n");
			}
			out.write("END_TARGET\n\n");
		}

		// write a section for all the queries

		for (DNASequence query : queryOrder) {
			out.write("BEGIN_QUERY " + query.getId() + "\n");
			if (query.getDescription() != null
					&& !query.getDescription().isEmpty()) {
				out.write(" description=" + query.getDescription() + "\n");
			}
			out.write(" size=" + query.getSize() + "\n");
			if (query.getOffset() > 0) {
				out.write(" offset=" + query.getOffset() + "\n");
			}
			if (query.getFile() != null) {
				out.write(" file=" + query.getFile().getAbsolutePath() + "\n");
			}
			if (query.isReverseComplemented()) {
				out.write(" reverse_complement="
						+ query.isReverseComplemented() + "\n");
			}
			out.write("END_QUERY\n\n");
		}

		// write the hit section
		// each hit is represented by a line with tab seperated values.
		// the order is written into the file too
		out.write("BEGIN_HITS\n");
		out
				.write("#query_id\tquery_start\tquery_end\ttarget_id\ttarget_start\ttarget_end\tq_hits\thit_variance\n");
		for (AlignmentPosition ap : alignmentPositions) {
			
			out.write(String.format((Locale) null,
					"%s\t%d\t%d\t%s\t%d\t%d", ap.getQuery().getId(), ap
							.getQueryStart(), ap.getQueryEnd(), ap
							.getTarget().getId(), ap.getTargetStart(), ap
							.getTargetEnd()));

			// if the number of qhits is -1 (default); leave it out.
			if (ap.getNumberOfQHits() >= 1) {
				// in this case the input could be imported from swift. then the
				// last two pieces of information are not available
				out.write(String.format((Locale) null,
						"\t%d", ap.getNumberOfQHits()));
			}
			
			// if the diagonal variance is -1 (default); leave it out.

			if(ap.getVariance()>=0) {
				out.write(String.format((Locale) null,
						"\t%f", ap.getNumberOfQHits()));
			} 
			out.write("\n");

		}
		out.write("END_HITS\n");

		out.close();
	}

	/**
	 * This method reads the data which are saved by the writeToFile() method.
	 * Every line starting with # will be ignored. every line wich is not
	 * between BEGIN_something and END_something will be ignored too. <br>
	 * The odrder of the sections should play no role. e.g. BEGIN_HITS first or
	 * BEGIN_TARGET first.
	 * 
	 * @param f
	 *            file to read from
	 * @throws IOException
	 */
	public void readFromFile(File f) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(f));

		this.alignmentPositions.clear();
		this.targets.clear();
		this.targetOrder.clear();
		this.queries.clear();
		this.queryOrder.clear();
		statistics = null; // will be recomputed

		String line;
		String[] propertyValue;
		String[] values;
		DNASequence target;
		DNASequence query;
		int linenumber = 0;

		while (in.ready()) {
			line = in.readLine();
			linenumber++;
			// each section begins with BEGIN_... ; jump to the appropriate code
			// part to parse this.
			//
			// process the hits section
			if (line.matches("BEGIN_HITS")) {
				// read every line until the end of this section
				while (in.ready() && !line.matches("END_HITS")) {
					line = in.readLine();
					linenumber++;

					// ignore comments
					if (line.startsWith("#") || line.startsWith("\"#")
							|| line.matches("END_HITS")) {
						continue;
					}

					// split the tab separated values...
					values = line.split("\t");
					// if a editor destroyed the tabs try out if spaces work
					if (values.length == 1) {
						values = line.split(" +");
					}

					if (values.length < 6) {
						continue;
					}

					try {
						// ...and assemble them to a alignmentPosition object
						target = targets.get(values[3]);
						query = queries.get(values[0]);
						if (query == null) {
							query = new DNASequence(values[0]);
							queries.put(query.getId(), query);
							queryOrder.add(query);

						
						}
						if (target == null) {
							target = new DNASequence(values[3]);
							targets.put(target.getId(), target);
							targetOrder.add(target);
						}

						long queryStart = Long.parseLong(values[1]);
						long queryEnd = Long.parseLong(values[2]);

						long targetStart = Long.parseLong(values[4]);
						long targetEnd = Long.parseLong(values[5]);

						// TODO set the size of a query/target at least to the
						// max
						// ending value.
						// this is needed for drawing if the size information is
						// missing
						// if (query.getSize() < queryEnd) {
						// query.setSize(queryEnd + 1);
						// }
						// if (target.getSize() < targetEnd) {
						// target.setSize(targetEnd + 1);
						// }

						AlignmentPosition ap = new AlignmentPosition(target,
								targetStart, targetEnd, query, queryStart,
								queryEnd);

						this.addAlignmentPosition(ap);

						// set additional properties
						if (values.length >= 7) {
							ap.setNumberOfQHits(Integer.parseInt(values[6]));
						}
						if (values.length >= 8) {
							ap.setVariance(Float.parseFloat(values[7]));
						}
						
					} catch (NumberFormatException e) {
						System.err.println("Expected a number on line "
								+ linenumber + " ; " + e.toString());
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
					targetOrder.add(target);
				}
				while (in.ready() && !line.matches("END_TARGET")) {
					line = in.readLine();
					linenumber++;
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
								.println("Line "
										+ linenumber
										+ " ignored, to much or to less values for property:\n"
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
						// TODO use one file object for all targets
						target.setFile(new File(propertyValue[1]));
					}

				}

				// process a query section
			} else if (line.matches("BEGIN_QUERY .+")) {
				String query_id = line.split(" ")[1];

				// if this query was added in the hits section, update the
				// values of old instance
				if (queries.containsKey(query_id)) {
					query = queries.get(query_id);
				} else {
					// if not create a new one
					query = new DNASequence(query_id);
					queries.put(query_id, query);
					queryOrder.add(query);
				}
				while (in.ready() && !line.matches("END_QUERY")) {
					line = in.readLine();
					linenumber++;
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
								.println("Line "
										+ linenumber
										+ " ignored, to much or to less values for property:\n"
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
						// TODO use one file object for all targets
						query.setFile(new File(propertyValue[1]));
					} else if (propertyValue[0].matches(".+reverse_complement")) {
						query.setReverseComplemented(Boolean
								.parseBoolean(propertyValue[1]));
					}
				}

			} else {
				// if this line does not match one of the sections, read the
				// next one
				continue;
			}
		}

		// postprocessing (don't know if this is needed here...)
		AlignmentPosition.setParentList(this);
		unmarkAllAlignments();
		this.queryOrderDefined=true;
		this.queryOrientationDefined=true;
		this.targetOrderDefined=true;
		this.setChanged();
	}

	/**
	 * Writes the order of contigs to a file. Each line is an identifier of a
	 * contig. The orientation is given by a preceding + for forward and - for
	 * reverse complement.
	 * 
	 * @param f
	 *            file to write to
	 * @throws IOException
	 */
	public void writeContigsOrder(File f) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(f));

		Vector<DNASequence> queriesList = new Vector<DNASequence>();
		queriesList.addAll(queries.values());
		Collections.sort(queriesList);

		for (DNASequence query : queriesList) {
			if (query.getCenterOfMass() >= 0) {
				out.write((query.isReverseComplemented() ? "-" : "+")
						+ query.getId() + "\n");
			}
		}
		out.close();
	}

	public void writeContigsOrderFasta(File f) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(f));

		HashMap<String, FastaFileReader> sequences = new HashMap<String, FastaFileReader>();
		FastaFileReader fastaFile = null;

		// get the sorted contigs
		Vector<DNASequence> queriesList = new Vector<DNASequence>();
		queriesList.addAll(queries.values());
		Collections.sort(queriesList);

		for (DNASequence query : queriesList) {
			String path = query.getFile().getAbsolutePath();
			if (sequences.containsKey(path)) {
				fastaFile = sequences.get(path);
			} else {
				fastaFile = new FastaFileReader(new File(path));
				fastaFile.scanContents(true);
				sequences.put(path, fastaFile);
			}

			if (fastaFile.containsId(query.getId())) {
				if (!query.isReverseComplemented()) {
					out.write(">" + query.getId() + "\n");
					fastaFile.writeSequence(query.getId(), out);
				} else {
					out.write(">" + query.getId() + " reverse complemented\n");
					fastaFile
							.writeReverseComplementSequence(query.getId(), out);
				}
			}
		}
		out.close();
	}

}
