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

package de.bielefeld.uni.cebitec.r2cat.gui;

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

import de.bielefeld.uni.cebitec.common.CustomFileFilter;
import de.bielefeld.uni.cebitec.common.MiscFileUtils;
import de.bielefeld.uni.cebitec.common.SequenceNotFoundException;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.r2cat.ContigSorter;
import de.bielefeld.uni.cebitec.r2cat.R2cat;
import de.bielefeld.uni.cebitec.r2cat.help.HelpFrame;

public class GuiController {

	private MainWindow mainWindow = null;

	private DotPlotMatchViewerActionListener dotPlotMatchViewerActionListener = null;

	private MainMenu mainMenu = null;

	// not used at the moment. should store different types of visualisation
	// for a tabbed view
	// private Vector<DataViewPlugin> dataViews;

	private MatchesTable matchesTable = null;

	private JFrame tableFrame = null;

	private DotPlotMatchViewer dotPlotMatchViewer;

	/**
	 * 
	 */
	public GuiController() {
		// not used - see above
		// dataViews = new Vector<DataViewPlugin>();
	}

	public void createMainWindow() {
		mainWindow = new MainWindow(this);
		URL url = Thread.currentThread().getContextClassLoader().getResource("de/bielefeld/uni/cebitec/r2cat/gui/cursorimages/icon.png");
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

	public MatchViewerPlugin createDotPlotVisualisation(
			MatchList matchList) {
		dotPlotMatchViewer = new DotPlotMatchViewer(matchList);

		DotPlotMatchViewerActionListener dotPlotVisualisationListener = new DotPlotMatchViewerActionListener(
				this, dotPlotMatchViewer);

		dotPlotMatchViewer
				.addMouseMotionListener(dotPlotVisualisationListener);
		dotPlotMatchViewer.addMouseListener(dotPlotVisualisationListener);
		dotPlotMatchViewer
				.addMouseWheelListener(dotPlotVisualisationListener);
		dotPlotMatchViewer.addKeyListener(dotPlotVisualisationListener);

		// load the previous state from prefs
		dotPlotMatchViewer.drawGrid(R2cat.preferences
				.getDisplayGrid());
		dotPlotMatchViewer.getMatchDisplayerList()
				.showReversedComplements(
						R2cat.preferences
								.getDisplayReverseComplements());

		dotPlotMatchViewer.getMatchDisplayerList()
				.setDisplayOffsets(
						R2cat.preferences
								.getDisplayOffsets());

		return dotPlotMatchViewer;
	}

	public void setVisualisation(MatchViewerPlugin vis) {
		mainWindow.setVisualisation((DotPlotMatchViewer) vis);
		mainWindow.validate();
	}

	/**
	 * Sets that the MatchDisplayers need to be regenerated
	 */
	public void setVisualisationNeedsUpdate() {
		if (visualisationInitialized()) {
			dotPlotMatchViewer.getMatchDisplayerList()
					.setNeedsRegeneration(true);
		}
	}

	public void createAlignmentsPositionTableFrame(
			MatchList matchList) {
		if (matchList != null) {
			tableFrame = new JFrame("Matching positions");
			MatchesTable at = new MatchesTable(matchList);
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
							.getMatchesList());
		}
		if (tableFrame != null) {
			tableFrame.setVisible(true);
		}
	}

