package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import javax.swing.border.AbstractBorder;
/**
 * Diese Klasse zeichnet die Ränder der Contig Panel
 */
public class ContigBorder extends AbstractBorder{

	private boolean isRepeat;
	private boolean isReverse;
	private boolean isSelected = false;
	private boolean woandersAusgewaehlt;
	private float[] dash = {2,2};
	private float[] dash2 = {15,2};
	
	public ContigBorder(boolean isRepeat, boolean isReverse) {
		this.isRepeat = isRepeat;
		this.isReverse = isReverse;
	}
	public ContigBorder(boolean isRepeat, boolean isReverse, boolean flag, boolean anderweitigAusgewaehlt) {
		this.isRepeat = isRepeat;
		this.isReverse = isReverse;
		this.isSelected = flag;
		this.woandersAusgewaehlt = anderweitigAusgewaehlt;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	        
	    Color oldColor = g.getColor();     
		Graphics2D g2 = (Graphics2D) g;
		
		/*
		 * Hiermit werden die Ränder "weich" gezeichnet.
		 */
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		GeneralPath p;
		
		/*
		 * Wenn das entsprechende Contig ein Ausgewähltes Contig ist, 
		 * wird es mit rotem Rand dargestellt. Ansonsten mit schwarzem.
		 */
		if (isSelected) {
			g2.setColor(Color.BLACK);
		}else if (woandersAusgewaehlt){
			g2.setColor(Color.DARK_GRAY);
			g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash2 , 0));
		}
		else{
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1 , dash, 0));
		}
		
		if(isRepeat ==false && isReverse == false){
			p = makeContig(0,0, c.getWidth(),c.getHeight());
			g2.draw(p);
		}
		if(isRepeat == true && isReverse == false){
			p = makeRepeatContig(0,0, c.getWidth(),c.getHeight());
			g2.draw(p);
		}
		if(isRepeat == false && isReverse == true){
			p = makeReverseContig(0,0, c.getWidth(),c.getHeight());
			g2.draw(p);
		}
		if(isRepeat == true && isReverse == true){
			p = makeReverseRepeatContig(0,0, c.getWidth(),c.getHeight());
			g2.draw(p);
		}
		
		g.setColor(oldColor);
	}

	/*
	 * (x, y + height)------------(x + width, y + height) (x + width + 10, y +
	 * height/2) Startpunkt *(x, y)-------------(x + width, y)
	 * 
	 * Hier wird er Rand es "normalen" Contigs erstellt, der Form | >
	 */
	private  GeneralPath makeContig(int x, int y, int width, int height) {

		GeneralPath p = new GeneralPath();

		p.moveTo(x, y); // Startpunkt
		p.lineTo(x + width - 10, y); // 1
		p.lineTo(x + width -1, y + height / 2); // 2
		p.lineTo(x + width -10, y + height -1); // 3
		p.lineTo(x, y + height -1); // 4
		p.closePath();

		return p;
	}
	
	/*
	 * Hier wird der Rand eines Repeats erstellt, d.h. die Kante oben und
	 * an der rechten Seite werden 3 Fach gemalt.
	 */
	private GeneralPath makeRepeatContig(int x, int y, int width, int height) {
		
		GeneralPath p = new GeneralPath();
		
		p.moveTo(x, y + 5); // Startpunkt
		p.lineTo(x + width - 15, y + 5); // 1
		p.lineTo(x + width - 5, y + height / 2); // 2
		p.lineTo(x + width -15, y + height  -1); // 3
		p.lineTo(x, y + height -1); // 4
		p.closePath();
		p.moveTo(x, y+5);
		p.lineTo(x+2, y+3);
		p.lineTo(x+width-14, y+3);
		p.lineTo(x+width-3, y+height/2 );
		p.lineTo(x+width-13, y+height-1);
		p.lineTo(x+width-13, y+height-1);
		p.moveTo(x + 2, y+3);
		p.lineTo(x +4, y+1);
		p.lineTo(x+width-13, y+1);
		p.lineTo(x+width-1, y+height/2 );
		p.lineTo(x+width-11, y+height-1);
		p.lineTo(x+width-11, y+height-1);
		return p;
	}
	
	/*
	 * Hier wird der Rand eines reversen Repeats erstellt, d.h. die Kante oben und
	 * an der linken Seite werden 3 Fach gemalt. 
	 */
	private GeneralPath makeReverseRepeatContig(int x, int y, int width, int height) {
		
		GeneralPath p = new GeneralPath();
		
		p.moveTo(x+15, y+height-1);
		p.lineTo(x +4, y + height/2);
		p.lineTo(x+15, y + 4);
		p.lineTo(x + width -1, y + 4);
		p.lineTo(x + width-1, y+height-1);
		p.closePath();
		p.moveTo(x+15, y+height-1);
		p.lineTo(x+13, y+height-1);
		p.lineTo(x+2, y+height/2);
		p.lineTo(x+14, y+2);
		p.lineTo(x+width-4, y+2);
		p.lineTo(x+width-1, y+4);
		p.moveTo(x+13, y+height-1);
		p.lineTo(x+11, y+height-1);
		p.lineTo(x, y+height/2);
		p.lineTo(x+13, y);
		p.lineTo(x+width-6, y);
		p.lineTo(x+width-4, y-2);
		
		return p;
	}
	
	/*
	 * 							 	2------------3
	 * 						1						
	 * 			Startpunkt *|5-------------4
	 * 
	 * Hier wird er Rand es reversen Contigs erstellt, der Form: < |
	 */
	private GeneralPath makeReverseContig(int x, int y, int width, int height) {
		
		GeneralPath p = new GeneralPath();
		
		p.moveTo(x + 10, y);
		p.lineTo(x +1, y + height/2);
		p.lineTo(x + 10, y + height -1);
		p.lineTo(x + width - 1, y + height -1);
		p.lineTo(x + width - 1, y);
		p.closePath();
		return p;
}

	public boolean isReverse() {
		return isReverse;
	}
	
	public Insets getBorderInsets(Component c){
		return new Insets(10, 10 ,10, 10);
	}
	
	public Insets getBorderInsets(Component c, Insets insets){
		return insets;
	}

	public boolean isBorderOpaque() {
		return true;
	}


}
