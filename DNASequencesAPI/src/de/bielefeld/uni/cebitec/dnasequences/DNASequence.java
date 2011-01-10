package de.bielefeld.uni.cebitec.dnasequences;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.File;

/**
 *
 * @author phuseman
 */
public interface DNASequence {

    public String getId();

    public String getDescription();

    public int getSize();

    public File getFastaFile();


}
