/**
 * 
 */
package de.bielefeld.uni.cebitec.cav.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * @author phuseman
 * 
 */
public class SwiftExecutor extends Observable implements Runnable {

	private File outputDir = null;

	private Vector<String> commands;

	public SwiftExecutor(Observer o) {
		this.addObserver(o);
		commands = new Vector<String>();
	}

	public void addCommand(String command) {
		this.commands.add(command);
	}

	public void setOutputDir(File dir) {
		this.outputDir = dir;
	}

	private void runCommand(String commandString) {
		this.notifyObservers(commandString + "\n");

		try {
			// command, environment, working directory
			Process process = Runtime.getRuntime().exec(commandString, null,
					outputDir);
			// start the job; current working directory is given by outputDir

			// get the output
//			BufferedReader in = new BufferedReader(new InputStreamReader(
//					process.getInputStream()));
//
//			for (String s; (s = in.readLine()) != null;) {
//				this.notifyObservers(s + "\n");
//			}

			this.notifyObservers("Process finished with status "
					+ process.waitFor());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		for (String command : commands) {
			this.runCommand(command);
		}
		commands.removeAllElements();

	}

}
