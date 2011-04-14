package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph.AdjacencyEdge;
import de.bielefeld.uni.cebitec.qgram.DNASequence;

public class CagController implements CagEventListener  {
	
	private CagCreator cagModel;
	private CAGWindow window;
	private CagController con = this;
	
	private ChooseContigPanel chooseContigPanel;
	private JScrollPane scrollPane;
	private ContigListPanel listScroller;
	private LegendAndInputOptionPanel inputOption;
	
	private boolean selectionByUpdate;
	public boolean isSelectionByUpdate() {
		return selectionByUpdate;
	}

	public void setSelectionByUpdate(boolean selectionByUpdate) {
		this.selectionByUpdate = selectionByUpdate;
	}

	private boolean contigsInChooseContigPanel;
	private RadioButtonActionListener radioButtonListener;
	private boolean leftNeigboursReady;
	private boolean rightNeighboursReady;

	
	public boolean isLeftNeigboursReady() {
		return leftNeigboursReady;
	}

	public void setLeftNeigboursReady(boolean leftNeigboursReady) {
		this.leftNeigboursReady = leftNeigboursReady;
	}

	public boolean isRightNeighboursReady() {
		return rightNeighboursReady;
	}

	public void setRightNeighboursReady(boolean rightNeighboursReady) {
		this.rightNeighboursReady = rightNeighboursReady;
	}

	public CagController (CagCreator model) {
		
		this.cagModel = model;
		model.addEventListener(this);
		
	}
	
	public void initWindowWithAllPanel(){
		
		window = new CAGWindow(this, cagModel);
		LayoutGraph layoutGraph = cagModel.getGraph();

//		Vector<DNASequence> nodes = cagModel.getListData();
		Vector<DNASequence> nodes = cagModel.getGraph().getNodes();
		int width = nodes .firstElement().getId().getBytes().length;
		
		if (width * 20 <= 100) {
			width = 100;
		} else if (width * 20 >= 200) {
			width = 200;
		} else {
			width = width * 20;
		}
		
		window.setMinimumSize(new Dimension((int) Toolkit.getDefaultToolkit()
				.getScreenSize().getWidth()
				- width, 500));
		window.setPreferredSize(new Dimension((int) Toolkit.getDefaultToolkit()
				.getScreenSize().getWidth()
				- width, 500));
		window.setMaximumSize(new Dimension((int) Toolkit.getDefaultToolkit()
				.getScreenSize().getWidth()
				- width, 500));
		

		double maxSupport = cagModel.getMaxSupport();
		double minSupport = cagModel.getMinSupport();
		
		
		boolean isZScore = cagModel.isZScore();
		int numberOfNeighbours = cagModel.getNumberOfNeighbours();
		/*
		 * This panel contains all illustrations of the contigs
		 * central contig in the middle and neighbours on the right and left side
		 */
		chooseContigPanel = new ChooseContigPanel(numberOfNeighbours, isZScore,
				maxSupport, minSupport);
		chooseContigPanel.createPanel();
		
		/*
		 * This scrollpane is going to be used, when the user choose 
		 * more than 8 neighbours or just when the size of the chooseContigPanel
		 * is to big.
		 */
		scrollPane = new JScrollPane(chooseContigPanel);
		scrollPane.setPreferredSize(new Dimension((int) Toolkit
				.getDefaultToolkit().getScreenSize().getWidth()
				- width, 500));
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setName("scroll pane");
//		scrollPane.setAlignmentX(TOP_ALIGNMENT);
		scrollPane.setVisible(true);
		scrollPane.setOpaque(false);
		scrollPane.validate();
		window.add(scrollPane, BorderLayout.CENTER);
		
		listScroller = new ContigListPanel(this, cagModel);
		listScroller.createList();
		window.add(listScroller, BorderLayout.EAST);

		inputOption = new LegendAndInputOptionPanel(this, cagModel);
		inputOption.createLegendAndInputOption(numberOfNeighbours);
		window.add(inputOption, BorderLayout.SOUTH);
		radioButtonListener = new RadioButtonActionListener(this, cagModel);
		
		window.initWindow();
	}
		public CAGWindow getWindow() {
		return window;
	}

	public ChooseContigPanel getChooseContigPanel() {
		return chooseContigPanel;
	}

