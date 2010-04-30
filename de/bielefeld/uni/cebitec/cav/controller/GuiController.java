/***************************************************************************
 *   Copyright (C) 2008 by Peter Husemann                                  *
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

package de.bielefeld.uni.cebitec.cav.controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ProgressMonitor;
import javax.swing.filechooser.FileFilter;

import org.freehep.util.export.ExportDialog;

import de.bielefeld.uni.cebitec.cav.R2cat;
import de.bielefeld.uni.cebitec.cav.datamodel.AlignmentPositionsList;
import de.bielefeld.uni.cebitec.cav.datamodel.ContigSorter;
import de.bielefeld.uni.cebitec.cav.datamodel.SequenceOrderTableModel;
import de.bielefeld.uni.cebitec.cav.gui.AlignmentTable;
import de.bielefeld.uni.cebitec.cav.gui.CustomFileFilter;
import de.bielefeld.uni.cebitec.cav.gui.DataViewPlugin;
import de.bielefeld.uni.cebitec.cav.gui.DotPlotVisualisation;
import de.bielefeld.uni.cebitec.cav.gui.DotPlotVisualisationActionListener;
import de.bielefeld.uni.cebitec.cav.gui.HelpFrame;
import de.bielefeld.uni.cebitec.cav.gui.MainMenu;
import de.bielefeld.uni.cebitec.cav.gui.MainWindow;
import de.bielefeld.uni.cebitec.cav.gui.MatchDialog;
import de.bielefeld.uni.cebitec.cav.gui.SequenceOrderTable;
import de.bielefeld.uni.cebitec.cav.utils.MiscFileUtils;

public class GuiController {

	private MainWindow mainWindow = null;

	private DotPlotVisualisationActionListener dotPlotVisualisationActionListener = null;

	private MainMenu mainMenu = null;

	// not used at the moment. should store different types of visualisation
	// for a tabbed view
	// private Vector<DataViewPlugin> dataViews;

	private AlignmentTable alignmentTable = null;

	private JFrame tableFrame = null;

	private DotPlotVisualisation dotPlotVisualisation;

	/**
	 * 
	 */
	public GuiController() {
		// not used - see above
		// dataViews = new Vector<DataViewPlugin>();
	}

	public void createMainWindow() {
		mainWindow = new MainWindow(this);
		URL url = Thread.currentThread().getContextClassLoader().getResource("images/icon.png");
	//	URL url = R2cat.class.getResource("/images/icon.png");
		

		if (url!=null) {
		Image image = Toolkit.getDefaultToolkit().getImage(url);
//		     while ( !Toolkit.getDefaultToolkit().prepareImage( image, -1, -1, mainWindow ) ) {
//		       try {
//		         Thread.sleep( 100 );
//		       } catch ( Exception e ) {}
//		     }
		     mainWindow.setIconImage( image );
		}
	}

	public void showMainWindow() {
		mainWindow.setVisible(true);
	}

	public DataViewPlugin createDotPlotVisualisation(
			AlignmentPositionsList alignmentPositionsList) {
		dotPlotVisualisation = new DotPlotVisualisation(alignmentPositionsList);

		DotPlotVisualisationActionListener dotPlotVisualisationListener = new DotPlotVisualisationActionListener(
				this, dotPlotVisualisation);

		dotPlotVisualisation
				.addMouseMotionListener(dotPlotVisualisationListener);
		dotPlotVisualisation.addMouseListener(dotPlotVisualisationListener);
		dotPlotVisualisation
				.addMouseWheelListener(dotPlotVisualisationListener);
		dotPlotVisualisation.addKeyListener(dotPlotVisualisationListener);

		// load the previous state from prefs
		dotPlotVisualisation.drawGrid(R2cat.preferences
				.getDisplayGrid());
		dotPlotVisualisation.getAlignmentPositionDisplayerList()
				.showReversedComplements(
						R2cat.preferences
								.getDisplayReverseComplements());

		dotPlotVisualisation.getAlignmentPositionDisplayerList()
				.setDisplayOffsets(
						R2cat.preferences
								.getDisplayOffsets());

		return dotPlotVisualisation;
	}

	public void setVisualisation(DataViewPlugin vis) {
		mainWindow.setVisualisation((DotPlotVisualisation) vis);
		mainWindow.validate();
	}

	/**
	 * Sets that the AlignmentPositionDisplayers need to be regenerated
	 */
	public void setVisualisationNeedsUpdate() {
		if (visualisationInitialized()) {
			dotPlotVisualisation.getAlignmentPositionDisplayerList()
					.setNeedsRegeneration(true);
		}
	}

	public void createAlignmentsPositionTableFrame(
			AlignmentPositionsList alignmentPositionsList) {
		if (alignmentPositionsList != null) {
			tableFrame = new JFrame("Matching positions");
			AlignmentTable at = new AlignmentTable(alignmentPositionsList);
			JScrollPane tp = new JScrollPane(at);
			tableFrame.add(tp);
			
			
			int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
			int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

			tableFrame.setPreferredSize(new Dimension(width/2,height));
			tableFrame.setSize(new Dimension(width/2,height));

			
			tableFrame.setIconImage(mainWindow.getIconImage());
			tableFrame.pack();
			tableFrame.setLocationByPlatform(true);
		}
	}

	public void showAlignmentsPositionTableFrame() {
		if (tableFrame == null) {
			this
					.createAlignmentsPositionTableFrame(R2cat.dataModelController
							.getAlignmentPositionsList());
		}
		if (tableFrame != null) {
			tableFrame.setVisible(true);
		}
	}

	public void showQuerySortTable(AlignmentPositionsList alignmentPositionsList) {
		if (alignmentPositionsList != null) {
			JFrame querySort = new JFrame("Query order");
			querySort.setLayout(new BorderLayout());
			SequenceOrderTable qso = new SequenceOrderTable(
					alignmentPositionsList);
			SequenceOrderTableModel model = (SequenceOrderTableModel) qso
					.getModel();
			model.setShowComplementColumn(true);
			JScrollPane tp = new JScrollPane(qso);
			querySort.add(tp,BorderLayout.CENTER);

			JPanel controlPanel = new JPanel();
			controlPanel.add(new JLabel("Move one step towards sequence"));
			JButton up = new JButton("start");
			up.setActionCommand("up");
			controlPanel.add(up);
			up.addActionListener(qso);
			JButton down = new JButton("end");
			down.setActionCommand("down");
			controlPanel.add(down);
			down.addActionListener(qso);

			querySort.add(controlPanel,BorderLayout.SOUTH);
			querySort.pack();
			
			int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
			int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

			querySort.setSize(new Dimension(width/3,height));

			querySort.setIconImage(mainWindow.getIconImage());
			querySort.setLocationByPlatform(true);
			querySort.setVisible(true);

		}

	}

	public void showMatchDialog() {
		MatchDialog matchDialog = new MatchDialog(mainWindow);
		matchDialog.setIconImage(mainWindow.getIconImage());
		if (matchDialog != null) {
			matchDialog.pack();
			matchDialog.setLocationByPlatform(true);
			matchDialog.setVisible(true);
		}
	}
	
	public void showHelpFrame() {
		HelpFrame help = new HelpFrame();
		help.setIconImage(mainWindow.getIconImage());
		help.pack();
		help.setSize(800, 600);
		help.setLocationByPlatform(true);
		help.setVisible(true);
	}
	
	public void showAbout() {
		JEditorPane about = new JEditorPane();
		URL aboutUrl = Thread.currentThread().getContextClassLoader().getResource(
		"extra/about.html");
		try {
			about.setPage(aboutUrl);
		} catch (IOException e) {
			about.setText("Sorry, could not read about.html");
		}
		
		about.setPreferredSize(new Dimension(600,400));

		JOptionPane.showMessageDialog(mainWindow, new JScrollPane(about)
				,"About",JOptionPane.PLAIN_MESSAGE);
	}

	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public void displayReverseComplements(boolean unidirectional) {
		R2cat.preferences
				.setDisplayReverseComplements(unidirectional);

		if (R2cat.dataModelController
				.isAlignmentpositionsListReady()) {

			dotPlotVisualisation.getAlignmentPositionDisplayerList()
					.showReversedComplements(unidirectional);

			dotPlotVisualisation.repaint();
		}
	}

	public void displayGrid(boolean b) {
		R2cat.preferences.setDisplayGrid(b);
		if (R2cat.dataModelController
				.isAlignmentpositionsListReady()) {
			dotPlotVisualisation.drawGrid(b);
			dotPlotVisualisation.repaint();
		}
	}

	public void displayOffsets(boolean displayOffsets) {
		R2cat.preferences.setDisplayOffsets(displayOffsets);
		if (R2cat.dataModelController
				.isAlignmentpositionsListReady()) {

			dotPlotVisualisation.getAlignmentPositionDisplayerList()
					.setDisplayOffsets(displayOffsets);

			dotPlotVisualisation.repaint();

		}
	}

	public void initVisualisation() {
		if (!visualisationInitialized()) {
			if (R2cat.dataModelController
					.isAlignmentpositionsListReady()) {

				DataViewPlugin dotPlotVisualisation = this
						.createDotPlotVisualisation(R2cat.dataModelController
								.getAlignmentPositionsList());

				this.setVisualisation(dotPlotVisualisation);

			}
		}
	}

	/**
	 * Checks if the visualisation was created
	 * 
	 * @return
	 * 
	 */
	public boolean visualisationInitialized() {
		return (dotPlotVisualisation != null);
	}

	public void loadCSVFile() {
	File csv = this.chooseFile("Import matches from csv file", true, new CustomFileFilter(".csv", "comma separaded values"));
		if (csv != null) {
			R2cat.dataModelController
					.setAlignmentsPositonsListFromCSV(csv);
			this.setVisualisationNeedsUpdate();
		}
	}

	/**
	 * Shows a dialog to save the actual displayed hits to a file. This one can
	 * then be loaded with the loadProject() method.
	 */
	public void saveProject() {
		if (!R2cat.dataModelController
				.isAlignmentpositionsListReady()) {
			errorAlert("There is nothing to save!");
			return;
		}

		File f = this.chooseFile("Save project data to file", false, new CustomFileFilter(".r2c", "r2cat hits file"));
		if (f != null) {
			try {
				f = MiscFileUtils.enforceExtension(f, ".r2c");
				R2cat.dataModelController
						.writeAlignmentPositions(f);
			} catch (IOException e) {
				this.errorAlert("Unable to open file:" + e);
			}
		}
	}

	/**
	 * Shows a dialog to load a project file
	 */
	public void loadProject() {
		File f = this.chooseFile("Load project data from file", true, new CustomFileFilter(".r2c", "r2cat hits file"));
		if (f != null) {
			try {
				R2cat.dataModelController
						.readAlignmentPositions(f);
				this.setVisualisationNeedsUpdate();
			} catch (IOException e) {
				this.errorAlert("Unable to open file:" + e);
			}
		}

	}

	/**
	 * Gives a dialog to save the contigs in the displayed order and orientation
	 * as fasta files.
	 */
	public void exportOrderFasta() {
		if (!R2cat.dataModelController
				.isAlignmentpositionsListReady()) {
			errorAlert("There is nothing to save!");
			return;
		}

		File f = this.chooseFile("Export contig order and orientation to FASTA", false, new CustomFileFilter(".fas,.fna,.fasta", "FASTA File"));
		if (f != null) {
			this.exportAsFastaFile(f, false);
		} else {
			return;
		}
	}

	/**
	 * Creates a dialog to export the displayed order of the contigs' fasta id's into a text file.
	 */
	public void exportOrderText() {
		if (!R2cat.dataModelController.isAlignmentpositionsListReady()) {
			errorAlert("There is nothing to save!");
			return;
		}

		File f = this.chooseFile(
				"Export contig order and orientation to text file", false,
				new CustomFileFilter(".txt,.text", "Text File"));
		if (f != null) {
			if (!f.getName().endsWith(".txt") || !f.getName().endsWith(".text")) {
				f = new File(f.getAbsolutePath() + ".txt");
			}
			try {
				R2cat.dataModelController.writeOrderOfContigs(f);
			} catch (IOException e) {
				errorAlert(e.getLocalizedMessage());
			}
		} else {
			return;
		}
	}

	private void exportAsFastaFile(File f, boolean ignoreMissingFiles) {
		try {
			if (!f.getName().endsWith(".fas")||!f.getName().endsWith(".fasta")||!f.getName().endsWith(".fna")) {
				f = new File(f.getAbsolutePath() + ".fas");
			}

			// TODO check if all files are existent

			// mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			int contigsWritten = R2cat.dataModelController
					.writeOrderOfContigsFasta(f, ignoreMissingFiles);
			 int totalContigs = R2cat.dataModelController.getAlignmentPositionsList().getQueries().size();
			JOptionPane.showMessageDialog(getMainWindow(),
					"Wrote " + contigsWritten 
					+ (contigsWritten!=totalContigs?(" out of " + totalContigs):"")
					+ " contigs into file:\n"+(f.getAbsolutePath()) ,
					"File written", JOptionPane.INFORMATION_MESSAGE);
			// mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		} catch (IOException e) {
			if (e.getClass() == SequenceNotFoundException.class) {
				
				SequenceNotFoundException seqNotFoundException = (SequenceNotFoundException)e;

				Object[] options = { "Yes", "No, leave out missing sequences", "Abort" };
				int n = JOptionPane.showOptionDialog(this.getMainWindow(),
						e.getMessage()+"\nDo you want to select a file?",
						"Sequence not found", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE, null,
						options,
						options[0]);
				
				switch (n) {
				case JOptionPane.YES_OPTION:
				seqNotFoundException.getDNASequence().setFile(this.chooseFile("Choose a new file",true, new CustomFileFilter(".fas,.fna,.fasta", "Fasta file")));
					this.exportAsFastaFile(f, ignoreMissingFiles);
					break;
				case JOptionPane.NO_OPTION:
					this.exportAsFastaFile(f, true);
					break;
				case JOptionPane.CANCEL_OPTION:
					return;

				default:
					return;
				}

			} else {
				this.errorAlert(e.getMessage() + "\nNothing was saved");
			}
		}

	}

	
	/**
	 * Chooses a file for opening or saving data
	 * @param dialogTitle Title of the dialog to show
	 * @param openDialog true:display open dialog; false=display save dialog
	 * @param filter A file filter to restrict the files shown. Use the CustomFileFilter
	 * @return The selected File or null, if aborted
	 */
	private File chooseFile(String dialogTitle, boolean openDialog,
			FileFilter filter) {
		File lastFile = new File(R2cat.preferences
				.getLastFile());
		File lastDir = lastFile.getParentFile();

		lastFile = MiscFileUtils.chooseFile(mainWindow, dialogTitle, lastDir,
				openDialog, filter);
		if (lastFile != null) {
			R2cat.preferences.setLastFile(lastFile.getAbsolutePath());
		}
		return lastFile;
	}

	
	/**
	 * Pop up an error message
	 * 
	 * @param error
	 *            Message
	 */
	private void errorAlert(String error) {
		JOptionPane.showMessageDialog(mainWindow, error, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * This method sorts the contigs with the contig sorter (in the background with a thread). During the sorting a progess bar is displayed.
	 * When the thread finishes it has to call sortContigsDone(), to update the gui and the datamodel.
	 */
	public void sortContigs() {
		ContigSorter sorter = new ContigSorter(R2cat.dataModelController.getAlignmentPositionsList());

		ProgressMonitor progress = new ProgressMonitor(mainWindow,"Sorting contigs",null,0,100);
	
		if (progress!=null) {
		sorter.register(progress);
		}
		sorter.register(this);
		
		//if the sorting is not done in a thread, the gui blocks
		Thread t = new Thread(sorter);
		t.start();
	}
	/**
	 * The thread in the sortContigs methhod calls this to say it is ready.
	 * @param sorter
	 */
	public void sortContigsDone(ContigSorter sorter) {
		R2cat.dataModelController.getAlignmentPositionsList().changeQueryOrder(sorter.getQueryOrder());
		R2cat.dataModelController.getAlignmentPositionsList().notifyObservers(AlignmentPositionsList.NotifyEvent.ORDER_CHANGED_OR_CONTIG_REVERSED);
		this.setVisualisationNeedsUpdate();
		dotPlotVisualisation.repaint();

	}

	public void exportAlignmentPositionsListAsImage() {
		ExportDialog export=new ExportDialog("r2cat",true);
		export.removeUninterestingFileTypes(); //hack to remove some bitmat formats
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
		java.util.Date date = new java.util.Date();

		export.showExportDialog(mainWindow, "Export view to file", dotPlotVisualisation, "r2catExport"+dateFormat.format(date));
	}
	

}
