/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestConstants;


import de.bielefeld.uni.cebitec.qgram.DNASequence;
import de.bielefeld.uni.cebitec.qgram.Match;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import junit.framework.TestCase;

/**
 *
 * @author Mark Ugarov
 */
public abstract class ExportConstantsTest extends TestCase {
    
    public static final Match M1 = new Match(new DNASequence(null), 300, 400, new DNASequence(null), 100, 200);
    public static final Match M2 = new Match(new DNASequence(null), 450, 550, new DNASequence(null), 200, 300);
}
