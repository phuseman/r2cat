/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport;

import de.bielefeld.uni.cebitec.qgram.DNASequence;
import de.bielefeld.uni.cebitec.qgram.Match;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import junit.framework.TestCase;

/**
 *
 * @author Mark Ugarov
 */
public abstract class ModelTestConstants extends TestCase {
    
    
    public static final Match M1 = new Match(new DNASequence(null), 300, 400, new DNASequence(null), 100, 200);
    public static final Match M2 = new Match(new DNASequence(null), 450, 550, new DNASequence(null), 200, 300);
    public static final Match M3 = new Match(new DNASequence(null), 800, 700, new DNASequence(null), 400, 500);
    public static final Match M4 = new Match(new DNASequence(null), 600, 650, new DNASequence(null), 600, 650);
    
    public static final Match E1 = new Match(new DNASequence(null), 500, 550, new DNASequence(null), 100, 150);
    
    public static final Match R1 = new Match(new DNASequence(null), 50, 200, new DNASequence(null), 50, 200);
    public static final Match R2 = new Match(new DNASequence(null), 150,300, new DNASequence(null), 100,300);
    public static final Match R3 = new Match(new DNASequence(null), 250,400, new DNASequence(null), 250, 450);
    public static final Match R4 = new Match(new DNASequence(null), 150, 300, new DNASequence(null), 300,400);
    public static final Match R5 = new Match(new DNASequence(null), 450, 600, new DNASequence(null), 400,500);
    
    public static final Match R6 = new Match(new DNASequence(null), 700, 500, new DNASequence(null), 200,300);
    public static final Match R7 = new Match(new DNASequence(null), 700, 500, new DNASequence(null), 300,450);
    public static final Match R8= new Match(new DNASequence(null), 550, 300, new DNASequence(null), 350, 550);
    public static final Match R9 = new Match(new DNASequence(null), 600, 400, new DNASequence(null), 400, 600);
    public static final Match R10 = new Match(new DNASequence(null), 400,200, new DNASequence(null), 500,800);
    
    public static final Match R11 = new Match(new DNASequence(null), 300, 600, new DNASequence(null), 300, 600);
    public static final Match R12 = new Match(new DNASequence(null), 1000, 700, new DNASequence(null), 400, 700);
    public static final Match R13 = new Match(new DNASequence(null), 800, 500, new DNASequence(null), 500, 800);
    public static final Match R14 = new Match(new DNASequence(null), 400, 100, new DNASequence(null), 700, 900);
    public static final Match R15 = new Match(new DNASequence(null), 800, 500, new DNASequence(null), 700, 900);
}
