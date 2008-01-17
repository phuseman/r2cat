/**
 * 
 */
package de.bielefeld.uni.cebitec.cav.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Start the swift program in a thread to avoid freezing of the gui.
 * 
 * @author phuseman
 * 
 */
public class SwiftExecutor implements Runnable {

	private File outputDir = null;

	private SwiftExternal caller;

	private Vector<String> commands;

	private static boolean threadRunning;

	public SwiftExecutor(SwiftExternal se) {
		this.caller = se;
		commands = new Vector<String>();
	}

	/**
	 * Adds a command to a queue. All commands are executed with <code>run()</code>
	 * @param command
	 */
	public void addCommand(String command) {
		this.commands.add(command);
	}

	/**
	 * Sets the directory, where the output shall be written.
	 * @param dir
	 */
	public void setOutputDir(File dir) {
		this.outputDir = dir;
	}

	private void runCommand(String commandString) {

		try {
			// command, environment, working directory
			Process process = Runtime.getRuntime().exec(commandString, null,
					outputDir);
			// start the job; current working directory is given by outputDir

			// get the output
			BufferedReader in = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			for (String s; (s = in.readLine()) != null;) {
				caller.log(s + "\n");
			}

			caller.log("Process finished with status: " + process.waitFor()
					+ "\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * runs the thread. Use with Thread.start().
	 */
	public void run() {
		SwiftExecutor.threadRunning = true; // set lock
		for (String command : commands) {
			caller.logBold("Executing:\n" + command + "\n");
			this.runCommand(command);
		}
		commands.removeAllElements();
		SwiftExecutor.threadRunning = false; // remove lock
	}

	/**
	 * Semaphore if already a thread is running. Remember the thread is only used that the GUI does not freezes.
	 * @return the threadRunning
	 */
	public static boolean isThreadRunning() {
		return threadRunning;
	}

}
