package de.bielefeld.uni.cebitec.cav.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import de.bielefeld.uni.cebitec.cav.primerdesign.PrimerResult;

public class PrimerResultFrame extends JFrame implements ActionListener {

	private Vector<PrimerResult> primerResults;
	private JTabbedPane tabbedPane;

	public PrimerResultFrame(Vector<PrimerResult> pr) {
		primerResults = pr;
		init();
	}

	/**
	 * Initializes the gui components of this frame
	 */
	public void init() {
		this.setTitle("Primer Results");
		this.setLayout(new BorderLayout());
		JPanel controlPanel = new JPanel();
		tabbedPane = new JTabbedPane();

		String tabName;
		for (int j = 0; j < primerResults.size(); j++) {
			JTextArea primerResultText = new JTextArea(primerResults.elementAt(
					j).toString(),50,80);
			tabName = primerResults.elementAt(j).getContigIDs();
			primerResultText.setEditable(false);

			JScrollPane scrollPane = new JScrollPane(primerResultText);
			   scrollPane.setAutoscrolls(true);
			 int max = scrollPane.getVerticalScrollBar().getMaximum();
		      scrollPane.getVerticalScrollBar().setValue( max );
			tabbedPane.addTab(tabName, scrollPane);
		}

		tabbedPane.setSelectedIndex(0);
		this.add(tabbedPane);

		JButton exportCurrentResult;
		if (primerResults.size() > 1) {
			exportCurrentResult = new JButton("Save current");
		} else {
			exportCurrentResult = new JButton("Save");
		}
		exportCurrentResult.setActionCommand("saveCurrentResult");
		exportCurrentResult.addActionListener(this);
		controlPanel.add(exportCurrentResult);

		//these are only needed, if there are more than one result lists.
		if (primerResults.size() > 1) {
			JButton exportAllResults;
			exportAllResults = new JButton("Save all in single file");
			exportAllResults.setActionCommand("saveAllResultsToOneFile");
			exportAllResults.addActionListener(this);
			controlPanel.add(exportAllResults);

			JButton exportEachResultToSeperateFile;
			exportEachResultToSeperateFile = new JButton(
					"Save all in separate files");
			exportEachResultToSeperateFile
					.setActionCommand("saveAllResultsToSeperateFile");
			exportEachResultToSeperateFile.addActionListener(this);
			controlPanel.add(exportEachResultToSeperateFile);
		}

		this.add(controlPanel, BorderLayout.SOUTH);
		this.pack();

		int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

		this.setSize(new Dimension(width / 3, height));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("saveCurrentResult")) {
			try {
				this.saveCurrentResult();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(),
						"Error saving primer results",
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		} else if (e.getActionCommand().equals("saveAllResultsToOneFile")) {
			try {
				this.saveAllResultsInSingleFile();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(),
						"Error saving primer results",
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		} else if (e.getActionCommand().equals("saveAllResultsToSeperateFile")) {
			try {
				Vector<File> files = saveAllResultsInSeveralFiles();
				String filesWritten = "Wrote:\n";
				if (files.size() < 1) {
					filesWritten = "Sorry, no files written";
				} else if (files.size() < 5) {
					for (int i = 0; i < files.size(); i++) {
						filesWritten += files.get(i).getName() + "\n";
					}
				} else {
					filesWritten += files.get(0).getName() + "\n"
							+ files.get(1).getName() + "\n...\n"
							+ files.get(files.size() - 1).getName();
				}
				JOptionPane.showMessageDialog(this, filesWritten,
						"Saving Primer Results",
						JOptionPane.INFORMATION_MESSAGE);

			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(),
						"Error saving primer results",
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Saves all primer results to several files in a user specified directory.
	 * 
	 * @return the list of files that were written
	 * @throws IOException
	 */
	public Vector<File> saveAllResultsInSeveralFiles() throws IOException {
		// the files that are written
		Vector<File> files = new Vector<File>();

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnValue = fc.showDialog(this, "Save to Directory");
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File directory = fc.getSelectedFile();
			for (int i = 0; i < primerResults.size(); i++) {
				File resultFile = new File(directory,
						"r2cat_Primerlist_for_contigs_"
								+ primerResults.get(i).getContigIDs() + ".txt");
				BufferedWriter writeToFile = new BufferedWriter(new FileWriter(
						resultFile));
				writeToFile.write(primerResults.elementAt(i).toString());
				writeToFile.close();
				files.add(resultFile);
			}
		}

		return files;

	}

	/**
	 * Saves all primer results in a single file that the user can choose
	 * 
	 * @return the file, if a user chose it, otherwise null
	 * @throws IOException
	 */
	public File saveAllResultsInSingleFile() throws IOException {
		File output = null;

		JFileChooser fc = new JFileChooser();
		int returnValue = fc.showSaveDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			output = fc.getSelectedFile();
			final String NEW_LINE = System.getProperty("line.separator");
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			for (int i = 0; i < primerResults.size(); i++) {
				writer.append(primerResults.elementAt(i).toString());
				writer.append(NEW_LINE + NEW_LINE);
			}
			writer.close();
		}
		return output;
	}

	/**
	 * Saves the currently displayed primer result into a file.
	 * 
	 * @return the written file, or null if the user aborted
	 * @throws IOException
	 */
	public File saveCurrentResult() throws IOException {
		File output = null;
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File(primerResults.get(
				tabbedPane.getSelectedIndex()).getContigIDs()
				+ ".txt"));
		int returnValue = fc.showSaveDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			output = fc.getSelectedFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			// the primer results and the tabbed panes have corresponding
			// indices.
			writer.write(primerResults.get(tabbedPane.getSelectedIndex())
					.toString());
			writer.close();
		}
		return output;
	}

}
