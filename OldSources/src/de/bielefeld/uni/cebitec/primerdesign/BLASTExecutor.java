/***************************************************************************
 *   Copyright (C) 2010 by Yvonne Herrmann, Peter Husemann                  *
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


package de.bielefeld.uni.cebitec.primerdesign;

import java.io.File;
import java.io.IOException;

/**
 * This class handels the execution of the external programm BLAST 2.2.23.
 *
 */

public class BLASTExecutor {
	private File queryFastaFile = null;
	private File targetFastaFile = null;
	private File temporaryDirectoryForBLAST = null;
	private File blastOutput = null;

	/**
	 * Use this constructor, if you want to blast a fasta file against itself. This is usefull for example for
	 * blasting a set of contigs against it self in order to repeatmask them.
	 * (internally query and target will be set the same)
	 * @param query
	 */
	public BLASTExecutor(File query) {
		queryFastaFile = query;
		targetFastaFile = query;
	}

	/**
	 * This is the constructor for blasting the query file against the target file. both files have to be in fasta format.
	 * 
	 * @param query
	 * @param target
	 */
	public BLASTExecutor(File query, File target) {
		queryFastaFile = query;
		targetFastaFile = target;
	}

/**
	 * This method sets up a temporary directory where the files of the BLAST
	 * run are put.
	 * 
	 * @return dir
	 * @throws IOException
	 */
	public File createTempDir() throws IOException {
		temporaryDirectoryForBLAST = File.createTempFile("r2cat_temp_BLAST_dir", Long.toString(System
				.nanoTime()));
		temporaryDirectoryForBLAST.delete();
		temporaryDirectoryForBLAST.mkdir();
		return temporaryDirectoryForBLAST;
	}

/**
 * This method executes the programm formatdb and makes a nucleotide database from the given
 * fasta file.
 *  @throws IOException
 * @throws InterruptedException
 */
	public int makeBlastDB() throws IOException, InterruptedException{
		if (temporaryDirectoryForBLAST == null) {
			//failsafe: create temp dir, if this has hot happened yet
			this.createTempDir();
		}
		String command = new String("formatdb -i "+targetFastaFile.getAbsolutePath()+" -p F -n blastdb");
		//command BLAST 2.2.24
		//String command = new String("makeblastdb -in "+targetFastaFile.getAbsolutePath()+" -dbtype nucl -parse_seqids -out blastdb");
		Process p = Runtime.getRuntime().exec(command,null,temporaryDirectoryForBLAST);
		return p.waitFor();
	}
	
	/**
	 * This method runs the blastall command with the made database and the given fasta file in order to
	 * get a output file with the information about the repeats within in the sequences.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	
	public int runBlastCommand() throws IOException, InterruptedException{
		if(temporaryDirectoryForBLAST == null) {
			//failsafe: create blast database if there is no temporary directory (and thus no blastdb)
			this.makeBlastDB();
		}
		blastOutput = new File(temporaryDirectoryForBLAST,"blastout.txt");
		String command = new String("blastall -p blastn -i "+queryFastaFile.getAbsolutePath()+" -d blastdb -F F -m 8 -e 1e-04 -o " +blastOutput.getAbsolutePath());
		//command for BLAST 2.2.24
		//String command = new String("blastn -query "+queryFastaFile.getAbsolutePath()+" -db blastdb  -dust no -outfmt 6 -evalue 1e-04 -out "+blastOutput.getAbsolutePath());
		Process p = Runtime.getRuntime().exec(command,null,temporaryDirectoryForBLAST);
		return p.waitFor();
	}

	/**
	 * Returns the output file of the blast run.
	 * Makes sure to remove the temporary files with deleteTempDir() after the result was parsed.
	 * If a blast run is not successful, then null can be returned.
	 * 
	 * @return File blast results in a temporary directory.
	 * 
	 */
	public File getBlastOutput() throws IOException, InterruptedException{
		if(blastOutput == null) {
			//failsafe: run blast, if there is no result file
			this.runBlastCommand();
		}
			return blastOutput;
	}

	
	/**
	 * Removes the temporary directory of this object and all first level files.
	 * @return if the oparation was successfull
	 */
	public boolean deleteTempDir() {
		if (temporaryDirectoryForBLAST.exists()) {
			File[] files = temporaryDirectoryForBLAST.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
			}
			return temporaryDirectoryForBLAST.delete();
		}
		return false;
	}

}
