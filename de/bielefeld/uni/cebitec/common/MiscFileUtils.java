package de.bielefeld.uni.cebitec.common;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * Convenience class to select files for saving or reading.
 * 
 * @author phuseman
 * 
 */
public class MiscFileUtils {

	/**
	 * Shows a dialog to select files for opening or saving files. When saving
	 * the dialog ask if an existing file should be overwritten. If the user
	 * selects not, then null will be returned instead of the file.
	 * 
	 * @param parent
	 *            the parent, which should be disabeled
	 * @param dialogTitle
	 *            Title displayed in this dialog
	 * @param openDialog
	 *            true means show open dialog; false show save dialog
	 * @param extensionFilter
	 *            extension to filter for. null or empty is all
	 * @param extensionDescription
	 *            description of the files to filter
	 * 
	 * @return the file if one was selected, else null;
	 */
	public static File chooseFile(Component parent, String dialogTitle,
			File lastDir, boolean openDialog, FileFilter filter) {
		JFileChooser fileChooser = new JFileChooser();

		if (dialogTitle != null && !dialogTitle.isEmpty()) {
			fileChooser.setDialogTitle(dialogTitle);
		}

		if (filter != null) {
			fileChooser.addChoosableFileFilter(filter);
		}

		if (lastDir != null) {
			fileChooser.setCurrentDirectory(lastDir);
		}

		int returnVal;

		if (openDialog) {
			returnVal = fileChooser.showOpenDialog(parent);
		} else {
			returnVal = fileChooser.showSaveDialog(parent);
		}

		File file = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();

			// if we write and the file exists, ask for confirmation
			if (!openDialog && file.exists()) {
				Object[] options = { "Overwrite!", "Better not" };

				int n = JOptionPane.showOptionDialog(parent,
						"File exists. Do you want to overwrite it?",
						"File exists", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, options, options[1]);
				// if the answer is no, return null, such that the file will not
				// be overwritten
				if (n == JOptionPane.NO_OPTION) {
					file = null;
				}

			}
		}

		return file;
	}

	/**
	 * Displays a file open dialog and returns a selected file. If the dialog
	 * was cancelled, then null is returned.
	 * 
	 * This is a shortcut for the other method.
	 * 
	 * @param parent
	 *            the Component which shall be locked
	 * 
	 * 
	 * @param dialogTitle
	 *            Gives the dialog a custom title. If null nothing happens.
	 * @param dir
	 *            preferred directory. if null, then the last used directory is
	 *            taken.
	 * @return The selected file or directory. null if cancelled.
	 */
	public static File chooseFile(Component parent, String dialogTitle, File dir) {
		return chooseFile(parent, dialogTitle, dir, true, null);
	}

	/**
	 * Displays a file open dialog and returns a selected file. If the dialog
	 * was cancelled, then null is returned
	 * 
	 * @param dialogTitle
	 *            Gives the dialog a custom title. If null nothing happens.
	 * @param dir
	 *            directory to start with.
	 * 
	 * @return The selected directory. null if cancelled.
	 */
	public static File chooseDir(Component parent, String dialogTitle, File dir) {
		JFileChooser fileChooser = new JFileChooser();

		if (dialogTitle != null) {
			fileChooser.setDialogTitle(dialogTitle);
		}

		if (dir != null && dir.exists() && dir.isDirectory()) {
			fileChooser.setCurrentDirectory(dir);
		}

		// only directories
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);

		int returnVal = fileChooser.showOpenDialog(parent);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}

	/**
	 * Gives the filename of a file without extension. If the file object is
	 * empty, or belongs to a directory, then null is returned.
	 * 
	 * @param file
	 *            file object of which we need the filename without extension
	 * @return filename without extension
	 */
	public static String getFileNameWithoutExtension(File file) {
		if (file == null || file.isDirectory()) {
			return null;
		}

		// remove the last dot plus extension
		String filename = file.getName();
		int lastDotPosition = filename.lastIndexOf('.');
		if (lastDotPosition > 0) {
			filename = filename.substring(0, lastDotPosition);
		}
		return filename;
	}

	/**
	 * Checks is a file has the given extension. If not, replace the existing
	 * one.
	 * 
	 * @param file
	 *            File object to replace extension
	 * @param extension
	 *            extension chosen.
	 * @return
	 */
	public static File enforceExtension(File file, String extension) {
		if (!file.getName().endsWith(extension)) {
			String fileStr = file.getAbsolutePath();
			int lastDot = fileStr.lastIndexOf('.');
			if (lastDot > 0) {
				fileStr = fileStr.substring(0, lastDot);
			}
			// add a dot if it is not the first character
			if (!extension.startsWith(".")) {
				extension = "." + extension;
			}
			file = new File(fileStr + extension);
		}
		return file;
	}
}
