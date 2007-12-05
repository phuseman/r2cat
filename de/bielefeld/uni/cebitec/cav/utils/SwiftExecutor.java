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
 * @author phuseman
 * 
 */
public class SwiftExecutor implements Runnable {

	private File outputDir = null;
	private SwiftExternal caller;

	private Vector<String> commands;

	public SwiftExecutor(SwiftExternal se) {
		this.caller=se;
		commands = new Vector<String>();
	}

	public void addCommand(String command) {
		this.commands.add(command);
	}

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

			caller.log("Process finished with status: "
					+ process.waitFor() + "\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		for (String command : commands) {
			caller.logBold("Executing:\n" + command + "\n");
			this.runCommand(command);
		}
		commands.removeAllElements();
	}

}