		/*
		 * Get Events
		 */
		@Override
		public void event_fired(CagEvent event) {

			Vector<Vector<AdjacencyEdge>> selectedLeftEdges = cagModel.getSelectedLeftEdges();
			Vector<Vector<AdjacencyEdge>> selectedRightEdges = cagModel.getSelectedRightEdges();
			int centralContigIndex;
			/*
			 * If a contig is choosed from the list or the panel, the neighbours are going to be 
			 * calculated and events fired the neighbours.
			 * The choosed contig is then the central contig.  
			 */
			if (event.getEvent_type().equals(EventType.EVENT_CHOOSED_CONTIG)) {
				
				selectionByUpdate = true;
				contigsInChooseContigPanel = true;
				
				DNASequence currentContig = event.getContigNode();

				centralContigIndex = event.getIndex();
				boolean isReverse = event.isReverse();
				boolean isSelected = false;

				if (!selectedLeftEdges.elementAt(centralContigIndex).isEmpty()
						|| !selectedRightEdges.elementAt(centralContigIndex).isEmpty()) {
					isSelected = true;
				}

				ContigAppearance centralContig = new ContigAppearance(currentContig,
						centralContigIndex, isSelected, isReverse,
						cagModel.getMaxSizeOfContigs(), cagModel.getMinSizeOfContigs());

				JPanel centerContainer = chooseContigPanel.getCenterContainer();

				if (centerContainer.getComponentCount() > 0) {
					centerContainer.removeAll();
				}

				listScroller.getList().setSelectedValue(currentContig.getId(), true);
				chooseContigPanel.setCentralContig(centralContig);
				centerContainer.add(centralContig);
				centerContainer.updateUI();
				
				selectionByUpdate = false;
			}
			
			/*
			 * Also the neighbours going to be changed, if the central contig changed.
			 */
			if (event.getEvent_type().equals(EventType.EVENT_SEND_LEFT_NEIGHBOURS)) {
				System.out.println("linke nachbarn angekommen");
				leftNeigboursReady = false;

				Vector<AdjacencyEdge> leftNeighbourEdges = event.getEdges();
				ContigAppearance contigPanel = null;

				ContigRadioButton radioButton;
				double[] leftSupport = new double[cagModel.getNumberOfNeighbours()];
				int t = 0;

				JPanel leftContainer = chooseContigPanel.getLeftContainer();
				JPanel leftRadioButtonContainer = chooseContigPanel
						.getLeftRadioButtonContainer();

				clearComponets(leftContainer, leftRadioButtonContainer);

				/*
				 * The terminator finish the creation of the layout. 
				 * it has either to be the number of neighbours 
				 * or if the number of neighbours, which is choosed from 
				 * user is bigger than there are neighbours, it should
				 * finish earlier.
				 */
				int terminator = setTerminator(leftNeighbourEdges);

				boolean isALeftNeighourSelected = false;
				AdjacencyEdge whichNeighbourIsSelected = null;

				/*
				 * Figure out, if there is a neighbour already selected
				 */
				for (AdjacencyEdge e : leftNeighbourEdges) {
					if (e.isSelected()) {
						isALeftNeighourSelected = true;
						whichNeighbourIsSelected = e;
					}
				}
				/*
				 * This is necessary to set the layout of the choosed ContigPanel
				 * or rather for the leftcontainer 
				 */
//				leftContainer.add(Box.createVerticalGlue());
//				leftRadioButtonContainer.add(Box.createVerticalGlue());
				for (AdjacencyEdge edge : leftNeighbourEdges) {

					if (t < terminator) {

						int indexOfContig = indexOfNeighbourContig(edge);
						
						/*
						 * Save the support or z-scores here in an array
						 * to commit them to the choose contig panel
						 * for setting the linestroke 
						 * 
						 * TODO berechnung des zScore für alle nachbarn zu
						 * Anfang im model
						 */
						if (cagModel.isZScore()) {
							leftSupport[t] = calculateZScore(edge,
									cagModel.getCurrentContigIndex(), cagModel.getMeanForLeftNeigbours(),
									cagModel.getsDeviationsForLeftNeigbours());
						} else {
							leftSupport[t] = edge.getSupport();
						}
						
						//Vector<Vector<AdjacencyEdge>> leftEdges = centralContig.isReverse() ? selectedRightEdges : selectedLeftEdges;
						boolean anderweitigAusgewaehlt = ulteriorSelected(true, indexOfContig, edge);//, leftEdges);

						/*
						 * Set the appearance for each contig
						 */
						System.out.println("l erzeuge contig appearance ");
						contigPanel = new ContigAppearance(cagModel.getGraph(), edge,
								indexOfContig, true, cagModel.getMaxSizeOfContigs(),
								cagModel.getMinSizeOfContigs(), anderweitigAusgewaehlt);
						contigPanel.addMouseListener(new ContigMouseListener());
//						contigPanel.setAlignmentX(RIGHT_ALIGNMENT);

						/*
						 * The radio Button get commands to differentiate between 
						 * adjacencies which are already selected, or selected somewhere
						 * else or not selected
						 */
						radioButton = new ContigRadioButton(edge, contigPanel);

						if (isALeftNeighourSelected) {
							radioButton.setActionCommand("nachbarAusgewaehlt");
							radioButton
							.setSelectedNeighbourOfButtonGroup(whichNeighbourIsSelected);
						}else 	if (!isALeftNeighourSelected) {
							radioButton
							.setActionCommand("noch kein nachbar ausgewaehlt");
						}					
						
						if (anderweitigAusgewaehlt) {
							radioButton.setActionCommand("anderweitigAusgewaehlt");
							AdjacencyEdge otherEdgeForThisNeighbour = selectedLeftEdges.get(indexOfContig).firstElement();
							radioButton
									.setNeighboursForTheThisNeighbour(otherEdgeForThisNeighbour);
						}
						if (edge.isSelected()) {
							radioButton.setSelected(true);
						}
//						System.out.println("l "+radioButton.getActionCommand());
						radioButton.setLeft(true);
						radioButton.setOpaque(false);
						radioButton
								.addActionListener(new RadioButtonActionListener(this, cagModel));

						// add here Contigs and RadioButton with 	dynamic space 
						leftContainer.add(contigPanel);

						Container leftGroup = null;
						leftGroup.add(radioButton);
						leftRadioButtonContainer.add(radioButton);

						/*
						 * There will be added some 	dynamic space
						 */
						if (t < (cagModel.getNumberOfNeighbours() - 1)) {
							leftContainer.add(Box.createVerticalGlue());
							leftRadioButtonContainer.add(Box.createVerticalGlue());
						}
						leftContainer.updateUI();
						leftRadioButtonContainer.updateUI();
						t++;
					}
					if (t == terminator) {
						break;
					}
				}
				leftContainer.add(Box.createVerticalGlue());
				leftRadioButtonContainer.add(Box.createVerticalGlue());
				chooseContigPanel.setLeftSupport(leftSupport);
				leftNeigboursReady = true;
				repaintWindowAndSubcomponents();
			}

			/*
			 * Also right neigbours
			 */
			if (event.getEvent_type().equals(EventType.EVENT_SEND_RIGHT_NEIGHBOURS)) {
				rightNeighboursReady = false;
				System.out.println("rechte nachbarn angekommen ");
				int s = 0;

				Vector<AdjacencyEdge> rightNeighbourEdges = event.getEdges();
				ContigAppearance contigPanel = null;

				/*
				 * Der Terminator muss entweder nach der Anzahl der Nachbarn zum
				 * Abbruch führen oder aber, wenn weniger Nachbarn auswählbar sind,
				 * nach dieser geringeren Anzahl einen Abbruch herbei führen.
				 */
				int terminator = setTerminator(rightNeighbourEdges);

				ContigRadioButton radioButton;
				double[] rightSupport = new double[cagModel.getNumberOfNeighbours()];

				/*
				 * Zunächst Löschen aller bisherigen Elemente in der GUI
				 */
				JPanel rightContainer = chooseContigPanel.getRightContainer();
				JPanel rightRadioButtonContainer = chooseContigPanel
						.getRightRadioButtonContainer();
				clearComponets(rightContainer, rightRadioButtonContainer);

				// Flag das gesetzt wird sollte einer der Nachbarn schon ausgewählt
				// worden sein.
				boolean isARightNeighourSelected = false;
				AdjacencyEdge neighbourForThisGroup = null;
				/*
				 * Setzten des Flags, falls einer der Nachbarn schon ausgewählt ist
				 * er das so wird damit kein anderer rechter Nachbarn auswählbar.
				 */
				for (AdjacencyEdge e : rightNeighbourEdges) {
					if (e.isSelected()) {
						isARightNeighourSelected = true;
						neighbourForThisGroup = e;
					}
				}

				/*
				 * For each adjacency edge here is going to be a contig Panel
				 */
		//		rightContainer.add(Box.createVerticalGlue());
		//		rightRadioButtonContainer.add(Box.createVerticalGlue());
				for (AdjacencyEdge edge : rightNeighbourEdges) {
					if (s < terminator) {

						int indexOfContig = indexOfNeighbourContig(edge);
						
						/*
						 * Speichern des relativen oder absoluten Support in einem
						 * Array, dieses Array wird später dem GlasPanel übergeben
						 * und die Liniendicke berechnet.
						 */
						if (cagModel.isZScore()) {
							rightSupport[s] = calculateZScore(edge,
									cagModel.getCurrentContigIndex(), cagModel.getMeanForRightNeigbours(),
									cagModel.getsDeviationsForRightNeigbours());
						} else {
							rightSupport[s] = edge.getSupport();
						}
						/*
						 * Hier wird für jeden Nachbarn sein Aussehen erstellt.
						 * 
						 * Test, ob der Nachbar schon für einen anderen Knoten im
						 * Graphen ausgewählt wurde. Ist dies der Fall bekommt
						 * dieses Contig ein anderes Aussehen und kann auch nicht
						 * mehr für dieses ausgewählt werden.
						 */
						//Vector<Vector<AdjacencyEdge>> rechteKanten = centralContig.isReverse() ? selectedLeftEdges : selectedRightEdges;
						boolean anderweitigAusgewaehlt = ulteriorSelected(false, indexOfContig, edge);//, rechteKanten);
						
						contigPanel = new ContigAppearance(cagModel.getGraph(), edge,
								indexOfContig, false, cagModel.getMaxSizeOfContigs(),
								cagModel.getMinSizeOfContigs(), anderweitigAusgewaehlt);
						System.out.println("r erzeuge contig appearance ");
//						contigPanel.setAlignmentX(LEFT_ALIGNMENT);
						contigPanel.addMouseListener(new ContigMouseListener());
						contigPanel.setName("contig Panel");

						/*
						 * Zu jedem Nachbarn wird auch ein RadioButton erstellt mit
						 * dem man einen dieser Contigs auswählen kann. Damit die
						 * Button unterscheidbar sind, werden ihm die Kante und der
						 * Index des zentralen Contigs übergeben
						 */
						radioButton = new ContigRadioButton(edge, contigPanel);

						if (edge.isSelected()) {
							radioButton.setSelected(true);
						}

						if (isARightNeighourSelected) {
							radioButton.setActionCommand("nachbarAusgewaehlt");
							radioButton
							.setSelectedNeighbourOfButtonGroup(neighbourForThisGroup);
					
						} else if (!isARightNeighourSelected) {
							radioButton
							.setActionCommand("noch kein nachbar ausgewaehlt");
						}
						if (anderweitigAusgewaehlt) {
							radioButton.setActionCommand("anderweitigAusgewaehlt");
							AdjacencyEdge otherEdge = selectedRightEdges.get(indexOfContig).firstElement();
							radioButton.setNeighboursForTheThisNeighbour(otherEdge);
							
						}

						radioButton.setNachbarIndex(indexOfContig);
						radioButton.setCentralIndex(cagModel.getCurrentContigIndex());
						radioButton.setLeft(false);
						radioButton.setOpaque(false);
						radioButton
								.addActionListener(new RadioButtonActionListener(this, cagModel));

						Container rightGroup = null;
						rightGroup.add(radioButton);
						rightContainer.add(contigPanel);
						rightRadioButtonContainer.add(radioButton);

						if (s < (cagModel.getNumberOfNeighbours() - 1)) {
							rightContainer.add(Box.createVerticalGlue());
							rightRadioButtonContainer.add(Box.createVerticalGlue());
						}
						rightContainer.updateUI();
						rightRadioButtonContainer.updateUI();
						s++;
					}
					if (s == terminator) {
						break;
					}
				}
				rightContainer.add(Box.createVerticalGlue());
				rightRadioButtonContainer.add(Box.createVerticalGlue());
				chooseContigPanel.setRightSupport(rightSupport);
				rightNeighboursReady = true;
				repaintWindowAndSubcomponents();
			}

		}
		
