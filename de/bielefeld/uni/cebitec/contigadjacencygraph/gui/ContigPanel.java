package de.bielefeld.uni.cebitec.contigadjacencygraph.gui;

import javax.swing.JPanel;

public class ContigPanel extends JPanel implements Cloneable{
	
	private String contigName;
	private long length;
	private boolean isRepeat;
	private boolean isReverse; 	
	
	public ContigPanel(){
		
	}

	public ContigPanel(String contigId, long size, boolean isRepeat, boolean isReverse){
		this.contigName = contigId;
		this.length = size;
		this.isRepeat = isRepeat;
		this.isReverse = isReverse;
	}
		
	@Override
	public ContigPanel clone(){
		try{
			return (ContigPanel) super.clone();
		} catch ( CloneNotSupportedException e ) { 
			// this shouldn't happen, since we are Cloneable 
			throw new InternalError(); 
		} 
		
	}
	
//	private final Brain brain; // brain is final since I do not want 
				// any transplant on it once created!
//	private int age;
//	public Person(Brain aBrain, int theAge)
//	{
//		brain = aBrain; 
//		age = theAge;
//	}
//
//	protected Person(Person another)
//	{
//		Brain refBrain = null;
//		try
//		{
//			refBrain = (Brain) another.brain.clone();
//			// You can set the brain in the constructor
//		}
//		catch(CloneNotSupportedException e) {}
//		brain = refBrain;
//		age = another.age;
//	}
//
//	public String toString()
//	{
//		return "This is person with " + brain;
//		// Not meant to sound rude as it reads!
//	}
//
//	public Object clone()
//	{
//		return new Person(this);
//	}
	
	
}