	public void showQuerySortTable(MatchList matchList) {
		if (matchList != null) {
			JFrame querySort = new JFrame("Query order");
			querySort.setLayout(new BorderLayout());
			SequenceOrderTable qso = new SequenceOrderTable(
					matchList);
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
		"de/bielefeld/uni/cebitec/r2cat/help/about.html");
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
				.isMatchesListReady()) {

			dotPlotMatchViewer.getMatchDisplayerList()
					.showReversedComplements(unidirectional);

			dotPlotMatchViewer.repaint();
		}
	}

	public void displayGrid(boolean b) {
		R2cat.preferences.setDisplayGrid(b);
		if (R2cat.dataModelController
				.isMatchesListReady()) {
			dotPlotMatchViewer.drawGrid(b);
			dotPlotMatchViewer.repaint();
		}
	}

	public void displayOffsets(boolean displayOffsets) {
		R2cat.preferences.setDisplayOffsets(displayOffsets);
		if (R2cat.dataModelController
				.isMatchesListReady()) {

			dotPlotMatchViewer.getMatchDisplayerList()
					.setDisplayOffsets(displayOffsets);

			dotPlotMatchViewer.repaint();

		}
	}

	public void initVisualisation() {
		if (!visualisationInitialized()) {
			if (R2cat.dataModelController
					.isMatchesListReady()) {

				MatchViewerPlugin dotPlotVisualisation = this
						.createDotPlotVisualisation(R2cat.dataModelController
								.getMatchesList());

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
		return (dotPlotMatchViewer != null);
	}

	public void loadCSVFile() {
	File csv = this.chooseFile("Import matches from csv file", true, new CustomFileFilter(".csv", "comma separaded values"));
		if (csv != null) {
			R2cat.dataModelController
					.setMatchesFromCSVFile(csv);
			this.setVisualisationNeedsUpdate();
		}
	}

	/**
	 * Shows a dialog to save the actual displayed hits to a file. This one can
	 * then be loaded with the loadProject() method.
	 */
	public void saveProject() {
		if (!R2cat.dataModelController
				.isMatchesListReady()) {
			errorAlert("There is nothing to save!");
			return;
		}

		File f = this.chooseFile("Save project data to file", false, new CustomFileFilter(".r2c", "r2cat hits file"));
		if (f != null) {
			try {
				f = MiscFileUtils.enforceExtension(f, ".r2c");
				R2cat.dataModelController
						.writeMatches(f);
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
						.readMatches(f);
				this.setVisualisationNeedsUpdate();
			} catch (IOException e) {
				this.errorAlert("Unable to open file:" + e);
			}
		}

	}
	
	/**
	 * @author Rolf Hilker
	 * Gives a dialog to save all unmatched contigs in one fasta file.
	 */
	public void exportUnmatchedFasta() {
		if (!R2cat.dataModelController
				.isUnmatchedListReady()) {
			errorAlert("No unmatched contigs available to save!");
			return;
		}
		
		File f = this.chooseFile("Save unmatched contigs (fasta format)", false, new CustomFileFilter(".fas,.fna,.fasta", "FASTA File"));
		if (f != null) {
			this.exportUnmatchedAsFastaFile(f, false);
		} else {
			return;
		}
	}

	/**
	 * Gives a dialog to save the contigs in the displayed order and orientation
	 * as fasta files.
	 */
	public void exportOrderFasta() {
		if (!R2cat.dataModelController
				.isMatchesListReady()) {
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
		if (!R2cat.dataModelController.isMatchesListReady()) {
			errorAlert("There is nothing to save!");
			return;
		}

		
//		//debugging:-----------------------------------------
//		boolean debug = true;
//		if (debug) {
//		JFrame orderInformation = new JFrame();
//		orderInformation.setMinimumSize(new Dimension(600,800));
//		orderInformation.setLayout(new BorderLayout());
//		JTextArea order = new JTextArea();
//		order.setText(R2cat.dataModelController.getMatchesList().getContigsOrderAsTextWithExtendedInformation());
//		ScrollPane scroll = new ScrollPane();
//		scroll.add(order);
//		orderInformation.add(scroll,BorderLayout.CENTER);
//		orderInformation.pack();
//		orderInformation.setVisible(true);
//		return;
//		}
//		//---------------------------------------------
//		

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
			MiscFileUtils.enforceExtension(f, ".fas");

			// mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			int contigsWritten = R2cat.dataModelController
					.writeOrderOfContigsFasta(f, ignoreMissingFiles);
			 int totalContigs = R2cat.dataModelController.getMatchesList().getQueries().size();
			JOptionPane.showMessageDialog(getMainWindow(),
					"Wrote " + contigsWritten 
					+ (contigsWritten!=totalContigs?(" out of " + totalContigs):"")
					+ " contigs into file:\n"+(f.getAbsolutePath()) ,
					"File written", JOptionPane.INFORMATION_MESSAGE);
			// mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		} catch (IOException e) {
			if (e.getClass() == SequenceNotFoundException.class) {
				int answer = SequenceNotFoundException.handleSequenceNotFoundException((SequenceNotFoundException)e);
				
				switch (answer) {
				case JOptionPane.YES_OPTION:
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
	 * @author Rolf Hilker
	 * 
	 * Exports all unmatched contigs into a single fasta file.
	 * @param f file to write to
	 * @param ignoreMissingFiles true, if missing files should be ignored
	 */
	private void exportUnmatchedAsFastaFile(File f, boolean ignoreMissingFiles) {
		try {
			MiscFileUtils.enforceExtension(f, ".fas");

			// mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			int contigsWritten = R2cat.dataModelController
					.writeUnmatchedContigsFasta(f, ignoreMissingFiles);
			JOptionPane.showMessageDialog(getMainWindow(),
					"Wrote " + contigsWritten 
					+ " unmatched contigs into file:\n"+(f.getAbsolutePath()) ,
					"File written", JOptionPane.INFORMATION_MESSAGE);
			// mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		} catch (IOException e) {
			if (e.getClass() == SequenceNotFoundException.class) {
				int answer = SequenceNotFoundException.handleSequenceNotFoundException((SequenceNotFoundException)e);
				
				switch (answer) {
				case JOptionPane.YES_OPTION:
					this.exportUnmatchedAsFastaFile(f, ignoreMissingFiles);
					break;
				case JOptionPane.NO_OPTION:
					this.exportUnmatchedAsFastaFile(f, true);
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
		ContigSorter sorter = new ContigSorter(R2cat.dataModelController.getMatchesList());

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
		R2cat.dataModelController.getMatchesList().changeQueryOrder(sorter.getQueryOrder());
		R2cat.dataModelController.getMatchesList().notifyObservers(MatchList.NotifyEvent.ORDER_CHANGED_OR_CONTIG_REVERSED);
		this.setVisualisationNeedsUpdate();
		dotPlotMatchViewer.repaint();

	}
	
	/**
	 * Sort the targets by their size. Biggest first.
	 */
	public void sortTargetsBySize() {
		R2cat.dataModelController.getMatchesList().sortTargetsBySize();
		R2cat.dataModelController.getMatchesList().notifyObservers(MatchList.NotifyEvent.ORDER_CHANGED_OR_CONTIG_REVERSED);
		this.setVisualisationNeedsUpdate();
		dotPlotMatchViewer.repaint();

	}

	public void exportMatchesAsImage() {
		ExportDialog export=new ExportDialog("r2cat",true);
		export.removeUninterestingFileTypes(); //hack to remove some bitmat formats
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
		java.util.Date date = new java.util.Date();

		export.showExportDialog(mainWindow, "Export view to file", dotPlotMatchViewer, "r2catExport"+dateFormat.format(date));
	}
	
	public void showGeneratePrimerFrame(MatchList matchList) {
		if (matchList != null) {	
			PrimerFrame primer = new PrimerFrame(matchList);
			primer.setIconImage(mainWindow.getIconImage());
			primer.pack();
			primer.setLocationByPlatform(true);
			primer.setVisible(true);
		}	
	}
/*	public void showPrimerResults(PrimerGenerator pg, Vector<String> output){
		PrimerResults pr = new PrimerResults(pg, output);
		pr.setIconImage(mainWindow.getIconImage());
		pr.pack();
		pr.setLocationByPlatform(true);
		pr.setVisible(true);
	}*/
}
