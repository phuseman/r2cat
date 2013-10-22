/***************************************************************************
 *   Copyright (C) 2007 by Peter Husemann                                  *
 *   phuseman  a t  cebitec.uni-bielefeld.de                               *
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

package de.bielefeld.uni.cebitec.qgram;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Vector;

import de.bielefeld.uni.cebitec.common.SequenceNotFoundException;

/**
 * This class is a list of alignment positions.
 * 
 * @author Peter Husemann
 * 
 */
public class MatchList extends Observable implements
		Iterable<Match> {
	private Vector<Match> matches;

	private HashMap<String, DNASequence> targets;
	private Vector<DNASequence> targetOrder;
	private boolean targetOrderDefined = false;

	private HashMap<String, DNASequence> queries;
	private Vector<DNASequence> queryOrder;
	private boolean queryOrderDefined = false;
	private boolean queryOrientationDefined = false;
	
	private List<DNASequence> unmatchedContigs;

	MatchStatistics statistics;

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
		CHANGE,
		/**
		 * This event is fired if there is a change on the data
		 */
		ORDER_CHANGED_OR_CONTIG_REVERSED
	};

	/**
	 * 
	 */
	public MatchList() {
		this.matches = new Vector<Match>();
		this.targets = new HashMap<String, DNASequence>();
		this.queries = new HashMap<String, DNASequence>();
		this.targetOrder = new Vector<DNASequence>();
		this.queryOrder = new Vector<DNASequence>();
		this.unmatchedContigs = new ArrayList<DNASequence>();

		// Collections.sort(targets);
		// Collections.sort(queries);
	}

	/**
	 * This method keeps the already registered observers and copies the new
	 * data
	 * 
	 * @param other
	 */
	public void copyDataFromOtherMatchList(MatchList other) {
		this.matches = other.matches;
		this.unmatchedContigs = other.unmatchedContigs;
		this.targets = other.targets;
		this.targetOrder = other.targetOrder;
		this.queries = other.queries;
		this.queryOrder = other.queryOrder;

		this.queryOrderDefined = other.queryOrderDefined;
		this.queryOrientationDefined = other.queryOrientationDefined;
		this.targetOrderDefined = other.targetOrderDefined;

		statistics = null; // will be recomputed
		Match.setParentList(this);
		unmarkAllAlignments();

		this.setChanged();
	}

	public void addMatch(Match a) {
		matches.add(a);
		Match.setParentList(this);

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

	public Iterator<Match> iterator() {
		return matches.iterator();
	}

	public boolean isEmpty() {
		return matches.isEmpty();
	}

	public int size() {
		return matches.size();
	}

	public Match getMatchAt(int index) {
		return matches.elementAt(index);
	}

	protected void alignmentChanged() {
		this.setChanged();
	}

	/**
	 * Add offsets if there are multiple targets. TODO: sort the targets by
	 * length when adding the offsets
	 */
	public void setInitialTargetOrder() {
		checkStatistics();
		if (!targetOrderDefined) {
			setTargetOffsets();
			this.setChanged();
		}
	}

	/**
	 * If wanted, the order of the targets can be changed such that they are ordered by size.
	 */
	public void sortTargetsBySize() {
		Collections.sort(targetOrder, new Comparator<DNASequence>() {
			public int compare(DNASequence a, DNASequence b) {
				if (a.getSize()==b.getSize()) {
					return 0;
				} else {
					return a.getSize()>b.getSize() ? -1 : 1 ;
				}
			}
		});
		setTargetOffsets();
	}
	
	/**
	 * This can be used if all sequences already have an offset, but maybe some of the sequences are missing.
	 */
	public void sortTargetsByPreviousOffset() {
		Collections.sort(targetOrder, new Comparator<DNASequence>() {
			public int compare(DNASequence a, DNASequence b) {
				if (a.getOffset()==b.getOffset()) {
					return 0;
				} else {
					return a.getOffset()<b.getOffset() ? -1 : 1 ;
				}
			}
		});
		setTargetOffsets();
	}
	
	
	
	/**
	 * Use the existing target order to put the appropriate offsets to the target {@link DNASequence}s.
	 */
	public void setTargetOffsets() {
		long offset = 0;
		
		for (DNASequence target : targetOrder) {
			target.setOffset(offset);
			offset += target.getSize();
		}
		targetOrderDefined = true;

	}

	public void moveTarget(int fromIndex, int toIndex) {
		if (fromIndex != toIndex) {

			DNASequence swap = targetOrder.remove(fromIndex);
			if (fromIndex < toIndex) {
				toIndex--;
			}
			targetOrder.add(toIndex, swap);
			this.setChanged();
		}

	}

	/**
	 * Orders the query sequences by their center of mass.
	 */
	@SuppressWarnings("unchecked")
	public void setInitialQueryOrder() {
		checkStatistics();
		if (!queryOrderDefined) {
			Collections.sort(queryOrder);
			queryOrderDefined = true;
			this.setQueryOffsets();
			this.setChanged();
		}
	}

	/**
	 * Marks a contig as reverse complement if more than 50% of the matches
	 * (size not number) are on the reverse strand
	 */
	public void setInitialQueryOrientation() {
		if (!queryOrientationDefined) {
			this.checkStatistics();
			for (DNASequence sequence : queryOrder) {
				if (sequence.getTotalAlignmentLength() == 0) {
					continue;
				}
				if (sequence.getReverseAlignmentLength()
						/ sequence.getTotalAlignmentLength() > 0.5) {
					sequence.setReverseComplemented(true);
				} else {
					sequence.setReverseComplemented(false);
				}
			}
			this.setChanged();
			queryOrientationDefined = true;
		}
	}

	public void setQueryReverseComplemented(int index,
			boolean reverseComplemented) {
		if (queryOrder.get(index).isReverseComplemented() != reverseComplemented) {
			queryOrder.get(index).setReverseComplemented(reverseComplemented);
			this.setChanged();
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
	
	public void changeQueryOrder(Vector<DNASequence> sequences) {
		assert sequences.containsAll(queryOrder);
		this.queryOrder = sequences;
		queryOrderDefined = true;
		this.setQueryOffsets();
		queryOrientationDefined=false;
		this.setInitialQueryOrientation();
//		this.setInitialQueryOrder();
	}
	
	public void calculateNewOrderFromSortKeyValues() {
		queryOrderDefined = false;
		this.setInitialQueryOrder();
	}

	public void moveQuery(int fromIndex, int toIndex) {
		if (fromIndex != toIndex) {
			DNASequence swap = queryOrder.remove(fromIndex);
			if (fromIndex < toIndex) {
				toIndex--;
			}
			queryOrder.add(toIndex, swap);
			this.setChanged();
		}
	}

	protected Vector<Match> getMatches() {
		return matches;
	}

	public Vector<DNASequence> getQueries() {
		return queryOrder;
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

	/**
	 * Gets the Vector containing all targets
	 * @return
	 */
	public Vector<DNASequence> getTargets() {
		return targetOrder;
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
	
	/**
	 * @return the unmatchedContigs the list of unmatched contigs belonging to this project.
	 */
	public List<DNASequence> getUnmatchedContigs() {
		return this.unmatchedContigs;
	}

	/**
	 * @param unmatchedContigs the unmatchedContigs to set for this project.
	 */
	public void setUnmatchedContigs(List<DNASequence> unmatchedContigs) {
		this.unmatchedContigs = unmatchedContigs;
	}

	public MatchStatistics getStatistics() {
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
	 * alignment length as well as reversed alignment length and checks if a match is repeating.
	 */
	public void generateNewStatistics() {
		statistics = new MatchStatistics(this);
	}

	public void markQueriesWithSelectedAps() {
		unmarkAllQueries();

		// mark all queries which have at least one selected alignment
		for (Match ap : matches) {
			if (ap.isSelected() && !ap.getQuery().isMarked()) {
				ap.getQuery().setMarked(true);
			}
		}
	}

	public void markQuery(int index, boolean marked) {
		if (queryOrder.get(index).isMarked() != marked) {
			queryOrder.get(index).setMarked(marked);
			this.setChanged();
		}
	}

	public void unmarkAllQueries() {
		for (DNASequence query : queryOrder) {
			query.setMarked(false);
		}
	}

	public void unmarkAllAlignments() {
		for (Match elem : matches) {
			elem.setSelected(false);
		}
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
	 * @param unmatchedContigs the unmatched contigs of this data set. Since they
	 * also need to be stored within the project.
	 * @throws IOException
	 */
	public void writeToFile(File f) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(f));

		out.write("# r2cat output\n# Warning: Comments will be overwritten\n\n");
		// write a section for each target
		for (DNASequence target : targetOrder) {
			out.write("BEGIN_TARGET " + target.getId() + "\n");
			if (target.getDescription() != null
					&& !target.getDescription().isEmpty()) {
				out.write(" description=\"" + target.getDescription() + "\"\n");
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
				out.write(" description=\"" + query.getDescription() + "\"\n");
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
		for (Match ap : matches) {

			out.write(String.format((Locale) null, "%s\t%d\t%d\t%s\t%d\t%d", ap
					.getQuery().getId(), ap.getQueryStart(), ap.getQueryEnd(),
					ap.getTarget().getId(), ap.getTargetStart(), ap
							.getTargetEnd()));

			// if the number of qhits is -1 (default); leave it out.
			if (ap.getNumberOfQHits() >= 1) {
				// in this case the input could be imported from swift. then the
				// last two pieces of information are not available
				out.write(String.format((Locale) null, "\t%d", ap
						.getNumberOfQHits()));
			}

			// if the diagonal variance is -1 (default); leave it out.

			if (ap.getVariance() >= 0) {
				out.write(String.format((Locale) null, "\t%f", ap
						.getNumberOfQHits()));
			}
			out.write("\n");

		}
		out.write("END_HITS\n");
		
		/** 
		 * @author Rolf Hilker
		 * write a section for each unmatched contig
		 */
		for (DNASequence contig : this.unmatchedContigs) {
			out.write("BEGIN_UNMATCHED " + contig.getId() + "\n");
			if (contig.getDescription() != null
					&& !contig.getDescription().isEmpty()) {
				out.write(" description=\"" + contig.getDescription() + "\"\n");
			}
			out.write(" size=" + contig.getSize() + "\n");
			if (contig.getOffset() > 0) {
				out.write(" offset=" + contig.getOffset() + "\n");
			}
			if (contig.getFile() != null) {
				out.write(" file=" + contig.getFile().getAbsolutePath() + "\n");
			}
			out.write("END_UNMATCHED\n\n");
		}

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

		this.matches.clear();
		this.targets.clear();
		this.targetOrder.clear();
		this.queries.clear();
		this.queryOrder.clear();
		this.unmatchedContigs.clear();
		statistics = null; // will be recomputed

		String line;
		String[] propertyValue;
		String[] values;
		DNASequence target;
		DNASequence query;
		DNASequence unmatchedContig;
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
						// ...and assemble them to a Match object
						target = targets.get(values[3]);
						query = queries.get(values[0]);
						if (query == null) {
							query = new DNASequence(values[0]);
							queries.put(query.getId(), query);
							//TODO take the order of the queries section!!
							queryOrder.add(query);

						}
						if (target == null) {
							target = new DNASequence(values[3]);
							targets.put(target.getId(), target);
							//TODO take the order of the targets section!!
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

						Match ap = new Match(target,
								targetStart, targetEnd, query, queryStart,
								queryEnd);

						this.addMatch(ap);

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
					if (propertyValue.length != 2
							&& !propertyValue[0].matches(".+description")) {
						System.err
								.println("Line "
										+ linenumber
										+ " ignored, to much or to less values for property:\n"
										+ line);
						continue;
					}

					if (propertyValue[0].matches(".+description")) {
						int start = line.indexOf("=") + 1;
						if (line.charAt(start)=='"') {
							//remove quotation (if present)
							target.setDescription(line.substring(start+1, line
									.length() - 2));
						} else {
						target.setDescription(line.substring(start, line
								.length() - 1));
						}
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
					if (propertyValue.length != 2
							&& !propertyValue[0].matches(".+description")) {
						System.err
								.println("Line "
										+ linenumber
										+ " ignored, to much or to less values for property:\n"
										+ line);
						continue;
					}

					if (propertyValue[0].matches(".+description")) {
						int start = line.indexOf("=") + 1;
						if (line.charAt(start)=='"') {
							//remove quotation (if present)
							query.setDescription(line.substring(start+1, line
									.length() - 2));
						} else {
							query.setDescription(line.substring(start, line
								.length() - 1));
						}
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
				
				/** 
				 * @author Rolf Hilker
				 * process unmatched contigs
				 */
			} else if (line.matches("BEGIN_UNMATCHED .+")) {
				String contig_id = line.split(" ")[1];
				unmatchedContig = new DNASequence(contig_id);
				unmatchedContigs.add(unmatchedContig);
				
				while (in.ready() && !line.matches("END_UNMATCHED")) {
					line = in.readLine();
					linenumber++;
					if (line.startsWith("#") || line.startsWith("\"#")
							|| line.matches("END_UNMATCHED")) {
						continue;
					}
					// read each line and process the
					// property=value
					// lines
					propertyValue = line.split("=");
					if (propertyValue.length != 2
							&& !propertyValue[0].matches(".+description")) {
						System.err.println("Line "
										+ linenumber
										+ " ignored, to much or to less values for property:\n"
										+ line);
						continue;
					}

					if (propertyValue[0].matches(".+description")) {
						int start = line.indexOf("=") + 1;
						if (line.charAt(start)=='"') {
							//remove quotation (if present)
							unmatchedContig.setDescription(line.substring(start+1, line
									.length() - 2));
						} else {
							unmatchedContig.setDescription(line.substring(start, line
								.length() - 1));
						}
					} else if (propertyValue[0].matches(".+size")) {
						unmatchedContig.setSize(Long.parseLong(propertyValue[1]));
					} else if (propertyValue[0].matches(".+offset")) {
						unmatchedContig.setOffset(Long.parseLong(propertyValue[1]));
					} else if (propertyValue[0].matches(".+file")) {
						// TODO use one file object for all unmatched contigs
						unmatchedContig.setFile(new File(propertyValue[1]));
					}

				}

			} else {
				// if this line does not match one of the sections, read the
				// next one
				continue;
			}
		}

		// postprocessing (don't know if this is needed here...)
		Match.setParentList(this);
		unmarkAllAlignments();
		this.queryOrderDefined = true;
		this.queryOrientationDefined = true;
		this.targetOrderDefined = true;
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
		out.write(getContigsOrderAsText());
		out.close();
	}
	
	

	/**
	 * Gets the current order of the contigs as text. Each line is an identifier of a
	 * contig. The orientation is given by a preceding + for forward and - for
	 * reverse complement.
	 */
	public String getContigsOrderAsText() {
		StringBuilder out = new StringBuilder();

		for (DNASequence query : queryOrder) {
				out.append( query.getId() + " " +
						(query.isReverseComplemented() ? "-" : "+") +
				"\n");
		}
		return out.toString();
	}
	
	/**
	 * Gets the current order of the contigs as text. 
	 * Each line gives the identiefier, the orientation, the repetitiveness,
	 * and a proposed name that reflects the order.
	 * regular contigs should have the order 000, 001, 002.
	 * repetitive contigs are just numbered as r000, r001 but this
	 *  does not tell anything about the order or them.
	 */
	public String getContigsOrderAsTextWithExtendedInformation() {
		StringBuilder out = new StringBuilder();

		int regularContigsCounter = 0;
		int repetitiveContigsCounter = 0;
		String newName ="";
		
		for (DNASequence query : queryOrder) {
			if(query.isRepetitive()) {
				newName= String.format("r%02d", repetitiveContigsCounter);
			} else {
				newName= String.format("c%02d", regularContigsCounter);
			}
			
			out.append(
					String.format((Locale)null,
							"%s, %s, %s, %s\n", 
							query.getId(),
							(query.isReverseComplemented() ? "-" : "+"),
							(query.isRepetitive() ? "repetitive" : "regular"),
							newName)
						);

					
					if(query.isRepetitive()) {
						repetitiveContigsCounter++;
					} else {
						regularContigsCounter++;
					}

		}
		return out.toString();
	}

	/**
	 * Writes all existing contigs of the query order vector, if the source fasta file is given.
	 * Contigs are reverse complemented if the DNASequence object says so.
	 * If no or wrong files are given, or Id's are not present then it throws a SequenceNotFoundException, which includes 
	 * the DNASequence object. This can be caught and another file can be st.
	 * If no sequences are available, then the output file is not written.
	 * 
	 * @param f the file to write the output to. If this file is not writable an IOException will be thrown.
	 * @param ignoreMissingFiles if this is true, no SequenceNotFoundException will be thrown.
	 * @return the number of contigs that have been written
	 * @throws IOException if output is not writable. If some Id's are not present in the given files or if the given files do not exist
	 * then a SequenceNotFoundException will be thrown.
	 */
	public int writeContigsOrderFasta(File f, boolean ignoreMissingFiles, Collection<DNASequence> dataToWrite)
			throws IOException {
		
		
		
		//change this to renumber the contigs (repeating and non repeating seperately)
		boolean renumberContigs=false;

		
		
		HashMap<String, FastaFileReader> sequences = new HashMap<String, FastaFileReader>();
		FastaFileReader fastaFile = null;
		boolean anySequenceContained = false;
		// ======================begin: check for files and id's
		// check if all sequences and id's exist
		for (DNASequence query : dataToWrite) {
			fastaFile = null;

			// if the file was not set, check if the id is in another loaded
			// file:
			if (query.getFile() == null) {
				for (FastaFileReader existingFastaFile : sequences.values()) {
					if (existingFastaFile.containsId(query.getId())) {
						// set the file if the id is present in another files
						query.setFile(existingFastaFile.getSource());
						fastaFile = existingFastaFile;
						break;
					}
				}
			}
			// if the file is still not set, throw an exception
			if (query.getFile() == null) {
				if (!ignoreMissingFiles) {
					throw new SequenceNotFoundException(
							"No fasta file given for sequence: "
									+ query.getId() + ".", query);
				} else {
					continue;
				}
			}

			// if the file is set, try to read a fastafile object for the
			// specified path
			// only if this has not happened before
			String path = query.getFile().getAbsolutePath();
			if (!sequences.containsKey(path)) {
				try {
					// try to open the file and scan the entries
					fastaFile = new FastaFileReader(new File(path));
					fastaFile.scanContents(true);
					sequences.put(path, fastaFile);
				} catch (IOException e) {
					// if the opening of the file was not possible
					if (!ignoreMissingFiles) {
						throw new SequenceNotFoundException(
								"The fasta file\n"
								+ query.getFile().getAbsolutePath()
								+ "\nfor sequence " 
								+ query.getId()+
								" could not be opened.",
								e, query);
					} else {
						continue;
					}
				}// end file was not readable
			} else {
				// if the path was already loaded into a fastafile: get it;
				fastaFile = sequences.get(path);
			}

			// check if the id is really included in the fastafile:
			if (!fastaFile.containsId(query.getId())) {
				// the id does not occur in fastaFile!
				// check if it is included in any other file:
				for (FastaFileReader existingFastaFile : sequences.values()) {
					if (existingFastaFile.containsId(query.getId())) {
						// set the file if the id is present in another files
						query.setFile(existingFastaFile.getSource());
						fastaFile = existingFastaFile;
						break;
					}
				}
			}

			// if there is still no sequence, throw exeption
			if (fastaFile.containsId(query.getId())) {
				anySequenceContained = true;
			} else {
				if (!ignoreMissingFiles) {
					throw new SequenceNotFoundException("Sequence "
							+ query.getId() + "\nwas not found in its associated fasta file:\n"
							+ fastaFile.getSource().getAbsolutePath(), query);
				} else {
					continue;
				}
			} // throw if id is not contained in any fastafile
		}
		fastaFile = null;
		// ======================end: check for files and id's

		
		
		int repeatingContigs=0;
		int nonrepeatingContigs=0;
		
		int contigsWritten=0;
		// ======================begin: write the existing id's into the file
		// it seems that all id's are present in the files
		// then write the output:
		if (anySequenceContained) {
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			for (DNASequence query : dataToWrite) {
				if(query.getFile()==null) {
					continue;
				}
				String path = query.getFile().getAbsolutePath();
				if (sequences.containsKey(path)) {
					fastaFile = sequences.get(path);
					if (fastaFile.containsId(query.getId())) {
						contigsWritten++;
						if (!query.isReverseComplemented()) {
							
							if (!renumberContigs) {
								out.write(">" +query.getId());
								if(query.getDescription()!=null && !query.getId().equals("")) {
									out.write(" " + query.getDescription()) ;
								}
							} else {
								// the contig ids should be renumbered
								out.write(">" + (query.isRepetitive()?("r"+String.format("%03d", repeatingContigs)):String.format("%03d", nonrepeatingContigs))
										+" oldid="+query.getId());
								
								if(query.isRepetitive()) {
									repeatingContigs++;
								} else {
									nonrepeatingContigs++;
								}
								
								if(query.getDescription()!=null && !query.getId().equals("")) {
									out.write(" " + query.getDescription());
								}
							}//renumber contigs
							out.write("\n");

// write the actual sequence							
							fastaFile.writeSequence(query.getId(), out);
						} else {
							if (!renumberContigs) {
								out.write(">" + query.getId()+ " reversed=true ");
								if(query.getDescription()!=null && !query.getId().equals("")) {
									out.write(" " +query.getDescription());
								}
							} else {
								// the contig ids should be renumbered
								out.write(">" + (query.isRepetitive()?("r"+String.format("%03d", repeatingContigs)):String.format("%03d", nonrepeatingContigs))
										+" reversed=true oldid="+query.getId());
								
								if(query.isRepetitive()) {
									repeatingContigs++;
								} else {
									nonrepeatingContigs++;
								}
								
								if(query.getDescription()!=null && !query.getId().equals("")) {
									out.write(" " + query.getDescription());
								}
								out.write("\n");
							} // renumber contigs
							out.write("\n");
							//write the actual sequence
							fastaFile.writeReverseComplementSequence(query
									.getId(), out);
						}
					}
				}
			}
			out.close();
		} // end writing files
		
		
		//this is only true if at least one contig has been written
		return contigsWritten;
	}
	
	

}