		private void repaintWindowAndSubcomponents(){
			if(con.isRightNeighboursReady() && con.isLeftNeigboursReady()){
				con.getChooseContigPanel().setFlag(true);
				con.getWindow().repaint();
				System.out.println("l rufe repaint auf "+con.isLeftNeigboursReady()+" "+con.isRightNeighboursReady());
			}
		}

		private int setTerminator(Vector<AdjacencyEdge> neighbourVector) {

			int value = neighbourVector.size();
			if (cagModel.getNumberOfNeighbours() < neighbourVector.size()) {
				value = cagModel.getNumberOfNeighbours();
			} else if (cagModel.getNumberOfNeighbours() > neighbourVector.size()) {
				value = neighbourVector.size();
			}

			return value;
		}

		private double calculateZScore(AdjacencyEdge edge, int centralContigIndex,
				double[] meanForNeighbours, double[] sDeviationForNeighbours) {

			double zScore = 0;

			zScore = (edge.getSupport() - meanForNeighbours[centralContigIndex])
					/ sDeviationForNeighbours[centralContigIndex];

			return zScore;

		}

		private int indexOfNeighbourContig(AdjacencyEdge edge) {

			int index;

			if (edge.geti() == cagModel.getCurrentContigIndex()) {
				index = edge.getj();
			} else {
				index = edge.geti();
			}

			return index;
		}

