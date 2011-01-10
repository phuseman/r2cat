package de.bielefeld.uni.cebitec.dnasequences;



import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author phuseman
 */
public class Contig implements DNASequence {

    private String id = null;
    private String description = null;
    private int size = -1;
    private File fastaFile = null;

    boolean isRepetitive =false;

  public int getEstimatedRepeatCount() {
    return estimatedRepeatCount;
  }

  public void setEstimatedRepeatCount(int estimatedRepeatCount) {
    this.estimatedRepeatCount = estimatedRepeatCount;
  }

  public boolean isIsRepetitive() {
    return isRepetitive;
  }

  public void setIsRepetitive(boolean isRepetitive) {
    this.isRepetitive = isRepetitive;
  }
    int estimatedRepeatCount = 0;

    public Contig(String id, int size) {
        this.id = id;
        this.size = size;
    }

    public Contig(String id, String description, int size) {
        this.id = id;
        this.description = description;
        this.size = size;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public File getFastaFile() {
        return fastaFile;
    }

    public void setDescription(String description) {
      String old = this.description;
        this.description = description;
        fire("description", old, description);
    }

    public void setFastaFile(File fastaFile) {
      File old = this.fastaFile;
      this.fastaFile = fastaFile;
      fire("fastaFile", old, fastaFile);
    }


    private List listeners = Collections.synchronizedList(new LinkedList());

public void addPropertyChangeListener (PropertyChangeListener pcl) {
    listeners.add (pcl);
}

public void removePropertyChangeListener (PropertyChangeListener pcl) {
    listeners.remove (pcl);
}

private void fire (String propertyName, Object old, Object nue) {
    //Passing 0 below on purpose, so you only synchronize for one atomic call:
    PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
    for (int i = 0; i < pcls.length; i++) {
        pcls[i].propertyChange(new PropertyChangeEvent (this, propertyName, old, nue));
    }
}
}
