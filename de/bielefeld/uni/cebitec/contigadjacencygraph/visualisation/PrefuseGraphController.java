package de.bielefeld.uni.cebitec.contigadjacencygraph.visualisation;

import javax.swing.JPanel;
import javax.swing.JLabel;

import de.bielefeld.uni.cebitec.contigadjacencygraph.visualisation.ContigGraph;
import de.bielefeld.uni.cebitec.contigadjacencygraph.visualisation.TreecatGraphFrame;
import de.bielefeld.uni.cebitec.contigadjacencygraph.LayoutGraph;

public class PrefuseGraphController
{
	private JPanel dispPanel = new JPanel();
	private JPanel ctrlPanel = new JPanel();
	
	public JPanel getControlPanel()
	{
		return this.ctrlPanel;
	}
	
	public JPanel getDispPanel()
	{
		return this.dispPanel;
	}
	
	public void setLayoutGraph(LayoutGraph lg)
	{
		if(lg == null)
		{
			this.setNeutral();
		}
		else
		{	
		ContigGraph cg = new ContigGraph(lg);
		TreecatGraphFrame tgf = new TreecatGraphFrame(cg);
		
		this.ctrlPanel.removeAll();
		this.ctrlPanel = tgf.getControlPanel();
		
		this.dispPanel.removeAll();
		this.dispPanel = tgf.getDisplayPanel();
		}
	}
	
	public void setNeutral()
	{
		this.dispPanel.removeAll();
		this.dispPanel.add(new JLabel("no content set"));
		
		this.ctrlPanel.removeAll();
		this.ctrlPanel.add(new JLabel("no content set"));
	}
}