		private int indexOfCentralContig(AdjacencyEdge edge) {

			int index;

			if (edge.geti() == cagModel.getCurrentContigIndex()) {
				index = edge.geti();
			} else {
				index = edge.getj();
			}

			return index;
		}

		/*
		 * If a not repetitiv contig is used in an another adjacency, the flag will be
		 * set on true. 
		 */
		private boolean ulteriorSelected(boolean isLeft, int indexOfNeighbour, AdjacencyEdge edge){//, Vector<Vector<AdjacencyEdge>> selectedEdges){//, boolean isCentralContigReverse) {
			boolean isSelected = false;
			
			if(isLeft){
				if(!cagModel.getSelectedLeftEdges().get(indexOfNeighbour).isEmpty() 
						&& !cagModel.getGraph().getNodes().get(indexOfNeighbour).isRepetitive()){

					AdjacencyEdge other = cagModel.getSelectedLeftEdges().get(indexOfNeighbour).firstElement();

					int i =indexOfCentralContig(other); 
					isSelected = true;
					 if (i == cagModel.getCurrentContigIndex()){
						 isSelected = false;
					 }
					
				}
			}else{
				if(!cagModel.getSelectedRightEdges().get(indexOfNeighbour).isEmpty()
						&& !cagModel.getGraph().getNodes().get(indexOfNeighbour).isRepetitive()){
					
					AdjacencyEdge other = cagModel.getSelectedRightEdges().get(indexOfNeighbour).firstElement();

					int i =indexOfCentralContig(other);
					isSelected = true;
					
					 if (i == cagModel.getCurrentContigIndex()){
						 isSelected = false;
					 }
				}
			}

			return isSelected;
		}
		
