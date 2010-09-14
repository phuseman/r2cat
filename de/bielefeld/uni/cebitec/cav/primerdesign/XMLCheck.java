package de.bielefeld.uni.cebitec.cav.primerdesign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class XMLCheck{
	private File configFile = null;	
	private FileReader fileReader=null;
	/**
	 * Constructor of this class. Gets an file, which should be in xml format.
	 * 
	 * @param config
	 * @throws FileNotFoundException 
	 */
	public XMLCheck(File config) throws FileNotFoundException{
			configFile = config;
			fileReader = new FileReader(configFile);
	}

	/**
	 * This method counts the opening angle bracket and the closing angle
	 * bracket. The number needs to be the same in other to have a rightful XML
	 * structure.
	 * 
	 * @return boolean
	 * @throws IOException
	 */
	public boolean scanXML() throws IOException {
		BufferedReader in = new BufferedReader(fileReader);
		int countClosing = 0;
		int countOpening = 0;
		String line;
		while ((line = in.readLine()) != null) {
			if (!line.contains("##")) {
				char[] currentLine = line.toCharArray();
				for (int i = 0; i < currentLine.length; i++) {
					if (currentLine[i] == '<') {
						countOpening++;
					}
					if (currentLine[i] == '>') {
						countClosing++;
					}
				}
			}
		}
		if (countOpening == countClosing) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * This method makes a quick scan through the given file and checks whether
	 * after opening angle brackets at least one closing angle bracket follows
	 * in the check lines.
	 * 
	 * @return boolean
	 * @throws IOException
	 */
	public boolean quickScan() throws IOException {
		BufferedReader in = new BufferedReader(fileReader);
		char[] cbuf = new char[1000];
		int opening = 0, closing = 0;
		in.read(cbuf, 0, 1000);
		for (int i = 0; i < cbuf.length; i++) {
			if (cbuf[i] == '<') {
				opening++;
			}
			if (cbuf[i] == '>') {
				closing++;
			}
		}
		if (opening != 0 && opening >= closing && closing != 0) {
			return true;
		} else {
			return false;
		}
	}

	}