		private void clearComponets(JPanel contigContainer, JPanel radioButtonContainer){
			
			if (contigContainer.getComponentCount() > 0
					|| radioButtonContainer.getComponentCount() > 0) {
				contigContainer.removeAll();
				radioButtonContainer.removeAll();
			}
		}
		public class ContigMouseListener implements MouseListener {

			@Override
			public void mouseClicked(MouseEvent e) {
				/*
				 * If this will be activated the choosed contig will be displayed 
				 * as central contig with its neighbours.			
				 */
				ContigAppearance contigPanel = (ContigAppearance) e.getSource();

				int index = contigPanel.getI();
				boolean currentContigIsReverse = contigPanel.isReverse();
				
				cagModel.changeContigs(index, currentContigIsReverse);
				chooseContigPanel.setFlag(false);
				
				
				ThreadForRightAndLeftNeigbours threadForRightNeighbours = new ThreadForRightAndLeftNeigbours(con, cagModel, false);
				threadForRightNeighbours.execute();

				ThreadForRightAndLeftNeigbours threadForLeftNeighbours =new ThreadForRightAndLeftNeigbours(con, cagModel, true);
				threadForLeftNeighbours.execute();

			}

			@Override
			public void mouseEntered(MouseEvent e) {
			/*	ContigAppearance contigPanel = (ContigAppearance) e.getSource();
				
				 Graphics2D g = contigPanel.getBorder().getG2();
				 GeneralPath p = contigPanel.getBorder().getP();
			
				 GradientPaint redtowhite = new GradientPaint(0,0,Color.RED,100, 0,Color.WHITE);
					g.setPaint(redtowhite);
					g.fill(p);*/
					//contigPanel.update(g);
					//chooseContigPanel.updateUI();
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				/*ContigAppearance contigPanel = (ContigAppearance) e.getSource();
				 Graphics2D g = contigPanel.getBorder().getG2();
				 GeneralPath p = contigPanel.getBorder().getP();
				 g.draw(p);*/
				 
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// Auto-generated method stub
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// Auto-generated method stub
			}

			
		}

		public JScrollPane getScrollPane() {
			return scrollPane;
		}

		public ContigListPanel getListScroller() {
			return listScroller;
		}


		public LegendAndInputOptionPanel getInputOption() {
			return inputOption;
		}

		public void updateLines() {
			
			
		}


}